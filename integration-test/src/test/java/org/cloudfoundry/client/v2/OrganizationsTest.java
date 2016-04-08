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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsRequest;
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
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;

import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;
import static reactor.core.publisher.Mono.when;

public final class OrganizationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> userId;

    @Autowired
    private String userName;

    @Test
    public void associateAuditor() {
        String organizationName = getOrganizationName();

        when(
            createOrganizationId(this.cloudFoundryClient, organizationName),
            this.userId
        )
            .then(function((organizationId, userId) ->
                when(
                    this.cloudFoundryClient.organizations()
                        .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                            .auditorId(userId)
                            .organizationId(organizationId)
                            .build())
                        .map(ResourceUtils::getId),
                    Mono.just(organizationId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateAuditorByUsername() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associateAuditorByUsername(AssociateOrganizationAuditorByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build())
                        .map(ResourceUtils::getId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateBillingManager() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .then(function((organizationId, userId) -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                            .billingManagerId(userId)
                            .organizationId(organizationId)
                            .build())
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateBillingManagerByUsername() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associateBillingManagerByUsername(AssociateOrganizationBillingManagerByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build())
                        .map(ResourceUtils::getId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManager() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .then(function((organizationId, userId) -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associateManager(AssociateOrganizationManagerRequest.builder()
                            .managerId(userId)
                            .organizationId(organizationId)
                            .build())
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManagerByUsername() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build())
                        .map(ResourceUtils::getId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associatePrivateDomain() {
        String domainName = getDomainName();
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    getPrivateDomainId(this.cloudFoundryClient, organizationId, domainName),
                    Mono.just(organizationId)
                ))
            .then(function((privateDomainId, organizationId) -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                            .organizationId(organizationId)
                            .privateDomainId(privateDomainId)
                            .build())
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateUser() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .then(function((organizationId, userId) -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associateUser(AssociateOrganizationUserRequest.builder()
                            .userId(userId)
                            .organizationId(organizationId)
                            .build())
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateUserByUsername() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                            .organizationId(organizationId)
                            .username(this.userName)
                            .build())
                        .map(ResourceUtils::getId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void create() {
        String organizationName = getOrganizationName();

        this.cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build())
            .map(ResourceUtils::getEntity)
            .subscribe(this.<OrganizationEntity>testSubscriber()
                .assertThat(entity -> assertEquals(organizationName, entity.getName())));
    }

    @Test
    public void delete() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .as(thenKeep(organizationId -> this.cloudFoundryClient.organizations()
                .delete(DeleteOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .async(true)
                    .build())
                .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, job))))
            .then(organizationId -> requestGetOrganization(this.cloudFoundryClient, organizationId))
            .subscribe(testSubscriber()
                .assertErrorMatch(CloudFoundryException.class, "CF-OrganizationNotFound\\([0-9]+\\): The organization could not be found: .*"));
    }

    @Test
    public void get() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    this.cloudFoundryClient.organizations()
                        .get(GetOrganizationRequest.builder()
                            .organizationId(organizationId)
                            .build())
                        .map(ResourceUtils::getId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void getInstanceUsage() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
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
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
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
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> PaginationUtils
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
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .list(ListOrganizationsRequest.builder()
                        .page(page)
                        .build()))
                .map(ResourceUtils::getId)
                .filter(id -> id.equals(organizationId)))
            .subscribe(this.testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listAuditors() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .then(function((organizationId, userId) -> associateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> Mono
                .when(
                    Mono.just(userId),
                    PaginationUtils.requestResources(page -> this.cloudFoundryClient.organizations()
                        .listAuditors(ListOrganizationAuditorsRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listBillingManagers() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .then(function((organizationId, userId) -> associateBillingManager(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> Mono
                .when(
                    Mono.just(userId),
                    PaginationUtils.requestResources(page -> this.cloudFoundryClient.organizations()
                        .listBillingManagers(ListOrganizationBillingManagersRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listDomains() {
        String defaultOrganizationName = getOrganizationName();
        String organizationName = getOrganizationName();
        String privateDomainName = getDomainName();
        String sharedDomainName = getDomainName();

        requestCreateSharedDomainResponse(this.cloudFoundryClient, sharedDomainName)
            .after(
                when(
                    createOrganizationId(this.cloudFoundryClient, defaultOrganizationName),
                    createOrganizationId(this.cloudFoundryClient, organizationName)
                ))
            .then(function((defaultOrganizationId, organizationId) -> associatePrivateDomain(this.cloudFoundryClient, defaultOrganizationId, organizationId, privateDomainName)))
            .flatMap(function((organizationId, privateDomainId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.organizations()
                    .listDomains(ListOrganizationDomainsRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .build()))))
            .subscribe(this.testSubscriber()
                .assertCount(2));
    }

    @Test
    public void listManagers() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .then(function((organizationId, userId) -> associateManager(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> Mono
                .when(
                    Mono.just(userId),
                    PaginationUtils.requestResources(page -> this.cloudFoundryClient.organizations()
                        .listManagers(ListOrganizationManagersRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listPrivateDomain() {
        String domainName = getDomainName();
        String defaultOrganizationName = getOrganizationName();
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, defaultOrganizationName),
                createOrganizationId(this.cloudFoundryClient, organizationName)
            )
            .then(function((defaultOrganizationId, organizationId) -> associatePrivateDomain(this.cloudFoundryClient, defaultOrganizationId, organizationId, domainName)))
            .then(function((organizationId, privateDomainId) -> Mono
                .when(
                    Mono.just(privateDomainId),
                    PaginationUtils.requestResources(page -> this.cloudFoundryClient.organizations()
                        .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listServices() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                getServiceCount(this.cloudFoundryClient),
                createOrganizationId(this.cloudFoundryClient, organizationName)
                    .then(organizationId -> PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.organizations()
                            .listServices(ListOrganizationServicesRequest.builder()
                                .organizationId(organizationId)
                                .page(page)
                                .build()))
                        .count()))
            .subscribe(this.<Tuple2<Long, Long>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listSpaceQuotaDefinitions() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> PaginationUtils
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
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .as(thenKeep(organizationId -> requestCreateSpace(cloudFoundryClient, organizationId, spaceName)))
            .flatMap(organizationId -> PaginationUtils
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
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .then(function((organizationId, userId) -> associateUser(this.cloudFoundryClient, organizationId, userId)))
            .then(function((organizationId, userId) -> Mono
                .when(
                    Mono.just(userId),
                    PaginationUtils.requestResources(page -> this.cloudFoundryClient.organizations()
                        .listUsers(ListOrganizationUsersRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()))
                        .single()
                        .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void removeAuditor() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String domainName = getDomainName();
        String defaultOrganizationName = getOrganizationName();
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, defaultOrganizationName),
                createOrganizationId(this.cloudFoundryClient, organizationName)
            )
            .then(function((defaultOrganizationId, organizationId) -> associatePrivateDomain(this.cloudFoundryClient, defaultOrganizationId, organizationId, domainName)))
            .then(function((organizationId, privateDomainId) -> this.cloudFoundryClient.organizations()
                .removePrivateDomain(RemoveOrganizationPrivateDomainRequest.builder()
                    .privateDomainId(privateDomainId)
                    .organizationId(organizationId)
                    .build())))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeUser() {
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String organizationName = getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
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
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .summary(SummaryOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .build())
                .map(SummaryOrganizationResponse::getName))
            .subscribe(this.testSubscriber()
                .assertEquals(organizationName));
    }

    @Test
    public void update() {
        String organizationName = getOrganizationName();
        String organizationName2 = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> this.cloudFoundryClient.organizations()
                .update(UpdateOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .name(organizationName2)
                    .build())
                .map(ResourceUtils::getEntity)
                .map(OrganizationEntity::getName))
            .subscribe(this.testSubscriber()
                .assertEquals(organizationName2));
    }

    private static Mono<Tuple2<String, String>> associateAuditor(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return requestAssociateAuditor(cloudFoundryClient, organizationId, userId)
            .map(ignore -> Tuple2.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associateBillingManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return requestAssociateBillingManager(cloudFoundryClient, organizationId, userId)
            .map(ignore -> Tuple2.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associateManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return requestAssociateManager(cloudFoundryClient, organizationId, userId)
            .map(ignore -> Tuple2.of(organizationId, userId));
    }

    private static Mono<Tuple2<String, String>> associatePrivateDomain(CloudFoundryClient cloudFoundryClient, String defaultOrganizationId, String organizationId, String domainName) {
        return getPrivateDomainId(cloudFoundryClient, defaultOrganizationId, domainName)
            .then(privateDomainId -> requestAssociatePrivateDomain(cloudFoundryClient, organizationId, privateDomainId)
                .map(response -> Tuple2.of(organizationId, privateDomainId)));
    }

    private static Mono<Tuple2<String, String>> associateUser(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return requestAssociateUser(cloudFoundryClient, organizationId, userId)
            .map(ignore -> Tuple2.of(organizationId, userId));
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getPrivateDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domainName) {
        return requestCreatePrivateDomain(cloudFoundryClient, domainName, organizationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<Long> getServiceCount(CloudFoundryClient cloudFoundryClient) {
        return requestListAllServices(cloudFoundryClient)
            .count();
    }

    private static Mono<AssociateOrganizationAuditorResponse> requestAssociateAuditor(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                .auditorId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationBillingManagerResponse> requestAssociateBillingManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                .billingManagerId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationManagerResponse> requestAssociateManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateManager(AssociateOrganizationManagerRequest.builder()
                .managerId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationPrivateDomainResponse> requestAssociatePrivateDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String privateDomainId) {
        return cloudFoundryClient.organizations()
            .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                .organizationId(organizationId)
                .privateDomainId(privateDomainId)
                .build());
    }

    private static Mono<AssociateOrganizationUserResponse> requestAssociateUser(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateUser(AssociateOrganizationUserRequest.builder()
                .userId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build());
    }

    private static Mono<CreatePrivateDomainResponse> requestCreatePrivateDomain(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .build());
    }

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomainResponse(CloudFoundryClient cloudFoundryClient, String sharedDomainName) {
        return cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(sharedDomainName)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<GetOrganizationResponse> requestGetOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build());
    }

    private static Flux<ServiceResource> requestListAllServices(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .build()));
    }

}
