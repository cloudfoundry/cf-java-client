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
import org.cloudfoundry.client.v2.users.CreateUserRequest;
import org.cloudfoundry.client.v2.users.CreateUserResponse;
import org.cloudfoundry.client.v2.users.DeleteUserRequest;
import org.cloudfoundry.client.v2.users.GetUserRequest;
import org.cloudfoundry.client.v2.users.GetUserResponse;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.ListUsersResponse;
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
