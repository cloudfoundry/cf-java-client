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

package org.cloudfoundry.reactor.routing.v1.tcproutes;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.routing.AbstractRoutingApiTest;
import org.cloudfoundry.routing.v1.ModificationTag;
import org.cloudfoundry.routing.v1.tcproutes.CreateTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.DeleteTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.ListTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.ListTcpRoutesResponse;
import org.cloudfoundry.routing.v1.tcproutes.TcpRoute;
import org.cloudfoundry.routing.v1.tcproutes.TcpRouteConfiguration;
import org.cloudfoundry.routing.v1.tcproutes.TcpRouteDeletion;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorTcpRoutesTest extends AbstractRoutingApiTest {

    private final ReactorTcpRoutes tcpRoutes = new ReactorTcpRoutes(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v1/tcp_routes/create")
                .payload("fixtures/routing/v1/tcproutes/POST_create_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .build())
            .build());

        this.tcpRoutes
            .create(CreateTcpRoutesRequest.builder()
                .tcpRoute(TcpRouteConfiguration.builder()
                    .backendIp("10.1.1.12")
                    .backendPort(60000)
                    .port(5200)
                    .routerGroupId("xyz789")
                    .ttl(30)
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v1/tcp_routes/delete")
                .payload("fixtures/routing/v1/tcproutes/POST_delete_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .build())
            .build());

        this.tcpRoutes
            .delete(DeleteTcpRoutesRequest.builder()
                .tcpRoute(TcpRouteDeletion.builder()
                    .backendIp("10.1.1.12")
                    .backendPort(60000)
                    .port(5200)
                    .routerGroupId("xyz789")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v1/tcp_routes")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/routing/v1/tcproutes/GET_response.json")
                .build())
            .build());

        this.tcpRoutes
            .list(ListTcpRoutesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListTcpRoutesResponse.builder()
                .tcpRoute(TcpRoute.builder()
                    .backendIp("10.1.1.12")
                    .backendPort(60000)
                    .modificationTag(ModificationTag.builder()
                        .modificationTagId("cbdhb4e3-141d-4259-b0ac-99140e8998l0")
                        .index(10)
                        .build())
                    .port(5200)
                    .routerGroupId("xyz789")
                    .ttl(30)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
