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

package org.cloudfoundry.operations.v3.routes;

import org.cloudfoundry.operations.v3.routes.Route;
import org.cloudfoundry.client.v3.domains.CheckReservedRoutesRequest;
import org.cloudfoundry.client.v3.domains.CheckReservedRoutesResponse;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.operations.v3.mapper.MapperUtils;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.cloudfoundry.client.v3.jobs.GetJobRequest;
import org.cloudfoundry.client.v3.jobs.GetJobResponse;
import org.cloudfoundry.client.v3.jobs.JobState;
import org.cloudfoundry.client.v3.roles.ListRolesRequest;

import java.time.Duration;

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.mockito.MockedStatic;

public final class ListRoutesTest extends AbstractOperationsTest {

        private final DefaultRoutes routes = new DefaultRoutes(Mono.just(this.cloudFoundryClient),
                        Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        private MockedStatic<MapperUtils> mappingUtilitiesMock;

        @Before
        public void settup() {

                mappingUtilitiesMock = Mockito.mockStatic(MapperUtils.class);
                mappingUtilitiesMock.when(() -> MapperUtils.listRoutes(this.cloudFoundryClient,
                                new String[] { TEST_ORGANIZATION_ID },
                                null, null, null, null, null, null))
                                .thenReturn(Flux.just(fill(org.cloudfoundry.client.v3.routes.RouteResource.builder())
                                                .build()));

        }

        @After
        public void teardown() {
                mappingUtilitiesMock.close();
        }

        @Test
        public void listRoutes() {

                this.routes.list(ListRoutesRequest.builder()
                                .level(Level.SPACE)
                                .build())
                                .as(StepVerifier::create)
                                .expectNext(Route.builder()
                                                .id("test-id")
                                                .domain("test-id")
                                                .host("test-host")
                                                .path("test-path")
                                                .space("test-id")
                                                .build())
                                .verifyComplete();
        }

}