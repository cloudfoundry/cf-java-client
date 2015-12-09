/*
 * Copyright 2015 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Associate Manager with the Organization operation
 */
@Data
public final class AssociateOrganizationManagerRequest implements Validatable {

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private volatile String id;

    /**
     * The manager id
     *
     * @param managerId the manager id
     * @return the manager id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private volatile String managerId;

    @Builder
    AssociateOrganizationManagerRequest(String id, String managerId) {
        this.id = id;
        this.managerId = managerId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        if (this.managerId == null) {
            builder.message("manager id must be specified");
        }

        return builder.build();
    }

}
