/*
 * Copyright 2013-2021 the original author or authors.
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
import org.cloudfoundry.client.v3.processes.HealthCheckType;
import org.immutables.value.Value;

@Value.Immutable
abstract class _ManifestV3Process {

    /**
     * The type of this process
     */
    abstract String getType();

    /**
     * The command to launch this process
     */
    @Nullable
    abstract String getCommand();

    /**
     * The disk quota of this process
     */
    @Nullable
    abstract String getDisk();

    /**
     * The HTTP health check endpoint
     */
    @Nullable
    abstract String getHealthCheckHttpEndpoint();

    /**
     * The timeout in seconds for individual health check requests for http and port health checks
     */
    @Nullable
    abstract Integer getHealthCheckInvocationTimeout();

    /**
     * Type of health check to perform
     */
    @Nullable
    abstract HealthCheckType getHealthCheckType();

    /**
     * The number of instances of this process
     */
    @Nullable
    abstract Integer getInstances();

    /**
     * The memory quota of this process
     */
    @Nullable
    abstract String getMemory();

    /**
     * Time in seconds at which the health-check will report failure
     */
    @Nullable
    abstract Integer getTimeout();
}
