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
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Map;

@JsonDeserialize
@Value.Immutable
abstract class _ApplicationEnvironmentResponse {

    /**
     * The application environment variables
     */
    @AllowNulls
    @JsonProperty("application_env_json")
    @Nullable
    abstract Map<String, Object> getApplicationEnvironmentJsons();

    /**
     * The environment variables
     */
    @AllowNulls
    @JsonProperty("environment_json")
    @Nullable
    abstract Map<String, Object> getEnvironmentJsons();

    /**
     * The running environment variables
     */
    @AllowNulls
    @JsonProperty("running_env_json")
    @Nullable
    abstract Map<String, Object> getRunningEnvironmentJsons();

    /**
     * The staging environment variables
     */
    @AllowNulls
    @JsonProperty("staging_env_json")
    @Nullable
    abstract Map<String, Object> getStagingEnvironmentJsons();

    /**
     * The system environment variables
     */
    @AllowNulls
    @JsonProperty("system_env_json")
    @Nullable
    abstract Map<String, Object> getSystemEnvironmentJsons();

}
