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

package org.cloudfoundry.operations.organizationadmin;

import org.immutables.value.Value;

/**
 * A Cloud Foundry Quota
 */
@Value.Immutable
abstract class _OrganizationQuota {

    /**
     * The allow paid service plans flag
     */
    abstract Boolean getAllowPaidServicePlans();

    /**
     * The application instance limit
     */
    abstract Integer getApplicationInstanceLimit();

    /**
     * The id
     */
    abstract String getId();

    /**
     * The instance memory limit
     */
    abstract Integer getInstanceMemoryLimit();

    /**
     * The memory limit
     */
    abstract Integer getMemoryLimit();

    /**
     * The name
     */
    abstract String getName();

    /**
     * Maximum number of routes that may be created with reserved ports
     */
    abstract Integer getTotalReservedRoutePorts();

    /**
     * The total routes
     */
    abstract Integer getTotalRoutes();

    /**
     * The total services
     */
    abstract Integer getTotalServices();

}
