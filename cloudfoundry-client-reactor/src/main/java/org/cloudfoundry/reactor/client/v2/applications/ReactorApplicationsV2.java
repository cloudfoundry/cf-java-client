/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.applications;

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
import org.cloudfoundry.client.v2.applications.GetApplicationPermissionsRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationPermissionsResponse;
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
import org.cloudfoundry.client.v2.applications.UploadApplicationDropletRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationDropletResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.MultipartHttpClientRequest;
import org.cloudfoundry.util.FileUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;

/**
 * The Reactor-based implementation of {@link ApplicationsV2}
 */
public final class ReactorApplicationsV2 extends AbstractClientV2Operations implements ApplicationsV2 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorApplicationsV2(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AssociateApplicationRouteResponse> associateRoute(AssociateApplicationRouteRequest request) {
        return put(request, AssociateApplicationRouteResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<CopyApplicationResponse> copy(CopyApplicationRequest request) {
        return post(request, CopyApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "copy_bits"))
            .checkpoint();
    }

    @Override
    public Mono<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, builder -> builder.pathSegment("apps"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Flux<byte[]> download(DownloadApplicationRequest request) {
        return get(request, builder -> builder.pathSegment("apps", request.getApplicationId(), "download"), ByteBufFlux::asByteArray)
            .checkpoint();
    }

    @Override
    public Flux<byte[]> downloadDroplet(DownloadApplicationDropletRequest request) {
        return get(request, builder -> builder.pathSegment("apps", request.getApplicationId(), "droplet", "download"), ByteBufFlux::asByteArray)
            .checkpoint();
    }

    @Override
    public Mono<ApplicationEnvironmentResponse> environment(ApplicationEnvironmentRequest request) {
        return get(request, ApplicationEnvironmentResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "env"))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationPermissionsResponse> getPermissions(GetApplicationPermissionsRequest request) {
        return get(request, GetApplicationPermissionsResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "permissions"))
            .checkpoint();
    }

    @Override
    public Mono<ApplicationInstancesResponse> instances(ApplicationInstancesRequest request) {
        return get(request, ApplicationInstancesResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "instances"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> builder.pathSegment("apps"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationRoutesResponse> listRoutes(ListApplicationRoutesRequest request) {
        return get(request, ListApplicationRoutesResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "routes"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationServiceBindingsResponse> listServiceBindings(ListApplicationServiceBindingsRequest request) {
        return get(request, ListApplicationServiceBindingsResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "service_bindings"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeRoute(RemoveApplicationRouteRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeServiceBinding(RemoveApplicationServiceBindingRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "service_bindings", request.getServiceBindingId()))
            .checkpoint();
    }

    @Override
    public Mono<RestageApplicationResponse> restage(RestageApplicationRequest request) {
        return post(request, RestageApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "restage"))
            .checkpoint();
    }

    @Override
    public Mono<ApplicationStatisticsResponse> statistics(ApplicationStatisticsRequest request) {
        return get(request, ApplicationStatisticsResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "stats"))
            .checkpoint();
    }

    @Override
    public Mono<SummaryApplicationResponse> summary(SummaryApplicationRequest request) {
        return get(request, SummaryApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "summary"))
            .checkpoint();
    }

    @Override
    public Mono<Void> terminateInstance(TerminateApplicationInstanceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "instances", request.getIndex()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateApplicationResponse> update(UpdateApplicationRequest request) {
        return put(request, UpdateApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Mono<UploadApplicationResponse> upload(UploadApplicationRequest request) {
        Path application = request.getApplication();

        if (application.toFile().isDirectory()) {
            return FileUtils.compress(application)
                .map(temporaryFile -> UploadApplicationRequest.builder()
                    .from(request)
                    .application(temporaryFile)
                    .build())
                .flatMap(requestWithTemporaryFile -> upload(requestWithTemporaryFile, () -> {
                    try {
                        Files.delete(requestWithTemporaryFile.getApplication());
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                }));
        } else {
            return upload(request, () -> {
            });
        }
    }

    @Override
    public Mono<UploadApplicationDropletResponse> uploadDroplet(UploadApplicationDropletRequest request) {
        return put(request, UploadApplicationDropletResponse.class,
            builder -> builder.pathSegment("apps", request.getApplicationId(), "droplet", "upload"),
            multipartRequest -> upload(multipartRequest, request), () -> {
            }).checkpoint();
    }

    private Mono<UploadApplicationResponse> upload(UploadApplicationRequest request, Runnable onTerminate) {
        return put(request, UploadApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "bits"),
            multipartRequest -> upload(request.getApplication(), multipartRequest, request), onTerminate).checkpoint();
    }

    private void upload(Path application, MultipartHttpClientRequest multipartRequest, UploadApplicationRequest request) {
        multipartRequest.addPart(part -> part.setName("resources")
            .setContentType(APPLICATION_JSON.toString())
            .send(request.getResources()))
            .addPart(part -> part.setName("application")
                .setContentType(APPLICATION_ZIP)
                .sendFile(application))
            .done();
    }

    private void upload(MultipartHttpClientRequest multipartRequest, UploadApplicationDropletRequest request) {
        multipartRequest.addPart(part -> part.setName("droplet")
            .sendFile(request.getDroplet()))
            .done();
    }

}
