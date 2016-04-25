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

import java.util.Map;

@Data
public class CreateServiceKeyRequest implements Validatable {

    /**
     * The name of the service instance
     *
     * @param serviceInstanceName the name of the service instance
     * @return the name of the service instance
     */
    private final String serviceInstanceName;

    /**
     * The name of the service key to create
     *
     * @param serviceKeyName the name of the service key
     * @return the name of the service key
     */
    private final String serviceKeyName;

    /**
     * The parameters of the service key
     *
     * @param parameters the parameters
     * @return the parameters
     */
    private final Map<String, Object> parameters;

    @Builder
    CreateServiceKeyRequest(String serviceInstanceName, String serviceKeyName, @Singular Map<String, Object> parameters) {
        this.serviceInstanceName = serviceInstanceName;
        this.serviceKeyName = serviceKeyName;
        this.parameters = parameters;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.serviceInstanceName == null) {
            builder.message("service instance name must be specified");
        }

        if (this.serviceKeyName == null) {
            builder.message("service key must be specified");
        }

        return builder.build();
    }

}
