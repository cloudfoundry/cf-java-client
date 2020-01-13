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

package org.cloudfoundry.client.v2.buildpacks;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Buildpacks Client API
 */
public interface Buildpacks {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/creates_an_admin_buildpack.html">Create the Buildpack</a> request
     *
     * @param request the Create Buildpack request
     * @return the response from the Create Buildpack request
     */
    Mono<CreateBuildpackResponse> create(CreateBuildpackRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/delete_a_particular_buildpack.html">Delete the Buildpack</a> request
     *
     * @param request the Delete Buildpack request
     * @return the response from the Delete Buildpack request
     */
    Mono<DeleteBuildpackResponse> delete(DeleteBuildpackRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/retrieve_a_particular_buildpack.html">Retrieve a particular Buildpack</a> request
     *
     * @param request the Get Buildpack request
     * @return the response from the Get Buildpack request
     */
    Mono<GetBuildpackResponse> get(GetBuildpackRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/list_all_buildpacks.html">List all Buildpacks</a> request
     *
     * @param request the List all Buildpacks request
     * @return the response from the List all Buildpacks request
     */
    Mono<ListBuildpacksResponse> list(ListBuildpacksRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/change_the_position_of_a_buildpack.html">Change the position of a Buildpack</a>,
     * <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/enable_or_disable_a_buildpack.html">Enable or disable a Buildpack</a>, and
     * <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/lock_or_unlock_a_buildpack.html">Lock or unlock a Buildpack</a> requests
     *
     * @param request the Update Buildpack request
     * @return the response from the Update Buildpack request
     */
    Mono<UpdateBuildpackResponse> update(UpdateBuildpackRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/buildpacks/upload_the_bits_for_an_admin_buildpack.html">Upload Buildpack</a> request
     *
     * @param request the Upload Buildpack request
     * @return the response from the Upload Buildpack request
     */
    Mono<UploadBuildpackResponse> upload(UploadBuildpackRequest request);

}
