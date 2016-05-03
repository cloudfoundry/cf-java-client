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

package org.cloudfoundry.doppler;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Optional;

/**
 * Records resource usage of an application in a container
 */
@Data
public final class ContainerMetric implements Event, Validatable {

    /**
     * The ID of the contained application
     *
     * @param applicationId the ID of the contained application
     * @return the ID of the contained application
     */
    private final String applicationId;

    /**
     * The CPU used, on a scale of 0 to 100
     *
     * @param cpuPercentage the CPU used, on a scale of 0 to 100
     * @return the CPU used, on a scale of 0 to 100
     */
    private final Double cpuPercentage;

    /**
     * The bytes of disk used
     *
     * @param diskBytes the bytes of disk used
     * @return the bytes of disk used
     */
    private final Long diskBytes;

    /**
     * The instance index of the contained application. (This, with applicationId, should uniquely identify a container.)
     *
     * @param instanceIndex the instance index of the contained application
     * @return the instance index of the contained application
     */
    private final Integer instanceIndex;

    /**
     * The bytes of memory used
     *
     * @param memoryBytes the bytes of memory used
     * @return the bytes of memory used
     */
    private final Long memoryBytes;

    @Builder
    ContainerMetric(org.cloudfoundry.dropsonde.events.ContainerMetric dropsonde, String applicationId, Double cpuPercentage, Long diskBytes, Integer instanceIndex, Long memoryBytes) {
        Optional<org.cloudfoundry.dropsonde.events.ContainerMetric> o = Optional.ofNullable(dropsonde);

        this.applicationId = o.map(d -> d.applicationId).orElse(applicationId);
        this.cpuPercentage = o.map(d -> d.cpuPercentage).orElse(cpuPercentage);
        this.diskBytes = o.map(d -> d.diskBytes).orElse(diskBytes);
        this.instanceIndex = o.map(d -> d.instanceIndex).orElse(instanceIndex);
        this.memoryBytes = o.map(d -> d.memoryBytes).orElse(memoryBytes);
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        if (this.cpuPercentage == null) {
            builder.message("cpu percentage must be specified");
        }

        if (this.diskBytes == null) {
            builder.message("disk bytes must be specified");
        }

        if (this.instanceIndex == null) {
            builder.message("instance index must be specified");
        }

        if (this.memoryBytes == null) {
            builder.message("memory bytes must be specified");
        }

        return builder.build();
    }

}
