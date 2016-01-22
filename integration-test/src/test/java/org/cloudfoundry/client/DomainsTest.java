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
import org.cloudfoundry.operations.util.v2.Resources;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;

import static org.cloudfoundry.operations.util.Tuples.consumer;
import static org.cloudfoundry.operations.util.Tuples.function;
import static org.junit.Assert.assertEquals;

public final class DomainsTest extends AbstractIntegrationTest {

    @Test
    public void create() {
        this.organizationId
                .then(this::createDomainEntity)
                .and(this.organizationId)
                .subscribe(this.<Tuple2<DomainEntity, String>>testSubscriber()
                        .assertThat(consumer(this::assertDomainNameAndOrganizationId)));
    }

    @Test
    public void delete() {
        this.organizationId
                .then(this::createDomainId)
                .then(domainId -> {
                    DeleteDomainRequest request = DeleteDomainRequest.builder()
                            .domainId(domainId)
                            .build();

                    return this.cloudFoundryClient.domains().delete(request);
                })
                .subscribe(testSubscriber());
    }

    @Test
    public void get() {
        this.organizationId
                .then(this::createDomainId)
                .then(domainId -> {
                    GetDomainRequest request = GetDomainRequest.builder()
                            .domainId(domainId)
                            .build();

                    return this.cloudFoundryClient.domains().get(request)
                            .map(Resources::getEntity);
                })
                .and(this.organizationId)
                .subscribe(this.<Tuple2<DomainEntity, String>>testSubscriber()
                        .assertThat(consumer(this::assertDomainNameAndOrganizationId)));
    }

    @Test
    public void list() {
        this.organizationId
                .then(this::createDomain)
                .flatMap(response -> {
                    ListDomainsRequest request = ListDomainsRequest.builder()
                            .build();

                    return this.cloudFoundryClient.domains().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(2));
    }

    @Test
    public void listDomainSpaces() {
        this.organizationId
                .then(this::createDomainId)
                .flatMap(domainId -> {
                    ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                            .domainId(domainId)
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(request)
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
                .then(this::createDomainId)
                .and(this.spaceId)
                .then(function((domainId, spaceId) -> {
                    CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
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

                    return Mono.when(Mono.just(domainId), applicationId, routeId);
                }))
                .then(function((domainId, applicationId, routeId) -> {
                    AssociateRouteApplicationRequest request = AssociateRouteApplicationRequest.builder()
                            .id(routeId)
                            .applicationId(applicationId)
                            .build();

                    return this.cloudFoundryClient.routes().associateApplication(request)
                            .map(response -> Tuple.of(domainId, applicationId));
                }))
                .flatMap(function((domainId, applicationId) -> {
                    ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                            .applicationId(applicationId)
                            .domainId(domainId)
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(request)
                            .flatMap(Resources::getResources);
                }))
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Ignore("TODO: implement once list users available https://www.pivotaltracker.com/story/show/101522708")
    @Test
    public void listDomainSpacesFilterByDeveloperId() {
        Assert.fail();
    }

    @Test
    public void listDomainSpacesFilterByName() {
        this.organizationId
                .then(this::createDomainId)
                .flatMap(domainId -> {
                    ListDomainSpacesRequest request = ListDomainSpacesRequest.builder()
                            .domainId(domainId)
                            .name(this.testSpaceName)
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(request)
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
                .then(this::createDomainId)
                .and(this.organizationId)
                .flatMap(function((domainId, organizationId) -> {
                    ListDomainSpacesRequest response = ListDomainSpacesRequest.builder()
                            .domainId(domainId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(response)
                            .flatMap(Resources::getResources)
                            .map(Resources::getId);
                }))
                .zipWith(this.spaceId)
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByName() {
        this.organizationId
                .then(this::createDomain)
                .flatMap(response -> {
                    ListDomainsRequest request = ListDomainsRequest.builder()
                            .name("test.domain.name")
                            .build();

                    return this.cloudFoundryClient.domains().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listFilterByOwningOrganizationId() {
        this.organizationId
                .then(organizationId -> createDomain(organizationId)
                        .then(response -> this.organizationId))
                .flatMap(organizationId -> {
                    ListDomainsRequest request = ListDomainsRequest.builder()
                            .owningOrganizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.domains().list(request)
                            .flatMap(Resources::getResources);
                })
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    private void assertDomainNameAndOrganizationId(DomainEntity entity, String organizationId) {
        assertEquals("test.domain.name", entity.getName());
        assertEquals(organizationId, entity.getOwningOrganizationId());
    }

    private Mono<CreateDomainResponse> createDomain(String organizationId) {
        CreateDomainRequest request = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build();

        return this.cloudFoundryClient.domains().create(request);
    }

    private Mono<DomainEntity> createDomainEntity(String organizationId) {
        return createDomain(organizationId)
                .map(Resources::getEntity);
    }

    private Mono<String> createDomainId(String organizationId) {
        return createDomain(organizationId)
                .map(Resources::getId);
    }

}
