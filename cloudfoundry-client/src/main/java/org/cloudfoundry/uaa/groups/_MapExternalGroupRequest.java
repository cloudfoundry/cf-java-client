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

package org.cloudfoundry.uaa.groups;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request payload for the map external group request
 */
@Value.Immutable
abstract class _MapExternalGroupRequest {

    /**
     * The identifier for the group in external identity provider that needs to be mapped to internal UAA groups
     */
    @JsonProperty("externalGroup")
    abstract String getExternalGroup();

    /**
     * The group unique ID
     */
    @JsonProperty("groupId")
    abstract String getGroupId();

    /**
     * Unique alias of the identity provider
     */
    @JsonProperty("origin")
    @Nullable
    abstract String getOriginKey();

}
