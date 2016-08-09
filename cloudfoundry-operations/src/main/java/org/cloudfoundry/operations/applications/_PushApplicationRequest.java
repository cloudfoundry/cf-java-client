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

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.nio.file.Path;
import java.time.Duration;

/**
 * The request options for the push application operation
 */
@Value.Immutable
abstract class _PushApplicationRequest {

    /**
     * The path to the application
     */
    @Nullable
    abstract Path getApplication();

    /**
     * The buildpack for the application
     */
    @Nullable
    abstract String getBuildpack();

    /**
     * The custom start command for the application
     */
    @Nullable
    abstract String getCommand();

    /**
     * The disk quota for the application
     */
    @Nullable
    abstract Integer getDiskQuota();

    /**
     * The Docker image for the application
     */
    @Nullable
    abstract String getDockerImage();

    /**
     * The domain for the application
     */
    @Nullable
    abstract String getDomain();

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
     * The memory in MB for the application
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
    abstract String getPath();

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
    @Nullable
    abstract Duration getStagingTimeout();

    /**
     * How long to wait for startup
     */
    @Nullable
    abstract Duration getStartupTimeout();

    /**
     * The health check timeout
     */
    @Nullable
    abstract Integer getTimeout();

    @Value.Check
    void check() {
        if (getApplication() == null && getDockerImage() == null) {
            throw new IllegalStateException("One of application or dockerImage must be supplied");
        }

        if (getApplication() != null && getDockerImage() != null) {
            throw new IllegalStateException("Only one of application or dockerImage can be supplied");
        }
    }
}
