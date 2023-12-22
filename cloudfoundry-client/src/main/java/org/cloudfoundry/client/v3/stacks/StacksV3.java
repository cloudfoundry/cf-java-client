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

package org.cloudfoundry.client.v3.stacks;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Stacks V3 Client API
 */
public interface StacksV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.94.0/#create-a-stack">Create a Stack</a> request
     *
     * @param request the Create Stack request
     * @return the response from the Create Stack request
     */
    Mono<CreateStackResponse> create(CreateStackRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.94.0/#delete-a-stack">Delete a Stack</a> request
     *
     * @param request the Delete Stack request
     * @return the response from Delete Stack request
     */
    Mono<Void> delete(DeleteStackRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.94.0/#get-a-stack">Get a Stack</a> request
     *
     * @param request the Get Stack request
     * @return the response from the Get Stack request
     */
    Mono<GetStackResponse> get(GetStackRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.94.0/#list-stacks">List Stacks</a> request
     *
     * @param request the List Stacks request
     * @return the response from the List Stacks request
     */
    Mono<ListStacksResponse> list(ListStacksRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.94.0/#update-a-stack">Update a Stack</a> request
     *
     * @param request the Update Stack request
     * @return the response from the Update Stack request
     */
    Mono<UpdateStackResponse> update(UpdateStackRequest request);
}
