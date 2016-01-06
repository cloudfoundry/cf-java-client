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

package org.cloudfoundry.client.v3;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.ValidationResult;

/**
 * Base class for requests that are paginated and sorted
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class PaginatedAndSortedRequest extends PaginatedRequest {

    /**
     * The order by
     *
     * @param orderBy the order by
     * @return the order by
     */
    @Getter(onMethod = @__(@QueryParameter("order_by")))
    private final OrderBy orderBy;

    /**
     * The order direction
     *
     * @param orderDirection the order direction
     * @return the order direction
     */
    @Getter(onMethod = @__(@QueryParameter("order_direction")))
    private final OrderDirection orderDirection;

    protected PaginatedAndSortedRequest(Integer page, Integer perPage, OrderBy orderBy, OrderDirection orderDirection) {
        super(page, perPage);
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    /**
     * Returns whether a {@link PaginatedAndSortedRequest} instance is valid
     *
     * @return a result indicating whether an instance is valid, and messages about what is wrong if it is invalid
     */
    protected final ValidationResult.ValidationResultBuilder isPaginatedAndSortedRequestValid() {
        return isPaginatedRequestValid();
    }

    /**
     * Sorting candidates in a V3 sorted request
     */
    public enum OrderBy {

        /**
         * Created at
         */
        CREATED_AT,

        /**
         * Updated at
         */
        UPDATED_AT;

        @JsonValue
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * Sorting order in a V3 sorted request
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
