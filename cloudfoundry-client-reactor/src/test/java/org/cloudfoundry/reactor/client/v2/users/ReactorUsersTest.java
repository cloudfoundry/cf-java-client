/*
 * Copyright 2013-2017 the original author or authors.
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

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.users.AssociateUserAuditedSpaceRequest;
import org.cloudfoundry.client.v2.users.AssociateUserAuditedSpaceResponse;
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
import org.cloudfoundry.client.v2.users.GetUserRequest;
import org.cloudfoundry.client.v2.users.GetUserResponse;
import org.cloudfoundry.client.v2.users.ListUserAuditedSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserAuditedSpacesResponse;
import org.cloudfoundry.client.v2.users.ListUserManagedSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserManagedSpacesResponse;
import org.cloudfoundry.client.v2.users.ListUserOrganizationsRequest;
import org.cloudfoundry.client.v2.users.ListUserOrganizationsResponse;
import org.cloudfoundry.client.v2.users.ListUserSpacesRequest;
import org.cloudfoundry.client.v2.users.ListUserSpacesResponse;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.ListUsersResponse;
import org.cloudfoundry.client.v2.users.RemoveUserAuditedSpaceRequest;
import org.cloudfoundry.client.v2.users.RemoveUserManagedSpaceRequest;
import org.cloudfoundry.client.v2.users.RemoveUserOrganizationRequest;
import org.cloudfoundry.client.v2.users.RemoveUserSpaceRequest;
import org.cloudfoundry.client.v2.users.SummaryUserRequest;
import org.cloudfoundry.client.v2.users.SummaryUserResponse;
import org.cloudfoundry.client.v2.users.UpdateUserRequest;
import org.cloudfoundry.client.v2.users.UpdateUserResponse;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.client.v2.users.UserOrganizationEntity;
import org.cloudfoundry.client.v2.users.UserOrganizationResource;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.client.v2.users.UserSpaceEntity;
import org.cloudfoundry.client.v2.users.UserSpaceResource;
import org.cloudfoundry.client.v2.users.UserSummaryEntity;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorUsersTest extends AbstractClientApiTest {

    private final ReactorUsers users = new ReactorUsers(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void associateAuditedSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/v2/users/uaa-id-280/audited_spaces/012602a2-98d7-4ab7-a766-bdf5a841c2d4")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/users/PUT_{id}_audited_spaces_{id}_response.json")
                .build())
            .build());

        this.users
            .associateAuditedSpace(AssociateUserAuditedSpaceRequest.builder()
                .auditedSpaceId("012602a2-98d7-4ab7-a766-bdf5a841c2d4")
                .userId("uaa-id-280")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateUserAuditedSpaceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:36Z")
                    .id("uaa-id-280")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/users/uaa-id-280")
                    .build())
                .entity(UserEntity.builder()
                    .active(false)
                    .admin(false)
                    .auditedOrganizationsUrl("/v2/users/uaa-id-280/audited_organizations")
                    .auditedSpacesUrl("/v2/users/uaa-id-280/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/uaa-id-280/billing_managed_organizations")
                    .defaultSpaceId("06ed47f7-656a-4f31-8b3a-b0bdaaafc826")
                    .defaultSpaceUrl("/v2/spaces/06ed47f7-656a-4f31-8b3a-b0bdaaafc826")
                    .managedOrganizationsUrl("/v2/users/uaa-id-280/managed_organizations")
                    .managedSpacesUrl("/v2/users/uaa-id-280/managed_spaces")
                    .organizationsUrl("/v2/users/uaa-id-280/organizations")
                    .spacesUrl("/v2/users/uaa-id-280/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void associateManagedOrganization() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/v2/users/uaa-id-287/managed_organizations/97e1bd4a-828e-4edf-b140-506533d4008e")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/users/PUT_{id}_managed_organizations_{id}_response.json")
                .build())
            .build());

        this.users
            .associateManagedOrganization(AssociateUserManagedOrganizationRequest.builder()
                .managedOrganizationId("97e1bd4a-828e-4edf-b140-506533d4008e")
                .userId("uaa-id-287")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateUserManagedOrganizationResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:36Z")
                    .id("uaa-id-287")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/users/uaa-id-287")
                    .build())
                .entity(UserEntity.builder()
                    .active(false)
                    .admin(false)
                    .auditedOrganizationsUrl("/v2/users/uaa-id-287/audited_organizations")
                    .auditedSpacesUrl("/v2/users/uaa-id-287/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/uaa-id-287/billing_managed_organizations")
                    .defaultSpaceId("2becd2d1-62ee-472e-a66f-bea5d9f0dc53")
                    .defaultSpaceUrl("/v2/spaces/2becd2d1-62ee-472e-a66f-bea5d9f0dc53")
                    .managedOrganizationsUrl("/v2/users/uaa-id-287/managed_organizations")
                    .managedSpacesUrl("/v2/users/uaa-id-287/managed_spaces")
                    .organizationsUrl("/v2/users/uaa-id-287/organizations")
                    .spacesUrl("/v2/users/uaa-id-287/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void associateManagedSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/v2/users/uaa-id-268/managed_spaces/b133899a-c3ea-451b-adaa-f8e2174cbfec")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/users/PUT_{id}_managed_spaces_{id}_response.json")
                .build())
            .build());

        this.users
            .associateManagedSpace(AssociateUserManagedSpaceRequest.builder()
                .managedSpaceId("b133899a-c3ea-451b-adaa-f8e2174cbfec")
                .userId("uaa-id-268")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateUserManagedSpaceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:35Z")
                    .id("uaa-id-268")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/users/uaa-id-268")
                    .build())
                .entity(UserEntity.builder()
                    .active(false)
                    .admin(false)
                    .auditedOrganizationsUrl("/v2/users/uaa-id-268/audited_organizations")
                    .auditedSpacesUrl("/v2/users/uaa-id-268/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/uaa-id-268/billing_managed_organizations")
                    .defaultSpaceId("81d79071-61db-43d7-86d3-8f885d14fd4e")
                    .defaultSpaceUrl("/v2/spaces/81d79071-61db-43d7-86d3-8f885d14fd4e")
                    .managedOrganizationsUrl("/v2/users/uaa-id-268/managed_organizations")
                    .managedSpacesUrl("/v2/users/uaa-id-268/managed_spaces")
                    .organizationsUrl("/v2/users/uaa-id-268/organizations")
                    .spacesUrl("/v2/users/uaa-id-268/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void associateOrganization() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/v2/users/uaa-id-301/organizations/2f0bb84e-5229-4dd4-b053-855910833d2a")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/users/PUT_{id}_organizations_{id}_response.json")
                .build())
            .build());

        this.users
            .associateOrganization(AssociateUserOrganizationRequest.builder()
                .organizationId("2f0bb84e-5229-4dd4-b053-855910833d2a")
                .userId("uaa-id-301")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateUserOrganizationResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:36Z")
                    .id("uaa-id-301")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/users/uaa-id-301")
                    .build())
                .entity(UserEntity.builder()
                    .active(false)
                    .admin(false)
                    .auditedOrganizationsUrl("/v2/users/uaa-id-301/audited_organizations")
                    .auditedSpacesUrl("/v2/users/uaa-id-301/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/uaa-id-301/billing_managed_organizations")
                    .defaultSpaceId("10db6c03-3e24-4a2a-b311-a28b541376f1")
                    .defaultSpaceUrl("/v2/spaces/10db6c03-3e24-4a2a-b311-a28b541376f1")
                    .managedOrganizationsUrl("/v2/users/uaa-id-301/managed_organizations")
                    .managedSpacesUrl("/v2/users/uaa-id-301/managed_spaces")
                    .organizationsUrl("/v2/users/uaa-id-301/organizations")
                    .spacesUrl("/v2/users/uaa-id-301/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void associateSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/v2/users/uaa-id-305/spaces/063d1561-16ab-4ece-825d-30e3814f4e2f")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/users/PUT_{id}_spaces_{id}_response.json")
                .build())
            .build());

        this.users
            .associateSpace(AssociateUserSpaceRequest.builder()
                .spaceId("063d1561-16ab-4ece-825d-30e3814f4e2f")
                .userId("uaa-id-305")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateUserSpaceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:36Z")
                    .id("uaa-id-305")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/users/uaa-id-305")
                    .build())
                .entity(UserEntity.builder()
                    .active(false)
                    .admin(false)
                    .auditedOrganizationsUrl("/v2/users/uaa-id-305/audited_organizations")
                    .auditedSpacesUrl("/v2/users/uaa-id-305/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/uaa-id-305/billing_managed_organizations")
                    .defaultSpaceId("063d1561-16ab-4ece-825d-30e3814f4e2f")
                    .defaultSpaceUrl("/v2/spaces/063d1561-16ab-4ece-825d-30e3814f4e2f")
                    .managedOrganizationsUrl("/v2/users/uaa-id-305/managed_organizations")
                    .managedSpacesUrl("/v2/users/uaa-id-305/managed_spaces")
                    .organizationsUrl("/v2/users/uaa-id-305/organizations")
                    .spacesUrl("/v2/users/uaa-id-305/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v2/users")
                .payload("fixtures/client/v2/users/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/users/POST_response.json")
                .build())
            .build());

        this.users
            .create(CreateUserRequest.builder()
                .uaaId("guid-cb24b36d-4656-468e-a50d-b53113ac6177")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateUserResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:37Z")
                    .id("guid-cb24b36d-4656-468e-a50d-b53113ac6177")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177")
                    .build())
                .entity(UserEntity.builder()
                    .admin(false)
                    .active(false)
                    .auditedOrganizationsUrl("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177/audited_organizations")
                    .auditedSpacesUrl("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177/billing_managed_organizations")
                    .managedOrganizationsUrl("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177/managed_organizations")
                    .managedSpacesUrl("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177/managed_spaces")
                    .organizationsUrl("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177/organizations")
                    .spacesUrl("/v2/users/guid-cb24b36d-4656-468e-a50d-b53113ac6177/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v2/users/uaa-id-319")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.users
            .delete(DeleteUserRequest.builder()
                .userId("uaa-id-319")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/users/uaa-id-317")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/users/GET_{id}_response.json")
                .build())
            .build());

        this.users
            .get(GetUserRequest.builder()
                .userId("uaa-id-317")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetUserResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:37Z")
                    .id("uaa-id-317")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/users/uaa-id-317")
                    .build())
                .entity(UserEntity.builder()
                    .active(false)
                    .admin(false)
                    .auditedOrganizationsUrl("/v2/users/uaa-id-317/audited_organizations")
                    .auditedSpacesUrl("/v2/users/uaa-id-317/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/uaa-id-317/billing_managed_organizations")
                    .defaultSpaceUrl("/v2/spaces/fc898723-2192-42d9-9567-c0b2e03a3169")
                    .defaultSpaceId("fc898723-2192-42d9-9567-c0b2e03a3169")
                    .managedOrganizationsUrl("/v2/users/uaa-id-317/managed_organizations")
                    .managedSpacesUrl("/v2/users/uaa-id-317/managed_spaces")
                    .organizationsUrl("/v2/users/uaa-id-317/organizations")
                    .spacesUrl("/v2/users/uaa-id-317/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/users?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/users/GET_response.json")
                .build())
            .build());

        this.users
            .list(ListUsersRequest.builder()
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUsersResponse.builder()
                .totalResults(2)
                .totalPages(1)
                .resource(UserResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-12-22T18:28:01Z")
                        .id("uaa-id-133")
                        .url("/v2/users/uaa-id-133")
                        .build())
                    .entity(UserEntity.builder()
                        .active(false)
                        .admin(false)
                        .auditedOrganizationsUrl("/v2/users/uaa-id-133/audited_organizations")
                        .auditedSpacesUrl("/v2/users/uaa-id-133/audited_spaces")
                        .billingManagedOrganizationsUrl("/v2/users/uaa-id-133/billing_managed_organizations")
                        .defaultSpaceUrl("/v2/spaces/55b306f6-b956-4c85-a7dc-64358121d39e")
                        .defaultSpaceId("55b306f6-b956-4c85-a7dc-64358121d39e")
                        .managedOrganizationsUrl("/v2/users/uaa-id-133/managed_organizations")
                        .managedSpacesUrl("/v2/users/uaa-id-133/managed_spaces")
                        .organizationsUrl("/v2/users/uaa-id-133/organizations")
                        .spacesUrl("/v2/users/uaa-id-133/spaces")
                        .username("user@example.com")
                        .build())
                    .build())
                .resource(UserResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-12-22T18:28:01Z")
                        .id("uaa-id-134")
                        .url("/v2/users/uaa-id-134")
                        .build())
                    .entity(UserEntity.builder()
                        .active(true)
                        .admin(false)
                        .auditedOrganizationsUrl("/v2/users/uaa-id-134/audited_organizations")
                        .auditedSpacesUrl("/v2/users/uaa-id-134/audited_spaces")
                        .billingManagedOrganizationsUrl("/v2/users/uaa-id-134/billing_managed_organizations")
                        .managedOrganizationsUrl("/v2/users/uaa-id-134/managed_organizations")
                        .managedSpacesUrl("/v2/users/uaa-id-134/managed_spaces")
                        .organizationsUrl("/v2/users/uaa-id-134/organizations")
                        .spacesUrl("/v2/users/uaa-id-134/spaces")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listAuditedSpaces() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/users/uaa-id-282/audited_spaces")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/users/GET_{id}_audited_spaces_response.json")
                .build())
            .build());

        this.users
            .listAuditedSpaces(ListUserAuditedSpacesRequest.builder()
                .userId("uaa-id-282")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUserAuditedSpacesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:36Z")
                        .id("b93f27a3-3fee-49a3-987a-20407c2b029b")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b")
                        .build())
                    .entity(SpaceEntity.builder()
                        .allowSsh(true)
                        .applicationEventsUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/app_events")
                        .applicationsUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/apps")
                        .auditorsUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/auditors")
                        .developersUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/developers")
                        .domainsUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/domains")
                        .eventsUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/events")
                        .managersUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/managers")
                        .name("name-1868")
                        .organizationId("cf8bee37-4644-4792-b6c1-de14b93390a4")
                        .organizationUrl("/v2/organizations/cf8bee37-4644-4792-b6c1-de14b93390a4")
                        .routesUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/routes")
                        .securityGroupsUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/security_groups")
                        .serviceInstancesUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/service_instances")
                        .stagingSecurityGroupsUrl("/v2/spaces/b93f27a3-3fee-49a3-987a-20407c2b029b/staging_security_groups")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listManagedSpaces() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/users/uaa-id-270/managed_spaces")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/users/GET_{id}_managed_spaces_response.json")
                .build())
            .build());

        this.users
            .listManagedSpaces(ListUserManagedSpacesRequest.builder()
                .userId("uaa-id-270")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUserManagedSpacesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:35Z")
                        .id("b0e100bd-6d7c-4a3d-b0d3-0249d739a086")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086")
                        .build())
                    .entity(SpaceEntity.builder()
                        .allowSsh(true)
                        .applicationEventsUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/app_events")
                        .applicationsUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/apps")
                        .auditorsUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/auditors")
                        .developersUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/developers")
                        .domainsUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/domains")
                        .eventsUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/events")
                        .managersUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/managers")
                        .name("name-1820")
                        .organizationId("14766fb1-d8fa-4604-ba7a-248f341492a2")
                        .organizationUrl("/v2/organizations/14766fb1-d8fa-4604-ba7a-248f341492a2")
                        .routesUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/routes")
                        .securityGroupsUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/security_groups")
                        .serviceInstancesUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/service_instances")
                        .stagingSecurityGroupsUrl("/v2/spaces/b0e100bd-6d7c-4a3d-b0d3-0249d739a086/staging_security_groups")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listOrganizations() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/users/uaa-id-299/organizations")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/users/GET_{id}_organizations_response.json")
                .build())
            .build());

        this.users
            .listOrganizations(ListUserOrganizationsRequest.builder()
                .userId("uaa-id-299")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUserOrganizationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(OrganizationResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:36Z")
                        .id("72d22faf-f70c-4e1d-ad42-256939db7fca")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca")
                        .build())
                    .entity(OrganizationEntity.builder()
                        .applicationEventsUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/app_events")
                        .auditorsUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/auditors")
                        .billingEnabled(false)
                        .billingManagersUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/billing_managers")
                        .domainsUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/domains")
                        .managersUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/managers")
                        .name("name-1919")
                        .privateDomainsUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/private_domains")
                        .quotaDefinitionId("8a51d151-a5fa-455f-9482-0ff0f2f50053")
                        .quotaDefinitionUrl("/v2/quota_definitions/8a51d151-a5fa-455f-9482-0ff0f2f50053")
                        .spaceQuotaDefinitionsUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/space_quota_definitions")
                        .spacesUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/spaces")
                        .status("active")
                        .usersUrl("/v2/organizations/72d22faf-f70c-4e1d-ad42-256939db7fca/users")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpaces() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/users/uaa-id-309/spaces")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/users/GET_{id}_spaces_response.json")
                .build())
            .build());

        this.users
            .listSpaces(ListUserSpacesRequest.builder()
                .userId("uaa-id-309")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUserSpacesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:37Z")
                        .id("9881c79e-d269-4a53-9d77-cb21b745356e")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e")
                        .build())
                    .entity(SpaceEntity.builder()
                        .allowSsh(true)
                        .applicationEventsUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/app_events")
                        .applicationsUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/apps")
                        .auditorsUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/auditors")
                        .developersUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/developers")
                        .domainsUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/domains")
                        .eventsUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/events")
                        .managersUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/managers")
                        .name("name-1948")
                        .organizationId("6a2a2d18-7620-43cf-a332-353824b431b2")
                        .organizationUrl("/v2/organizations/6a2a2d18-7620-43cf-a332-353824b431b2")
                        .routesUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/routes")
                        .securityGroupsUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/security_groups")
                        .serviceInstancesUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/service_instances")
                        .stagingSecurityGroupsUrl("/v2/spaces/9881c79e-d269-4a53-9d77-cb21b745356e/staging_security_groups")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeAuditedSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v2/users/uaa-id-278/audited_spaces/95b843ee-9f7a-4021-a155-ad9c0f76e6fc")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.users
            .removeAuditedSpace(RemoveUserAuditedSpaceRequest.builder()
                .auditedSpaceId("95b843ee-9f7a-4021-a155-ad9c0f76e6fc")
                .userId("uaa-id-278")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeManagedSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v2/users/uaa-id-266/managed_spaces/0af3c27b-d995-4a63-a9c5-26fc01210128")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.users
            .removeManagedSpace(RemoveUserManagedSpaceRequest.builder()
                .managedSpaceId("0af3c27b-d995-4a63-a9c5-26fc01210128")
                .userId("uaa-id-266")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeOrganization() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v2/users/uaa-id-303/organizations/aaac52d1-e99d-4536-a981-379980a3cb23")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.users
            .removeOrganization(RemoveUserOrganizationRequest.builder()
                .organizationId("aaac52d1-e99d-4536-a981-379980a3cb23")
                .userId("uaa-id-303")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeSpace() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v2/users/uaa-id-307/spaces/6c37bc37-f712-4399-be89-2272980b66ef")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.users
            .removeSpace(RemoveUserSpaceRequest.builder()
                .spaceId("6c37bc37-f712-4399-be89-2272980b66ef")
                .userId("uaa-id-307")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void summary() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/users/uaa-id-355/summary")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/users/GET_{id}_summary_response.json")
                .build())
            .build());

        this.users
            .summary(SummaryUserRequest.builder()
                .userId("uaa-id-355")
                .build())
            .as(StepVerifier::create)
            .expectNext(SummaryUserResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:40Z")
                    .id("uaa-id-355")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .build())
                .entity(UserSummaryEntity.builder()

                    .billingManagedOrganization(UserOrganizationResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-06-08T16:41:40Z")
                            .id("a8f940a4-3b9f-4e26-9ceb-2e5062cc1513")
                            .updatedAt("2016-06-08T16:41:26Z")
                            .build())
                        .entity(UserOrganizationEntity.builder()
                            .billingEnabled(false)
                            .name("name-2041")
                            .status("active")
                            .space(UserSpaceResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("db69758e-14e9-4fed-b69a-353a98a05a2e")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserSpaceEntity.builder()
                                    .name("name-2043")
                                    .build())
                                .build())
                            .quotaDefinition(OrganizationQuotaDefinitionResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("6c6b51c9-823d-4c91-ab5c-9487a93360bc")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(OrganizationQuotaDefinitionEntity.builder()
                                    .applicationInstanceLimit(-1)
                                    .applicationTaskLimit(-1)
                                    .instanceMemoryLimit(-1)
                                    .memoryLimit(20480)
                                    .name("name-2042")
                                    .nonBasicServicesAllowed(true)
                                    .totalPrivateDomains(-1)
                                    .totalRoutes(1000)
                                    .totalServices(60)
                                    .trialDatabaseAllowed(false)
                                    .build())
                                .build())
                            .manager(UserResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("uaa-id-355")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserEntity.builder()
                                    .active(false)
                                    .admin(false)
                                    .defaultSpaceId(null)
                                    .build())
                                .build())
                            .build())
                        .build())

                    .auditedOrganization(UserOrganizationResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-06-08T16:41:40Z")
                            .id("a8f940a4-3b9f-4e26-9ceb-2e5062cc1513")
                            .updatedAt("2016-06-08T16:41:26Z")
                            .build())
                        .entity(UserOrganizationEntity.builder()
                            .billingEnabled(false)
                            .name("name-2041")
                            .status("active")
                            .space(UserSpaceResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("db69758e-14e9-4fed-b69a-353a98a05a2e")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserSpaceEntity.builder()
                                    .name("name-2043")
                                    .build())
                                .build())
                            .quotaDefinition(OrganizationQuotaDefinitionResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("6c6b51c9-823d-4c91-ab5c-9487a93360bc")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(OrganizationQuotaDefinitionEntity.builder()
                                    .applicationInstanceLimit(-1)
                                    .applicationTaskLimit(-1)
                                    .instanceMemoryLimit(-1)
                                    .memoryLimit(20480)
                                    .name("name-2042")
                                    .nonBasicServicesAllowed(true)
                                    .totalPrivateDomains(-1)
                                    .totalRoutes(1000)
                                    .totalServices(60)
                                    .trialDatabaseAllowed(false)
                                    .build())
                                .build())
                            .manager(UserResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("uaa-id-355")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserEntity.builder()
                                    .active(false)
                                    .admin(false)
                                    .defaultSpaceId(null)
                                    .build())
                                .build())
                            .build())
                        .build())

                    .managedOrganization(UserOrganizationResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-06-08T16:41:40Z")
                            .id("a8f940a4-3b9f-4e26-9ceb-2e5062cc1513")
                            .updatedAt("2016-06-08T16:41:26Z")
                            .build())
                        .entity(UserOrganizationEntity.builder()
                            .billingEnabled(false)
                            .name("name-2041")
                            .status("active")
                            .space(UserSpaceResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("db69758e-14e9-4fed-b69a-353a98a05a2e")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserSpaceEntity.builder()
                                    .name("name-2043")
                                    .build())
                                .build())
                            .quotaDefinition(OrganizationQuotaDefinitionResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("6c6b51c9-823d-4c91-ab5c-9487a93360bc")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(OrganizationQuotaDefinitionEntity.builder()
                                    .applicationInstanceLimit(-1)
                                    .applicationTaskLimit(-1)
                                    .instanceMemoryLimit(-1)
                                    .memoryLimit(20480)
                                    .name("name-2042")
                                    .nonBasicServicesAllowed(true)
                                    .totalPrivateDomains(-1)
                                    .totalRoutes(1000)
                                    .totalServices(60)
                                    .trialDatabaseAllowed(false)
                                    .build())
                                .build())
                            .manager(UserResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("uaa-id-355")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserEntity.builder()
                                    .active(false)
                                    .admin(false)
                                    .defaultSpaceId(null)
                                    .build())
                                .build())
                            .build())
                        .build())

                    .organization(UserOrganizationResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-06-08T16:41:40Z")
                            .id("a8f940a4-3b9f-4e26-9ceb-2e5062cc1513")
                            .updatedAt("2016-06-08T16:41:26Z")
                            .build())
                        .entity(UserOrganizationEntity.builder()
                            .billingEnabled(false)
                            .name("name-2041")
                            .status("active")
                            .space(UserSpaceResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("db69758e-14e9-4fed-b69a-353a98a05a2e")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserSpaceEntity.builder()
                                    .name("name-2043")
                                    .build())
                                .build())
                            .quotaDefinition(OrganizationQuotaDefinitionResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("6c6b51c9-823d-4c91-ab5c-9487a93360bc")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(OrganizationQuotaDefinitionEntity.builder()
                                    .applicationInstanceLimit(-1)
                                    .applicationTaskLimit(-1)
                                    .instanceMemoryLimit(-1)
                                    .memoryLimit(20480)
                                    .name("name-2042")
                                    .nonBasicServicesAllowed(true)
                                    .totalPrivateDomains(-1)
                                    .totalRoutes(1000)
                                    .totalServices(60)
                                    .trialDatabaseAllowed(false)
                                    .build())
                                .build())
                            .manager(UserResource.builder()
                                .metadata(Metadata.builder()
                                    .createdAt("2016-06-08T16:41:40Z")
                                    .id("uaa-id-355")
                                    .updatedAt("2016-06-08T16:41:26Z")
                                    .build())
                                .entity(UserEntity.builder()
                                    .active(false)
                                    .admin(false)
                                    .defaultSpaceId(null)
                                    .build())
                                .build())
                            .build())
                        .build())

                    .auditedSpace(UserSpaceResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-06-08T16:41:40Z")
                            .id("db69758e-14e9-4fed-b69a-353a98a05a2e")
                            .updatedAt("2016-06-08T16:41:26Z")
                            .build())
                        .entity(UserSpaceEntity.builder()
                            .name("name-2043")
                            .build())
                        .build())

                    .managedSpace(UserSpaceResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-06-08T16:41:40Z")
                            .id("db69758e-14e9-4fed-b69a-353a98a05a2e")
                            .updatedAt("2016-06-08T16:41:26Z")
                            .build())
                        .entity(UserSpaceEntity.builder()
                            .name("name-2043")
                            .build())
                        .build())

                    .space(UserSpaceResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-06-08T16:41:40Z")
                            .id("db69758e-14e9-4fed-b69a-353a98a05a2e")
                            .updatedAt("2016-06-08T16:41:26Z")
                            .build())
                        .entity(UserSpaceEntity.builder()
                            .name("name-2043")
                            .build())
                        .build())

                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/v2/users/uaa-id-313")
                .payload("fixtures/client/v2/users/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/users/PUT_{id}_response.json")
                .build())
            .build());

        this.users
            .update(UpdateUserRequest.builder()
                .defaultSpaceId("56d8e095-b2c8-4ba9-b540-dc42ba1c7351")
                .userId("uaa-id-313")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateUserResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:37Z")
                    .id("uaa-id-313")
                    .updatedAt("2016-06-08T16:41:37Z")
                    .url("/v2/users/uaa-id-313")
                    .build())
                .entity(UserEntity.builder()
                    .admin(false)
                    .active(false)
                    .auditedOrganizationsUrl("/v2/users/uaa-id-313/audited_organizations")
                    .auditedSpacesUrl("/v2/users/uaa-id-313/audited_spaces")
                    .billingManagedOrganizationsUrl("/v2/users/uaa-id-313/billing_managed_organizations")
                    .defaultSpaceId("56d8e095-b2c8-4ba9-b540-dc42ba1c7351")
                    .defaultSpaceUrl("/v2/spaces/56d8e095-b2c8-4ba9-b540-dc42ba1c7351")
                    .managedOrganizationsUrl("/v2/users/uaa-id-313/managed_organizations")
                    .managedSpacesUrl("/v2/users/uaa-id-313/managed_spaces")
                    .organizationsUrl("/v2/users/uaa-id-313/organizations")
                    .spacesUrl("/v2/users/uaa-id-313/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
