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
import org.cloudfoundry.operations.Validatable;
import org.cloudfoundry.operations.ValidationResult;

/**
 * The request options for the scale application operation
 */
@Data
public final class ScaleApplicationRequest implements Validatable {

    /**
     * The disk limit in MB
     *
     * @param diskLimit the disk limit in MB
     * @return the disk limit in MB
     */
    private final Integer diskLimit;

    /**
     * The number of instances
     *
     * @param instances the number of instances
     * @return the number of instances
     */
    private final Integer instances;

    /**
     * The memory limit in MB
     *
     * @param memoryLimit the memory limit in MB
     * @return the memory limit in MB
     */
    private final Integer memoryLimit;

    /**
     * The name of the application
     *
     * @param name the name of the application
     * @return the name of the application
     */
    private final String name;

    @Builder
    ScaleApplicationRequest(Integer diskLimit,
                            Integer instances,
                            Integer memoryLimit,
                            String name) {
        this.diskLimit = diskLimit;
        this.instances = instances;
        this.memoryLimit = memoryLimit;
        this.name = name;
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
