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

package org.cloudfoundry.client.v3.serviceplans;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

import java.util.List;

/**
 * Base class for responses that are Service Plans
 */
public abstract class ServicePlan extends Resource {

    /**
     * Whether or not the service plan is available
     */
    @JsonProperty("available")
    public abstract Boolean getAvailable();

    /**
     * Information obtained from the service broker catalog
     */
    @JsonProperty("broker_catalog")
    public abstract BrokerCatalog getBrokerCatalog();

    /**
     * The cost of the service plan as obtained from the service broker catalog
     */
    @JsonProperty("costs")
    public abstract List<Cost> getCosts();

    /**
     * Description of the service plan
     */
    @JsonProperty("description")
    public abstract String getDescription();

    /**
     * Whether or not the service plan is free of charge
     */
    @JsonProperty("free")
    public abstract Boolean getFree();

    /**
     * Information about the version of this service plan
     */
    @JsonProperty("maintenance_info")
    @Nullable
    public abstract MaintenanceInfo getMaintenanceInfo();

    /**
     * The metadata
     */
    @AllowNulls
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The name of the service plan
     */
    @JsonProperty("name")
    public abstract String getName();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    public abstract ServicePlanRelationships getRelationships();

    /**
     * Schema definitions for service instances and service bindings for the service plan
     */
    @JsonProperty("schemas")
    public abstract Schemas getSchemas();

    /**
     * Denotes the visibility of the plan
     */
    @JsonProperty("visibility_type")
    public abstract Visibility getVisibilityType();

}
