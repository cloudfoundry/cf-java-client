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
import lombok.Singular;

import java.util.Map;

@Data
public final class ApplicationEnvironmentResponse {

    private static final String VCAP_APPLICATION_KEY = "VCAP_APPLICATION";

    private final Map<String, Object> applicationEnvironmentJsons;

    private final Map<String, Object> environmentJsons;

    private final Map<String, Object> runningEnvironmentJsons;

    private final Map<String, Object> stagingEnvironmentJsons;

    private final Map<String, Object> systemEnvironmentJsons;

    @SuppressWarnings("unchecked")
    public Map<String, Object> vcapApplication() {
        Object vcapAppObj = this.applicationEnvironmentJsons.get(VCAP_APPLICATION_KEY);
        if (vcapAppObj instanceof Map) {
            return (Map<String,Object>) vcapAppObj;
        }
        return null;
    }

    @Builder
    ApplicationEnvironmentResponse(
            @JsonProperty("application_env_json") @Singular Map<String, Object> applicationEnvironmentJsons,
            @JsonProperty("environment_json") @Singular Map<String, Object> environmentJsons,
            @JsonProperty("running_env_json") @Singular Map<String, Object> runningEnvironmentJsons,
            @JsonProperty("staging_env_json") @Singular Map<String, Object> stagingEnvironmentJsons,
            @JsonProperty("system_env_json") @Singular Map<String, Object> systemEnvironmentJsons) {

        this.applicationEnvironmentJsons = applicationEnvironmentJsons;
        this.environmentJsons = environmentJsons;
        this.runningEnvironmentJsons = runningEnvironmentJsons;
        this.stagingEnvironmentJsons = stagingEnvironmentJsons;
        this.systemEnvironmentJsons = systemEnvironmentJsons;
    }
}
