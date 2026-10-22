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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request payload for the Update Feature Flag operation
 *
 * see https://v3-apidocs.cloudfoundry.org/#update-a-feature-flag
 */
@JsonSerialize
@Value.Immutable
abstract class _UpdateFeatureFlagRequest {

    /**
     * The name of the feature flag
     */
    @JsonIgnore
    abstract String getName();

    /**
     * Whether the feature flag is enabled
     */
    @JsonProperty("enabled")
    @Nullable
    abstract Boolean getEnabled();

    /**
     * The custom error message displayed to users when the flag is disabled
     */
    @JsonProperty("custom_error_message")
    @Nullable
    abstract String getCustomErrorMessage();
}
