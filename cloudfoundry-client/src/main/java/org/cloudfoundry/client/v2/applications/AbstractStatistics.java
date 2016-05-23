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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@JsonDeserialize
@Value.Immutable
abstract class AbstractStatistics {

    /**
     * The application disk quota
     */
    @JsonProperty("disk_quota")
    abstract Long getDiskQuota();

    /**
     * The application file descriptor quota
     */
    @JsonProperty("fds_quota")
    abstract Integer getFdsQuota();

    /**
     * The application host
     */
    @JsonProperty("host")
    abstract String getHost();

    /**
     * The application memory quota
     */
    @JsonProperty("mem_quota")
    abstract Long getMemoryQuota();

    /**
     * The application name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The application port
     */
    @JsonProperty("port")
    abstract Integer getPort();

    /**
     * The application uptime
     */
    @JsonProperty("uptime")
    abstract Long getUptime();

    /**
     * The application uris
     */
    @JsonProperty("uris")
    abstract List<String> getUris();

    /**
     * The application usage
     */
    @JsonProperty("usage")
    abstract Usage getUsage();

}
