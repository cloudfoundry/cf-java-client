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

package org.cloudfoundry.client.v3.serviceinstances;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.LastOperation;
import org.cloudfoundry.client.v3.MaintenanceInfo;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

import java.util.List;

/**
 * Base class for responses that are service instances
 */
public abstract class ServiceInstance extends Resource {

    /**
     * The dashboard url
     */
    @JsonProperty("dashboard_url")
    @Nullable
    public abstract String getDashboardUrl();

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
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The name
     */
    @JsonProperty("name")
    public abstract String getName();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    @Nullable
    public abstract ServiceInstanceRelationships getRelationships();

    /**
     * The route service url
     */
    @JsonProperty("route_service_url")
    @Nullable
    public abstract String getRouteServiceUrl();

    /**
     * The syslog drain url
     */
    @JsonProperty("syslog_drain_url")
    @Nullable
    public abstract String getSyslogDrainUrl();

    /**
     * A list of tags for the service instance
     */
    @JsonProperty("tags")
    @Nullable
    public abstract List<String> getTags();

    /**
     * The type of the service instance
     */
    @JsonProperty("type")
    @Nullable
    public abstract ServiceInstanceType getType();

    /**
     * Whether or not an upgrade of this service instance is available on the current Service Plan
     */
    @JsonProperty("upgrade_available")
    @Nullable
    public abstract Boolean getUpdateAvailable();

}
