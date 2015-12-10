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
import org.cloudfoundry.client.v2.organizations.AssociateAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserResponse;
import org.cloudfoundry.client.v2.organizations.AssociatePrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociatePrivateDomainResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationResponse;
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
    public Publisher<AssociateAuditorResponse> associateAuditor(final AssociateAuditorRequest request) {
        return put(request, AssociateAuditorResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors",
                        request.getAuditorId());
            }

        });
    }

    @Override
    public Publisher<AssociateBillingManagerResponse> associateBillingManager(final AssociateBillingManagerRequest
                                                                                      request) {
        return put(request, AssociateBillingManagerResponse.class, new Consumer<UriComponentsBuilder>() {
            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "billing_managers", request
                        .getBillingManagerId());
            }

        });
    }

    @Override
    public Publisher<AssociateOrganizationManagerResponse> associateManager(final AssociateOrganizationManagerRequest
                                                                                    request) {
        return put(request, AssociateOrganizationManagerResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Publisher<AssociatePrivateDomainResponse> associatePrivateDomain(final AssociatePrivateDomainRequest
                                                                                    request) {
        return put(request, AssociatePrivateDomainResponse.class, new Consumer<UriComponentsBuilder>() {

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
    public Publisher<ListOrganizationBillingManagersResponse> listBillingManagers(final
                                                                                  ListOrganizationBillingManagersRequest request) {
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
    public Publisher<ListOrganizationSpaceQuotaDefinitionsResponse> listSpaceQuotaDefinitions(final
                                                                                              ListOrganizationSpaceQuotaDefinitionsRequest request) {
        return get(request, ListOrganizationSpaceQuotaDefinitionsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getId(), "space_quota_definitions");
                QueryBuilder.augment(builder, request);
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

}
