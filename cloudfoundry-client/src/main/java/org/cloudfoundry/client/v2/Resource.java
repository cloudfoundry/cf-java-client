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

package org.cloudfoundry.client.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

/**
 * The resource payload for a paginated response
 *
 * @param <T> the type of entity contained within the resource
 */
public abstract class Resource<T> {

    /**
     * The resource's entity
     */
    @JsonProperty("entity")
    @Nullable
    public abstract T getEntity();

    /**
     * The resource's metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

}
