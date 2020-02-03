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

package org.cloudfoundry.reactor.uaa.groups;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.Metadata;
import org.cloudfoundry.uaa.groups.AddMemberRequest;
import org.cloudfoundry.uaa.groups.AddMemberResponse;
import org.cloudfoundry.uaa.groups.CheckMembershipRequest;
import org.cloudfoundry.uaa.groups.CheckMembershipResponse;
import org.cloudfoundry.uaa.groups.CreateGroupRequest;
import org.cloudfoundry.uaa.groups.CreateGroupResponse;
import org.cloudfoundry.uaa.groups.DeleteGroupRequest;
import org.cloudfoundry.uaa.groups.DeleteGroupResponse;
import org.cloudfoundry.uaa.groups.ExternalGroupResource;
import org.cloudfoundry.uaa.groups.GetGroupRequest;
import org.cloudfoundry.uaa.groups.GetGroupResponse;
import org.cloudfoundry.uaa.groups.Group;
import org.cloudfoundry.uaa.groups.ListExternalGroupMappingsRequest;
import org.cloudfoundry.uaa.groups.ListExternalGroupMappingsResponse;
import org.cloudfoundry.uaa.groups.ListGroupsRequest;
import org.cloudfoundry.uaa.groups.ListGroupsResponse;
import org.cloudfoundry.uaa.groups.ListMembersRequest;
import org.cloudfoundry.uaa.groups.ListMembersResponse;
import org.cloudfoundry.uaa.groups.MapExternalGroupRequest;
import org.cloudfoundry.uaa.groups.MapExternalGroupResponse;
import org.cloudfoundry.uaa.groups.Member;
import org.cloudfoundry.uaa.groups.MemberSummary;
import org.cloudfoundry.uaa.groups.MemberType;
import org.cloudfoundry.uaa.groups.RemoveMemberRequest;
import org.cloudfoundry.uaa.groups.RemoveMemberResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdResponse;
import org.cloudfoundry.uaa.groups.UpdateGroupRequest;
import org.cloudfoundry.uaa.groups.UpdateGroupResponse;
import org.cloudfoundry.uaa.groups.UserEntity;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.uaa.SortOrder.ASCENDING;

public final class ReactorGroupsTest extends AbstractUaaApiTest {

    private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void addMember() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/Groups/test-group-id/members")
                .payload("fixtures/uaa/groups/POST_{id}_members_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/groups/POST_{id}_members_response.json")
                .build())
            .build());

        this.groups
            .addMember(AddMemberRequest.builder()
                .groupId("test-group-id")
                .origin("uaa")
                .type(MemberType.USER)
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .build())
            .as(StepVerifier::create)
            .expectNext(AddMemberResponse.builder()
                .origin("uaa")
                .type(MemberType.USER)
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void checkMember() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/Groups/test-group-id/members/test-member-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/GET_{id}_members_{id}_response.json")
                .build())
            .build());

        this.groups
            .checkMembership(CheckMembershipRequest.builder()
                .groupId("test-group-id")
                .memberId("test-member-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(CheckMembershipResponse.builder()
                .origin("uaa")
                .type(MemberType.USER)
                .memberId("test-member-id")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .header("X-Identity-Zone-Id", "uaa")
                .method(POST).path("/Groups")
                .payload("fixtures/uaa/groups/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/groups/POST_response.json")
                .build())
            .build());

        this.groups
            .create(CreateGroupRequest.builder()
                .description("the cool group")
                .displayName("Cool Group Name")
                .identityZoneId("uaa")
                .member(MemberSummary.builder()
                    .origin("uaa")
                    .type(MemberType.USER)
                    .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateGroupResponse.builder()
                .id("46081184-7ca9-453d-9bf8-74da7113bec6")
                .metadata(Metadata.builder()
                    .created("2016-06-03T17:59:30.527Z")
                    .lastModified("2016-06-03T17:59:30.527Z")
                    .version(0)
                    .build())
                .description("the cool group")
                .displayName("Cool Group Name")
                .member(MemberSummary.builder()
                    .origin("uaa")
                    .type(MemberType.USER)
                    .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .zoneId("uaa")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .header("If-Match", "*")
                .method(DELETE).path("/Groups/test-group-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/DELETE_{id}_response.json")
                .build())
            .build());

        this.groups
            .delete(DeleteGroupRequest.builder()
                .groupId("test-group-id")
                .version("*")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteGroupResponse.builder()
                .id("test-group-id")
                .metadata(Metadata.builder()
                    .created("2016-06-03T17:59:30.527Z")
                    .lastModified("2016-06-03T17:59:30.561Z")
                    .version(1)
                    .build())
                .description("the cool group")
                .displayName("Cooler Group Name for Delete")
                .member(MemberSummary.builder()
                    .origin("uaa")
                    .type(MemberType.USER)
                    .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .zoneId("uaa")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/Groups/test-group-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/GET_{id}_response.json")
                .build())
            .build());

        this.groups
            .get(GetGroupRequest.builder()
                .groupId("test-group-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetGroupResponse.builder()
                .id("test-group-id")
                .metadata(Metadata.builder()
                    .created("2016-06-03T17:59:30.527Z")
                    .lastModified("2016-06-03T17:59:30.561Z")
                    .version(1)
                    .build())
                .description("the cool group")
                .displayName("Cooler Group Name for Retrieve")
                .member(MemberSummary.builder()
                    .origin("uaa")
                    .type(MemberType.USER)
                    .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .zoneId("uaa")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/Groups?count=50&filter=id%2Beq%2B%22f87c557a-8ddc-43d3-98fb-e420ebc7f0f1%22%2Bor%2BdisplayName%2Beq%2B%22Cooler%20Group%20Name%20for%20List%22" +
                    "&sortBy=email&sortOrder=ascending&startIndex=1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/GET_response.json")
                .build())
            .build());

        this.groups
            .list(ListGroupsRequest.builder()
                .filter("id+eq+\"f87c557a-8ddc-43d3-98fb-e420ebc7f0f1\"+or+displayName+eq+\"Cooler Group Name for List\"")
                .count(50)
                .startIndex(1)
                .sortBy("email")
                .sortOrder(ASCENDING)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListGroupsResponse.builder()
                .resource(Group.builder()
                    .id("f87c557a-8ddc-43d3-98fb-e420ebc7f0f1")
                    .metadata(Metadata.builder()
                        .created("2016-06-16T00:01:41.692Z")
                        .lastModified("2016-06-16T00:01:41.728Z")
                        .version(1)
                        .build())
                    .description("the cool group")
                    .displayName("Cooler Group Name for List")
                    .member(MemberSummary.builder()
                        .origin("uaa")
                        .type(MemberType.USER)
                        .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                        .build())
                    .schema("urn:scim:schemas:core:1.0")
                    .zoneId("uaa")
                    .build()
                )
                .startIndex(1)
                .itemsPerPage(50)
                .totalResults(1)
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listExternalGroupMappings() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/Groups/External?count=50&filter=group_id%2Beq%2B%220480db7f-d1bc-4d2b-b723-febc684c0ee9%22&startIndex=1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/GET_external_response.json")
                .build())
            .build());

        this.groups
            .listExternalGroupMappings(ListExternalGroupMappingsRequest.builder()
                .filter("group_id+eq+\"0480db7f-d1bc-4d2b-b723-febc684c0ee9\"")
                .count(50)
                .startIndex(1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListExternalGroupMappingsResponse.builder()
                .resource(ExternalGroupResource.builder()
                    .groupId("c4a41861-6c83-45a7-995e-64fb66565dce")
                    .displayName("Group For Testing Retrieving External Group Mappings")
                    .origin("ldap")
                    .externalGroup("external group")
                    .build()
                )
                .startIndex(1)
                .itemsPerPage(1)
                .totalResults(1)
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listMembers() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/Groups/f87c557a-8ddc-43d3-98fb-e420ebc7f0f1/members?returnEntities=true")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/GET_members_response.json")
                .build())
            .build());

        this.groups
            .listMembers(ListMembersRequest.builder()
                .groupId("f87c557a-8ddc-43d3-98fb-e420ebc7f0f1")
                .returnEntities(true)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListMembersResponse.builder()
                .member(Member.builder()
                    .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                    .type(MemberType.USER)
                    .origin("uaa")
                    .entity(UserEntity.builder()
                        .id("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                        .externalId("test-user")
                        .meta(Meta.builder()
                            .version(0)
                            .created("2016-06-16T00:01:41.665Z")
                            .lastModified("2016-06-16T00:01:41.665Z")
                            .build())
                        .userName("40HfKc")
                        .name(Name.builder()
                            .familyName("cool-familyName")
                            .givenName("cool-name")
                            .build())
                        .email(Email.builder()
                            .value("cool@chill.com")
                            .primary(false)
                            .build())
                        .active(true)
                        .verified(true)
                        .origin("uaa")
                        .zoneId("uaa")
                        .passwordLastModified("2016-06-16T00:01:41.000Z")
                        .schema("urn:scim:schemas:core:1.0")
                        .build()
                    )
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listMembersNoEntity() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/Groups/f87c557a-8ddc-43d3-98fb-e420ebc7f0f1/members?returnEntities=false")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/GET_members_response_no_entity.json")
                .build())
            .build());

        this.groups
            .listMembers(ListMembersRequest.builder()
                .groupId("f87c557a-8ddc-43d3-98fb-e420ebc7f0f1")
                .returnEntities(false)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListMembersResponse.builder()
                .member(Member.builder()
                    .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                    .type(MemberType.USER)
                    .origin("uaa")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void mapExternalGroup() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/Groups/External")
                .payload("fixtures/uaa/groups/POST_external_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/uaa/groups/POST_external_response.json")
                .build())
            .build());

        this.groups
            .mapExternalGroup(MapExternalGroupRequest.builder()
                .groupId("76937b62-346c-4848-953c-d790b87ec80a")
                .externalGroup("External group")
                .build())
            .as(StepVerifier::create)
            .expectNext(MapExternalGroupResponse.builder()
                .groupId("76937b62-346c-4848-953c-d790b87ec80a")
                .displayName("Group For Testing Creating External Group Mapping")
                .origin("ldap")
                .externalGroup("External group")
                .metadata(Metadata.builder()
                    .created("2016-06-16T00:01:41.393Z")
                    .lastModified("2016-06-16T00:01:41.393Z")
                    .version(0)
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeMember() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/Groups/test-group-id/members/40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/DELETE_{id}_members_{id}_response.json")
                .build())
            .build());

        this.groups
            .removeMember(RemoveMemberRequest.builder()
                .groupId("test-group-id")
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .build())
            .as(StepVerifier::create)
            .expectNext(RemoveMemberResponse.builder()
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .type(MemberType.USER)
                .origin("uaa")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void success() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/Groups/test-group-id")
                .header("If-Match", "0")
                .header("X-Identity-Zone-Id", "uaa")
                .payload("fixtures/uaa/groups/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/PUT_{id}_response.json")
                .build())
            .build());

        this.groups
            .update(UpdateGroupRequest.builder()
                .identityZoneId("uaa")
                .groupId("test-group-id")
                .version("0")
                .description("the cool group")
                .displayName("Cooler Group Name for Update")
                .member(MemberSummary.builder()
                    .origin("uaa")
                    .type(MemberType.USER)
                    .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateGroupResponse.builder()
                .id("test-group-id")
                .metadata(Metadata.builder()
                    .created("2016-06-03T17:59:30.527Z")
                    .lastModified("2016-06-03T17:59:30.561Z")
                    .version(1)
                    .build())
                .description("the cool group")
                .displayName("Cooler Group Name for Update")
                .member(MemberSummary.builder()
                    .origin("uaa")
                    .type(MemberType.USER)
                    .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .zoneId("uaa")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unmapExternalGroupByGroupDisplayName() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/Groups/External/displayName/Group%20For%20Testing%20Deleting%20External%20Group%20Mapping%20By%20Name/externalGroup/external%20group/origin/ldap")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/DELETE_external_displayname_{displayName}_externalgroup_{externalGroup}_origin_{origin}_response.json")
                .build())
            .build());

        this.groups
            .unmapExternalGroupByGroupDisplayName(UnmapExternalGroupByGroupDisplayNameRequest.builder()
                .groupDisplayName("Group For Testing Deleting External Group Mapping By Name")
                .externalGroup("external group")
                .origin("ldap")
                .build())
            .as(StepVerifier::create)
            .expectNext(UnmapExternalGroupByGroupDisplayNameResponse.builder()
                .groupId("f8f0048f-de32-4d20-b41d-5820b690063d")
                .displayName("Group For Testing Deleting External Group Mapping By Name")
                .origin("ldap")
                .externalGroup("external group")
                .metadata(Metadata.builder()
                    .created("2016-06-16T00:01:41.465Z")
                    .lastModified("2016-06-16T00:01:41.465Z")
                    .version(0)
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unmapExternalGroupByGroupId() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/Groups/External/groupId/d68167b4-81b3-490d-9838-94092d5c89f6/externalGroup/external%20group/origin/ldap")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/groups/DELETE_external_groupid_{groupId}_externalgroup_{externalGroup}_origin_{origin}_response.json")
                .build())
            .build());

        this.groups
            .unmapExternalGroupByGroupId(UnmapExternalGroupByGroupIdRequest.builder()
                .groupId("d68167b4-81b3-490d-9838-94092d5c89f6")
                .externalGroup("external group")
                .origin("ldap")
                .build())
            .as(StepVerifier::create)
            .expectNext(UnmapExternalGroupByGroupIdResponse.builder()
                .groupId("d68167b4-81b3-490d-9838-94092d5c89f6")
                .displayName("Group For Testing Deleting External Group Mapping")
                .origin("ldap")
                .externalGroup("external group")
                .metadata(Metadata.builder()
                    .created("2016-06-16T00:01:41.223Z")
                    .lastModified("2016-06-16T00:01:41.223Z")
                    .version(0)
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
