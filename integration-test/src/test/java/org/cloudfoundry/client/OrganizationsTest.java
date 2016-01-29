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
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationServicesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;

import static org.cloudfoundry.operations.util.Tuples.function;
import static org.junit.Assert.assertEquals;

public final class OrganizationsTest extends AbstractIntegrationTest {

    private Mono<String> organizationId;

    @Test
    public void associateAuditor() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> {
                    AssociateOrganizationAuditorRequest request = AssociateOrganizationAuditorRequest.builder()
                            .auditorId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateAuditor(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                }))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateAuditorByUsername() {
        this.organizationId
                .then(organizationId -> {
                    AssociateOrganizationAuditorByUsernameRequest request = AssociateOrganizationAuditorByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build();

                    return this.cloudFoundryClient.organizations().associateAuditorByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateBillingManager() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> {
                    AssociateOrganizationBillingManagerRequest request = AssociateOrganizationBillingManagerRequest.builder()
                            .billingManagerId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateBillingManager(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                }))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateBillingManagerByUsername() {
        this.organizationId
                .then(organizationId -> {
                    AssociateOrganizationBillingManagerByUsernameRequest request = AssociateOrganizationBillingManagerByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build();

                    return this.cloudFoundryClient.organizations().associateBillingManagerByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManager() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> {
                    AssociateOrganizationManagerRequest request = AssociateOrganizationManagerRequest.builder()
                            .managerId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateManager(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                }))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManagerByUsername() {
        this.organizationId
                .then(organizationId -> {
                    AssociateOrganizationManagerByUsernameRequest request = AssociateOrganizationManagerByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build();

                    return this.cloudFoundryClient.organizations().associateManagerByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associatePrivateDomain() {
        this.organizationId
                .then(organizationId -> getPrivateDomainId(this.cloudFoundryClient, organizationId)
                        .and(Mono.just(organizationId)))
                .then(function((privateDomainId, organizationId) -> {
                    AssociateOrganizationPrivateDomainRequest request = AssociateOrganizationPrivateDomainRequest.builder()
                            .organizationId(organizationId)
                            .privateDomainId(privateDomainId)
                            .build();

                    return this.cloudFoundryClient.organizations().associatePrivateDomain(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                }))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateUser() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> {
                    AssociateOrganizationUserRequest request = AssociateOrganizationUserRequest.builder()
                            .userId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateUser(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                }))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateUserByUsername() {
        this.organizationId
                .then(organizationId -> {
                    AssociateOrganizationUserByUsernameRequest request = AssociateOrganizationUserByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build();

                    return this.cloudFoundryClient.organizations().associateUserByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void create() {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("test-organization")
                .build();

        this.cloudFoundryClient.organizations().create(request)
                .map(Resources::getEntity)
                .subscribe(this.<OrganizationEntity>testSubscriber()
                        .assertThat(entity -> assertEquals("test-organization", entity.getName())));
    }

    @Before
    public void createOrganization() {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("test-organization")
                .build();

        this.organizationId = this.cloudFoundryClient.organizations().create(request)
                .map(Resources::getId);
    }

    @Test
    public void delete() {
        this.organizationId
                .then(organizationId -> {
                    DeleteOrganizationRequest request = DeleteOrganizationRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().delete(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void get() {
        this.organizationId
                .then(organizationId -> {
                    GetOrganizationRequest request = GetOrganizationRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().get(request)
                            .map(Resources::getId)
                            .and(Mono.just(organizationId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void getInstanceUsage() {
        this.organizationId
                .then(organizationId -> {
                    GetOrganizationInstanceUsageRequest request = GetOrganizationInstanceUsageRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().getInstanceUsage(request);
                })
                .map(GetOrganizationInstanceUsageResponse::getInstanceUsage)
                .subscribe(this.testSubscriber()
                        .assertEquals(0));
    }

    @Test
    public void getMemoryUsage() {
        this.organizationId
                .then(organizationId -> {
                    GetOrganizationMemoryUsageRequest request = GetOrganizationMemoryUsageRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().getMemoryUsage(request);
                })
                .map(GetOrganizationMemoryUsageResponse::getMemoryUsageInMb)
                .subscribe(this.testSubscriber()
                        .assertEquals(0));
    }

    @Test
    public void getUserRoles() {
        this.organizationId
                .flatMap(organizationId -> Paginated
                        .requestResources(page -> {
                            GetOrganizationUserRolesRequest request = GetOrganizationUserRolesRequest.builder()
                                    .organizationId(organizationId)
                                    .page(page)
                                    .build();

                            return this.cloudFoundryClient.organizations().getUserRoles(request);
                        }))
                .subscribe(this.testSubscriber()
                        .assertCount(0));
    }

    @Test
    public void list() {
        this.organizationId
                .flatMap(organizationId -> Paginated
                        .requestResources(page -> {
                            ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                                    .page(page)
                                    .build();

                            return this.cloudFoundryClient.organizations().list(request);
                        })
                        .map(Resources::getId)
                        .filter(id -> id.equals(organizationId)))
                .subscribe(this.testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listAuditors() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateAuditor(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationAuditorsRequest request = ListOrganizationAuditorsRequest.builder()
                                    .page(page)
                                    .organizationId(organizationId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listAuditors(request);
                        })
                        .single()
                        .map(Resources::getId)
                        .and(Mono.just(userId))))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listBillingManagers() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateBillingManager(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationBillingManagersRequest request = ListOrganizationBillingManagersRequest.builder()
                                    .page(page)
                                    .organizationId(organizationId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listBillingManagers(request);
                        })
                        .single()
                        .map(Resources::getId)
                        .and(Mono.just(userId))))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listManagers() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateManager(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationManagersRequest request = ListOrganizationManagersRequest.builder()
                                    .page(page)
                                    .organizationId(organizationId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listManagers(request);
                        })
                        .single()
                        .map(Resources::getId)
                        .and(Mono.just(userId))))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listPrivateDomain() {
        Mono
                .when(super.organizationId, this.organizationId)
                .then(tuple -> {
                    String defaultOrganizationId = tuple.t1;
                    String organizationId = tuple.t2;

                    return associatePrivateDomain(this.cloudFoundryClient, defaultOrganizationId, organizationId);
                })
                .then(function((organizationId, privateDomainId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                                    .page(page)
                                    .organizationId(organizationId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listPrivateDomains(request);
                        })
                        .single()
                        .map(Resources::getId)
                        .and(Mono.just(privateDomainId))
                ))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Ignore("TODO: implement once create service plan visibility available https://www.pivotaltracker.com/story/show/101451560")
    @Test
    public void listServices() {
        this.organizationId
                .flatMap(organizationId -> Paginated
                        .requestResources(page -> {
                            ListOrganizationServicesRequest request = ListOrganizationServicesRequest.builder()
                                    .organizationId(organizationId)
                                    .page(page)
                                    .build();

                            return this.cloudFoundryClient.organizations().listServices(request);
                        })
                )
                .subscribe(this.testSubscriber()
                        .assertCount(0));
    }

    @Test
    public void listSpaceQuotaDefinitions() {
        this.organizationId
                .flatMap(organizationId -> Paginated
                        .requestResources(page -> {
                            ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                                    .organizationId(organizationId)
                                    .page(page)
                                    .build();

                            return this.cloudFoundryClient.organizations().listSpaceQuotaDefinitions(request);
                        })
                )
                .subscribe(this.testSubscriber()
                        .assertCount(0));
    }

    @Test
    public void listSpaces() {
        this.organizationId
                .then(organizationId -> {
                    CreateSpaceRequest request = CreateSpaceRequest.builder()
                            .name("test-space")
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.spaces().create(request)
                            .map(r -> organizationId);
                })
                .flatMap(organizationId -> Paginated
                        .requestResources(page -> {
                            ListOrganizationSpacesRequest request = ListOrganizationSpacesRequest.builder()
                                    .organizationId(organizationId)
                                    .page(page)
                                    .build();

                            return this.cloudFoundryClient.organizations().listSpaces(request);
                        })

                )
                .subscribe(this.testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void listUsers() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateUser(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationUsersRequest request = ListOrganizationUsersRequest.builder()
                                    .page(page)
                                    .organizationId(organizationId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listUsers(request);
                        })
                        .single()
                        .map(Resources::getId)
                        .and(Mono.just(userId))))
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void removeAuditor() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateAuditor(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationAuditorRequest request = RemoveOrganizationAuditorRequest.builder()
                            .auditorId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditor(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeAuditorByUsername() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateAuditor(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationAuditorByUsernameRequest request = RemoveOrganizationAuditorByUsernameRequest.builder()
                            .username(this.userName)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditorByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeBillingManager() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateBillingManager(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationBillingManagerRequest request = RemoveOrganizationBillingManagerRequest.builder()
                            .billingManagerId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeBillingManager(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeBillingManagerByUsername() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateBillingManager(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationBillingManagerByUsernameRequest request = RemoveOrganizationBillingManagerByUsernameRequest.builder()
                            .username(this.userName)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeBillingManagerByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeManager() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateManager(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationManagerRequest request = RemoveOrganizationManagerRequest.builder()
                            .managerId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeManager(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeManagerByUsername() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateManager(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationManagerByUsernameRequest request = RemoveOrganizationManagerByUsernameRequest.builder()
                            .username(this.userName)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeManagerByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removePrivateDomain() {
        Mono
                .when(super.organizationId, this.organizationId)
                .then(tuple -> {
                    String defaultOrganizationId = tuple.t1;
                    String organizationId = tuple.t2;

                    return associatePrivateDomain(this.cloudFoundryClient, defaultOrganizationId, organizationId);
                })
                .then(function((organizationId, privateDomainId) -> {
                    RemoveOrganizationPrivateDomainRequest request = RemoveOrganizationPrivateDomainRequest.builder()
                            .privateDomainId(privateDomainId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removePrivateDomain(request);
                }))
                .subscribe(this.testSubscriber());
    }

    @Test
    public void removeUser() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateUser(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationUserRequest request = RemoveOrganizationUserRequest.builder()
                            .userId(userId)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeUser(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeUserByUsername() {
        Mono
                .when(this.organizationId, this.userId)
                .then(function((organizationId, userId) -> associateUser(this.cloudFoundryClient, organizationId, userId)))
                .then(function((organizationId, userId) -> {
                    RemoveOrganizationUserByUsernameRequest request = RemoveOrganizationUserByUsernameRequest.builder()
                            .username(this.userName)
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeUserByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void summary() {
        this.organizationId
                .then(organizationId -> {
                    SummaryOrganizationRequest request = SummaryOrganizationRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return this.cloudFoundryClient.organizations().summary(request)
                            .map(SummaryOrganizationResponse::getName);
                })
                .subscribe(this.testSubscriber()
                        .assertEquals("test-organization"));
    }

    @Test
    public void update() {
        this.organizationId
                .then(organizationId -> {
                    UpdateOrganizationRequest request = UpdateOrganizationRequest.builder()
                            .organizationId(organizationId)
                            .name("new-test-organization")
                            .build();

                    return this.cloudFoundryClient.organizations().update(request)
                            .map(Resources::getEntity)
                            .map(OrganizationEntity::getName);
                })
                .subscribe(this.testSubscriber()
                        .assertEquals("new-test-organization"));
    }

    private static Mono<Tuple2<String, String>> associateAuditor(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        AssociateOrganizationAuditorRequest request = AssociateOrganizationAuditorRequest.builder()
                .auditorId(userId)
                .organizationId(organizationId)
                .build();

        return cloudFoundryClient.organizations().associateAuditor(request)
                .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associateBillingManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        AssociateOrganizationBillingManagerRequest request = AssociateOrganizationBillingManagerRequest.builder()
                .billingManagerId(userId)
                .organizationId(organizationId)
                .build();

        return cloudFoundryClient.organizations().associateBillingManager(request)
                .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associateManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        AssociateOrganizationManagerRequest request = AssociateOrganizationManagerRequest.builder()
                .managerId(userId)
                .organizationId(organizationId)
                .build();

        return cloudFoundryClient.organizations().associateManager(request)
                .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associatePrivateDomain(CloudFoundryClient cloudFoundryClient, String defaultOrganizationId, String organizationId) {
        return getPrivateDomainId(cloudFoundryClient, defaultOrganizationId)
                .then(privateDomainId -> {
                    AssociateOrganizationPrivateDomainRequest request = AssociateOrganizationPrivateDomainRequest.builder()
                            .organizationId(organizationId)
                            .privateDomainId(privateDomainId)
                            .build();

                    return cloudFoundryClient.organizations().associatePrivateDomain(request)
                            .map(response -> Tuple2.of(organizationId, privateDomainId));
                });
    }

    private static Mono<Tuple2<String, String>> associateUser(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        AssociateOrganizationUserRequest request = AssociateOrganizationUserRequest.builder()
                .userId(userId)
                .organizationId(organizationId)
                .build();

        return cloudFoundryClient.organizations().associateUser(request)
                .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<String> getPrivateDomainId(CloudFoundryClient cloudFoundryClient, String organizationId) {
        CreateDomainRequest request = CreateDomainRequest.builder()
                .domainName("test.private.domain")
                .owningOrganizationId(organizationId)
                .wildcard(false)
                .build();

        return cloudFoundryClient.domains().create(request)
                .map(Resources::getId);
    }

}
