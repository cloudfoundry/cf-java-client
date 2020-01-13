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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.operations.organizations.CreateOrganizationRequest;
import org.cloudfoundry.operations.spaces.CreateSpaceRequest;
import org.cloudfoundry.operations.useradmin.CreateUserRequest;
import org.cloudfoundry.operations.useradmin.DeleteUserRequest;
import org.cloudfoundry.operations.useradmin.ListOrganizationUsersRequest;
import org.cloudfoundry.operations.useradmin.ListSpaceUsersRequest;
import org.cloudfoundry.operations.useradmin.OrganizationRole;
import org.cloudfoundry.operations.useradmin.OrganizationUsers;
import org.cloudfoundry.operations.useradmin.SetOrganizationRoleRequest;
import org.cloudfoundry.operations.useradmin.SetSpaceRoleRequest;
import org.cloudfoundry.operations.useradmin.SpaceRole;
import org.cloudfoundry.operations.useradmin.SpaceUsers;
import org.cloudfoundry.operations.useradmin.UnsetOrganizationRoleRequest;
import org.cloudfoundry.operations.useradmin.UnsetSpaceRoleRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public final class UserAdminTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void create() {
        String username = this.nameFactory.getUserName();

        this.cloudFoundryOperations.userAdmin()
            .create(CreateUserRequest.builder()
                .password("test-password")
                .username(username)
                .build())
            .thenMany(requestListUsers(this.cloudFoundryClient))
            .filter(response -> username.equals(ResourceUtils.getEntity(response).getUsername()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createDuplicate() {
        String username = this.nameFactory.getUserName();

        createUser(this.cloudFoundryOperations, username)
            .then(createUser(this.cloudFoundryOperations, username))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("User %s already exists", username))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String username = this.nameFactory.getUserName();

        createUser(this.cloudFoundryOperations, username)
            .then(this.cloudFoundryOperations.userAdmin()
                .delete(DeleteUserRequest.builder()
                    .username(username)
                    .build()))
            .thenMany(requestListUsers(this.cloudFoundryClient))
            .filter(response -> username.equals(ResourceUtils.getEntity(response).getUsername()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteNotFound() {
        this.cloudFoundryOperations.userAdmin()
            .delete(DeleteUserRequest.builder()
                .username("not-found")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("User not-found does not exist"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationUsers() {
        String organizationName = this.nameFactory.getOrganizationName();
        String username = this.nameFactory.getUserName();

        Mono.when(
            createUser(this.cloudFoundryOperations, username),
            createOrganization(this.cloudFoundryOperations, organizationName)
        )
            .then(setOrganizationRole(this.cloudFoundryOperations, organizationName, OrganizationRole.BILLING_MANAGER, username))
            .then(this.cloudFoundryOperations.userAdmin()
                .listOrganizationUsers(ListOrganizationUsersRequest.builder()
                    .organizationName(organizationName)
                    .build()))
            .flatMapIterable(OrganizationUsers::getBillingManagers)
            .as(StepVerifier::create)
            .expectNext(username)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpaceUsers() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String username = this.nameFactory.getUserName();

        Mono.when(
            createUser(this.cloudFoundryOperations, username),
            createOrganization(this.cloudFoundryOperations, organizationName)
        )
            .then(createSpace(this.cloudFoundryOperations, organizationName, spaceName))
            .then(setSpaceRole(this.cloudFoundryOperations, organizationName, spaceName, SpaceRole.AUDITOR, username))
            .then(this.cloudFoundryOperations.userAdmin()
                .listSpaceUsers(ListSpaceUsersRequest.builder()
                    .organizationName(organizationName)
                    .spaceName(spaceName)
                    .build()))
            .flatMapIterable(SpaceUsers::getAuditors)
            .as(StepVerifier::create)
            .expectNext(username)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setOrganizationUser() {
        String organizationName = this.nameFactory.getOrganizationName();
        String username = this.nameFactory.getUserName();

        Mono.when(
            createUser(this.cloudFoundryOperations, username),
            createOrganization(this.cloudFoundryOperations, organizationName)
        )
            .then(this.cloudFoundryOperations.userAdmin()
                .setOrganizationRole(SetOrganizationRoleRequest.builder()
                    .organizationName(organizationName)
                    .organizationRole(OrganizationRole.AUDITOR)
                    .username(username)
                    .build()))
            .thenMany(getOrganizationId(this.cloudFoundryClient, organizationName))
            .flatMap(organizationId -> requestListOrganizationAuditors(this.cloudFoundryClient, organizationId))
            .map(resource -> ResourceUtils.getEntity(resource).getUsername())
            .as(StepVerifier::create)
            .expectNext(username)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setSpaceUser() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String username = this.nameFactory.getUserName();

        Mono.when(
            createUser(this.cloudFoundryOperations, username),
            createOrganization(this.cloudFoundryOperations, organizationName)
        )
            .then(createSpace(this.cloudFoundryOperations, organizationName, spaceName))
            .then(this.cloudFoundryOperations.userAdmin()
                .setSpaceRole(SetSpaceRoleRequest.builder()
                    .organizationName(organizationName)
                    .spaceName(spaceName)
                    .spaceRole(SpaceRole.AUDITOR)
                    .username(username)
                    .build()))
            .thenMany(listSpaceUsers(this.cloudFoundryOperations, organizationName, spaceName))
            .flatMapIterable(SpaceUsers::getAuditors)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unsetOrganizationUser() {
        String organizationName = this.nameFactory.getOrganizationName();
        String username = this.nameFactory.getUserName();

        Mono.when(
            createUser(this.cloudFoundryOperations, username),
            createOrganization(this.cloudFoundryOperations, organizationName)
        )
            .then(setOrganizationRole(this.cloudFoundryOperations, organizationName, OrganizationRole.MANAGER, username))
            .then(this.cloudFoundryOperations.userAdmin()
                .unsetOrganizationRole(UnsetOrganizationRoleRequest.builder()
                    .organizationName(organizationName)
                    .organizationRole(OrganizationRole.MANAGER)
                    .username(username)
                    .build()))
            .thenMany(listOrganizationUsers(this.cloudFoundryOperations, organizationName))
            .flatMapIterable(OrganizationUsers::getManagers)
            .filter(username::equals)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unsetSpaceUser() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String username = this.nameFactory.getUserName();

        Mono.when(
            createUser(this.cloudFoundryOperations, username),
            createOrganization(this.cloudFoundryOperations, organizationName)
        )
            .then(createSpace(this.cloudFoundryOperations, organizationName, spaceName))
            .then(setSpaceRole(this.cloudFoundryOperations, organizationName, spaceName, SpaceRole.MANAGER, username))
            .then(this.cloudFoundryOperations.userAdmin()
                .unsetSpaceRole(UnsetSpaceRoleRequest.builder()
                    .organizationName(organizationName)
                    .spaceName(spaceName)
                    .spaceRole(SpaceRole.MANAGER)
                    .username(username)
                    .build()))
            .thenMany(listSpaceUsers(this.cloudFoundryOperations, organizationName, spaceName))
            .flatMapIterable(SpaceUsers::getManagers)
            .filter(username::equals)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> createOrganization(CloudFoundryOperations cloudFoundryOperations, String organizationName) {
        return cloudFoundryOperations.organizations()
            .create(CreateOrganizationRequest.builder()
                .organizationName(organizationName)
                .build());
    }

    private static Mono<Void> createSpace(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName) {
        return cloudFoundryOperations.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Void> createUser(CloudFoundryOperations cloudFoundryOperations, String username) {
        return cloudFoundryOperations.userAdmin()
            .create(CreateUserRequest.builder()
                .password("test-password")
                .username(username)
                .build());
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .page(page)
                .build()))
            .filter(r -> organizationName.equals(ResourceUtils.getEntity(r).getName()))
            .map(ResourceUtils::getId)
            .singleOrEmpty();
    }

    private static Mono<OrganizationUsers> listOrganizationUsers(CloudFoundryOperations cloudFoundryOperations, String organizationName) {
        return cloudFoundryOperations.userAdmin()
            .listOrganizationUsers(ListOrganizationUsersRequest.builder()
                .organizationName(organizationName)
                .build());
    }

    private static Mono<SpaceUsers> listSpaceUsers(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName) {
        return cloudFoundryOperations.userAdmin()
            .listSpaceUsers(ListSpaceUsersRequest.builder()
                .organizationName(organizationName)
                .spaceName(spaceName)
                .build());
    }

    private static Flux<UserResource> requestListOrganizationAuditors(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.organizations()
            .listAuditors(ListOrganizationAuditorsRequest.builder()
                .organizationId(organizationId)
                .page(page)
                .build()));
    }

    private static Flux<UserResource> requestListUsers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.users()
            .list(ListUsersRequest.builder()
                .page(page)
                .build()));
    }

    private static Mono<Void> setOrganizationRole(CloudFoundryOperations cloudFoundryOperations, String organizationName, OrganizationRole organizationRole, String username) {
        return cloudFoundryOperations.userAdmin()
            .setOrganizationRole(SetOrganizationRoleRequest.builder()
                .organizationName(organizationName)
                .organizationRole(organizationRole)
                .username(username)
                .build());
    }

    private static Mono<Void> setSpaceRole(CloudFoundryOperations cloudFoundryOperations, String organizationName, String spaceName, SpaceRole spaceRole, String username) {
        return cloudFoundryOperations.userAdmin()
            .setSpaceRole(SetSpaceRoleRequest.builder()
                .organizationName(organizationName)
                .spaceName(spaceName)
                .spaceRole(spaceRole)
                .username(username)
                .build());
    }

}
