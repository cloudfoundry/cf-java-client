/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v2.stacks;

import reactor.core.publisher.Mono;

public interface Stacks {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/stacks/retrieve_a_particular_stack.html">Get Stack</a> request
     *
     * @param request the Get Stack request
     * @return the response from the Get Stack Request
     */
    Mono<GetStackResponse> get(GetStackRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/stacks/list_all_stacks.html">List Stacks</a> request
     *
     * @param request the List Stacks request
     * @return the response from the List Stacks request
     */
    Mono<ListStacksResponse> list(ListStacksRequest request);

}
