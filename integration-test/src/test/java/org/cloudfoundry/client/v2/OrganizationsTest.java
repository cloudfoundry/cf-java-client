/*
 * Copyright 2013-2017 the original author or authors.
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
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
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
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationServicesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
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
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class OrganizationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> userId;

    @Autowired
    private String username;

    @Test
    public void associateAuditor() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .flatMap(function((organizationId, userId) -> Mono.when(
                requestAssociateAuditor(this.cloudFoundryClient, organizationId, userId)
                    .map(ResourceUtils::getId),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateAuditorByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associateAuditorByUsername(AssociateOrganizationAuditorByUsernameRequest.builder()
                        .organizationId(organizationId)
                        .username(this.username)
                        .build())
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateBillingManager() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                        .billingManagerId(userId)
                        .organizationId(organizationId)
                        .build())
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateBillingManagerByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associateBillingManagerByUsername(AssociateOrganizationBillingManagerByUsernameRequest.builder()
                        .organizationId(organizationId)
                        .username(this.username)
                        .build())
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateManager() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associateManager(AssociateOrganizationManagerRequest.builder()
                        .managerId(userId)
                        .organizationId(organizationId)
                        .build())
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateManagerByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest.builder()
                        .organizationId(organizationId)
                        .username(this.username)
                        .build())
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associatePrivateDomain() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                createPrivateDomainId(this.cloudFoundryClient, organizationId, domainName),
                Mono.just(organizationId)
            ))
            .flatMap(function((privateDomainId, organizationId) -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                        .organizationId(organizationId)
                        .privateDomainId(privateDomainId)
                        .build())
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateUser() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associateUser(AssociateOrganizationUserRequest.builder()
                        .userId(userId)
                        .organizationId(organizationId)
                        .build())
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateUserByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                this.cloudFoundryClient.organizations()
                    .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                        .organizationId(organizationId)
                        .username(this.username)
                        .build())
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        this.cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build())
            .map(ResourceUtils::getEntity)
            .map(OrganizationEntity::getName)
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> this.cloudFoundryClient.organizations()
                .delete(DeleteOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .async(true)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMap(organizationId -> requestGetOrganization(this.cloudFoundryClient, organizationId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-OrganizationNotFound\\([0-9]+\\): The organization could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> this.cloudFoundryClient.organizations()
                .delete(DeleteOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .async(false)
                    .build()))
            .flatMap(organizationId -> requestGetOrganization(this.cloudFoundryClient, organizationId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-OrganizationNotFound\\([0-9]+\\): The organization could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.organizations()
                .get(GetOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(OrganizationEntity::getName)
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getInstanceUsage() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.organizations()
                .getInstanceUsage(GetOrganizationInstanceUsageRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .map(GetOrganizationInstanceUsageResponse::getInstanceUsage)
            .as(StepVerifier::create)
            .expectNext(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getMemoryUsage() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.organizations()
                .getMemoryUsage(GetOrganizationMemoryUsageRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .map(GetOrganizationMemoryUsageResponse::getMemoryUsageInMb)
            .as(StepVerifier::create)
            .expectNext(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getUserRoles() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMapMany(organizationId -> PaginationUtils.
                requestClientV2Resources(page -> this.cloudFoundryClient.organizations()
                    .getUserRoles(GetOrganizationUserRolesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMapMany(organizationId -> requestListOrganizations(this.cloudFoundryClient)
                .map(ResourceUtils::getId)
                .filter(organizationId::equals))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditors() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId)
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditorsFilterByAuditedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateAuditor(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateAuditor(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId1, builder -> builder.auditedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditorsFilterByAuditedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateAuditor(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceAuditor(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId, builder -> builder.auditedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditorsFilterByBillingManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateAuditor(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId1, builder -> builder.billingManagedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditorsFilterByManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateAuditor(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId1, builder -> builder.managedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditorsFilterByManagedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateAuditor(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId, builder -> builder.managedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: https://github.com/cloudfoundry/cf-java-client/issues/617
    @Ignore("https://github.com/cloudfoundry/cf-java-client/issues/617")
    @Test
    public void listAuditorsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(organizationId),
                Mono.just(userId),
                getUserDefaultSpaceId(this.cloudFoundryClient, userId)
            )))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId, builder -> builder.spaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagers() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateBillingManager(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId)
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagersFilterByAuditedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateAuditor(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId1, builder -> builder.auditedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagersFilterByAuditedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceAuditor(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId, builder -> builder.auditedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagersFilterByBillingManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId1, builder -> builder.billingManagedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagersFilterByManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId1, builder -> builder.managedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagersFilterByManagedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId, builder -> builder.managedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: https://github.com/cloudfoundry/cf-java-client/issues/617
    @Ignore("https://github.com/cloudfoundry/cf-java-client/issues/617")
    @Test
    public void listBillingManagersFilterBySpaceId() {

    }

    @Test
    public void listDomainsFilterByName() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String sharedDomainName = this.nameFactory.getDomainName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                createSharedDomainId(this.cloudFoundryClient, sharedDomainName)
            )
            .flatMapMany(function((organizationId, sharedDomainId) -> getDomainNames(this.cloudFoundryClient, organizationId, builder -> builder.name(sharedDomainName))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDomainsPrivate() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> createPrivateDomainId(this.cloudFoundryClient, organizationId, privateDomainName))
            .flatMapMany(organizationId -> getDomainNames(this.cloudFoundryClient, organizationId))
            .filter(privateDomainName::equals)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDomainsShared() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String sharedDomainName = this.nameFactory.getDomainName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                createSharedDomainId(this.cloudFoundryClient, sharedDomainName)
            )
            .flatMapMany(function((organizationId, sharedDomainId) -> getDomainNames(this.cloudFoundryClient, organizationId)))
            .filter(sharedDomainName::equals)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByAuditorId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .flatMapMany(function((organizationId, auditorId) -> requestListOrganizations(this.cloudFoundryClient, builder -> builder.auditorId(auditorId))
                .map(ResourceUtils::getId)
                .filter(organizationId::equals)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByBillingManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateBillingManager(this.cloudFoundryClient, organizationId, userId)))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizations(this.cloudFoundryClient, builder -> builder.billingManagerId(userId))
                .map(ResourceUtils::getId)
                .filter(organizationId::equals)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateManager(this.cloudFoundryClient, organizationId, userId)))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizations(this.cloudFoundryClient, builder -> builder.managerId(userId))
                .map(ResourceUtils::getId)
                .filter(organizationId::equals)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        requestCreateOrganization(this.cloudFoundryClient, organizationName)
            .thenMany(requestListOrganizations(this.cloudFoundryClient, builder -> builder.name(organizationName)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> Mono.when(
                Mono.just(organizationId),
                requestListOrganizations(this.cloudFoundryClient, builder -> builder.spaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByStatus() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String organizationStatus = "suspended";

        requestCreateOrganization(this.cloudFoundryClient, organizationName, builder -> builder.status(organizationStatus))
            .map(ResourceUtils::getId)
            .flatMapMany(organizationId -> requestListOrganizations(this.cloudFoundryClient, builder -> builder.status(organizationStatus))
                .filter(resource -> organizationId.equals(ResourceUtils.getId(resource)))
                .map(ResourceUtils::getEntity)
                .map(OrganizationEntity::getName))
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByUserId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateUser(this.cloudFoundryClient, organizationId, userId)))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizations(this.cloudFoundryClient, builder -> builder.userId(userId))
                .map(ResourceUtils::getId)
                .filter(organizationId::equals)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagers() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateManager(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationManagers(this.cloudFoundryClient, organizationId)
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByAuditedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateAuditor(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationManagers(this.cloudFoundryClient, organizationId1, builder -> builder.auditedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByAuditedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceAuditor(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationManagers(this.cloudFoundryClient, organizationId, builder -> builder.auditedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByBillingManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationManagers(this.cloudFoundryClient, organizationId1, builder -> builder.billingManagedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationManagers(this.cloudFoundryClient, organizationId1, builder -> builder.managedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByManagedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateManager(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationManagers(this.cloudFoundryClient, organizationId, builder -> builder.managedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: https://github.com/cloudfoundry/cf-java-client/issues/617
    @Ignore("https://github.com/cloudfoundry/cf-java-client/issues/617")
    @Test
    public void listManagersFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateManager(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(organizationId),
                Mono.just(userId),
                getUserDefaultSpaceId(this.cloudFoundryClient, userId)
            )))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationManagers(this.cloudFoundryClient, organizationId, builder -> builder.spaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listPrivateDomains() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String defaultOrganizationName = this.nameFactory.getOrganizationName();
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, defaultOrganizationName),
                createOrganizationId(this.cloudFoundryClient, organizationName)
            )
            .flatMap(function((defaultOrganizationId, organizationId) -> Mono.when(
                Mono.just(organizationId),
                createPrivateDomainId(this.cloudFoundryClient, defaultOrganizationId, domainName)
            )))
            .delayUntil(function((organizationId, privateDomainId) -> requestAssociatePrivateDomain(this.cloudFoundryClient, organizationId, privateDomainId)))
            .flatMap(function((organizationId, privateDomainId) -> Mono.when(
                Mono.just(privateDomainId),
                requestListOrganizationPrivateDomains(this.cloudFoundryClient, organizationId)
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listPrivateDomainsFilterByName() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createPrivateDomainId(this.cloudFoundryClient, organizationId, privateDomainName)
            ))
            .flatMap(function((organizationId, privateDomainId) -> Mono.when(
                Mono.just(privateDomainId),
                requestListOrganizationPrivateDomains(this.cloudFoundryClient, organizationId, builder -> builder.name(privateDomainName))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServices() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                requestListServices(this.cloudFoundryClient)
                    .count(),
                createOrganizationId(this.cloudFoundryClient, organizationName)
                    .flatMap(organizationId -> requestListOrganizationServices(this.cloudFoundryClient, organizationId)
                        .count()))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicesFilterByActive() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicesFilterByLabel() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicesFilterByProvider() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicesFilterByServiceBrokerId() {
        //
    }

    @Test
    public void listSpaceQuotaDefinitions() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMapMany(organizationId -> PaginationUtils.
                requestClientV2Resources(page -> this.cloudFoundryClient.organizations()
                    .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpaces() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateSpace(this.cloudFoundryClient, organizationId, spaceName))
            .flatMapMany(organizationId -> requestListOrganizationSpaces(this.cloudFoundryClient, organizationId))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByApplicationId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String applicationName = this.nameFactory.getApplicationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> Mono.when(
                Mono.just(organizationId),
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            )))
            .flatMapMany((function((organizationId, spaceId, applicationId) -> Mono.when(
                Mono.just(spaceId),
                requestListOrganizationSpaces(this.cloudFoundryClient, organizationId, builder -> builder.applicationId(applicationId))
                    .single()
                    .map(ResourceUtils::getId)
            ))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByDeveloperId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateUser(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                Mono.just(userId)
            )))
            .delayUntil(function((organizationId, spaceId, userId) -> requestAssociateSpaceDeveloper(this.cloudFoundryClient, spaceId, userId)))
            .flatMapMany((function((organizationId, spaceId, userId) -> Mono.when(
                Mono.just(spaceId),
                requestListOrganizationSpaces(this.cloudFoundryClient, organizationId, builder -> builder.developerId(userId))
                    .single()
                    .map(ResourceUtils::getId)
            ))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByName() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMapMany((function((organizationId, spaceId) -> Mono.when(
                Mono.just(spaceId),
                requestListOrganizationSpaces(this.cloudFoundryClient, organizationId, builder -> builder.name(spaceName))
                    .single()
                    .map(ResourceUtils::getId)
            ))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUsers() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateUser(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationUsers(this.cloudFoundryClient, organizationId)
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUsersFilterByAuditedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateUser(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateAuditor(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationUsers(this.cloudFoundryClient, organizationId1, builder -> builder.auditedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUsersFilterByAuditedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateUser(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceAuditor(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationUsers(this.cloudFoundryClient, organizationId, builder -> builder.auditedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUsersFilterByBillingManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateUser(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateBillingManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationUsers(this.cloudFoundryClient, organizationId1, builder -> builder.billingManagedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUsersFilterByManagedOrganizationId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateUser(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateManager(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationUsers(this.cloudFoundryClient, organizationId1, builder -> builder.managedOrganizationId(organizationId2))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUsersFilterByManagedSpaceId() throws TimeoutException, InterruptedException {
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName1),
                createOrganizationId(this.cloudFoundryClient, organizationName2),
                this.userId
            )
            .delayUntil(function((organizationId1, organizationId2, userId) -> Mono.when(
                requestAssociateUser(this.cloudFoundryClient, organizationId1, userId),
                requestAssociateUser(this.cloudFoundryClient, organizationId2, userId)
            )))
            .flatMap(function((organizationId1, organizationId2, userId) -> Mono.when(
                Mono.just(organizationId1),
                Mono.just(userId),
                createSpaceId(this.cloudFoundryClient, organizationId2, spaceName)
            )))
            .delayUntil(function((organizationId, userId, spaceId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationUsers(this.cloudFoundryClient, organizationId, builder -> builder.managedSpaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: https://github.com/cloudfoundry/cf-java-client/issues/617
    @Ignore("https://github.com/cloudfoundry/cf-java-client/issues/617")
    @Test
    public void listUsersFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(function((organizationId, userId) -> Mono.when(
                Mono.just(organizationId),
                Mono.just(userId),
                getUserDefaultSpaceId(this.cloudFoundryClient, userId)
            )))
            .flatMap(function((organizationId, userId, spaceId) -> Mono.when(
                Mono.just(userId),
                requestListOrganizationAuditors(this.cloudFoundryClient, organizationId, builder -> builder.spaceId(spaceId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeAuditor() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeAuditor(RemoveOrganizationAuditorRequest.builder()
                    .auditorId(userId)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationAuditors(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeAuditorByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateAuditor(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeAuditorByUsername(RemoveOrganizationAuditorByUsernameRequest.builder()
                    .username(this.username)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationAuditors(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeBillingManager() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateBillingManager(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeBillingManager(RemoveOrganizationBillingManagerRequest.builder()
                    .billingManagerId(userId)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeBillingManagerByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateBillingManager(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeBillingManagerByUsername(RemoveOrganizationBillingManagerByUsernameRequest.builder()
                    .username(this.username)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationBillingManagers(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeManager() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateManager(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeManager(RemoveOrganizationManagerRequest.builder()
                    .managerId(userId)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationManagers(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeManagerByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateManager(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeManagerByUsername(RemoveOrganizationManagerByUsernameRequest.builder()
                    .username(this.username)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationManagers(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removePrivateDomain() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();
        String defaultOrganizationName = this.nameFactory.getOrganizationName();
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, defaultOrganizationName),
                createOrganizationId(this.cloudFoundryClient, organizationName)
            )
            .flatMap(function((defaultOrganizationId, organizationId) -> Mono.when(
                Mono.just(organizationId),
                createPrivateDomainId(this.cloudFoundryClient, defaultOrganizationId, domainName)
            )))
            .delayUntil(function((organizationId, privateDomainId) -> requestAssociatePrivateDomain(this.cloudFoundryClient, organizationId, privateDomainId)))
            .delayUntil(function((organizationId, privateDomainId) -> this.cloudFoundryClient.organizations()
                .removePrivateDomain(RemoveOrganizationPrivateDomainRequest.builder()
                    .privateDomainId(privateDomainId)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, privateDomainId) -> requestListOrganizationPrivateDomains(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeUser() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateUser(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeUser(RemoveOrganizationUserRequest.builder()
                    .userId(userId)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationUsers(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeUserByUsername() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        Mono
            .when(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                this.userId
            )
            .delayUntil(function((organizationId, userId) -> requestAssociateUser(this.cloudFoundryClient, organizationId, userId)))
            .delayUntil(function((organizationId, userId) -> this.cloudFoundryClient.organizations()
                .removeUserByUsername(RemoveOrganizationUserByUsernameRequest.builder()
                    .username(this.username)
                    .organizationId(organizationId)
                    .build())))
            .flatMapMany(function((organizationId, userId) -> requestListOrganizationUsers(this.cloudFoundryClient, organizationId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void summary() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.organizations()
                .summary(SummaryOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .build())
                .map(SummaryOrganizationResponse::getName))
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> this.cloudFoundryClient.organizations()
                .update(UpdateOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .name(organizationName2)
                    .build()))
            .flatMap(organizationId -> requestGetOrganization(this.cloudFoundryClient, organizationId))
            .map(ResourceUtils::getEntity)
            .map(OrganizationEntity::getName)
            .as(StepVerifier::create)
            .expectNext(organizationName2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createPrivateDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domainName) {
        return requestCreatePrivateDomain(cloudFoundryClient, domainName, organizationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSharedDomainId(CloudFoundryClient cloudFoundryClient, String sharedDomainName) {
        return requestCreateSharedDomain(cloudFoundryClient, sharedDomainName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Flux<String> getDomainNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return getDomainNames(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<String> getDomainNames(CloudFoundryClient cloudFoundryClient, String organizationId, UnaryOperator<ListSharedDomainsRequest.Builder> transformer) {
        return requestListPrivateDomains(cloudFoundryClient, organizationId)
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .mergeWith(requestListSharedDomains(cloudFoundryClient, transformer)
                .map(resource -> ResourceUtils.getEntity(resource).getName()));
    }

    private static Mono<String> getUserDefaultSpaceId(CloudFoundryClient cloudFoundryClient, String userId) {
        return requestListUsers(cloudFoundryClient)
            .filter(resource -> userId.equals(ResourceUtils.getId(resource)))
            .single()
            .map(ResourceUtils::getEntity)
            .map(UserEntity::getDefaultSpaceId);
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

    private static Mono<AssociateSpaceAuditorResponse> requestAssociateSpaceAuditor(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateAuditor(AssociateSpaceAuditorRequest.builder()
                .spaceId(spaceId)
                .auditorId(userId)
                .build());
    }

    private static Mono<AssociateSpaceDeveloperResponse> requestAssociateSpaceDeveloper(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                .spaceId(spaceId)
                .developerId(userId)
                .build());
    }

    private static Mono<AssociateSpaceManagerResponse> requestAssociateSpaceManager(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateManager(AssociateSpaceManagerRequest.builder()
                .spaceId(spaceId)
                .managerId(userId)
                .build());
    }

    private static Mono<AssociateOrganizationUserResponse> requestAssociateUser(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateUser(AssociateOrganizationUserRequest.builder()
                .userId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName, UnaryOperator.identity());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName,
                                                                              UnaryOperator<CreateOrganizationRequest.Builder> transformer) {
        return cloudFoundryClient.organizations()
            .create(transformer.apply(CreateOrganizationRequest.builder())
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

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String sharedDomainName) {
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

    private static Flux<UserResource> requestListOrganizationAuditors(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationAuditors(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<UserResource> requestListOrganizationAuditors(CloudFoundryClient cloudFoundryClient, String organizationId,
                                                                      UnaryOperator<ListOrganizationAuditorsRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listAuditors(transformer.apply(ListOrganizationAuditorsRequest.builder())
                    .page(page)
                    .organizationId(organizationId)
                    .build()));
    }

    private static Flux<UserResource> requestListOrganizationBillingManagers(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationBillingManagers(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<UserResource> requestListOrganizationBillingManagers(CloudFoundryClient cloudFoundryClient, String organizationId,
                                                                             UnaryOperator<ListOrganizationBillingManagersRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listBillingManagers(transformer.apply(ListOrganizationBillingManagersRequest.builder())
                    .page(page)
                    .organizationId(organizationId)
                    .build()));
    }

    private static Flux<UserResource> requestListOrganizationManagers(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationManagers(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<UserResource> requestListOrganizationManagers(CloudFoundryClient cloudFoundryClient, String organizationId,
                                                                      UnaryOperator<ListOrganizationManagersRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listManagers(transformer.apply(ListOrganizationManagersRequest.builder())
                    .page(page)
                    .organizationId(organizationId)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestListOrganizationPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationPrivateDomains(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<PrivateDomainResource> requestListOrganizationPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId,
                                                                                     UnaryOperator<ListOrganizationPrivateDomainsRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(transformer.apply(ListOrganizationPrivateDomainsRequest.builder())
                    .page(page)
                    .organizationId(organizationId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListOrganizationServices(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationServices(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<ServiceResource> requestListOrganizationServices(CloudFoundryClient cloudFoundryClient, String organizationId,
                                                                         UnaryOperator<ListOrganizationServicesRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listServices(transformer.apply(ListOrganizationServicesRequest.builder())
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestListOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationSpaces(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<SpaceResource> requestListOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, UnaryOperator<ListOrganizationSpacesRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaces(transformer.apply(ListOrganizationSpacesRequest.builder())
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<UserResource> requestListOrganizationUsers(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationUsers(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<UserResource> requestListOrganizationUsers(CloudFoundryClient cloudFoundryClient, String organizationId, UnaryOperator<ListOrganizationUsersRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listUsers(transformer.apply(ListOrganizationUsersRequest.builder())
                    .page(page)
                    .organizationId(organizationId)
                    .build()));
    }

    private static Flux<OrganizationResource> requestListOrganizations(CloudFoundryClient cloudFoundryClient) {
        return requestListOrganizations(cloudFoundryClient, UnaryOperator.identity());
    }

    private static Flux<OrganizationResource> requestListOrganizations(CloudFoundryClient cloudFoundryClient, UnaryOperator<ListOrganizationsRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(transformer.apply(ListOrganizationsRequest.builder())
                    .page(page)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestListPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .build()));
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient, UnaryOperator<ListSharedDomainsRequest.Builder> transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(transformer.apply(ListSharedDomainsRequest.builder())
                    .page(page)
                    .build()));
    }

    private static Flux<UserResource> requestListUsers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.users()
                .list(ListUsersRequest.builder()
                    .page(page)
                    .build()));
    }

}
