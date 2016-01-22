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

/**
 * Instance information in Get Application Instance response.
 */
@Data
public final class ApplicationInstanceInfo {

    /**
     * The console IP
     *
     * @param consoleIp the console IP
     * @return the console IP
     */
    private final String consoleIp;

    /**
     * The console port
     *
     * @param consolePort the console port
     * @return the console port
     */
    private final Integer consolePort;

    /**
     * The debug IP
     *
     * @param debugIp the debug IP
     * @return the debug IP
     */
    private final String debugIp;

    /**
     * The debug port
     *
     * @param debugPort the debug port
     * @return the debug port
     */
    private final Integer debugPort;

    /**
     * The since
     *
     * @param since the since
     * @return the since
     */
    private final Double since;

    /**
     * The state
     *
     * @param state the state
     * @return the state
     */
    private final String state;

    /**
     * The update
     *
     * @param update the uptime
     * @return the uptime
     */
    private final Long uptime;

    @Builder
    ApplicationInstanceInfo(@JsonProperty("console_ip") String consoleIp,
                            @JsonProperty("console_port") Integer consolePort,
                            @JsonProperty("debug_ip") String debugIp,
                            @JsonProperty("debug_port") Integer debugPort,
                            @JsonProperty("since") Double since,
                            @JsonProperty("state") String state,
                            @JsonProperty("uptime") Long uptime) {
        this.consoleIp = consoleIp;
        this.consolePort = consolePort;
        this.debugIp = debugIp;
        this.debugPort = debugPort;
        this.since = since;
        this.state = state;
        this.uptime = uptime;
    }

}
