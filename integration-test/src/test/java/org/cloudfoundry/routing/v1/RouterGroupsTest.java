/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.routing.v1;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.cloudfoundry.routing.v1.routergroups.RouterGroup;
import org.cloudfoundry.routing.v1.routergroups.UpdateRouterGroupRequest;
import org.cloudfoundry.routing.v1.routergroups.UpdateRouterGroupResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class RouterGroupsTest extends AbstractIntegrationTest {

    private static final String DEFAULT_ROUTER_GROUP = "default-tcp";

    @Autowired
    private RoutingClient routingClient;

    @Test
    public void list() throws TimeoutException, InterruptedException {
        this.routingClient.routerGroups()
            .list(ListRouterGroupsRequest.builder()
                .build())
            .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
            .map(RouterGroup::getName)
            .filter(DEFAULT_ROUTER_GROUP::equals)
            .as(StepVerifier::create)
            .expectNext(DEFAULT_ROUTER_GROUP)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        getRouterGroupId(this.routingClient, DEFAULT_ROUTER_GROUP)
            .flatMap(routerGroupId -> this.routingClient.routerGroups()
                .update(UpdateRouterGroupRequest.builder()
                    .reservablePorts("61001-61099")
                    .routerGroupId(routerGroupId)
                    .build()))
            .map(UpdateRouterGroupResponse::getReservablePorts)
            .as(StepVerifier::create)
            .expectNext("61001-61099")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> getRouterGroupId(RoutingClient routingClient, String routerGroupName) {
        return requestListRouterGroups(routingClient)
            .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
            .filter(group -> routerGroupName.equals(group.getName()))
            .single()
            .map(RouterGroup::getRouterGroupId);
    }

    private static Mono<ListRouterGroupsResponse> requestListRouterGroups(RoutingClient routingClient) {
        return routingClient.routerGroups()
            .list(ListRouterGroupsRequest.builder()
                .build());
    }

}
