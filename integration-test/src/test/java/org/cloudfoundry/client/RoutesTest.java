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

package org.cloudfoundry.client;

import org.cloudfoundry.AbstractIntegrationTest;
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
import org.cloudfoundry.utils.JobUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;

import static org.cloudfoundry.utils.tuple.TupleUtils.consumer;
import static org.cloudfoundry.utils.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;

public final class RoutesTest extends AbstractIntegrationTest {

    private Mono<String> domainId;

    @Test
    public void associateApplication() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function(this::createApplicationAndRoute))
            .then(function(this::associateApplicationWithRoute))
            .flatMap(routeId -> this.cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void create() {
        Mono
            .when(this.domainId, this.spaceId)
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
                .assertThat(consumer(this::assertDomainIdAndSpaceId)));
    }

    @Before
    public void createDomain() throws Exception {
        this.domainId = this.organizationId
            .then(organizationId -> this.cloudFoundryClient.domains()
                .create(CreateDomainRequest.builder()
                    .name("test.domain.name")
                    .owningOrganizationId(organizationId)
                    .wildcard(true)
                    .build()))
            .map(ResourceUtils::getId);
    }

    @Test
    public void delete() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build())
                .map(ResourceUtils::getId)))
            .then(routeId -> this.cloudFoundryClient.routes()
                .delete(DeleteRouteRequest.builder()
                    .routeId(routeId)
                    .build()))
            .map(ResourceUtils::getId)
            .flatMap(jobId -> JobUtils.waitForCompletion(this.cloudFoundryClient, jobId))
            .subscribe(testSubscriber());
    }

    @Test
    public void exists() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .spaceId(spaceId)
                    .build())
                .map(response -> domainId)))
            .then(domainId -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(true));
    }

    @Test
    public void existsDoesNotExist() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .spaceId(spaceId)
                    .build())
                .map(response -> domainId)))
            .then(domainId -> this.cloudFoundryClient.routes()
                .exists(RouteExistsRequest.builder()
                    .domainId(domainId)
                    .host("test-host-2")
                    .build()))
            .subscribe(testSubscriber()
                .assertEquals(false));
    }

    @Test
    public void get() {
        Mono
            .when(this.domainId, this.spaceId)
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
                .assertThat(consumer(this::assertDomainIdAndSpaceId)));
    }

    @Test
    public void list() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build())))
            .flatMap(response -> this.cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplications() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function(this::createApplicationAndRoute))
            .then(function(this::associateApplicationWithRoute))
            .flatMap(routeId -> this.cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterByDiego() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function(this::createApplicationAndRoute))
            .then(function(this::associateApplicationWithRoute))
            .flatMap(routeId -> {
                ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                    .diego(true)
                    .routeId(routeId)
                    .build();

                return this.cloudFoundryClient.routes().listApplications(request)
                    .flatMap(ResourceUtils::getResources);
            })
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterByName() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function(this::createApplicationAndRoute))
            .then(function(this::associateApplicationWithRoute))
            .flatMap(routeId -> this.cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .name("test-application-name")
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function(this::createApplicationAndRoute))
            .then(function(this::associateApplicationWithRoute))
            .and(this.organizationId)
            .flatMap(function((routeId, organizationId) -> this.cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .organizationId(organizationId)
                    .build())
                .flatMap(ResourceUtils::getResources)))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listApplicationsFilterBySpaceId() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function(this::createApplicationAndRoute))
            .then(function(this::associateApplicationWithRoute))
            .and(this.spaceId)
            .flatMap(function((routeId, spaceId) -> this.cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .spaceId(spaceId)
                    .build())
                .flatMap(ResourceUtils::getResources)))
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
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build())
                .map(response -> domainId)))
            .flatMap(domainId -> this.cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .domainId(domainId)
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByHost() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .host("test-host")
                    .spaceId(spaceId)
                    .build())))
            .flatMap(response -> this.cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .host("test-host")
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByOrganizationId() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .spaceId(spaceId)
                    .build())
                .then(response -> this.organizationId)))
            .flatMap(organizationId -> this.cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .organizationId(organizationId)
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listFilterByPath() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function((domainId, spaceId) -> this.cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .domainId(domainId)
                    .path("/test-path")
                    .spaceId(spaceId)
                    .build())))
            .flatMap(response -> this.cloudFoundryClient.routes()
                .list(ListRoutesRequest.builder()
                    .path("/test-path")
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void removeApplication() {
        Mono
            .when(this.domainId, this.spaceId)
            .then(function(this::createApplicationAndRoute))
            .then(function((applicationId, routeId) -> associateApplicationWithRoute(applicationId, routeId)
                .map(response -> Tuple.of(applicationId, routeId))))
            .then(function((applicationId, routeId) -> this.cloudFoundryClient.routes()
                .removeApplication(RemoveRouteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .routeId(routeId)
                    .build())
                .map(response -> routeId)))
            .flatMap(routeId -> this.cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .build())
                .flatMap(ResourceUtils::getResources))
            .subscribe(testSubscriber()
                .assertCount(0));
    }

    @Test
    public void update() {
        Mono
            .when(this.domainId, this.spaceId)
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

    private void assertDomainIdAndSpaceId(String domainId, String spaceId, RouteEntity entity) {
        assertEquals(domainId, entity.getDomainId());
        assertEquals(spaceId, entity.getSpaceId());
    }

    private Mono<String> associateApplicationWithRoute(String applicationId, String routeId) {
        return this.cloudFoundryClient.routes()
            .associateApplication(AssociateRouteApplicationRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build())
            .map(response -> routeId);
    }

    private Mono<Tuple2<String, String>> createApplicationAndRoute(String domainId, String spaceId) {
        return Mono
            .when(
                this.cloudFoundryClient.applicationsV2()
                    .create(CreateApplicationRequest.builder()
                        .diego(true)
                        .name("test-application-name")
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getId),
                this.cloudFoundryClient.routes()
                    .create(CreateRouteRequest.builder()
                        .domainId(domainId)
                        .spaceId(spaceId)
                        .build())
                    .map(ResourceUtils::getId)
            );
    }

}
