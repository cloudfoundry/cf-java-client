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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for requests that are paginated
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <T> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 * @param <U> the entity type
 */
public abstract class PaginatedResponse<T extends PaginatedResponse<T, U>, U> {

    private volatile Pagination pagination;

    private final List<U> resources = new ArrayList<>();

    /**
     * Returns the pagination
     *
     * @return the pagination
     */
    public final Pagination getPagination() {
        return this.pagination;
    }

    /**
     * Configure the pagination
     *
     * @param pagination the pagination
     * @return {@code this}
     */
    @JsonProperty("pagination")
    @SuppressWarnings("unchecked")
    public final T withPagination(Pagination pagination) {
        this.pagination = pagination;
        return (T) this;
    }

    /**
     * Returns the resources
     *
     * @return the resources
     */
    public final List<U> getResources() {
        return this.resources;
    }

    /**
     * Configure the resource
     *
     * @param resource the resource
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withResource(U resource) {
        this.resources.add(resource);
        return (T) this;
    }

    /**
     * Configure the resources
     *
     * @param resources the resources
     * @return {@code this}
     */
    @JsonProperty("resources")
    @SuppressWarnings("unchecked")
    public final T withResources(List<U> resources) {
        this.resources.addAll(resources);
        return (T) this;
    }

    public static final class Pagination {

        private volatile Link first;

        private volatile Link last;

        private volatile Link next;

        private volatile Link previous;

        private volatile Integer totalResults;

        /**
         * Returns the first link
         *
         * @return the first link
         */
        public Link getFirst() {
            return this.first;
        }

        /**
         * Configure the first link
         *
         * @param first the first link
         * @return {@code this}
         */
        @JsonProperty("first")
        public Pagination withFirst(Link first) {
            this.first = first;
            return this;
        }

        /**
         * Returns the last link
         *
         * @return the last link
         */
        public Link getLast() {
            return this.last;
        }

        /**
         * Configure the last link
         *
         * @param last the last link
         * @return {@code this}
         */
        @JsonProperty("last")
        public Pagination withLast(Link last) {
            this.last = last;
            return this;
        }

        /**
         * Returns the next link
         *
         * @return the next link
         */
        public Link getNext() {
            return this.next;
        }

        /**
         * Configure the next link
         *
         * @param next the next link
         * @return {@code this}
         */
        @JsonProperty("next")
        public Pagination withNext(Link next) {
            this.next = next;
            return this;
        }

        /**
         * Returns the previous link
         *
         * @return the previous link
         */
        public Link getPrevious() {
            return this.previous;
        }

        /**
         * Configure the previous link
         *
         * @param previous the previous link
         * @return {@code this}
         */
        @JsonProperty("previous")
        public Pagination withPrevious(Link previous) {
            this.previous = previous;
            return this;
        }

        /**
         * Returns the total results
         *
         * @return the total results
         */
        public Integer getTotalResults() {
            return this.totalResults;
        }

        /**
         * Configure the total results
         *
         * @param totalResults the total results
         * @return {@code this}
         */
        @JsonProperty("total_results")
        public Pagination withTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
            return this;
        }

    }

}
