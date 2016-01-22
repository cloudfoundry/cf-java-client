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
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;

import static org.cloudfoundry.operations.util.Tuples.function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class OrganizationsTest extends AbstractIntegrationTest {

    private String orgTestUsername;

    private Mono<String> testOrganizationId;

    @Test
    public void associateAuditor() {
        associateAuditorRequest(this.cloudFoundryClient, this.testOrganizationId)
                .as(OrganizationsTest::checkOrgIds)
                .subscribe(this.testSubscriber());
    }

    @Test
    public void associateAuditorByUsername() {
        this.testOrganizationId
                .then(orgId -> {
                    AssociateOrganizationAuditorByUsernameRequest request = AssociateOrganizationAuditorByUsernameRequest.builder()
                            .organizationId(orgId)
                            .username(this.orgTestUsername)
                            .build();

                    return this.cloudFoundryClient.organizations().associateAuditorByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(orgId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateBillingManager() {
        associateBillingManagerRequest(this.cloudFoundryClient, this.testOrganizationId)
                .as(OrganizationsTest::checkOrgIds)
                .subscribe(this.testSubscriber());
    }

    @Test
    public void associateBillingManagerByUsername() {
        this.testOrganizationId
                .then(orgId -> {
                    AssociateOrganizationBillingManagerByUsernameRequest request = AssociateOrganizationBillingManagerByUsernameRequest.builder()
                            .organizationId(orgId)
                            .username(this.orgTestUsername)
                            .build();

                    return this.cloudFoundryClient.organizations().associateBillingManagerByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(orgId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManager() {
        associateManagerRequest(this.cloudFoundryClient, this.testOrganizationId)
                .as(OrganizationsTest::checkOrgIds)
                .subscribe(this.testSubscriber());
    }

    @Test
    public void associateManagerByUsername() {
        this.testOrganizationId
                .then(orgId -> {
                    AssociateOrganizationManagerByUsernameRequest request = AssociateOrganizationManagerByUsernameRequest.builder()
                            .organizationId(orgId)
                            .username(this.orgTestUsername)
                            .build();

                    return this.cloudFoundryClient.organizations().associateManagerByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(orgId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associatePrivateDomain() {
        associatePrivateDomain(this.cloudFoundryClient, this.organizationId, this.testOrganizationId)
                .after()
                .subscribe(this.testSubscriber());
    }

    @Test
    public void associateUser() {
        associateUserRequest(this.cloudFoundryClient, this.testOrganizationId)
                .as(OrganizationsTest::checkOrgIds)
                .subscribe(this.testSubscriber());
    }

    @Test
    public void associateUserByUsername() {
        this.testOrganizationId
                .then(orgId -> {
                    AssociateOrganizationUserByUsernameRequest request = AssociateOrganizationUserByUsernameRequest.builder()
                            .organizationId(orgId)
                            .username(this.orgTestUsername)
                            .build();

                    return this.cloudFoundryClient.organizations().associateUserByUsername(request)
                            .map(Resources::getId)
                            .and(Mono.just(orgId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber()
                        .assertThat(this::assertTupleEquality));
    }

    @Test
    public void create() {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("test-org")
                .build();

        this.cloudFoundryClient.organizations().create(request)
                .map(response -> Resources.getEntity(response).getName())
                .subscribe(this.testSubscriber()
                        .assertEquals("test-org"));
    }

    @Test
    public void get() {
        this.organizationId
                .then(orgId -> {
                    GetOrganizationRequest request = GetOrganizationRequest.builder()
                            .organizationId(orgId)
                            .build();

                    return Mono.just(orgId).and(this.cloudFoundryClient.organizations().get(request)
                            .map(Resources::getId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber().assertThat(this::assertTupleEquality));
    }

    @Test
    public void getInstanceUsage() {
        this.organizationId
                .then(orgId -> {
                    GetOrganizationInstanceUsageRequest request = GetOrganizationInstanceUsageRequest.builder()
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().getInstanceUsage(request);
                })
                .subscribe(this.<GetOrganizationInstanceUsageResponse>testSubscriber().assertThat(response -> assertNotNull(response.getInstanceUsage())));
    }

    @Test
    public void getMemoryUsage() {
        this.organizationId
                .then(orgId -> {
                    GetOrganizationMemoryUsageRequest request = GetOrganizationMemoryUsageRequest.builder()
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().getMemoryUsage(request);
                })
                .subscribe(this.<GetOrganizationMemoryUsageResponse>testSubscriber().assertThat(response -> assertNotNull(response.getMemoryUsageInMb())));
    }

    @Test
    public void getUserRoles() {
        this.organizationId
                .flatMap(orgId ->
                        Paginated
                                .requestResources(page -> {
                                    GetOrganizationUserRolesRequest request = GetOrganizationUserRolesRequest.builder()
                                            .organizationId(orgId)
                                            .page(page)
                                            .build();

                                    return this.cloudFoundryClient.organizations().getUserRoles(request);
                                }))
                .subscribe(this.testSubscriber());
    }

    @Test
    public void list() {
        Paginated
                .requestResources(page -> {
                    ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.organizations().list(request);
                })
                .after()
                .subscribe(this.testSubscriber());
    }

    @Test
    public void listAuditors() {
        associateAuditor(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationAuditorsRequest request = ListOrganizationAuditorsRequest.builder()
                                    .page(page)
                                    .organizationId(orgId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listAuditors(request);
                        })
                        .map(Resources::getId)
                        .exists(userId::equals)))
                .subscribe(testSubscriber().assertEquals(true));
    }

    @Test
    public void listBillingManagers() {
        associateBillingManager(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationBillingManagersRequest request = ListOrganizationBillingManagersRequest.builder()
                                    .page(page)
                                    .organizationId(orgId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listBillingManagers(request);
                        })
                        .map(Resources::getId)
                        .exists(userId::equals)))
                .subscribe(testSubscriber().assertEquals(true));
    }

    @Test
    public void listManagers() {
        associateManager(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationManagersRequest request = ListOrganizationManagersRequest.builder()
                                    .page(page)
                                    .organizationId(orgId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listManagers(request);
                        })
                        .map(Resources::getId)
                        .exists(userId::equals)))
                .subscribe(testSubscriber().assertEquals(true));
    }

    @Test
    public void listPrivateDomain() {
        associatePrivateDomain(this.cloudFoundryClient, this.organizationId, this.testOrganizationId)
                .then(function((testOrgId, privateDomainId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                                    .page(page)
                                    .organizationId(testOrgId)
                                    .build();
                            
                            return this.cloudFoundryClient.organizations().listPrivateDomains(request);
                        })
                        .map(Resources::getId)
                        .exists(privateDomainId::equals)
                ))
                .subscribe(this.testSubscriber().assertEquals(true));
    }

    @Test
    public void listServices() {
        this.organizationId
                .flatMap(orgId ->
                        Paginated
                                .requestResources(page -> {
                                    ListOrganizationServicesRequest request = ListOrganizationServicesRequest.builder()
                                            .organizationId(orgId)
                                            .page(page)
                                            .build();

                                    return this.cloudFoundryClient.organizations().listServices(request);
                                })
                )
                .subscribe(this.testSubscriber());
    }

    @Test
    public void listSpaceQuotaDefinitions() {
        this.organizationId
                .flatMap(orgId ->
                        Paginated
                                .requestResources(page -> {
                                    ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                                            .organizationId(orgId)
                                            .page(page)
                                            .build();

                                    return this.cloudFoundryClient.organizations().listSpaceQuotaDefinitions(request);
                                })
                )
                .subscribe(this.testSubscriber());
    }

    @Test
    public void listSpaces() {
        this.organizationId
                .flatMap(orgId ->
                        Paginated
                                .requestResources(page -> {
                                    ListOrganizationSpacesRequest request = ListOrganizationSpacesRequest.builder()
                                            .organizationId(orgId)
                                            .page(page)
                                            .build();

                                    return this.cloudFoundryClient.organizations().listSpaces(request);
                                })
                )
                .map(resource -> Resources.getEntity(resource).getName())
                .subscribe(this.testSubscriber().assertEquals("integration-test"));
    }

    @Test
    public void listUsers() {
        associateUser(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> Paginated
                        .requestResources(page -> {
                            ListOrganizationUsersRequest request = ListOrganizationUsersRequest.builder()
                                    .page(page)
                                    .organizationId(orgId)
                                    .build();

                            return this.cloudFoundryClient.organizations().listUsers(request);
                        })
                        .map(Resources::getId)
                        .exists(userId::equals)))
                .subscribe(testSubscriber().assertEquals(true));
    }

    @Test
    public void removeAuditor() {
        associateAuditor(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationAuditorRequest request = RemoveOrganizationAuditorRequest.builder()
                            .auditorId(userId)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditor(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeAuditorByUsername() {
        associateAuditor(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationAuditorByUsernameRequest request = RemoveOrganizationAuditorByUsernameRequest.builder()
                            .username(this.orgTestUsername)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditorByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeBillingManager() {
        associateBillingManager(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationBillingManagerRequest request = RemoveOrganizationBillingManagerRequest.builder()
                            .billingManagerId(userId)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeBillingManager(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeBillingManagerByUsername() {
        associateBillingManager(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationBillingManagerByUsernameRequest request = RemoveOrganizationBillingManagerByUsernameRequest.builder()
                            .username(this.orgTestUsername)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeBillingManagerByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeManager() {
        associateManager(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationManagerRequest request = RemoveOrganizationManagerRequest.builder()
                            .managerId(userId)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeManager(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeManagerByUsername() {
        associateManager(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationManagerByUsernameRequest request = RemoveOrganizationManagerByUsernameRequest.builder()
                            .username(this.orgTestUsername)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeManagerByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removePrivateDomain() {
        associatePrivateDomain(this.cloudFoundryClient, this.organizationId, this.testOrganizationId)
                .then(function((testOrgId, privateDomainId) -> {
                    RemoveOrganizationPrivateDomainRequest request = RemoveOrganizationPrivateDomainRequest.builder()
                            .privateDomainId(privateDomainId)
                            .organizationId(testOrgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removePrivateDomain(request);
                }))
                .subscribe(this.testSubscriber());
    }

    @Test
    public void removeUser() {
        associateUser(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationUserRequest request = RemoveOrganizationUserRequest.builder()
                            .userId(userId)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeUser(request);
                }))
                .subscribe(testSubscriber());
    }

    @Test
    public void removeUserByUsername() {
        associateUser(this.cloudFoundryClient, this.testOrganizationId)
                .then(function((orgId, userId) -> {
                    RemoveOrganizationUserByUsernameRequest request = RemoveOrganizationUserByUsernameRequest.builder()
                            .username(this.orgTestUsername)
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removeUserByUsername(request);
                }))
                .subscribe(testSubscriber());
    }

    @Before
    public void setup() {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("test-org")
                .build();

        this.testOrganizationId = this.cloudFoundryClient.organizations().create(request)
                .map(Resources::getId);

        this.orgTestUsername = "admin";
    }

    @Test
    public void summary() {
        this.organizationId
                .then(orgId -> {
                    SummaryOrganizationRequest request = SummaryOrganizationRequest.builder()
                            .organizationId(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().summary(request)
                            .map(SummaryOrganizationResponse::getName);
                })
                .subscribe(this.testSubscriber().assertEquals("integration-test"));
    }

    @Test
    public void update() {
        this.organizationId
                .then(orgId -> {
                    UpdateOrganizationRequest request = UpdateOrganizationRequest.builder()
                            .organizationId(orgId)
                            .build();

                    return Mono.just(orgId).and(this.cloudFoundryClient.organizations().update(request)
                            .map(Resources::getId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber().assertThat(tuple -> assertEquals(tuple.t1, tuple.t2)));
    }

    private static Mono<Tuple2<String, String>> associateAuditor(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return associateAuditorRequest(cloudFoundryClient, testOrganizationId).as(OrganizationsTest::orgIdUserID);
    }

    private static Mono<Tuple3<String, String, String>> associateAuditorRequest(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return getTestUserId(cloudFoundryClient)
                .and(testOrganizationId)
                .then(function((String userId, String orgId) -> {
                    AssociateOrganizationAuditorRequest request = AssociateOrganizationAuditorRequest.builder()
                            .auditorId(userId)
                            .organizationId(orgId)
                            .build();

                    return Mono.when(Mono.just(orgId), cloudFoundryClient.organizations().associateAuditor(request).map(Resources::getId), Mono.just(userId));
                }));
    }

    private static Mono<Tuple2<String, String>> associateBillingManager(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return associateBillingManagerRequest(cloudFoundryClient, testOrganizationId).as(OrganizationsTest::orgIdUserID);
    }

    private static Mono<Tuple3<String, String, String>> associateBillingManagerRequest(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return getTestUserId(cloudFoundryClient)
                .and(testOrganizationId)
                .then(function((String userId, String orgId) -> {
                    AssociateOrganizationBillingManagerRequest request = AssociateOrganizationBillingManagerRequest.builder()
                            .billingManagerId(userId)
                            .organizationId(orgId)
                            .build();

                    return Mono.when(Mono.just(orgId), cloudFoundryClient.organizations().associateBillingManager(request).map(Resources::getId), Mono.just(userId));
                }));
    }

    private static Mono<Tuple2<String, String>> associateManager(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return associateManagerRequest(cloudFoundryClient, testOrganizationId).as(OrganizationsTest::orgIdUserID);
    }

    private static Mono<Tuple3<String, String, String>> associateManagerRequest(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return getTestUserId(cloudFoundryClient)
                .and(testOrganizationId)
                .then(function((String userId, String orgId) -> {
                    AssociateOrganizationManagerRequest request = AssociateOrganizationManagerRequest.builder()
                            .managerId(userId)
                            .organizationId(orgId)
                            .build();

                    return Mono.when(Mono.just(orgId), cloudFoundryClient.organizations().associateManager(request).map(Resources::getId), Mono.just(userId));
                }));
    }

    private static Mono<Tuple2<String, String>> associateUser(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return associateUserRequest(cloudFoundryClient, testOrganizationId).as(OrganizationsTest::orgIdUserID);
    }

    private static Mono<Tuple3<String, String, String>> associateUserRequest(CloudFoundryClient cloudFoundryClient, Mono<String> testOrganizationId) {
        return getTestUserId(cloudFoundryClient)
                .and(testOrganizationId)
                .then(function((String userId, String orgId) -> {
                    AssociateOrganizationUserRequest request = AssociateOrganizationUserRequest.builder()
                            .userId(userId)
                            .organizationId(orgId)
                            .build();

                    return Mono.when(Mono.just(orgId), cloudFoundryClient.organizations().associateUser(request).map(Resources::getId), Mono.just(userId));
                }));
    }

    private static Mono<Void> checkOrgIds(Mono<Tuple3<String, String, String>> tuple3Mono) {
        return tuple3Mono
                .then(function((orgId, resultantOrgId, userId) -> {
                    if (!orgId.equals(resultantOrgId)) {
                        return Mono.error(new AssertionError("association failed: input and output organization ids are not equal"));
                    } else {
                        return Mono.empty();
                    }
                }));
    }

    private static Mono<String> getTestUserId(CloudFoundryClient cloudFoundryClient) {
        return Paginated
                .requestResources(page -> {
                    ListUsersRequest request = ListUsersRequest.builder()
                            .page(page)
                            .build();

                    return cloudFoundryClient.users().listUsers(request);
                })
                .filter(userResource -> Resources.getEntity(userResource).getUsername().equals("admin"))
                .single()
                .map(Resources::getId);
    }

    private static Mono<Tuple2<String, String>> orgIdUserID(Mono<Tuple3<String, String, String>> tuple3Mono) {
        return tuple3Mono.map(function((String orgId, String resultantOrgId, String userId) -> Tuple.of(orgId, userId)));
    }

    private Mono<Tuple2<String, String>> associatePrivateDomain(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, Mono<String>
            testOrganizationId) {
        return testOrganizationId
                .and(getTestPrivateDomainId(organizationId))
                .then(function((testOrgId, privateDomainId) -> {
                    AssociateOrganizationPrivateDomainRequest request = AssociateOrganizationPrivateDomainRequest.builder()
                            .organizationId(testOrgId)
                            .privateDomainId(privateDomainId)
                            .build();

                    return cloudFoundryClient.organizations().associatePrivateDomain(request)
                            .then(response -> Mono.when(Mono.<String>just(testOrgId), Mono.<String>just(privateDomainId)));
                }));
    }

    private Mono<String> getTestPrivateDomainId(Mono<String> organizationId) {
        return organizationId
                .then(orgId -> {
                    CreateDomainRequest request = CreateDomainRequest.builder()
                            .name("test.private.domain")
                            .owningOrganizationId(orgId)
                            .wildcard(false)
                            .build();
                    return this.cloudFoundryClient.domains().create(request)
                            .map(Resources::getId);
                });
    }

}
