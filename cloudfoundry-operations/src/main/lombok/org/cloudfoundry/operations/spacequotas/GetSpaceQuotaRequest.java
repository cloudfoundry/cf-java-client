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

package org.cloudfoundry.operations.spacequotas;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request options for the list routes operation
 */
@Data
public final class GetSpaceQuotaRequest implements Validatable {

    /**
     * The name of the space quota to get
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    @Builder
    GetSpaceQuotaRequest(String name) {
        this.name = name;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("space quota name must be specified");
        }

        return builder.build();
    }

}
