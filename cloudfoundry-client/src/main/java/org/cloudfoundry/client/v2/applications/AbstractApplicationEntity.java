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
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;

import java.util.Map;

/**
 * The core entity response payload for Application resources
 */
public abstract class AbstractApplicationEntity {

    /**
     * The buildpack
     */
    @JsonProperty("buildpack")
    @Nullable
    public abstract String getBuildpack();

    /**
     * The command
     */
    @JsonProperty("command")
    @Nullable
    public abstract String getCommand();

    /**
     * The console
     */
    @Deprecated
    @JsonProperty("console")
    @Nullable
    public abstract Boolean getConsole();

    /**
     * Debug
     */
    @Deprecated
    @JsonProperty("debug")
    @Nullable
    public abstract String getDebug();

    /**
     * The detected start command
     */
    @JsonProperty("detected_start_command")
    @Nullable
    public abstract String getDetectedStartCommand();

    /**
     * Diego
     */
    @JsonProperty("diego")
    @Nullable
    public abstract Boolean getDiego();

    /**
     * The disk quota in megabytes
     */
    @JsonProperty("disk_quota")
    @Nullable
    public abstract Integer getDiskQuota();

    /**
     * The docker credentials
     */
    @JsonProperty("docker_credentials")
    @Nullable
    public abstract DockerCredentials getDockerCredentials();

    /**
     * The docker image
     */
    @JsonProperty("docker_image")
    @Nullable
    public abstract String getDockerImage();

    /**
     * The environment JSONs
     */
    @AllowNulls
    @JsonProperty("environment_json")
    @Nullable
    public abstract Map<String, Object> getEnvironmentJsons();

    /**
     * The health check HTTP endpoint
     */
    @JsonProperty("health_check_http_endpoint")
    @Nullable
    public abstract String getHealthCheckHttpEndpoint();

    /**
     * The health check timeout
     */
    @JsonProperty("health_check_timeout")
    @Nullable
    public abstract Integer getHealthCheckTimeout();

    /**
     * The health check type
     */
    @JsonProperty("health_check_type")
    @Nullable
    public abstract String getHealthCheckType();

    /**
     * The instances
     */
    @JsonProperty("instances")
    @Nullable
    public abstract Integer getInstances();

    /**
     * The memory in megabytes
     */
    @JsonProperty("memory")
    @Nullable
    public abstract Integer getMemory();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    public abstract String getName();

    /**
     * Production
     */
    @Deprecated
    @JsonProperty("production")
    @Nullable
    public abstract Boolean getProduction();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    @Nullable
    public abstract String getSpaceId();

    /**
     * The stack id
     */
    @JsonProperty("stack_guid")
    @Nullable
    public abstract String getStackId();

    /**
     * The staging failed description
     */
    @JsonProperty("staging_failed_description")
    @Nullable
    public abstract String getStagingFailedDescription();

    /**
     * The staging failed reason
     */
    @JsonProperty("staging_failed_reason")
    @Nullable
    public abstract String getStagingFailedReason();

    /**
     * The staging task id
     */
    @JsonProperty("staging_task_id")
    @Nullable
    public abstract String getStagingTaskId();

    /**
     * The state
     */
    @JsonProperty("state")
    @Nullable
    public abstract String getState();

}
