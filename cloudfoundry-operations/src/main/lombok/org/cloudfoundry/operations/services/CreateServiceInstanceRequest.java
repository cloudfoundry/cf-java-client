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

package org.cloudfoundry.operations.services;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.List;
import java.util.Map;

/**
 * The request options for the create service instance operation
 */
@Data
public final class CreateServiceInstanceRequest implements Validatable {

    /**
     * The parameters of the service instance
     *
     * @param parameters the parameters
     * @return the parameters
     */
    private final Map<String, Object> parameters;

    /**
     * The service plan
     *
     * @param plan the service plan
     * @return the service plan
     */
    private final String plan;

    /**
     * The service
     *
     * @param service the service
     * @return the name of the service
     */
    private final String service;

    /**
     * The service instance to create
     *
     * @param serviceInstance the service instance
     * @return the service instance
     */
    private final String serviceInstance;

    /**
     * The tags
     *
     * @param tags the tags
     * @return the tags
     */
    private final List<String> tags;


    @Builder
    CreateServiceInstanceRequest(@Singular Map<String, Object> parameters,
                                 String plan,
                                 String service,
                                 String serviceInstance,
                                 @Singular List<String> tags) {
        this.parameters = parameters;
        this.plan = plan;
        this.service = service;
        this.serviceInstance = serviceInstance;
        this.tags = tags;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.plan == null) {
            builder.message("service plan must be specified");
        }

        if (this.service == null) {
            builder.message("service must be specified");
        }

        if (this.serviceInstance == null) {
            builder.message("service instance must be specified");
        }

        return builder.build();
    }

}
