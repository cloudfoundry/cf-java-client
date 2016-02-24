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

package org.cloudfoundry.operations.organizations;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request options for the create org operation
 */
@Data
public final class CreateOrganizationRequest implements Validatable {

    /**
     * The organization name
     *
     * @param organizationName the organization name
     * @return the organization name
     */
    private final String organizationName;

    /**
     * The quota definition name
     *
     * @param quotaDefinitionName the quota definition name
     * @return the quota definition name
     */
    private final String quotaDefinitionName;

    @Builder
    CreateOrganizationRequest(String organizationName, String quotaDefinitionName) {
        this.organizationName = organizationName;
        this.quotaDefinitionName = quotaDefinitionName;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.organizationName == null) {
            builder.message("organization name must be specified");
        }

        return builder.build();
    }

}
