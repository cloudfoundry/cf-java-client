/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.spaces;

import org.cloudfoundry.client.v3.spaces.*;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link SpacesV3}
 */
public final class ReactorSpacesV3 extends AbstractClientV3Operations implements SpacesV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorSpacesV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<AssignSpaceIsolationSegmentResponse> assignIsolationSegment(AssignSpaceIsolationSegmentRequest request) {
        return patch(request, AssignSpaceIsolationSegmentResponse.class, builder -> builder.pathSegment("spaces", request.getSpaceId(), "relationships", "isolation_segment"))
            .checkpoint();
    }

    @Override
    public Mono<CreateSpaceResponse> create(CreateSpaceRequest request) {
        return post(request, CreateSpaceResponse.class, builder -> builder.pathSegment("spaces"))
            .checkpoint();
    }

    @Override
    public Mono<GetSpaceResponse> get(GetSpaceRequest request) {
        return get(request, GetSpaceResponse.class, builder -> builder.pathSegment("spaces", request.getSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<GetSpaceIsolationSegmentResponse> getIsolationSegment(GetSpaceIsolationSegmentRequest request) {
        return get(request, GetSpaceIsolationSegmentResponse.class, builder -> builder.pathSegment("spaces", request.getSpaceId(), "relationships", "isolation_segment"))
            .checkpoint();
    }

    @Override
    public Mono<ListSpacesResponse> list(ListSpacesRequest request) {
        return get(request, ListSpacesResponse.class, builder -> builder.pathSegment("spaces"))
            .checkpoint();
    }

    @Override public Mono<String> deleteUnmappedRoutes(DeleteUnmappedRoutesRequest request) {
        return delete(request, builder -> builder.pathSegment("spaces", request.getSpaceId(), "routes"))
            .checkpoint();
    }

}
