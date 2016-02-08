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
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.PaginatedResponse;

import java.util.List;

/**
 * The response payload for the Get Detailed Stats for a Process operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class GetProcessDetailedStatisticsResponse extends PaginatedResponse<GetProcessDetailedStatisticsResponse.Resource> {

    @Builder
    GetProcessDetailedStatisticsResponse(@JsonProperty("pagination") Pagination pagination,
                                         @JsonProperty("resources") @Singular List<Resource> resources) {
        super(pagination, resources);
    }

    /**
     * The Resource response payload for the Get Detailed Stats for a Process operation
     */
    @Data
    public static final class Resource {

        private final long diskQuota;

        private final long fdsQuota;

        private final String host;

        private final int index;

        private final long memQuota;

        private final int port;

        private final String state;

        private final String type;

        private final long uptime;

        private final Usage usage;

        @Builder
        Resource(@JsonProperty("disk_quota") long diskQuota,
                 @JsonProperty("fds_quota") long fdsQuota,
                 @JsonProperty("host") String host,
                 @JsonProperty("index") int index,
                 @JsonProperty("mem_quota") long memQuota,
                 @JsonProperty("port") int port,
                 @JsonProperty("state") String state,
                 @JsonProperty("type") String type,
                 @JsonProperty("uptime") long uptime,
                 @JsonProperty("usage") Usage usage) {
            this.diskQuota = diskQuota;
            this.fdsQuota = fdsQuota;
            this.host = host;
            this.index = index;
            this.memQuota = memQuota;
            this.port = port;
            this.state = state;
            this.type = type;
            this.uptime = uptime;
            this.usage = usage;
        }

    }

    @Data
    public static final class Usage {

        private final Double cpu;

        private final Long disk;

        private final Long memory;

        private final String time;

        @Builder
        Usage(@JsonProperty("cpu") Double cpu,
              @JsonProperty("disk") Long disk,
              @JsonProperty("mem") Long memory,
              @JsonProperty("time") String time) {
            this.cpu = cpu;
            this.disk = disk;
            this.memory = memory;
            this.time = time;
        }
    }
}
