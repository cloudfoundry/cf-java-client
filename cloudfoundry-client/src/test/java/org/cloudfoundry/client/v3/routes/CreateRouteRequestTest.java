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

import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.junit.jupiter.api.Test;

final class CreateRouteRequestTest {

    @Test
    void invalidWithMissingDomainRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRouteRequest.builder()
                            .relationships(
                                    RouteRelationships.builder()
                                            .space(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-space-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();
                });
    }

    @Test
    void invalidWithMissingRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRouteRequest.builder().build();
                });
    }

    @Test
    void invalidWithMissingSpaceRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRouteRequest.builder()
                            .relationships(
                                    RouteRelationships.builder()
                                            .domain(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-domain-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();
                });
    }

    @Test
    void valid() {
        CreateRouteRequest.builder()
                .relationships(
                        RouteRelationships.builder()
                                .domain(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-domain-id")
                                                                .build())
                                                .build())
                                .space(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-space-id")
                                                                .build())
                                                .build())
                                .build());
    }

    @Test
    void validWithRouteOptions() {
        CreateRouteRequest request =
                CreateRouteRequest.builder()
                        .relationships(
                                RouteRelationships.builder()
                                        .domain(
                                                ToOneRelationship.builder()
                                                        .data(
                                                                Relationship.builder()
                                                                        .id("test-domain-id")
                                                                        .build())
                                                        .build())
                                        .space(
                                                ToOneRelationship.builder()
                                                        .data(
                                                                Relationship.builder()
                                                                        .id("test-space-id")
                                                                        .build())
                                                        .build())
                                        .build())
                        .options(
                                RouteOptions.builder()
                                        .value("loadbalancing", "hash")
                                        .value("hash_header", "X-Hash")
                                        .value("hash_balance", "90")
                                        .build())
                        .build();

        assertEquals("hash", request.getOptions().getLoadbalancing().get());
        assertEquals("X-Hash", request.getOptions().getHashHeader().get());
        assertEquals("90", request.getOptions().getHashBalance().get());
    }
}
