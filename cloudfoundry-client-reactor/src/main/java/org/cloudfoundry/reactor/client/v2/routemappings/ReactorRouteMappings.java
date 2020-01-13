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

package org.cloudfoundry.reactor.client.v2.routemappings;

import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routemappings.RouteMappings;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link RouteMappings}
 */
public final class ReactorRouteMappings extends AbstractClientV2Operations implements RouteMappings {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorRouteMappings(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateRouteMappingResponse> create(CreateRouteMappingRequest request) {
        return post(request, CreateRouteMappingResponse.class, builder -> builder.pathSegment("route_mappings"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteRouteMappingResponse> delete(DeleteRouteMappingRequest request) {
        return delete(request, DeleteRouteMappingResponse.class, builder -> builder.pathSegment("route_mappings", request.getRouteMappingId()))
            .checkpoint();
    }

    @Override
    public Mono<GetRouteMappingResponse> get(GetRouteMappingRequest request) {
        return get(request, GetRouteMappingResponse.class, builder -> builder.pathSegment("route_mappings", request.getRouteMappingId()))
            .checkpoint();
    }

    @Override
    public Mono<ListRouteMappingsResponse> list(ListRouteMappingsRequest request) {
        return get(request, ListRouteMappingsResponse.class, builder -> builder.pathSegment("route_mappings"))
            .checkpoint();
    }

}
