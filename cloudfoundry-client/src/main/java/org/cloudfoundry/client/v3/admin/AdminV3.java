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

package org.cloudfoundry.client.v3.admin;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Admin Client API
 */
public interface AdminV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.91.0/index.html#clear-buildpack-cache">Clear BuildPack Cache</a> request
     *
     * @param request the Clear BuildPack Cache request
     * @return the response from the Clear BuildPack Cache request
     */
    Mono<String> clearBuildpackCache(ClearBuildpackCacheRequest request);
}
