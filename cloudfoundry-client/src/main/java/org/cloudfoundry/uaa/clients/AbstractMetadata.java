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

package org.cloudfoundry.uaa.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

/**
 * The payload for Client Metadata responses
 */
abstract class AbstractMetadata {

    /**
     * Base64 encoded image file
     */
    @JsonProperty("appIcon")
    abstract String getAppIcon();

    /**
     * URL to which the app is linked
     */
    @JsonProperty("appLaunchUrl")
    @Nullable
    abstract String getAppLaunchUrl();

    /**
     * Client identifier, unique within identity zone
     */
    @JsonProperty("clientId")
    abstract String getClientId();

    /**
     * Client name
     */
    @JsonProperty("clientName")
    @Nullable
    abstract String getClientName();

    /**
     * Flag to control visibility on home page
     */
    @JsonProperty("showOnHomePage")
    abstract Boolean getShowOnHomePage();
}
