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

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
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
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationSpaceSummary;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.client.v2.users.UserResource;
import org.junit.Test;
import reactor.rx.Streams;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringOrganizationsTest extends AbstractRestTest {

    private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root);

    @Test
    public void associateAuditor() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors/uaa-id-71")
                .status(CREATED)
                .responsePayload("v2/organizations/auditors/PUT_{id}_response.json"));

        AssociateAuditorRequest request = AssociateAuditorRequest.builder()
                .auditorId("uaa-id-71")
                .organizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                .build();

        AssociateAuditorResponse expected = AssociateAuditorResponse.builder()
                .metadata(Metadata.builder()
                        .id("83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                        .url("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                        .createdAt("2015-07-27T22:43:10Z")
                        .build())
                .entity(OrganizationEntity.builder()
                        .name("name-187")
                        .billingEnabled(false)
                        .quotaDefinitionId("1d18a00b-4e36-412b-9308-2f5f2402e880")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/1d18a00b-4e36-412b-9308-2f5f2402e880")
                        .spacesUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/spaces")
                        .domainsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/domains")
                        .privateDomainsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/private_domains")
                        .usersUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/users")
                        .managersUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/managers")
                        .billingManagersUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/billing_managers")
                        .auditorsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors")
                        .applicationEventsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/app_events")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/space_quota_definitions")
                        .build())
                .build();

        AssociateAuditorResponse actual = Streams.wrap(this.organizations.associateAuditor(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateAuditorError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors/uaa-id-71")
                .errorResponse());

        AssociateAuditorRequest request = AssociateAuditorRequest.builder()
                .auditorId("uaa-id-71")
                .organizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                .build();

        Streams.wrap(this.organizations.associateAuditor(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateAuditorInvalidRequest() throws Throwable {
        AssociateAuditorRequest request = AssociateAuditorRequest.builder()
                .build();

        Streams.wrap(this.organizations.associateAuditor(request)).next().get();
    }

    @Test
    public void associateBillingManager() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/billing_managers/test-billing-manager-id")
                .status(OK)
                .responsePayload("v2/organizations/PUT_{id}_billing_managers_{billing-manager-id}_response.json"));

        AssociateBillingManagerRequest request = AssociateBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .id("test-id")
                .build();

        AssociateBillingManagerResponse expected = AssociateBillingManagerResponse.builder()
                .entity(OrganizationEntity.builder()
                        .applicationEventsUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/app_events")
                        .auditorsUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/auditors")
                        .billingEnabled(false)
                        .billingManagersUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/billing_managers")
                        .domainsUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/domains")
                        .managersUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/managers")
                        .name("name-200")
                        .privateDomainsUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/private_domains")
                        .quotaDefinitionId("ab51f0d8-1920-4bfc-9401-cd0e978e8c5e")
                        .quotaDefinitionUrl("/v2/quota_definitions/ab51f0d8-1920-4bfc-9401-cd0e978e8c5e")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/space_quota_definitions")
                        .spacesUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/spaces")
                        .status("active")
                        .usersUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/users")
                        .build())
                .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:10Z")
                        .id("39ab104d-79f9-4bac-82e0-35b826a236b8")
                        .url("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8")
                        .build())
                .build();

        AssociateBillingManagerResponse actual = Streams.wrap(this.organizations.associateBillingManager(request))
                .next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateBillingManagerError() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/billing_managers/test-billing-manager-id")
                .errorResponse());

        AssociateBillingManagerRequest request = AssociateBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.associateBillingManager(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateBillingManagerInvalidRequest() {
        AssociateBillingManagerRequest request = AssociateBillingManagerRequest.builder()
                .build();

        Streams.wrap(this.organizations.associateBillingManager(request)).next().get();
    }

    @Test
    public void associateManager() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/managers/test-manager-id")
                .status(OK)
                .responsePayload("v2/organizations/PUT_{id}_managers_{manager-id}_response.json"));

        AssociateOrganizationManagerRequest request = AssociateOrganizationManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        AssociateOrganizationManagerResponse expected = AssociateOrganizationManagerResponse.builder()
                .metadata(Metadata.builder()
                        .id("cc7c5224-f973-4358-a95a-dd72decbb20f")
                        .url("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f")
                        .createdAt("2015-07-27T22:43:10Z")
                        .build())
                .entity(OrganizationEntity.builder()
                        .name("name-218")
                        .billingEnabled(false)
                        .quotaDefinitionId("57f59bb7-7581-4257-9502-cbd60bb92d99")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/57f59bb7-7581-4257-9502-cbd60bb92d99")
                        .spacesUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/spaces")
                        .domainsUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/domains")
                        .privateDomainsUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/private_domains")
                        .usersUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/users")
                        .managersUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/managers")
                        .billingManagersUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/billing_managers")
                        .auditorsUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/auditors")
                        .applicationEventsUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/app_events")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/space_quota_definitions")
                        .build())
                .build();

        AssociateOrganizationManagerResponse actual = Streams.wrap(this.organizations.associateManager(request)).next
                ().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateManagerError() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/managers/test-manager-id")
                .errorResponse());

        AssociateOrganizationManagerRequest request = AssociateOrganizationManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        Streams.wrap(this.organizations.associateManager(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateManagerInvalidRequest() {
        AssociateOrganizationManagerRequest request = AssociateOrganizationManagerRequest.builder()
                .build();

        Streams.wrap(this.organizations.associateManager(request)).next().get();
    }

    @Test
    public void associatePrivateDomain() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/private_domains/test-private-domain-id")
                .status(OK)
                .responsePayload("v2/organizations/PUT_{id}_private_domains_{private-domain-id}_response.json"));

        AssociatePrivateDomainRequest request = AssociatePrivateDomainRequest.builder()
                .id("test-id")
                .privateDomainId("test-private-domain-id")
                .build();

        AssociatePrivateDomainResponse expected = AssociatePrivateDomainResponse.builder()
                .entity(OrganizationEntity.builder()
                        .name("name-228")
                        .billingEnabled(false)
                        .quotaDefinitionId("855b0cb8-5c58-4ebc-8189-6582c37060e6")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/855b0cb8-5c58-4ebc-8189-6582c37060e6")
                        .spacesUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/spaces")
                        .domainsUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/domains")
                        .privateDomainsUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/private_domains")
                        .usersUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/users")
                        .managersUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/managers")
                        .billingManagersUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/billing_managers")
                        .auditorsUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/auditors")
                        .applicationEventsUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/app_events")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/space_quota_definitions")
                        .build())
                .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:10Z")
                        .id("676f9ff8-8c35-49ed-8ebf-fdf3db34cde7")
                        .url("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7")
                        .build())
                .build();

        AssociatePrivateDomainResponse actual = Streams.wrap(this.organizations.associatePrivateDomain(request)).next
                ().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associatePrivateDomainError() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/private_domains/test-private-domain-id")
                .errorResponse());

        AssociatePrivateDomainRequest request = AssociatePrivateDomainRequest.builder()
                .id("test-id")
                .privateDomainId("test-private-domain-id")
                .build();

        Streams.wrap(this.organizations.associatePrivateDomain(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associatePrivateDomainInvalidRequest() {
        AssociatePrivateDomainRequest request = AssociatePrivateDomainRequest.builder()
                .build();

        Streams.wrap(this.organizations.associatePrivateDomain(request)).next().get();
    }

    @Test
    public void associateUser() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/users/test-user-id")
                .status(OK)
                .responsePayload("v2/organizations/PUT_{id}_users_{user-id}_response.json"));

        AssociateOrganizationUserRequest request = AssociateOrganizationUserRequest.builder()
                .id("test-id")
                .userId("test-user-id")
                .build();

        AssociateOrganizationUserResponse expected = AssociateOrganizationUserResponse.builder()
                .metadata(Metadata.builder()
                        .id("584664d0-e5bb-449b-bfe5-0136c30c4ff8")
                        .url("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8")
                        .createdAt("2015-07-27T22:43:11Z")
                        .build())
                .entity(OrganizationEntity.builder()
                        .name("name-234")
                        .billingEnabled(false)
                        .quotaDefinitionId("51a2f10b-9803-4f35-ad69-0350ff4b66d4")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/51a2f10b-9803-4f35-ad69-0350ff4b66d4")
                        .spacesUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/spaces")
                        .domainsUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/domains")
                        .privateDomainsUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/private_domains")
                        .usersUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/users")
                        .managersUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/managers")
                        .billingManagersUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/billing_managers")
                        .auditorsUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/auditors")
                        .applicationEventsUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/app_events")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/space_quota_definitions")
                        .build())
                .build();

        AssociateOrganizationUserResponse actual = Streams.wrap(this.organizations.associateUser(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateUserError() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id/users/test-user-id")
                .errorResponse());

        AssociateOrganizationUserRequest request = AssociateOrganizationUserRequest.builder()
                .id("test-id")
                .userId("test-user-id")
                .build();

        Streams.wrap(this.organizations.associateUser(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateUserInvalidRequest() {
        AssociateOrganizationUserRequest request = AssociateOrganizationUserRequest.builder()
                .build();

        Streams.wrap(this.organizations.associateUser(request)).next().get();
    }

    @Test
    public void create() {
        mockRequest(new RequestContext()
                .method(POST).path("v2/organizations")
                .requestPayload("v2/organizations/POST_request.json")
                .status(OK)
                .responsePayload("v2/organizations/POST_response.json"));

        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("my-org-name")
                .quotaDefinitionId("ffc919cd-3e21-43a6-9e4e-62802d149cdb")
                .build();

        CreateOrganizationResponse expected = CreateOrganizationResponse.builder()
                .metadata(Metadata.builder()
                        .id("137bfc86-5a2f-4759-9c0c-59ef614cd0be")
                        .url("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be")
                        .createdAt("2015-07-27T22:43:11Z")
                        .build())
                .entity(OrganizationEntity.builder()
                        .name("my-org-name")
                        .billingEnabled(false)
                        .quotaDefinitionId("ffc919cd-3e21-43a6-9e4e-62802d149cdb")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/ffc919cd-3e21-43a6-9e4e-62802d149cdb")
                        .spacesUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/spaces")
                        .domainsUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/domains")
                        .privateDomainsUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/private_domains")
                        .usersUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/users")
                        .managersUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/managers")
                        .billingManagersUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/billing_managers")
                        .auditorsUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/auditors")
                        .applicationEventsUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/app_events")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/space_quota_definitions")
                        .build())
                .build();

        CreateOrganizationResponse actual = Streams.wrap(this.organizations.create(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() {
        mockRequest(new RequestContext()
                .method(POST).path("v2/organizations")
                .requestPayload("v2/organizations/POST_request.json")
                .errorResponse());

        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name("my-org-name")
                .quotaDefinitionId("ffc919cd-3e21-43a6-9e4e-62802d149cdb")
                .build();

        Streams.wrap(this.organizations.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() {
        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .build();

        Streams.wrap(this.organizations.create(request)).next().get();
    }

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id?async=true")
                .status(NO_CONTENT));

        DeleteOrganizationRequest request = DeleteOrganizationRequest.builder()
                .async(true)
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id?async=true")
                .errorResponse());

        DeleteOrganizationRequest request = DeleteOrganizationRequest.builder()
                .async(true)
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        DeleteOrganizationRequest request = DeleteOrganizationRequest.builder()
                .build();

        Streams.wrap(this.organizations.delete(request)).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/organizations?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_response.json"));

        ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                .name("test-name")
                .page(-1)
                .build();

        ListOrganizationsResponse expected = ListOrganizationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ListOrganizationsResponse.Resource.builder()
                        .metadata(Metadata.builder()
                                .id("deb3c359-2261-45ba-b34f-ee7487acd71a")
                                .url("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a")
                                .createdAt("2015-07-27T22:43:05Z")
                                .build())
                        .entity(OrganizationEntity.builder()
                                .name("the-system_domain-org-name")
                                .billingEnabled(false)
                                .quotaDefinitionId("9b56a1ec-4981-4a1e-9348-0d78eeca842c")
                                .status("active")
                                .quotaDefinitionUrl("/v2/quota_definitions/9b56a1ec-4981-4a1e-9348-0d78eeca842c")
                                .spacesUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/spaces")
                                .domainsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/domains")
                                .privateDomainsUrl
                                        ("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/private_domains")
                                .usersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/users")
                                .managersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/managers")
                                .billingManagersUrl
                                        ("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/billing_managers")
                                .auditorsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/auditors")
                                .applicationEventsUrl
                                        ("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/app_events")
                                .spaceQuotaDefinitionsUrl
                                        ("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/space_quota_definitions")
                                .build())
                        .build())
                .build();

        ListOrganizationsResponse actual = Streams.wrap(this.organizations.list(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test
    public void listAuditors() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/auditors?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_auditors_response.json"));

        ListOrganizationAuditorsRequest request = ListOrganizationAuditorsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationAuditorsResponse expected = ListOrganizationAuditorsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-73")
                                .url("/v2/users/uaa-id-73")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(UserEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .username("auditor@example.com")
                                .spacesUrl("/v2/users/uaa-id-73/spaces")
                                .organizationsUrl("/v2/users/uaa-id-73/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-73/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-73/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-73/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-73/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-73/audited_spaces")
                                .build())
                        .build())
                .build();

        ListOrganizationAuditorsResponse actual = Streams.wrap(this.organizations.listAuditors(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listAuditorsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/auditors?page=-1")
                .errorResponse());

        ListOrganizationAuditorsRequest request = ListOrganizationAuditorsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listAuditors(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listAuditorsInvalidRequest() {
        ListOrganizationAuditorsRequest request = ListOrganizationAuditorsRequest.builder()
                .build();

        Streams.wrap(this.organizations.listAuditors(request)).next().get();
    }

    @Test
    public void listBillingManagers() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/billing_managers?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_billing_managers_response.json"));

        ListOrganizationBillingManagersRequest request = ListOrganizationBillingManagersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationBillingManagersResponse expected = ListOrganizationBillingManagersResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-84")
                                .url("/v2/users/uaa-id-84")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(UserEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .username("billing_manager@example.com")
                                .spacesUrl("/v2/users/uaa-id-84/spaces")
                                .organizationsUrl("/v2/users/uaa-id-84/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-84/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-84/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-84/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-84/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-84/audited_spaces")
                                .build())
                        .build())
                .build();

        ListOrganizationBillingManagersResponse actual = Streams.wrap(this.organizations.listBillingManagers(request)
        ).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listBillingManagersError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/billing_managers?page=-1")
                .errorResponse());

        ListOrganizationBillingManagersRequest request = ListOrganizationBillingManagersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listBillingManagers(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listBillingManagersInvalidRequest() {
        ListOrganizationBillingManagersRequest request = ListOrganizationBillingManagersRequest.builder()
                .build();

        Streams.wrap(this.organizations.listBillingManagers(request)).next().get();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/organizations?q=name%20IN%20test-name&page=-1")
                .errorResponse());

        ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                .name("test-name")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.list(request)).next().get();
    }

    @Test
    public void listManagers() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/managers?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_managers_response.json"));

        ListOrganizationManagersRequest request = ListOrganizationManagersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationManagersResponse expected = ListOrganizationManagersResponse.builder()
                .totalResults(2)
                .totalPages(1)
                .resource(UserResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-99")
                                .url("/v2/users/uaa-id-99")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(UserEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .username("manager@example.com")
                                .spacesUrl("/v2/users/uaa-id-99/spaces")
                                .organizationsUrl("/v2/users/uaa-id-99/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-99/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-99/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-99/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-99/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-99/audited_spaces")
                                .build())
                        .build())
                .resource(UserResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-100")
                                .url("/v2/users/uaa-id-100")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(UserEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .spacesUrl("/v2/users/uaa-id-100/spaces")
                                .organizationsUrl("/v2/users/uaa-id-100/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-100/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-100/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-100/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-100/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-100/audited_spaces")
                                .build())
                        .build())
                .build();

        ListOrganizationManagersResponse actual = Streams.wrap(this.organizations.listManagers(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listManagersError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/managers?page=-1")
                .errorResponse());

        ListOrganizationManagersRequest request = ListOrganizationManagersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listManagers(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listManagersInvalidRequest() {
        ListOrganizationManagersRequest request = ListOrganizationManagersRequest.builder()
                .build();

        Streams.wrap(this.organizations.listManagers(request)).next().get();
    }

    @Test
    public void listPrivateDomains() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/private_domains?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_private_domains_response.json"));

        ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationPrivateDomainsResponse expected = ListOrganizationPrivateDomainsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(PrivateDomainResource.builder()
                        .metadata(Metadata.builder()
                                .id("4625debe-c7ac-4d8e-84d2-448691c30ebc")
                                .url("/v2/private_domains/4625debe-c7ac-4d8e-84d2-448691c30ebc")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(PrivateDomainEntity.builder()
                                .name("domain-2.example.com")
                                .owningOrganizationId("09beeba3-f2ed-4e45-90f9-fc2119e02e9e")
                                .owningOrganizationUrl("/v2/organizations/09beeba3-f2ed-4e45-90f9-fc2119e02e9e")
                                .sharedOrganizationsUrl
                                        ("/v2/private_domains/4625debe-c7ac-4d8e-84d2-448691c30ebc/shared_organizations")
                                .build())
                        .build())
                .build();

        ListOrganizationPrivateDomainsResponse actual = Streams.wrap(this.organizations.listPrivateDomains(request))
                .next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listPrivateDomainsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/private_domains?page=-1")
                .errorResponse());

        ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listPrivateDomains(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listPrivateDomainsInvalidRequest() {
        ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                .build();

        Streams.wrap(this.organizations.listPrivateDomains(request)).next().get();
    }

    @Test
    public void listServices() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/services?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_services_response.json"));

        ListOrganizationServicesRequest request = ListOrganizationServicesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationServicesResponse expected = ListOrganizationServicesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceResource.builder()
                        .metadata(Metadata.builder()
                                .id("5529fa75-d2f3-426c-b864-4ae45c3622da")
                                .url("/v2/services/5529fa75-d2f3-426c-b864-4ae45c3622da")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(ServiceEntity.builder()
                                .label("label-16")
                                .description("desc-39")
                                .active(true)
                                .bindable(true)
                                .uniqueId("4d4e8356-f753-433e-8514-88ee78c4e153")
                                .serviceBrokerId("bf5b5cf7-acac-426b-8e79-cc57a227cd3c")
                                .planUpdateable(false)
                                .servicePlansUrl("/v2/services/5529fa75-d2f3-426c-b864-4ae45c3622da/service_plans")
                                .build())
                        .build())
                .build();

        ListOrganizationServicesResponse actual = Streams.wrap(this.organizations.listServices(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listServicesError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/services?page=-1")
                .errorResponse());

        ListOrganizationServicesRequest request = ListOrganizationServicesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listServices(request)).next().get();
    }

    @Test
    public void listSpaceQuotaDefinitions() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/space_quota_definitions?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_space_quota_definitions_response.json"));

        ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationSpaceQuotaDefinitionsResponse expected = ListOrganizationSpaceQuotaDefinitionsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceQuotaDefinitionResource.builder()
                        .metadata(Metadata.builder()
                                .id("d0bf6c52-a880-4c8f-b7d9-d9302a2ac6c9")
                                .url("/v2/space_quota_definitions/d0bf6c52-a880-4c8f-b7d9-d9302a2ac6c9")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(SpaceQuotaDefinitionEntity.builder()
                                .instanceMemoryLimit(-1)
                                .memoryLimit(20480)
                                .name("name-199")
                                .nonBasicServicesAllowed(true)
                                .organizationId("a163840f-5bd5-48a0-8736-1d837bda1353")
                                .organizationUrl("/v2/organizations/a163840f-5bd5-48a0-8736-1d837bda1353")
                                .spacesUrl("/v2/space_quota_definitions/d0bf6c52-a880-4c8f-b7d9-d9302a2ac6c9/spaces")
                                .totalRoutes(1000)
                                .totalServices(60)
                                .build())
                        .build())
                .build();

        ListOrganizationSpaceQuotaDefinitionsResponse actual = Streams.wrap(this.organizations
                .listSpaceQuotaDefinitions(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listSpaceQuotaDefinitionsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/space_quota_definitions?page=-1")
                .errorResponse());

        ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listSpaceQuotaDefinitions(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listSpaceQuotaDefinitionsInvalidRequest() {
        ListOrganizationSpaceQuotaDefinitionsRequest request = ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .build();

        Streams.wrap(this.organizations.listSpaceQuotaDefinitions(request)).next().get();
    }

    @Test
    public void listSpaces() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/spaces?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_spaces_response.json"));

        ListOrganizationSpacesRequest request = ListOrganizationSpacesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationSpacesResponse expected = ListOrganizationSpacesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceResource.builder()
                        .metadata(Metadata.builder()
                                .id("9f6ce6e0-e3db-42ae-9572-bbc38f4f541b")
                                .url("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b")
                                .createdAt("2015-07-27T22:43:10Z")
                                .build())
                        .entity(SpaceEntity.builder()
                                .name("name-215")
                                .organizationId("4df92169-5b8d-489b-ba0f-7114168aa476")
                                .spaceQuotaDefinitionId(null)
                                .allowSsh(true)
                                .organizationUrl("/v2/organizations/4df92169-5b8d-489b-ba0f-7114168aa476")
                                .developersUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/developers")
                                .managersUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/managers")
                                .auditorsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/auditors")
                                .applicationsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/apps")
                                .routesUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/routes")
                                .domainsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/domains")
                                .serviceInstancesUrl
                                        ("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/service_instances")
                                .applicationEventsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/app_events")
                                .eventsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/events")
                                .securityGroupsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/security_groups")
                                .build())
                        .build())
                .build();

        ListOrganizationSpacesResponse actual = Streams.wrap(this.organizations.listSpaces(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listSpacesError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/spaces?page=-1")
                .errorResponse());

        ListOrganizationSpacesRequest request = ListOrganizationSpacesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listSpaces(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listSpacesInvalidRequest() {
        ListOrganizationSpacesRequest request = ListOrganizationSpacesRequest.builder()
                .build();

        Streams.wrap(this.organizations.listSpaces(request)).next().get();
    }

    @Test
    public void listUsers() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/users?page=-1")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_users_response.json"));

        ListOrganizationUsersRequest request = ListOrganizationUsersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListOrganizationUsersResponse expected = ListOrganizationUsersResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-111")
                                .url("/v2/users/uaa-id-111")
                                .createdAt("2015-07-27T22:43:11Z")
                                .build())
                        .entity(UserEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .username("user@example.com")
                                .spacesUrl("/v2/users/uaa-id-111/spaces")
                                .organizationsUrl("/v2/users/uaa-id-111/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-111/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-111/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-111/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-111/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-111/audited_spaces")
                                .build())
                        .build())
                .build();

        ListOrganizationUsersResponse actual = Streams.wrap(this.organizations.listUsers(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listUsersError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/organizations/test-id/users?page=-1")
                .errorResponse());

        ListOrganizationUsersRequest request = ListOrganizationUsersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.organizations.listUsers(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listUsersInvalidRequest() {
        ListOrganizationUsersRequest request = ListOrganizationUsersRequest.builder()
                .build();

        Streams.wrap(this.organizations.listUsers(request)).next().get();
    }

    @Test
    public void removeAuditor() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id/auditors/test-auditor-id")
                .status(NO_CONTENT));

        RemoveOrganizationAuditorRequest request = RemoveOrganizationAuditorRequest.builder()
                .auditorId("test-auditor-id")
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.removeAuditor(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void removeAuditorError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id/auditors/test-auditor-id")
                .errorResponse());

        RemoveOrganizationAuditorRequest request = RemoveOrganizationAuditorRequest.builder()
                .auditorId("test-auditor-id")
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.removeAuditor(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void removeAuditorInvalidRequest() {
        RemoveOrganizationAuditorRequest request = RemoveOrganizationAuditorRequest.builder()
                .build();

        Streams.wrap(this.organizations.removeAuditor(request)).next().get();
    }

    @Test
    public void removeBillingManager() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id/billing_managers/test-billing-manager-id")
                .status(NO_CONTENT));

        RemoveOrganizationBillingManagerRequest request = RemoveOrganizationBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.removeBillingManager(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void removeBillingManagerError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id/billing_managers/test-billing-manager-id")
                .errorResponse());

        RemoveOrganizationBillingManagerRequest request = RemoveOrganizationBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.removeBillingManager(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void removeBillingManagerInvalidRequest() {
        RemoveOrganizationBillingManagerRequest request = RemoveOrganizationBillingManagerRequest.builder()
                .build();

        Streams.wrap(this.organizations.removeBillingManager(request)).next().get();
    }

    @Test
    public void removeManager() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id/managers/test-manager-id")
                .status(NO_CONTENT));

        RemoveOrganizationManagerRequest request = RemoveOrganizationManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        Streams.wrap(this.organizations.removeManager(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void removeManagerError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/organizations/test-id/managers/test-manager-id")
                .errorResponse());

        RemoveOrganizationManagerRequest request = RemoveOrganizationManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        Streams.wrap(this.organizations.removeManager(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void removeManagerInvalidRequest() {
        RemoveOrganizationManagerRequest request = RemoveOrganizationManagerRequest.builder()
                .build();

        Streams.wrap(this.organizations.removeManager(request)).next().get();
    }

    @Test
    public void summary() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/organizations/test-id/summary")
                .status(OK)
                .responsePayload("v2/organizations/GET_{id}_summary_response.json"));

        SummaryOrganizationRequest request = SummaryOrganizationRequest.builder()
                .id("test-id")
                .build();

        SummaryOrganizationResponse expected = SummaryOrganizationResponse.builder()
                .id("525a6450-9202-4ea1-beca-6fdda210710e")
                .name("name-357")
                .status("active")
                .space(OrganizationSpaceSummary.builder()
                        .id("dec1c7b0-8ea6-488a-9410-f73649d30228")
                        .name("name-359")
                        .serviceCount(0)
                        .appCount(0)
                        .memDevTotal(0)
                        .memProdTotal(0)
                        .build()
                ).build();

        SummaryOrganizationResponse actual = Streams.wrap(this.organizations.summary(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void summaryError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/organizations/test-id/summary")
                .errorResponse());

        SummaryOrganizationRequest request = SummaryOrganizationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.organizations.summary(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void summaryInvalidRequest() {
        SummaryOrganizationRequest request = SummaryOrganizationRequest.builder()
                .build();

        Streams.wrap(this.organizations.summary(request)).next().get();
    }

    @Test
    public void update() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id")
                .requestPayload("v2/organizations/PUT_{id}_request.json")
                .status(OK)
                .responsePayload("v2/organizations/PUT_{id}_response.json"));

        UpdateOrganizationRequest request = UpdateOrganizationRequest.builder()
                .id("test-id")
                .name("New Organization Name")
                .quotaDefinitionId("7df44b58-1834-486f-aed8-d5d97126e603")
                .build();

        UpdateOrganizationResponse expected = UpdateOrganizationResponse.builder()
                .metadata(Metadata.builder()
                        .id("31a539be-dbfd-4db6-aec1-6565ebe975ed")
                        .url("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed")
                        .createdAt("2015-07-27T22:43:11Z")
                        .updatedAt("2015-07-27T22:43:11Z")
                        .build())
                .entity(OrganizationEntity.builder()
                        .name("New Organization Name")
                        .billingEnabled(false)
                        .quotaDefinitionId("7df44b58-1834-486f-aed8-d5d97126e603")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/7df44b58-1834-486f-aed8-d5d97126e603")
                        .spacesUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/spaces")
                        .domainsUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/domains")
                        .privateDomainsUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/private_domains")
                        .usersUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/users")
                        .managersUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/managers")
                        .billingManagersUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/billing_managers")
                        .auditorsUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/auditors")
                        .applicationEventsUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/app_events")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/space_quota_definitions")
                        .build())
                .build();

        UpdateOrganizationResponse actual = Streams.wrap(this.organizations.update(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void updateError() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/organizations/test-id")
                .requestPayload("v2/organizations/PUT_{id}_request.json")
                .errorResponse());

        UpdateOrganizationRequest request = UpdateOrganizationRequest.builder()
                .id("test-id")
                .name("New Organization Name")
                .quotaDefinitionId("7df44b58-1834-486f-aed8-d5d97126e603")
                .build();

        Streams.wrap(this.organizations.update(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void updateInvalidRequest() {
        UpdateOrganizationRequest request = UpdateOrganizationRequest.builder()
                .build();

        Streams.wrap(this.organizations.update(request)).next().get();
    }

}
