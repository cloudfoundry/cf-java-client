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

package org.cloudfoundry.operations.applications;


import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * An application manifest that captures some of the details of how an application is deployed.  See <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html">the manifest
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

        if (getDocker() != null) {
            if (getDocker().getImage() != null && getBuildpacks() != null && !getBuildpacks().isEmpty()) {
                throw new IllegalStateException("docker image and buildpack cannot both be set");
            }

            if (getDocker().getImage() != null && getPath() != null) {
                throw new IllegalStateException("docker image and path cannot both be set");
            }

            if (getDocker().getImage() == null && (getDocker().getUsername() != null || getDocker().getPassword() != null)) {
                throw new IllegalStateException("docker credentials require docker image to be set");
            }

            if (getDocker().getPassword() != null && getDocker().getUsername() == null) {
                throw new IllegalStateException("Docker password requires username");
            }

            if (getDocker().getPassword() == null && getDocker().getUsername() != null) {
                throw new IllegalStateException("Docker username requires password");
            }
        }
    }

    /**
     * The buildpacks used by the application
     */
    @Nullable
    abstract List<String> getBuildpacks();

    /**
     * The command used to execute the application
     */
    @Nullable
    abstract String getCommand();

    /**
     * The disk quota in megabytes
     */
    @Nullable
    abstract Integer getDisk();

    /**
     * The docker information
     */
    @Nullable
    abstract Docker getDocker();

    /**
     * The collection of domains bound to the application
     */
    @Nullable
    abstract List<String> getDomains();

    /**
     * The environment variables to set on the application
     */
    @AllowNulls
    @Nullable
    abstract Map<String, Object> getEnvironmentVariables();

    /**
     * The HTTP health check endpoint
     */
    @Nullable
    abstract String getHealthCheckHttpEndpoint();

    /**
     * The health check type
     */
    @Nullable
    abstract ApplicationHealthCheck getHealthCheckType();

    /**
     * The collection of hosts bound to the application
     */
    @Nullable
    abstract List<String> getHosts();

    /**
     * The number of instances of the application
     */
    @Nullable
    abstract Integer getInstances();

    /**
     * The memory quota in megabytes
     */
    @Nullable
    abstract Integer getMemory();

    /**
     * The name of the application
     */
    abstract String getName();

    /**
     * Map the the root domain to the app
     */
    @Nullable
    abstract Boolean getNoHostname();

    /**
     * Prevent a route being created for the app
     */
    @Nullable
    abstract Boolean getNoRoute();

    /**
     * The location of the application
     */
    @Nullable
    abstract Path getPath();

    /**
     * Generate a random route
     */
    @Nullable
    abstract Boolean getRandomRoute();

    /**
     * The route path for all applications
     */
    @Nullable
    abstract String getRoutePath();

    /**
     * The collection of routes bound to the application
     */
    @Nullable
    abstract List<Route> getRoutes();

    /**
     * The collection of service names bound to the application
     */
    @Nullable
    abstract List<String> getServices();

    /**
     * The stack used to run the application
     */
    @Nullable
    abstract String getStack();

    /**
     * The number of seconds allowed for application start
     */
    @Nullable
    abstract Integer getTimeout();

}
