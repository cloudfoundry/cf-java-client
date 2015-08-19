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

package org.cloudfoundry.client.v3;

import com.fasterxml.jackson.annotation.JsonValue;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.ValidationResult;

/**
 * Base class for requests that are paginated and sorted
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <T> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 */
public abstract class PaginatedAndSortedRequest<T extends PaginatedAndSortedRequest<T>> extends PaginatedRequest<T> {

    private volatile OrderBy orderBy;

    private volatile OrderDirection orderDirection;

    /**
     * Returns the order by
     *
     * @return the order by
     */
    @QueryParameter("order_by")
    public final OrderBy getOrderBy() {
        return this.orderBy;
    }

    /**
     * Configure the order by
     *
     * @param orderBy the order by
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return (T) this;
    }

    /**
     * Returns the order direction
     *
     * @return the order direction
     */
    @QueryParameter("order_direction")
    public final OrderDirection getOrderDirection() {
        return this.orderDirection;
    }

    /**
     * Configure the order direction
     *
     * @param orderDirection the order direction
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
        return (T) this;
    }

    /**
     * Returns whether a {@link PaginatedAndSortedRequest} instance is valid
     *
     * @return a result indicating whether an instance is valid, and messages about what is wrong if it is invalid
     */
    protected final ValidationResult isPaginatedAndSortedRequestValid() {
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
