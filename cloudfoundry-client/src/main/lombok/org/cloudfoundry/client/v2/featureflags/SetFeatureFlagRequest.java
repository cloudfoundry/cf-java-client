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

package org.cloudfoundry.client.v2.featureflags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Set Feature Flag operation
 */
@Data
public final class SetFeatureFlagRequest implements Validatable {

    /**
     * The state of the feature flag
     *
     * @param enabled the state of the feature flag
     * @return the state of the feature flag
     */
    @Getter(onMethod = @__(@JsonProperty("enabled")))
    private final Boolean enabled;

    /**
     * The custom error message for the feature flag
     *
     * @param errorMessage the custom error message for the feature flag
     * @return the custom error message for the feature flag
     */
    @Getter(onMethod = @__(@JsonProperty("error_message")))
    private final String errorMessage;

    /**
     * The name of the feature flag
     *
     * @param name the name of the feature flag
     * @return the name of the feature flag
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String name;

    @Builder
    SetFeatureFlagRequest(Boolean enabled,
                          String errorMessage,
                          String name) {
        this.enabled = enabled;
        this.errorMessage = errorMessage;
        this.name = name;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.enabled == null) {
            builder.message("enabled must be specified");
        }

        return builder.build();
    }
}
