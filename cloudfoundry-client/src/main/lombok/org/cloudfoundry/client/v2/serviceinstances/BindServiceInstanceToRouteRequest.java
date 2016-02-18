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

package org.cloudfoundry.client.v2.serviceinstances;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload to Bind Service Instance To a Route
 */
public final class BindServiceInstanceToRouteRequest implements Validatable {

    /**
     * Key/value pairs of all arbitrary parameters to pass along to the service broker
     *
     * @return the arbitrary parameters to pass along to the service broker
     */
    @Getter(onMethod = @__({@JsonProperty("parameters"), @JsonInclude(NON_EMPTY)}))
    private final Map<String, Object> parameters;

    /**
     * The route id
     *
     * @param routeId the route id
     * @return the route id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String routeId;

    /**
     * The service instance id
     *
     * @param serviceInstanceId the service instance id
     * @return the service instance id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String serviceInstanceId;

    @Builder
    BindServiceInstanceToRouteRequest(@Singular Map<String, Object> parameters,
                                      String routeId,
                                      String serviceInstanceId) {
        this.parameters = parameters;
        this.routeId = routeId;
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.serviceInstanceId == null) {
            builder.message("service instance id must be specified");
        }

        if (this.routeId == null) {
            builder.message("route id must be specified");
        }

        return builder.build();
    }

}
