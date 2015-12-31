/*
 * Copyright 2013-2015 the original author or authors.
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
import org.cloudfoundry.operations.v2.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;
import reactor.rx.Stream;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;

public final class RoutesTest extends AbstractClientIntegrationTest {

    private Stream<String> domainId;

    @Test
    public void associateApplication() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(this::createApplicationAndRoute)
                .flatMap(this::associateApplicationWithRoute)
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .id(routeId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().listApplications(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void create() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    Stream<RouteEntity> entity = Streams
                            .wrap(this.cloudFoundryClient.routes().create(request))
                            .map(Resources::getEntity);

                    return Streams.zip(this.domainId, this.spaceId, entity);
                })
                .subscribe(this.<Tuple3<String, String, RouteEntity>>testSubscriber()
                        .assertThat(this::assertDomainIdAndSpaceId));
    }

    @Before
    public void createDomain() throws Exception {
        this.domainId = this.organizationId
                .flatMap(organizationId -> {
                    CreateDomainRequest organization = CreateDomainRequest.builder()
                            .name("test.domain.name")
                            .owningOrganizationId(organizationId)
                            .wildcard(true)
                            .build();

                    return this.cloudFoundryClient.domains().create(organization);

                })
                .map(response -> response.getMetadata().getId())
                .take(1)
                .cache(1);
    }

    @Test
    public void delete() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().create(request))
                            .map(Resources::getId);
                })
                .flatMap(routeId -> {
                    DeleteRouteRequest request = DeleteRouteRequest.builder()
                            .id(routeId)
                            .build();

                    return this.cloudFoundryClient.routes().delete(request);
                })
                .subscribe(testSubscriber());
    }

    @Test
    public void exists() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .spaceId(spaceId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().create(request))
                            .flatMap(response -> this.domainId);
                })
                .flatMap(domainId -> {
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
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .spaceId(spaceId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().create(request))
                            .flatMap(response -> this.domainId);
                })
                .flatMap(domainId -> {
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
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return Streams.wrap(this.cloudFoundryClient.routes().create(request))
                            .map(Resources::getId);
                })
                .flatMap(routeId -> {
                    GetRouteRequest request = GetRouteRequest.builder()
                            .id(routeId)
                            .build();

                    Stream<RouteEntity> entity = Streams
                            .wrap(this.cloudFoundryClient.routes().get(request))
                            .map(Resources::getEntity);

                    return Streams.zip(this.domainId, this.spaceId, entity);
                })
                .subscribe(this.<Tuple3<String, String, RouteEntity>>testSubscriber()
                        .assertThat(this::assertDomainIdAndSpaceId));
    }

    @Test
    public void list() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request);
                })
                .flatMap(response -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listApplications() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(this::createApplicationAndRoute)
                .flatMap(this::associateApplicationWithRoute)
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .id(routeId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().listApplications(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listApplicationsFilterByDiego() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(this::createApplicationAndRoute)
                .flatMap(this::associateApplicationWithRoute)
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .diego(true)
                            .id(routeId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().listApplications(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listApplicationsFilterByName() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(this::createApplicationAndRoute)
                .flatMap(this::associateApplicationWithRoute)
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .id(routeId)
                            .name("test-application-name")
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().listApplications(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(this::createApplicationAndRoute)
                .flatMap(this::associateApplicationWithRoute)
                .zipWith(this.organizationId)
                .flatMap(tuple -> {
                    String routeId = tuple.t1;
                    String organizationId = tuple.t2;

                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .id(routeId)
                            .organizationId(organizationId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().listApplications(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listApplicationsFilterBySpaceId() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(this::createApplicationAndRoute)
                .flatMap(this::associateApplicationWithRoute)
                .zipWith(this.spaceId)
                .flatMap(tuple -> {
                    String routeId = tuple.t1;
                    String spaceId = tuple.t2;

                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .id(routeId)
                            .spaceId(spaceId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().listApplications(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Ignore("TODO: implement once list stacks available")
    @Test
    public void listApplicationsFilterByStackId() {
        Assert.fail();
    }

    @Test
    public void listFilterByDomainId() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().create(request))
                            .flatMap(response -> this.domainId);
                })
                .flatMap(domainId -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .domainId(domainId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listFilterByHost() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .host("test-host")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request);
                })
                .flatMap(response -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .host("test-host")
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listFilterByOrganizationId() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().create(request))
                            .flatMap(response -> this.organizationId);
                })
                .flatMap(organizationId -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void listFilterByPath() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .path("/test-path")
                            .spaceId(spaceId)
                            .build();

                    return this.cloudFoundryClient.routes().create(request);
                })
                .flatMap(response -> {
                    ListRoutesRequest request = ListRoutesRequest.builder()
                            .path("/test-path")
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    @Test
    public void removeApplication() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(this::createApplicationAndRoute)
                .flatMap(tuple -> associateApplicationWithRoute(tuple)
                        .map(response -> tuple))
                .flatMap(tuple -> {
                    String applicationId = tuple.t1;
                    String routeId = tuple.t2;

                    RemoveRouteApplicationRequest request = RemoveRouteApplicationRequest.builder()
                            .applicationId(applicationId)
                            .id(routeId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().removeApplication(request))
                            .map(response -> routeId);
                })
                .flatMap(routeId -> {
                    ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                            .id(routeId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().listApplications(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(0L));
    }

    @Test
    public void update() {
        Streams
                .zip(this.domainId, this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateRouteRequest request = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    return Streams.wrap(this.cloudFoundryClient.routes().create(request))
                            .map(Resources::getId);
                })
                .flatMap(routeId -> {
                    UpdateRouteRequest request = UpdateRouteRequest.builder()
                            .host("test-host")
                            .id(routeId)
                            .build();


                    return Streams
                            .wrap(this.cloudFoundryClient.routes().update(request))
                            .map(Resources::getEntity);
                })
                .subscribe(this.<RouteEntity>testSubscriber()
                        .assertThat(entity -> assertEquals("test-host", entity.getHost())));
    }

    private void assertDomainIdAndSpaceId(Tuple3<String, String, RouteEntity> tuple) {
        String domainId = tuple.t1;
        String spaceId = tuple.t2;
        RouteEntity entity = tuple.t3;

        assertEquals(domainId, entity.getDomainId());
        assertEquals(spaceId, entity.getSpaceId());
    }

    private Stream<String> associateApplicationWithRoute(Tuple2<String, String> tuple) {
        String applicationId = tuple.t1;
        String routeId = tuple.t2;

        AssociateRouteApplicationRequest request = AssociateRouteApplicationRequest.builder()
                .applicationId(applicationId)
                .id(routeId)
                .build();

        return Streams
                .wrap(this.cloudFoundryClient.routes().associateApplication(request))
                .map(response -> routeId);
    }

    private Stream<Tuple2<String, String>> createApplicationAndRoute(Tuple2<String, String> tuple) {
        String domainId = tuple.t1;
        String spaceId = tuple.t2;

        CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                .diego(true)
                .name("test-application-name")
                .spaceId(spaceId)
                .build();

        Stream<String> applicationId = Streams
                .wrap(this.cloudFoundryClient.applicationsV2().create(createApplicationRequest))
                .map(response -> response.getMetadata().getId());

        CreateRouteRequest createRouteRequest = CreateRouteRequest.builder()
                .domainId(domainId)
                .spaceId(spaceId)
                .build();

        Stream<String> routeId = Streams
                .wrap(this.cloudFoundryClient.routes().create(createRouteRequest))
                .map(resource -> resource.getMetadata().getId());

        return Streams.zip(applicationId, routeId);
    }

}
