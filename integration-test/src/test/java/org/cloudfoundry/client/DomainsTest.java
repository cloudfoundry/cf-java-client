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
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainResponse;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ClientConfiguration.class)
public final class DomainsTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private String organizationId;

    @Autowired
    private String spaceId;

    @Value("${test.space}")
    private String spaceName;

    @Test
    public void create() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        this.cloudFoundryClient.domains().create(createDomainRequest)
                .subscribe(new TestSubscriber<CreateDomainResponse>()
                        .assertThat(response -> {
                            DomainEntity entity = response.getEntity();

                            assertEquals("test.domain.name", entity.getName());
                            assertEquals(this.organizationId, entity.getOwningOrganizationId());
                        }));
    }

    @Test
    public void delete() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    DeleteDomainRequest deleteDomainRequest = DeleteDomainRequest.builder()
                            .id(response.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.domains().delete(deleteDomainRequest);
                })
                .subscribe(new TestSubscriber<>());
    }

    @Test
    public void get() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    GetDomainRequest getDomainRequest = GetDomainRequest.builder()
                            .id(response.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.domains().get(getDomainRequest);
                })
                .subscribe(new TestSubscriber<GetDomainResponse>()
                        .assertThat(response -> {
                            DomainEntity entity = response.getEntity();

                            assertEquals("test.domain.name", entity.getName());
                            assertEquals(this.organizationId, entity.getOwningOrganizationId());
                        }));
    }

    @Test
    public void list() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    ListDomainsRequest listDomainsRequest = ListDomainsRequest.builder()
                            .build();

                    return this.cloudFoundryClient.domains().list(listDomainsRequest);
                })
                .flatMap(Resources.extractResources())
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(2L));
    }

    @Test
    public void listDomainSpaces() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    ListDomainSpacesRequest listDomainSpacesRequest = ListDomainSpacesRequest.builder()
                            .id(response.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(listDomainSpacesRequest);
                })
                .flatMap(Resources.extractResources())
                .map(resource -> resource.getMetadata().getId())
                .subscribe(new TestSubscriber<>()
                        .assertEquals(this.spaceId));
    }

    @Test
    public void listDomainSpacesFilterByApplicationId() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        CreateApplicationRequest createApplicationRequest = CreateApplicationRequest.builder()
                .name("test-application-name")
                .spaceId(this.spaceId)
                .build();

        Streams
                .zip(this.cloudFoundryClient.domains().create(createDomainRequest), this.cloudFoundryClient.applicationsV2().create(createApplicationRequest))
                .flatMap(tuple -> {
                    CreateRouteRequest createRouteRequest = CreateRouteRequest.builder()
                            .domainId(tuple.getT1().getMetadata().getId())
                            .spaceId(this.spaceId)
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().create(createRouteRequest))
                            .map(response -> Tuple.of(tuple.getT1(), tuple.getT2(), response));
                })
                .flatMap(tuple -> {
                    AssociateRouteApplicationRequest associateRouteApplicationRequest = AssociateRouteApplicationRequest.builder()
                            .id(tuple.getT3().getMetadata().getId())
                            .applicationId(tuple.getT2().getMetadata().getId())
                            .build();

                    return Streams
                            .wrap(this.cloudFoundryClient.routes().associateApplication(associateRouteApplicationRequest))
                            .map(response -> Tuple.of(tuple.getT1(), tuple.getT2()));
                })
                .flatMap((Tuple2<CreateDomainResponse, CreateApplicationResponse> tuple) -> {
                    ListDomainSpacesRequest listDomainSpacesRequest = ListDomainSpacesRequest.builder()
                            .applicationId(tuple.getT2().getMetadata().getId())
                            .id(tuple.getT1().getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(listDomainSpacesRequest);
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
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    ListDomainSpacesRequest listDomainSpacesRequest = ListDomainSpacesRequest.builder()
                            .id(response.getMetadata().getId())
                            .name("test.domain.name")
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(listDomainSpacesRequest);
                })
                .flatMap(Resources.extractResources())
                .subscribe(new TestSubscriber<>()
                        .assertEquals(this.spaceId));
    }

    @Test
    public void listDomainSpacesFilterByOrganizationId() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    ListDomainSpacesRequest listDomainSpacesRequest = ListDomainSpacesRequest.builder()
                            .id(response.getMetadata().getId())
                            .organizationId(this.organizationId)
                            .build();

                    return this.cloudFoundryClient.domains().listSpaces(listDomainSpacesRequest);
                })
                .flatMap(Resources.extractResources())
                .map(resource -> resource.getMetadata().getId())
                .subscribe(new TestSubscriber<>()
                        .assertEquals(this.spaceId));
    }

    @Test
    public void listFilterByName() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    ListDomainsRequest listDomainsRequest = ListDomainsRequest.builder()
                            .name("test.domain.name")
                            .build();

                    return this.cloudFoundryClient.domains().list(listDomainsRequest);
                })
                .flatMap(Resources.extractResources())
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(1L));
    }

    @Test
    public void listFilterByOwningOrganizationId() {
        CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .name("test.domain.name")
                .owningOrganizationId(this.organizationId)
                .wildcard(true)
                .build();

        Streams
                .wrap(this.cloudFoundryClient.domains().create(createDomainRequest))
                .flatMap(response -> {
                    ListDomainsRequest listDomainsRequest = ListDomainsRequest.builder()
                            .owningOrganizationId(this.organizationId)
                            .build();

                    return this.cloudFoundryClient.domains().list(listDomainsRequest);
                })
                .flatMap(Resources.extractResources())
                .count()
                .subscribe(new TestSubscriber<>()
                        .assertEquals(1L));
    }

    @After
    public void tearDown() throws Exception {
        Paginated
                .requestResources(page -> {
                    ListApplicationsRequest listApplicationsRequest = ListApplicationsRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.applicationsV2().list(listApplicationsRequest);
                })
                .flatMap(resource -> {
                    DeleteApplicationRequest deleteApplicationRequest = DeleteApplicationRequest.builder()
                            .id(resource.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.applicationsV2().delete(deleteApplicationRequest);
                })
                .subscribe(new TestSubscriber<>());


        Paginated
                .requestResources(page -> {
                    ListRoutesRequest listRoutesRequest = ListRoutesRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.routes().list(listRoutesRequest);
                })
                .flatMap(resource -> {
                    DeleteRouteRequest deleteRouteRequest = DeleteRouteRequest.builder()
                            .id(resource.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.routes().delete(deleteRouteRequest);
                })
                .subscribe(new TestSubscriber<>());

        Paginated
                .requestResources(page -> {
                    ListDomainsRequest listDomainsRequest = ListDomainsRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.domains().list(listDomainsRequest);
                })
                .filter(resource -> !resource.getEntity().getName().equals("local.micropcf.io"))
                .flatMap(resource -> {
                    DeleteDomainRequest deleteDomainRequest = DeleteDomainRequest.builder()
                            .id(resource.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.domains().delete(deleteDomainRequest);
                })
                .subscribe(new TestSubscriber<>());
    }

}
