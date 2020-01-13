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

import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;

/**
 * Base class for requests that are paginated
 */
public abstract class PaginatedRequest {

    /**
     * The order direction
     */
    @Nullable
    @QueryParameter("order-direction")
    public abstract OrderDirection getOrderDirection();

    /**
     * The page
     */
    @Nullable
    @QueryParameter("page")
    public abstract Integer getPage();

    /**
     * The results per page
     */
    @Nullable
    @QueryParameter("results-per-page")
    public abstract Integer getResultsPerPage();

}
