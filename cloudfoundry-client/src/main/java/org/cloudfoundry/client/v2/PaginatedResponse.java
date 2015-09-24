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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for requests that are paginated
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <T> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 * @param <U> the resource type
 */
public abstract class PaginatedResponse<T extends PaginatedResponse<T, U>, U extends Resource<U, ?>> {

    private volatile String nextUrl;

    private volatile String previousUrl;

    private final List<U> resources = new ArrayList<>();

    private volatile Integer totalPages;

    private volatile Integer totalResults;

    /**
     * Returns the next url
     *
     * @return the next url
     */
    public final String getNextUrl() {
        return this.nextUrl;
    }

    /**
     * Configure the next url
     *
     * @param nextUrl the next url
     * @return {@code this}
     */
    @JsonProperty("next_url")
    @SuppressWarnings("unchecked")
    public final T withNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
        return (T) this;
    }

    /**
     * Returns the previous url
     *
     * @return the previous url
     */
    public final String getPreviousUrl() {
        return this.previousUrl;
    }

    /**
     * Configure the previous url
     *
     * @param previousUrl the previous url
     * @return {@code this}
     */
    @JsonProperty("prev_url")
    @SuppressWarnings("unchecked")
    public final T withPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
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
     * Add a resource
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
     * Add resources
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

    /**
     * Returns the total pages
     *
     * @return the total pages
     */
    public final Integer getTotalPages() {
        return this.totalPages;
    }

    /**
     * Configure the total pages
     *
     * @param totalPages the total pages
     * @return {@code this}
     */
    @JsonProperty("total_pages")
    @SuppressWarnings("unchecked")
    public final T withTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return (T) this;
    }

    /**
     * Returns the total results
     *
     * @return the total results
     */
    public final Integer getTotalResults() {
        return this.totalResults;
    }

    /**
     * Configure the total results
     *
     * @param totalResults the total results
     * @return {@code this}
     */
    @JsonProperty("total_results")
    @SuppressWarnings("unchecked")
    public final T withTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
        return (T) this;
    }

}
