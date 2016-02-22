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

package org.cloudfoundry.client.v2.routemappings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Creating a Route Mapping operation
 */
@Data
public final class CreateRouteMappingRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    @Getter(onMethod = @__(@JsonProperty("app_guid")))
    private final String applicationId;

    /**
     * The application port on which the application should listen, and to which requests for the mapped route will be routed.
     *
     * @param applicationPort the application port
     * @return the application port
     */
    @Getter(onMethod = @__(@JsonProperty("app_port")))
    private final Integer applicationPort;

    /**
     * The route id
     *
     * @param routeId the route id
     * @return route id
     */
    @Getter(onMethod = @__(@JsonProperty("route_guid")))
    private final String routeId;

    @Builder
    CreateRouteMappingRequest(String applicationId, Integer applicationPort, String routeId) {
        this.applicationId = applicationId;
        this.applicationPort = applicationPort;
        this.routeId = routeId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        if (this.routeId == null) {
            builder.message("route id must be specified");
        }

        return builder.build();
    }

}
