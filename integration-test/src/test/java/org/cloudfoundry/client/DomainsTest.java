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
import org.cloudfoundry.client.v2.domains.CreateDomainResponse;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.operations.v2.Resources;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;

public final class DomainsTest extends AbstractIntegrationTest {

    @Test
    public void create() {
        this.organizationId
                .flatMap(this::createDomainEntity)
                .zipWith(this.organizationId)
                .subscribe(this.<Tuple2<DomainEntity, String>>testSubscriber()
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
                .subscribe(testSubscriber());
    }

    @Test
    public void get() {
        this.organizationId
                .flatMap(this::createDomainId)
                .flatMap(domainId -> {
                    GetDomainRequest request = GetDomainRequest.builder()
                            .id(domainId)
                            .build();

                    return Streams
                            .from(this.cloudFoundryClient.domains().get(request))
                            .map(Resources::getEntity);
                })
                .zipWith(this.organizationId)
                .subscribe(this.<Tuple2<DomainEntity, String>>testSubscriber()
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
                            .from(this.cloudFoundryClient.domains().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
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

                    return Streams
                            .from(this.cloudFoundryClient.domains().listSpaces(request))
                            .flatMap(Resources::getResources)
                            .map(Resources::getId);
                })
                .zipWith(this.spaceId)
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listDomainSpacesFilterByApplicationId() {
        this.organizationId
                .flatMap(this::createDomainId)
                .zipWith(this.spaceId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String spaceId = tuple.t2;

                    CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                            .name("test-application-name")
                            .spaceId(spaceId)
                            .build();

                    Stream<String> applicationId = Streams
                            .from(this.cloudFoundryClient.applicationsV2().create(createApplicationRequest))
                            .map(Resources::getId);

                    CreateRouteRequest createRouteRequest = CreateRouteRequest.builder()
                            .domainId(domainId)
                            .spaceId(spaceId)
                            .build();

                    Stream<String> routeId = Streams
                            .from(this.cloudFoundryClient.routes().create(createRouteRequest))
                            .map(Resources::getId);

                    return Streams.zip(Streams.just(domainId), applicationId, routeId);
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
                            .from(this.cloudFoundryClient.routes().associateApplication(request))
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
                            .from(this.cloudFoundryClient.domains().listSpaces(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
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
                            .name(this.spaceName)
                            .build();

                    return Streams
                            .from(this.cloudFoundryClient.domains().listSpaces(request))
                            .flatMap(Resources::getResources)
                            .map(Resources::getId);
                })
                .zipWith(this.spaceId)
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listDomainSpacesFilterByOrganizationId() {
        this.organizationId
                .flatMap(this::createDomainId)
                .zipWith(this.organizationId)
                .flatMap(tuple -> {
                    String domainId = tuple.t1;
                    String organizationId = tuple.t2;

                    ListDomainSpacesRequest response = ListDomainSpacesRequest.builder()
                            .id(domainId)
                            .organizationId(organizationId)
                            .build();

                    return Streams
                            .from(this.cloudFoundryClient.domains().listSpaces(response))
                            .flatMap(Resources::getResources)
                            .map(Resources::getId);
                })
                .zipWith(this.spaceId)
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
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
                            .from(this.cloudFoundryClient.domains().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
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
                            .from(this.cloudFoundryClient.domains().list(request))
                            .flatMap(Resources::getResources);
                })
                .count()
                .subscribe(testSubscriber()
                        .assertEquals(1L));
    }

    private void assertDomainNameAndOrganizationId(Tuple2<DomainEntity, String> tuple) {
        DomainEntity entity = tuple.t1;
        String organizationId = tuple.t2;

        assertEquals("test.domain.name", entity.getName());
        assertEquals(organizationId, entity.getOwningOrganizationId());
    }

    private Stream<CreateDomainResponse> createDomain(String organizationId) {
        CreateDomainRequest request = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build();

        return Streams
                .from(this.cloudFoundryClient.domains().create(request));
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
