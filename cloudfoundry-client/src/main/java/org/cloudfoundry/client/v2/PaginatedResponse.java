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

package org.cloudfoundry.client.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.cloudfoundry.Nullable;

/**
 * Base class for requests that are paginated
 *
 * @param <T> the resource type
 */
public abstract class PaginatedResponse<T extends Resource<?>> {

    /**
     * The next url
     */
    @JsonProperty("next_url")
    @Nullable
    public abstract String getNextUrl();

    /**
     * The previous url
     */
    @JsonProperty("prev_url")
    @Nullable
    public abstract String getPreviousUrl();

    /**
     * The resources
     */
    @JsonProperty("resources")
    @Nullable
    public abstract List<T> getResources();

    /**
     * The total pages
     */
    @JsonProperty("total_pages")
    @Nullable
    public abstract Integer getTotalPages();

    /**
     * The total results
     */
    @JsonProperty("total_results")
    @Nullable
    public abstract Integer getTotalResults();
}
