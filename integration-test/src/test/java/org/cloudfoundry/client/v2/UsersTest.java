/*
 * Copyright 2013-2020 the original author or authors.
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
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.users.AssociateUserAuditedOrganizationRequest;
import org.cloudfoundry.client.v2.users.AssociateUserAuditedOrganizationResponse;
import org.cloudfoundry.client.v2.users.AssociateUserAuditedSpaceRequest;
import org.cloudfoundry.client.v2.users.AssociateUserAuditedSpaceResponse;
import org.cloudfoundry.client.v2.users.AssociateUserBillingManagedOrganizationRequest;
import org.cloudfoundry.client.v2.users.AssociateUserBillingManagedOrganizationResponse;
import org.cloudfoundry.client.v2.users.AssociateUserManagedOrganizationRequest;
import org.cloudfoundry.client.v2.users.AssociateUserManagedOrganizationResponse;
import org.cloudfoundry.client.v2.users.AssociateUserManagedSpaceRequest;
import org.cloudfoundry.client.v2.users.AssociateUserManagedSpaceResponse;
import org.cloudfoundry.client.v2.users.AssociateUserOrganizationRequest;
import org.cloudfoundry.client.v2.users.AssociateUserOrganizationResponse;
import org.cloudfoundry.client.v2.users.AssociateUserSpaceRequest;
import org.cloudfoundry.client.v2.users.AssociateUserSpaceResponse;
import org.cloudfoundry.client.v2.users.CreateUserRequest;
import org.cloudfoundry.client.v2.users.CreateUserResponse;
import org.cloudfoundry.client.v2.users.DeleteUserRequest;
import org.cloudfoundry.client.v2.users.GetUserRequest;
import org.cloudfoundry.client.v2.users.ListUserAuditedOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserAuditedSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserBillingManagedOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserManagedOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserManagedSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.RemoveUserAuditedOrganizationRequest;
import org.cloudfoundry.client.v2.users.RemoveUserAuditedSpaceRequest;
import org.cloudfoundry.client.v2.users.RemoveUserBillingManagedOrganizationRequest;
import org.cloudfoundry.client.v2.users.RemoveUserManagedOrganizationRequest;
import org.cloudfoundry.client.v2.users.RemoveUserManagedSpaceRequest;
import org.cloudfoundry.client.v2.users.RemoveUserOrganizationRequest;
import org.cloudfoundry.client.v2.users.RemoveUserSpaceRequest;
import org.cloudfoundry.client.v2.users.SummaryUserRequest;
import org.cloudfoundry.client.v2.users.SummaryUserResponse;
import org.cloudfoundry.client.v2.users.UpdateUserRequest;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class UsersTest extends AbstractIntegrationTest {

    private static final String STATUS_FILTER = "active";

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Test
    public void associateAuditedOrganization() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganizationAuditor(this.cloudFoundryClient, organizationId, userId))
                .then(this.cloudFoundryClient.users()
                    .associateAuditedOrganization(AssociateUserAuditedOrganizationRequest.builder()
                        .auditedOrganizationId(organizationId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId)
                .flatMapIterable(response -> response.getEntity().getAuditedOrganizations())
                .map(resource -> resource.getEntity().getName())
                .single())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateAuditedSpace() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(this.cloudFoundryClient.users()
                    .associateAuditedSpace(AssociateUserAuditedSpaceRequest.builder()
                        .auditedSpaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getAuditedSpaces())
            .map(space -> space.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateBillingManagedOrganization() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganizationBillingManager(this.cloudFoundryClient, organizationId, userId))
                .then(this.cloudFoundryClient.users()
                    .associateBillingManagedOrganization(AssociateUserBillingManagedOrganizationRequest.builder()
                        .billingManagedOrganizationId(organizationId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId)
                .flatMapIterable(response -> response.getEntity().getBillingManagedOrganizations())
                .map(resource -> resource.getEntity().getName())
                .single())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateManagedOrganization() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganizationManager(this.cloudFoundryClient, organizationId, userId))
                .then(this.cloudFoundryClient.users()
                    .associateManagedOrganization(AssociateUserManagedOrganizationRequest.builder()
                        .managedOrganizationId(organizationId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId)
                .flatMapIterable(response -> response.getEntity().getManagedOrganizations())
                .map(resource -> resource.getEntity().getName())
                .single())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateManagedSpace() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(this.cloudFoundryClient.users()
                    .associateManagedSpace(AssociateUserManagedSpaceRequest.builder()
                        .managedSpaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getManagedSpaces())
            .map(space -> space.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateOrganization() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(this.cloudFoundryClient.users()
                    .associateOrganization(AssociateUserOrganizationRequest.builder()
                        .organizationId(organizationId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId)
                .flatMapIterable(response -> response.getEntity().getOrganizations())
                .map(resource -> resource.getEntity().getName())
                .single())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateSpace() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(this.cloudFoundryClient.users()
                    .associateSpace(AssociateUserSpaceRequest.builder()
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getSpaces())
            .map(space -> space.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> this.cloudFoundryClient.users()
                .create(CreateUserRequest.builder()
                    .defaultSpaceId(spaceId)
                    .uaaId(userId)
                    .build()))
            .thenMany(requestListUsers(this.cloudFoundryClient))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsync() {
        String userId = this.nameFactory.getUserId();

        requestCreateUser(this.cloudFoundryClient, userId)
            .then(this.cloudFoundryClient.users()
                .delete(DeleteUserRequest.builder()
                    .async(true)
                    .userId(userId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .thenMany(requestListUsers(this.cloudFoundryClient))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteNoAsync() {
        String userId = this.nameFactory.getUserId();

        requestCreateUser(this.cloudFoundryClient, userId)
            .then(this.cloudFoundryClient.users()
                .delete(DeleteUserRequest.builder()
                    .async(false)
                    .userId(userId)
                    .build()))
            .thenMany(requestListUsers(this.cloudFoundryClient))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .map(ResourceUtils::getId)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(this.cloudFoundryClient.users()
                    .get(GetUserRequest.builder()
                        .userId(userId)
                        .build())
                    .map(response -> Tuples.of(spaceId, response.getEntity().getDefaultSpaceId()))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String userId = this.nameFactory.getUserId();

        requestCreateUser(this.cloudFoundryClient, userId)
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .list(ListUsersRequest.builder()
                        .page(page)
                        .build())))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

    }

    @Test
    public void listAuditedOrganizations() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedOrganizations(ListUserAuditedOrganizationsRequest.builder()
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedOrganizationsFilterByAuditorId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedOrganizations(ListUserAuditedOrganizationsRequest.builder()
                        .auditorId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedOrganizationsFilterByBillingManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.zip(
                associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId),
                associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedOrganizations(ListUserAuditedOrganizationsRequest.builder()
                        .billingManagerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedOrganizationsFilterByManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.zip(
                associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId),
                associateManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedOrganizations(ListUserAuditedOrganizationsRequest.builder()
                        .managerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedOrganizationsFilterByName() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedOrganizations(ListUserAuditedOrganizationsRequest.builder()
                        .name(organizationName)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedOrganizationsFilterBySpaceId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId))
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedOrganizations(ListUserAuditedOrganizationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedOrganizationsFilterByStatus() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedOrganizations(ListUserAuditedOrganizationsRequest.builder()
                        .page(page)
                        .status(STATUS_FILTER)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedSpaces() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateAuditedSpace(this.cloudFoundryClient, spaceId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedSpaces(ListUserAuditedSpacesRequest.builder()
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedSpacesFilterByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                    .then(requestAssociateAuditedSpace(this.cloudFoundryClient, spaceId, userId)))
            )
            .flatMapMany(function((applicationId, ignore) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedSpaces(ListUserAuditedSpacesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .userId(userId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedSpacesFilterByDeveloperId() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(Mono.zip(
                    requestAssociateSpace(this.cloudFoundryClient, spaceId, userId),
                    requestAssociateAuditedSpace(this.cloudFoundryClient, spaceId, userId))
                ))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedSpaces(ListUserAuditedSpacesRequest.builder()
                        .developerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedSpacesFilterByName() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateAuditedSpace(this.cloudFoundryClient, spaceId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedSpaces(ListUserAuditedSpacesRequest.builder()
                        .name(spaceName)
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditedSpacesFilterByOrganizationId() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateAuditedSpace(this.cloudFoundryClient, spaceId, userId))
                .thenReturn(organizationId)))
            .flatMapMany(organizationId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listAuditedSpaces(ListUserAuditedSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagedOrganizations() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest.builder()
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagedOrganizationsFilterByAuditorId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.zip(
                associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId),
                associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest.builder()
                        .auditorId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagedOrganizationsFilterByBillingManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest.builder()
                        .billingManagerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagedOrganizationsFilterByManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.zip(
                associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId),
                associateManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest.builder()
                        .managerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagedOrganizationsFilterByName() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest.builder()
                        .name(organizationName)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagedOrganizationsFilterBySpaceId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId))
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listBillingManagedOrganizationsFilterByStatus() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest.builder()
                        .page(page)
                        .status(STATUS_FILTER)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganization() {
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .delayUntil(organizationId -> requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId))
            .flatMapMany(organizationId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .list(ListUsersRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBySpace() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .delayUntil(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId))
            .delayUntil(spaceId -> requestAssociateSpace(this.cloudFoundryClient, spaceId, userId))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .list(ListUsersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build())))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedOrganizations() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> associateManagerOrganization(this.cloudFoundryClient, organizationId, userId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedOrganizations(ListUserManagedOrganizationsRequest.builder()
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedOrganizationsFilterByAuditorId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.zip(
                associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId),
                associateManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedOrganizations(ListUserManagedOrganizationsRequest.builder()
                        .auditorId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedOrganizationsFilterByBillingManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.zip(
                associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId),
                associateManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedOrganizations(ListUserManagedOrganizationsRequest.builder()
                        .billingManagerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedOrganizationsFilterByManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> associateManagerOrganization(this.cloudFoundryClient, organizationId, userId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedOrganizations(ListUserManagedOrganizationsRequest.builder()
                        .managerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedOrganizationsFilterByName() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(associateManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedOrganizations(ListUserManagedOrganizationsRequest.builder()
                        .name(organizationName)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedOrganizationsFilterBySpaceId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(associateManagerOrganization(this.cloudFoundryClient, organizationId, userId))
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedOrganizations(ListUserManagedOrganizationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedOrganizationsFilterByStatus() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(associateManagerOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedOrganizations(ListUserManagedOrganizationsRequest.builder()
                        .page(page)
                        .status(STATUS_FILTER)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedSpaces() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateManagedSpace(this.cloudFoundryClient, spaceId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedSpaces(ListUserManagedSpacesRequest.builder()
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedSpacesFilterByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                    .then(requestAssociateManagedSpace(this.cloudFoundryClient, spaceId, userId)))
            )
            .flatMapMany(function((applicationId, ignore) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedSpaces(ListUserManagedSpacesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .userId(userId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedSpacesFilterByDeveloperId() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(Mono.zip(
                    requestAssociateSpace(this.cloudFoundryClient, spaceId, userId),
                    requestAssociateManagedSpace(this.cloudFoundryClient, spaceId, userId))
                ))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedSpaces(ListUserManagedSpacesRequest.builder()
                        .developerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedSpacesFilterByName() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateManagedSpace(this.cloudFoundryClient, spaceId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedSpaces(ListUserManagedSpacesRequest.builder()
                        .name(spaceName)
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagedSpacesFilterByOrganizationId() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateManagedSpace(this.cloudFoundryClient, spaceId, userId))
                .thenReturn(organizationId)))
            .flatMapMany(organizationId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listManagedSpaces(ListUserManagedSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizations() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listOrganizations(ListUserOrganizationsRequest.builder()
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationsFilterByAuditorId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(Mono.zip(
                    requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId),
                    associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId))))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listOrganizations(ListUserOrganizationsRequest.builder()
                        .auditorId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationsFilterByBillingManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(Mono.zip(
                    requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId),
                    associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId))))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listOrganizations(ListUserOrganizationsRequest.builder()
                        .billingManagerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationsFilterByManagerId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(Mono.zip(
                    requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId),
                    associateManagerOrganization(this.cloudFoundryClient, organizationId, userId))))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listOrganizations(ListUserOrganizationsRequest.builder()
                        .managerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationsFilterByName() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listOrganizations(ListUserOrganizationsRequest.builder()
                        .name(organizationName)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationsFilterBySpaceId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId))
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listOrganizations(ListUserOrganizationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationsFilterByStatus() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listOrganizations(ListUserOrganizationsRequest.builder()
                        .page(page)
                        .status(STATUS_FILTER)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpaces() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .page(page)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                    .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            )
            .flatMapMany(function((applicationId, ignore) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .userId(userId)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByDeveloperId() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .developerId(userId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByName() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .name(spaceName)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByOrganizationId() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId))
                .thenReturn(organizationId)))
            .flatMapMany(organizationId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .userId(userId)
                        .build())))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeAuditedOrganization() {
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganizationAuditor(this.cloudFoundryClient, organizationId, userId))
                .then(requestAssociateAuditedOrganization(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(organizationId -> this.cloudFoundryClient.users()
                .removeAuditedOrganization(RemoveUserAuditedOrganizationRequest.builder()
                    .auditedOrganizationId(organizationId)
                    .userId(userId)
                    .build()))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getAuditedOrganizations())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeAuditedSpace() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateAuditedSpace(this.cloudFoundryClient, spaceId, userId))
                .then(this.cloudFoundryClient.users()
                    .removeAuditedSpace(RemoveUserAuditedSpaceRequest.builder()
                        .auditedSpaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getAuditedSpaces())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeBillingManagedOrganization() {
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganizationBillingManager(this.cloudFoundryClient, organizationId, userId))
                .then(requestAssociateBillingManagedOrganization(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(organizationId -> this.cloudFoundryClient.users()
                .removeBillingManagedOrganization(RemoveUserBillingManagedOrganizationRequest.builder()
                    .billingManagedOrganizationId(organizationId)
                    .userId(userId)
                    .build()))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getBillingManagedOrganizations())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeManagedOrganization() {
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganizationManager(this.cloudFoundryClient, organizationId, userId))
                .then(requestAssociateManagedOrganization(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(organizationId -> this.cloudFoundryClient.users()
                .removeManagedOrganization(RemoveUserManagedOrganizationRequest.builder()
                    .managedOrganizationId(organizationId)
                    .userId(userId)
                    .build()))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getManagedOrganizations())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeManagedSpace() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateManagedSpace(this.cloudFoundryClient, spaceId, userId))
                .then(this.cloudFoundryClient.users()
                    .removeManagedSpace(RemoveUserManagedSpaceRequest.builder()
                        .managedSpaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getManagedSpaces())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeOrganization() {
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId)))
            .flatMap(organizationId -> this.cloudFoundryClient.users()
                .removeOrganization(RemoveUserOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .userId(userId)
                    .build()))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getOrganizations())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeSpace() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId))
                .then(this.cloudFoundryClient.users()
                    .removeSpace(RemoveUserSpaceRequest.builder()
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getSpaces())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void summary() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId)))
            .then(this.cloudFoundryClient.users()
                .summary(SummaryUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getOrganizations())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .delayUntil(spaceId -> requestCreateUser(this.cloudFoundryClient, userId))
            .delayUntil(spaceId -> this.cloudFoundryClient.users()
                .update(UpdateUserRequest.builder()
                    .defaultSpaceId(spaceId)
                    .userId(userId)
                    .build()))
            .flatMapMany(spaceId -> requestListUsers(this.cloudFoundryClient)
                .filter(resource -> spaceId.equals(resource.getEntity().getDefaultSpaceId())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<AssociateUserAuditedOrganizationResponse> associateAuditorOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return requestAssociateOrganizationAuditor(cloudFoundryClient, organizationId, userId)
            .then(requestAssociateAuditedOrganization(cloudFoundryClient, organizationId, userId));
    }

    private static Mono<AssociateUserBillingManagedOrganizationResponse> associateBillingManagerOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return requestAssociateOrganizationBillingManager(cloudFoundryClient, organizationId, userId)
            .then(requestAssociateBillingManagedOrganization(cloudFoundryClient, organizationId, userId));
    }

    private static Mono<AssociateUserManagedOrganizationResponse> associateManagerOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return requestAssociateOrganizationManager(cloudFoundryClient, organizationId, userId)
            .then(requestAssociateManagedOrganization(cloudFoundryClient, organizationId, userId));
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<AssociateUserAuditedOrganizationResponse> requestAssociateAuditedOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.users()
            .associateAuditedOrganization(AssociateUserAuditedOrganizationRequest.builder()
                .auditedOrganizationId(organizationId)
                .userId(userId)
                .build());
    }

    private static Mono<AssociateUserAuditedSpaceResponse> requestAssociateAuditedSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.users()
            .associateAuditedSpace(AssociateUserAuditedSpaceRequest.builder()
                .auditedSpaceId(spaceId)
                .userId(userId)
                .build());
    }

    private static Mono<AssociateUserBillingManagedOrganizationResponse> requestAssociateBillingManagedOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.users()
            .associateBillingManagedOrganization(AssociateUserBillingManagedOrganizationRequest.builder()
                .billingManagedOrganizationId(organizationId)
                .userId(userId)
                .build());
    }

    private static Mono<AssociateUserManagedOrganizationResponse> requestAssociateManagedOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.users()
            .associateManagedOrganization(AssociateUserManagedOrganizationRequest.builder()
                .managedOrganizationId(organizationId)
                .userId(userId)
                .build());
    }

    private static Mono<AssociateUserManagedSpaceResponse> requestAssociateManagedSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.users()
            .associateManagedSpace(AssociateUserManagedSpaceRequest.builder()
                .managedSpaceId(spaceId)
                .userId(userId)
                .build());
    }

    private static Mono<AssociateUserOrganizationResponse> requestAssociateOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.users()
            .associateOrganization(AssociateUserOrganizationRequest.builder()
                .organizationId(organizationId)
                .userId(userId)
                .build());
    }

    private static Mono<AssociateOrganizationAuditorResponse> requestAssociateOrganizationAuditor(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                .auditorId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationBillingManagerResponse> requestAssociateOrganizationBillingManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                .billingManagerId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationManagerResponse> requestAssociateOrganizationManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateManager(AssociateOrganizationManagerRequest.builder()
                .managerId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateUserSpaceResponse> requestAssociateSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.users()
            .associateSpace(AssociateUserSpaceRequest.builder()
                .spaceId(spaceId)
                .userId(userId)
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
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .status(STATUS_FILTER)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .organizationId(organizationId)
                .name(spaceName)
                .build());
    }

    private static Mono<CreateUserResponse> requestCreateUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient.users()
            .create(CreateUserRequest.builder()
                .uaaId(userId)
                .build());
    }

    private static Mono<CreateUserResponse> requestCreateUser(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.users()
            .create(CreateUserRequest.builder()
                .defaultSpaceId(spaceId)
                .uaaId(userId)
                .build());
    }

    private static Flux<UserResource> requestListUsers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.users()
                .list(ListUsersRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Mono<SummaryUserResponse> requestSummaryUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient.users()
            .summary(SummaryUserRequest.builder()
                .userId(userId)
                .build());
    }

}
