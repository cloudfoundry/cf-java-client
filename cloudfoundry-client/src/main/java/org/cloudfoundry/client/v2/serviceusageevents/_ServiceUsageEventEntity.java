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

package org.cloudfoundry.client.v2.serviceusageevents;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The entity response payload for Service Usage Events
 */
@JsonDeserialize
@Value.Immutable
abstract class _ServiceUsageEventEntity {

    /**
     * The organization id
     */
    @JsonProperty("org_guid")
    @Nullable
    abstract String getOrganizationId();

    /**
     * The service broker id
     */
    @JsonProperty("service_broker_guid")
    @Nullable
    abstract String getServiceBrokerId();

    /**
     * The service broker name
     */
    @JsonProperty("service_broker_name")
    @Nullable
    abstract String getServiceBrokerName();

    /**
     * The service id
     */
    @JsonProperty("service_guid")
    @Nullable
    abstract String getServiceId();

    /**
     * The service instance id
     */
    @JsonProperty("service_instance_guid")
    @Nullable
    abstract String getServiceInstanceId();

    /**
     * The service instance name
     */
    @JsonProperty("service_instance_name")
    @Nullable
    abstract String getServiceInstanceName();

    /**
     * The service instance type
     */
    @JsonProperty("service_instance_type")
    @Nullable
    abstract String getServiceInstanceType();

    /**
     * The service label
     */
    @JsonProperty("service_label")
    @Nullable
    abstract String getServiceLabel();

    /**
     * The service plan id
     */
    @JsonProperty("service_plan_guid")
    @Nullable
    abstract String getServicePlanId();

    /**
     * The service plan name
     */
    @JsonProperty("service_plan_name")
    @Nullable
    abstract String getServicePlanName();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    @Nullable
    abstract String getSpaceId();

    /**
     * The space name
     */
    @JsonProperty("space_name")
    @Nullable
    abstract String getSpaceName();

    /**
     * The state
     */
    @JsonProperty("state")
    @Nullable
    abstract String getState();

}
