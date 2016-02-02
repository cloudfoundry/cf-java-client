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
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                    .auditorId(userId)
                    .organizationId(organizationId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateAuditorByUsername() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .associateAuditorByUsername(AssociateOrganizationAuditorByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(this.userName)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId)))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateBillingManager() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                    .billingManagerId(userId)
                    .organizationId(organizationId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateBillingManagerByUsername() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .associateBillingManagerByUsername(AssociateOrganizationBillingManagerByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(this.userName)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId)))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManager() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .associateManager(AssociateOrganizationManagerRequest.builder()
                    .managerId(userId)
                    .organizationId(organizationId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManagerByUsername() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(this.userName)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId)))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associatePrivateDomain() {
        this.organizationId
            .then(organizationId -> getPrivateDomainId(this.cloudFoundryClient, organizationId)
                .and(Mono.just(organizationId)))
            .then(function((privateDomainId, organizationId) -> this.cloudFoundryClient.organizations()
                .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                    .organizationId(organizationId)
                    .privateDomainId(privateDomainId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateUser() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .associateUser(AssociateOrganizationUserRequest.builder()
                    .userId(userId)
                    .organizationId(organizationId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateUserByUsername() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(this.userName)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId)))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void create() {
        this.cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name("test-organization")
                .build())
            .map(Resources::getEntity)
            .subscribe(this.<OrganizationEntity>testSubscriber()
                .assertThat(entity -> assertEquals("test-organization", entity.getName())));
    }

    @Before
    public void createOrganization() {
        this.organizationId = this.cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name("test-organization")
                .build())
            .map(Resources::getId);
    }

    @Test
    public void delete() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .delete(DeleteOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void get() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .get(GetOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(organizationId)))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void getInstanceUsage() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .getInstanceUsage(GetOrganizationInstanceUsageRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .map(GetOrganizationInstanceUsageResponse::getInstanceUsage)
            .subscribe(this.testSubscriber()
                .assertEquals(0));
    }

    @Test
    public void getMemoryUsage() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .getMemoryUsage(GetOrganizationMemoryUsageRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .map(GetOrganizationMemoryUsageResponse::getMemoryUsageInMb)
            .subscribe(this.testSubscriber()
                .assertEquals(0));
    }

    @Test
    public void getUserRoles() {
        this.organizationId
            .flatMap(organizationId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .getUserRoles(GetOrganizationUserRolesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .subscribe(this.testSubscriber()
                .assertCount(0));
    }

    @Test
    public void list() {
        this.organizationId
            .flatMap(organizationId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .list(ListOrganizationsRequest.builder()
                        .page(page)
                        .build()))
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
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listAuditors(ListOrganizationAuditorsRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .build()))
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
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listBillingManagers(ListOrganizationBillingManagersRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .build()))
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
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listManagers(ListOrganizationManagersRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .build()))
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
            .then(function((defaultOrganizationId, organizationId) -> associatePrivateDomain(this.cloudFoundryClient, defaultOrganizationId, organizationId)))
            .then(function((organizationId, privateDomainId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .build()))
                .single()
                .map(Resources::getId)
                .and(Mono.just(privateDomainId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Ignore("TODO: implement once create service plan visibility available https://www.pivotaltracker.com/story/show/101451560")
    @Test
    public void listServices() {
        this.organizationId
            .flatMap(organizationId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listServices(ListOrganizationServicesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .subscribe(this.testSubscriber()
                .assertCount(0));
    }

    @Test
    public void listSpaceQuotaDefinitions() {
        this.organizationId
            .flatMap(organizationId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .subscribe(this.testSubscriber()
                .assertCount(0));
    }

    @Test
    public void listSpaces() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .name("test-space")
                    .organizationId(organizationId)
                    .build())
                .map(r -> organizationId))
            .flatMap(organizationId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listSpaces(ListOrganizationSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .subscribe(this.testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listUsers() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateUser(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listUsers(ListOrganizationUsersRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .build()))
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
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeAuditor(RemoveOrganizationAuditorRequest.builder()
                    .auditorId(userId)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void removeAuditorByUsername() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeAuditorByUsername(RemoveOrganizationAuditorByUsernameRequest.builder()
                    .username(this.userName)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void removeBillingManager() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateBillingManager(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeBillingManager(RemoveOrganizationBillingManagerRequest.builder()
                    .billingManagerId(userId)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void removeBillingManagerByUsername() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateBillingManager(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeBillingManagerByUsername(RemoveOrganizationBillingManagerByUsernameRequest.builder()
                    .username(this.userName)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void removeManager() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateManager(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeManager(RemoveOrganizationManagerRequest.builder()
                    .managerId(userId)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void removeManagerByUsername() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateManager(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeManagerByUsername(RemoveOrganizationManagerByUsernameRequest.builder()
                    .username(this.userName)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void removePrivateDomain() {
        Mono
            .when(super.organizationId, this.organizationId)
            .then(function((defaultOrganizationId, organizationId) -> associatePrivateDomain(this.cloudFoundryClient, defaultOrganizationId, organizationId)))
            .then(function((organizationId, privateDomainId) -> this.cloudFoundryClient.organizations()
                .removePrivateDomain(RemoveOrganizationPrivateDomainRequest.builder()
                    .privateDomainId(privateDomainId)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeUser() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateUser(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeUser(RemoveOrganizationUserRequest.builder()
                    .userId(userId)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void removeUserByUsername() {
        Mono
            .when(this.organizationId, this.userId)
            .then(function((organizationId, userId) -> associateUser(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeUserByUsername(RemoveOrganizationUserByUsernameRequest.builder()
                    .username(this.userName)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(testSubscriber());
    }

    @Test
    public void summary() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .summary(SummaryOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .build())
                .map(SummaryOrganizationResponse::getName))
            .subscribe(this.testSubscriber()
                .assertEquals("test-organization"));
    }

    @Test
    public void update() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .update(UpdateOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .name("new-test-organization")
                    .build())
                .map(Resources::getEntity)
                .map(OrganizationEntity::getName))
            .subscribe(this.testSubscriber()
                .assertEquals("new-test-organization"));
    }

    private static Mono<Tuple2<String, String>> associateAuditor(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                .auditorId(userId)
                .organizationId(organizationId)
                .build())
            .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associateBillingManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                .billingManagerId(userId)
                .organizationId(organizationId)
                .build())
            .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associateManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateManager(AssociateOrganizationManagerRequest.builder()
                .managerId(userId)
                .organizationId(organizationId)
                .build())
            .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associatePrivateDomain(CloudFoundryClient cloudFoundryClient, String defaultOrganizationId, String organizationId) {
        return getPrivateDomainId(cloudFoundryClient, defaultOrganizationId)
            .then(privateDomainId -> cloudFoundryClient.organizations()
                .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                    .organizationId(organizationId)
                    .privateDomainId(privateDomainId)
                    .build())
                .map(response -> Tuple2.of(organizationId, privateDomainId)));
    }

    private static Mono<Tuple2<String, String>> associateUser(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateUser(AssociateOrganizationUserRequest.builder()
                .userId(userId)
                .organizationId(organizationId)
                .build())
            .map(ignore -> Tuple.of(organizationId, userId));
    }

    private static Mono<String> getPrivateDomainId(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.domains()
            .create(CreateDomainRequest.builder()
                .name("test.private.domain")
                .owningOrganizationId(organizationId)
                .wildcard(false)
                .build())
            .map(Resources::getId);
    }

}
