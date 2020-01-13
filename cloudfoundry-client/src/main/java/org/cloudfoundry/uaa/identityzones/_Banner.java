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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The payload for the identity branding banner configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _Banner {

    /**
     * The hexadecimal color code for banner background color
     */
    @JsonProperty("backgroundColor")
    @Nullable
    abstract String getBackgroundColor();

    /**
     * The UAA login banner will be a link pointing to this url
     */
    @JsonProperty("link")
    @Nullable
    abstract String getLink();

    /**
     * The base64 encoded PNG data displayed in a banner at the top of the UAA login page, overrides banner text
     */
    @JsonProperty("logo")
    @Nullable
    abstract String getLogo();

    /**
     * The text displayed in a banner at the top of the UAA login page
     */
    @JsonProperty("text")
    @Nullable
    abstract String getText();

    /**
     * The hexadecimal color code for the banner text color
     */
    @JsonProperty("textColor")
    @Nullable
    abstract String getTextColor();

}
