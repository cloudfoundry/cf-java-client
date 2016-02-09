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

package org.cloudfoundry.operations.routes;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request options for the map route operation
 */
@Data
public final class DeleteRouteRequest implements Validatable {

    /**
     * The domain of the route
     *
     * @param domain the domain of the route
     * @return the domain of the route
     */
    private final String domain;

    /**
     * The host of the route
     *
     * @param host the host of the route
     * @return the host of the route
     */
    private final String host;

    /**
     * The path of the route.
     *
     * Note: the path must be specified without a leading "/"
     *
     * @param path the path of the route
     * @return the path of the route
     */
    private final String path;

    @Builder
    DeleteRouteRequest(String domain, String host, String path) {
        this.domain = domain;
        this.host = host;
        this.path = path;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.domain == null) {
            builder.message("domain must be specified");
        }

        return builder.build();
    }

}
