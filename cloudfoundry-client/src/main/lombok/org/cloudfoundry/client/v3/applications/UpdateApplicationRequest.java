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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v3.Lifecycle;

import java.util.Map;

/**
 * The request payload for the Update Application operation
 */
@Data
public final class UpdateApplicationRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String applicationId;

    /**
     * The environment variables
     *
     * @param environmentVariables the environment variables
     * @return the environment variables
     */
    @Getter(onMethod = @__(@JsonProperty("environment_variables")))
    private final Map<String, String> environmentVariables;

    /**
     * The lifecycle
     *
     * @param lifecycle the lifecycle
     * @return the lifecycle
     */
    @Getter(onMethod = @__(@JsonProperty("lifecycle")))
    private final Lifecycle lifecycle;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    @Builder
    UpdateApplicationRequest(String applicationId,
                             @Singular Map<String, String> environmentVariables,
                             Lifecycle lifecycle,
                             String name) {
        this.applicationId = applicationId;
        this.environmentVariables = environmentVariables;
        this.lifecycle = lifecycle;
        this.name = name;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        return builder.build();
    }

}