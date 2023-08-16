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

import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.Relationship;

import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.operations.v3.mapper.MapperUtils;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import org.mockito.MockedStatic;

public final class CreateRouteTest extends AbstractOperationsTest {

    private final DefaultRoutes routes = new DefaultRoutes(Mono.just(this.cloudFoundryClient),
            Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));
    private final String TEST_DOMAIN_NAME = "test-domain";
    private final String TEST_HOST = "192.168.0,.1";
    private final Integer TEST_PORT = 8080;
    private final String TEST_DOMAIN_ID = "test-domain-id";
    private MockedStatic<MapperUtils> mappingUtilitiesMock;

    @Before
    public void settup() {

        mappingUtilitiesMock = Mockito.mockStatic(MapperUtils.class);
        mappingUtilitiesMock
                .when(() -> MapperUtils.getSpaceIdByName(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
                        TEST_SPACE_NAME))
                .thenReturn(Mono.just(TEST_SPACE_ID));
        mappingUtilitiesMock
                .when(() -> MapperUtils.getDomainIdByName(this.cloudFoundryClient, TEST_ORGANIZATION_ID,
                        TEST_DOMAIN_NAME))
                .thenReturn(Mono.just(TEST_DOMAIN_ID));

        when(this.cloudFoundryClient.routesV3()
                .create(org.cloudfoundry.client.v3.routes.CreateRouteRequest.builder()
                        .relationships(RouteRelationships.builder()
                                .space(ToOneRelationship.builder()
                                        .data(Relationship.builder()
                                                .id(TEST_SPACE_ID)
                                                .build())
                                        .build())
                                .domain(ToOneRelationship.builder()
                                        .data(Relationship.builder()
                                                .id(TEST_DOMAIN_ID)
                                                .build())
                                        .build())
                                .build())
                        .host(TEST_HOST)
                        .port(TEST_PORT)
                        .build()))
                .thenReturn(Mono
                        .just(fill(org.cloudfoundry.client.v3.routes.CreateRouteResponse.builder()).build()));
    }

    @After
    public void teardown() {
        mappingUtilitiesMock.close();
    }

    @Test
    public void createRoute() {

        this.routes.create(CreateRouteRequest.builder()
                .host(TEST_HOST)
                .port(TEST_PORT)
                .space(TEST_SPACE_NAME)
                .domain(TEST_DOMAIN_NAME)
                .build())
                .as(StepVerifier::create)
                .expectNext(1)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

}
