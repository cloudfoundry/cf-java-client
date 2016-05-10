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

/**
 * The request options for the deleting the service key
 */
@Data
public final class DeleteServiceKeyRequest implements Validatable {

    /**
     * The name of the service instance
     *
     * @param serviceInstanceName the name of the service instance
     * @return the name of the service instance
     */
    private final String serviceInstanceName;

    /**
     * The name of the service key to delete
     *
     * @param serviceKeyName the name of the service key to delete
     * @return the name of the service key to delete
     */
    private final String serviceKeyName;

    @Builder
    DeleteServiceKeyRequest(String serviceInstanceName, String serviceKeyName) {
        this.serviceInstanceName = serviceInstanceName;
        this.serviceKeyName = serviceKeyName;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (serviceInstanceName == null) {
            builder.message("service instance must be specified");
        }

        if (serviceKeyName == null) {
            builder.message("service key must be specified");
        }

        return builder.build();
    }

}
