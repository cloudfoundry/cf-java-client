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

package org.cloudfoundry.client.v2.runningsecuritygroups;

import reactor.core.publisher.Mono;

public interface RunningSecurityGroups {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/security_group_running_defaults/return_the_security_groups_used_for_running_apps.html">List Running Security Groups</a>
     * request.
     *
     * @param request the list running security groups request
     * @return the response from the list running security groups request
     */
    Mono<ListRunningSecurityGroupResponse> list(ListRunningSecurityGroupsRequest request);

}
