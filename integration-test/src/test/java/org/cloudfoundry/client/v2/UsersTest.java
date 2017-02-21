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
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.users.AssociateUserManagedSpaceRequest;
import org.cloudfoundry.client.v2.users.AssociateUserSpaceRequest;
import org.cloudfoundry.client.v2.users.AssociateUserSpaceResponse;
import org.cloudfoundry.client.v2.users.CreateUserRequest;
import org.cloudfoundry.client.v2.users.CreateUserResponse;
import org.cloudfoundry.client.v2.users.DeleteUserRequest;
import org.cloudfoundry.client.v2.users.GetUserRequest;
import org.cloudfoundry.client.v2.users.ListUserSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.RemoveUserSpaceRequest;
import org.cloudfoundry.client.v2.users.SummaryUserRequest;
import org.cloudfoundry.client.v2.users.SummaryUserResponse;
import org.cloudfoundry.client.v2.users.UpdateUserRequest;
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
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class UsersTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/646
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/646")
    @Test
    public void associateAuditedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/647
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/647")
    @Test
    public void associateAuditedSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/648
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/648")
    @Test
    public void associateBillingManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/649
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/649")
    @Test
    public void associateManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    @Test
    public void associateManagedSpace() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(this.cloudFoundryClient.users()
                    .associateManagedSpace(AssociateUserManagedSpaceRequest.builder()
                        .managedSpaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getManagedSpaces())
            .filter(space -> spaceName.equals(space.getEntity().getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/651
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/651")
    @Test
    public void associateOrganization() throws TimeoutException, InterruptedException {
        //
    }

    @Test
    public void associateSpace() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(this.cloudFoundryClient.users()
                    .associateSpace(AssociateUserSpaceRequest.builder()
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getSpaces())
            .filter(space -> spaceName.equals(space.getEntity().getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> this.cloudFoundryClient.users()
                .create(CreateUserRequest.builder()
                    .defaultSpaceId(spaceId)
                    .uaaId(userId)
                    .build())
                .then(Mono.just(spaceId)))
            .flatMap(ignore -> requestListUsers(this.cloudFoundryClient))
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
                .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, job)))
            .flatMap(ignore -> requestListUsers(this.cloudFoundryClient))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectNextCount(0)
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
            .flatMap(ignore -> requestListUsers(this.cloudFoundryClient))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .map(ResourceUtils::getId)
            .as(StepVerifier::create)
            .expectNextCount(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
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
            .flatMap(ignore -> PaginationUtils
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

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/655
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/655")
    @Test
    public void listAuditedOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/656
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/656")
    @Test
    public void listAuditedSpaces() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/657
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/657")
    @Test
    public void listBillingManagedOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/651
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/651")
    @Test
    public void listFilterByOrganization() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .then(function((organizationId, spaceId) -> Mono.when(
                Mono.just(organizationId),
                requestCreateUser(this.cloudFoundryClient, spaceId, userId))
            ))
            .flatMap(function((organizationId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .list(ListUsersRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build()))))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/652
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/652")
    @Test
    public void listFilterBySpace() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .flatMap(ignore -> PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                        .list(ListUsersRequest.builder()
                            .page(page)
                            .spaceId(spaceId)
                            .build()))))
            .filter(resource -> userId.equals(resource.getMetadata().getId()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/658
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/658")
    @Test
    public void listManagedOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/659
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/659")
    @Test
    public void listManagedSpaces() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/660
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/660")
    @Test
    public void listOrganizations() throws TimeoutException, InterruptedException {
        //
    }

    @Test
    public void listSpaces() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(ignore -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
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
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> Mono.when(
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                    .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            )
            .flatMap(function((applicationId, ignore) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .applicationId(applicationId)
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
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(ignore -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .developerId(userId)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByName() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(ignore -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .name(spaceName)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .then(function((organizationId, spaceId) -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId))
                .map(ignore -> organizationId)))
            .flatMap(organizationId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.users()
                    .listSpaces(ListUserSpacesRequest.builder()
                        .organizationId(organizationId)
                        .userId(userId)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/662
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/662")
    @Test
    public void removeAuditedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/663
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/663")
    @Test
    public void removeAuditedSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/664
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/664")
    @Test
    public void removeBillingManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/665
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/665")
    @Test
    public void removeManagedOrganization() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/666
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/666")
    @Test
    public void removeManagedSpace() throws TimeoutException, InterruptedException {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/667
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/667")
    @Test
    public void removeOrganization() throws TimeoutException, InterruptedException {
        //
    }

    @Test
    public void removeSpace() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, spaceId, userId)
                .then(requestAssociateSpace(this.cloudFoundryClient, spaceId, userId))
                .then(this.cloudFoundryClient.users()
                    .removeSpace(RemoveUserSpaceRequest.builder()
                        .spaceId(spaceId)
                        .userId(userId)
                        .build())))
            .then(requestSummaryUser(this.cloudFoundryClient, userId))
            .flatMapIterable(response -> response.getEntity().getSpaces())
            .filter(space -> spaceName.equals(space.getEntity().getName()))
            .as(StepVerifier::create)
            .expectNextCount(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Consider improving test when associate spaces/organizations is available
    @Test
    public void summary() throws TimeoutException, InterruptedException {
        String userId = this.nameFactory.getUserId();

        requestCreateUser(this.cloudFoundryClient, userId)
            .then(this.cloudFoundryClient.users()
                .summary(SummaryUserRequest.builder()
                    .userId(userId)
                    .build()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String spaceName = this.nameFactory.getSpaceName();
        String userId = this.nameFactory.getUserId();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> requestCreateUser(this.cloudFoundryClient, userId)
                .map(ignore -> spaceId))
            .then(spaceId -> this.cloudFoundryClient.users()
                .update(UpdateUserRequest.builder()
                    .defaultSpaceId(spaceId)
                    .userId(userId)
                    .build())
                .map(ignore -> spaceId))
            .flatMap(spaceId -> requestListUsers(this.cloudFoundryClient)
                .filter(resource -> spaceId.equals(resource.getEntity().getDefaultSpaceId())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
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
