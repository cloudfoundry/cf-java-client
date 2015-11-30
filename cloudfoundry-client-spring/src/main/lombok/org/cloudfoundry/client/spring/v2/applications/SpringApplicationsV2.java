/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.v2.applications;

import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
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
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingResponse;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.rx.Stream;

import java.net.URI;
import java.util.function.Consumer;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;

/**
 * The Spring-based implementation of {@link ApplicationsV2}
 */
@ToString(callSuper = true)
public final class SpringApplicationsV2 extends AbstractSpringOperations implements ApplicationsV2 {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringApplicationsV2(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<AssociateApplicationRouteResponse> associateRoute(AssociateApplicationRouteRequest request) {
        return put(request, AssociateApplicationRouteResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "routes", request.getRouteId()));
    }

    @Override
    public Publisher<CopyApplicationResponse> copy(CopyApplicationRequest request) {
        return post(request, CopyApplicationResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "copy_bits"));
    }

    @Override
    public Publisher<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class,
                builder -> builder.pathSegment("v2", "apps"));
    }

    @Override
    public Publisher<Void> delete(DeleteApplicationRequest request) {
        return delete(request, builder -> builder.pathSegment("v2", "apps", request.getId()));
    }

    private <T> Stream<T> deleteWithResponse(Validatable request, Class<T> responseType,
                                             Consumer<UriComponentsBuilder> builderCallback) {
        return exchange(request, () -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(this.root);
            builderCallback.accept(builder);
            URI uri = builder.build().toUri();

            this.logger.debug("DELETE {}", uri);
            return this.restOperations.exchange(new RequestEntity<>(request, DELETE, uri), responseType).getBody();
        });
    }

    @Override
    public Publisher<ApplicationEnvironmentResponse> environment(ApplicationEnvironmentRequest request) {
        return get(request, ApplicationEnvironmentResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "env"));
    }

    @Override
    public Publisher<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId()));
    }

    @Override
    public Publisher<ApplicationInstancesResponse> instances(ApplicationInstancesRequest request) {
        return get(request, ApplicationInstancesResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "instances"));
    }

    @Override
    public Publisher<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> {
            builder.pathSegment("v2", "apps");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Publisher<ListApplicationRoutesResponse> listRoutes(ListApplicationRoutesRequest request) {
        return get(request, ListApplicationRoutesResponse.class, builder -> {
            builder.pathSegment("v2", "apps", request.getId(), "routes");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Publisher<ListApplicationServiceBindingsResponse> listServiceBindings
            (ListApplicationServiceBindingsRequest request) {
        return get(request, ListApplicationServiceBindingsResponse.class, builder -> {
            builder.pathSegment("v2", "apps", request.getId(), "service_bindings");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Publisher<RemoveApplicationRouteResponse> removeRoute(RemoveApplicationRouteRequest request) {
        return deleteWithResponse(request, RemoveApplicationRouteResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "routes", request.getRouteId()));
    }

    @Override
    public Publisher<RemoveApplicationServiceBindingResponse> removeServiceBinding
            (RemoveApplicationServiceBindingRequest request) {
        return deleteWithResponse(request, RemoveApplicationServiceBindingResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "service_bindings",
                        request.getServiceBindingId()));
    }

    @Override
    public Publisher<RestageApplicationResponse> restage(RestageApplicationRequest request) {
        return post(request, RestageApplicationResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "restage"));
    }

    @Override
    public Publisher<ApplicationStatisticsResponse> statistics(ApplicationStatisticsRequest request) {
        return get(request, ApplicationStatisticsResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "stats"));
    }

    @Override
    public Publisher<SummaryApplicationResponse> summary(SummaryApplicationRequest request) {
        return get(request, SummaryApplicationResponse.class,
                builder -> builder.pathSegment("v2", "apps", request.getId(), "summary"));
    }

    @Override
    public Publisher<Void> terminateInstance(TerminateApplicationInstanceRequest request) {
        return delete(request, builder ->
                builder.pathSegment("v2", "apps", request.getId(), "instances", request.getIndex()));
    }

    @Override
    public Publisher<UploadApplicationResponse> upload(UploadApplicationRequest request) {
        return exchange(request, () -> {
            URI uri = UriComponentsBuilder.fromUri(this.root)
                    .pathSegment("v2", "apps", request.getId(), "bits")
                    .build().toUri();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            if (request.getAsync() != null) {
                body.add("async", request.getAsync());
            }

            body.add("resources", request.getResources());
            body.add("application", new FileSystemResource(request.getApplication()));

            this.logger.debug("PUT {}", uri);
            return this.restOperations.exchange(new RequestEntity<MultiValueMap<String, Object>>(body, PUT, uri),
                    UploadApplicationResponse.class).getBody();
        });
    }

}
