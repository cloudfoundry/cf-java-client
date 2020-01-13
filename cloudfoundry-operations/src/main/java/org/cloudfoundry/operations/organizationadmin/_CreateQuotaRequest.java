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

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request options for the create quota operation
 */
@Value.Immutable
abstract class _CreateQuotaRequest {

    /**
     * Can provision instances of paid service plans
     */
    @Nullable
    abstract Boolean getAllowPaidServicePlans();

    /**
     * Total number of application instances
     */
    @Nullable
    abstract Integer getApplicationInstanceLimit();

    /**
     * The application instance memory limit in mb
     */
    @Nullable
    abstract Integer getInstanceMemoryLimit();

    /**
     * Total amount of memory a space can have in mb
     */
    @Nullable
    abstract Integer getMemoryLimit();

    /**
     * The name
     */
    abstract String getName();

    /**
     * Maximum number of routes that may be created with reserved ports
     */
    @Nullable
    abstract Integer getTotalReservedRoutePorts();

    /**
     * The total number of routes
     */
    @Nullable
    abstract Integer getTotalRoutes();

    /**
     * Total number of service instances
     */
    @Nullable
    abstract Integer getTotalServices();

}
