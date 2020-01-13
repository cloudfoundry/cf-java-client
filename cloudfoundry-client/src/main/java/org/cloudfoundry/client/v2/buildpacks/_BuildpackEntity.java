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

package org.cloudfoundry.client.v2.buildpacks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The entity response payload for the buildpack resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _BuildpackEntity {

    /**
     * Whether the buildpack is enabled
     */
    @JsonProperty("enabled")
    @Nullable
    abstract Boolean getEnabled();

    /**
     * The filename
     */
    @JsonProperty("filename")
    @Nullable
    abstract String getFilename();

    /**
     * Whether the buildpack is locked
     */
    @JsonProperty("locked")
    @Nullable
    abstract Boolean getLocked();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The position
     */
    @JsonProperty("position")
    @Nullable
    abstract Integer getPosition();

    /**
     * The stack
     */
    @JsonProperty("stack")
    @Nullable
    abstract String getStack();

}
