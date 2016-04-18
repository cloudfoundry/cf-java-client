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

package org.cloudfoundry.operations.organizationadmin;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request options for the set quota operation
 */
@Data
public final class SetQuotaRequest implements Validatable {

    /**
     * The name of the organization on which the quota must be set
     *
     * @param organizationName the name of the organization
     * @return the name of the organization
     */
    private final String organizationName;

    /**
     * The name of the quota that will be set
     *
     * @param quotaName the name of the quota
     * @return the name of the quota
     */
    private final String quotaName;

    @Builder
    SetQuotaRequest(String organizationName,
                    String quotaName) {
        this.organizationName = organizationName;
        this.quotaName = quotaName;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.organizationName == null) {
            builder.message("organization name must be specified");
        }

        if (this.quotaName == null) {
            builder.message("quota name must be specified");
        }

        return builder.build();
    }

}
