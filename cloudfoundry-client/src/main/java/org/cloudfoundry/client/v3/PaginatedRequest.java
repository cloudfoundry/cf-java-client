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

import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.ValidationResult;

/**
 * Base class for requests that are paginated
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <T> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 */
public abstract class PaginatedRequest<T extends PaginatedRequest<T>> {

    private volatile Integer page;

    private volatile Integer perPage;

    /**
     * Returns the page
     *
     * @return the page
     */
    @QueryParameter("page")
    public final Integer getPage() {
        return this.page;
    }

    /**
     * Configure the page
     *
     * @param page the page
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withPage(Integer page) {
        this.page = page;
        return (T) this;
    }

    /**
     * Returns the per page
     *
     * @return the per page
     */
    @QueryParameter("per_page")
    public final Integer getPerPage() {
        return this.perPage;
    }

    /**
     * Configure the per page
     *
     * @param perPage the per page
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withPerPage(Integer perPage) {
        this.perPage = perPage;
        return (T) this;
    }

    /**
     * Returns whether a {@link PaginatedRequest} instance is valid
     *
     * @return a result indicating whether an instance is valid, and messages about what is wrong if it is invalid
     */
    protected final ValidationResult isPaginatedRequestValid() {
        ValidationResult result = new ValidationResult();

        if (this.page != null && this.page < 1) {
            result.invalid("page must be greater than or equal to 1");
        }

        if (this.perPage != null && (this.perPage < 1 || this.perPage > 5_000)) {
            result.invalid("perPage must be between 1 and 5000 inclusive");
        }

        return result;
    }

}
