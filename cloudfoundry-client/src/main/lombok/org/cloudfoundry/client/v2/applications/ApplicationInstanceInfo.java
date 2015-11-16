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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Instance information in Get Application Instance response.
 */
@Data
public final class ApplicationInstanceInfo {

    private final String consoleIp;

    private final Integer consolePort;

    private final String debugIp;

    private final Integer debugPort;

    private final Double since;

    private final String state;

    @Builder
    ApplicationInstanceInfo(@JsonProperty("console_ip") String consoleIp,
                            @JsonProperty("console_port") Integer consolePort,
                            @JsonProperty("debug_ip") String debugIp,
                            @JsonProperty("debug_port") Integer debugPort,
                            @JsonProperty("since") Double since,
                            @JsonProperty("state") String state) {
        this.consoleIp = consoleIp;
        this.consolePort = consolePort;
        this.debugIp = debugIp;
        this.debugPort = debugPort;
        this.since = since;
        this.state = state;
    }

}
