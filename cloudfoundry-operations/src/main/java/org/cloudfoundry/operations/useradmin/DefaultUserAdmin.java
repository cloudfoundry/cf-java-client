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
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.UaaException;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class DefaultUserAdmin implements UserAdmin {

    private static final String SET_ROLES_BY_USERNAME_FEATURE_FLAG = "set_roles_by_username";

    private static final String UNSET_ROLES_BY_USERNAME_FEATURE_FLAG = "unset_roles_by_username";

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<UaaClient> uaaClient;

    public DefaultUserAdmin(Mono<CloudFoundryClient> cloudFoundryClient, Mono<UaaClient> uaaClient) {
        this.uaaClient = uaaClient;
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<Void> create(CreateUserRequest request) {
        return Mono.zip(this.cloudFoundryClient, this.uaaClient)
            .flatMap(function((cloudFoundryClient, uaaClient) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                createUaaUserId(uaaClient, request))))
            .flatMap(function(DefaultUserAdmin::requestCreateUser))
            .then()
            .transform(OperationsLogging.log("Create User"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteUserRequest request) {
        return Mono.zip(this.cloudFoundryClient, this.uaaClient)
            .flatMap(function((cloudFoundryClient, uaaClient) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                Mono.just(uaaClient),
                getUserId(uaaClient, request.getUsername()))))
            .flatMap(function((cloudFoundryClient, uaaClient, userId) -> Mono.zip(
                deleteUser(cloudFoundryClient, userId),
                requestDeleteUaaUser(uaaClient, userId))))
            .then()
            .transform(OperationsLogging.log("Delete User"))
            .checkpoint();
    }

    @Override
    public Mono<OrganizationUsers> listOrganizationUsers(ListOrganizationUsersRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getOrganizationName())
            ))
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono.zip(
                listOrganizationAuditorNames(cloudFoundryClient, organizationId),
                listOrganizationBillingManagerNames(cloudFoundryClient, organizationId),
                listOrganizationManagerNames(cloudFoundryClient, organizationId)
            )))
            .flatMap(function(this::toOrganizationUsers))
            .transform(OperationsLogging.log("List Organization Users"))
            .checkpoint();
    }

    @Override
    public Mono<SpaceUsers> listSpaceUsers(ListSpaceUsersRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getOrganizationName())
            ))
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getSpaceId(cloudFoundryClient, organizationId, request.getSpaceName()))
            ))
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.zip(
                listSpaceAuditorNames(cloudFoundryClient, spaceId),
                listSpaceDeveloperNames(cloudFoundryClient, spaceId),
                listSpaceManagerNames(cloudFoundryClient, spaceId)
            )))
            .flatMap(function(this::toSpaceUsers))
            .transform(OperationsLogging.log("List Space Users"))
            .checkpoint();
    }

    @Override
    public Mono<Void> setOrganizationRole(SetOrganizationRoleRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getFeatureFlagEnabled(cloudFoundryClient, SET_ROLES_BY_USERNAME_FEATURE_FLAG)
            ))
            .filter(predicate((cloudFoundryClient, setRolesByUsernameEnabled) -> setRolesByUsernameEnabled))
            .switchIfEmpty(ExceptionUtils.illegalState("Setting roles by username is not enabled"))
            .flatMap(function((cloudFoundryClient, ignore) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getOrganizationName()))
            ))
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono.zip(
                requestAssociateOrganizationUserByUsername(cloudFoundryClient, organizationId, request),
                associateOrganizationRole(cloudFoundryClient, organizationId, request))
            ))
            .transform(OperationsLogging.log("Set User Organization Role"))
            .then();
    }

    @Override
    public Mono<Void> setSpaceRole(SetSpaceRoleRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getFeatureFlagEnabled(cloudFoundryClient, SET_ROLES_BY_USERNAME_FEATURE_FLAG)
            ))
            .filter(predicate((cloudFoundryClient, setRolesByUsernameEnabled) -> setRolesByUsernameEnabled))
            .switchIfEmpty(ExceptionUtils.illegalState("Setting roles by username is not enabled"))
            .flatMap(function((cloudFoundryClient, ignore) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getOrganizationName()))
            ))
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                Mono.just(organizationId),
                getSpaceId(cloudFoundryClient, organizationId, request.getSpaceName())
            )))
            .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                Mono.just(spaceId),
                associateOrganizationRole(cloudFoundryClient, request.getUsername(), organizationId))
            ))
            .flatMap(function((cloudFoundryClient, spaceId, ignore) -> associateSpaceRole(cloudFoundryClient, request, spaceId)))
            .transform(OperationsLogging.log("Set User Space Role"))
            .then();
    }

    @Override
    public Mono<Void> unsetOrganizationRole(UnsetOrganizationRoleRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getFeatureFlagEnabled(cloudFoundryClient, UNSET_ROLES_BY_USERNAME_FEATURE_FLAG)
            ))
            .filter(predicate((cloudFoundryClient, setRolesByUsernameEnabled) -> setRolesByUsernameEnabled))
            .switchIfEmpty(ExceptionUtils.illegalState("Unsetting roles by username is not enabled"))
            .flatMap(function((cloudFoundryClient, ignore) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getOrganizationName()))
            ))
            .flatMap(function((cloudFoundryClient, organizationId) -> removeOrganizationRole(cloudFoundryClient, organizationId, request)))
            .transform(OperationsLogging.log("Unset User Organization Role"))
            .then();
    }

    @Override
    public Mono<Void> unsetSpaceRole(UnsetSpaceRoleRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getFeatureFlagEnabled(cloudFoundryClient, UNSET_ROLES_BY_USERNAME_FEATURE_FLAG)
            ))
            .filter(predicate((cloudFoundryClient, setRolesByUsernameEnabled) -> setRolesByUsernameEnabled))
            .switchIfEmpty(ExceptionUtils.illegalState("Unsetting roles by username is not enabled"))
            .flatMap(function((cloudFoundryClient, ignore) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getOrganizationName()))
            ))
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                getSpaceId(cloudFoundryClient, organizationId, request.getSpaceName())
            )))
            .flatMap(function((cloudFoundryClient, spaceId) -> removeSpaceRole(cloudFoundryClient, request, spaceId)))
            .transform(OperationsLogging.log("Unset User Space Role"))
            .then();
    }

    private static Mono<Void> associateOrganizationRole(CloudFoundryClient cloudFoundryClient, String organizationId, SetOrganizationRoleRequest request) {
        if (OrganizationRole.AUDITOR == request.getOrganizationRole()) {
            return cloudFoundryClient.organizations()
                .associateAuditorByUsername(AssociateOrganizationAuditorByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (OrganizationRole.BILLING_MANAGER == request.getOrganizationRole()) {
            return cloudFoundryClient.organizations()
                .associateBillingManagerByUsername(AssociateOrganizationBillingManagerByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (OrganizationRole.MANAGER == request.getOrganizationRole()) {
            return cloudFoundryClient.organizations()
                .associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        return ExceptionUtils.illegalArgument("Unknown organization role specified");
    }

    private static Mono<AssociateOrganizationUserByUsernameResponse> associateOrganizationRole(CloudFoundryClient cloudFoundryClient, String username, String organizationId) {
        return cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build());
    }

    private static Mono<Void> associateSpaceRole(CloudFoundryClient cloudFoundryClient, SetSpaceRoleRequest request, String spaceId) {
        if (SpaceRole.AUDITOR == request.getSpaceRole()) {
            return cloudFoundryClient.spaces()
                .associateAuditorByUsername(AssociateSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (SpaceRole.DEVELOPER == request.getSpaceRole()) {
            return cloudFoundryClient.spaces()
                .associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (SpaceRole.MANAGER == request.getSpaceRole()) {
            return cloudFoundryClient.spaces()
                .associateManagerByUsername(AssociateSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        return ExceptionUtils.illegalArgument("Unknown space role specified");
    }

    private static Mono<String> createUaaUserId(UaaClient uaaClient, CreateUserRequest request) {
        return requestCreateUaaUser(uaaClient, request)
            .map(CreateUserResponse::getId)
            .onErrorResume(UaaException.class, t -> ExceptionUtils.illegalArgument("User %s already exists", request.getUsername()));
    }

    private static Mono<Void> deleteUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return requestDeleteUser(cloudFoundryClient, userId)
            .onErrorResume(t -> t instanceof ClientV2Exception && ((ClientV2Exception) t).getStatusCode() == 404, t -> Mono.empty())
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, Duration.ofMinutes(5), job));
    }

    private static Mono<Boolean> getFeatureFlagEnabled(CloudFoundryClient cloudFoundryClient, String featureFlag) {
        return requestGetFeatureFlag(cloudFoundryClient, featureFlag)
            .map(GetFeatureFlagResponse::getEnabled);
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestListOrganizations(cloudFoundryClient, organizationName)
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Organization %s not found", organizationName));
    }

    private static Mono<String> getSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestListSpaces(cloudFoundryClient, organizationId, spaceName)
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Space %s not found", spaceName));
    }

    private static Mono<String> getUserId(UaaClient uaaClient, String username) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.users()
                .list(ListUsersRequest.builder()
                    .filter(String.format("userName eq \"%s\"", username))
                    .startIndex(startIndex)
                    .build()))
            .switchIfEmpty(ExceptionUtils.illegalArgument("User %s does not exist", username))
            .single()
            .map(User::getId);
    }

    private static Mono<List<String>> listOrganizationAuditorNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationAuditors(cloudFoundryClient, organizationId)
            .map(resource -> ResourceUtils.getEntity(resource).getUsername())
            .collectList();
    }

    private static Mono<List<String>> listOrganizationBillingManagerNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationBillingManagers(cloudFoundryClient, organizationId)
            .map(resource -> ResourceUtils.getEntity(resource).getUsername())
            .collectList();
    }

    private static Mono<List<String>> listOrganizationManagerNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListOrganizationManagers(cloudFoundryClient, organizationId)
            .map(resource -> ResourceUtils.getEntity(resource).getUsername())
            .collectList();
    }

    private static Mono<List<String>> listSpaceAuditorNames(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceAuditors(cloudFoundryClient, spaceId)
            .filter(resource -> null != ResourceUtils.getEntity(resource).getUsername())
            .map(resource -> ResourceUtils.getEntity(resource).getUsername())
            .collectList();
    }

    private static Mono<List<String>> listSpaceDeveloperNames(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceDevelopers(cloudFoundryClient, spaceId)
            .filter(resource -> null != ResourceUtils.getEntity(resource).getUsername())
            .map(resource -> ResourceUtils.getEntity(resource).getUsername())
            .collectList();
    }

    private static Mono<List<String>> listSpaceManagerNames(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceManagers(cloudFoundryClient, spaceId)
            .filter(resource -> null != ResourceUtils.getEntity(resource).getUsername())
            .map(resource -> ResourceUtils.getEntity(resource).getUsername())
            .collectList();
    }

    private static Mono<Void> removeOrganizationRole(CloudFoundryClient cloudFoundryClient, String organizationId, UnsetOrganizationRoleRequest request) {
        if (OrganizationRole.AUDITOR == request.getOrganizationRole()) {
            return cloudFoundryClient.organizations()
                .removeAuditorByUsername(RemoveOrganizationAuditorByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (OrganizationRole.BILLING_MANAGER == request.getOrganizationRole()) {
            return cloudFoundryClient.organizations()
                .removeBillingManagerByUsername(RemoveOrganizationBillingManagerByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (OrganizationRole.MANAGER == request.getOrganizationRole()) {
            return cloudFoundryClient.organizations()
                .removeManagerByUsername(RemoveOrganizationManagerByUsernameRequest.builder()
                    .organizationId(organizationId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        return ExceptionUtils.illegalArgument("Unknown organization role specified");
    }

    private static Mono<Void> removeSpaceRole(CloudFoundryClient cloudFoundryClient, UnsetSpaceRoleRequest request, String spaceId) {
        if (SpaceRole.AUDITOR == request.getSpaceRole()) {
            return cloudFoundryClient.spaces()
                .removeAuditorByUsername(RemoveSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (SpaceRole.DEVELOPER == request.getSpaceRole()) {
            return cloudFoundryClient.spaces()
                .removeDeveloperByUsername(RemoveSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        if (SpaceRole.MANAGER == request.getSpaceRole()) {
            return cloudFoundryClient.spaces()
                .removeManagerByUsername(RemoveSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(request.getUsername())
                    .build())
                .then();
        }

        return ExceptionUtils.illegalArgument("Unknown space role specified");
    }

    private static Mono<AssociateOrganizationUserByUsernameResponse> requestAssociateOrganizationUserByUsername(CloudFoundryClient cloudFoundryClient, String organizationId,
                                                                                                                SetOrganizationRoleRequest request) {
        return cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(request.getUsername())
                .build());
    }

    private static Mono<CreateUserResponse> requestCreateUaaUser(UaaClient uaaClient, CreateUserRequest request) {
        return uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value(request.getUsername())
                    .build())
                .name(Name.builder()
                    .familyName(request.getUsername())
                    .givenName(request.getUsername())
                    .build())
                .origin(request.getOrigin())
                .password(request.getPassword())
                .userName(request.getUsername())
                .build());
    }

    private static Mono<org.cloudfoundry.client.v2.users.CreateUserResponse> requestCreateUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient.users()
            .create(org.cloudfoundry.client.v2.users.CreateUserRequest.builder()
                .uaaId(userId)
                .build());
    }

    private static Mono<DeleteUserResponse> requestDeleteUaaUser(UaaClient uaaClient, String userId) {
        return uaaClient.users().delete(org.cloudfoundry.uaa.users.DeleteUserRequest.builder()
            .userId(userId)
            .build());
    }

    private static Mono<org.cloudfoundry.client.v2.users.DeleteUserResponse> requestDeleteUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient.users()
            .delete(org.cloudfoundry.client.v2.users.DeleteUserRequest.builder()
                .async(true)
                .userId(userId)
                .build());
    }

    private static Mono<GetFeatureFlagResponse> requestGetFeatureFlag(CloudFoundryClient cloudFoundryClient, String featureFlag) {
        return cloudFoundryClient.featureFlags()
            .get(GetFeatureFlagRequest.builder()
                .name(featureFlag)
                .build());
    }

    private static Flux<UserResource> requestListOrganizationAuditors(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.organizations()
            .listAuditors(ListOrganizationAuditorsRequest.builder()
                .organizationId(organizationId)
                .page(page)
                .build()));
    }

    private static Flux<UserResource> requestListOrganizationBillingManagers(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.organizations()
            .listBillingManagers(ListOrganizationBillingManagersRequest.builder()
                .organizationId(organizationId)
                .page(page)
                .build()));
    }

    private static Flux<UserResource> requestListOrganizationManagers(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.organizations()
            .listManagers(ListOrganizationManagersRequest.builder()
                .organizationId(organizationId)
                .page(page)
                .build()));
    }

    private static Flux<OrganizationResource> requestListOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organizationName)
                .page(page)
                .build()));
    }

    private static Flux<UserResource> requestListSpaceAuditors(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.spaces()
            .listAuditors(ListSpaceAuditorsRequest.builder()
                .page(page)
                .spaceId(spaceId)
                .build()));
    }

    private static Flux<UserResource> requestListSpaceDevelopers(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.spaces()
            .listDevelopers(ListSpaceDevelopersRequest.builder()
                .page(page)
                .spaceId(spaceId)
                .build()));
    }

    private static Flux<UserResource> requestListSpaceManagers(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.spaces()
            .listManagers(ListSpaceManagersRequest.builder()
                .page(page)
                .spaceId(spaceId)
                .build()));
    }

    private static Flux<SpaceResource> requestListSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .organizationId(organizationId)
                .name(spaceName)
                .page(page)
                .build()));
    }

    private Mono<OrganizationUsers> toOrganizationUsers(List<String> auditors, List<String> billingManagers, List<String> managers) {
        return Mono.just(OrganizationUsers.builder()
            .addAllAuditors(auditors)
            .addAllBillingManagers(billingManagers)
            .addAllManagers(managers)
            .build());
    }

    private Mono<SpaceUsers> toSpaceUsers(List<String> auditors, List<String> developers, List<String> managers) {
        return Mono.just(SpaceUsers.builder()
            .addAllAuditors(auditors)
            .addAllDevelopers(developers)
            .addAllManagers(managers)
            .build());
    }

}
