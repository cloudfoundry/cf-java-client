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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Base class for requests that are paginated
 *
 * @param <T> the entity type
 */
@Data
public abstract class PaginatedResponse<T> {

    /**
     * The pagination
     *
     * @param pagination the pagination
     * @return the pagination
     */
    private final Pagination pagination;

    /**
     * The resources
     *
     * @param resources the resources
     * @return the resources
     */
    private final List<T> resources;

    protected PaginatedResponse(@JsonProperty("pagination") Pagination pagination,
                                @JsonProperty("resources") @Singular List<T> resources) {
        this.pagination = pagination;
        this.resources = resources;
    }

    @Data
    public static final class Pagination {

        /**
         * The first
         *
         * @param first the first
         * @return the first
         */
        private final Link first;

        /**
         * The last
         *
         * @param last the last
         * @return the last
         */
        private final Link last;

        /**
         * The next
         *
         * @param next the next
         * @return the next
         */
        private final Link next;

        /**
         * The previous
         *
         * @param previous the previous
         * @return the previous
         */
        private final Link previous;

        /**
         * The total results
         *
         * @param totalResults the total results
         * @return the total results
         */
        private final Integer totalResults;

        @Builder
        Pagination(@JsonProperty("first") Link first,
                   @JsonProperty("last") Link last,
                   @JsonProperty("next") Link next,
                   @JsonProperty("previous") Link previous,
                   @JsonProperty("total_results") Integer totalResults) {
            this.first = first;
            this.last = last;
            this.next = next;
            this.previous = previous;
            this.totalResults = totalResults;
        }

    }

}
