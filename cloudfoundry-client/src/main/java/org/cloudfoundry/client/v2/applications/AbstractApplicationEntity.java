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
    public abstract Boolean getDebug();

    /**
     * The detected start command
     */
    @JsonProperty("detected_start_command")
    public abstract String getDetectedStartCommand();

    /**
     * Diego
     */
    @JsonProperty("diego")
    public abstract Boolean getDiego();

    /**
     * The disk quota in megabytes
     */
    @JsonProperty("disk_quota")
    public abstract Integer getDiskQuota();

    /**
     * The docker credentials JSONs
     */
    @Nullable
    @JsonProperty("docker_credentials_json")
    public abstract Map<String, Object> getDockerCredentialsJsons();

    /**
     * The docker image
     */
    @JsonProperty("docker_image")
    @Nullable
    public abstract String getDockerImage();

    /**
     * The environment JSONs
     */
    @JsonProperty("environment_json")
    public abstract Map<String, Object> getEnvironmentJsons();

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
    public abstract String getHealthCheckType();

    /**
     * The instances
     */
    @JsonProperty("instances")
    public abstract Integer getInstances();

    /**
     * The memory in megabytes
     */
    @JsonProperty("memory")
    public abstract Integer getMemory();

    /**
     * The name
     */
    @JsonProperty("name")
    public abstract String getName();

    /**
     * Production
     */
    @Deprecated
    @JsonProperty("production")
    public abstract Boolean getProduction();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    public abstract String getSpaceId();

    /**
     * The stack id
     */
    @JsonProperty("stack_guid")
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
    public abstract String getState();

}

