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

package org.cloudfoundry.uaa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The metadata payload entities
 */
@JsonDeserialize
@Value.Immutable
abstract class _Metadata {

    /**
     * When the resource record was created
     */
    @JsonProperty("created")
    @Nullable
    abstract String getCreated();

    /**
     * When the resource record was last modified
     */
    @JsonProperty("lastModified")
    @Nullable
    abstract String getLastModified();

    /**
     * The metadata version
     */
    @JsonProperty("version")
    @Nullable
    abstract Integer getVersion();

}
