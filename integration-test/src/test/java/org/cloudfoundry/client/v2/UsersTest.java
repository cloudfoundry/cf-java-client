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
import java.util.concurrent.TimeoutException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class UsersTest extends AbstractIntegrationTest {

    private static final String STATUS_FILTER = "active";

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Test
    public void associateAuditedOrganization() throws TimeoutException, InterruptedException {
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
    public void associateAuditedSpace() throws TimeoutException, InterruptedException {
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
    public void associateBillingManagedOrganization() throws TimeoutException, InterruptedException {
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
    public void associateManagedOrganization() throws TimeoutException, InterruptedException {
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
    public void associateManagedSpace() throws TimeoutException, InterruptedException {
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
    public void associateOrganization() throws TimeoutException, InterruptedException {
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
    public void associateSpace() throws TimeoutException, InterruptedException {
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
    public void create() throws TimeoutException, InterruptedException {
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
    public void deleteAsync() throws TimeoutException, InterruptedException {
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
    public void deleteNoAsync() throws TimeoutException, InterruptedException {
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
    public void get() throws TimeoutException, InterruptedException {
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
    public void list() throws TimeoutException, InterruptedException {
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
    public void listAuditedOrganizations() throws TimeoutException, InterruptedException {
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
    public void listAuditedOrganizationsFilterByAuditorId() throws TimeoutException, InterruptedException {
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
    public void listAuditedOrganizationsFilterByBillingManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.when(
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
    public void listAuditedOrganizationsFilterByManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.when(
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
    public void listAuditedOrganizationsFilterByName() throws TimeoutException, InterruptedException {
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
    public void listAuditedOrganizationsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(associateAuditorOrganization(this.cloudFoundryClient, organizationId, userId))
                .then(Mono.just(spaceId))))
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
    public void listAuditedOrganizationsFilterByStatus() throws TimeoutException, InterruptedException {
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
    public void listAuditedSpaces() throws TimeoutException, InterruptedException {
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
    public void listAuditedSpacesFilterByApplicationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.when(
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
    public void listAuditedSpacesFilterByDeveloperId() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(Mono.when(
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
    public void listAuditedSpacesFilterByName() throws TimeoutException, InterruptedException {
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
    public void listAuditedSpacesFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateAuditedSpace(this.cloudFoundryClient, spaceId, userId))
                .then(Mono.just(organizationId))))
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
    public void listBillingManagedOrganizations() throws TimeoutException, InterruptedException {
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
    public void listBillingManagedOrganizationsFilterByAuditorId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.when(
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
    public void listBillingManagedOrganizationsFilterByBillingManagerId() throws TimeoutException, InterruptedException {
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
    public void listBillingManagedOrganizationsFilterByManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.when(
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
    public void listBillingManagedOrganizationsFilterByName() throws TimeoutException, InterruptedException {
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
    public void listBillingManagedOrganizationsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(associateBillingManagerOrganization(this.cloudFoundryClient, organizationId, userId))
                .then(Mono.just(spaceId))))
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
    public void listBillingManagedOrganizationsFilterByStatus() throws TimeoutException, InterruptedException {
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
    public void listFilterByOrganization() throws TimeoutException, InterruptedException {
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
    public void listFilterBySpace() throws TimeoutException, InterruptedException {
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
    public void listManagedOrganizations() throws TimeoutException, InterruptedException {
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
    public void listManagedOrganizationsFilterByAuditorId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.when(
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
    public void listManagedOrganizationsFilterByBillingManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateUser(this.cloudFoundryClient, userId))
            .flatMap(organizationId -> Mono.when(
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
    public void listManagedOrganizationsFilterByManagerId() throws TimeoutException, InterruptedException {
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
    public void listManagedOrganizationsFilterByName() throws TimeoutException, InterruptedException {
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
    public void listManagedOrganizationsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(associateManagerOrganization(this.cloudFoundryClient, organizationId, userId))
                .then(Mono.just(spaceId))))
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
    public void listManagedOrganizationsFilterByStatus() throws TimeoutException, InterruptedException {
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
    public void listManagedSpaces() throws TimeoutException, InterruptedException {
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
    public void listManagedSpacesFilterByApplicationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.when(
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
    public void listManagedSpacesFilterByDeveloperId() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(Mono.when(
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
    public void listManagedSpacesFilterByName() throws TimeoutException, InterruptedException {
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
    public void listManagedSpacesFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateManagedSpace(this.cloudFoundryClient, spaceId, userId))
                .then(Mono.just(organizationId))))
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
    public void listOrganizations() throws TimeoutException, InterruptedException {
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
    public void listOrganizationsFilterByAuditorId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(Mono.when(
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
    public void listOrganizationsFilterByBillingManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(Mono.when(
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
    public void listOrganizationsFilterByManagerId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateUser(this.cloudFoundryClient, userId)
                .then(Mono.when(
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
    public void listOrganizationsFilterByName() throws TimeoutException, InterruptedException {
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
    public void listOrganizationsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateOrganization(this.cloudFoundryClient, organizationId, userId))
                .then(Mono.just(spaceId))))
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
    public void listOrganizationsFilterByStatus() throws TimeoutException, InterruptedException {
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
    public void listSpaces() throws TimeoutException, InterruptedException {
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
    public void listSpacesFilterByApplicationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.when(
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
    public void listSpacesFilterByDeveloperId() throws TimeoutException, InterruptedException {
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
    public void listSpacesFilterByName() throws TimeoutException, InterruptedException {
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
    public void listSpacesFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId))
                .then(Mono.just(organizationId))))
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
    public void removeAuditedOrganization() throws TimeoutException, InterruptedException {
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
    public void removeAuditedSpace() throws TimeoutException, InterruptedException {
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
    public void removeBillingManagedOrganization() throws TimeoutException, InterruptedException {
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
    public void removeManagedOrganization() throws TimeoutException, InterruptedException {
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
    public void removeManagedSpace() throws TimeoutException, InterruptedException {
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
    public void removeOrganization() throws TimeoutException, InterruptedException {
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
    public void removeSpace() throws TimeoutException, InterruptedException {
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
    public void summary() throws TimeoutException, InterruptedException {
        String userId = this.nameFactory.getUserId();

        requestCreateUser(this.cloudFoundryClient, userId)
            .then(this.cloudFoundryClient.users()
                .summary(SummaryUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(response -> response.getEntity().getOrganizations().size())
            .as(StepVerifier::create)
            .expectNext(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
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
