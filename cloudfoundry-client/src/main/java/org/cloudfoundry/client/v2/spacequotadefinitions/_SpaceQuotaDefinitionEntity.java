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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The entity response payload for the Space Quota Definition resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _SpaceQuotaDefinitionEntity {

    /**
     * The application instance limit
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
     * The instance memory limit
     */
    @JsonProperty("instance_memory_limit")
    @Nullable
    abstract Integer getInstanceMemoryLimit();

    /**
     * The memory limit
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
     * The non basic services allowed
     */
    @JsonProperty("non_basic_services_allowed")
    @Nullable
    abstract Boolean getNonBasicServicesAllowed();

    /**
     * The organization id
     */
    @JsonProperty("organization_guid")
    @Nullable
    abstract String getOrganizationId();

    /**
     * The organization url
     */
    @JsonProperty("organization_url")
    @Nullable
    abstract String getOrganizationUrl();

    /**
     * The spaces url
     */
    @JsonProperty("spaces_url")
    @Nullable
    abstract String getSpacesUrl();

    /**
     * How many routes a space can have that use a reserved port. These routes count toward total_routes. (-1 represents an unlimited amount; subject to org quota)
     */
    @JsonProperty("total_reserved_route_ports")
    @Nullable
    abstract Integer getTotalReservedRoutePorts();

    /**
     * The total routes
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
     * The total services
     */
    @JsonProperty("total_services")
    @Nullable
    abstract Integer getTotalServices();

}
