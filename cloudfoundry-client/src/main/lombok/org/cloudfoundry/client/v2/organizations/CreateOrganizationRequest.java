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

package org.cloudfoundry.client.v2.organizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the Creating an Organization operation
 */
@Data
public final class CreateOrganizationRequest implements Validatable {

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The quota definition id
     *
     * @param quotaDefinitionId the quota definition id
     * @return the quota definition id
     */
    @Getter(onMethod = @__(@JsonProperty("quota_definition_guid")))
    private final String quotaDefinitionId;

    /**
     * The status
     *
     * @param status the status
     * @return the status
     */
    @Getter(onMethod = @__(@JsonProperty("status")))
    private final String status;

    @Builder
    CreateOrganizationRequest(String name, String quotaDefinitionId, String status) {
        this.name = name;
        this.quotaDefinitionId = quotaDefinitionId;
        this.status = status;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        return builder.build();
    }

}
