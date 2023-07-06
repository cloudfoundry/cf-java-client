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
     * @param request the create security group request
     * @return the response from the create security group request
     */
    Mono<CreateSecurityGroupResponse> create(CreateSecurityGroupRequest request);

}
