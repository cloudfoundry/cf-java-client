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

package org.cloudfoundry.client.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The metadata payload for a resource
 */
@JsonDeserialize
@Value.Immutable
abstract class AbstractMetadata {

    /**
     * When the resource was created
     */
    @JsonProperty("created_at")
    abstract String getCreatedAt();

    /**
     * The resource's id
     */
    @JsonProperty("guid")
    abstract String getId();

    /**
     * When the resource was last updated
     */
    @JsonProperty("updated_at")
    @Nullable
    abstract String getUpdatedAt();

    /**
     * The resource's URL
     */
    @JsonProperty("url")
    abstract String getUrl();

}
