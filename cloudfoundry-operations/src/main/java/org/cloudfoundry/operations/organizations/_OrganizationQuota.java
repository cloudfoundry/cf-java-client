/*
 * Copyright 2013-2020 the original author or authors.
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

import org.immutables.value.Value;

/**
 * A Cloud Foundry Quota
 */
@Value.Immutable
abstract class _OrganizationQuota {

    /**
     * The id
     */
    abstract String getId();

    /**
     * The instance memory limit
     */
    abstract Integer getInstanceMemoryLimit();

    /**
     * The name
     */
    abstract String getName();

    /**
     * The organization id
     */
    abstract String getOrganizationId();

    /**
     * Whether paid service plans are allowed
     */
    abstract Boolean getPaidServicePlans();

    /**
     * The total memory limit
     */
    abstract Integer getTotalMemoryLimit();

    /**
     * The total number of routes
     */
    abstract Integer getTotalRoutes();

    /**
     * The total number of service instances
     */
    abstract Integer getTotalServiceInstances();

}
