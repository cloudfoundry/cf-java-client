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
import org.cloudfoundry.operations.util.v2.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;

import static org.cloudfoundry.operations.util.Tuples.consumer;
import static org.cloudfoundry.operations.util.Tuples.function;
import static org.junit.Assert.assertEquals;

public final class RoutesTest extends AbstractIntegrationTest {

    private Mono<String> domainId;

    @Test
    public void associateApplication() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function(this::createApplicationAndRoute))
                .then(function(this::associateApplicationWithRoute))
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .routeId(routeId)
                            .build();

                    return this.cloudFoundryClient.routes().listApplications(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void create() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    Mono<RouteEntity> entity = this.cloudFoundryClient.routes().create(request)
                            .map(Resources::getEntity);

                    return Mono.when(Mono.just(domainId), Mono.just(spaceId), entity);
                }))
                .subscribe(this.<Tuple3<String, String, RouteEntity>>testSubscriber()
                        .assertThat(consumer(this::assertDomainIdAndSpaceId)));
    }

    @Before
    public void createDomain() throws Exception {
        this.domainId = this.organizationId
                .then(organizationId -> {
                    CreateDomainRequest organization = CreateDomainRequest.builder()
                            .domainName("test.domain.name")
                            .owningOrganizationId(organizationId)
                            .wildcard(true)
                            .build();

                    return this.cloudFoundryClient.domains().create(organization);

                })
                .map(Resources::getId);
    }

    @Test
    public void delete() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request)
                            .map(Resources::getId);
                }))
                .then(routeId -> {
                    DeleteRouteRequest request = DeleteRouteRequest.builder()
                            .routeId(routeId)
                            .build();

                    return this.cloudFoundryClient.routes().delete(request);
                })
                .subscribe(testSubscriber());
    }

    @Test
    public void exists() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request)
                            .map(response -> domainId);
                }))
                .then(domainId -> {
                    RouteExistsRequest request = RouteExistsRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .build();

                    return this.cloudFoundryClient.routes().exists(request);
                })
                .subscribe(testSubscriber()
                        .assertEquals(true));
    }

    @Test
    public void existsDoesNotExist() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request)
                            .map(response -> domainId);
                }))
                .then(domainId -> {
                    RouteExistsRequest request = RouteExistsRequest.builder()
                            .domainId(domainId)
                            .host("test-host-2")
                            .build();

                    return this.cloudFoundryClient.routes().exists(request);
                })
                .subscribe(testSubscriber()
                        .assertEquals(false));
    }

    @Test
    public void get() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    Mono<String> routeId = this.cloudFoundryClient.routes().create(request)
                            .map(Resources::getId);

                    return Mono.when(Mono.just(domainId), Mono.just(spaceId), routeId);
                }))
                .then(function((domainId, spaceId, routeId) -> {
                    GetRouteRequest request = GetRouteRequest.builder()
                            .routeId(routeId)
                            .build();

                    Mono<RouteEntity> entity = this.cloudFoundryClient.routes().get(request)
                            .map(Resources::getEntity);

                    return Mono.when(Mono.just(domainId), Mono.just(spaceId), entity);
                }))
                .subscribe(this.<Tuple3<String, String, RouteEntity>>testSubscriber()
                        .assertThat(consumer(this::assertDomainIdAndSpaceId)));
    }

    @Test
    public void list() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request);
                }))
                .flatMap(response -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .build();

                    return this.cloudFoundryClient.routes().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listApplications() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function(this::createApplicationAndRoute))
                .then(function(this::associateApplicationWithRoute))
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .routeId(routeId)
                            .build();

                    return this.cloudFoundryClient.routes().listApplications(request)
                            .flatMap(Resources::getResources);
                })
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
                            .flatMap(Resources::getResources);
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
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .routeId(routeId)
                            .name("test-application-name")
                            .build();

                    return this.cloudFoundryClient.routes().listApplications(request)
                            .flatMap(Resources::getResources);
                })
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
                .flatMap(function((routeId, organizationId) -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .routeId(routeId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.routes().listApplications(request)
                            .flatMap(Resources::getResources);
                }))
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
                .flatMap(function((routeId, spaceId) -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .routeId(routeId)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().listApplications(request)
                            .flatMap(Resources::getResources);
                }))
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
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request)
                            .map(response -> domainId);
                }))
                .flatMap(domainId -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .domainId(domainId)
                            .build();

                    return this.cloudFoundryClient.routes().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByHost() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request);
                }))
                .flatMap(response -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .host("test-host")
                            .build();

                    return this.cloudFoundryClient.routes().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByOrganizationId() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request)
                            .then(response -> this.organizationId);
                }))
                .flatMap(organizationId -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.routes().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByPath() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .path("/test-path")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request);
                }))
                .flatMap(response -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .path("/test-path")
                            .build();

                    return this.cloudFoundryClient.routes().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void removeApplication() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function(this::createApplicationAndRoute))
                .then(function((applicationId, routeId) -> {
                    return associateApplicationWithRoute(applicationId, routeId)
                            .map(response -> Tuple.of(applicationId, routeId));
                }))
                .then(function((applicationId, routeId) -> {
                    RemoveRouteApplicationRequest request = RemoveRouteApplicationRequest.builder()
                            .applicationId(applicationId)
                            .routeId(routeId)
                            .build();

                    return this.cloudFoundryClient.routes().removeApplication(request)
                            .map(response -> routeId);
                }))
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .routeId(routeId)
                            .build();

                    return this.cloudFoundryClient.routes().listApplications(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(0));
    }

    @Test
    public void update() {
        Mono
                .when(this.domainId, this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request)
                            .map(Resources::getId);
                }))
                .then(routeId -> {
                    UpdateRouteRequest request = UpdateRouteRequest.builder()
                            .host("test-host")
                            .routeId(routeId)
                            .build();


                    return this.cloudFoundryClient.routes().update(request)
                            .map(Resources::getEntity);
                })
                .subscribe(this.<RouteEntity>testSubscriber()
                        .assertThat(entity -> assertEquals("test-host", entity.getHost())));
    }

    private void assertDomainIdAndSpaceId(String domainId, String spaceId, RouteEntity entity) {
        assertEquals(domainId, entity.getDomainId());
        assertEquals(spaceId, entity.getSpaceId());
    }

    private Mono<String> associateApplicationWithRoute(String applicationId, String routeId) {
        AssociateRouteApplicationRequest request = AssociateRouteApplicationRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build();

        return this.cloudFoundryClient.routes().associateApplication(request)
                .map(response -> routeId);
    }

    private Mono<Tuple2<String, String>> createApplicationAndRoute(String domainId, String spaceId) {
        CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                .diego(true)
                .name("test-application-name")
                .spaceId(spaceId)
                .build();

        Mono<String> applicationId = this.cloudFoundryClient.applicationsV2().create(createApplicationRequest)
                .map(Resources::getId);

        CreateRouteRequest createRouteRequest = CreateRouteRequest.builder()
                .domainId(domainId)
                .spaceId(spaceId)
                .build();

        Mono<String> routeId = this.cloudFoundryClient.routes().create(createRouteRequest)
                .map(Resources::getId);

        return Mono.when(applicationId, routeId);
    }

}
