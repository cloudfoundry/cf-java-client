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
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.rx.Stream;

import static org.cloudfoundry.operations.routes.ListRoutesRequest.Level.ORGANIZATION;
import static org.cloudfoundry.operations.routes.ListRoutesRequest.Level.SPACE;
import static org.cloudfoundry.util.OperationUtils.afterComplete;
import static org.cloudfoundry.util.OperationUtils.afterStreamComplete;

public final class RoutesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Autowired
    private String spaceName;

    @Test
    public void checkFalse() {
        String domainName = getDomainName();
        String host = getHostName();
        String path = getPath();

        this.cloudFoundryOperations.routes()
            .check(CheckRouteRequest.builder()
                .domain(domainName)
                .host(host)
                .path(path)
                .build())
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void checkTrue() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void create() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void createRouteWithNonExistentDomain() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        this.cloudFoundryOperations.routes()
            .create(CreateRouteRequest.builder()
                .domain(domainName)
                .host(hostName)
                .path(path)
                .space(this.spaceName)
                .build())
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class, "Domain " + domainName + " does not exist"));
    }

    @Test
    public void delete() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .delete(DeleteRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build())))
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void deleteInvalidDomain() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        this.cloudFoundryOperations.routes()
            .delete(DeleteRouteRequest.builder()
                .domain(domainName)
                .host(hostName)
                .path(path)
                .build())
            .subscribe(testSubscriber()
                .assertError(IllegalArgumentException.class, "Domain " + domainName + " does not exist"));
    }

    @Test
    public void deleteOrphanedRoutes() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .deleteOrphanedRoutes()))
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void listWithOrganizationLevel() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(Stream::from)
            .as(afterStreamComplete(() -> Stream.from(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(ORGANIZATION)
                    .build()))))
            .filter(returnedRoute -> routeMatches(returnedRoute, domainName, hostName, path))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listWithSpaceLevel() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(Stream::from)
            .as(afterStreamComplete(() -> Stream.from(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))))
            .filter(returnedRoute -> routeMatches(returnedRoute, domainName, hostName, path))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void map() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .map(MapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("Needs push application - https://www.pivotaltracker.com/story/show/106155434")
    @Test
    public void unmap() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .as(afterComplete(() -> this.cloudFoundryOperations.routes()
                .unmap(UnmapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    private static Mono<Void> createRoute(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName, String domainName, String hostName, String path) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build())
            .as(afterComplete(() -> cloudFoundryOperations.routes()
                .create(CreateRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .space(spaceName)
                    .build())));
    }

    private static boolean routeMatches(Route returnedRoute, String domainName, String host, String path) {
        return domainName.equals(returnedRoute.getDomain()) && host.equals(returnedRoute.getHost()) && path.equals(returnedRoute.getPath());
    }

}
