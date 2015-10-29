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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.Map;

/**
 * The request payload for the Stage Package operation
 */
@Data
public final class StagePackageRequest implements Validatable {

    @Getter(onMethod = @__(@JsonProperty("buildpack")))
    private final String buildpack;

    @Getter(onMethod = @__(@JsonProperty("disk_limit")))
    private final Integer diskLimit;

    @Getter(onMethod = @__(@JsonProperty("environment_variables")))
    private final Map<String, Object> environmentVariables;

    @Getter(onMethod = @__(@JsonIgnore))
    private final String id;

    @Getter(onMethod = @__(@JsonProperty("memory_limit")))
    private final Integer memoryLimit;

    @Getter(onMethod = @__(@JsonProperty("stack")))
    private final String stack;

    @Builder
    StagePackageRequest(String buildpack, Integer diskLimit, @Singular Map<String, Object> environmentVariables,
                        String id, Integer memoryLimit, String stack) {
        this.buildpack = buildpack;
        this.diskLimit = diskLimit;
        this.environmentVariables = environmentVariables;
        this.id = id;
        this.memoryLimit = memoryLimit;
        this.stack = stack;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
