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
 * The request options for the get quota operation
 */
@Data
public final class CreateQuotaRequest implements Validatable {

    /**
     * The allow paid service plans flag
     *
     * @param allowPaidServicePlans the allow paid service plans flag
     * @return the allow paid service plans flag
     */
    private final Boolean allowPaidServicePlans;

    /**
     * The instance memory limit
     *
     * @param instanceMemoryLimit the instance memory limit
     * @return instanceMemoryLimit
     */
    private final Integer instanceMemoryLimit;

    /**
     * The memory limit
     *
     * @param memoryLimit the memory limit
     * @return memory limit
     */
    private final Integer memoryLimit;

    /**
     * The name
     *
     * @param name the name
     * @return name
     */
    private final String name;

    /**
     * The total routes
     *
     * @param totalRoutes the total routes
     * @return the total routes
     */
    private final Integer totalRoutes;

    /**
     * The total services
     *
     * @param totalServices the total services
     * @return the total services
     */
    private final Integer totalServices;

    @Builder
    CreateQuotaRequest(Boolean allowPaidServicePlans,
                       Integer instanceMemoryLimit,
                       Integer memoryLimit,
                       String name,
                       Integer totalRoutes,
                       Integer totalServices) {
        this.allowPaidServicePlans = allowPaidServicePlans;
        this.instanceMemoryLimit = instanceMemoryLimit;
        this.memoryLimit = memoryLimit;
        this.name = name;
        this.totalRoutes = totalRoutes;
        this.totalServices = totalServices;
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
