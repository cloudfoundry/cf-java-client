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

import java.util.Map;

@Data
public final class ApplicationEnvironmentResponse {

    private final Map<String, Object> applicationEnvironment;

    private final Map<String, String> environment;

    private final Map<String, String> runningEnvironment;

    private final Map<String, String> stagingEnvironment;

    private final Map<String, Object> systemEnvironment;

    @Builder
    ApplicationEnvironmentResponse(
            @JsonProperty("application_env_json") Map<String, Object> applicationEnvironment,
            @JsonProperty("environment_json") Map<String, String> environment,
            @JsonProperty("running_env_json") Map<String, String> runningEnvironment,
            @JsonProperty("staging_env_json") Map<String, String> stagingEnvironment,
            @JsonProperty("system_env_json") Map<String, Object> systemEnvironment) {

        this.applicationEnvironment = applicationEnvironment;
        this.environment = environment;
        this.runningEnvironment = runningEnvironment;
        this.stagingEnvironment = stagingEnvironment;
        this.systemEnvironment = systemEnvironment;
    }
}
