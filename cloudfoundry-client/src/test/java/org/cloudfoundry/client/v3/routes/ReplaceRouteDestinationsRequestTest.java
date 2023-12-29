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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ReplaceRouteDestinationsRequestTest {

    @Test
    void noRouteId() {
        assertThrows(IllegalStateException.class, () -> {
            ReplaceRouteDestinationsRequest.builder()
                .destination(Destination.builder()
                    .application(Application.builder()
                        .applicationId("test-application-id")
                        .process(Process.builder()
                            .type("test-type")
                            .build())
                        .build())
                    .destinationId("test-destination-id")
                    .port(999)
                    .build())
                .build();
        });
    }

    @Test
    void valid() {
        ReplaceRouteDestinationsRequest.builder()
            .routeId("test-route-destination-id")
            .build();
    }
}
