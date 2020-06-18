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

package org.cloudfoundry.operations.services;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.MaintenanceInfo;

import java.util.List;

/**
 * A service instance summary base class
 */
public abstract class AbstractServiceInstanceSummary {

    /**
     * The bound applications
     */
    abstract List<String> getApplications();

    /**
     * The service instance id
     */
    abstract String getId();

    /**
     * The last operation for the service
     */
    @Nullable
    abstract String getLastOperation();

    /**
     * The maintenance info
     */
    @Nullable
    abstract MaintenanceInfo getMaintenanceInfo();

    /**
     * The service instance name
     */
    abstract String getName();

    /**
     * The managed service plan
     */
    @Nullable
    abstract String getPlan();

    /**
     * The name of the managed service
     */
    @Nullable
    abstract String getService();

    /**
     * The tags
     */
    @Nullable
    abstract List<String> getTags();

    /**
     * The type of the service instance
     */
    abstract ServiceInstanceType getType();

}
