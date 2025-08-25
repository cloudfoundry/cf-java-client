/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.organizationquotadefinitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are organization quota definitions
 */
public abstract class OrganizationQuotaDefinition extends Resource {

    /**
     * Name of the quota
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * Quotas that affect applications and application sub-resources
     */
    @JsonProperty("apps")
    @Nullable
    abstract Apps getApps();

    /**
     * Quotas that affect services
     */
    @JsonProperty("services")
    @Nullable
    abstract Services getServices();

    /**
     * Quotas that affect routes
     */
    @JsonProperty("routes")
    @Nullable
    abstract Routes getRoutes();

    /**
     * Quotas that affect domains
     */
    @JsonProperty("domains")
    @Nullable
    abstract Domains getDomains();

    /**
     * A relationship to the organizations where the quota is applied
     */
    @JsonProperty("relationships")
    @Nullable
    abstract OrganizationQuotaDefinitionRelationships getRelationships();
}
