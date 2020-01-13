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

package org.cloudfoundry.reactor.client.v2.organizations;

import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationServicesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationServicesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.Organizations;
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
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Organizations}
 */
public final class ReactorOrganizations extends AbstractClientV2Operations implements Organizations {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorOrganizations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AssociateOrganizationAuditorResponse> associateAuditor(AssociateOrganizationAuditorRequest request) {
        return put(request, AssociateOrganizationAuditorResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "auditors", request.getAuditorId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationAuditorByUsernameResponse> associateAuditorByUsername(AssociateOrganizationAuditorByUsernameRequest request) {
        return put(request, AssociateOrganizationAuditorByUsernameResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "auditors"))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationBillingManagerResponse> associateBillingManager(AssociateOrganizationBillingManagerRequest request) {
        return put(request, AssociateOrganizationBillingManagerResponse.class,
            builder -> builder.pathSegment("organizations", request.getOrganizationId(), "billing_managers", request.getBillingManagerId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationBillingManagerByUsernameResponse> associateBillingManagerByUsername(AssociateOrganizationBillingManagerByUsernameRequest request) {
        return put(request, AssociateOrganizationBillingManagerByUsernameResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "billing_managers"))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationManagerResponse> associateManager(AssociateOrganizationManagerRequest request) {
        return put(request, AssociateOrganizationManagerResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "managers", request.getManagerId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationManagerByUsernameResponse> associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest request) {
        return put(request, AssociateOrganizationManagerByUsernameResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "managers"))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationPrivateDomainResponse> associatePrivateDomain(AssociateOrganizationPrivateDomainRequest request) {
        return put(request, AssociateOrganizationPrivateDomainResponse.class,
            builder -> builder.pathSegment("organizations", request.getOrganizationId(), "private_domains", request.getPrivateDomainId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationUserResponse> associateUser(AssociateOrganizationUserRequest request) {
        return put(request, AssociateOrganizationUserResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "users", request.getUserId()))
            .checkpoint();
    }

    @Override
    public Mono<AssociateOrganizationUserByUsernameResponse> associateUserByUsername(AssociateOrganizationUserByUsernameRequest request) {
        return put(request, AssociateOrganizationUserByUsernameResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "users"))
            .checkpoint();
    }

    @Override
    public Mono<CreateOrganizationResponse> create(CreateOrganizationRequest request) {
        return post(request, CreateOrganizationResponse.class, builder -> builder.pathSegment("organizations"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteOrganizationResponse> delete(DeleteOrganizationRequest request) {
        return delete(request, DeleteOrganizationResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<GetOrganizationResponse> get(GetOrganizationRequest request) {
        return get(request, GetOrganizationResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<GetOrganizationInstanceUsageResponse> getInstanceUsage(GetOrganizationInstanceUsageRequest request) {
        return get(request, GetOrganizationInstanceUsageResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "instance_usage"))
            .checkpoint();
    }

    @Override
    public Mono<GetOrganizationMemoryUsageResponse> getMemoryUsage(GetOrganizationMemoryUsageRequest request) {
        return get(request, GetOrganizationMemoryUsageResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "memory_usage"))
            .checkpoint();
    }

    @Override
    public Mono<GetOrganizationUserRolesResponse> getUserRoles(GetOrganizationUserRolesRequest request) {
        return get(request, GetOrganizationUserRolesResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "user_roles"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationsResponse> list(ListOrganizationsRequest request) {
        return get(request, ListOrganizationsResponse.class, builder -> builder.pathSegment("organizations"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationAuditorsResponse> listAuditors(ListOrganizationAuditorsRequest request) {
        return get(request, ListOrganizationAuditorsResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "auditors"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationBillingManagersResponse> listBillingManagers(ListOrganizationBillingManagersRequest request) {
        return get(request, ListOrganizationBillingManagersResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "billing_managers"))
            .checkpoint();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Mono<ListOrganizationDomainsResponse> listDomains(ListOrganizationDomainsRequest request) {
        return get(request, ListOrganizationDomainsResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "domains"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationManagersResponse> listManagers(ListOrganizationManagersRequest request) {
        return get(request, ListOrganizationManagersResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "managers"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationPrivateDomainsResponse> listPrivateDomains(ListOrganizationPrivateDomainsRequest request) {
        return get(request, ListOrganizationPrivateDomainsResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "private_domains"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationServicesResponse> listServices(ListOrganizationServicesRequest request) {
        return get(request, ListOrganizationServicesResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "services"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationSpaceQuotaDefinitionsResponse> listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest request) {
        return get(request, ListOrganizationSpaceQuotaDefinitionsResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "space_quota_definitions"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationSpacesResponse> listSpaces(ListOrganizationSpacesRequest request) {
        return get(request, ListOrganizationSpacesResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "spaces"))
            .checkpoint();
    }

    @Override
    public Mono<ListOrganizationUsersResponse> listUsers(ListOrganizationUsersRequest request) {
        return get(request, ListOrganizationUsersResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "users"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeAuditor(RemoveOrganizationAuditorRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "auditors", request.getAuditorId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeAuditorByUsername(RemoveOrganizationAuditorByUsernameRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "auditors"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeBillingManager(RemoveOrganizationBillingManagerRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "billing_managers", request.getBillingManagerId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeBillingManagerByUsername(RemoveOrganizationBillingManagerByUsernameRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "billing_managers"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeManager(RemoveOrganizationManagerRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "managers", request.getManagerId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeManagerByUsername(RemoveOrganizationManagerByUsernameRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "managers"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removePrivateDomain(RemoveOrganizationPrivateDomainRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "private_domains", request.getPrivateDomainId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeUser(RemoveOrganizationUserRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "users", request.getUserId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeUserByUsername(RemoveOrganizationUserByUsernameRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "users"))
            .checkpoint();
    }

    @Override
    public Mono<SummaryOrganizationResponse> summary(SummaryOrganizationRequest request) {
        return get(request, SummaryOrganizationResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId(), "summary"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateOrganizationResponse> update(UpdateOrganizationRequest request) {
        return put(request, UpdateOrganizationResponse.class, builder -> builder.pathSegment("organizations", request.getOrganizationId()))
            .checkpoint();
    }

}
