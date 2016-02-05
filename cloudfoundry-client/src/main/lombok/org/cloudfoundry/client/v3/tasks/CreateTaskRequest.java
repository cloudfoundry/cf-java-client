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

package org.cloudfoundry.client.v3.tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.Map;

/**
 * The request payload for the Create Task endpoint
 */
@Data
public final class CreateTaskRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String applicationId;

    /**
     * The command
     *
     * @param command the command
     * @return the command
     */
    @Getter(onMethod = @__(@JsonProperty("command")))
    private final String command;
    
    /**
     * The environment variables
     *
     * @param environmentVariables the environment variables
     * @return the environment variables
     */
    @Getter(onMethod = @__(@JsonProperty("environment_variables")))
    private final Map<String, String> environmentVariables;
    
    /**
     * The memoryInMb
     *
     * @param memoryInMb the memory in Mb
     * @return the memoryInMb
     */
    @Getter(onMethod = @__(@JsonProperty("memory_in_mb")))
    private final Integer memoryInMb;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    @Builder
    CreateTaskRequest(String applicationId, String command, Map<String, String> environmentVariables, String name, Integer memoryInMb) {
        this.applicationId = applicationId;
        this.command = command;
        this.environmentVariables = environmentVariables;
        this.name = name;
        this.memoryInMb = memoryInMb;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        if (this.command == null) {
            builder.message("command must be specified");
        }

        if (this.name == null) {
            builder.message("name must be specified");
        }

        return builder.build();
    }

}
