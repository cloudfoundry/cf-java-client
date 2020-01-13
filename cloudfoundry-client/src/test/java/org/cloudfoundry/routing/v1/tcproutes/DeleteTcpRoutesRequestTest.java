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

public class DeleteTcpRoutesRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noBackendIp() {
        DeleteTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteDeletion.builder()
                .backendPort(9999)
                .port(999)
                .routerGroupId("test-router-group-id")
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noBackendPort() {
        DeleteTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteDeletion.builder()
                .backendIp("test-backend-ip")
                .port(999)
                .routerGroupId("test-router-group-id")
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPort() {
        DeleteTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteDeletion.builder()
                .backendIp("test-backend-ip")
                .backendPort(9999)
                .routerGroupId("test-router-group-id")
                .build())
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noRouterGroupId() {
        DeleteTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteDeletion.builder()
                .backendIp("test-backend-ip")
                .backendPort(9999)
                .port(999)
                .build())
            .build();
    }

    @Test
    public void valid() {
        DeleteTcpRoutesRequest.builder()
            .tcpRoute(TcpRouteDeletion.builder()
                .backendIp("test-backend-ip")
                .backendPort(9999)
                .port(999)
                .routerGroupId("test-router-group-id")
                .build())
            .build();
    }

}
