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

package org.cloudfoundry.reactor.client.v2.routes;

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
import org.cloudfoundry.client.v2.routes.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v2.routes.UpdateRouteResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.util.ExceptionUtils;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * The Reactor-based implementation of {@link Routes}
 */
public final class ReactorRoutes extends AbstractClientV2Operations implements Routes {

    private static final int CF_NOT_FOUND = 10000;

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorRoutes(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AssociateRouteApplicationResponse> associateApplication(AssociateRouteApplicationRequest request) {
        return put(request, AssociateRouteApplicationResponse.class, builder -> builder.pathSegment("routes", request.getRouteId(), "apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Mono<CreateRouteResponse> create(CreateRouteRequest request) {
        return post(request, CreateRouteResponse.class, builder -> builder.pathSegment("routes"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteRouteResponse> delete(DeleteRouteRequest request) {
        return delete(request, DeleteRouteResponse.class, builder -> builder.pathSegment("routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<Boolean> exists(RouteExistsRequest request) {
        return get(request, Boolean.class,
            builder -> {
                builder.pathSegment("routes", "reserved", "domain", request.getDomainId());
                Optional.ofNullable(request.getHost()).ifPresent(host -> builder.pathSegment("host", host));
                return builder;
            })
            .defaultIfEmpty(true)
            .onErrorResume(ExceptionUtils.statusCode(CF_NOT_FOUND), t -> Mono.just(false))
            .checkpoint();
    }

    @Override
    public Mono<GetRouteResponse> get(GetRouteRequest request) {
        return get(request, GetRouteResponse.class, builder -> builder.pathSegment("routes", request.getRouteId()))
            .checkpoint();
    }

    @Override
    public Mono<ListRoutesResponse> list(ListRoutesRequest request) {
        return get(request, ListRoutesResponse.class, builder -> builder.pathSegment("routes"))
            .checkpoint();
    }

    @Override
    public Mono<ListRouteApplicationsResponse> listApplications(ListRouteApplicationsRequest request) {
        return get(request, ListRouteApplicationsResponse.class, builder -> builder.pathSegment("routes", request.getRouteId(), "apps"))
            .checkpoint();
    }

    @Override
    public Mono<ListRouteMappingsResponse> listMappings(ListRouteMappingsRequest request) {
        return get(request, ListRouteMappingsResponse.class, builder -> builder.pathSegment("routes", request.getRouteId(), "route_mappings"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeApplication(RemoveRouteApplicationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("routes", request.getRouteId(), "apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateRouteResponse> update(UpdateRouteRequest request) {
        return put(request, UpdateRouteResponse.class, builder -> builder.pathSegment("routes", request.getRouteId()))
            .checkpoint();
    }

}
