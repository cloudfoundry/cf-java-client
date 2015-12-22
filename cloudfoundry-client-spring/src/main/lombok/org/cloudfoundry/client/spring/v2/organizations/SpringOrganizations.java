/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.v2.organizations;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
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
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Organizations}
 */
@ToString(callSuper = true)
public final class SpringOrganizations extends AbstractSpringOperations implements Organizations {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringOrganizations(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<AssociateOrganizationAuditorResponse> associateAuditor(
            final AssociateOrganizationAuditorRequest request) {
        return put(request, AssociateOrganizationAuditorResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors",
                        request.getAuditorId());
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationAuditorByUsernameResponse> associateAuditorByUsername(
            final AssociateOrganizationAuditorByUsernameRequest request) {
        return put(request, AssociateOrganizationAuditorByUsernameResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "auditors");
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationBillingManagerResponse> associateBillingManager(
            final AssociateOrganizationBillingManagerRequest request) {
        return put(request, AssociateOrganizationBillingManagerResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "billing_managers",
                        request.getBillingManagerId());
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationBillingManagerByUsernameResponse> associateBillingManagerByUsername(
            final AssociateOrganizationBillingManagerByUsernameRequest request) {
        return put(request, AssociateOrganizationBillingManagerByUsernameResponse.class, new
                Consumer<UriComponentsBuilder>() {

                    @Override
                    public void accept(UriComponentsBuilder builder) {
                        builder.pathSegment("v2", "organizations", request.getId(), "billing_managers");
                    }

                });
    }

    @Override
    public Publisher<AssociateOrganizationManagerResponse> associateManager(
            final AssociateOrganizationManagerRequest request) {
        return put(request, AssociateOrganizationManagerResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationManagerByUsernameResponse> associateManagerByUsername(
            final AssociateOrganizationManagerByUsernameRequest request) {
        return put(request, AssociateOrganizationManagerByUsernameResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "managers");
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationPrivateDomainResponse> associatePrivateDomain(
            final AssociateOrganizationPrivateDomainRequest request) {
        return put(request, AssociateOrganizationPrivateDomainResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "private_domains", request
                        .getPrivateDomainId());
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationUserResponse> associateUser(final AssociateOrganizationUserRequest request) {
        return put(request, AssociateOrganizationUserResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "users", request.getUserId());
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationUserByUsernameResponse> associateUserByUsername(
            final AssociateOrganizationUserByUsernameRequest request) {
        return put(request, AssociateOrganizationUserByUsernameResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "users");
            }

        });
    }

    @Override
    public Publisher<CreateOrganizationResponse> create(final CreateOrganizationRequest request) {
        return post(request, CreateOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations");
            }

        });
    }

    @Override
    public Publisher<Void> delete(final DeleteOrganizationRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId());
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<GetOrganizationResponse> get(final GetOrganizationRequest request) {
        return get(request, GetOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId());
            }

        });
    }

    @Override
    public Publisher<GetOrganizationInstanceUsageResponse> getInstanceUsage(
            final GetOrganizationInstanceUsageRequest request) {
        return get(request, GetOrganizationInstanceUsageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "instance_usage");
            }

        });
    }

    @Override
    public Publisher<GetOrganizationMemoryUsageResponse> getMemoryUsage(
            final GetOrganizationMemoryUsageRequest request) {
        return get(request, GetOrganizationMemoryUsageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "memory_usage");
            }

        });
    }

    @Override
    public Publisher<GetOrganizationUserRolesResponse> getUserRoles(final GetOrganizationUserRolesRequest request) {
        return get(request, GetOrganizationUserRolesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "user_roles");
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationsResponse> list(final ListOrganizationsRequest request) {
        return get(request, ListOrganizationsResponse.class, new Consumer<UriComponentsBuilder>() {
            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }
        });
    }

    @Override
    public Publisher<ListOrganizationAuditorsResponse> listAuditors(final ListOrganizationAuditorsRequest request) {
        return get(request, ListOrganizationAuditorsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "auditors");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationBillingManagersResponse> listBillingManagers(
            final ListOrganizationBillingManagersRequest request) {
        return get(request, ListOrganizationBillingManagersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "billing_managers");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationManagersResponse> listManagers(final ListOrganizationManagersRequest request) {
        return get(request, ListOrganizationManagersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "managers");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationPrivateDomainsResponse> listPrivateDomains(
            final ListOrganizationPrivateDomainsRequest request) {
        return get(request, ListOrganizationPrivateDomainsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "private_domains");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationServicesResponse> listServices(final ListOrganizationServicesRequest request) {
        return get(request, ListOrganizationServicesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "services");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationSpaceQuotaDefinitionsResponse> listSpaceQuotaDefinitions(
            final ListOrganizationSpaceQuotaDefinitionsRequest request) {
        return get(request, ListOrganizationSpaceQuotaDefinitionsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "space_quota_definitions");
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationSpacesResponse> listSpaces(final ListOrganizationSpacesRequest request) {
        return get(request, ListOrganizationSpacesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "spaces");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<ListOrganizationUsersResponse> listUsers(final ListOrganizationUsersRequest request) {
        return get(request, ListOrganizationUsersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "users");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Publisher<Void> removeAuditor(final RemoveOrganizationAuditorRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "auditors", request.getAuditorId());
            }

        });
    }

    @Override
    public Publisher<Void> removeAuditorByUsername(final RemoveOrganizationAuditorByUsernameRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "auditors");
            }
        });
    }

    @Override
    public Publisher<Void> removeBillingManager(final RemoveOrganizationBillingManagerRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "billing_managers", request
                        .getBillingManagerId());
            }

        });
    }

    @Override
    public Publisher<Void> removeManager(final RemoveOrganizationManagerRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Publisher<Void> removeManagerByUsername(final RemoveOrganizationManagerByUsernameRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "managers");
            }

        });
    }

    @Override
    public Publisher<Void> removePrivateDomain(final RemoveOrganizationPrivateDomainRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "private_domains",
                        request.getPrivateDomainId());
            }

        });
    }

    @Override
    public Publisher<Void> removeUser(final RemoveOrganizationUserRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "users", request.getUserId());
            }

        });
    }

    @Override
    public Publisher<Void> removeUserByUsername(final RemoveOrganizationUserByUsernameRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "users");
            }

        });
    }

    @Override
    public Publisher<SummaryOrganizationResponse> summary(final SummaryOrganizationRequest request) {
        return get(request, SummaryOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "summary");
            }

        });
    }

    @Override
    public Publisher<UpdateOrganizationResponse> update(final UpdateOrganizationRequest request) {
        return put(request, UpdateOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId());
            }

        });
    }

}
