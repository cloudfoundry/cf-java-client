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

package org.cloudfoundry.client.v2.featureflags;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry config/feature_flags Client API
 */
public interface FeatureFlags {

    /**
     * Makes one of the Get feature flags requests. See <a href="https://apidocs.cloudfoundry.org/latest-release">API</a> under Feature Flags.
     *
     * @param request the get feature flag request
     * @return the response from the get feature flag request
     */
    Mono<GetFeatureFlagResponse> get(GetFeatureFlagRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/feature_flags/get_all_feature_flags.html">List Feature Flags</a> request
     *
     * @param request the list feature flags request
     * @return the response from the list feature flags request
     */
    Mono<ListFeatureFlagsResponse> list(ListFeatureFlagsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/feature_flags/set_a_feature_flag.html">Set Feature Flag</a> request
     *
     * @param request the set feature flag request
     * @return the response from the set feature flag request
     */
    Mono<SetFeatureFlagResponse> set(SetFeatureFlagRequest request);

}
