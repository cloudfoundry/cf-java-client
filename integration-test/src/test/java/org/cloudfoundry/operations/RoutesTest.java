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
import org.cloudfoundry.operations.routes.Route;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.rx.Stream;

import static org.cloudfoundry.operations.routes.ListRoutesRequest.Level.ORGANIZATION;
import static org.cloudfoundry.operations.routes.ListRoutesRequest.Level.SPACE;
import static org.cloudfoundry.utils.OperationUtils.afterComplete;
import static org.cloudfoundry.utils.OperationUtils.afterStreamComplete;

public final class RoutesTest extends AbstractIntegrationTest {

    private static final String TEST_APPLICATION_NAME = "application";

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
                .organization(this.organizationName)
                .build())
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .create(CreateRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .space(this.spaceName)
                    .build())));
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
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void create() {
        this.route
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void createRouteWithNonExistentDomain() {
        this.cloudFoundryOperations.routes()
                .create(CreateRouteRequest.builder()
                    .domain(TEST_INVALID_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .space(this.spaceName)
                    .build())
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class));
    }

    @Test
    public void delete() {
        this.route
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .delete(DeleteRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void deleteInvalidDomain() {
        this.route
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .delete(DeleteRouteRequest.builder()
                    .domain(TEST_INVALID_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class));
    }

    @Test
    public void deleteOrphanedRoutes() {
        this.route
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .deleteOrphanedRoutes()))
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void listWithOrganizationLevel() {
        this.route
            .as(Stream::from)
            .as(afterStreamComplete(() -> Stream.from(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(ORGANIZATION)
                    .build()))))
            .filter(returnedRoute -> routeMatches(returnedRoute, TEST_DOMAIN_NAME, TEST_HOST, TEST_PATH))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listWithSpaceLevel() {
        this.route
            .as(Stream::from)
            .as(afterStreamComplete(() -> Stream.from(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))))
            .filter(returnedRoute -> routeMatches(returnedRoute, TEST_DOMAIN_NAME, TEST_HOST, TEST_PATH))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void map() {
        this.route
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .map(MapRouteRequest.builder()
                    .applicationName(TEST_APPLICATION_NAME)
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void unmap() {
        this.route
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .unmap(UnmapRouteRequest.builder()
                    .applicationName(TEST_APPLICATION_NAME)
                    .domain(TEST_DOMAIN_NAME)
                    .host(TEST_HOST)
                    .path(TEST_PATH)
                    .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    private static boolean routeMatches(Route returnedRoute, String domainName, String host, String path) {
        return domainName.equals(returnedRoute.getDomain()) && host.equals(returnedRoute.getHost()) && path.equals(returnedRoute.getPath());
    }

}
