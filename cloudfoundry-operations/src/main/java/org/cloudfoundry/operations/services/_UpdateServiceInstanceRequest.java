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

import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.MaintenanceInfo;
import org.immutables.value.Value;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * The request options for the update service instance operation
 */
@Value.Immutable
abstract class _UpdateServiceInstanceRequest {

    /**
     * How long to wait for update
     */
    @Value.Default
    Duration getCompletionTimeout() {
        return Duration.ofMinutes(5);
    }

    /**
     * The maintenance info to upgrade to
     */
    @Nullable
    abstract MaintenanceInfo getMaintenanceInfo();

    /**
     * The parameters of the service instance
     */
    @AllowNulls
    @Nullable
    abstract Map<String, Object> getParameters();

    /**
     * The name of the required plan
     */
    @Nullable
    abstract String getPlanName();

    /**
     * The name of the service instance
     */
    abstract String getServiceInstanceName();

    /**
     * The tags
     */
    @Nullable
    abstract List<String> getTags();

}
