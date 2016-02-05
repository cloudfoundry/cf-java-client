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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Relationship;

import java.util.Map;

/**
 * The request payload for the Create Application operation
 */
@Data
public final class CreateApplicationRequest implements Validatable {

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

    /**
     * The relationships
     *
     * @param relationships the relationships
     * @return the relationships
     */
    @Getter(onMethod = @__(@JsonProperty("relationships")))
    private final Map<String, Relationship> relationships;

    @Builder
    CreateApplicationRequest(@Singular Map<String, String> environmentVariables,
                             Lifecycle lifecycle,
                             String name,
                             @Singular Map<String, Relationship> relationships) {
        this.environmentVariables = environmentVariables;
        this.lifecycle = lifecycle;
        this.name = name;
        this.relationships = relationships;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        Relationship spaceRelationship = this.relationships.get("space");
        if (spaceRelationship == null) {
            builder.message("space relationship must be specified");
        } else {
            builder.messages(spaceRelationship.isValid().getMessages());
        }

        return builder.build();
    }

}
