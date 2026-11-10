/*
 * Copyright 2026 the original author or authors.
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

package org.cloudfoundry.client.v3.featureflags;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Feature Flags V3 Client API
 * see: https://v3-apidocs.cloudfoundry.org/#feature-flags
 */
public interface FeatureFlagsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#get-a-feature-flag">Get a Feature Flag</a> request
     *
     * @param request the Get Feature Flag request
     * @return the response from the Get Feature Flag request
     */
    Mono<GetFeatureFlagResponse> get(GetFeatureFlagRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#list-feature-flags">List Feature Flags</a> request
     *
     * @param request the List Feature Flags request
     * @return the response from the List Feature Flags request
     */
    Mono<ListFeatureFlagsResponse> list(ListFeatureFlagsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#update-a-feature-flag">Update a Feature Flag</a> request
     *
     * @param request the Update Feature Flag request
     * @return the response from the Update Feature Flag request
     */
    Mono<UpdateFeatureFlagResponse> update(UpdateFeatureFlagRequest request);
}
