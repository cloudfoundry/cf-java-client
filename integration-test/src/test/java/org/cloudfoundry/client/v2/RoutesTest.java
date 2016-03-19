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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.GetRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;
import reactor.core.tuple.Tuple3;

import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class RoutesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void associateApplication() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> createApplicationAndRoute(this.cloudFoundryClient, domainId, spaceId, applicationName)))
            .then(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMap(routeId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void create() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> Mono
                .when(
                    Mono.just(domainId),
                    Mono.just(spaceId),
                    this.cloudFoundryClient.routes()
                        .create(CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build())
                        .map(ResourceUtils::getEntity))
            ))
            .subscribe(this.<Tuple3<String, String, RouteEntity>>testSubscriber()
                .assertThat(consumer(RoutesTest::assertDomainIdAndSpaceId)));
    }

    @Test
    public void delete() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build())
                .map(ResourceUtils::getId)))
            .then(routeId -> this.cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .async(true)
                    .routeId(routeId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(jobId -> JobUtils.waitForCompletion(this.cloudFoundryClient, jobId))
            .subscribe(testSubscriber());
    }

    @Test
    public void exists() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .as(thenKeep(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .spaceId(spaceId)
                    .build()))))
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void existsDoesNotExist() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .as(thenKeep(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .spaceId(spaceId)
                    .build()))))
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host("test-host-2")
                    .build())))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void get() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> Mono
                .when(
                    Mono.just(domainId),
                    Mono.just(spaceId),
                    this.cloudFoundryClient.routes()
                        .create(CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build())
                        .map(ResourceUtils::getId))
            ))
            .then(function((domainId, spaceId, routeId) -> Mono
                .when(
                    Mono.just(domainId),
                    Mono.just(spaceId),
                    this.cloudFoundryClient.routes()
                        .get(GetRouteRequest.builder()
                            .routeId(routeId)
                            .build())
                        .map(ResourceUtils::getEntity))
            ))
            .subscribe(this.<Tuple3<String, String, RouteEntity>>testSubscriber()
                .assertThat(consumer(RoutesTest::assertDomainIdAndSpaceId)));
    }

    @Test
    public void listApplications() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> createApplicationAndRoute(this.cloudFoundryClient, domainId, spaceId, applicationName)))
            .then(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMap(routeId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterByDiego() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> createApplicationAndRoute(this.cloudFoundryClient, domainId, spaceId, applicationName)))
            .then(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMap(routeId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .diego(true)
                        .page(page)
                        .routeId(routeId)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterByName() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> createApplicationAndRoute(this.cloudFoundryClient, domainId, spaceId, applicationName)))
            .then(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .flatMap(routeId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .name(applicationName)
                        .page(page)
                        .routeId(routeId)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> createApplicationAndRoute(this.cloudFoundryClient, domainId, spaceId, applicationName)))
            .then(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .and(this.organizationId)
            .flatMap(function((routeId, organizationId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterBySpaceId() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> createApplicationAndRoute(this.cloudFoundryClient, domainId, spaceId, applicationName)))
            .then(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId)))
            .and(this.spaceId)
            .flatMap(function((routeId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Ignore("TODO: implement once list stacks available https://www.pivotaltracker.com/story/show/101527384")
    @Test
    public void listApplicationsFilterByStackId() {
        Assert.fail();
    }

    @Test
    public void listFilterByDomainId() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .as(thenKeep(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build()))))
            .flatMap(function((domainId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .domainId(domainId)
                        .page(page)
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByHost() {
        String domainName = getDomainName();
        String host = getHostName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host(host)
                    .spaceId(spaceId)
                    .build())))
            .flatMap(response -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .host(host)
                        .page(page)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByOrganizationId() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build())
                .then(response -> this.organizationId)))
            .flatMap(organizationId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .count()
            .subscribe(this.<Long>testSubscriber()
                .assertThat(count -> assertTrue(count > 1)));
    }

    @Test
    public void listFilterByPath() {
        String domainName = getDomainName();
        String path = getPath();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .path(path)
                    .spaceId(spaceId)
                    .build())))
            .flatMap(response -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .list(ListRoutesRequest.builder()
                        .page(page)
                        .path(path)
                        .build())))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void removeApplication() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> createApplicationAndRoute(this.cloudFoundryClient, domainId, spaceId, applicationName)))
            .as(thenKeep(function((applicationId, routeId) -> associateApplicationWithRoute(this.cloudFoundryClient, applicationId, routeId))))
            .as(thenKeep((function((applicationId, routeId) -> this.cloudFoundryClient.routes()
                .removeApplication(RemoveRouteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .routeId(routeId)
                    .build())))))
            .flatMap(function((applicationId, routeId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.routes()
                    .listApplications(ListRouteApplicationsRequest.builder()
                        .page(page)
                        .routeId(routeId)
                        .build()))))
            .subscribe(testSubscriber()
                .assertCount(0));
    }

    @Test
    public void update() {
        String domainName = getDomainName();

        this.organizationId
            .then(organizationId -> createDomainId(this.cloudFoundryClient, organizationId, domainName))
            .and(this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build())
                .map(ResourceUtils::getId)))
            .then(routeId -> this.cloudFoundryClient.routes()
                .update(UpdateRouteRequest.builder()
                    .host("test-host")
                    .routeId(routeId)
                    .build())
                .map(ResourceUtils::getEntity))
            .subscribe(this.<RouteEntity>testSubscriber()
                .assertThat(entity -> assertEquals("test-host", entity.getHost())));
    }

    private static void assertDomainIdAndSpaceId(String domainId, String spaceId, RouteEntity entity) {
        assertEquals(domainId, entity.getDomainId());
        assertEquals(spaceId, entity.getSpaceId());
    }

    private static Mono<String> associateApplicationWithRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.routes()
            .associateApplication(AssociateRouteApplicationRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build())
            .map(response -> routeId);
    }

    private static Mono<Tuple2<String, String>> createApplicationAndRoute(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId, String applicationName) {
        return Mono
            .when(
                cloudFoundryClient.applicationsV2()
                    .create(CreateApplicationRequest.builder()
                        .diego(true)
                        .name(applicationName)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getId),
                cloudFoundryClient.routes()
                    .create(CreateRouteRequest.builder()
                        .domainId(domainId)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getId)
            );
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domainName) {
        return cloudFoundryClient.domains()
            .create(CreateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build())
            .map(ResourceUtils::getId);
    }

}
