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

package org.cloudfoundry.client.v3.securitygroups;

import reactor.core.publisher.Mono;

public interface SecurityGroupsV3 {

    /**
     * Makes the <a href=
     * "https://apidocs.cloudfoundry.org/latest-release/security_groups/creating_a_security_group.html">Creating
     * a Security Group</a> request.
     *
     * @param request the Create Security Group request
     * @return the response from the Create Security Group request
     */
    Mono<CreateSecurityGroupResponse> create(CreateSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#get-a-security-group">Get
     * a Security Group</a> request.
     *
     * @param request the Get Security Group request
     * @return the response from the Get Security Group request
     */
    Mono<GetSecurityGroupResponse> get(GetSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#list-security-groups">List
     * Security Groups</a> request
     *
     * @param request the List Security Group request
     * @return the response from the List Security Group request
     */
    Mono<ListSecurityGroupsResponse> list(ListSecurityGroupsRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#update-a-security-group">Update
     * Security Groups</a> request
     *
     * @param request the Update Security Group request
     * @return the response from the Update Security Group request
     */
    Mono<UpdateSecurityGroupResponse> update(UpdateSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#delete-a-security-group">Delete
     * Security Groups</a> request
     *
     * @param request the Delete Security Group request
     * @return the response from the Delete Security Group request
     */
    Mono<String> delete(DeleteSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#bind-a-staging-security-group-to-spaces">Bind
     * Staging Security Group</a> request
     *
     * @param request the Bind Staging Security Group request
     * @return the response from the Bind Staging Security Group request
     */
    Mono<BindStagingSecurityGroupResponse> bindStagingSecurityGroup(BindStagingSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#bind-a-running-security-group-to-spaces">Bind
     * Running Security Group</a> request
     *
     * @param request the Bind Running Security Group request
     * @return the response from the Bind Running Security Group request
     */
    Mono<BindRunningSecurityGroupResponse> bindRunningSecurityGroup(BindRunningSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#unbind-a-staging-security-group-from-a-space">Unbind
     * Staging
     * Security Group</a> request
     *
     * @param request the Unbind Staging Security Group request
     * @return the response from the Unbind staging Security Group request
     */
    Mono<Void> unbindStagingSecurityGroup(UnbindStagingSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#delete-a-security-group">Unbind
     * Running
     * Security Groups</a> request
     *
     * @param request the Unbind Staging Running Security Group request
     * @return the response from the Unbind Running Security Group request
     */
    Mono<Void> unbindRunningSecurityGroup(UnbindRunningSecurityGroupRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#list-staging-security-groups-for-a-space">List
     * Running Security Groups</a> request
     *
     * @param request the List Staging Security Group request
     * @return the response from the List Staging Security Group request
     */
    Mono<ListStagingSecurityGroupsResponse> listStaging(ListStagingSecurityGroupsRequest request);

    /**
     * Makes the <a href=
     * "https://v3-apidocs.cloudfoundry.org/version/3.140.0/index.html#list-running-security-groups-for-a-space">List
     * Running Security Groups</a> request
     *
     * @param request the List Staging Security Group request
     * @return the response from the List Running Security Group request
     */
    Mono<ListRunningSecurityGroupsResponse> listRunning(ListRunningSecurityGroupsRequest request);
}
