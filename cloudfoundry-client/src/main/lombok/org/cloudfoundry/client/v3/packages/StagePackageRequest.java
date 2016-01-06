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

    /**
     * The buildpack
     *
     * @param buildpack the buildpack
     * @return the buildpack
     */
    @Getter(onMethod = @__(@JsonProperty("buildpack")))
    private final String buildpack;

    /**
     * The disk limit
     *
     * @param diskLimit the disk limit
     * @return the disk limit
     */
    @Getter(onMethod = @__(@JsonProperty("disk_limit")))
    private final Integer diskLimit;

    /**
     * The environment variables
     *
     * @param environmentVariables the environment variables
     * @return the environment variables
     */
    @Getter(onMethod = @__(@JsonProperty("environment_variables")))
    private final Map<String, Object> environmentVariables;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String id;

    /**
     * The memory limit
     *
     * @param memoryLimit the memoty limit
     * @return the memory limit
     */
    @Getter(onMethod = @__(@JsonProperty("memory_limit")))
    private final Integer memoryLimit;

    /**
     * The stack
     *
     * @param stack the stack
     * @return the stack
     */
    @Getter(onMethod = @__(@JsonProperty("stack")))
    private final String stack;

    @Builder
    StagePackageRequest(String buildpack,
                        Integer diskLimit,
                        @Singular Map<String, Object> environmentVariables,
                        String id,
                        Integer memoryLimit,
                        String stack) {
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
