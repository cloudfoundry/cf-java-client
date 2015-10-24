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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v2.applications.AbstractApplicationEntity;

import java.util.List;
import java.util.Map;

/**
 * The Application part of a Space Summary in a response payload.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class SpaceApplicationSummary extends AbstractApplicationEntity {

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The routes
     *
     * @param routes the routes
     * @return the routes
     */
    private final List<Route> routes;

    /**
     * The running instances
     *
     * @param runningInstances the running instances
     * @return the running instances
     */
    private final Integer runningInstances;

    /**
     * The service count
     *
     * @param serviceCount the service count
     * @return the service count
     */
    private final Integer serviceCount;

    /**
     * The service names
     *
     * @param serviceNames the service names
     * @return the service names
     */
    private final List<String> serviceNames;

    /**
     * The urls
     *
     * @param urls the urls
     * @return the urls
     */
    private final List<String> urls;

    @Builder
    SpaceApplicationSummary(@JsonProperty("buildpack") String buildpack,
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
                            @JsonProperty("instances") Integer instances,
                            @JsonProperty("memory") Integer memory,
                            @JsonProperty("name") String name,
                            @JsonProperty("package_state") String packageState,
                            @JsonProperty("package_updated_at") String packageUpdatedAt,
                            @JsonProperty("production") @Deprecated Boolean production,
                            @JsonProperty("space_guid") String spaceId,
                            @JsonProperty("stack_guid") String stackId,
                            @JsonProperty("staging_failed_description") String stagingFailedDescription,
                            @JsonProperty("staging_failed_reason") String stagingFailedReason,
                            @JsonProperty("staging_task_id") String stagingTaskId,
                            @JsonProperty("state") String state,
                            @JsonProperty("version") String version,
                            @JsonProperty("guid") String id,
                            @JsonProperty("routes") @Singular List<Route> routes,
                            @JsonProperty("running_instances") Integer runningInstances,
                            @JsonProperty("service_count") Integer serviceCount,
                            @JsonProperty("service_names") @Singular List<String> serviceNames,
                            @JsonProperty("urls") @Singular List<String> urls) {
        super(buildpack, command, console, debug, detectedBuildpack, detectedStartCommand, diego, diskQuota,
                dockerCredentialsJsons, dockerImage, enableSsh, environmentJsons, healthCheckTimeout, healthCheckType,
                instances, memory, name, packageState, packageUpdatedAt, production, spaceId, stackId,
                stagingFailedDescription, stagingFailedReason, stagingTaskId, state, version);
        this.id = id;
        this.routes = routes;
        this.runningInstances = runningInstances;
        this.serviceCount = serviceCount;
        this.serviceNames = serviceNames;
        this.urls = urls;
    }

    @Data
    public static final class Route {

        /**
         * The domain
         *
         * @param domain the domain
         * @return the domain
         */
        private final Domain domain;

        /**
         * The host
         *
         * @param host the host
         * @return the host
         */
        private final String host;

        /**
         * The id
         *
         * @param id the id
         * @return the id
         */
        private final String id;

        @Builder
        Route(@JsonProperty("domain") Domain domain,
              @JsonProperty("host") String host,
              @JsonProperty("guid") String id) {
            this.domain = domain;
            this.host = host;
            this.id = id;
        }

        @Data
        public static final class Domain {

            /**
             * The id
             *
             * @param id the id
             * @return the id
             */
            private final String id;

            /**
             * The name
             *
             * @param name the name
             * @return the name
             */
            private final String name;

            @Builder
            Domain(@JsonProperty("guid") String id,
                   @JsonProperty("name") String name) {
                this.id = id;
                this.name = name;
            }

        }

    }

}
