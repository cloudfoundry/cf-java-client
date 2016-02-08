/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.cloudfoundry.client.v3.processes.AbstractProcessDetailedStatistics;
import org.cloudfoundry.client.v3.processes.ProcessUsage;

import java.util.List;

/**
 * The response payload for the Get Detailed Stats for an Application's Process operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class GetApplicationProcessDetailedStatisticsResponse extends PaginatedResponse<GetApplicationProcessDetailedStatisticsResponse.Resource> {

    @Builder
    GetApplicationProcessDetailedStatisticsResponse(@JsonProperty("pagination") Pagination pagination,
                                                    @JsonProperty("resources") @Singular List<Resource> resources) {
        super(pagination, resources);
    }

    /**
     * The Resource response payload for the Get Detailed Stats for an Application's Process operation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static final class Resource extends AbstractProcessDetailedStatistics {

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
                 @JsonProperty("usage") ProcessUsage usage) {
            super(index, memQuota, diskQuota, fdsQuota, usage, port, uptime, host, type, state);
        }

    }

}
