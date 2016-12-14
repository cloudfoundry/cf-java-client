/*
 * Copyright 2013-2016 the original author or authors.
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
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Link;

import java.util.List;
import java.util.Map;

/**
 * Base class for responses that are processes
 */
public abstract class Process {

    /**
     * The command
     */
    @JsonProperty("command")
    @Nullable
    public abstract String getCommand();

    /**
     * The created at
     */
    @JsonProperty("created_at")
    @Nullable
    public abstract String getCreatedAt();

    /**
     * The disk in megabytes
     */
    @JsonProperty("disk_in_mb")
    @Nullable
    public abstract Integer getDiskInMb();

    /**
     * The health check
     */
    @JsonProperty("health_check")
    @Nullable
    public abstract HealthCheck getHealthCheck();

    /**
     * The id
     */
    @JsonProperty("guid")
    @Nullable
    public abstract String getId();

    /**
     * The instances
     */
    @JsonProperty("instances")
    @Nullable
    public abstract Integer getInstances();

    /**
     * The links
     */
    @AllowNulls
    @JsonProperty("links")
    @Nullable
    public abstract Map<String, Link> getLinks();

    /**
     * The memory in megabytes
     */
    @JsonProperty("memory_in_mb")
    @Nullable
    public abstract Integer getMemoryInMb();

    /**
     * The ports opened to the application
     */
    @JsonProperty("ports")
    @Nullable
    public abstract List<Integer> getPorts();

    /**
     * The type
     */
    @JsonProperty("type")
    @Nullable
    public abstract String getType();

    /**
     * The updated at
     */
    @JsonProperty("updated_at")
    @Nullable
    public abstract String getUpdatedAt();

}
