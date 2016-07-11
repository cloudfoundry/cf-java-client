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
import org.cloudfoundry.uaa.groups.ExternalGroupMapping;
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
import org.cloudfoundry.uaa.groups.MemberUser;
import org.cloudfoundry.uaa.groups.RemoveMemberRequest;
import org.cloudfoundry.uaa.groups.RemoveMemberResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdResponse;
import org.cloudfoundry.uaa.groups.UpdateGroupRequest;
import org.cloudfoundry.uaa.groups.UpdateGroupResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.uaa.SortOrder.ASCENDING;


public final class ReactorGroupsTest {

    public static final class AddMember extends AbstractUaaApiTest<AddMemberRequest, AddMemberResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/Groups/test-group-id/members")
                    .payload("fixtures/uaa/groups/POST_{id}_members_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/groups/POST_{id}_members_response.json")
                    .build())
                .build();
        }

        @Override
        protected AddMemberResponse getResponse() {
            return AddMemberResponse.builder()
                .origin("uaa")
                .type(MemberType.USER)
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .build();
        }

        @Override
        protected AddMemberRequest getValidRequest() throws Exception {
            return AddMemberRequest.builder()
                .groupId("test-group-id")
                .origin("uaa")
                .type(MemberType.USER)
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .build();
        }

        @Override
        protected Mono<AddMemberResponse> invoke(AddMemberRequest request) {
            return this.groups.addMember(request);
        }
    }

    public static final class CheckMember extends AbstractUaaApiTest<CheckMembershipRequest, CheckMembershipResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/Groups/test-group-id/members/test-member-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/GET_{id}_members_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected CheckMembershipResponse getResponse() {
            return CheckMembershipResponse.builder()
                .origin("uaa")
                .type(MemberType.USER)
                .memberId("test-member-id")
                .build();
        }

        @Override
        protected CheckMembershipRequest getValidRequest() throws Exception {
            return CheckMembershipRequest.builder()
                .groupId("test-group-id")
                .memberId("test-member-id")
                .build();
        }

        @Override
        protected Mono<CheckMembershipResponse> invoke(CheckMembershipRequest request) {
            return this.groups.checkMembership(request);
        }
    }

    public static final class Create extends AbstractUaaApiTest<CreateGroupRequest, CreateGroupResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .header("X-Identity-Zone-Id", "uaa")
                    .method(POST).path("/Groups")
                    .payload("fixtures/uaa/groups/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/groups/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateGroupResponse getResponse() {
            return CreateGroupResponse.builder()
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
                .build();
        }

        @Override
        protected CreateGroupRequest getValidRequest() throws Exception {
            return CreateGroupRequest.builder()
                .description("the cool group")
                .displayName("Cool Group Name")
                .identityZoneId("uaa")
                .member(MemberSummary.builder()
                    .origin("uaa")
                    .type(MemberType.USER)
                    .memberId("f0e6a061-6e3a-4be9-ace5-142ee24e20b7")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateGroupResponse> invoke(CreateGroupRequest request) {
            return this.groups.create(request);
        }
    }

    public static final class Delete extends AbstractUaaApiTest<DeleteGroupRequest, DeleteGroupResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .header("If-Match", "*")
                    .method(DELETE).path("/Groups/test-group-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/DELETE_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected DeleteGroupResponse getResponse() {
            return DeleteGroupResponse.builder()
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
                .build();
        }

        @Override
        protected DeleteGroupRequest getValidRequest() throws Exception {
            return DeleteGroupRequest.builder()
                .groupId("test-group-id")
                .version("*")
                .build();
        }

        @Override
        protected Mono<DeleteGroupResponse> invoke(DeleteGroupRequest request) {
            return this.groups.delete(request);
        }
    }

    public static final class Get extends AbstractUaaApiTest<GetGroupRequest, GetGroupResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/Groups/test-group-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetGroupResponse getResponse() {
            return GetGroupResponse.builder()
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
                .build();
        }

        @Override
        protected GetGroupRequest getValidRequest() throws Exception {
            return GetGroupRequest.builder()
                .groupId("test-group-id")
                .build();
        }

        @Override
        protected Mono<GetGroupResponse> invoke(GetGroupRequest request) {
            return this.groups.get(request);
        }
    }

    public static final class List extends AbstractUaaApiTest<ListGroupsRequest, ListGroupsResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET)
                    .path("/Groups?count=50&filter=id%2Beq%2B%22f87c557a-8ddc-43d3-98fb-e420ebc7f0f1%22%2Bor%2BdisplayName%2Beq%2B%22Cooler%20Group%20Name%20for%20List%22" +
                        "&sortBy=email&sortOrder=ascending&startIndex=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListGroupsResponse getResponse() {
            return ListGroupsResponse.builder()
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
                .build();
        }

        @Override
        protected ListGroupsRequest getValidRequest() throws Exception {
            return ListGroupsRequest.builder()
                .filter("id+eq+\"f87c557a-8ddc-43d3-98fb-e420ebc7f0f1\"+or+displayName+eq+\"Cooler Group Name for List\"")
                .count(50)
                .startIndex(1)
                .sortBy("email")
                .sortOrder(ASCENDING)
                .build();
        }

        @Override
        protected Mono<ListGroupsResponse> invoke(ListGroupsRequest request) {
            return this.groups.list(request);
        }
    }

    public static final class ListExternalGroupMappings extends AbstractUaaApiTest<ListExternalGroupMappingsRequest, ListExternalGroupMappingsResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET)
                    .path("/Groups/External?count=50&filter=group_id%2Beq%2B%220480db7f-d1bc-4d2b-b723-febc684c0ee9%22&startIndex=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/GET_external_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListExternalGroupMappingsResponse getResponse() {
            return ListExternalGroupMappingsResponse.builder()
                .resource(ExternalGroupMapping.builder()
                    .groupId("0480db7f-d1bc-4d2b-b723-febc684c0ee9")
                    .groupDisplayName("Group For Testing Retrieving External Group Mappings")
                    .originKey("ldap")
                    .externalGroup("external group")
                    .metadata(Metadata.builder()
                        .created("2016-06-16T00:01:41.223Z")
                        .lastModified("2016-06-16T00:01:41.223Z")
                        .version(0)
                        .build())
                    .build()
                )
                .startIndex(1)
                .itemsPerPage(1)
                .totalResults(1)
                .schema("urn:scim:schemas:core:1.0")
                .build();
        }

        @Override
        protected ListExternalGroupMappingsRequest getValidRequest() throws Exception {
            return ListExternalGroupMappingsRequest.builder()
                .filter("group_id+eq+\"0480db7f-d1bc-4d2b-b723-febc684c0ee9\"")
                .count(50)
                .startIndex(1)
                .build();
        }

        @Override
        protected Mono<ListExternalGroupMappingsResponse> invoke(ListExternalGroupMappingsRequest request) {
            return this.groups.listExternalGroupMappings(request);
        }
    }

    public static final class ListMembers extends AbstractUaaApiTest<ListMembersRequest, ListMembersResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET)
                    .path("/Groups/f87c557a-8ddc-43d3-98fb-e420ebc7f0f1/members?returnEntities=true")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/GET_members_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListMembersResponse getResponse() {
            return ListMembersResponse.builder()
                .member(Member.builder()
                    .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                    .type(MemberType.USER)
                    .origin("uaa")
                    .entity(MemberUser.builder()
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
                .build();
        }

        @Override
        protected ListMembersRequest getValidRequest() throws Exception {
            return ListMembersRequest.builder()
                .groupId("f87c557a-8ddc-43d3-98fb-e420ebc7f0f1")
                .returnEntities(true)
                .build();
        }

        @Override
        protected Mono<ListMembersResponse> invoke(ListMembersRequest request) {
            return this.groups.listMembers(request);
        }
    }

    public static final class MapExternalGroup extends AbstractUaaApiTest<MapExternalGroupRequest, MapExternalGroupResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/Groups/External")
                    .payload("fixtures/uaa/groups/POST_external_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/uaa/groups/POST_external_response.json")
                    .build())
                .build();
        }

        @Override
        protected MapExternalGroupResponse getResponse() {
            return MapExternalGroupResponse.builder()
                .groupId("76937b62-346c-4848-953c-d790b87ec80a")
                .groupDisplayName("Group For Testing Creating External Group Mapping")
                .originKey("ldap")
                .externalGroup("External group")
                .metadata(Metadata.builder()
                    .created("2016-06-16T00:01:41.393Z")
                    .lastModified("2016-06-16T00:01:41.393Z")
                    .version(0)
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .build();
        }

        @Override
        protected MapExternalGroupRequest getValidRequest() throws Exception {
            return MapExternalGroupRequest.builder()
                .groupId("76937b62-346c-4848-953c-d790b87ec80a")
                .externalGroup("External group")
                .build();
        }

        @Override
        protected Mono<MapExternalGroupResponse> invoke(MapExternalGroupRequest request) {
            return this.groups.mapExternalGroup(request);
        }
    }

    public static final class RemoveMember extends AbstractUaaApiTest<RemoveMemberRequest, RemoveMemberResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/Groups/test-group-id/members/40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/DELETE_{id}_members_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected RemoveMemberResponse getResponse() {
            return RemoveMemberResponse.builder()
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .type(MemberType.USER)
                .origin("uaa")
                .build();
        }

        @Override
        protected RemoveMemberRequest getValidRequest() throws Exception {
            return RemoveMemberRequest.builder()
                .groupId("test-group-id")
                .memberId("40bc8ef1-0719-4a0c-9f60-e9f843cd4af2")
                .build();
        }

        @Override
        protected Mono<RemoveMemberResponse> invoke(RemoveMemberRequest request) {
            return this.groups.removeMember(request);
        }
    }

    public static final class UnmapExternalGroupByGroupDisplayName extends AbstractUaaApiTest<UnmapExternalGroupByGroupDisplayNameRequest, UnmapExternalGroupByGroupDisplayNameResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/Groups/External/displayName/Group For Testing Deleting External Group Mapping By Name/externalGroup/external group/origin/ldap")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/DELETE_external_displayname_{displayName}_externalgroup_{externalGroup}_origin_{origin}_response.json")
                    .build())
                .build();
        }

        @Override
        protected UnmapExternalGroupByGroupDisplayNameResponse getResponse() {
            return UnmapExternalGroupByGroupDisplayNameResponse.builder()
                .groupId("f8f0048f-de32-4d20-b41d-5820b690063d")
                .groupDisplayName("Group For Testing Deleting External Group Mapping By Name")
                .originKey("ldap")
                .externalGroup("external group")
                .metadata(Metadata.builder()
                    .created("2016-06-16T00:01:41.465Z")
                    .lastModified("2016-06-16T00:01:41.465Z")
                    .version(0)
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .build();
        }

        @Override
        protected UnmapExternalGroupByGroupDisplayNameRequest getValidRequest() throws Exception {
            return UnmapExternalGroupByGroupDisplayNameRequest.builder()
                .groupDisplayName("Group For Testing Deleting External Group Mapping By Name")
                .externalGroup("external group")
                .originKey("ldap")
                .build();
        }

        @Override
        protected Mono<UnmapExternalGroupByGroupDisplayNameResponse> invoke(UnmapExternalGroupByGroupDisplayNameRequest request) {
            return this.groups.unmapExternalGroupByGroupDisplayName(request);
        }
    }

    public static final class UnmapExternalGroupByGroupId extends AbstractUaaApiTest<UnmapExternalGroupByGroupIdRequest, UnmapExternalGroupByGroupIdResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/Groups/External/groupId/d68167b4-81b3-490d-9838-94092d5c89f6/externalGroup/external group/origin/ldap")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/groups/DELETE_external_groupid_{groupId}_externalgroup_{externalGroup}_origin_{origin}_response.json")
                    .build())
                .build();
        }

        @Override
        protected UnmapExternalGroupByGroupIdResponse getResponse() {
            return UnmapExternalGroupByGroupIdResponse.builder()
                .groupId("d68167b4-81b3-490d-9838-94092d5c89f6")
                .groupDisplayName("Group For Testing Deleting External Group Mapping")
                .originKey("ldap")
                .externalGroup("external group")
                .metadata(Metadata.builder()
                    .created("2016-06-16T00:01:41.223Z")
                    .lastModified("2016-06-16T00:01:41.223Z")
                    .version(0)
                    .build())
                .schema("urn:scim:schemas:core:1.0")
                .build();
        }

        @Override
        protected UnmapExternalGroupByGroupIdRequest getValidRequest() throws Exception {
            return UnmapExternalGroupByGroupIdRequest.builder()
                .groupId("d68167b4-81b3-490d-9838-94092d5c89f6")
                .externalGroup("external group")
                .originKey("ldap")
                .build();
        }

        @Override
        protected Mono<UnmapExternalGroupByGroupIdResponse> invoke(UnmapExternalGroupByGroupIdRequest request) {
            return this.groups.unmapExternalGroupByGroupId(request);
        }
    }

    public static final class Update extends AbstractUaaApiTest<UpdateGroupRequest, UpdateGroupResponse> {

        private final ReactorGroups groups = new ReactorGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
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
                .build();
        }

        @Override
        protected UpdateGroupResponse getResponse() {
            return UpdateGroupResponse.builder()
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
                .build();
        }

        @Override
        protected UpdateGroupRequest getValidRequest() throws Exception {
            return UpdateGroupRequest.builder()
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
                .build();
        }

        @Override
        protected Mono<UpdateGroupResponse> invoke(UpdateGroupRequest request) {
            return this.groups.update(request);
        }
    }

}
