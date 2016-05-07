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

package org.cloudfoundry.reactor.client.v2.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.GetRouteRequest;
import org.cloudfoundry.client.v2.routes.GetRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v2.routes.UpdateRouteResponse;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.util.ExceptionUtils;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link Routes}
 */
public final class ReactorRoutes extends AbstractClientV2Operations implements Routes {

    private static final int CF_NOT_FOUND = 10000;

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorRoutes(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<AssociateRouteApplicationResponse> associateApplication(AssociateRouteApplicationRequest request) {
        return put(request, AssociateRouteApplicationResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "routes", validRequest.getRouteId(), "apps", validRequest.getApplicationId())));
    }

    @Override
    public Mono<CreateRouteResponse> create(CreateRouteRequest request) {
        return post(request, CreateRouteResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes")));
    }

    @Override
    public Mono<DeleteRouteResponse> delete(DeleteRouteRequest request) {
        return delete(request, DeleteRouteResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes", validRequest.getRouteId())));
    }

    @Override
    public Mono<Boolean> exists(RouteExistsRequest request) {
        return get(request, Boolean.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes", "reserved", "domain", validRequest.getDomainId(), "host", validRequest.getHost())))
            .defaultIfEmpty(true)
            .otherwise(ExceptionUtils.replace(CF_NOT_FOUND, () -> Mono.just(false)));
    }

    @Override
    public Mono<GetRouteResponse> get(GetRouteRequest request) {
        return get(request, GetRouteResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes", validRequest.getRouteId())));
    }

    @Override
    public Mono<ListRoutesResponse> list(ListRoutesRequest request) {
        return get(request, ListRoutesResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes")));
    }

    @Override
    public Mono<ListRouteApplicationsResponse> listApplications(ListRouteApplicationsRequest request) {
        return get(request, ListRouteApplicationsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes", validRequest.getRouteId(), "apps")));
    }

    @Override
    public Mono<Void> removeApplication(RemoveRouteApplicationRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes", validRequest.getRouteId(), "apps", validRequest.getApplicationId())));
    }

    @Override
    public Mono<UpdateRouteResponse> update(UpdateRouteRequest request) {
        return put(request, UpdateRouteResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "routes", validRequest.getRouteId())));
    }

}
