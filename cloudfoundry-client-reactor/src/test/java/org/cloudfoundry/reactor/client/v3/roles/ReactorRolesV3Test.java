/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.roles;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.roles.CreateRoleRequest;
import org.cloudfoundry.client.v3.roles.CreateRoleResponse;
import org.cloudfoundry.client.v3.roles.DeleteRoleRequest;
import org.cloudfoundry.client.v3.roles.GetRoleRequest;
import org.cloudfoundry.client.v3.roles.GetRoleResponse;
import org.cloudfoundry.client.v3.roles.ListRolesRequest;
import org.cloudfoundry.client.v3.roles.ListRolesResponse;
import org.cloudfoundry.client.v3.roles.RoleRelationships;
import org.cloudfoundry.client.v3.roles.RoleResource;
import org.cloudfoundry.client.v3.roles.RoleType;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class ReactorRolesV3Test extends AbstractClientApiTest {

    private final ReactorRolesV3 roles =
            new ReactorRolesV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/roles")
                                        .payload("fixtures/client/v3/roles/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload("fixtures/client/v3/roles/POST_response.json")
                                        .build())
                        .build());

        this.roles
                .create(
                        CreateRoleRequest.builder()
                                .type(RoleType.ORGANIZATION_AUDITOR)
                                .relationships(
                                        RoleRelationships.builder()
                                                .user(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id("test-user-id")
                                                                                .build())
                                                                .build())
                                                .organization(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "test-organization-id")
                                                                                .build())
                                                                .build())
                                                .build())
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateRoleResponse.builder()
                                .id("40557c70-d1bd-4976-a2ab-a85f5e882418")
                                .createdAt("2019-10-10T17:19:12Z")
                                .updatedAt("2019-10-10T17:19:12Z")
                                .type(RoleType.ORGANIZATION_AUDITOR)
                                .relationships(
                                        RoleRelationships.builder()
                                                .organization(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "test-organization-id")
                                                                                .build())
                                                                .build())
                                                .user(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id("test-user-id")
                                                                                .build())
                                                                .build())
                                                .space(ToOneRelationship.builder().build())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/roles/40557c70-d1bd-4976-a2ab-a85f5e882418")
                                                .build())
                                .link(
                                        "user",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/users/test-user-id")
                                                .build())
                                .link(
                                        "organization",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/organizations/test-organization-id")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void delete() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path("/roles/test-role-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/test-role-id")
                                        .build())
                        .build());

        this.roles
                .delete(DeleteRoleRequest.builder().roleId("test-role-id").build())
                .as(StepVerifier::create)
                .expectNext("test-role-id")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/roles/40557c70-d1bd-4976-a2ab-a85f5e882418")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/client/v3/roles/GET_{id}_response.json")
                                        .build())
                        .build());

        this.roles
                .get(
                        GetRoleRequest.builder()
                                .roleId("40557c70-d1bd-4976-a2ab-a85f5e882418")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetRoleResponse.builder()
                                .id("40557c70-d1bd-4976-a2ab-a85f5e882418")
                                .type(RoleType.ORGANIZATION_AUDITOR)
                                .createdAt("2019-10-10T17:19:12Z")
                                .updatedAt("2019-10-10T17:19:12Z")
                                .relationships(
                                        RoleRelationships.builder()
                                                .organization(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "test-organization-id")
                                                                                .build())
                                                                .build())
                                                .user(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id("test-user-id")
                                                                                .build())
                                                                .build())
                                                .space(ToOneRelationship.builder().build())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/roles/40557c70-d1bd-4976-a2ab-a85f5e882418")
                                                .build())
                                .link(
                                        "user",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/users/test-user-id")
                                                .build())
                                .link(
                                        "organization",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/organizations/test-organization-id")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/roles").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/client/v3/roles/GET_response.json")
                                        .build())
                        .build());

        this.roles
                .list(ListRolesRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListRolesResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(3)
                                                .totalPages(2)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/roles?page=1&per_page=2")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/roles?page=2&per_page=2")
                                                                .build())
                                                .next(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/roles?page=2&per_page=2")
                                                                .build())
                                                .build())
                                .resource(
                                        RoleResource.builder()
                                                .id("40557c70-d1bd-4976-a2ab-a85f5e882418")
                                                .createdAt("2019-10-10T17:19:12Z")
                                                .updatedAt("2019-10-10T17:19:12Z")
                                                .type(RoleType.ORGANIZATION_AUDITOR)
                                                .relationships(
                                                        RoleRelationships.builder()
                                                                .organization(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "test-organization-id")
                                                                                                .build())
                                                                                .build())
                                                                .user(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "test-user-id")
                                                                                                .build())
                                                                                .build())
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .build())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/roles/40557c70-d1bd-4976-a2ab-a85f5e882418")
                                                                .build())
                                                .link(
                                                        "user",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/users/test-user-id")
                                                                .build())
                                                .link(
                                                        "organization",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/organizations/test-organization-id")
                                                                .build())
                                                .build())
                                .resource(
                                        RoleResource.builder()
                                                .id("12347c70-d1bd-4976-a2ab-a85f5e882418")
                                                .createdAt("2047-11-10T17:19:12Z")
                                                .updatedAt("2047-11-10T17:19:12Z")
                                                .type(RoleType.SPACE_AUDITOR)
                                                .relationships(
                                                        RoleRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "test-space-id")
                                                                                                .build())
                                                                                .build())
                                                                .user(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "test-user-id")
                                                                                                .build())
                                                                                .build())
                                                                .organization(
                                                                        ToOneRelationship.builder()
                                                                                .build())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/roles/12347c70-d1bd-4976-a2ab-a85f5e882418")
                                                                .build())
                                                .link(
                                                        "user",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/users/test-user-id")
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/spaces/test-space-id")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}
