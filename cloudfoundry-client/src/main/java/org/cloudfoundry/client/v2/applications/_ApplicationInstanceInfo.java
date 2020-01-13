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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Instance information in Get Application Instance response.
 */
@JsonDeserialize
@Value.Immutable
abstract class _ApplicationInstanceInfo {

    /**
     * The console IP
     */
    @JsonProperty("console_ip")
    @Nullable
    abstract String getConsoleIp();

    /**
     * The console port
     */
    @JsonProperty("console_port")
    @Nullable
    abstract Integer getConsolePort();

    /**
     * The debug IP
     */
    @JsonProperty("debug_ip")
    @Nullable
    abstract String getDebugIp();

    /**
     * The debug port
     */
    @JsonProperty("debug_port")
    @Nullable
    abstract Integer getDebugPort();

    /**
     * The details
     */
    @JsonProperty("details")
    @Nullable
    abstract String getDetails();

    /**
     * The since
     */
    @JsonProperty("since")
    @Nullable
    abstract Double getSince();

    /**
     * The state
     */
    @JsonProperty("state")
    @Nullable
    abstract String getState();

    /**
     * The update
     */
    @JsonProperty("uptime")
    @Nullable
    abstract Long getUptime();

}
