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

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.groups.AddMemberRequest;
import org.cloudfoundry.uaa.groups.AddMemberResponse;
import org.cloudfoundry.uaa.groups.CheckMembershipRequest;
import org.cloudfoundry.uaa.groups.CheckMembershipResponse;
import org.cloudfoundry.uaa.groups.CreateGroupRequest;
import org.cloudfoundry.uaa.groups.CreateGroupResponse;
import org.cloudfoundry.uaa.groups.DeleteGroupRequest;
import org.cloudfoundry.uaa.groups.DeleteGroupResponse;
import org.cloudfoundry.uaa.groups.GetGroupRequest;
import org.cloudfoundry.uaa.groups.GetGroupResponse;
import org.cloudfoundry.uaa.groups.Groups;
import org.cloudfoundry.uaa.groups.ListExternalGroupMappingsRequest;
import org.cloudfoundry.uaa.groups.ListExternalGroupMappingsResponse;
import org.cloudfoundry.uaa.groups.ListGroupsRequest;
import org.cloudfoundry.uaa.groups.ListGroupsResponse;
import org.cloudfoundry.uaa.groups.ListMembersRequest;
import org.cloudfoundry.uaa.groups.ListMembersResponse;
import org.cloudfoundry.uaa.groups.MapExternalGroupRequest;
import org.cloudfoundry.uaa.groups.MapExternalGroupResponse;
import org.cloudfoundry.uaa.groups.RemoveMemberRequest;
import org.cloudfoundry.uaa.groups.RemoveMemberResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdResponse;
import org.cloudfoundry.uaa.groups.UpdateGroupRequest;
import org.cloudfoundry.uaa.groups.UpdateGroupResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Groups}
 */
public final class ReactorGroups extends AbstractUaaOperations implements Groups {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorGroups(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AddMemberResponse> addMember(AddMemberRequest request) {
        return post(request, AddMemberResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId(), "members"))
            .checkpoint();
    }

    @Override
    public Mono<CheckMembershipResponse> checkMembership(CheckMembershipRequest request) {
        return get(request, CheckMembershipResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId(), "members", request.getMemberId()))
            .checkpoint();
    }

    @Override
    public Mono<CreateGroupResponse> create(CreateGroupRequest request) {
        return post(request, CreateGroupResponse.class, builder -> builder.pathSegment("Groups"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteGroupResponse> delete(DeleteGroupRequest request) {
        return delete(request, DeleteGroupResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<GetGroupResponse> get(GetGroupRequest request) {
        return get(request, GetGroupResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<ListGroupsResponse> list(ListGroupsRequest request) {
        return get(request, ListGroupsResponse.class, builder -> builder.pathSegment("Groups"))
            .checkpoint();
    }

    @Override
    public Mono<ListExternalGroupMappingsResponse> listExternalGroupMappings(ListExternalGroupMappingsRequest request) {
        return get(request, ListExternalGroupMappingsResponse.class, builder -> builder.pathSegment("Groups", "External"))
            .checkpoint();
    }

    @Override
    public Mono<ListMembersResponse> listMembers(ListMembersRequest request) {
        return get(request, ListMembersResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId(), "members"))
            .checkpoint();
    }

    @Override
    public Mono<MapExternalGroupResponse> mapExternalGroup(MapExternalGroupRequest request) {
        return post(request, MapExternalGroupResponse.class, builder -> builder.pathSegment("Groups", "External"))
            .checkpoint();
    }

    @Override
    public Mono<RemoveMemberResponse> removeMember(RemoveMemberRequest request) {
        return delete(request, RemoveMemberResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId(), "members", request.getMemberId()))
            .checkpoint();
    }

    @Override
    public Mono<UnmapExternalGroupByGroupDisplayNameResponse> unmapExternalGroupByGroupDisplayName(UnmapExternalGroupByGroupDisplayNameRequest request) {
        return delete(
            request,
            UnmapExternalGroupByGroupDisplayNameResponse.class,
            builder -> builder.pathSegment("Groups", "External", "displayName", request.getGroupDisplayName(), "externalGroup", request.getExternalGroup(), "origin", request.getOrigin()))
            .checkpoint();
    }

    @Override
    public Mono<UnmapExternalGroupByGroupIdResponse> unmapExternalGroupByGroupId(UnmapExternalGroupByGroupIdRequest request) {
        return delete(
            request,
            UnmapExternalGroupByGroupIdResponse.class,
            builder -> builder.pathSegment("Groups", "External", "groupId", request.getGroupId(), "externalGroup", request.getExternalGroup(), "origin", request.getOrigin()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateGroupResponse> update(UpdateGroupRequest request) {
        return put(request, UpdateGroupResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId()))
            .checkpoint();
    }

}
