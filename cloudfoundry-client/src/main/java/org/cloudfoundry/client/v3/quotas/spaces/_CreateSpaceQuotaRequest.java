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

package org.cloudfoundry.client.v3.quotas.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.quotas.Apps;
import org.cloudfoundry.client.v3.quotas.Routes;
import org.cloudfoundry.client.v3.quotas.Services;
import org.immutables.value.Value;

/**
 * The request payload to Create a new Space Quota
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateSpaceQuotaRequest {

    /**
     * Name of the quota
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * A relationship to the organizations and spaces where the quota is applied
     */
    @JsonProperty("relationships")
    abstract SpaceQuotaRelationships getRelationships();

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

}
