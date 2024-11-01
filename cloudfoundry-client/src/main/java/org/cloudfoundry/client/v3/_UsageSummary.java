/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Represents a summary of resource usage
 */
@JsonDeserialize
@Value.Immutable
abstract class _UsageSummary {

    /**
     * The total memory usage
     */
    @JsonProperty("memory_in_mb")
    abstract Integer getMemoryInMb();

    /**
     * The number of started instances
     */
    @JsonProperty("started_instances")
    abstract Integer getStartedInstances();

    /**
     * The number of routes
     */
    @JsonProperty("routes")
    @Nullable
    abstract Integer getRoutes();

        /**
     * The number of service instances
     */
    @JsonProperty("service_instances")
    @Nullable
    abstract Integer getServiceInstances();

        /**
     * The number of reserved ports
     */
    @JsonProperty("reserved_ports")
    @Nullable
    abstract Integer getReservedPorts();

             /**
     * The number of domains
     */
    @JsonProperty("domains")
    @Nullable
    abstract Integer getDomains();

            /**
     * The number of tasks per app
     */
    @JsonProperty("per_app_tasks")
    @Nullable
    abstract Integer getPerAppTasks();

            /**
     * The number of service keys
     */
    @JsonProperty("service_keys")
    @Nullable
    abstract Integer getServiceKeys();

}
