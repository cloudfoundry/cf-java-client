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

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * The request options for the push application operation
 */
@Value.Immutable
abstract class _PushApplicationRequest {

    @SuppressWarnings("deprecation")
    @Value.Check
    void check() {
        if (getApplication() == null && getPath() == null && getDockerImage() == null) {
            throw new IllegalStateException("One of path or dockerImage must be supplied");
        }

        if ((getApplication() != null || getPath() != null) && getDockerImage() != null) {
            throw new IllegalStateException("Only one of path or dockerImage can be supplied");
        }

        if (getDockerImage() == null && (getDockerPassword() != null || getDockerUsername() != null)) {
            throw new IllegalStateException("Docker credentials require docker image to be set");
        }

        if (getDockerPassword() != null && getDockerUsername() == null) {
            throw new IllegalStateException("Docker password requires username");
        }

        if (getDockerPassword() == null && getDockerUsername() != null) {
            throw new IllegalStateException("Docker username requires password");
        }
    }

    /**
     * The path to the application
     *
     * @see #getPath()
     * @see PushApplicationRequest.Builder#path(Path)
     * @deprecated in favor of variants of {@code path}
     */
    @Deprecated
    @Nullable
    abstract Path getApplication();

    /**
     * The buildpacks for the application
     */
    @Nullable
    abstract List<String> getBuildpacks();

    /**
     * The custom start command for the application
     */
    @Nullable
    abstract String getCommand();

    /**
     * The disk quota in megabytes for the application
     */
    @Nullable
    abstract Integer getDiskQuota();

    /**
     * The Docker image for the application
     */
    @Nullable
    abstract String getDockerImage();

    /**
     * The Docker repository password
     */
    @Nullable
    abstract String getDockerPassword();

    /**
     * The Docker repository username
     */
    @Nullable
    abstract String getDockerUsername();

    /**
     * The domain for the application
     */
    @Nullable
    abstract String getDomain();

    /**
     * The HTTP health check endpoint
     */
    @Nullable
    abstract String getHealthCheckHttpEndpoint();

    /**
     * The health check type for the application
     */
    @Nullable
    abstract ApplicationHealthCheck getHealthCheckType();

    /**
     * The host for the application
     */
    @Nullable
    abstract String getHost();

    /**
     * The number of instances for the application
     */
    @Nullable
    abstract Integer getInstances();

    /**
     * The memory in megabytes for the application
     */
    @Nullable
    abstract Integer getMemory();

    /**
     * The name for the application
     */
    abstract String getName();

    /**
     * Map the root domain to the application
     */
    @Nullable
    abstract Boolean getNoHostname();

    /**
     * Do not create a route for the application
     */
    @Nullable
    abstract Boolean getNoRoute();

    /**
     * Do not start the application after pushing
     */
    @Nullable
    abstract Boolean getNoStart();

    /**
     * The path for the application
     */
    @Nullable
    abstract Path getPath();

    /**
     * Use a random route for the application
     */
    @Nullable
    abstract Boolean getRandomRoute();

    /**
     * The route path for the application
     */
    @Nullable
    abstract String getRoutePath();

    /**
     * The stack for the application
     */
    @Nullable
    abstract String getStack();

    /**
     * How long to wait for staging
     */
    @Value.Default
    Duration getStagingTimeout() {
        return Duration.ofMinutes(5);
    }

    /**
     * How long to wait for startup
     */
    @Value.Default
    Duration getStartupTimeout() {
        return Duration.ofMinutes(5);
    }

    /**
     * The health check timeout
     */
    @Nullable
    abstract Integer getTimeout();
}
