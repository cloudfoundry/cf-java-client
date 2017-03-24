/*
 * Copyright 2013-2017 the original author or authors.
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


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * An application manifest which captures some of the details of how an application is deployed.  See <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html">the manifest
 * definition</a> for more details.
 */
@Value.Immutable
abstract class _ApplicationManifest {

    @Value.Check
    void check() {
        if (getRoutes() != null) {
            if (getHosts() != null) {
                throw new IllegalStateException("routes and hosts cannot both be set");
            }

            if (getDomains() != null) {
                throw new IllegalStateException("routes and domains cannot both be set");
            }

            if (getNoHostname() != null) {
                throw new IllegalStateException("routes and noHostname cannot both be set");
            }
        }
    }

    /**
     * The buildpack used by the application
     */
    @JsonProperty("buildpack")
    @Nullable
    abstract String getBuildpack();

    /**
     * The command used to execute the application
     */
    @JsonProperty("command")
    @Nullable
    abstract String getCommand();

    /**
     * The disk quota in megabytes
     */
    @JsonProperty("disk_quota")
    @Nullable
    abstract Integer getDisk();

    /**
     * The collection of domains bound to the application
     */
    @JsonProperty("domains")
    @Nullable
    abstract List<String> getDomains();

    /**
     * The environment variables to set on the application
     */
    @AllowNulls
    @JsonProperty("env")
    @Nullable
    abstract Map<String, Object> getEnvironmentVariables();

    /**
     * The HTTP health check endpoint
     */
    @JsonProperty("health-check-http-endpoint")
    @Nullable
    abstract String getHealthCheckHttpEndpoint();

    /**
     * The health check type
     */
    @JsonProperty("health-check-type")
    @Nullable
    abstract String getHealthCheckType();

    /**
     * The collection of hosts bound to the application
     */
    @JsonProperty("hosts")
    @Nullable
    abstract List<String> getHosts();

    /**
     * The number of instances of the application
     */
    @JsonProperty("instances")
    @Nullable
    abstract Integer getInstances();

    /**
     * The memory quota in megabytes
     */
    @JsonProperty("memory")
    @Nullable
    abstract Integer getMemory();

    /**
     * The name of the application
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * Map the the root domain to the app
     */
    @JsonProperty("no-hostname")
    @Nullable
    abstract Boolean getNoHostname();

    /**
     * Prevent a route being created for the app
     */
    @JsonProperty("no-route")
    @Nullable
    abstract Boolean getNoRoute();

    /**
     * The location of the application
     */
    @JsonProperty("path")
    @Nullable
    abstract Path getPath();

    /**
     * Generate a random route
     */
    @JsonProperty("random-route")
    @Nullable
    abstract Boolean getRandomRoute();

    /**
     * The collection of routes bound to the application
     */
    @JsonProperty("routes")
    @Nullable
    abstract List<Route> getRoutes();

    /**
     * The collection of service names bound to the application
     */
    @JsonProperty("services")
    @Nullable
    abstract List<String> getServices();

    /**
     * The stack used to run the application
     */
    @JsonProperty("stack")
    @Nullable
    abstract String getStack();

    /**
     * The number of seconds allowed for application start
     */
    @JsonProperty("timeout")
    @Nullable
    abstract Integer getTimeout();

}
