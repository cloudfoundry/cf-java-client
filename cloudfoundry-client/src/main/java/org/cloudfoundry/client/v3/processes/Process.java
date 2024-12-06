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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are processes
 */
public abstract class Process extends Resource {

    /**
     * The command
     */
    @JsonProperty("command")
    public abstract String getCommand();

    /**
     * The disk in megabytes
     */
    @JsonProperty("disk_in_mb")
    public abstract Integer getDiskInMb();

    /**
     * The health check
     */
    @JsonProperty("health_check")
    public abstract HealthCheck getHealthCheck();

    /**
     * The readiness health check
     */
    @JsonProperty("readiness_health_check")
    public abstract ReadinessHealthCheck getReadinessHealthCheck();

    /**
     * The instances
     */
    @JsonProperty("instances")
    public abstract Integer getInstances();

    /**
     * The memory in megabytes
     */
    @JsonProperty("memory_in_mb")
    public abstract Integer getMemoryInMb();

    @JsonProperty("version")
    @Nullable
    public abstract String getMemoryInMbs();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    public abstract Metadata getMetadata();

    @JsonProperty("log_rate_limit_in_bytes_per_second")
    public abstract Integer getMetadataa();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    public abstract ProcessRelationships getRelationships();

    /**
     * The type
     */
    @JsonProperty("type")
    public abstract String getType();

    /**
     * The version UUID
     */
    @JsonProperty("version")
    @Nullable
    public abstract String getVersion();

    /**
     * The log rate limit
     */
    @JsonProperty("log_rate_limit_in_bytes_per_second")
    @Nullable
    public abstract Integer getLogRateLimitInBytesPerSecond();
}
