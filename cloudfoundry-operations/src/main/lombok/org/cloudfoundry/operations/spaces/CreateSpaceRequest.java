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

package org.cloudfoundry.operations.spaces;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request options for the delete space operation
 */
@Data
public final class CreateSpaceRequest implements Validatable {

    /**
     * The name of the new space
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The organization of the new space
     *
     * @param organization the organization
     * @return the organization
     */
    private final String organization;

    /**
     * The space quota definition of the new space
     *
     * @param spaceQuota the space quota definition
     * @return the space quota definition
     */
    private final String spaceQuota;

    @Builder
    CreateSpaceRequest(String name,
                       String organization,
                       String spaceQuota) {
        this.name = name;
        this.organization = organization;
        this.spaceQuota = spaceQuota;
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
