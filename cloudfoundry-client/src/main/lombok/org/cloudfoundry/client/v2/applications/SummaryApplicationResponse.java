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
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.client.v2.domains.Domain;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;

import java.util.List;
import java.util.Map;

/**
 * The response payload for the Get Application Summary operation
 */
@Data
public final class SummaryApplicationResponse {

    private final List<Domain> availableDomains;

    private final String buildpack;

    private final String command;

    @Getter(onMethod = @__(@Deprecated))
    private final Boolean console;

    @Getter(onMethod = @__(@Deprecated))
    private final Boolean debug;

    private final String detectedBuildpack;

    private final String detectedStartCommand;

    private final Boolean diego;

    private final Integer diskQuota;

    private final Map<String, Object> dockerCredentialsJsons;

    private final String dockerImage;

    private final Boolean enableSsh;

    private final Map<String, Object> environmentJsons;

    private final Integer healthCheckTimeout;

    private final String healthCheckType;

    private final String id;

    private final Integer instances;

    private final Integer memory;

    private final String name;

    private final String packageState;

    private final String packageUpdatedAt;

    @Getter(onMethod = @__(@Deprecated))
    private final Boolean production;

    private final List<Route> routes;

    private final Integer runningInstances;

    private final List<ServiceInstance> services;

    private final String spaceId;

    private final String stackId;

    private final String stagingFailedDescription;

    private final String stagingFailedReason;

    private final String stagingTaskId;

    private final String state;

    private final String version;

    @Builder
    SummaryApplicationResponse(@JsonProperty("available_domains") @Singular List<Domain> availableDomains,
                               @JsonProperty("buildpack") String buildpack,
                               @JsonProperty("command") String command,
                               @JsonProperty("console") @Deprecated Boolean console,
                               @JsonProperty("debug") @Deprecated Boolean debug,
                               @JsonProperty("detected_buildpack") String detectedBuildpack,
                               @JsonProperty("detected_start_command") String detectedStartCommand,
                               @JsonProperty("diego") Boolean diego,
                               @JsonProperty("disk_quota") Integer diskQuota,
                               @JsonProperty("docker_credentials_json") @Singular Map<String, Object>
                                       dockerCredentialsJsons,
                               @JsonProperty("docker_image") String dockerImage,
                               @JsonProperty("enable_ssh") Boolean enableSsh,
                               @JsonProperty("environment_json") @Singular Map<String, Object> environmentJsons,
                               @JsonProperty("health_check_timeout") Integer healthCheckTimeout,
                               @JsonProperty("health_check_type") String healthCheckType,
                               @JsonProperty("guid") String id,
                               @JsonProperty("instances") Integer instances,
                               @JsonProperty("memory") Integer memory,
                               @JsonProperty("name") String name,
                               @JsonProperty("package_state") String packageState,
                               @JsonProperty("package_updated_at") String packageUpdatedAt,
                               @JsonProperty("production") @Deprecated Boolean production,
                               @JsonProperty("routes") @Singular List<Route> routes,
                               @JsonProperty("running_instances") Integer runningInstances,
                               @JsonProperty("services") @Singular List<ServiceInstance> services,
                               @JsonProperty("space_guid") String spaceId,
                               @JsonProperty("stack_guid") String stackId,
                               @JsonProperty("staging_failed_description") String stagingFailedDescription,
                               @JsonProperty("staging_failed_reason") String stagingFailedReason,
                               @JsonProperty("staging_task_id") String stagingTaskId,
                               @JsonProperty("state") String state,
                               @JsonProperty("version") String version) {
        this.availableDomains = availableDomains;
        this.buildpack = buildpack;
        this.command = command;
        this.console = console;
        this.debug = debug;
        this.detectedBuildpack = detectedBuildpack;
        this.detectedStartCommand = detectedStartCommand;
        this.diego = diego;
        this.diskQuota = diskQuota;
        this.dockerCredentialsJsons = dockerCredentialsJsons;
        this.dockerImage = dockerImage;
        this.enableSsh = enableSsh;
        this.environmentJsons = environmentJsons;
        this.healthCheckTimeout = healthCheckTimeout;
        this.healthCheckType = healthCheckType;
        this.id = id;
        this.instances = instances;
        this.memory = memory;
        this.name = name;
        this.packageState = packageState;
        this.packageUpdatedAt = packageUpdatedAt;
        this.production = production;
        this.routes = routes;
        this.runningInstances = runningInstances;
        this.services = services;
        this.spaceId = spaceId;
        this.stackId = stackId;
        this.stagingFailedDescription = stagingFailedDescription;
        this.stagingFailedReason = stagingFailedReason;
        this.stagingTaskId = stagingTaskId;
        this.state = state;
        this.version = version;
    }

}
