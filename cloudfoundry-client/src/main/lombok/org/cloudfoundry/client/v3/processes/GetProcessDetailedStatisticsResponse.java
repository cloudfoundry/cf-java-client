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
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static final class Resource extends AbstractProcessStatistics {

        @Builder
        Resource(@JsonProperty("disk_quota") Long diskQuota,
                 @JsonProperty("fds_quota") Integer fdsQuota,
                 @JsonProperty("host") String host,
                 @JsonProperty("index") Integer index,
                 @JsonProperty("instance_ports") @Singular List<PortMapping> instancePorts,
                 @JsonProperty("mem_quota") Long memoryQuota,
                 @JsonProperty("state") String state,
                 @JsonProperty("type") String type,
                 @JsonProperty("uptime") Long uptime,
                 @JsonProperty("usage") ProcessUsage usage) {

            super(diskQuota, fdsQuota, host, index, instancePorts, memoryQuota, state, type, uptime, usage);
        }

    }

}
