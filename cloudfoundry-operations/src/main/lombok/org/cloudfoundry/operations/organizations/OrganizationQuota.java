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

/**
 * A Cloud Foundry Quota
 */
@Data
public final class OrganizationQuota {

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The instance memory limit
     *
     * @param instanceMemoryLimit the instance memory limit
     * @return the instance memory limit
     */
    private final Integer instanceMemoryLimit;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The organization id
     *
     * @param organizationId the organization id
     * @return the organization id
     */
    private final String organizationId;

    /**
     * Whether paid service plans are allowed
     *
     * @param paidServicePlans whether paid service plans are allowed
     * @return whether paid service plans are allowed
     */
    private final Boolean paidServicePlans;

    /**
     * The total memory limit
     *
     * @param totalMemoryLimit the total memory limit
     * @return the total memory limit
     */
    private final Integer totalMemoryLimit;

    /**
     * The total number of routes
     *
     * @param totalRoutes the total number of routes
     * @return the total number of routes
     */
    private final Integer totalRoutes;

    /**
     * The total number of service instances
     *
     * @param totalServiceInstances the total number of service instances
     * @return the total number of service instances
     */
    private final Integer totalServiceInstances;

    @Builder
    private OrganizationQuota(String id,
                              Integer instanceMemoryLimit,
                              String name,
                              String organizationId,
                              Boolean paidServicePlans,
                              Integer totalMemoryLimit,
                              Integer totalRoutes,
                              Integer totalServiceInstances) {
        this.id = id;
        this.instanceMemoryLimit = instanceMemoryLimit;
        this.name = name;
        this.organizationId = organizationId;
        this.paidServicePlans = paidServicePlans;
        this.totalMemoryLimit = totalMemoryLimit;
        this.totalRoutes = totalRoutes;
        this.totalServiceInstances = totalServiceInstances;
    }

}
