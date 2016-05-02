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
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Process details statistics
 */
@Data
public abstract class AbstractProcessDetailedStatistics {

    protected final long diskQuota;

    protected final long fdsQuota;

    protected final String host;

    protected final int index;

    protected final List<PortMapping> instancePorts;

    protected final long memoryQuota;

    protected final String state;

    protected final String type;

    protected final long uptime;

    protected final ProcessUsage usage;

    protected AbstractProcessDetailedStatistics(@JsonProperty("disk_quota") Long diskQuota,
                                                @JsonProperty("fds_quota") Integer fdsQuota,
                                                @JsonProperty("host") String host,
                                                @JsonProperty("index") Integer index,
                                                @JsonProperty("instance_ports") @Singular List<PortMapping> instancePorts,
                                                @JsonProperty("mem_quota") Long memoryQuota,
                                                @JsonProperty("state") String state,
                                                @JsonProperty("type") String type,
                                                @JsonProperty("uptime") Long uptime,
                                                @JsonProperty("usage") ProcessUsage usage) {
        this.diskQuota = diskQuota;
        this.fdsQuota = fdsQuota;
        this.host = host;
        this.index = index;
        this.instancePorts = instancePorts;
        this.memoryQuota = memoryQuota;
        this.state = state;
        this.type = type;
        this.usage = usage;
        this.uptime = uptime;
    }

    @Data
    public static final class PortMapping {

        private final Integer external;

        private final Integer internal;

        @Builder
        PortMapping(@JsonProperty("external") Integer external,
                    @JsonProperty("internal") Integer internal) {

            this.external = external;
            this.internal = internal;
        }

    }

}

