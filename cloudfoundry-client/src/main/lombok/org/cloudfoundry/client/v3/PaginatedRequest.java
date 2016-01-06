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

import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.ValidationResult;

/**
 * Base class for requests that are paginated
 */
@Data
public abstract class PaginatedRequest {

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
     * @param perPage the results per page
     * @return the results per page
     */
    @Getter(onMethod = @__(@QueryParameter("per_page")))
    private final Integer perPage;

    protected PaginatedRequest(Integer page, Integer perPage) {
        this.page = page;
        this.perPage = perPage;
    }

    /**
     * Returns whether a {@link PaginatedRequest} instance is valid
     *
     * @return a result indicating whether an instance is valid, and messages about what is wrong if it is invalid
     */
    protected final ValidationResult.ValidationResultBuilder isPaginatedRequestValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.page != null && this.page < 1) {
            builder.message("page must be greater than or equal to 1");
        }

        if (this.perPage != null && (this.perPage < 1 || this.perPage > 5_000)) {
            builder.message("perPage must be between 1 and 5000 inclusive");
        }

        return builder;
    }

}
