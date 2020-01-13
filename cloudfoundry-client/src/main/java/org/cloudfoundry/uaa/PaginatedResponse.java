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

import java.util.List;

/**
 * Base class for requests that are paginated
 *
 * @param <T> the entity type
 */
public abstract class PaginatedResponse<T> {

    /**
     * The itemsPerPage
     */
    @JsonProperty("itemsPerPage")
    public abstract Integer getItemsPerPage();

    /**
     * The resources
     */
    @JsonProperty("resources")
    public abstract List<T> getResources();

    /**
     * The schemas
     */
    @JsonProperty("schemas")
    public abstract List<String> getSchemas();

    /**
     * The startIndex
     */
    @JsonProperty("startIndex")
    public abstract Integer getStartIndex();

    /**
     * The total results
     */
    @JsonProperty("totalResults")
    public abstract Integer getTotalResults();

}
