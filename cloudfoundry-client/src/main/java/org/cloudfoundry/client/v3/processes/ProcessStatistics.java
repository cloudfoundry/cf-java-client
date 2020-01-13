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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

import java.util.List;

/**
 * Process details statistics
 */
public abstract class ProcessStatistics {

    /**
     * Information about errors placing the instance
     */
    @JsonProperty("details")
    @Nullable
    public abstract String getDetails();

    /**
     * The disk quota
     */
    @JsonProperty("disk_quota")
    public abstract Integer getDiskQuota();

    /**
     * The file descriptor quota
     */
    @JsonProperty("fds_quota")
    public abstract Integer getFileDescriptorQuota();

    /**
     * The host
     */
    @JsonProperty("host")
    public abstract String getHost();

    /**
     * The index
     */
    @JsonProperty("index")
    public abstract Integer getIndex();

    /**
     * The instance port mappings
     */
    @JsonProperty("instance_ports")
    public abstract List<PortMapping> getInstancePorts();

    /**
     * The isolation segment
     */
    @JsonProperty("isolation_segment")
    @Nullable
    public abstract String getIsolationSegment();

    /**
     * The memory quota
     */
    @JsonProperty("mem_quota")
    public abstract Integer getMemoryQuota();

    /**
     * The state
     */
    @JsonProperty("state")
    @Nullable
    public abstract ProcessState getState();

    /**
     * The type
     */
    @JsonProperty("type")
    public abstract String getType();

    /**
     * The uptime
     */
    @JsonProperty("uptime")
    public abstract Integer getUptime();

    /**
     * The usage
     */
    @JsonProperty("usage")
    public abstract ProcessUsage getUsage();

}

