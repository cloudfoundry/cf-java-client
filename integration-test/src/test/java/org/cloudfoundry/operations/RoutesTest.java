/*
 * Copyright 2013-2017 the original author or authors.
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
import org.cloudfoundry.IfCloudFoundryVersion;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.IfCloudFoundryVersion.CloudFoundryVersion.PCF_1_8;
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
    public void checkFalse() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String host = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        this.cloudFoundryOperations.routes()
            .check(CheckRouteRequest.builder()
                .domain(domainName)
                .host(host)
                .path(path)
                .build())
            .as(StepVerifier::create)
            .expectNext(false)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = PCF_1_8)
    @Test
    public void checkTruePrivateDomainNoHost() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName = null;
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .then(this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .path(path)
                    .build()))
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void checkTrueSharedDomain() throws TimeoutException, InterruptedException {
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
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
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
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createRouteWithNonExistentDomain() throws TimeoutException, InterruptedException {
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
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Domain %s does not exist", domainName))
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/588")
    @Test
    public void createTcpRoute() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, 5000)
            .then(this.cloudFoundryOperations.routes()
                .check(CheckRouteRequest.builder()
                    .domain(domainName)
                    .path(path)
                    .build()))
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
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
            .as(StepVerifier::create)
            .expectNext(false)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteInvalidDomain() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        this.cloudFoundryOperations.routes()
            .delete(DeleteRouteRequest.builder()
                .domain(domainName)
                .host(hostName)
                .path(path)
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Domain %s does not exist", domainName))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteOrphanedRoutes() throws TimeoutException, InterruptedException {
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
            .as(StepVerifier::create)
            .expectNext(false)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/588")
    @Test
    public void deleteTcpRoute() throws TimeoutException, InterruptedException {
        //
    }

    @Test
    public void listWithOrganizationLevel() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(ORGANIZATION)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, null))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listWithSpaceLevel() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path)
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .level(SPACE)
                    .build()))
            .filter(filterRoutes(domainName, hostName, path, null))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void map() throws TimeoutException, InterruptedException, IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                requestCreateApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
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
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void mapNoHost() throws TimeoutException, InterruptedException, IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = null;
        String path = this.nameFactory.getPath();

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                requestCreateApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
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
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void mapNoPath() throws TimeoutException, InterruptedException, IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = null;

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                requestCreateApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
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
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/588")
    @Test
    public void mapTcpRoute() throws TimeoutException, InterruptedException {
        //
    }

    @Test
    public void unmap() throws TimeoutException, InterruptedException, IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = this.nameFactory.getPath();

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                requestCreateApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
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
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unmapNoPath() throws TimeoutException, InterruptedException, IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String path = null;

        Mono
            .when(
                createDomainAndRoute(this.cloudFoundryOperations, this.organizationName, this.spaceName, domainName, hostName, path),
                requestCreateApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
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
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/588")
    @Test
    public void unmapTcpRoute() throws TimeoutException, InterruptedException {
        //
    }

    private static Mono<Void> createDomainAndRoute(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName, String domainName, String hostName, String path) {
        return requestCreateDomain(cloudFoundryOperations, organizationName, domainName)
            .then(requestCreateRoute(cloudFoundryOperations, spaceName, domainName, hostName, path));
    }

    private static Mono<Void> createDomainAndRoute(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName, String domainName, Integer port) {
        return requestCreateDomain(cloudFoundryOperations, organizationName, domainName)
            .then(requestCreateRoute(cloudFoundryOperations, spaceName, domainName, port));
    }

    private static Mono<Void> createSharedDomainAndRoute(CloudFoundryOperations cloudFoundryOperations, String spaceName, String domainName, String hostName, String path) {
        return requestCreateSharedDomain(cloudFoundryOperations, domainName)
            .then(requestCreateRoute(cloudFoundryOperations, spaceName, domainName, hostName, path));
    }

    private static Predicate<Route> filterRoutes(String domainName, String host, String path, String applicationName) {
        return route -> Optional.ofNullable(domainName).map(route.getDomain()::equals).orElse(true)
            && Optional.ofNullable(host).map(route.getHost()::equals).orElse(true)
            && Optional.ofNullable(applicationName).map(Collections::singletonList).map(route.getApplications()::equals).orElse(true)
            && Optional.ofNullable(path).map(route.getPath()::equals).orElse(true);
    }

    private static Mono<Void> requestCreateApplication(CloudFoundryOperations cloudFoundryOperations, Path application, String name, Boolean noStart) {
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

    private static Mono<Void> requestCreateDomain(CloudFoundryOperations cloudFoundryOperations, String organizationName, String domainName) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Void> requestCreateRoute(CloudFoundryOperations cloudFoundryOperations, String spaceName, String domainName, String hostName, String path) {
        return cloudFoundryOperations.routes()
            .create(CreateRouteRequest.builder()
                .domain(domainName)
                .host(hostName)
                .path(path)
                .space(spaceName)
                .build());
    }

    private static Mono<Void> requestCreateRoute(CloudFoundryOperations cloudFoundryOperations, String spaceName, String domainName, Integer port) {
        return cloudFoundryOperations.routes()
            .create(CreateRouteRequest.builder()
                .domain(domainName)
                .port(port)
                .space(spaceName)
                .build());
    }

    private static Mono<Void> requestCreateSharedDomain(CloudFoundryOperations cloudFoundryOperations, String domainName) {
        return cloudFoundryOperations.domains()
            .createShared(CreateSharedDomainRequest.builder()
                .domain(domainName)
                .build());
    }

}
