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

import lombok.Data;

/**
 * General feature flag payload.
 */
@Data
public abstract class AbstractFeatureFlag {

    /**
     * The state of the feature flag
     *
     * @param enabled the state of the feature flag
     * @return the state of the feature flag
     */
    private final Boolean enabled;

    /**
     * The custom error message for the feature flag
     *
     * @param errorMessage the custom error message for the feature flag
     * @return the custom error message for the feature flag
     */
    private final String errorMessage;

    /**
     * The name of the feature flag
     *
     * @param name the name of the feature flag
     * @return the name of the feature flag
     */
    private final String name;

    /**
     * The url for the feature flag
     *
     * @param url the url for the feature flag
     * @return the url for the feature flag
     */
    private final String url;

    AbstractFeatureFlag(Boolean enabled,
                                  String errorMessage,
                                  String name,
                                  String url) {
        this.enabled = enabled;
        this.errorMessage = errorMessage;
        this.name = name;
        this.url = url;
    }

}
