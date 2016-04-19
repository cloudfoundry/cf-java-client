/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.spring.client.v2.applications;

import lombok.ToString;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationDropletRequest;
import org.cloudfoundry.client.v2.applications.DownloadApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;
import java.util.List;

/**
 * The Spring-based implementation of {@link ApplicationsV2}
 */
@ToString(callSuper = true)
public final class SpringApplicationsV2 extends AbstractSpringOperations implements ApplicationsV2 {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringApplicationsV2(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<AssociateApplicationRouteResponse> associateRoute(AssociateApplicationRouteRequest request) {
        return put(request, AssociateApplicationRouteResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "routes", request.getRouteId()));
    }

    @Override
    public Mono<CopyApplicationResponse> copy(CopyApplicationRequest request) {
        return post(request, CopyApplicationResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "copy_bits"));
    }

    @Override
    public Mono<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, builder -> builder.pathSegment("v2", "apps"));
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId()));
    }

    @Override
    public Flux<byte[]> download(DownloadApplicationRequest request) {
        return getStream(request, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "download"));
    }

    @Override
    public Flux<byte[]> downloadDroplet(DownloadApplicationDropletRequest request) {
        return getStream(request, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "droplet", "download"));
    }

    @Override
    public Mono<ApplicationEnvironmentResponse> environment(ApplicationEnvironmentRequest request) {
        return get(request, ApplicationEnvironmentResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "env"));
    }

    @Override
    public Mono<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId()));
    }

    @Override
    public Mono<ApplicationInstancesResponse> instances(ApplicationInstancesRequest request) {
        return get(request, ApplicationInstancesResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "instances"));
    }

    @Override
    public Mono<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> {
            builder.pathSegment("v2", "apps");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<ListApplicationRoutesResponse> listRoutes(ListApplicationRoutesRequest request) {
        return get(request, ListApplicationRoutesResponse.class, builder -> {
            builder.pathSegment("v2", "apps", request.getApplicationId(), "routes");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<ListApplicationServiceBindingsResponse> listServiceBindings(ListApplicationServiceBindingsRequest request) {
        return get(request, ListApplicationServiceBindingsResponse.class, builder -> {
            builder.pathSegment("v2", "apps", request.getApplicationId(), "service_bindings");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<Void> removeRoute(RemoveApplicationRouteRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "routes", request.getRouteId()));
    }

    @Override
    public Mono<Void> removeServiceBinding(RemoveApplicationServiceBindingRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "service_bindings", request.getServiceBindingId()));
    }

    @Override
    public Mono<RestageApplicationResponse> restage(RestageApplicationRequest request) {
        return post(request, RestageApplicationResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "restage"));
    }

    @Override
    public Mono<ApplicationStatisticsResponse> statistics(ApplicationStatisticsRequest request) {
        return get(request, ApplicationStatisticsResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "stats"));
    }

    @Override
    public Mono<SummaryApplicationResponse> summary(SummaryApplicationRequest request) {
        return get(request, SummaryApplicationResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "summary"));
    }

    @Override
    public Mono<Void> terminateInstance(TerminateApplicationInstanceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId(), "instances", request.getIndex()));
    }

    @Override
    public Mono<UpdateApplicationResponse> update(UpdateApplicationRequest request) {
        return put(request, UpdateApplicationResponse.class, builder -> builder.pathSegment("v2", "apps", request.getApplicationId()));
    }

    @Override
    public Mono<UploadApplicationResponse> upload(UploadApplicationRequest request) {
        return putWithBody(request, () -> {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("resources", getResourcesPart(request));
            body.add("application", getApplicationPart(request));
            return body;
        }, UploadApplicationResponse.class, builder -> {
            builder.pathSegment("v2", "apps", request.getApplicationId(), "bits");
            QueryBuilder.augment(builder, request);
        });
    }

    private static HttpEntity<Resource> getApplicationPart(UploadApplicationRequest request) {
        Resource body = new InputStreamResource(request.getApplication());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("application", "application.zip");
        headers.setContentType(MediaType.parseMediaType("application/zip"));

        return new HttpEntity<>(body, headers);
    }

    private static HttpEntity<List<UploadApplicationRequest.Resource>> getResourcesPart(UploadApplicationRequest request) {
        List<UploadApplicationRequest.Resource> body = request.getResources();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("resources", null);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }

}
