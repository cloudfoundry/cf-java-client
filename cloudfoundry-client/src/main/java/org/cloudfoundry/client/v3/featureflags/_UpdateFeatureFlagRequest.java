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
 *
 * Please note that a request that defines only a "name", but no "enabled" and no "customErrorMessage" is still valid.
 * It will update the "updated_at" property of the named feature flag.
 */
@JsonSerialize(using = UpdateFeatureFlagRequestSerializer.class)
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
     * The custom error message to set. Ignored when {@link #clearCustomErrorMessage()} is true.
     * When neither this nor {@link #clearCustomErrorMessage()} is set, the field is omitted from
     * the request and the existing value is preserved.
     */
    @JsonIgnore
    @Nullable
    abstract String getCustomErrorMessage();

    /**
     * When true, sends {@code "custom_error_message": null} to clear the existing message.
     * Takes precedence over {@link #getCustomErrorMessage()}.
     */
    @JsonIgnore
    @Nullable
    abstract Boolean clearCustomErrorMessage();

    @Value.Check
    void check() {
        if (getName().isEmpty()) {
            throw new IllegalStateException("name must not be empty");
        }
    }
}
