/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cloudfoundry.client.v3.Metadata;
import org.junit.jupiter.api.Test;

class UpdateRouteRequestTest {

    @Test
    void noRouteId() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UpdateRouteRequest.builder().build();
                });
    }

    @Test
    void valid() {
        UpdateRouteRequest.builder()
                .metadata(Metadata.builder().label("test-key", "test-value").build())
                .routeId("test-route-id")
                .build();
    }

    @Test
    void validWithRouteOptions() {
        UpdateRouteRequest request = UpdateRouteRequest.builder()
                .metadata(Metadata.builder().label("test-key", "test-value").build())
                .options(
                        RouteOptions.builder()
                                    .value("loadbalancing", "hash")
                                    .value("hash_header", "X-Hash")
                                    .value("hash_balance", "90")
                                    .build())
                .routeId("test-route-id")
                .build();

        assertEquals("hash", request.getOptions().getLoadbalancing().get());
        assertEquals("X-Hash", request.getOptions().getHashHeader().get());
        assertEquals("90", request.getOptions().getHashBalance().get());
    }
}
