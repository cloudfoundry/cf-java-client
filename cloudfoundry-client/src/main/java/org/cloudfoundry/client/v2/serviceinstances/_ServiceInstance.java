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

package org.cloudfoundry.client.v2.serviceinstances;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.MaintenanceInfo;
import org.immutables.value.Value;

import java.util.List;

/**
 * A service instance
 */
@JsonDeserialize
@Value.Immutable
abstract class _ServiceInstance {

    /**
     * The bound application count
     */
    @JsonProperty("bound_app_count")
    @Nullable
    abstract Integer getBoundApplicationCount();

    /**
     * The dashboard url
     */
    @JsonProperty("dashboard_url")
    @Nullable
    abstract String getDashboardUrl();

    /**
     * The id
     */
    @JsonProperty("guid")
    @Nullable
    abstract String getId();

    /**
     * The last operation
     */
    @JsonProperty("last_operation")
    @Nullable
    abstract LastOperation getLastOperation();

    /**
     * The maintenance info
     */
    @JsonProperty("maintenance_info")
    @Nullable
    abstract MaintenanceInfo getMaintenanceInfo();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The service broker name
     */
    @JsonProperty("service_broker_name")
    @Nullable
    abstract String getServiceBrokerName();

    /**
     * The service plan
     */
    @JsonProperty("service_plan")
    @Nullable
    abstract Plan getServicePlan();

    /**
     * The shared from
     */
    @JsonProperty("shared_from")
    @Nullable
    abstract Share getSharedFrom();

    /**
     * The shared to
     */
    @AllowNulls
    @JsonProperty("shared_to")
    @Nullable
    abstract List<Share> getSharedTo();

    /**
     * The type
     */
    @JsonProperty("type")
    @Nullable
    abstract String getType();

}
