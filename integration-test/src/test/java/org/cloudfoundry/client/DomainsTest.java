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
import org.cloudfoundry.client.v2.domains.CreateDomainResponse;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.operations.v2.Resources;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;

public final class DomainsTest extends AbstractClientIntegrationTest {

    @Test
    public void create() {
        this.organizationId
                .flatMap(organizationId -> Streams.zip(this.organizationId, createDomainEntity(organizationId)))
                .subscribe(new TestSubscriber<Tuple2<String, DomainEntity>>()
                        .assertThat(this::assertDomainNameAndOrganizationId));
    }

    @Test
    public void delete() {
        this.organizationId
                .flatMap(this::createDomainId)
                .flatMap(domainId -> {
                    DeleteDomainRequest request = DeleteDomainRequest.builder()
                            .id(domainId)
                            .build();

                    return this.cloudFoundryClient.domains().delete(request);
                })
                .subscribe(new TestSubscriber<>());
    }

    @Test
    public void get() {
        this.organizationId
                .flatMap(this::createDomainId)
                .flatMap(domainId -> {
                    GetDomainRequest request = GetDomainRequest.builder()
                            .id(domainId)
                            .build();

                    Stream<DomainEntity> domainEntity = Streams
                            .wrap(this.cloudFoundryClient.domains().get(request))
                            .map(Resources::getEntity);


                    return Streams.zip(this.organizationId, domainEntity);
                })
                .subscribe(new TestSubscriber<Tuple2<String, DomainEntity>>()
                        .assertThat(this::assertDomainNameAndOrganizationId));
    }

    @Test
    public void list() {
        this.organizationId
                .flatMap(this::createDomain)
                .flatMap(response -> {
                    ListDomainsRequest request = ListDomainsRequest.builder()
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.domains().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(2L));
    }

    @Test
    public void listDomainSpaces() {
        this.organizationId
                .flatMap(this::createDomainId)
                .flatMap(domainId -> {
                    ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                            .id(domainId)
                            .build();

                    Stream<String> actual = Streams
                            .wrap(this.cloudFoundryClient.domains().listSpaces(request))
                            .flatMap(Resources::getResources)
                            .map(Resources::getId);

                    return Streams.zip(this.spaceId, actual);
                })
                .subscribe(new TestSubscriber<Tuple2<String, String>>()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listDomainSpacesFilterByApplicationId() {
        this.organizationId
                .flatMap(organizationId -> Streams.zip(this.spaceId, createDomainId(organizationId)))
                .flatMap(tuple -> {
                    String spaceId = tuple.t1;
                    String domainId = tuple.t2;

                    CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                            .name("test-application-name")
                            .spaceId(spaceId)
                            .build();

                    Stream<String> applicationId = Streams
                            .wrap(this.cloudFoundryClient.applicationsV2().create(createApplicationRequest))
                            .map(Resources::getId);

                    CreateRouteRequest createRouteRequest = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    Stream<String> routeId = Streams
                            .wrap(this.cloudFoundryClient.routes().create(createRouteRequest))
                            .map(Resources::getId);

                    return Streams.zip(Streams.just(domainId), applicationId, routeId, t -> t);
                })
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String applicationId = tuple.t2;
                    String routeId = tuple.t3;

                    AssociateRouteApplicationRequest request = AssociateRouteApplicationRequest.builder()
                            .id(routeId)
                            .applicationId(applicationId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().associateApplication(request))
                            .map(response -> Tuple.of(domainId, applicationId));
                })
                .flatMap((Tuple2<String, String> tuple) -> {
                    String domainId = tuple.t1;
                    String applicationId = tuple.t2;

                    ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                            .applicationId(applicationId)
                            .id(domainId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.domains().listSpaces(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(1L));
    }

    @Ignore("TODO: implement once list users available")
    @Test
    public void listDomainSpacesFilterByDeveloperId() {
        Assert.fail();
    }

    @Test
    public void listDomainSpacesFilterByName() {
        this.organizationId
                .flatMap(this::createDomainId)
                .flatMap(domainId -> {
                    ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                            .id(domainId)
                            .name("test.domain.name")
                            .build();

                    Stream<String> actual = Streams
                            .wrap(this.cloudFoundryClient.domains().listSpaces(request))
                            .flatMap(Resources::getResources)
                            .map(Resources::getId);

                    return Streams.zip(this.spaceId, actual);
                })
                .subscribe(new TestSubscriber<Tuple2<String, String>>()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listDomainSpacesFilterByOrganizationId() {
        this.organizationId
                .flatMap(organizationId -> Streams.zip(this.organizationId, createDomainId(organizationId)))
                .flatMap(tuple -> {
                    String organizationId = tuple.t1;
                    String domainId = tuple.t2;

                    ListDomainSpacesRequest response = ListDomainSpacesRequest.builder()
                            .id(domainId)
                            .organizationId(organizationId)
                            .build();

                    Stream<String> actual = Streams
                            .wrap(this.cloudFoundryClient.domains().listSpaces(response))
                            .flatMap(Resources::getResources)
                            .map(Resources::getId);

                    return Streams.zip(this.organizationId, actual);
                })
                .subscribe(new TestSubscriber<Tuple2<String, String>>()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByName() {
        this.organizationId
                .flatMap(this::createDomain)
                .flatMap(response -> {
                    ListDomainsRequest request = ListDomainsRequest.builder()
                            .name("test.domain.name")
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.domains().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(1L));
    }

    @Test
    public void listFilterByOwningOrganizationId() {
        this.organizationId
                .flatMap(organizationId -> createDomain(organizationId)
                        .flatMap(response -> this.organizationId))
                .flatMap(organizationId -> {
                    ListDomainsRequest request = ListDomainsRequest.builder()
                            .owningOrganizationId(organizationId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.domains().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(1L));
    }

    private void assertDomainNameAndOrganizationId(Tuple2<String, DomainEntity> tuple) {
        String organizationId = tuple.t1;
        DomainEntity entity = tuple.t2;

        assertEquals("test.domain.name", entity.getName());
        assertEquals(organizationId, entity.getOwningOrganizationId());
    }

    private void assertTupleEquality(Tuple2<String, String> tuple) {
        String expected = tuple.t1;
        String actual = tuple.t2;

        assertEquals(expected, actual);
    }

    private Stream<CreateDomainResponse> createDomain(String organizationId) {
        CreateDomainRequest request = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build();

        return Streams.wrap(this.cloudFoundryClient.domains().create(request));
    }

    private Stream<DomainEntity> createDomainEntity(String organizationId) {
        return createDomain(organizationId)
                .map(Resources::getEntity);
    }

    private Stream<String> createDomainId(String organizationId) {
        return createDomain(organizationId)
                .map(response -> response.getMetadata().getId());
    }

}
