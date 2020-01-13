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

import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;

/**
 * Base class for requests that are paginated
 */
public abstract class PaginatedAndSortedRequest {

    /**
     * The number of results per page
     */
    @Nullable
    @QueryParameter("count")
    public abstract Integer getCount();

    /**
     * The filter
     */
    @Nullable
    @QueryParameter("filter")
    public abstract String getFilter();

    /**
     * The sort order
     */
    @Nullable
    @QueryParameter("sortOrder")
    public abstract SortOrder getSortOrder();

    /**
     * The start index
     */
    @Nullable
    @QueryParameter("startIndex")
    public abstract Integer getStartIndex();

}
