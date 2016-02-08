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
import lombok.Data;

/**
 * Process details statistics
 */
@Data
public abstract class AbstractProcessDetailedStatistics {

    protected final long diskQuota;

    protected final long fdsQuota;

    protected final String host;

    protected final int index;

    protected final long memQuota;

    protected final int port;

    protected final String state;

    protected final String type;

    protected final long uptime;

    protected final ProcessUsage usage;

    protected AbstractProcessDetailedStatistics(@JsonProperty("index") int index,
                                                @JsonProperty("mem_quota") long memQuota,
                                                @JsonProperty("disk_quota") long diskQuota,
                                                @JsonProperty("fds_quota") long fdsQuota,
                                                @JsonProperty("usage") ProcessUsage usage,
                                                @JsonProperty("port") int port,
                                                @JsonProperty("uptime") long uptime,
                                                @JsonProperty("host") String host,
                                                @JsonProperty("type") String type,
                                                @JsonProperty("state") String state) {
        this.index = index;
        this.memQuota = memQuota;
        this.diskQuota = diskQuota;
        this.fdsQuota = fdsQuota;
        this.usage = usage;
        this.port = port;
        this.uptime = uptime;
        this.host = host;
        this.type = type;
        this.state = state;
    }
}
