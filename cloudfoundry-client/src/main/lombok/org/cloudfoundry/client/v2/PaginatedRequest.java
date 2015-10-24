/*
 * Copyright 2013-2015 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.QueryParameter;

/**
 * Base class for requests that are paginated
 */
@Data
public abstract class PaginatedRequest {

    /**
     * The order direction
     *
     * @param orderDirection the order direction
     * @return the order direction
     */
    @Getter(onMethod = @__(@QueryParameter("order-direction")))
    private final OrderDirection orderDirection;

    /**
     * The page
     *
     * @param page the page
     * @return the page
     */
    @Getter(onMethod = @__(@QueryParameter("page")))
    private final Integer page;

    /**
     * The results per page
     *
     * @param resultsPerPage the results per page
     * @return the results per page
     */
    @Getter(onMethod = @__(@QueryParameter("results-per-page")))
    private final Integer resultsPerPage;

    protected PaginatedRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage) {
        this.orderDirection = orderDirection;
        this.page = page;
        this.resultsPerPage = resultsPerPage;
    }

    /**
     * The order direction of the {@link PaginatedRequest}
     */
    public enum OrderDirection {

        /**
         * Indicates that order should be ascending
         */
        ASC,

        /**
         * Indicates that order should be descending
         */
        DESC;

        @JsonValue
        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

}
