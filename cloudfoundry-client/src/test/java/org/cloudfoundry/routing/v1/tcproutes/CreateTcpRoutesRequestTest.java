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

package org.cloudfoundry.routing.v1.tcproutes;

import org.junit.Test;

public class CreateTcpRoutesRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noBackendIp() {
        CreateTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteConfiguration.builder()
                .backendPort(9999)
                .port(999)
                .routerGroupId("test-router-group-id")
                .ttl(99)
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noBackendPort() {
        CreateTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteConfiguration.builder()
                .backendIp("test-backend-ip")
                .port(999)
                .routerGroupId("test-router-group-id")
                .ttl(99)
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPort() {
        CreateTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteConfiguration.builder()
                .backendIp("test-backend-ip")
                .backendPort(9999)
                .routerGroupId("test-router-group-id")
                .ttl(99)
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noRouterGroupId() {
        CreateTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteConfiguration.builder()
                .backendIp("test-backend-ip")
                .backendPort(9999)
                .port(999)
                .ttl(99)
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noTtl() {
        CreateTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteConfiguration.builder()
                .backendIp("test-backend-ip")
                .backendPort(9999)
                .port(999)
                .routerGroupId("test-router-group-id")
                .build())
            .build();
    }

    @Test
    public void valid() {
        CreateTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteConfiguration.builder()
                .backendIp("test-backend-ip")
                .backendPort(9999)
                .port(999)
                .routerGroupId("test-router-group-id")
                .ttl(99)
                .build())
            .build();
    }

}
