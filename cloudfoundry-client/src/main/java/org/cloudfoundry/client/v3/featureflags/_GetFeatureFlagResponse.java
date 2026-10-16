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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Link;
import org.immutables.value.Value;

/**
 * The response payload for the Get Feature Flag operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _GetFeatureFlagResponse {

    /**
     * The name of the feature flag
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * Whether the feature flag is enabled
     */
    @JsonProperty("enabled")
    abstract Boolean getEnabled();

    /**
     * The custom error message displayed to users when the flag is disabled
     */
    @JsonProperty("custom_error_message")
    @Nullable
    abstract String getCustomErrorMessage();

    /**
     * The time the feature flag was last updated
     */
    @JsonProperty("updated_at")
    @Nullable
    abstract String getUpdatedAt();

    /**
     * Links to related resources and actions for the feature flag
     */
    @AllowNulls
    @JsonProperty("links")
    @Nullable
    abstract Map<String, Link> getLinks();
}
