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

package org.cloudfoundry.client.v2.securitygroups;

import reactor.core.publisher.Mono;

public interface SecurityGroups {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/associate_space_with_the_security_group.html">Associate Space with the Security Group</a> request.
     *
     * @param request the associate security group space request
     * @return the response from the associate security group space request
     */
    Mono<AssociateSecurityGroupSpaceResponse> associateSpace(AssociateSecurityGroupSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/creating_a_security_group.html">Creating a Security Group</a> request.
     *
     * @param request the create security group request
     * @return the response from the create security group request
     */
    Mono<CreateSecurityGroupResponse> create(CreateSecurityGroupRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/delete_a_particular_security_group.html">Delete a Particular Security Group</a> request.
     *
     * @param request the delete security group request
     * @return the response from the delete security group request
     */
    Mono<DeleteSecurityGroupResponse> delete(DeleteSecurityGroupRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/retrieve_a_particular_security_group.html">Retrieve a Particular Security Group</a> request.
     *
     * @param request the get security groups request
     * @return the response from the get security groups request
     */
    Mono<GetSecurityGroupResponse> get(GetSecurityGroupRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/list_all_security_groups.html">List all Security Groups</a> request.
     *
     * @param request the list all security groups request
     * @return the response from the list all security groups request
     */
    Mono<ListSecurityGroupsResponse> list(ListSecurityGroupsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_group_running_defaults/return_the_security_groups_used_for_running_apps.html">List Running Security Groups</a>
     * request.
     *
     * @param request the list running security groups request
     * @return the response from the list running security groups request
     */
    Mono<ListSecurityGroupRunningDefaultsResponse> listRunningDefaults(ListSecurityGroupRunningDefaultsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/list_all_spaces_for_the_security_group.html">List all Spaces for the Security Group</a> request.
     *
     * @param request the list all spaces for the security group request
     * @return the response from the list all spaces for the security group request
     */
    Mono<ListSecurityGroupSpacesResponse> listSpaces(ListSecurityGroupSpacesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_group_staging_defaults/return_the_security_groups_used_for_staging.html">List Staging Security Groups</a> request.
     *
     * @param request the list staging security groups request
     * @return the response from the list staging security groups request
     */
    Mono<ListSecurityGroupStagingDefaultsResponse> listStagingDefaults(ListSecurityGroupStagingDefaultsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_group_running_defaults/removing_a_security_group_as_a_default_for_running_apps.html">
     * Removing a Security Group as a default for running Apps</a> request.
     *
     * @param request the remove running security group request
     * @return the response from the remove running security group request
     */
    Mono<Void> removeRunningDefault(RemoveSecurityGroupRunningDefaultRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/remove_space_from_the_security_group.html">Remove Space from the Security Group</a> request.
     *
     * @param request the remove security group space request
     * @return the response from the remove security group space request
     */
    Mono<Void> removeSpace(RemoveSecurityGroupSpaceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_group_staging_defaults/removing_a_security_group_as_a_default_for_staging.html">
     * Removing a Security Group as a default for staging</a> request.
     *
     * @param request the remove staging security group request
     * @return the response from the remove staging security group request
     */
    Mono<Void> removeStagingDefault(RemoveSecurityGroupStagingDefaultRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_group_running_defaults/set_a_security_group_as_a_default_for_running_apps.html">Set a Security Group as a default for
     * running Apps</a> request.
     *
     * @param request the list running security groups request
     * @return the response from the list running security groups request
     */
    Mono<SetSecurityGroupRunningDefaultResponse> setRunningDefault(SetSecurityGroupRunningDefaultRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_group_staging_defaults/set_a_security_group_as_a_default_for_staging.html">Set a Security Group as a default for
     * staging Apps</a> request.
     *
     * @param request the list staging security groups request
     * @return the response from the list staging security groups request
     */
    Mono<SetSecurityGroupStagingDefaultResponse> setStagingDefault(SetSecurityGroupStagingDefaultRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/security_groups/updating_a_security_group.html">Updating a Security Group</a> request.
     *
     * @param request the update security group request
     * @return the response from the update security group request
     */
    Mono<UpdateSecurityGroupResponse> update(UpdateSecurityGroupRequest request);

}
