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

package org.cloudfoundry.reactor.client.v2.organizations;

import org.cloudfoundry.client.v2.Metadata;
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
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorOrganizationsTest {

    public static final class AssociateOrganizationAuditor extends AbstractClientApiTest<AssociateOrganizationAuditorRequest, AssociateOrganizationAuditorResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationAuditorResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationAuditorResponse>create()
                .expectNext(AssociateOrganizationAuditorResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/auditors/uaa-id-71")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_auditors_{auditor-id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationAuditorResponse> invoke(AssociateOrganizationAuditorRequest request) {
            return this.organizations.associateAuditor(request);
        }

        @Override
        protected AssociateOrganizationAuditorRequest validRequest() {
            return AssociateOrganizationAuditorRequest.builder()
                .auditorId("uaa-id-71")
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class AssociateOrganizationAuditorByUsername extends AbstractClientApiTest<AssociateOrganizationAuditorByUsernameRequest, AssociateOrganizationAuditorByUsernameResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationAuditorByUsernameResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationAuditorByUsernameResponse>create()
                .expectNext(AssociateOrganizationAuditorByUsernameResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/auditors")
                    .payload("fixtures/client/v2/organizations/PUT_{id}_auditors_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_auditors_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationAuditorByUsernameResponse> invoke(AssociateOrganizationAuditorByUsernameRequest request) {
            return this.organizations.associateAuditorByUsername(request);
        }

        @Override
        protected AssociateOrganizationAuditorByUsernameRequest validRequest() {
            return AssociateOrganizationAuditorByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

    }

    public static final class AssociateOrganizationBillingManager extends AbstractClientApiTest<AssociateOrganizationBillingManagerRequest, AssociateOrganizationBillingManagerResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationBillingManagerResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationBillingManagerResponse>create()
                .expectNext(AssociateOrganizationBillingManagerResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/billing_managers/test-billing-manager-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_billing_managers_{billing-manager-id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationBillingManagerResponse> invoke(AssociateOrganizationBillingManagerRequest request) {
            return this.organizations.associateBillingManager(request);
        }

        @Override
        protected AssociateOrganizationBillingManagerRequest validRequest() {
            return AssociateOrganizationBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class AssociateOrganizationBillingManagerByUsername
        extends AbstractClientApiTest<AssociateOrganizationBillingManagerByUsernameRequest, AssociateOrganizationBillingManagerByUsernameResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationBillingManagerByUsernameResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationBillingManagerByUsernameResponse>create()
                .expectNext(AssociateOrganizationBillingManagerByUsernameResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/billing_managers")
                    .payload("fixtures/client/v2/organizations/PUT_{id}_billing_managers_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_billing_managers_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationBillingManagerByUsernameResponse> invoke(AssociateOrganizationBillingManagerByUsernameRequest request) {
            return this.organizations.associateBillingManagerByUsername(request);
        }

        @Override
        protected AssociateOrganizationBillingManagerByUsernameRequest validRequest() {
            return AssociateOrganizationBillingManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

    }

    public static final class AssociateOrganizationManager extends AbstractClientApiTest<AssociateOrganizationManagerRequest, AssociateOrganizationManagerResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationManagerResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationManagerResponse>create()
                .expectNext(AssociateOrganizationManagerResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/managers/test-manager-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_managers_{manager-id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationManagerResponse> invoke(AssociateOrganizationManagerRequest request) {
            return this.organizations.associateManager(request);
        }

        @Override
        protected AssociateOrganizationManagerRequest validRequest() {
            return AssociateOrganizationManagerRequest.builder()
                .organizationId("test-organization-id")
                .managerId("test-manager-id")
                .build();
        }

    }

    public static final class AssociateOrganizationManagerByUsername extends AbstractClientApiTest<AssociateOrganizationManagerByUsernameRequest, AssociateOrganizationManagerByUsernameResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationManagerByUsernameResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationManagerByUsernameResponse>create()
                .expectNext(AssociateOrganizationManagerByUsernameResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/managers")
                    .payload("fixtures/client/v2/organizations/PUT_{id}_managers_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_managers_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationManagerByUsernameResponse> invoke(AssociateOrganizationManagerByUsernameRequest request) {
            return this.organizations.associateManagerByUsername(request);
        }

        @Override
        protected AssociateOrganizationManagerByUsernameRequest validRequest() {
            return AssociateOrganizationManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

    }

    public static final class AssociateOrganizationUser extends AbstractClientApiTest<AssociateOrganizationUserRequest, AssociateOrganizationUserResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationUserResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationUserResponse>create()
                .expectNext(AssociateOrganizationUserResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/users/test-user-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_users_{user-id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationUserResponse> invoke(AssociateOrganizationUserRequest request) {
            return this.organizations.associateUser(request);
        }

        @Override
        protected AssociateOrganizationUserRequest validRequest() {
            return AssociateOrganizationUserRequest.builder()
                .organizationId("test-organization-id")
                .userId("test-user-id")
                .build();
        }

    }

    public static final class AssociateOrganizationUserByUsername extends AbstractClientApiTest<AssociateOrganizationUserByUsernameRequest, AssociateOrganizationUserByUsernameResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationUserByUsernameResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationUserByUsernameResponse>create()
                .expectNext(AssociateOrganizationUserByUsernameResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/users")
                    .payload("fixtures/client/v2/organizations/PUT_{id}_users_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_users_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationUserByUsernameResponse> invoke(AssociateOrganizationUserByUsernameRequest request) {
            return this.organizations.associateUserByUsername(request);
        }

        @Override
        protected AssociateOrganizationUserByUsernameRequest validRequest() {
            return AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

    }

    public static final class AssociatePrivateDomain extends AbstractClientApiTest<AssociateOrganizationPrivateDomainRequest, AssociateOrganizationPrivateDomainResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<AssociateOrganizationPrivateDomainResponse> expectations() {
            return ScriptedSubscriber.<AssociateOrganizationPrivateDomainResponse>create()
                .expectNext(AssociateOrganizationPrivateDomainResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id/private_domains/test-private-domain-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_private_domains_{private-domain-id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<AssociateOrganizationPrivateDomainResponse> invoke(AssociateOrganizationPrivateDomainRequest request) {
            return this.organizations.associatePrivateDomain(request);
        }

        @Override
        protected AssociateOrganizationPrivateDomainRequest validRequest() {
            return AssociateOrganizationPrivateDomainRequest.builder()
                .organizationId("test-organization-id")
                .privateDomainId("test-private-domain-id")
                .build();
        }

    }

    public static final class Create extends AbstractClientApiTest<CreateOrganizationRequest, CreateOrganizationResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CreateOrganizationResponse> expectations() {
            return ScriptedSubscriber.<CreateOrganizationResponse>create()
                .expectNext(CreateOrganizationResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/organizations")
                    .payload("fixtures/client/v2/organizations/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateOrganizationResponse> invoke(CreateOrganizationRequest request) {
            return this.organizations.create(request);
        }

        @Override
        protected CreateOrganizationRequest validRequest() {
            return CreateOrganizationRequest.builder()
                .name("my-org-name")
                .quotaDefinitionId("ffc919cd-3e21-43a6-9e4e-62802d149cdb")
                .build();
        }

    }

    public static final class Delete extends AbstractClientApiTest<DeleteOrganizationRequest, DeleteOrganizationResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteOrganizationResponse> expectations() {
            return ScriptedSubscriber.<DeleteOrganizationResponse>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteOrganizationResponse> invoke(DeleteOrganizationRequest request) {
            return this.organizations.delete(request);
        }

        @Override
        protected DeleteOrganizationRequest validRequest() {
            return DeleteOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class DeleteAsync extends AbstractClientApiTest<DeleteOrganizationRequest, DeleteOrganizationResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteOrganizationResponse> expectations() {
            return ScriptedSubscriber.<DeleteOrganizationResponse>create()
                .expectNext(DeleteOrganizationResponse.builder()
                    .metadata(Metadata.builder()
                        .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .createdAt("2016-02-02T17:16:31Z")
                        .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .build())
                    .entity(JobEntity.builder()
                        .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                        .status("queued")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id?async=true")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v2/organizations/DELETE_{id}_async_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteOrganizationResponse> invoke(DeleteOrganizationRequest request) {
            return this.organizations.delete(request);
        }

        @Override
        protected DeleteOrganizationRequest validRequest() {
            return DeleteOrganizationRequest.builder()
                .async(true)
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class Get extends AbstractClientApiTest<GetOrganizationRequest, GetOrganizationResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetOrganizationResponse> expectations() {
            return ScriptedSubscriber.<GetOrganizationResponse>create()
                .expectNext(GetOrganizationResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetOrganizationResponse> invoke(GetOrganizationRequest request) {
            return this.organizations.get(request);
        }

        @Override
        protected GetOrganizationRequest validRequest() {
            return GetOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class GetInstanceUsage extends AbstractClientApiTest<GetOrganizationInstanceUsageRequest, GetOrganizationInstanceUsageResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetOrganizationInstanceUsageResponse> expectations() {
            return ScriptedSubscriber.<GetOrganizationInstanceUsageResponse>create()
                .expectNext(GetOrganizationInstanceUsageResponse.builder()
                    .instanceUsage(3)
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/instance_usage")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_instance_usage_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetOrganizationInstanceUsageResponse> invoke(GetOrganizationInstanceUsageRequest request) {
            return this.organizations.getInstanceUsage(request);
        }

        @Override
        protected GetOrganizationInstanceUsageRequest validRequest() {
            return GetOrganizationInstanceUsageRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class GetMemoryUsage extends AbstractClientApiTest<GetOrganizationMemoryUsageRequest, GetOrganizationMemoryUsageResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetOrganizationMemoryUsageResponse> expectations() {
            return ScriptedSubscriber.<GetOrganizationMemoryUsageResponse>create()
                .expectNext(GetOrganizationMemoryUsageResponse.builder()
                    .memoryUsageInMb(0)
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/memory_usage")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_memory_usage_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetOrganizationMemoryUsageResponse> invoke(GetOrganizationMemoryUsageRequest request) {
            return this.organizations.getMemoryUsage(request);
        }

        @Override
        protected GetOrganizationMemoryUsageRequest validRequest() {
            return GetOrganizationMemoryUsageRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class GetUserRoles extends AbstractClientApiTest<GetOrganizationUserRolesRequest, GetOrganizationUserRolesResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetOrganizationUserRolesResponse> expectations() {
            return ScriptedSubscriber.<GetOrganizationUserRolesResponse>create()
                .expectNext(GetOrganizationUserRolesResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/user_roles?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_user_roles_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<GetOrganizationUserRolesResponse> invoke(GetOrganizationUserRolesRequest request) {
            return this.organizations.getUserRoles(request);
        }

        @Override
        protected GetOrganizationUserRolesRequest validRequest() {
            return GetOrganizationUserRolesRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class List extends AbstractClientApiTest<ListOrganizationsRequest, ListOrganizationsResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationsResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationsResponse>create()
                .expectNext(ListOrganizationsResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations?q=name%20IN%20test-name&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationsResponse> invoke(ListOrganizationsRequest request) {
            return this.organizations.list(request);
        }

        @Override
        protected ListOrganizationsRequest validRequest() {
            return ListOrganizationsRequest.builder()
                .name("test-name")
                .page(-1)
                .build();
        }

    }

    public static final class ListAuditors extends AbstractClientApiTest<ListOrganizationAuditorsRequest, ListOrganizationAuditorsResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationAuditorsResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationAuditorsResponse>create()
                .expectNext(ListOrganizationAuditorsResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/auditors?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_auditors_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationAuditorsResponse> invoke(ListOrganizationAuditorsRequest request) {
            return this.organizations.listAuditors(request);
        }

        @Override
        protected ListOrganizationAuditorsRequest validRequest() {
            return ListOrganizationAuditorsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListBillingManagers extends AbstractClientApiTest<ListOrganizationBillingManagersRequest, ListOrganizationBillingManagersResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationBillingManagersResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationBillingManagersResponse>create()
                .expectNext(ListOrganizationBillingManagersResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/billing_managers?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_billing_managers_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationBillingManagersResponse> invoke(ListOrganizationBillingManagersRequest request) {
            return this.organizations.listBillingManagers(request);
        }

        @Override
        protected ListOrganizationBillingManagersRequest validRequest() {
            return ListOrganizationBillingManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListDomains extends AbstractClientApiTest<ListOrganizationDomainsRequest, ListOrganizationDomainsResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationDomainsResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationDomainsResponse>create()
                .expectNext(ListOrganizationDomainsResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/domains?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_domains_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationDomainsResponse> invoke(ListOrganizationDomainsRequest request) {
            return this.organizations.listDomains(request);
        }

        @Override
        protected ListOrganizationDomainsRequest validRequest() {
            return ListOrganizationDomainsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListManagers extends AbstractClientApiTest<ListOrganizationManagersRequest, ListOrganizationManagersResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationManagersResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationManagersResponse>create()
                .expectNext(ListOrganizationManagersResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/managers?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_managers_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationManagersResponse> invoke(ListOrganizationManagersRequest request) {
            return this.organizations.listManagers(request);
        }

        @Override
        protected ListOrganizationManagersRequest validRequest() {
            return ListOrganizationManagersRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListPrivateDomains extends AbstractClientApiTest<ListOrganizationPrivateDomainsRequest, ListOrganizationPrivateDomainsResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationPrivateDomainsResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationPrivateDomainsResponse>create()
                .expectNext(ListOrganizationPrivateDomainsResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/private_domains?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_private_domains_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationPrivateDomainsResponse> invoke(ListOrganizationPrivateDomainsRequest request) {
            return this.organizations.listPrivateDomains(request);
        }

        @Override
        protected ListOrganizationPrivateDomainsRequest validRequest() {
            return ListOrganizationPrivateDomainsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListServices extends AbstractClientApiTest<ListOrganizationServicesRequest, ListOrganizationServicesResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationServicesResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationServicesResponse>create()
                .expectNext(ListOrganizationServicesResponse.builder()
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
                            .requires(Collections.emptyList())
                            .uniqueId("4d4e8356-f753-433e-8514-88ee78c4e153")
                            .serviceBrokerId("bf5b5cf7-acac-426b-8e79-cc57a227cd3c")
                            .planUpdateable(false)
                            .servicePlansUrl("/v2/services/5529fa75-d2f3-426c-b864-4ae45c3622da/service_plans")
                            .tags(Collections.emptyList())
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/services?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_services_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationServicesResponse> invoke(ListOrganizationServicesRequest request) {
            return this.organizations.listServices(request);
        }

        @Override
        protected ListOrganizationServicesRequest validRequest() {
            return ListOrganizationServicesRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListSpaceQuotaDefinitions extends AbstractClientApiTest<ListOrganizationSpaceQuotaDefinitionsRequest, ListOrganizationSpaceQuotaDefinitionsResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationSpaceQuotaDefinitionsResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationSpaceQuotaDefinitionsResponse>create()
                .expectNext(ListOrganizationSpaceQuotaDefinitionsResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/space_quota_definitions?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_space_quota_definitions_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationSpaceQuotaDefinitionsResponse> invoke(ListOrganizationSpaceQuotaDefinitionsRequest request) {
            return this.organizations.listSpaceQuotaDefinitions(request);
        }

        @Override
        protected ListOrganizationSpaceQuotaDefinitionsRequest validRequest() {
            return ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListSpaces extends AbstractClientApiTest<ListOrganizationSpacesRequest, ListOrganizationSpacesResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationSpacesResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationSpacesResponse>create()
                .expectNext(ListOrganizationSpacesResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/spaces?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_spaces_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationSpacesResponse> invoke(ListOrganizationSpacesRequest request) {
            return this.organizations.listSpaces(request);
        }

        @Override
        protected ListOrganizationSpacesRequest validRequest() {
            return ListOrganizationSpacesRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class ListUsers extends AbstractClientApiTest<ListOrganizationUsersRequest, ListOrganizationUsersResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListOrganizationUsersResponse> expectations() {
            return ScriptedSubscriber.<ListOrganizationUsersResponse>create()
                .expectNext(ListOrganizationUsersResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/users?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_users_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListOrganizationUsersResponse> invoke(ListOrganizationUsersRequest request) {
            return this.organizations.listUsers(request);
        }

        @Override
        protected ListOrganizationUsersRequest validRequest() {
            return ListOrganizationUsersRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build();
        }

    }

    public static final class RemoveAuditor extends AbstractClientApiTest<RemoveOrganizationAuditorRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/auditors/test-auditor-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationAuditorRequest request) {
            return this.organizations.removeAuditor(request);
        }

        @Override
        protected RemoveOrganizationAuditorRequest validRequest() {
            return RemoveOrganizationAuditorRequest.builder()
                .auditorId("test-auditor-id")
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class RemoveAuditorByUsername extends AbstractClientApiTest<RemoveOrganizationAuditorByUsernameRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/auditors")
                    .payload("fixtures/client/v2/organizations/DELETE_{id}_auditors_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationAuditorByUsernameRequest request) {
            return this.organizations.removeAuditorByUsername(request);
        }

        @Override
        protected RemoveOrganizationAuditorByUsernameRequest validRequest() {
            return RemoveOrganizationAuditorByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("auditor@example.com")
                .build();
        }

    }

    public static final class RemoveBillingManager extends AbstractClientApiTest<RemoveOrganizationBillingManagerRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/billing_managers/test-billing-manager-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationBillingManagerRequest request) {
            return this.organizations.removeBillingManager(request);
        }

        @Override
        protected RemoveOrganizationBillingManagerRequest validRequest() {
            return RemoveOrganizationBillingManagerRequest.builder()
                .billingManagerId("test-billing-manager-id")
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class RemoveManager extends AbstractClientApiTest<RemoveOrganizationManagerRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/managers/test-manager-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationManagerRequest request) {
            return this.organizations.removeManager(request);
        }

        @Override
        protected RemoveOrganizationManagerRequest validRequest() {
            return RemoveOrganizationManagerRequest.builder()
                .organizationId("test-organization-id")
                .managerId("test-manager-id")
                .build();
        }

    }

    public static final class RemoveOrganizationBillingManagerByUsername extends AbstractClientApiTest<RemoveOrganizationBillingManagerByUsernameRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/billing_managers")
                    .payload("fixtures/client/v2/organizations/DELETE_{id}_billing_managers_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationBillingManagerByUsernameRequest request) {
            return this.organizations.removeBillingManagerByUsername(request);
        }

        @Override
        protected RemoveOrganizationBillingManagerByUsernameRequest validRequest() {
            return RemoveOrganizationBillingManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("billing_manager@example.com")
                .build();
        }

    }

    public static final class RemoveOrganizationManagerByUsername extends AbstractClientApiTest<RemoveOrganizationManagerByUsernameRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/managers")
                    .payload("fixtures/client/v2/organizations/DELETE_{id}_managers_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationManagerByUsernameRequest request) {
            return this.organizations.removeManagerByUsername(request);
        }

        @Override
        protected RemoveOrganizationManagerByUsernameRequest validRequest() {
            return RemoveOrganizationManagerByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("manage@example.com")
                .build();
        }

    }

    public static final class RemovePrivateDomain extends AbstractClientApiTest<RemoveOrganizationPrivateDomainRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/private_domains/test-private-domain-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationPrivateDomainRequest request) {
            return this.organizations.removePrivateDomain(request);
        }

        @Override
        protected RemoveOrganizationPrivateDomainRequest validRequest() {
            return RemoveOrganizationPrivateDomainRequest.builder()
                .organizationId("test-organization-id")
                .privateDomainId("test-private-domain-id")
                .build();
        }
    }

    public static final class RemoveUser extends AbstractClientApiTest<RemoveOrganizationUserRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/users/test-user-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationUserRequest request) {
            return this.organizations.removeUser(request);
        }

        @Override
        protected RemoveOrganizationUserRequest validRequest() {
            return RemoveOrganizationUserRequest.builder()
                .organizationId("test-organization-id")
                .userId("test-user-id")
                .build();
        }
    }

    public static final class RemoveUserByUsername extends AbstractClientApiTest<RemoveOrganizationUserByUsernameRequest, Void> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<Void> expectations() {
            return ScriptedSubscriber.<Void>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/organizations/test-organization-id/users")
                    .payload("fixtures/client/v2/organizations/DELETE_{id}_users_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveOrganizationUserByUsernameRequest request) {
            return this.organizations.removeUserByUsername(request);
        }

        @Override
        protected RemoveOrganizationUserByUsernameRequest validRequest() {
            return RemoveOrganizationUserByUsernameRequest.builder()
                .organizationId("test-organization-id")
                .username("user@example.com")
                .build();
        }

    }

    public static final class Summary extends AbstractClientApiTest<SummaryOrganizationRequest, SummaryOrganizationResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<SummaryOrganizationResponse> expectations() {
            return ScriptedSubscriber.<SummaryOrganizationResponse>create()
                .expectNext(SummaryOrganizationResponse.builder()
                    .id("525a6450-9202-4ea1-beca-6fdda210710e")
                    .name("name-357")
                    .status("active")
                    .space(OrganizationSpaceSummary.builder()
                        .id("dec1c7b0-8ea6-488a-9410-f73649d30228")
                        .name("name-359")
                        .serviceCount(0)
                        .applicationCount(0)
                        .memoryDevelopmentTotal(0)
                        .memoryProductionTotal(0)
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/organizations/test-organization-id/summary")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/GET_{id}_summary_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<SummaryOrganizationResponse> invoke(SummaryOrganizationRequest request) {
            return this.organizations.summary(request);
        }

        @Override
        protected SummaryOrganizationRequest validRequest() {
            return SummaryOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .build();
        }

    }

    public static final class Update extends AbstractClientApiTest<UpdateOrganizationRequest, UpdateOrganizationResponse> {

        private final ReactorOrganizations organizations = new ReactorOrganizations(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<UpdateOrganizationResponse> expectations() {
            return ScriptedSubscriber.<UpdateOrganizationResponse>create()
                .expectNext(UpdateOrganizationResponse.builder()
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
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/organizations/test-organization-id")
                    .payload("fixtures/client/v2/organizations/PUT_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/organizations/PUT_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<UpdateOrganizationResponse> invoke(UpdateOrganizationRequest request) {
            return this.organizations.update(request);
        }

        @Override
        protected UpdateOrganizationRequest validRequest() {
            return UpdateOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .name("New Organization Name")
                .quotaDefinitionId("7df44b58-1834-486f-aed8-d5d97126e603")
                .build();
        }

    }

}
