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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Scale Process operation
 */
@Data
public final class ScaleProcessRequest implements Validatable {

    /**
     * The disk in megabytes
     *
     * @param diskInMb the disk in megabytes
     * @return the disk in megabytes
     */
    @Getter(onMethod = @__(@JsonProperty("disk_in_mb")))
    private final Integer diskInMb;

    /**
     * The number of instances
     *
     * @param instances the number of instances
     * @return the number of instances
     */
    @Getter(onMethod = @__(@JsonProperty("instances")))
    private final Integer instances;

    /**
     * The memory in megabytes
     *
     * @param memoryInMb the memory in megabytes
     * @return the memory in megabytes
     */
    @Getter(onMethod = @__(@JsonProperty("memory_in_mb")))
    private final Integer memoryInMb;

    /**
     * The process id
     *
     * @param processId the process id
     * @return the process id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String processId;

    @Builder
    ScaleProcessRequest(Integer diskInMb, Integer instances, Integer memoryInMb, String processId) {
        this.diskInMb = diskInMb;
        this.instances = instances;
        this.memoryInMb = memoryInMb;
        this.processId = processId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.processId == null) {
            builder.message("process id must be specified");
        }

        return builder.build();
    }

}
