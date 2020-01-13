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

package org.cloudfoundry.client.v3.builds;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Builds Client API
 */
public interface Builds {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#create-a-build">Create Build</a> request
     *
     * @param request the Create Build request
     * @return the response from the Create Build request
     */
    Mono<CreateBuildResponse> create(CreateBuildRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-a-build">Get Build</a> request
     *
     * @param request the Get Build request
     * @return the response from the Get Build request
     */
    Mono<GetBuildResponse> get(GetBuildRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.47.0/#list-builds">List Builds</a> request
     *
     * @param request the List Builds request
     * @return the response from the List Builds request
     */
    Mono<ListBuildsResponse> list(ListBuildsRequest request);

}
