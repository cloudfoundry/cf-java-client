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
import org.cloudfoundry.NameFactory;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.cloudfoundry.routing.v1.routergroups.RouterGroup;
import org.cloudfoundry.routing.v1.tcproutes.CreateTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.CreateTcpRoutesResponse;
import org.cloudfoundry.routing.v1.tcproutes.DeleteTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.EventsRequest;
import org.cloudfoundry.routing.v1.tcproutes.ListTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.ListTcpRoutesResponse;
import org.cloudfoundry.routing.v1.tcproutes.TcpRoute;
import org.cloudfoundry.routing.v1.tcproutes.TcpRouteConfiguration;
import org.cloudfoundry.routing.v1.tcproutes.TcpRouteDeletion;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class TcpRoutesTest extends AbstractIntegrationTest {

    private static final String DEFAULT_ROUTER_GROUP = "default-tcp";

    @Autowired
    private NameFactory nameFactory;

    @Autowired
    private RoutingClient routingClient;

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String backendIp = this.nameFactory.getIpAddress();
        Integer backendPort = this.nameFactory.getPort();
        Integer port = this.nameFactory.getPort();

        getRouterGroupId(this.routingClient, DEFAULT_ROUTER_GROUP)
            .flatMap(routerGroupId -> this.routingClient.tcpRoutes()
                .create(CreateTcpRoutesRequest.builder()
                    .tcpRoute(TcpRouteConfiguration.builder()
                        .backendIp(backendIp)
                        .backendPort(backendPort)
                        .port(port)
                        .routerGroupId(routerGroupId)
                        .ttl(59)
                        .build())
                    .build()))
            .then(requestListTcpRoutes(this.routingClient))
            .flatMapIterable(ListTcpRoutesResponse::getTcpRoutes)
            .filter(route -> backendIp.equals(route.getBackendIp()))
            .filter(route -> backendPort.equals(route.getBackendPort()))
            .map(TcpRoute::getPort)
            .as(StepVerifier::create)
            .expectNext(port)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String backendIp = this.nameFactory.getIpAddress();
        Integer backendPort = this.nameFactory.getPort();
        Integer port = this.nameFactory.getPort();

        getRouterGroupId(this.routingClient, DEFAULT_ROUTER_GROUP)
            .delayUntil(routerGroupId -> createTcpRoute(this.routingClient, backendIp, backendPort, port, routerGroupId))
            .flatMap(routerGroupId -> this.routingClient.tcpRoutes()
                .delete(DeleteTcpRoutesRequest.builder()
                    .tcpRoute(TcpRouteDeletion.builder()
                        .backendIp(backendIp)
                        .backendPort(backendPort)
                        .port(port)
                        .routerGroupId(routerGroupId)
                        .build())
                    .build()))
            .then(requestListTcpRoutes(this.routingClient))
            .flatMapIterable(ListTcpRoutesResponse::getTcpRoutes)
            .filter(route -> backendIp.equals(route.getBackendIp()))
            .filter(route -> backendPort.equals(route.getBackendPort()))
            .filter(route -> port.equals(route.getPort()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void events() {
        String backendIp = this.nameFactory.getIpAddress();
        Integer backendPort = this.nameFactory.getPort();
        Integer port = this.nameFactory.getPort();

        Flux.firstEmitting(
            getRouterGroupId(this.routingClient, DEFAULT_ROUTER_GROUP)
                .flatMapMany(routerGroupId -> Flux.interval(Duration.ofMillis(500))
                    .flatMap(i -> createTcpRoute(this.routingClient, backendIp, backendPort, port, routerGroupId)))
                .then(),
            this.routingClient.tcpRoutes()
                .events(EventsRequest.builder()
                    .build())
                .filter(event -> event.getBackendIp().equals(backendIp) && event.getBackendPort().equals(backendPort) && event.getPort().equals(port))
                .take(1))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String backendIp = this.nameFactory.getIpAddress();
        Integer backendPort = this.nameFactory.getPort();
        Integer port = this.nameFactory.getPort();

        getRouterGroupId(this.routingClient, DEFAULT_ROUTER_GROUP)
            .flatMap(routerGroupId -> createTcpRoute(this.routingClient, backendIp, backendPort, port, routerGroupId))
            .then(this.routingClient.tcpRoutes()
                .list(ListTcpRoutesRequest.builder()
                    .build()))
            .flatMapIterable(ListTcpRoutesResponse::getTcpRoutes)
            .filter(route -> backendIp.equals(route.getBackendIp()))
            .map(TcpRoute::getPort)
            .as(StepVerifier::create)
            .expectNext(port)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<CreateTcpRoutesResponse> createTcpRoute(RoutingClient routingClient, String backendIp, Integer backendPort, Integer port, String routerGroupId) {
        return routingClient.tcpRoutes()
            .create(CreateTcpRoutesRequest.builder()
                .tcpRoute(TcpRouteConfiguration.builder()
                    .backendIp(backendIp)
                    .backendPort(backendPort)
                    .port(port)
                    .routerGroupId(routerGroupId)
                    .ttl(59)
                    .build())
                .build());
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

    private static Mono<ListTcpRoutesResponse> requestListTcpRoutes(RoutingClient routingClient) {
        return routingClient.tcpRoutes()
            .list(ListTcpRoutesRequest.builder()
                .build());
    }

}
