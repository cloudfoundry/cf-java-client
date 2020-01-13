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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;
import org.immutables.value.Value;

/**
 * Base class for requests that are paginated
 */
public abstract class PaginatedRequest {

    /**
     * The order by
     */
    @Nullable
    @QueryParameter("order_by")
    public abstract String getOrderBy();

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
    @QueryParameter("per_page")
    public abstract Integer getPerPage();

    @Value.Check
    void check() {
        if (getPage() != null && getPage() < 1) {
            throw new IllegalStateException("page must be greater than or equal to 1");
        }

        if (getPerPage() != null && (getPerPage() < 1 || getPerPage() > 5_000)) {
            throw new IllegalStateException("perPage much be between 1 and 5000 inclusive");
        }
    }

}
