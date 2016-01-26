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

package org.cloudfoundry.client.v2.servicekeys;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload for the Create Service Key
 */
public final class CreateServiceKeyRequest implements Validatable {

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    @Getter(onMethod = @__(@JsonProperty("name")))
    private final String name;

    /**
     * Key/value pairs of all arbitrary parameters to pass along to the service broker
     *
     * @return the arbitrary parameters to pass along to the service broker
     */
    @Getter(onMethod = @__({@JsonProperty("parameters"), @JsonInclude(NON_EMPTY)}))
    private final Map<String, Object> parameters;

    /**
     * The service instance id
     *
     * @param serviceInstanceId the service instance id
     * @return the service instance id
     */
    @Getter(onMethod = @__(@JsonProperty("service_instance_guid")))
    private final String serviceInstanceId;


    @Builder
    CreateServiceKeyRequest(String name,
                            @Singular Map<String, Object> parameters,
                            String serviceInstanceId) {
        this.name = name;
        this.parameters = parameters;
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.serviceInstanceId == null) {
            builder.message("service instance id must be specified");
        }

        return builder.build();
    }

}
