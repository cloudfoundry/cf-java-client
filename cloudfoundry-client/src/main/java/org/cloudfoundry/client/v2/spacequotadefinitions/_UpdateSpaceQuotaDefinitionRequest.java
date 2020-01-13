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

package org.cloudfoundry.client.v2.spacequotadefinitions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request payload for the Update a Space Quota Definition operation
 */
@JsonSerialize
@Value.Immutable
abstract class _UpdateSpaceQuotaDefinitionRequest {

    /**
     * How many app instances a space can create. (-1 represents an unlimited amount)
     */
    @JsonProperty("app_instance_limit")
    @Nullable
    abstract Integer getApplicationInstanceLimit();

    /**
     * The number of tasks that can be run per app. (-1 represents an unlimited amount)
     */
    @JsonProperty("app_task_limit")
    @Nullable
    abstract Integer getApplicationTaskLimit();

    /**
     * The maximum amount of memory in megabytes an application instance can have. (-1 represents an unlimited amount)
     */
    @JsonProperty("instance_memory_limit")
    @Nullable
    abstract Integer getInstanceMemoryLimit();

    /**
     * How much memory in megabytes a space can have
     */
    @JsonProperty("memory_limit")
    @Nullable
    abstract Integer getMemoryLimit();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * If a space can have non basic services
     */
    @JsonProperty("non_basic_services_allowed")
    @Nullable
    abstract Boolean getNonBasicServicesAllowed();

    /**
     * The owning organization of the space quota
     */
    @JsonProperty("organization_guid")
    @Nullable
    abstract String getOrganizationId();

    /**
     * The space quota definition id
     */
    @JsonIgnore
    abstract String getSpaceQuotaDefinitionId();

    /**
     * How many routes a space can have that use a reserved port. These routes count toward total_routes. (-1 represents an unlimited amount; subject to org quota)
     */
    @JsonProperty("total_reserved_route_ports")
    @Nullable
    abstract Integer getTotalReservedRoutePorts();

    /**
     * How many routes a space can have. (-1 represents an unlimited amount)
     */
    @JsonProperty("total_routes")
    @Nullable
    abstract Integer getTotalRoutes();

    /**
     * How many service keys an organization can have. (-1 represents an unlimited amount)
     */
    @JsonProperty("total_service_keys")
    @Nullable
    abstract Integer getTotalServiceKeys();

    /**
     * How many services a space can have. (-1 represents an unlimited amount)
     */
    @JsonProperty("total_services")
    @Nullable
    abstract Integer getTotalServices();

}
