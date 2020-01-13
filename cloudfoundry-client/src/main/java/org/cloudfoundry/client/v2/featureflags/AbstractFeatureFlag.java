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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

/**
 * General feature flag payload.
 */
public abstract class AbstractFeatureFlag {

    /**
     * The state of the feature flag
     */
    @JsonProperty("enabled")
    @Nullable
    abstract Boolean getEnabled();

    /**
     * The custom error message for the feature flag
     */
    @JsonProperty("error_message")
    @Nullable
    abstract String getErrorMessage();

    /**
     * The name of the feature flag
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The url for the feature flag
     */
    @JsonProperty("url")
    @Nullable
    abstract String getUrl();

}
