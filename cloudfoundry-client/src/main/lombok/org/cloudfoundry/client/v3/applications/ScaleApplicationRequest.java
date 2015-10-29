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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Scale Application operation
 */
@Data
public final class ScaleApplicationRequest implements Validatable {

    @Getter(onMethod = @__(@JsonProperty("disk_in_mb")))
    private final Integer diskInMb;

    @Getter(onMethod = @__(@JsonIgnore))
    private final String id;

    @Getter(onMethod = @__(@JsonProperty("instances")))
    private final Integer instances;

    @Getter(onMethod = @__(@JsonProperty("memory_in_mb")))
    private final Integer memoryInMb;

    @Getter(onMethod = @__(@JsonIgnore))
    private final String type;

    @Builder
    ScaleApplicationRequest(Integer diskInMb, String id, Integer instances, Integer memoryInMb, String type) {
        this.diskInMb = diskInMb;
        this.id = id;
        this.instances = instances;
        this.memoryInMb = memoryInMb;
        this.type = type;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        if (this.type == null) {
            builder.message("type must be specified");
        }

        return builder.build();
    }

}
