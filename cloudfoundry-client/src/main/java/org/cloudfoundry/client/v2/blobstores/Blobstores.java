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

package org.cloudfoundry.client.v2.blobstores;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Blobstores Client API
 */
public interface Blobstores {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/blobstores/delete_all_blobs_in_the_buildpack_cache_blobstore.html">Delete Buildpack Caches</a> request
     *
     * @param request the Delete Buildpack Caches request
     * @return the response from the Delete Buildpack Caches request
     */
    Mono<DeleteBlobstoreBuildpackCachesResponse> deleteBuildpackCaches(DeleteBlobstoreBuildpackCachesRequest request);

}
