/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.spring.client.v2.organizations;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.jobs.JobEntity;
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
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.OrganizationSpaceSummary;
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
import org.cloudfoundry.client.v2.organizations.UserOrganizationRoleEntity;
import org.cloudfoundry.client.v2.organizations.UserOrganizationRoleResource;
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
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringOrganizationsTest {

    public static final class AssociateOrganizationAuditor extends AbstractApiTest<AssociateOrganizationAuditorRequest, AssociateOrganizationAuditorResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationAuditorRequest getInvalidRequest() {
            return AssociateOrganizationAuditorRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/auditors/uaa-id-71")
                .status(CREATED)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_auditors_{auditor-id}_response.json");
        }

        @Override
        protected AssociateOrganizationAuditorResponse getResponse() {
            return AssociateOrganizationAuditorResponse.builder()
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
                    .spaceQuotaDefinitionsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationAuditorRequest getValidRequest() throws Exception {
            return AssociateOrganizationAuditorRequest.builder()
                .auditorId("uaa-id-71")
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationAuditorResponse> invoke(AssociateOrganizationAuditorRequest request) {
            return this.organizations.associateAuditor(request);
        }

    }

    public static final class AssociateOrganizationAuditorByUsername extends AbstractApiTest<AssociateOrganizationAuditorByUsernameRequest, AssociateOrganizationAuditorByUsernameResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationAuditorByUsernameRequest getInvalidRequest() {
            return AssociateOrganizationAuditorByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/auditors")
                .requestPayload("fixtures/client/v2/organizations/PUT_{id}_auditors_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_auditors_response.json");
        }

        @Override
        protected AssociateOrganizationAuditorByUsernameResponse getResponse() {
            return AssociateOrganizationAuditorByUsernameResponse.builder()
                .metadata(Metadata.builder()
                    .id("50dfb04d-cd49-477d-a54c-32e00e180022")
                    .url("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022")
                    .createdAt("2015-11-30T23:38:58Z")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("name-2476")
                    .billingEnabled(false)
                    .quotaDefinitionId("8de0754e-bb1e-4739-be6e-91104bbab281")
                    .status("active")
                    .quotaDefinitionUrl("/v2/quota_definitions/8de0754e-bb1e-4739-be6e-91104bbab281")
                    .spacesUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/spaces")
                    .domainsUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/domains")
                    .privateDomainsUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/private_domains")
                    .usersUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/users")
                    .managersUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/managers")
                    .billingManagersUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/billing_managers")
                    .auditorsUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/auditors")
                    .applicationEventsUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/app_events")
                    .spaceQuotaDefinitionsUrl("/v2/organizations/50dfb04d-cd49-477d-a54c-32e00e180022/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationAuditorByUsernameRequest getValidRequest() throws Exception {
            return AssociateOrganizationAuditorByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationAuditorByUsernameResponse> invoke(AssociateOrganizationAuditorByUsernameRequest request) {
            return this.organizations.associateAuditorByUsername(request);
        }

    }

    public static final class AssociateOrganizationBillingManager extends AbstractApiTest<AssociateOrganizationBillingManagerRequest, AssociateOrganizationBillingManagerResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationBillingManagerRequest getInvalidRequest() {
            return AssociateOrganizationBillingManagerRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/billing_managers/test-billing-manager-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_billing_managers_{billing-manager-id}_response.json");
        }

        @Override
        protected AssociateOrganizationBillingManagerResponse getResponse() {
            return AssociateOrganizationBillingManagerResponse.builder()
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
                    .spaceQuotaDefinitionsUrl("/v2/organizations/39ab104d-79f9-4bac-82e0-35b826a236b8/space_quota_definitions")
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
        }

        @Override
        protected AssociateOrganizationBillingManagerRequest getValidRequest() throws Exception {
            return AssociateOrganizationBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationBillingManagerResponse> invoke(AssociateOrganizationBillingManagerRequest request) {
            return this.organizations.associateBillingManager(request);
        }

    }

    public static final class AssociateOrganizationBillingManagerByUsername
        extends AbstractApiTest<AssociateOrganizationBillingManagerByUsernameRequest, AssociateOrganizationBillingManagerByUsernameResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationBillingManagerByUsernameRequest getInvalidRequest() {
            return AssociateOrganizationBillingManagerByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/billing_managers")
                .requestPayload("fixtures/client/v2/organizations/PUT_{id}_billing_managers_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_billing_managers_response.json");
        }

        @Override
        protected AssociateOrganizationBillingManagerByUsernameResponse getResponse() {
            return AssociateOrganizationBillingManagerByUsernameResponse.builder()
                .metadata(Metadata.builder()
                    .id("c8d4f13c-8880-4859-8e03-fc690efd8f48")
                    .url("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48")
                    .createdAt("2015-11-30T23:38:58Z")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("name-2470")
                    .billingEnabled(false)
                    .quotaDefinitionId("4ad7378e-e90a-4714-b906-a451dd0d5507")
                    .status("active")
                    .quotaDefinitionUrl("/v2/quota_definitions/4ad7378e-e90a-4714-b906-a451dd0d5507")
                    .spacesUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/spaces")
                    .domainsUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/domains")
                    .privateDomainsUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/private_domains")
                    .usersUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/users")
                    .managersUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/managers")
                    .billingManagersUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/billing_managers")
                    .auditorsUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/auditors")
                    .applicationEventsUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/app_events")
                    .spaceQuotaDefinitionsUrl("/v2/organizations/c8d4f13c-8880-4859-8e03-fc690efd8f48/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationBillingManagerByUsernameRequest getValidRequest() throws Exception {
            return AssociateOrganizationBillingManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationBillingManagerByUsernameResponse> invoke(AssociateOrganizationBillingManagerByUsernameRequest request) {
            return this.organizations.associateBillingManagerByUsername(request);
        }

    }

    public static final class AssociateOrganizationManager extends AbstractApiTest<AssociateOrganizationManagerRequest, AssociateOrganizationManagerResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationManagerRequest getInvalidRequest() {
            return AssociateOrganizationManagerRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/managers/test-manager-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_managers_{manager-id}_response.json");
        }

        @Override
        protected AssociateOrganizationManagerResponse getResponse() {
            return AssociateOrganizationManagerResponse.builder()
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
                    .spaceQuotaDefinitionsUrl("/v2/organizations/cc7c5224-f973-4358-a95a-dd72decbb20f/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationManagerRequest getValidRequest() throws Exception {
            return AssociateOrganizationManagerRequest.builder()
                .organizationId("test-organization-id")
                .managerId("test-manager-id")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationManagerResponse> invoke(AssociateOrganizationManagerRequest request) {
            return this.organizations.associateManager(request);
        }

    }

    public static final class AssociateOrganizationManagerByUsername extends AbstractApiTest<AssociateOrganizationManagerByUsernameRequest, AssociateOrganizationManagerByUsernameResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationManagerByUsernameRequest getInvalidRequest() {
            return AssociateOrganizationManagerByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/managers")
                .requestPayload("fixtures/client/v2/organizations/PUT_{id}_managers_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_managers_response.json");
        }

        @Override
        protected AssociateOrganizationManagerByUsernameResponse getResponse() {
            return AssociateOrganizationManagerByUsernameResponse.builder()
                .metadata(Metadata.builder()
                    .id("8d2238e2-2fb3-4ede-b188-1fd3a533c4b4")
                    .url("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4")
                    .createdAt("2015-11-30T23:38:59Z")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("name-2523")
                    .billingEnabled(false)
                    .quotaDefinitionId("0e36ae22-a752-4e37-9dbf-0bac5c1b93c1")
                    .status("active")
                    .quotaDefinitionUrl("/v2/quota_definitions/0e36ae22-a752-4e37-9dbf-0bac5c1b93c1")
                    .spacesUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/spaces")
                    .domainsUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/domains")
                    .privateDomainsUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/private_domains")
                    .usersUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/users")
                    .managersUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/managers")
                    .billingManagersUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/billing_managers")
                    .auditorsUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/auditors")
                    .applicationEventsUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/app_events")
                    .spaceQuotaDefinitionsUrl("/v2/organizations/8d2238e2-2fb3-4ede-b188-1fd3a533c4b4/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationManagerByUsernameRequest getValidRequest() throws Exception {
            return AssociateOrganizationManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationManagerByUsernameResponse> invoke(AssociateOrganizationManagerByUsernameRequest request) {
            return this.organizations.associateManagerByUsername(request);
        }

    }

    public static final class AssociateOrganizationUser extends AbstractApiTest<AssociateOrganizationUserRequest, AssociateOrganizationUserResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationUserRequest getInvalidRequest() {
            return AssociateOrganizationUserRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/users/test-user-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_users_{user-id}_response.json");
        }

        @Override
        protected AssociateOrganizationUserResponse getResponse() {
            return AssociateOrganizationUserResponse.builder()
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
                    .spaceQuotaDefinitionsUrl("/v2/organizations/584664d0-e5bb-449b-bfe5-0136c30c4ff8/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationUserRequest getValidRequest() throws Exception {
            return AssociateOrganizationUserRequest.builder()
                .organizationId("test-organization-id")
                .userId("test-user-id")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationUserResponse> invoke(AssociateOrganizationUserRequest request) {
            return this.organizations.associateUser(request);
        }

    }

    public static final class AssociateOrganizationUserByUsername extends AbstractApiTest<AssociateOrganizationUserByUsernameRequest, AssociateOrganizationUserByUsernameResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationUserByUsernameRequest getInvalidRequest() {
            return AssociateOrganizationUserByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/users")
                .requestPayload("fixtures/client/v2/organizations/PUT_{id}_users_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_users_response.json");
        }

        @Override
        protected AssociateOrganizationUserByUsernameResponse getResponse() {
            return AssociateOrganizationUserByUsernameResponse.builder()
                .metadata(Metadata.builder()
                    .id("1a93417a-811a-46c7-85fa-4a0507c53f08")
                    .url("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08")
                    .createdAt("2015-11-30T23:38:59Z")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("name-2510")
                    .billingEnabled(false)
                    .quotaDefinitionId("4df84394-d265-4b78-a679-c31bb2e5379c")
                    .status("active")
                    .quotaDefinitionUrl("/v2/quota_definitions/4df84394-d265-4b78-a679-c31bb2e5379c")
                    .spacesUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/spaces")
                    .domainsUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/domains")
                    .privateDomainsUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/private_domains")
                    .usersUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/users")
                    .managersUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/managers")
                    .billingManagersUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/billing_managers")
                    .auditorsUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/auditors")
                    .applicationEventsUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/app_events")
                    .spaceQuotaDefinitionsUrl("/v2/organizations/1a93417a-811a-46c7-85fa-4a0507c53f08/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationUserByUsernameRequest getValidRequest() throws Exception {
            return AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationUserByUsernameResponse> invoke(AssociateOrganizationUserByUsernameRequest request) {
            return this.organizations.associateUserByUsername(request);
        }

    }

    public static final class AssociatePrivateDomain extends AbstractApiTest<AssociateOrganizationPrivateDomainRequest, AssociateOrganizationPrivateDomainResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateOrganizationPrivateDomainRequest getInvalidRequest() {
            return AssociateOrganizationPrivateDomainRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id/private_domains/test-private-domain-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_private_domains_{private-domain-id}_response.json");
        }

        @Override
        protected AssociateOrganizationPrivateDomainResponse getResponse() {
            return AssociateOrganizationPrivateDomainResponse.builder()
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
                    .spaceQuotaDefinitionsUrl("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7/space_quota_definitions")
                    .build())
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:10Z")
                    .id("676f9ff8-8c35-49ed-8ebf-fdf3db34cde7")
                    .url("/v2/organizations/676f9ff8-8c35-49ed-8ebf-fdf3db34cde7")
                    .build())
                .build();
        }

        @Override
        protected AssociateOrganizationPrivateDomainRequest getValidRequest() throws Exception {
            return AssociateOrganizationPrivateDomainRequest.builder()
                .organizationId("test-organization-id")
                .privateDomainId("test-private-domain-id")
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationPrivateDomainResponse> invoke(AssociateOrganizationPrivateDomainRequest request) {
            return this.organizations.associatePrivateDomain(request);
        }

    }

    public static final class Create extends AbstractApiTest<CreateOrganizationRequest, CreateOrganizationResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateOrganizationRequest getInvalidRequest() {
            return CreateOrganizationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/organizations")
                .requestPayload("fixtures/client/v2/organizations/POST_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/POST_response.json");
        }

        @Override
        protected CreateOrganizationResponse getResponse() {
            return CreateOrganizationResponse.builder()
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
                    .spaceQuotaDefinitionsUrl("/v2/organizations/137bfc86-5a2f-4759-9c0c-59ef614cd0be/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected CreateOrganizationRequest getValidRequest() throws Exception {
            return CreateOrganizationRequest.builder()
                .name("my-org-name")
                .quotaDefinitionId("ffc919cd-3e21-43a6-9e4e-62802d149cdb")
                .build();
        }

        @Override
        protected Mono<CreateOrganizationResponse> invoke(CreateOrganizationRequest request) {
            return this.organizations.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteOrganizationRequest, DeleteOrganizationResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteOrganizationRequest getInvalidRequest() {
            return DeleteOrganizationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id")
                .status(NO_CONTENT);
        }

        @Override
        protected DeleteOrganizationResponse getResponse() {
            return null;
        }

        @Override
        protected DeleteOrganizationRequest getValidRequest() throws Exception {
            return DeleteOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<DeleteOrganizationResponse> invoke(DeleteOrganizationRequest request) {
            return this.organizations.delete(request);
        }

    }

    public static final class DeleteAsync extends AbstractApiTest<DeleteOrganizationRequest, DeleteOrganizationResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteOrganizationRequest getInvalidRequest() {
            return DeleteOrganizationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id?async=true")
                .status(ACCEPTED)
                .responsePayload("fixtures/client/v2/organizations/DELETE_{id}_async_response.json");
        }

        @Override
        protected DeleteOrganizationResponse getResponse() {
            return DeleteOrganizationResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected DeleteOrganizationRequest getValidRequest() throws Exception {
            return DeleteOrganizationRequest.builder()
                .async(true)
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<DeleteOrganizationResponse> invoke(DeleteOrganizationRequest request) {
            return this.organizations.delete(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetOrganizationRequest, GetOrganizationResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetOrganizationRequest getInvalidRequest() {
            return GetOrganizationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_response.json");
        }

        @Override
        protected GetOrganizationResponse getResponse() {
            return GetOrganizationResponse.builder()
                .metadata(Metadata.builder()
                    .id("027616f3-66c4-412c-8214-7e43db2d587b")
                    .url("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b")
                    .createdAt("2015-07-27T22:43:11Z")
                    .build())
                .entity(OrganizationEntity.builder()
                    .name("name-240")
                    .billingEnabled(false)
                    .quotaDefinitionId("f3f0b830-d50a-4265-b8f8-3c430aa0313b")
                    .status("active")
                    .quotaDefinitionUrl("/v2/quota_definitions/f3f0b830-d50a-4265-b8f8-3c430aa0313b")
                    .spacesUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/spaces")
                    .domainsUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/domains")
                    .privateDomainsUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/private_domains")
                    .usersUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/users")
                    .managersUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/managers")
                    .billingManagersUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/billing_managers")
                    .auditorsUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/auditors")
                    .applicationEventsUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/app_events")
                    .spaceQuotaDefinitionsUrl("/v2/organizations/027616f3-66c4-412c-8214-7e43db2d587b/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected GetOrganizationRequest getValidRequest() throws Exception {
            return GetOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<GetOrganizationResponse> invoke(GetOrganizationRequest request) {
            return this.organizations.get(request);
        }

    }

    public static final class GetInstanceUsage extends AbstractApiTest<GetOrganizationInstanceUsageRequest, GetOrganizationInstanceUsageResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetOrganizationInstanceUsageRequest getInvalidRequest() {
            return GetOrganizationInstanceUsageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/instance_usage")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_instance_usage_response.json");
        }

        @Override
        protected GetOrganizationInstanceUsageResponse getResponse() {
            return GetOrganizationInstanceUsageResponse.builder()
                .instanceUsage(3)
                .build();
        }

        @Override
        protected GetOrganizationInstanceUsageRequest getValidRequest() throws Exception {
            return GetOrganizationInstanceUsageRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<GetOrganizationInstanceUsageResponse> invoke(GetOrganizationInstanceUsageRequest request) {
            return this.organizations.getInstanceUsage(request);
        }

    }

    public static final class GetMemoryUsage extends AbstractApiTest<GetOrganizationMemoryUsageRequest, GetOrganizationMemoryUsageResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetOrganizationMemoryUsageRequest getInvalidRequest() {
            return GetOrganizationMemoryUsageRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/memory_usage")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_memory_usage_response.json");
        }

        @Override
        protected GetOrganizationMemoryUsageResponse getResponse() {
            return GetOrganizationMemoryUsageResponse.builder()
                .memoryUsageInMb(0)
                .build();
        }

        @Override
        protected GetOrganizationMemoryUsageRequest getValidRequest() throws Exception {
            return GetOrganizationMemoryUsageRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<GetOrganizationMemoryUsageResponse> invoke(GetOrganizationMemoryUsageRequest request) {
            return this.organizations.getMemoryUsage(request);
        }

    }

    public static final class GetUserRoles extends AbstractApiTest<GetOrganizationUserRolesRequest, GetOrganizationUserRolesResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetOrganizationUserRolesRequest getInvalidRequest() {
            return GetOrganizationUserRolesRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/user_roles?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_user_roles_response.json");
        }

        @Override
        protected GetOrganizationUserRolesResponse getResponse() {
            return GetOrganizationUserRolesResponse.builder()
                .totalResults(2)
                .totalPages(2)
                .nextUrl("/v2/organizations/6fd1790e-0785-41ec-aff0-99915ce000c1/user_roles?order-direction=asc&page=2&results-per-page=1")
                .resource(UserOrganizationRoleResource.builder()
                    .metadata(Metadata.builder()
                        .id("uaa-id-92")
                        .url("/v2/users/uaa-id-92")
                        .createdAt("2015-07-27T22:43:10Z")
                        .build())
                    .entity(UserOrganizationRoleEntity.builder()
                        .admin(false)
                        .active(false)
                        .defaultSpaceId(null)
                        .username("everything@example.com")
                        .organizationRole("org_user")
                        .organizationRole("org_manager")
                        .organizationRole("org_auditor")
                        .organizationRole("billing_manager")
                        .spacesUrl("/v2/users/uaa-id-92/spaces")
                        .organizationsUrl("/v2/users/uaa-id-92/organizations")
                        .managedOrganizationsUrl("/v2/users/uaa-id-92/managed_organizations")
                        .billingManagedOrganizationsUrl("/v2/users/uaa-id-92/billing_managed_organizations")
                        .auditedOrganizationsUrl("/v2/users/uaa-id-92/audited_organizations")
                        .managedSpacesUrl("/v2/users/uaa-id-92/managed_spaces")
                        .auditedSpacesUrl("/v2/users/uaa-id-92/audited_spaces")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected GetOrganizationUserRolesRequest getValidRequest() throws Exception {
            return GetOrganizationUserRolesRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<GetOrganizationUserRolesResponse> invoke(GetOrganizationUserRolesRequest request) {
            return this.organizations.getUserRoles(request);
        }

    }

    public static final class List extends AbstractApiTest<ListOrganizationsRequest, ListOrganizationsResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_response.json");
        }

        @Override
        protected ListOrganizationsResponse getResponse() {
            return ListOrganizationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(OrganizationResource.builder()
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
                        .privateDomainsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/private_domains")
                        .usersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/users")
                        .managersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/managers")
                        .billingManagersUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/billing_managers")
                        .auditorsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/auditors")
                        .applicationEventsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/app_events")
                        .spaceQuotaDefinitionsUrl("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/space_quota_definitions")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListOrganizationsRequest getValidRequest() throws Exception {
            return ListOrganizationsRequest.builder()
                .name("test-name")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationsResponse> invoke(ListOrganizationsRequest request) {
            return this.organizations.list(request);
        }

    }

    public static final class ListAuditors extends AbstractApiTest<ListOrganizationAuditorsRequest, ListOrganizationAuditorsResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationAuditorsRequest getInvalidRequest() {
            return ListOrganizationAuditorsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/auditors?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_auditors_response.json");
        }

        @Override
        protected ListOrganizationAuditorsResponse getResponse() {
            return ListOrganizationAuditorsResponse.builder()
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
        }

        @Override
        protected ListOrganizationAuditorsRequest getValidRequest() throws Exception {
            return ListOrganizationAuditorsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationAuditorsResponse> invoke(ListOrganizationAuditorsRequest request) {
            return this.organizations.listAuditors(request);
        }

    }

    public static final class ListBillingManagers extends AbstractApiTest<ListOrganizationBillingManagersRequest, ListOrganizationBillingManagersResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationBillingManagersRequest getInvalidRequest() {
            return ListOrganizationBillingManagersRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/billing_managers?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_billing_managers_response.json");
        }

        @Override
        protected ListOrganizationBillingManagersResponse getResponse() {
            return ListOrganizationBillingManagersResponse.builder()
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
        }

        @Override
        protected ListOrganizationBillingManagersRequest getValidRequest() throws Exception {
            return ListOrganizationBillingManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationBillingManagersResponse> invoke(ListOrganizationBillingManagersRequest request) {
            return this.organizations.listBillingManagers(request);
        }

    }

    public static final class ListManagers extends AbstractApiTest<ListOrganizationManagersRequest, ListOrganizationManagersResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationManagersRequest getInvalidRequest() {
            return ListOrganizationManagersRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/managers?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_managers_response.json");
        }

        @Override
        protected ListOrganizationManagersResponse getResponse() {
            return ListOrganizationManagersResponse.builder()
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
        }

        @Override
        protected ListOrganizationManagersRequest getValidRequest() throws Exception {
            return ListOrganizationManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationManagersResponse> invoke(ListOrganizationManagersRequest request) {
            return this.organizations.listManagers(request);
        }

    }

    public static final class ListPrivateDomains extends AbstractApiTest<ListOrganizationPrivateDomainsRequest, ListOrganizationPrivateDomainsResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationPrivateDomainsRequest getInvalidRequest() {
            return ListOrganizationPrivateDomainsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/private_domains?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_private_domains_response.json");
        }

        @Override
        protected ListOrganizationPrivateDomainsResponse getResponse() {
            return ListOrganizationPrivateDomainsResponse.builder()
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
        }

        @Override
        protected ListOrganizationPrivateDomainsRequest getValidRequest() throws Exception {
            return ListOrganizationPrivateDomainsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationPrivateDomainsResponse> invoke(ListOrganizationPrivateDomainsRequest request) {
            return this.organizations.listPrivateDomains(request);
        }

    }

    public static final class ListDomains extends AbstractApiTest<ListOrganizationDomainsRequest, ListOrganizationDomainsResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationDomainsRequest getInvalidRequest() {
            return ListOrganizationDomainsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/domains?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_domains_response.json");
        }

        @Override
        protected ListOrganizationDomainsResponse getResponse() {
            return ListOrganizationDomainsResponse.builder()
                .totalResults(2)
                .totalPages(1)
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("0010dd3b-aca0-4647-87d3-679059d67600")
                        .url("/v2/domains/0010dd3b-aca0-4647-87d3-679059d67600")
                        .createdAt("2016-01-26T22:20:04Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("customer-app-domain1.com")
                        .build())
                    .build())
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("e366a4ce-73d2-4a5a-8194-04c8ff4adfe1")
                        .url("/v2/domains/e366a4ce-73d2-4a5a-8194-04c8ff4adfe1")
                        .createdAt("2016-01-26T22:20:04Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("customer-app-domain2.com")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListOrganizationDomainsRequest getValidRequest() throws Exception {
            return ListOrganizationDomainsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationDomainsResponse> invoke(ListOrganizationDomainsRequest request) {
            return this.organizations.listDomains(request);
        }

    }

    public static final class ListServices extends AbstractApiTest<ListOrganizationServicesRequest, ListOrganizationServicesResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationServicesRequest getInvalidRequest() {
            return ListOrganizationServicesRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/services?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_services_response.json");
        }

        @Override
        protected ListOrganizationServicesResponse getResponse() {
            return ListOrganizationServicesResponse.builder()
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
        }

        @Override
        protected ListOrganizationServicesRequest getValidRequest() throws Exception {
            return ListOrganizationServicesRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationServicesResponse> invoke(ListOrganizationServicesRequest request) {
            return this.organizations.listServices(request);
        }

    }

    public static final class ListSpaceQuotaDefinitions extends AbstractApiTest<ListOrganizationSpaceQuotaDefinitionsRequest, ListOrganizationSpaceQuotaDefinitionsResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationSpaceQuotaDefinitionsRequest getInvalidRequest() {
            return ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/space_quota_definitions?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_space_quota_definitions_response.json");
        }

        @Override
        protected ListOrganizationSpaceQuotaDefinitionsResponse getResponse() {
            return ListOrganizationSpaceQuotaDefinitionsResponse.builder()
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
        }

        @Override
        protected ListOrganizationSpaceQuotaDefinitionsRequest getValidRequest() throws Exception {
            return ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationSpaceQuotaDefinitionsResponse> invoke(ListOrganizationSpaceQuotaDefinitionsRequest request) {
            return this.organizations.listSpaceQuotaDefinitions(request);
        }

    }

    public static final class ListSpaces extends AbstractApiTest<ListOrganizationSpacesRequest, ListOrganizationSpacesResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationSpacesRequest getInvalidRequest() {
            return ListOrganizationSpacesRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/spaces?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_spaces_response.json");
        }

        @Override
        protected ListOrganizationSpacesResponse getResponse() {
            return ListOrganizationSpacesResponse.builder()
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
                        .serviceInstancesUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/service_instances")
                        .applicationEventsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/app_events")
                        .eventsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/events")
                        .securityGroupsUrl("/v2/spaces/9f6ce6e0-e3db-42ae-9572-bbc38f4f541b/security_groups")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListOrganizationSpacesRequest getValidRequest() throws Exception {
            return ListOrganizationSpacesRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationSpacesResponse> invoke(ListOrganizationSpacesRequest request) {
            return this.organizations.listSpaces(request);
        }

    }

    public static final class ListUsers extends AbstractApiTest<ListOrganizationUsersRequest, ListOrganizationUsersResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationUsersRequest getInvalidRequest() {
            return ListOrganizationUsersRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/users?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_users_response.json");
        }

        @Override
        protected ListOrganizationUsersResponse getResponse() {
            return ListOrganizationUsersResponse.builder()
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
        }

        @Override
        protected ListOrganizationUsersRequest getValidRequest() throws Exception {
            return ListOrganizationUsersRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListOrganizationUsersResponse> invoke(ListOrganizationUsersRequest request) {
            return this.organizations.listUsers(request);
        }

    }

    public static final class RemoveAuditor extends AbstractApiTest<RemoveOrganizationAuditorRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationAuditorRequest getInvalidRequest() {
            return RemoveOrganizationAuditorRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/auditors/test-auditor-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationAuditorRequest getValidRequest() throws Exception {
            return RemoveOrganizationAuditorRequest.builder()
                .auditorId("test-auditor-id")
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationAuditorRequest request) {
            return this.organizations.removeAuditor(request);
        }

    }

    public static final class RemoveAuditorByUsername extends AbstractApiTest<RemoveOrganizationAuditorByUsernameRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationAuditorByUsernameRequest getInvalidRequest() {
            return RemoveOrganizationAuditorByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/auditors")
                .requestPayload("fixtures/client/v2/organizations/DELETE_{id}_auditors_request.json")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationAuditorByUsernameRequest getValidRequest() throws Exception {
            return RemoveOrganizationAuditorByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("auditor@example.com")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationAuditorByUsernameRequest request) {
            return this.organizations.removeAuditorByUsername(request);
        }

    }

    public static final class RemoveBillingManager extends AbstractApiTest<RemoveOrganizationBillingManagerRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationBillingManagerRequest getInvalidRequest() {
            return RemoveOrganizationBillingManagerRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/billing_managers/test-billing-manager-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationBillingManagerRequest getValidRequest() throws Exception {
            return RemoveOrganizationBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationBillingManagerRequest request) {
            return this.organizations.removeBillingManager(request);
        }

    }

    public static final class RemoveManager extends AbstractApiTest<RemoveOrganizationManagerRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationManagerRequest getInvalidRequest() {
            return RemoveOrganizationManagerRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/managers/test-manager-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationManagerRequest getValidRequest() throws Exception {
            return RemoveOrganizationManagerRequest.builder()
                .organizationId("test-organization-id")
                .managerId("test-manager-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationManagerRequest request) {
            return this.organizations.removeManager(request);
        }

    }

    public static final class RemoveOrganizationBillingManagerByUsername extends AbstractApiTest<RemoveOrganizationBillingManagerByUsernameRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationBillingManagerByUsernameRequest getInvalidRequest() {
            return RemoveOrganizationBillingManagerByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/billing_managers")
                .requestPayload("fixtures/client/v2/organizations/DELETE_{id}_billing_managers_request.json")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationBillingManagerByUsernameRequest getValidRequest() throws Exception {
            return RemoveOrganizationBillingManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("billing_manager@example.com")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationBillingManagerByUsernameRequest request) {
            return this.organizations.removeBillingManagerByUsername(request);
        }

    }

    public static final class RemoveOrganizationManagerByUsername extends AbstractApiTest<RemoveOrganizationManagerByUsernameRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationManagerByUsernameRequest getInvalidRequest() {
            return RemoveOrganizationManagerByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/managers")
                .requestPayload("fixtures/client/v2/organizations/DELETE_{id}_managers_request.json")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationManagerByUsernameRequest getValidRequest() throws Exception {
            return RemoveOrganizationManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("manage@example.com")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationManagerByUsernameRequest request) {
            return this.organizations.removeManagerByUsername(request);
        }

    }

    public static final class RemovePrivateDomain extends AbstractApiTest<RemoveOrganizationPrivateDomainRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationPrivateDomainRequest getInvalidRequest() {
            return RemoveOrganizationPrivateDomainRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/private_domains/test-private-domain-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationPrivateDomainRequest getValidRequest() throws Exception {
            return RemoveOrganizationPrivateDomainRequest.builder()
                .organizationId("test-organization-id")
                .privateDomainId("test-private-domain-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationPrivateDomainRequest request) {
            return this.organizations.removePrivateDomain(request);
        }
    }

    public static final class RemoveUser extends AbstractApiTest<RemoveOrganizationUserRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationUserRequest getInvalidRequest() {
            return RemoveOrganizationUserRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/users/test-user-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationUserRequest getValidRequest() throws Exception {
            return RemoveOrganizationUserRequest.builder()
                .organizationId("test-organization-id")
                .userId("test-user-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationUserRequest request) {
            return this.organizations.removeUser(request);
        }
    }

    public static final class RemoveUserByUsername extends AbstractApiTest<RemoveOrganizationUserByUsernameRequest, Void> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveOrganizationUserByUsernameRequest getInvalidRequest() {
            return RemoveOrganizationUserByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/organizations/test-organization-id/users")
                .requestPayload("fixtures/client/v2/organizations/DELETE_{id}_users_request.json")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveOrganizationUserByUsernameRequest getValidRequest() throws Exception {
            return RemoveOrganizationUserByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationUserByUsernameRequest request) {
            return this.organizations.removeUserByUsername(request);
        }

    }

    public static final class Summary extends AbstractApiTest<SummaryOrganizationRequest, SummaryOrganizationResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected SummaryOrganizationRequest getInvalidRequest() {
            return SummaryOrganizationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/organizations/test-organization-id/summary")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/GET_{id}_summary_response.json");
        }

        @Override
        protected SummaryOrganizationResponse getResponse() {
            return SummaryOrganizationResponse.builder()
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
                    .build())
                .build();
        }

        @Override
        protected SummaryOrganizationRequest getValidRequest() throws Exception {
            return SummaryOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

        @Override
        protected Mono<SummaryOrganizationResponse> invoke(SummaryOrganizationRequest request) {
            return this.organizations.summary(request);
        }

    }

    public static final class Update extends AbstractApiTest<UpdateOrganizationRequest, UpdateOrganizationResponse> {

        private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateOrganizationRequest getInvalidRequest() {
            return UpdateOrganizationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/organizations/test-organization-id")
                .requestPayload("fixtures/client/v2/organizations/PUT_{id}_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/organizations/PUT_{id}_response.json");
        }

        @Override
        protected UpdateOrganizationResponse getResponse() {
            return UpdateOrganizationResponse.builder()
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
                    .spaceQuotaDefinitionsUrl("/v2/organizations/31a539be-dbfd-4db6-aec1-6565ebe975ed/space_quota_definitions")
                    .build())
                .build();
        }

        @Override
        protected UpdateOrganizationRequest getValidRequest() throws Exception {
            return UpdateOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .name("New Organization Name")
                .quotaDefinitionId("7df44b58-1834-486f-aed8-d5d97126e603")
                .build();
        }

        @Override
        protected Mono<UpdateOrganizationResponse> invoke(UpdateOrganizationRequest request) {
            return this.organizations.update(request);
        }

    }

}
