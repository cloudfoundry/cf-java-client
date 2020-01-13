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

package org.cloudfoundry.routing.v1.routergroups;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Routing Groups API
 */
public interface RouterGroups {

    /**
     * Makes the <a href="https://github.com/cloudfoundry-incubator/routing-api/blob/master/docs/api_docs.md#list-router-groups">List Router Groups</a> request
     *
     * @param request the List Router Groups request
     * @return the response to the List Router Groups request
     */
    Mono<ListRouterGroupsResponse> list(ListRouterGroupsRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry-incubator/routing-api/blob/master/docs/api_docs.md#update-router-group">Update Router Group</a> request
     *
     * @param request the Update Router Groups request
     * @return the response to the Update Router Groups request
     */
    Mono<UpdateRouterGroupResponse> update(UpdateRouterGroupRequest request);

}
