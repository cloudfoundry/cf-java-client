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

package org.cloudfoundry.operations.useradmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersResponse;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.users.DeleteUserResponse;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.UaaException;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.User;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultUserAdminTest extends AbstractOperationsTest {

    private final DefaultUserAdmin userAdmin = new DefaultUserAdmin(Mono.just(this.cloudFoundryClient), Mono.just(this.uaaClient));

    @Test
    public void createUaaUserExists() {
        requestCreateUaaUserAlreadyExists(this.uaaClient);
        requestCreateUser(this.cloudFoundryClient);

        this.userAdmin
            .create(CreateUserRequest.builder()
                .username("test-username")
                .password("test-password")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("User test-username already exists")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createWithPassword() {
        requestCreateUaaUser(this.uaaClient);
        requestCreateUser(this.cloudFoundryClient);

        this.userAdmin
            .create(CreateUserRequest.builder()
                .username("test-username")
                .password("test-password")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        requestListUser(this.uaaClient);
        requestDeleteUser(this.cloudFoundryClient);
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestDeleteUaaUser(this.uaaClient);

        StepVerifier.withVirtualTime(() -> this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteFailure() {
        requestListUser(this.uaaClient);
        requestDeleteUser(this.cloudFoundryClient);
        requestJobFailure(this.cloudFoundryClient, "test-job-entity-id");
        requestDeleteUaaUser(this.uaaClient);

        StepVerifier.withVirtualTime(() -> this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-error-details-errorCode(1): test-error-details-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteNoCfUser() {
        requestListUser(this.uaaClient);
        requestDeleteUserEmpty(this.cloudFoundryClient);
        requestDeleteUaaUser(this.uaaClient);

        this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteNoUaaUser() {
        requestListUserEmpty(this.uaaClient);

        this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("User test-username does not exist")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listOrganizationUsersAllFound() {
        requestOrganization(this.cloudFoundryClient);
        requestListOrganizationAuditors(this.cloudFoundryClient);
        requestListOrganizationBillingManagers(this.cloudFoundryClient);
        requestListOrganizationManagers(this.cloudFoundryClient);

        this.userAdmin
            .listOrganizationUsers(ListOrganizationUsersRequest.builder()
                .organizationName("test-organization-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(OrganizationUsers.builder()
                .auditor("test-auditor-username")
                .billingManager("test-billing-manager-username")
                .manager("test-manager-username")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listOrganizationUsersNoneFound() {
        requestOrganization(this.cloudFoundryClient);
        requestListOrganizationAuditorsEmpty(this.cloudFoundryClient);
        requestListOrganizationBillingManagersEmpty(this.cloudFoundryClient);
        requestListOrganizationManagersEmpty(this.cloudFoundryClient);

        this.userAdmin
            .listOrganizationUsers(ListOrganizationUsersRequest.builder()
                .organizationName("test-organization-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(OrganizationUsers.builder()
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listOrganizationUsersOrganizationNotFound() {
        requestOrganizationEmpty(this.cloudFoundryClient);

        this.userAdmin
            .listOrganizationUsers(ListOrganizationUsersRequest.builder()
                .organizationName("unknown-organization-name")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Organization unknown-organization-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpaceUsersAllFound() {
        requestOrganization(this.cloudFoundryClient);
        requestSpace(this.cloudFoundryClient);
        requestListSpaceAuditors(this.cloudFoundryClient);
        requestListSpaceDevelopers(this.cloudFoundryClient);
        requestListSpaceManagers(this.cloudFoundryClient);

        this.userAdmin
            .listSpaceUsers(ListSpaceUsersRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(SpaceUsers.builder()
                .auditor("test-auditor-username")
                .developer("test-developer-username")
                .manager("test-manager-username")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpaceUsersNoneFound() {
        requestOrganization(this.cloudFoundryClient);
        requestSpace(this.cloudFoundryClient);
        requestListSpaceAuditorsEmpty(this.cloudFoundryClient);
        requestListSpaceDevelopersEmpty(this.cloudFoundryClient);
        requestListSpaceManagersEmpty(this.cloudFoundryClient);

        this.userAdmin
            .listSpaceUsers(ListSpaceUsersRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(SpaceUsers.builder()
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpaceUsersNullsFound() {
        requestOrganization(this.cloudFoundryClient);
        requestSpace(this.cloudFoundryClient);
        requestListSpaceAuditorsNulls(this.cloudFoundryClient);
        requestListSpaceDevelopersNulls(this.cloudFoundryClient);
        requestListSpaceManagersNulls(this.cloudFoundryClient);

        this.userAdmin
            .listSpaceUsers(ListSpaceUsersRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(SpaceUsers.builder()
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpaceUsersOrganizationNotFound() {
        requestOrganizationEmpty(this.cloudFoundryClient);

        this.userAdmin
            .listSpaceUsers(ListSpaceUsersRequest.builder()
                .organizationName("unknown-organization-name")
                .spaceName("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Organization unknown-organization-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpaceUsersSpaceNotFound() {
        requestOrganization(this.cloudFoundryClient);
        requestSpaceEmpty(this.cloudFoundryClient);

        this.userAdmin
            .listSpaceUsers(ListSpaceUsersRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("unknown-space-name")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Space unknown-space-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setOrganizationRole() {
        requestGetFeatureFlag(this.cloudFoundryClient, "set_roles_by_username", true);
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, "test-organization-id", "test-username");
        requestAssociateOrganizationAuditorByUsername(this.cloudFoundryClient, "test-organization-id", "test-username");

        this.userAdmin
            .setOrganizationRole(SetOrganizationRoleRequest.builder()
                .organizationName("test-organization-name")
                .organizationRole(OrganizationRole.AUDITOR)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setOrganizationRoleFeatureDisabled() {
        requestGetFeatureFlag(this.cloudFoundryClient, "set_roles_by_username", false);

        this.userAdmin
            .setOrganizationRole(SetOrganizationRoleRequest.builder()
                .organizationName("test-organization-name")
                .organizationRole(OrganizationRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Setting roles by username is not enabled")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setOrganizationRoleInvalidOrganization() {
        requestGetFeatureFlag(this.cloudFoundryClient, "set_roles_by_username", true);
        requestListOrganizationsEmpty(this.cloudFoundryClient, "test-organization-name");

        this.userAdmin
            .setOrganizationRole(SetOrganizationRoleRequest.builder()
                .organizationName("test-organization-name")
                .organizationRole(OrganizationRole.BILLING_MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Organization test-organization-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setSpaceRole() {
        requestGetFeatureFlag(this.cloudFoundryClient, "set_roles_by_username", true);
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestListOrganizationSpaces(this.cloudFoundryClient, "test-organization-id", "test-space-name");
        requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, "test-organization-id", "test-username");
        requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", "test-username");

        this.userAdmin
            .setSpaceRole(SetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setSpaceRoleFeatureDisabled() {
        requestGetFeatureFlag(this.cloudFoundryClient, "set_roles_by_username", false);

        this.userAdmin
            .setSpaceRole(SetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Setting roles by username is not enabled")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setSpaceRoleInvalidOrganization() {
        requestGetFeatureFlag(this.cloudFoundryClient, "set_roles_by_username", true);
        requestListOrganizationsEmpty(this.cloudFoundryClient, "test-organization-name");

        this.userAdmin
            .setSpaceRole(SetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Organization test-organization-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setSpaceRoleInvalidSpace() {
        requestGetFeatureFlag(this.cloudFoundryClient, "set_roles_by_username", true);
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestListOrganizationSpacesEmpty(this.cloudFoundryClient, "test-organization-id", "test-space-name");

        this.userAdmin
            .setSpaceRole(SetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Space test-space-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetOrganizationRole() {
        requestGetFeatureFlag(this.cloudFoundryClient, "unset_roles_by_username", true);
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestRemoveOrganizationManagerByUsername(this.cloudFoundryClient, "test-organization-id", "test-username");

        this.userAdmin
            .unsetOrganizationRole(UnsetOrganizationRoleRequest.builder()
                .organizationName("test-organization-name")
                .organizationRole(OrganizationRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetOrganizationRoleFeatureDisabled() {
        requestGetFeatureFlag(this.cloudFoundryClient, "unset_roles_by_username", false);

        this.userAdmin
            .unsetOrganizationRole(UnsetOrganizationRoleRequest.builder()
                .organizationName("test-organization-name")
                .organizationRole(OrganizationRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Unsetting roles by username is not enabled")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetOrganizationRoleInvalidOrganization() {
        requestGetFeatureFlag(this.cloudFoundryClient, "unset_roles_by_username", true);
        requestListOrganizationsEmpty(this.cloudFoundryClient, "test-organization-name");

        this.userAdmin
            .unsetOrganizationRole(UnsetOrganizationRoleRequest.builder()
                .organizationName("test-organization-name")
                .organizationRole(OrganizationRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Organization test-organization-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetSpaceRole() {
        requestGetFeatureFlag(this.cloudFoundryClient, "unset_roles_by_username", true);
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestListOrganizationSpaces(this.cloudFoundryClient, "test-organization-id", "test-space-name");
        requestRemoveSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", "test-username");

        this.userAdmin
            .unsetSpaceRole(UnsetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetSpaceRoleFeatureDisabled() {
        requestGetFeatureFlag(this.cloudFoundryClient, "unset_roles_by_username", false);

        this.userAdmin
            .unsetSpaceRole(UnsetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Unsetting roles by username is not enabled")
            .verify(Duration.ofSeconds(5));
    }


    @Test
    public void unsetSpaceRoleInvalidOrganization() {
        requestGetFeatureFlag(this.cloudFoundryClient, "unset_roles_by_username", true);
        requestListOrganizationsEmpty(this.cloudFoundryClient, "test-organization-name");

        this.userAdmin
            .unsetSpaceRole(UnsetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Organization test-organization-name not found")
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetSpaceRoleInvalidSpace() {
        requestGetFeatureFlag(this.cloudFoundryClient, "unset_roles_by_username", true);
        requestListOrganizations(this.cloudFoundryClient, "test-organization-name");
        requestListOrganizationSpacesEmpty(this.cloudFoundryClient, "test-organization-id", "test-space-name");

        this.userAdmin
            .unsetSpaceRole(UnsetSpaceRoleRequest.builder()
                .organizationName("test-organization-name")
                .spaceName("test-space-name")
                .spaceRole(SpaceRole.MANAGER)
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("Space test-space-name not found")
            .verify(Duration.ofSeconds(5));
    }

    private static void requestAssociateOrganizationAuditorByUsername(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        when(cloudFoundryClient.organizations()
            .associateAuditorByUsername(AssociateOrganizationAuditorByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateOrganizationAuditorByUsernameResponse.builder(), "associate-auditor-")
                    .build()));
    }

    private static void requestAssociateOrganizationUserByUsername(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        when(cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateOrganizationUserByUsernameResponse.builder(), "associate-user-")
                    .build()));
    }

    private static void requestAssociateSpaceManagerByUsername(CloudFoundryClient cloudFoundryClient, String spaceId, String username) {
        when(cloudFoundryClient.spaces()
            .associateManagerByUsername(AssociateSpaceManagerByUsernameRequest.builder()
                .spaceId(spaceId)
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateSpaceManagerByUsernameResponse.builder(), "associate-manager-")
                    .build()));
    }

    private static void requestCreateUaaUser(UaaClient uaaClient) {
        when(uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value("test-username")
                    .build())
                .name(Name.builder()
                    .familyName("test-username")
                    .givenName("test-username")
                    .build())
                .password("test-password")
                .userName("test-username")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.uaa.users.CreateUserResponse.builder()
                    .id("test-user-id")
                    .active(true)
                    .meta(Meta.builder()
                        .created("test-created")
                        .lastModified("test-last-modified")
                        .version(1)
                        .build())
                    .name(Name.builder().build())
                    .origin("test-origin")
                    .passwordLastModified("test-password-last-modified")
                    .userName("test-username")
                    .verified(false)
                    .zoneId("test-zone-id")
                    .build()));
    }

    private static void requestCreateUaaUserAlreadyExists(UaaClient uaaClient) {
        when(uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value("test-username")
                    .build())
                .name(Name.builder()
                    .familyName("test-username")
                    .givenName("test-username")
                    .build())
                .password("test-password")
                .userName("test-username")
                .build()))
            .thenReturn(Mono.error(new UaaException(409, "test-error", "test-error-description")));
    }

    private static void requestCreateUser(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.users()
            .create(org.cloudfoundry.client.v2.users.CreateUserRequest.builder()
                .uaaId("test-user-id")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.client.v2.users.CreateUserResponse.builder()
                    .build()));
    }

    private static void requestDeleteUaaUser(UaaClient uaaClient) {
        when(uaaClient.users()
            .delete(org.cloudfoundry.uaa.users.DeleteUserRequest.builder()
                .userId("test-user-id")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.uaa.users.DeleteUserResponse.builder()
                    .id("test-user-id")
                    .active(true)
                    .meta(Meta.builder()
                        .created("test-created")
                        .lastModified("test-last-modified")
                        .version(1)
                        .build())
                    .name(Name.builder().build())
                    .origin("test-origin")
                    .passwordLastModified("test-password-last-modified")
                    .userName("test-username")
                    .verified(false)
                    .zoneId("test-zone-id")
                    .build()));
    }

    private static void requestDeleteUser(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.users()
            .delete(org.cloudfoundry.client.v2.users.DeleteUserRequest.builder()
                .async(true)
                .userId("test-user-id")
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteUserResponse.builder())
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));
    }

    private static void requestDeleteUserEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.users()
            .delete(org.cloudfoundry.client.v2.users.DeleteUserRequest.builder()
                .async(true)
                .userId("test-user-id")
                .build()))
            .thenReturn(Mono.error(new ClientV2Exception(404, 404, "test-description", "test-error-code")));
    }

    private static void requestGetFeatureFlag(CloudFoundryClient cloudFoundryClient, String featureFlag, Boolean enabled) {
        when(cloudFoundryClient.featureFlags()
            .get(GetFeatureFlagRequest.builder()
                .name(featureFlag)
                .build()))
            .thenReturn(Mono.just(GetFeatureFlagResponse.builder()
                .enabled(enabled)
                .build()));
    }

    private static void requestJobFailure(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .errorDetails(fill(ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestListOrganizationAuditors(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listAuditors(ListOrganizationAuditorsRequest.builder()
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationAuditorsResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username("test-auditor-username")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListOrganizationAuditorsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listAuditors(ListOrganizationAuditorsRequest.builder()
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationAuditorsResponse.builder())
                    .build()));
    }

    private static void requestListOrganizationBillingManagers(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listBillingManagers(ListOrganizationBillingManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationBillingManagersResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username("test-billing-manager-username")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListOrganizationBillingManagersEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listBillingManagers(ListOrganizationBillingManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationBillingManagersResponse.builder())
                    .build()));
    }

    private static void requestListOrganizationManagers(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listManagers(ListOrganizationManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationManagersResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username("test-manager-username")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListOrganizationManagersEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listManagers(ListOrganizationManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationManagersResponse.builder())
                    .build()));
    }

    private static void requestListOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
                        .entity(fill(SpaceEntity.builder(), "space-entity-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListOrganizationSpacesEmpty(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .build()));
    }

    private static void requestListOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organizationName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .entity(fill(OrganizationEntity.builder(), "organization-entity-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListOrganizationsEmpty(CloudFoundryClient cloudFoundryClient, String organizationName) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organizationName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .build()));
    }

    private static void requestListSpaceAuditors(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listAuditors(ListSpaceAuditorsRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceAuditorsResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username("test-auditor-username")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceAuditorsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listAuditors(ListSpaceAuditorsRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceAuditorsResponse.builder())
                    .build()));
    }

    private static void requestListSpaceAuditorsNulls(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listAuditors(ListSpaceAuditorsRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceAuditorsResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username(null)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceDevelopers(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listDevelopers(ListSpaceDevelopersRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceDevelopersResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username("test-developer-username")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceDevelopersEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listDevelopers(ListSpaceDevelopersRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceDevelopersResponse.builder())
                    .build()));
    }

    private static void requestListSpaceDevelopersNulls(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listDevelopers(ListSpaceDevelopersRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceDevelopersResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username(null)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceManagers(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listManagers(ListSpaceManagersRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceManagersResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username("test-manager-username")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListSpaceManagersEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listManagers(ListSpaceManagersRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceManagersResponse.builder())
                    .build()));
    }

    private static void requestListSpaceManagersNulls(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.spaces()
            .listManagers(ListSpaceManagersRequest.builder()
                .spaceId("test-space-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceManagersResponse.builder())
                    .resource(fill(UserResource.builder())
                        .entity(fill(UserEntity.builder())
                            .username(null)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListUser(UaaClient uaaClient) {
        when(uaaClient.users()
            .list(org.cloudfoundry.uaa.users.ListUsersRequest.builder()
                .filter("userName eq \"test-username\"")
                .startIndex(1)
                .build()))
            .thenReturn(Mono
                .just(fill(org.cloudfoundry.uaa.users.ListUsersResponse.builder())
                    .resource(User.builder()
                        .id("test-user-id")
                        .active(true)
                        .meta(Meta.builder()
                            .created("test-created")
                            .lastModified("test-last-modified")
                            .version(1)
                            .build())
                        .name(Name.builder().build())
                        .origin("test-origin")
                        .passwordLastModified("test-password-last-modified")
                        .userName("test-username")
                        .verified(false)
                        .zoneId("test-zone-id")
                        .build())
                    .build()));
    }

    private static void requestListUserEmpty(UaaClient uaaClient) {
        when(uaaClient.users()
            .list(org.cloudfoundry.uaa.users.ListUsersRequest.builder()
                .filter("userName eq \"test-username\"")
                .startIndex(1)
                .build()))
            .thenReturn(Mono
                .just(fill(org.cloudfoundry.uaa.users.ListUsersResponse.builder())
                    .build()));
    }

    private static void requestOrganization(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name("test-organization-name")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .metadata(fill(Metadata.builder())
                            .id("test-organization-id")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestOrganizationEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name("unknown-organization-name")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .build()));
    }

    private static void requestRemoveOrganizationManagerByUsername(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        when(cloudFoundryClient.organizations()
            .removeManagerByUsername(RemoveOrganizationManagerByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestRemoveSpaceManagerByUsername(CloudFoundryClient cloudFoundryClient, String spaceId, String username) {
        when(cloudFoundryClient.spaces()
            .removeManagerByUsername(RemoveSpaceManagerByUsernameRequest.builder()
                .spaceId(spaceId)
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(RemoveSpaceManagerByUsernameResponse.builder(), "associate-manager-")
                    .build()));
    }

    private static void requestSpace(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .name("test-space-name")
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
                        .build())
                    .build()));
    }

    private static void requestSpaceEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .name("unknown-space-name")
                .organizationId("test-organization-id")
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .build()));
    }

}
