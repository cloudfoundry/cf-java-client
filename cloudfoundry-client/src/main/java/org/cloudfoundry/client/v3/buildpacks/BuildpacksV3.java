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

package org.cloudfoundry.client.v3.buildpacks;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Buildpacks V3 Client API
 */
public interface BuildpacksV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.91.0/index.html#create-a-buildpack">Create the Buildpack</a> request
     *
     * @param request the Create Buildpack request
     * @return the response from the Create Buildpack request
     */
    Mono<CreateBuildpackResponse> create(CreateBuildpackRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.91.0/index.html#delete-a-buildpack">Delete the Buildpack</a> request
     *
     * @param request the Delete Buildpack request
     * @return the response from the Delete Buildpack request
     */
    Mono<String> delete(DeleteBuildpackRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.91.0/index.html#get-a-buildpack">Retrieve a particular Buildpack</a> request
     *
     * @param request the Get Buildpack request
     * @return the response from the Get Buildpack request
     */
    Mono<GetBuildpackResponse> get(GetBuildpackRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.91.0/index.html#list-buildpacks">List all Buildpacks</a> request
     *
     * @param request the List all Buildpacks request
     * @return the response from the List all Buildpacks request
     */
    Mono<ListBuildpacksResponse> list(ListBuildpacksRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.91.0/index.html#update-a-buildpack">Update a Buildpack</a> requests
     *
     * @param request the Update Buildpack request
     * @return the response from the Update Buildpack request
     */
    Mono<UpdateBuildpackResponse> update(UpdateBuildpackRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.91.0/index.html#upload-buildpack-bits">Upload Buildpack</a> request
     *
     * @param request the Upload Buildpack request
     * @return the response from the Upload Buildpack request
     */
    Mono<UploadBuildpackResponse> upload(UploadBuildpackRequest request);

}
