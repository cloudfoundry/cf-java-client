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
import lombok.Data;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

/**
 * The core entity response payload for Application resources
 */
@Data
public abstract class AbstractApplicationEntity {

    /**
     * The buildpack
     *
     * @param buildpack the buildpack
     * @return the buildpack
     */
    private final String buildpack;

    /**
     * The command
     *
     * @param command the command
     * @return the command
     */
    private final String command;

    /**
     * The console
     *
     * @param console the console
     * @return the console
     */
    @Getter(onMethod = @__(@Deprecated))
    private final Boolean console;

    /**
     * Debug
     *
     * @param debug debug
     * @return debug
     */
    @Getter(onMethod = @__(@Deprecated))
    private final Boolean debug;

    /**
     * The detected start command
     *
     * @param detectedStartCommand the detected start command
     * @return the detected start command
     */
    private final String detectedStartCommand;

    /**
     * Diego
     *
     * @param diego diego
     * @return diego
     */
    private final Boolean diego;

    /**
     * The disk quota
     *
     * @param diskQuota the disk quota
     * @return the disk quota
     */
    private final Integer diskQuota;

    /**
     * The docker credentials JSONs
     *
     * @param dockerCredentialsJson the docker credentials JSONs
     * @return the docker credentials JSONs
     */
    private final Map<String, Object> dockerCredentialsJsons;

    /**
     * The docker image
     *
     * @param dockerImage the docker image
     * @return the docker image
     */
    private final String dockerImage;

    /**
     * The environment JSONs
     *
     * @param environmentJSON the environment JSONs
     * @return environment JSONs
     */
    private final Map<String, Object> environmentJsons;

    /**
     * The health check timeout
     *
     * @param healthCheckTimeout health check timeout
     * @return the health check timeout
     */
    private final Integer healthCheckTimeout;

    /**
     * The health check type
     *
     * @param healthCheckType the health check type
     * @return the health check type
     */
    private final String healthCheckType;

    /**
     * The instances
     *
     * @param instances the instances
     * @return the instances
     */
    private final Integer instances;

    /**
     * The memory
     *
     * @param memory the memory
     * @return the memory
     */
    private final Integer memory;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * Production
     *
     * @param production Production
     * @return production
     */
    @Getter(onMethod = @__(@Deprecated))
    private final Boolean production;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    private final String spaceId;

    /**
     * The stack id
     *
     * @param stackId the stack id
     * @return the stack id
     */
    private final String stackId;

    /**
     * The staging failed description
     *
     * @param stagingFailedDescription the staging failed description
     * @return the staging failed description
     */
    private final String stagingFailedDescription;

    /**
     * The staging failed reason
     *
     * @param stagingFailedReason the staging failed reason
     * @return the staging failed reason
     */
    private final String stagingFailedReason;

    /**
     * The state
     *
     * @param state the state
     * @return the state
     */
    private final String state;

    protected AbstractApplicationEntity(@JsonProperty("buildpack") String buildpack,
                                        @JsonProperty("command") String command,
                                        @JsonProperty("console") @Deprecated Boolean console,
                                        @JsonProperty("debug") @Deprecated Boolean debug,
                                        @JsonProperty("detected_start_command") String detectedStartCommand,
                                        @JsonProperty("diego") Boolean diego,
                                        @JsonProperty("disk_quota") Integer diskQuota,
                                        @JsonProperty("docker_credentials_json") @Singular Map<String, Object> dockerCredentialsJsons,
                                        @JsonProperty("docker_image") String dockerImage,
                                        @JsonProperty("environment_json") @Singular Map<String, Object> environmentJsons,
                                        @JsonProperty("health_check_timeout") Integer healthCheckTimeout,
                                        @JsonProperty("health_check_type") String healthCheckType,
                                        @JsonProperty("instances") Integer instances,
                                        @JsonProperty("memory") Integer memory,
                                        @JsonProperty("name") String name,
                                        @JsonProperty("production") @Deprecated Boolean production,
                                        @JsonProperty("space_guid") String spaceId,
                                        @JsonProperty("stack_guid") String stackId,
                                        @JsonProperty("staging_failed_description") String stagingFailedDescription,
                                        @JsonProperty("staging_failed_reason") String stagingFailedReason,
                                        @JsonProperty("state") String state) {
        this.buildpack = buildpack;
        this.command = command;
        this.console = console;
        this.debug = debug;
        this.detectedStartCommand = detectedStartCommand;
        this.diego = diego;
        this.diskQuota = diskQuota;
        this.dockerCredentialsJsons = dockerCredentialsJsons;
        this.dockerImage = dockerImage;
        this.environmentJsons = environmentJsons;
        this.healthCheckTimeout = healthCheckTimeout;
        this.healthCheckType = healthCheckType;
        this.instances = instances;
        this.memory = memory;
        this.name = name;
        this.production = production;
        this.spaceId = spaceId;
        this.stackId = stackId;
        this.stagingFailedDescription = stagingFailedDescription;
        this.stagingFailedReason = stagingFailedReason;
        this.state = state;
    }

}

