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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.uaa.groups.CreateGroupRequest;
import org.cloudfoundry.uaa.groups.CreateGroupResponse;
import org.cloudfoundry.uaa.groups.DeleteGroupRequest;
import org.cloudfoundry.uaa.groups.DeleteGroupResponse;
import org.cloudfoundry.uaa.groups.GetGroupRequest;
import org.cloudfoundry.uaa.groups.GetGroupResponse;
import org.cloudfoundry.uaa.groups.Groups;
import org.cloudfoundry.uaa.groups.ListGroupsRequest;
import org.cloudfoundry.uaa.groups.ListGroupsResponse;
import org.cloudfoundry.uaa.groups.MapExternalGroupRequest;
import org.cloudfoundry.uaa.groups.MapExternalGroupResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdResponse;
import org.cloudfoundry.uaa.groups.UpdateGroupRequest;
import org.cloudfoundry.uaa.groups.UpdateGroupResponse;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Reactor-based implementation of {@link Groups}
 */
public class ReactorGroups extends AbstractUaaOperations implements Groups {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorGroups(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<CreateGroupResponse> create(CreateGroupRequest request) {
        return post(request, CreateGroupResponse.class, builder -> builder.pathSegment("Groups"));
    }

    @Override
    public Mono<DeleteGroupResponse> delete(DeleteGroupRequest request) {
        return delete(request, DeleteGroupResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId()));
    }

    @Override
    public Mono<GetGroupResponse> get(GetGroupRequest request) {
        return get(request, GetGroupResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId()));
    }

    @Override
    public Mono<ListGroupsResponse> list(ListGroupsRequest request) {
        return get(request, ListGroupsResponse.class, builder -> builder.pathSegment("Groups"));
    }

    @Override
    public Mono<MapExternalGroupResponse> mapExternalGroup(MapExternalGroupRequest request) {
        return post(request, MapExternalGroupResponse.class, builder -> builder.pathSegment("Groups", "External"));
    }

    @Override
    public Mono<UnmapExternalGroupByGroupDisplayNameResponse> unmapExternalGroupByGroupDisplayName(UnmapExternalGroupByGroupDisplayNameRequest request) {
        return delete(
            request,
            UnmapExternalGroupByGroupDisplayNameResponse.class,
            builder -> builder.pathSegment("Groups", "External", "displayName", request.getGroupDisplayName(), "externalGroup", request.getExternalGroup(), "origin", request.getOriginKey()));
    }

    @Override
    public Mono<UnmapExternalGroupByGroupIdResponse> unmapExternalGroupByGroupId(UnmapExternalGroupByGroupIdRequest request) {
        return delete(
            request,
            UnmapExternalGroupByGroupIdResponse.class,
            builder -> builder.pathSegment("Groups", "External", "groupId", request.getGroupId(), "externalGroup", request.getExternalGroup(), "origin", request.getOriginKey()));
    }

    @Override
    public Mono<UpdateGroupResponse> update(UpdateGroupRequest request) {
        return put(request, UpdateGroupResponse.class, builder -> builder.pathSegment("Groups", request.getGroupId()));
    }

}
