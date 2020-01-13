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

package org.cloudfoundry.client.v2.serviceplans;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.MaintenanceInfo;
import org.immutables.value.Value;

/**
 * The entity response payload for Service Plans
 */
@JsonDeserialize
@Value.Immutable
abstract class _ServicePlanEntity {

    /**
     * The active flag
     */
    @JsonProperty("active")
    @Nullable
    abstract Boolean getActive();

    /**
     * The bindable flag
     */
    @JsonProperty("bindable")
    @Nullable
    abstract Boolean getBindable();

    /**
     * The description
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The extra (A JSON string with additional data about the plan)
     */
    @JsonProperty("extra")
    @Nullable
    abstract String getExtra();

    /**
     * The free flag
     */
    @JsonProperty("free")
    @Nullable
    abstract Boolean getFree();

    /**
     * The plan maintenance info
     */
    @JsonProperty("maintenance_info")
    @Nullable
    abstract MaintenanceInfo getMaintenanceInfo();

    /**
     * The maximum polling duration
     */
    @JsonProperty("maximum_polling_duration")
    @Nullable
    abstract Long getMaximumPollingDuration();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * Whether the plan is updatable
     */
    @JsonProperty("plan_updateable")
    @Nullable
    abstract Boolean getPlanUpdatable();

    /**
     * The public flag
     */
    @JsonProperty("public")
    @Nullable
    abstract Boolean getPubliclyVisible();

    /**
     * The schemas
     */
    @JsonProperty("schemas")
    @Nullable
    abstract Schemas getSchemas();

    /**
     * The service id
     */
    @JsonProperty("service_guid")
    @Nullable
    abstract String getServiceId();

    /**
     * The service instances url
     */
    @JsonProperty("service_instances_url")
    @Nullable
    abstract String getServiceInstancesUrl();

    /**
     * The service url
     */
    @JsonProperty("service_url")
    @Nullable
    abstract String getServiceUrl();

    /**
     * The unique id in the service broker
     */
    @JsonProperty("unique_id")
    @Nullable
    abstract String getUniqueId();

}
