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

package org.cloudfoundry.operations.applications;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request options for the copy source application operation
 */
@Data
public final class CopySourceApplicationRequest implements Validatable {

    /**
     * The name of the application
     *
     * @param name the name of the application
     * @return the name of the application
     */
    private final String name;

    /**
     * Whether to restart the target application
     *
     * @param restart whether to restart the target application
     * @return whether to restart the target application
     */
    private final Boolean restart;

    /**
     * The name of the target application
     *
     * @param targetName the name of the target application
     * @return the name of the target application
     */
    private final String targetName;

    /**
     * The organization of the target application
     *
     * @param targetOrganization the organization of the target application
     * @return the organization of the target application
     */
    private final String targetOrganization;

    /**
     * The space of the target application
     *
     * @param targetSpace the space of the target application
     * @return the space of the target application
     */
    private final String targetSpace;

    @Builder
    CopySourceApplicationRequest(String name, 
                                 Boolean restart,
                                 String targetName,
                                 String targetOrganization,
                                 String targetSpace) {
        this.name = name;
        this.restart = restart;
        this.targetName = targetName;
        this.targetOrganization = targetOrganization;
        this.targetSpace = targetSpace;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.targetName == null) {
            builder.message("target application name must be specified");
        }
        
        if (this.targetOrganization != null && this.targetSpace == null) {
            builder.message("target space must be specified with target organization");
        }

        return builder.build();
    }

}
