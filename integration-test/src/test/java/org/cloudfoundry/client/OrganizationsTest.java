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
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationUserRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.junit.Test;
import reactor.Mono;
import reactor.fn.tuple.Tuple2;

import static org.junit.Assert.assertTrue;

public final class OrganizationsTest extends AbstractIntegrationTest {

    @Test
    public void auditor() {
        getAdminId()
                .and(this.organizationId)
                .then(tuple -> {
                    AssociateOrganizationAuditorRequest request = AssociateOrganizationAuditorRequest.builder()
                            .auditorId(tuple.t1)
                            .organizationId(tuple.t2)
                            .build();

                    return Mono.just(tuple.t1).and(this.cloudFoundryClient.organizations().associateAuditor(request));
                })
                .doOnSuccess(tuple -> {
                    assertTrue("admin is not an auditor", Paginated
                            .requestResources(page -> {
                                ListOrganizationAuditorsRequest request = ListOrganizationAuditorsRequest.builder()
                                        .page(page)
                                        .id(Resources.getId(tuple.t2))
                                        .build();
                                return this.cloudFoundryClient.organizations().listAuditors(request);
                            })
                            .exists(auditor -> Resources.getId(auditor).equals(tuple.t1))
                            .get());
                })
                .then(tuple -> {
                    RemoveOrganizationAuditorRequest request = RemoveOrganizationAuditorRequest.builder()
                            .auditorId(tuple.t1)
                            .id(Resources.getId(tuple.t2))
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditor(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void auditorByUsername() {
        this.organizationId
                .then(orgId -> {
                    AssociateOrganizationAuditorByUsernameRequest request = AssociateOrganizationAuditorByUsernameRequest.builder()
                            .username("admin")
                            .id(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateAuditorByUsername(request);
                })
                .then(response -> {
                    RemoveOrganizationAuditorByUsernameRequest request = RemoveOrganizationAuditorByUsernameRequest.builder()
                            .username("admin")
                            .id(response.getMetadata().getId())
                            .build();

                    return this.cloudFoundryClient.organizations().removeAuditorByUsername(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void billingManager() {
        getAdminId()
                .and(this.organizationId)
                .then(tuple -> {
                    AssociateOrganizationBillingManagerRequest request = AssociateOrganizationBillingManagerRequest.builder()
                            .billingManagerId(tuple.t1)
                            .id(tuple.t2)
                            .build();

                    return Mono.just(tuple.t1).and(this.cloudFoundryClient.organizations().associateBillingManager(request));
                })
                .doOnSuccess(tuple -> {
                    assertTrue("admin is not a billing manager", Paginated
                            .requestResources(page -> {
                                ListOrganizationBillingManagersRequest request = ListOrganizationBillingManagersRequest.builder()
                                        .page(page)
                                        .id(Resources.getId(tuple.t2))
                                        .build();
                                return this.cloudFoundryClient.organizations().listBillingManagers(request);
                            })
                            .exists(billingManager -> Resources.getId(billingManager).equals(tuple.t1))
                            .get());
                })
                .then(tuple -> {
                    RemoveOrganizationBillingManagerRequest request = RemoveOrganizationBillingManagerRequest.builder()
                            .billingManagerId(tuple.t1)
                            .id(Resources.getId(tuple.t2))
                            .build();

                    return this.cloudFoundryClient.organizations().removeBillingManager(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void billingManagerByUsername() {
        this.organizationId
                .then(orgId -> {
                    AssociateOrganizationBillingManagerByUsernameRequest request = AssociateOrganizationBillingManagerByUsernameRequest.builder()
                            .username("admin")
                            .id(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateBillingManagerByUsername(request)
                            .and(Mono.just(orgId));
                })
                .then(tuple -> {
                    RemoveOrganizationBillingManagerByUsernameRequest request = RemoveOrganizationBillingManagerByUsernameRequest.builder()
                            .username("admin")
                            .id(tuple.t2)
                            .build();

                    return this.cloudFoundryClient.organizations().removeBillingManagerByUsername(request);
                })
                .subscribe(this.testSubscriber());
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
                            .id(orgId)
                            .build();

                    return Mono.just(orgId).and(this.cloudFoundryClient.organizations().get(request)
                            .map(Resources::getId));
                })
                .subscribe(this.<Tuple2<String, String>>testSubscriber().assertThat(tuple -> assertTupleEquality(tuple)));
    }

    @Test
    public void manager() {
        getAdminId()
                .and(this.organizationId)
                .then(tuple -> {
                    AssociateOrganizationManagerRequest request = AssociateOrganizationManagerRequest.builder()
                            .managerId(tuple.t1)
                            .id(tuple.t2)
                            .build();

                    return Mono.just(tuple.t1).and(this.cloudFoundryClient.organizations().associateManager(request));
                })
                .doOnSuccess(tuple -> {
                    assertTrue("admin is not a manager", Paginated
                            .requestResources(page -> {
                                ListOrganizationManagersRequest request = ListOrganizationManagersRequest.builder()
                                        .page(page)
                                        .id(Resources.getId(tuple.t2))
                                        .build();
                                return this.cloudFoundryClient.organizations().listManagers(request);
                            })
                            .exists(manager -> Resources.getId(manager).equals(tuple.t1))
                            .get());
                })
                .then(tuple -> {
                    RemoveOrganizationManagerRequest request = RemoveOrganizationManagerRequest.builder()
                            .managerId(tuple.t1)
                            .id(Resources.getId(tuple.t2))
                            .build();

                    return this.cloudFoundryClient.organizations().removeManager(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void managerByUsername() {
        this.organizationId
                .then(orgId -> {
                    AssociateOrganizationManagerByUsernameRequest request = AssociateOrganizationManagerByUsernameRequest.builder()
                            .username("admin")
                            .id(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateManagerByUsername(request)
                            .and(Mono.just(orgId));
                })
                .then(tuple -> {
                    RemoveOrganizationManagerByUsernameRequest request = RemoveOrganizationManagerByUsernameRequest.builder()
                            .username("admin")
                            .id(tuple.t2)
                            .build();

                    return this.cloudFoundryClient.organizations().removeManagerByUsername(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void privateDomain() {
        this.organizationId
                .then(orgId -> {
                    CreateDomainRequest request = CreateDomainRequest.builder()
                            .name("test.private.domain")
                            .owningOrganizationId(orgId)
                            .wildcard(false)
                            .build();
                    return this.organizationId.and(this.cloudFoundryClient.domains().create(request));
                })
                .then(tuple -> {
                    CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                            .name("test-org")
                            .build();

                    return this.cloudFoundryClient.organizations().create(request)
                            .and(Mono.just(tuple.t2));
                })
                .then(tuple -> {
                    String privateDomainId = Resources.getId(tuple.t2);
                    String testOrgId = Resources.getId(tuple.t1);
                    AssociateOrganizationPrivateDomainRequest request = AssociateOrganizationPrivateDomainRequest.builder()
                            .id(testOrgId)
                            .privateDomainId(privateDomainId)
                            .build();
                    return Mono.just(privateDomainId).and(this.cloudFoundryClient.organizations().associatePrivateDomain(request));
                })
                .doOnSuccess(tuple -> {
                    String privateDomainId = tuple.t1;
                    String testOrgId = Resources.getId(tuple.t2);
                    assertTrue("test.private.domain is not associated", Paginated
                            .requestResources(page -> {
                                ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                                        .page(page)
                                        .id(testOrgId)
                                        .build();
                                return this.cloudFoundryClient.organizations().listPrivateDomains(request);
                            })
                            .exists(privateDomain -> Resources.getId(privateDomain).equals(privateDomainId))
                            .get());
                })
                .then(tuple -> {
                    String privateDomainId = tuple.t1;
                    String testOrgId = Resources.getId(tuple.t2);
                    RemoveOrganizationPrivateDomainRequest request = RemoveOrganizationPrivateDomainRequest.builder()
                            .privateDomainId(privateDomainId)
                            .id(testOrgId)
                            .build();

                    return this.cloudFoundryClient.organizations().removePrivateDomain(request);
                })
                .subscribe(this.testSubscriber());

    }

    @Test
    public void user() {
        getAdminId()
                .and(this.organizationId)
                .then(tuple -> {
                    AssociateOrganizationUserRequest request = AssociateOrganizationUserRequest.builder()
                            .userId(tuple.t1)
                            .id(tuple.t2)
                            .build();

                    return Mono.just(tuple.t1).and(this.cloudFoundryClient.organizations().associateUser(request));
                })
                .doOnSuccess(tuple -> {
                    assertTrue("admin is not an associated user", Paginated
                            .requestResources(page -> {
                                ListOrganizationUsersRequest request = ListOrganizationUsersRequest.builder()
                                        .page(page)
                                        .id(Resources.getId(tuple.t2))
                                        .build();
                                return this.cloudFoundryClient.organizations().listUsers(request);
                            })
                            .exists(user -> Resources.getId(user).equals(tuple.t1))
                            .get());
                })
                .then(tuple -> {
                    RemoveOrganizationUserRequest request = RemoveOrganizationUserRequest.builder()
                            .userId(tuple.t1)
                            .id(Resources.getId(tuple.t2))
                            .build();

                    return this.cloudFoundryClient.organizations().removeUser(request);
                })
                .subscribe(this.testSubscriber());
    }

    @Test
    public void userByUsername() {
        this.organizationId
                .then(orgId -> {
                    AssociateOrganizationUserByUsernameRequest request = AssociateOrganizationUserByUsernameRequest.builder()
                            .username("admin")
                            .id(orgId)
                            .build();

                    return this.cloudFoundryClient.organizations().associateUserByUsername(request)
                            .and(Mono.just(orgId));
                })
                .then(tuple -> {
                    RemoveOrganizationUserByUsernameRequest request = RemoveOrganizationUserByUsernameRequest.builder()
                            .username("admin")
                            .id(tuple.t2)
                            .build();

                    return this.cloudFoundryClient.organizations().removeUserByUsername(request);
                })
                .subscribe(this.testSubscriber());
    }


    private Mono<String> getAdminId() {
        return Paginated
                .requestResources(page -> {
                    ListUsersRequest request = ListUsersRequest.builder()
                            .page(page)
                            .build();

                    return this.cloudFoundryClient.users().listUsers(request);
                })
                .filter(userResource -> Resources.getEntity(userResource).getUsername().equals("admin"))
                .single()
                .map(Resources::getId);
    }

}
