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

package org.cloudfoundry.doppler;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Objects;

/**
 * Records resource usage of an application in a container
 */
@Value.Immutable
abstract class _ContainerMetric {

    public static ContainerMetric from(org.cloudfoundry.dropsonde.events.ContainerMetric dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return ContainerMetric.builder()
            .applicationId(dropsonde.applicationId)
            .cpuPercentage(dropsonde.cpuPercentage)
            .diskBytes(dropsonde.diskBytes)
            .diskBytesQuota(dropsonde.diskBytesQuota)
            .instanceIndex(dropsonde.instanceIndex)
            .memoryBytes(dropsonde.memoryBytes)
            .memoryBytesQuota(dropsonde.memoryBytesQuota)
            .build();
    }

    /**
     * The ID of the contained application
     */
    abstract String getApplicationId();

    /**
     * The CPU used, on a scale of 0 to 100
     */
    abstract Double getCpuPercentage();

    /**
     * The bytes of disk used
     */
    abstract Long getDiskBytes();

    /**
     * The maximum bytes of disk allocated to container
     */
    @Nullable
    abstract Long getDiskBytesQuota();

    /**
     * The instance index of the contained application. (This, with applicationId, should uniquely identify a container.)
     */
    abstract Integer getInstanceIndex();

    /**
     * The bytes of memory used
     */
    abstract Long getMemoryBytes();

    /**
     * The maximum bytes of memory allocated to container
     */
    @Nullable
    abstract Long getMemoryBytesQuota();

}
