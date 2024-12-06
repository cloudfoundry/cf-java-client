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

package org.cloudfoundry.uaa.identityzones;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The payload for the identity zone user configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _UserConfig {

    /**
     * The default groups
     */
    @JsonProperty("defaultGroups")
    @Nullable
    abstract List<String> getDefaultGroups();

    /**
     * The allowed groups
     */
    @JsonProperty("allowedGroups")
    @Nullable
    abstract List<String> getAllowedGroups();

    /**
     * Number of users in the zone. If more than 0, it limits the amount of users in the zone. (defaults to -1, no limit).
     */
    @JsonProperty("maxUsers")
    @Nullable
    abstract Integer getMaxUsers();

    /**
     * Flag for switching on the check if origin is valid when creating or updating users
     */
    @JsonProperty("checkOriginEnabled")
    @Nullable
    abstract Boolean getcheckOriginEnabled();

    /**
     * Flag for switching off the loop over all origins in a zone (defaults to true)
     */
    @JsonProperty("allowOriginLoop")
    @Nullable
    abstract Boolean getAllowOriginLoop();


}
