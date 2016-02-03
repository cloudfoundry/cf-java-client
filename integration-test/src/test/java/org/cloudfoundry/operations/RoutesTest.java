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

public final class RoutesTest extends AbstractIntegrationTest {

    private static final String HOST = "host";

    private static final String INVALID_TEST_DOMAIN_NAME = "invalid-domain";

    private static final String PATH = "/path";

    private static final String TEST_DOMAIN_NAME = "test.domain";

    private Mono<Void> createRoute;

    private Mono<Void> invalidDomain;

    @Before
    public void before() {
        this.invalidDomain = this.cloudFoundryOperations.domains().create(new CreateDomainRequest(INVALID_TEST_DOMAIN_NAME, organizationName));
        this.createRoute =
            this.cloudFoundryOperations.domains().create(new CreateDomainRequest(TEST_DOMAIN_NAME, organizationName))
                .after(() -> this.cloudFoundryOperations.routes().create(new CreateRouteRequest(TEST_DOMAIN_NAME, HOST, PATH, spaceName)));
    }

    @Test
    public void checkFalse() {
        this.cloudFoundryOperations.routes().check(new CheckRouteRequest(INVALID_TEST_DOMAIN_NAME, HOST, PATH))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void checkTrue() {
        this.createRoute.after()
            .after(() -> this.cloudFoundryOperations.routes().check(new CheckRouteRequest(TEST_DOMAIN_NAME, HOST, PATH)))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void create() {
        this.createRoute
            .subscribe(testSubscriber());
    }

    @Test
    public void createInvalidDomain() {
        this.invalidDomain
            .after(() -> this.cloudFoundryOperations.routes().create(new CreateRouteRequest(INVALID_TEST_DOMAIN_NAME, HOST, PATH, spaceName)))
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class));
    }

    @Test
    public void delete() {
        this.createRoute
            .after(() -> this.cloudFoundryOperations.routes().delete(new DeleteRouteRequest(TEST_DOMAIN_NAME, HOST, PATH)))
            .subscribe(testSubscriber());
    }

    @Test
    public void deleteInvalidDomain() {
        this.createRoute
            .after(() -> this.cloudFoundryOperations.routes().delete(new DeleteRouteRequest(INVALID_TEST_DOMAIN_NAME, HOST, PATH)))
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class));
    }

    @Test
    public void deleteOrphanedRoutes() {
        this.createRoute
            .after(() -> this.cloudFoundryOperations.routes().deleteOrphanedRoutes())
            .subscribe(testSubscriber());
    }

    @Test
    public void list() {
        this.createRoute
            .after(() -> Mono.from(cloudFoundryOperations.routes().list(new ListRoutesRequest(ORGANIZATION))))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void map() {
        this.createRoute
            .after(() -> this.cloudFoundryOperations.routes().map(new MapRouteRequest("application", TEST_DOMAIN_NAME, HOST, PATH)))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void unmap() {
        this.createRoute
            .after(() -> this.cloudFoundryOperations.routes().unmap(new UnmapRouteRequest("application", TEST_DOMAIN_NAME, HOST, PATH)))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

}
