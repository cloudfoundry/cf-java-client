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

package org.cloudfoundry.uaa.groups;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Group Management Client API
 */
public interface Groups {

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#add-member">Add Member</a> request
     *
     * @param request the Add Member request
     * @return the response from the Add Member request
     */
    Mono<AddMemberResponse> addMember(AddMemberRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#create58">Create Group</a> request
     *
     * @param request the Create Group request
     * @return the response from the Create Group request
     */
    Mono<CreateGroupResponse> create(CreateGroupRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#delete61">Delete a Group</a> request
     *
     * @param request the Delete Group request
     * @return the response from the Delete Group request
     */
    Mono<DeleteGroupResponse> delete(DeleteGroupRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#retrieve59">Retrieve a Group</a> request
     *
     * @param request the Get Group request
     * @return the response from the Get Group request
     */
    Mono<GetGroupResponse> get(GetGroupRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#list62">List Groups</a> request
     *
     * @param request the List Groups request
     * @return the response from the List Groups request
     */
    Mono<ListGroupsResponse> list(ListGroupsRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#list72">List External Group Mappings</a> request
     *
     * @param request the List External Group Mappings request
     * @return the response from the List External Group Mappings request
     */
    Mono<ListExternalGroupMappingsResponse> listExternalGroupMappings(ListExternalGroupMappingsRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#map">Map External Group</a> request
     *
     * @param request the Map External Group request
     * @return the response from the Map External Group request
     */
    Mono<MapExternalGroupResponse> mapExternalGroup(MapExternalGroupRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#by-group-display-name">Unmap External Group By Group Display Name</a> request
     *
     * @param request the Unmap External Group By Group Display Name request
     * @return the response from the Unmap External Group By Group Display Name request
     */
    Mono<UnmapExternalGroupByGroupDisplayNameResponse> unmapExternalGroupByGroupDisplayName(UnmapExternalGroupByGroupDisplayNameRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#by-group-id">Unmap External Group By Group Id</a> request
     *
     * @param request the Unmap External Group By Group Id request
     * @return the response from the Unmap External Group By Group Id request
     */
    Mono<UnmapExternalGroupByGroupIdResponse> unmapExternalGroupByGroupId(UnmapExternalGroupByGroupIdRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#update60">Update Group</a> request
     *
     * @param request the Update Group request
     * @return the response from the Update Group request
     */
    Mono<UpdateGroupResponse> update(UpdateGroupRequest request);

}
