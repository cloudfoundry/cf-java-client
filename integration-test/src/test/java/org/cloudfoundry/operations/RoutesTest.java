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
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.routes.CheckRouteRequest;
import org.cloudfoundry.operations.routes.CreateRouteRequest;
import org.cloudfoundry.operations.routes.DeleteRouteRequest;
import org.cloudfoundry.operations.routes.ListRoutesRequest;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.Route;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

import static org.cloudfoundry.operations.routes.Level.ORGANIZATION;
import static org.cloudfoundry.operations.routes.Level.SPACE;

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
            .then(this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void create() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .then(this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
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
            .then(this.cloudFoundryOperations.routes()
                .delete(DeleteRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .then(this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
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
            .then(this.cloudFoundryOperations.routes()
                .deleteOrphanedRoutes())
            .then(this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void listWithOrganizationLevel() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(ORGANIZATION)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, null))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listWithSpaceLevel() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, null))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void map() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        Mono
            .when(
                createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, true)
            )
            .then(this.cloudFoundryOperations.routes()
                .map(MapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, applicationName))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void mapNoPath() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = null;

        Mono
            .when(
                createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, true)
            )
            .then(this.cloudFoundryOperations.routes()
                .map(MapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, applicationName))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void unmap() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = getPath();

        Mono
            .when(
                createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, true)
            )
            .then(this.cloudFoundryOperations.routes()
                .map(MapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .then(this.cloudFoundryOperations.routes()
                .unmap(UnmapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, applicationName))
            .subscribe(testSubscriber()); // expect no results
    }

    @Test
    public void unmapNoPath() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();
        String hostName = getHostName();
        String path = null;

        Mono
            .when(
                createRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, true)
            )
            .then(this.cloudFoundryOperations.routes()
                .map(MapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .then(this.cloudFoundryOperations.routes()
                .unmap(UnmapRouteRequest.builder()
                    .applicationName(applicationName)
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, applicationName))
            .subscribe(testSubscriber()); // expect no results
    }

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, InputStream applicationBits, String name, Boolean noStart) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(applicationBits)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .memory(64)
                .name(name)
                .noStart(noStart)
                .build());
    }

    private static Mono<Void> createRoute(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName, String domainName, String hostName, String path) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build())
            .then(cloudFoundryOperations.routes()
                .create(CreateRouteRequest.builder()
                    .domain(domainName)
                    .host(hostName)
                    .path(path)
                    .space(spaceName)
                    .build()));
    }

    private static Predicate<Route> filterRoutes(String domainName, String host, String path, String applicationName) {
        return route -> Optional.ofNullable(domainName).map(route.getDomain()::equals).orElse(true)
            && Optional.ofNullable(host).map(route.getHost()::equals).orElse(true)
            && Optional.ofNullable(applicationName).map(Collections::singletonList).map(route.getApplications()::equals).orElse(true)
            && Optional.ofNullable(path).map(route.getPath()::equals).orElse(true);
    }

    private static InputStream getApplicationBits() {
        try {
            return new ClassPathResource("test-application.zip").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
