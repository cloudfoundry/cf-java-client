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

package org.cloudfoundry.reactor.client.v2.users;

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
import org.cloudfoundry.client.v2.users.DeleteUserResponse;
import org.cloudfoundry.client.v2.users.GetUserRequest;
import org.cloudfoundry.client.v2.users.GetUserResponse;
import org.cloudfoundry.client.v2.users.ListUserAuditedOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserAuditedOrganizationsResponse;
import org.cloudfoundry.client.v2.users.ListUserAuditedSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserAuditedSpacesResponse;
import org.cloudfoundry.client.v2.users.ListUserBillingManagedOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserBillingManagedOrganizationsResponse;
import org.cloudfoundry.client.v2.users.ListUserManagedOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserManagedOrganizationsResponse;
import org.cloudfoundry.client.v2.users.ListUserManagedSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserManagedSpacesResponse;
import org.cloudfoundry.client.v2.users.ListUserOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserOrganizationsResponse;
import org.cloudfoundry.client.v2.users.ListUserSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserSpacesResponse;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.ListUsersResponse;
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
import org.cloudfoundry.client.v2.users.UpdateUserResponse;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Users}
 */
public final class ReactorUsers extends AbstractClientV2Operations implements Users {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorUsers(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AssociateUserAuditedOrganizationResponse> associateAuditedOrganization(AssociateUserAuditedOrganizationRequest request) {
        return put(request, AssociateUserAuditedOrganizationResponse.class, builder ->
            builder.pathSegment("users", request.getUserId(), "audited_organizations", request.getAuditedOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateUserAuditedSpaceResponse> associateAuditedSpace(AssociateUserAuditedSpaceRequest request) {
        return put(request, AssociateUserAuditedSpaceResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "audited_spaces", request.getAuditedSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateUserBillingManagedOrganizationResponse> associateBillingManagedOrganization(AssociateUserBillingManagedOrganizationRequest request) {
        return put(request, AssociateUserBillingManagedOrganizationResponse.class, builder ->
            builder.pathSegment("users", request.getUserId(), "billing_managed_organizations", request.getBillingManagedOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateUserManagedOrganizationResponse> associateManagedOrganization(AssociateUserManagedOrganizationRequest request) {
        return put(request, AssociateUserManagedOrganizationResponse.class, builder ->
            builder.pathSegment("users", request.getUserId(), "managed_organizations", request.getManagedOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateUserManagedSpaceResponse> associateManagedSpace(AssociateUserManagedSpaceRequest request) {
        return put(request, AssociateUserManagedSpaceResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "managed_spaces", request.getManagedSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateUserOrganizationResponse> associateOrganization(AssociateUserOrganizationRequest request) {
        return put(request, AssociateUserOrganizationResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "organizations", request.getOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateUserSpaceResponse> associateSpace(AssociateUserSpaceRequest request) {
        return put(request, AssociateUserSpaceResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "spaces", request.getSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<CreateUserResponse> create(CreateUserRequest request) {
        return post(request, CreateUserResponse.class, builder -> builder.pathSegment("users"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteUserResponse> delete(DeleteUserRequest request) {
        return delete(request, DeleteUserResponse.class, builder -> builder.pathSegment("users", request.getUserId()))
            .checkpoint();
    }

    @Override
    public Mono<GetUserResponse> get(GetUserRequest request) {
        return get(request, GetUserResponse.class, builder -> builder.pathSegment("users", request.getUserId()))
            .checkpoint();
    }

    @Override
    public Mono<ListUsersResponse> list(ListUsersRequest request) {
        return get(request, ListUsersResponse.class, builder -> builder.pathSegment("users"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserAuditedOrganizationsResponse> listAuditedOrganizations(ListUserAuditedOrganizationsRequest request) {
        return get(request, ListUserAuditedOrganizationsResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "audited_organizations"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserAuditedSpacesResponse> listAuditedSpaces(ListUserAuditedSpacesRequest request) {
        return get(request, ListUserAuditedSpacesResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "audited_spaces"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserBillingManagedOrganizationsResponse> listBillingManagedOrganizations(ListUserBillingManagedOrganizationsRequest request) {
        return get(request, ListUserBillingManagedOrganizationsResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "billing_managed_organizations"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserManagedOrganizationsResponse> listManagedOrganizations(ListUserManagedOrganizationsRequest request) {
        return get(request, ListUserManagedOrganizationsResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "managed_organizations"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserManagedSpacesResponse> listManagedSpaces(ListUserManagedSpacesRequest request) {
        return get(request, ListUserManagedSpacesResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "managed_spaces"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserOrganizationsResponse> listOrganizations(ListUserOrganizationsRequest request) {
        return get(request, ListUserOrganizationsResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "organizations"))
            .checkpoint();
    }

    @Override
    public Mono<ListUserSpacesResponse> listSpaces(ListUserSpacesRequest request) {
        return get(request, ListUserSpacesResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "spaces"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeAuditedOrganization(RemoveUserAuditedOrganizationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("users", request.getUserId(), "audited_organizations", request.getAuditedOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeAuditedSpace(RemoveUserAuditedSpaceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("users", request.getUserId(), "audited_spaces", request.getAuditedSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeBillingManagedOrganization(RemoveUserBillingManagedOrganizationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("users", request.getUserId(), "billing_managed_organizations", request.getBillingManagedOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeManagedOrganization(RemoveUserManagedOrganizationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("users", request.getUserId(), "managed_organizations", request.getManagedOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeManagedSpace(RemoveUserManagedSpaceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("users", request.getUserId(), "managed_spaces", request.getManagedSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeOrganization(RemoveUserOrganizationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("users", request.getUserId(), "organizations", request.getOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeSpace(RemoveUserSpaceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("users", request.getUserId(), "spaces", request.getSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<SummaryUserResponse> summary(SummaryUserRequest request) {
        return get(request, SummaryUserResponse.class, builder -> builder.pathSegment("users", request.getUserId(), "summary"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateUserResponse> update(UpdateUserRequest request) {
        return put(request, UpdateUserResponse.class, builder -> builder.pathSegment("users", request.getUserId()))
            .checkpoint();
    }

}
