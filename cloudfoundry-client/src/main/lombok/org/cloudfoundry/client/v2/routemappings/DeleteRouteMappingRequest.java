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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * Request payload object for deleting the route mapping
 */
@Data
public final class DeleteRouteMappingRequest implements Validatable {

    /**
     * The route mapping id
     *
     * @param routeMappingId the route mapping id
     * @return the route mapping id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String routeMappingId;

    /**
     * The async parameter
     *
     * @param async the async parameter
     * @return the async parameter
     */
    @Getter(onMethod = @__(@QueryParameter("async")))
    private final Boolean async;

    @Builder
    DeleteRouteMappingRequest(String routeMappingId, Boolean async) {
        this.routeMappingId = routeMappingId;
        this.async = async;
    }


    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (routeMappingId == null) {
            builder.message("route mapping id must be specified");
        }
        return builder.build();
    }
}