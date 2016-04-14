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

package org.cloudfoundry.client.v2.privatedomains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the deprecated Create a Private Domain operation
 */
@Data
public final class CreatePrivateDomainRequest implements Validatable {

    /**
     * The domain name
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

    @Builder
    CreatePrivateDomainRequest(String name, String owningOrganizationId) {
        this.name = name;
        this.owningOrganizationId = owningOrganizationId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.owningOrganizationId == null) {
            builder.message("owning organization id must be specified");
        }

        return builder.build();
    }

}
