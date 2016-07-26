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
import org.cloudfoundry.operations.domains.CreateSharedDomainRequest;
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
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
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
        String domainName = this.nameFactory.getDomainName();
        String host = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        this.cloudFoundryOperations.routes()
            .check(CheckRouteRequest.builder()
                .domain(domainName)
                .host(host)
                .path(path)
                .build())
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Ignore("Awaiting resolution of https://github.com/cloudfoundry/cloud_controller_ng/issues/650")
    @Test
    public void checkTruePrivateDomainNoHost() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .then(this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .path(path)
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void checkTrueSharedDomain() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createSharedDomainAndRoute(this.cloudFoundryOperations, this.spaceName, domainName, hostName, path)
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
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
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
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

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
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
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
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

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
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
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
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
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
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
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
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
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
    public void mapNoHost() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = null;
        String path = this.nameFactory.getPath();

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
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
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = null;

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
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
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
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
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = null;

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
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

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, Path application, String name, Boolean noStart) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(application)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .memory(64)
                .name(name)
                .noStart(noStart)
                .build());
    }

    private static Mono<Void> createDomain(CloudFoundryOperations cloudFoundryOperations, String organizationName, String domainName) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Void> createDomainAndRoute(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName, String domainName, String hostName, String path) {
        return createDomain(cloudFoundryOperations, organizationName, domainName)
            .then(createRoute(cloudFoundryOperations, spaceName, domainName, hostName, path));
    }

    private static Mono<Void> createRoute(CloudFoundryOperations cloudFoundryOperations, String spaceName, String domainName, String hostName, String path) {
        return cloudFoundryOperations.routes()
            .create(CreateRouteRequest.builder()
                .domain(domainName)
                .host(hostName)
                .path(path)
                .space(spaceName)
                .build());
    }

    private static Mono<Void> createSharedDomain(CloudFoundryOperations cloudFoundryOperations, String domainName) {
        return cloudFoundryOperations.domains()
            .createShared(CreateSharedDomainRequest.builder()
                .domain(domainName)
                .build());
    }

    private static Mono<Void> createSharedDomainAndRoute(CloudFoundryOperations cloudFoundryOperations, String spaceName, String domainName, String hostName, String path) {
        return createSharedDomain(cloudFoundryOperations, domainName)
            .then(createRoute(cloudFoundryOperations, spaceName, domainName, hostName, path));
    }

    private static Predicate<Route> filterRoutes(String domainName, String host, String path, String applicationName) {
        return route -> Optional.ofNullable(domainName).map(route.getDomain()::equals).orElse(true)
            && Optional.ofNullable(host).map(route.getHost()::equals).orElse(true)
            && Optional.ofNullable(applicationName).map(Collections::singletonList).map(route.getApplications()::equals).orElse(true)
            && Optional.ofNullable(path).map(route.getPath()::equals).orElse(true);
    }

    private static Path getApplicationPath() {
        try {
            return new ClassPathResource("test-application.zip").getFile().toPath();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
