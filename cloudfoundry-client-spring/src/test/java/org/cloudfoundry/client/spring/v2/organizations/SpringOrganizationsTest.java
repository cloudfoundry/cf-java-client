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
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationSpaceSummary;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationResponse;
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
}
