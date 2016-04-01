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

package org.cloudfoundry.client.v2.buildpacks;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Buildpacks Client API
 */
public interface Buildpacks {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/buildpacks/delete_a_particular_buildpack.html">Delete the Buildpack</a> request
     *
     * @param request the Delete Buildpack request
     * @return the response from the Delete Buildpack request
     */
    Mono<DeleteBuildpackResponse> delete(DeleteBuildpackRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/buildpacks/list_all_buildpacks.html">List all Buildpacks</a> request
     *
     * @param request the List all Buildpacks request
     * @return the response from the List all Buildpacks request
     */
    Mono<ListBuildpacksResponse> list(ListBuildpacksRequest request);

}
