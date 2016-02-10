/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.routes.CheckRouteRequest;
import org.cloudfoundry.operations.routes.CreateRouteRequest;
import org.cloudfoundry.operations.routes.DeleteRouteRequest;
import org.cloudfoundry.operations.routes.ListRoutesRequest;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.operations.routes.ListRoutesRequest.Level.ORGANIZATION;
import static org.cloudfoundry.operations.routes.ListRoutesRequest.Level.SPACE;

public final class RoutesTest extends AbstractIntegrationTest {

    public static final String TEST_APPLICATION_NAME = "application";

    private static final String TEST_DOMAIN_NAME = "test.domain";

    private static final String TEST_HOST = "host";

    private static final String TEST_INVALID_DOMAIN_NAME = "invalid-domain";

    private static final String TEST_PATH = "/path";

    private Mono<Void> route;

    @Before
    public void before() {
        this.route = this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(TEST_DOMAIN_NAME)
                .organization(organizationName)
                .build())
            .after(() -> this.cloudFoundryOperations.routes()
                .create(CreateRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .space(spaceName)
                    .build()));
    }

    @Test
    public void checkFalse() {
        this.cloudFoundryOperations.routes()
            .check(CheckRouteRequest.builder()
                .domain(TEST_INVALID_DOMAIN_NAME)
                .host(TEST_HOST)
                .path(TEST_PATH)
                .build())
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void checkTrue() {
        this.route
            .after(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void create() {
        this.route
            .after(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void createInvalidDomain() {
        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(TEST_INVALID_DOMAIN_NAME)
                .organization(organizationName)
                .build())
            .after(() -> this.cloudFoundryOperations.routes()
                .create(CreateRouteRequest.builder()
                    .domain(TEST_INVALID_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .space(spaceName)
                    .build()))
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class));
    }

    @Test
    public void delete() {
        this.route
            .after(() -> this.cloudFoundryOperations.routes()
                .delete(DeleteRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .after(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void deleteInvalidDomain() {
        this.route
            .after(() -> this.cloudFoundryOperations.routes()
                .delete(DeleteRouteRequest.builder()
                    .domain(TEST_INVALID_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class));
    }

    @Test
    public void deleteOrphanedRoutes() {
        this.route
            .after(() -> this.cloudFoundryOperations.routes()
                .deleteOrphanedRoutes())
            .after(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void listWithOrganizationLevel() {
        this.route
            .after(() -> Mono.from(cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(ORGANIZATION)
                    .build())))
            .where(returnedRoute -> TEST_DOMAIN_NAME.equals(returnedRoute.getDomain()) &&
                TEST_HOST.equals(returnedRoute.getHost()) &&
                TEST_PATH.equals(returnedRoute.getPath()))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listWithSpaceLevel() {
        this.route
            .after(() -> Mono.from(cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build())))
            .where(returnedRoute -> TEST_DOMAIN_NAME.equals(returnedRoute.getDomain()) &&
                TEST_HOST.equals(returnedRoute.getHost()) &&
                TEST_PATH.equals(returnedRoute.getPath()))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void map() {
        this.route
            .after(() -> this.cloudFoundryOperations.routes()
                .map(MapRouteRequest.builder()
                    .applicationName(TEST_APPLICATION_NAME)
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void unmap() {
        this.route
            .after(() -> this.cloudFoundryOperations.routes()
                .unmap(UnmapRouteRequest.builder()
                    .applicationName(TEST_APPLICATION_NAME)
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build()))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

}
