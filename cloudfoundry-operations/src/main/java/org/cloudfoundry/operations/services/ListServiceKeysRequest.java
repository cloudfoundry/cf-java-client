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
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

@Data
public class ListServiceKeysRequest implements Validatable {

    /**
     * The name of the service instance for which the keys will be listed 
     *
     * @param serviceInstanceName the service instance name for which the keys will be listed
     * @return service instance
     */
    private final String serviceInstanceName;

    @Builder
    ListServiceKeysRequest(String serviceInstanceName) {
        this.serviceInstanceName = serviceInstanceName;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if(serviceInstanceName == null) {
            builder.message("service instance name must be specified");
        }

        return builder.build();
    }
}
