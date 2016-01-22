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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload for the v2 Create Application request
 */
@Data
public final class UpdateApplicationRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String applicationId;

    /**
     * Buildpack to build the app.
     *
     * @param buildpack the buildpack to build the app
     * @return buildpack to build the app
     */
    @Getter(onMethod = @__(@JsonProperty("buildpack")))
    private final String buildpack;

    /**
     * The command to start the app after it is staged.
     *
     * @param command the command to start the app
     * @return the command to start the app
     */
    @Getter(onMethod = @__(@JsonProperty("command")))
    private final String command;

    /**
     * Open the console port for the app (at $CONSOLE_PORT).
     *
     * @param console whether to open the console port
     * @return whether to open the console port
     */
    @Getter(onMethod = @__({@JsonProperty("console"), @Deprecated}))
    private final Boolean console;

    /**
     * Open the debug port for the app (at $DEBUG_PORT).
     *
     * @param debug whether to open the debug port
     * @return whether to open the debug port
     */
    @Getter(onMethod = @__({@JsonProperty("debug"), @Deprecated}))
    private final Boolean debug;

    /**
     * Use diego to stage and to run when available.
     *
     * @param diego whether to use diego
     * @return whether to use diego
     */
    @Getter(onMethod = @__(@JsonProperty("diego")))
    private final Boolean diego;

    /**
     * The maximum amount of disk available to an instance of an app. In megabytes.
     *
     * @param diskQuota the maximum amount of disk available to an instance
     * @return the maximum amount of disk available to an instance
     */
    @Getter(onMethod = @__(@JsonProperty("disk_quota")))
    private final Integer diskQuota;

    /**
     * Docker credentials for pulling docker image.
     *
     * @param dockerCredentialsJsons docker credentials
     * @return docker credentials
     */
    @Getter(onMethod = @__({@JsonProperty("docker_credentials_json"), @JsonInclude(NON_EMPTY)}))
    private final Map<String, Object> dockerCredentialsJsons;

    /**
     * Name of the Docker image containing the app.
     *
     * @param dockerImage the name of the Docker image
     * @return name of the Docker image
     */
    @Getter(onMethod = @__(@JsonProperty("docker_image")))
    private final String dockerImage;

    /**
     * Key/value pairs of all the environment variables to run in your app. Does not include any system or service variables.
     *
     * @param environmentJsons the environment variables to run in your app
     * @return the environment variables to run in your app
     */
    @Getter(onMethod = @__({@JsonProperty("environment_json"), @JsonInclude(NON_EMPTY)}))
    private final Map<String, Object> environmentJsons;

    /**
     * Timeout for health checking of an staged app when starting up.
     *
     * @param healthCheckTimeout timeout for health checking
     * @return timeout for health checking
     */
    @Getter(onMethod = @__(@JsonProperty("health_check_timeout")))
    private final Integer healthCheckTimeout;

    /**
     * Type of health check to perform.
     *
     * @param healthCheckType the type of health check to perform
     * @return type of health check to perform
     */
    @Getter(onMethod = @__(@JsonProperty("health_check_type")))
    private final String healthCheckType;

    /**
     * The number of instances of the app to run. To ensure optimal availability, ensure there are at least 2 instances.
     *
     * @param instances the number of instances to run
     * @return the number of instances to run
     */
    @Getter(onMethod = @__(@JsonProperty("instances")))
    private final Integer instances;

    /**
     * The amount of memory each instance should have. In megabytes.
     *
     * @param memory the amount of memory each instance should have
     * @return the amount of memory each instance should have
     */
    @Getter(onMethod = @__(@JsonProperty("memory")))
    private final Integer memory;

    /**
     * The name of the app.
     *
     * @param name the name of the app
     * @return the name of the app
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * Whether the app is production
     *
     * @param production whether the app is production
     * @return whether the app is production
     */
    @Getter(onMethod = @__({@JsonProperty("production"), @Deprecated}))
    private final Boolean production;

    /**
     * The id of the associated space.
     *
     * @param spaceId the id of the associated space
     * @return the id of the associated space
     */
    @Getter(onMethod = @__(@JsonProperty("space_guid")))
    private final String spaceId;

    /**
     * The id of the associated stack.
     *
     * @param stackId the id of the associated stack
     * @return the id of the associated stack
     */
    @Getter(onMethod = @__(@JsonProperty("stack_guid")))
    private final String stackId;

    /**
     * The current desired state of the app.
     *
     * @param state the current desired state of the app
     * @return the current desired state of the app
     */
    @Getter(onMethod = @__(@JsonProperty("state")))
    private final String state;

    @Builder
    UpdateApplicationRequest(String applicationId,
                             String buildpack,
                             String command,
                             Boolean console,
                             Boolean debug,
                             Boolean diego,
                             Integer diskQuota,
                             @Singular Map<String, Object> dockerCredentialsJsons,
                             String dockerImage,
                             @Singular Map<String, Object> environmentJsons,
                             Integer healthCheckTimeout,
                             String healthCheckType,
                             Integer instances,
                             Integer memory,
                             String name,
                             Boolean production,
                             String spaceId,
                             String stackId,
                             String state) {
        this.applicationId = applicationId;
        this.buildpack = buildpack;
        this.command = command;
        this.console = console;
        this.debug = debug;
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
        this.state = state;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        return builder.build();
    }

}
