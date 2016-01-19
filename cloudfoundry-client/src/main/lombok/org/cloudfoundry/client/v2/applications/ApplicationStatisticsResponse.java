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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The response payload for the Get Application Statistics operation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ApplicationStatisticsResponse extends HashMap<String, ApplicationStatisticsResponse.InstanceStats> {

    private static final long serialVersionUID = 5833542672981662359L;

    ApplicationStatisticsResponse() {
        super();
    }

    @Builder
    ApplicationStatisticsResponse(@Singular Map<String, InstanceStats> instances) {
        super(instances);
    }

    @Data
    public static final class InstanceStats {

        private final String state;

        private final Statistics statistics;

        private final Long uptime;

        @Builder
        InstanceStats(@JsonProperty("state") String state,
                      @JsonProperty("stats") Statistics statistics,
                      @JsonProperty("uptime") Long uptime) {
            this.state = state;
            this.statistics = statistics;
            this.uptime = uptime;
        }

        @Data
        public static final class Statistics {

            private final Long diskQuota;

            private final Integer fdsQuota;

            private final String host;

            private final Long memoryQuota;

            private final String name;

            private final Integer port;

            private final Long uptime;

            private final List<String> uris;

            private final Usage usage;

            @Builder
            Statistics(@JsonProperty("disk_quota") Long diskQuota,
                       @JsonProperty("fds_quota") Integer fdsQuota,
                       @JsonProperty("host") String host,
                       @JsonProperty("mem_quota") Long memoryQuota,
                       @JsonProperty("name") String name,
                       @JsonProperty("port") Integer port,
                       @JsonProperty("uptime") Long uptime,
                       @JsonProperty("uris") @Singular("uri") List<String> uris,
                       @JsonProperty("usage") Usage usage) {
                this.diskQuota = diskQuota;
                this.fdsQuota = fdsQuota;
                this.host = host;
                this.memoryQuota = memoryQuota;
                this.name = name;
                this.port = port;
                this.uptime = uptime;
                this.uris = uris;
                this.usage = usage;
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

    }

}
