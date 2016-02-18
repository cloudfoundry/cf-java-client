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

package org.cloudfoundry.client.v2.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the deprecated Create a Domain operation
 */
@Data
public final class CreateDomainRequest implements Validatable {

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The owning organization id
     *
     * @param owningOrganizationId the owning organization id
     * @return the owning organization id
     */
    @Getter(onMethod = @__(@JsonProperty("owning_organization_guid")))
    private final String owningOrganizationId;

    /**
     * The wildcard
     *
     * @param wildcard the wildcard
     * @return the wildcard
     */
    @Getter(onMethod = @__(@JsonProperty("wildcard")))
    private final Boolean wildcard;

    @Builder
    CreateDomainRequest(String name, String owningOrganizationId, Boolean wildcard) {
        this.name = name;
        this.owningOrganizationId = owningOrganizationId;
        this.wildcard = wildcard;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.wildcard == null) {
            builder.message("wildcard must be specified");
        }

        return builder.build();
    }

}
