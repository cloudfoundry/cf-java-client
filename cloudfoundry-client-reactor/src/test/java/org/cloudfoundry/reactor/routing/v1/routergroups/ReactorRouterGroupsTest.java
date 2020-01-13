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

package org.cloudfoundry.reactor.routing.v1.routergroups;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.routing.AbstractRoutingApiTest;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.cloudfoundry.routing.v1.routergroups.RouterGroup;
import org.cloudfoundry.routing.v1.routergroups.UpdateRouterGroupRequest;
import org.cloudfoundry.routing.v1.routergroups.UpdateRouterGroupResponse;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorRouterGroupsTest extends AbstractRoutingApiTest {

    private final ReactorRouterGroups routerGroups = new ReactorRouterGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v1/router_groups")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/routing/v1/routergroups/GET_response.json")
                .build())
            .build());

        this.routerGroups
            .list(ListRouterGroupsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListRouterGroupsResponse.builder()
                .routerGroup(RouterGroup.builder()
                    .name("default-tcp")
                    .reservablePorts("1024-65535")
                    .routerGroupId("abc123")
                    .type("tcp")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/v1/router_groups/abc123")
                .payload("fixtures/routing/v1/routergroups/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/routing/v1/routergroups/PUT_{id}_response.json")
                .build())
            .build());

        this.routerGroups
            .update(UpdateRouterGroupRequest.builder()
                .reservablePorts("1024-65535")
                .routerGroupId("abc123")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateRouterGroupResponse.builder()
                .name("default-tcp")
                .reservablePorts("1024-65535")
                .routerGroupId("abc123")
                .type("tcp")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
