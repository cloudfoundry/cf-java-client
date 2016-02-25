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

package org.cloudfoundry.operations.applications;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * An application manifest which captures some of the details of how an application is deployed.  See <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html">the manifest
 * definition</a> for more details.
 */
@Data
public final class ApplicationManifest {

    /**
     * The buildpack used by the application
     *
     * @param buildpack the buildpack used by the application
     * @return the buildpack used by the application
     */
    @Getter(onMethod = @__(@JsonProperty("buildpack")))
    private final String buildpack;

    /**
     * The command used to execute the application
     *
     * @param command the command used to execute the application
     * @return the command used to execute the application
     */
    @Getter(onMethod = @__(@JsonProperty("command")))
    private final String command;

    /**
     * The disk quota in megabytes
     *
     * @param disk the disk quota in megabytes
     * @return the disk quota in megabytes
     */
    @Getter(onMethod = @__(@JsonProperty("disk_quota")))
    private final Integer disk;

    /**
     * The collection of domains bound to the application
     *
     * <p> This representation of an application manifest cannot preserve the correct relationship between domains and hosts. See <a href="https://github.com/cloudfoundry/cli/issues/765">this
     * issue</a> for more information.
     *
     * @param domains the collection of domains bound to the application
     * @return the collection of domains bound to the application
     */
    @Getter(onMethod = @__({@JsonProperty("domains"), @JsonInclude(NON_EMPTY)}))
    private final List<String> domains;

    /**
     * The environment variables to set on the application
     *
     * @param environmentVariables the environment variables to set on the application
     * @return the environment variables to set on the application
     */
    @Getter(onMethod = @__({@JsonProperty("env"), @JsonInclude(NON_EMPTY)}))
    private final Map<String, Object> environmentVariables;

    /**
     * The collection of hosts bound to the application
     *
     * <p> This representation of an application manifest cannot preserve the correct relationship between domains and hosts. See <a href="https://github.com/cloudfoundry/cli/issues/765">this
     * issue</a> for more information.
     *
     * @param hosts the collection of hosts bound to the application
     * @return the collection of hosts bound to the application
     */
    @Getter(onMethod = @__({@JsonProperty("hosts"), @JsonInclude(NON_EMPTY)}))
    private final List<String> hosts;

    /**
     * The number of instances of the application
     *
     * @param instance the number of instances of the application
     * @return the number of instances of the application
     */
    @Getter(onMethod = @__(@JsonProperty("instances")))
    private final Integer instances;

    /**
     * The memory quota in megabytes
     *
     * @param memory the memory quota in megabytes
     * @return the memory quota in megabytes
     */
    @Getter(onMethod = @__(@JsonProperty("memory")))
    private final Integer memory;

    /**
     * The name of the application
     *
     * @param name the name of the application
     * @return the name of the application
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * The collection of service names bound to the application
     *
     * @param services the collection of service names bound to the application
     * @return the collection of service names bound to the application
     */
    @Getter(onMethod = @__({@JsonProperty("services"), @JsonInclude(NON_EMPTY)}))
    private final List<String> services;

    /**
     * The stack used to run the application
     *
     * @param stack the stack used to run the application
     * @return the stack used to run the application
     */
    @Getter(onMethod = @__(@JsonProperty("stack")))
    private final String stack;

    /**
     * The number of seconds allowed for application start
     *
     * @param timeout the number of seconds allowed for application start
     * @return the number of seconds allowed for application start
     */
    @Getter(onMethod = @__(@JsonProperty("timeout")))
    private final Integer timeout;

    @Builder
    ApplicationManifest(@JsonProperty("buildpack") String buildpack,
                        @JsonProperty("command") String command,
                        @JsonProperty("disk_quota") Integer disk,
                        @JsonProperty("domains") @Singular List<String> domains,
                        @JsonProperty("env") @Singular Map<String, Object> environmentVariables,
                        @JsonProperty("hosts") @Singular List<String> hosts,
                        @JsonProperty("instances") Integer instances,
                        @JsonProperty("memory") Integer memory,
                        @JsonProperty("name") String name,
                        @JsonProperty("services") @Singular List<String> services,
                        @JsonProperty("stack") String stack,
                        @JsonProperty("timeout") Integer timeout) {
        this.buildpack = buildpack;
        this.command = command;
        this.disk = disk;
        this.domains = domains;
        this.environmentVariables = environmentVariables;
        this.hosts = hosts;
        this.instances = instances;
        this.memory = memory;
        this.name = name;
        this.services = services;
        this.stack = stack;
        this.timeout = timeout;
    }

}
