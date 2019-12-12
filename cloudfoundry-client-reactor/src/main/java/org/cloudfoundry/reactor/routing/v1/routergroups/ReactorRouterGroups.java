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

package org.cloudfoundry.reactor.routing.v1.routergroups;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.routing.v1.AbstractRoutingV1Operations;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.cloudfoundry.routing.v1.routergroups.RouterGroups;
import org.cloudfoundry.routing.v1.routergroups.UpdateRouterGroupRequest;
import org.cloudfoundry.routing.v1.routergroups.UpdateRouterGroupResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link RouterGroups}
 */
public class ReactorRouterGroups extends AbstractRoutingV1Operations implements RouterGroups {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorRouterGroups(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider,
                               Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<ListRouterGroupsResponse> list(ListRouterGroupsRequest request) {
        return get(ListRouterGroupsResponse.class, builder -> builder.pathSegment("v1", "router_groups")).checkpoint();
    }

    @Override
    public Mono<UpdateRouterGroupResponse> update(UpdateRouterGroupRequest request) {
        return put(request, UpdateRouterGroupResponse.class,
            builder -> builder.pathSegment("v1", "router_groups", request.getRouterGroupId())).checkpoint();
    }

}
