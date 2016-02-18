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

package org.cloudfoundry.client.v2.servicebindings;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload for the Create Service Binding
 */
@Data
public final class CreateServiceBindingRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the applicationId
     * @return the applicationId
     */
    @Getter(onMethod = @__(@JsonProperty("app_guid")))
    private final String applicationId;

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
    CreateServiceBindingRequest(String applicationId, String serviceInstanceId, @Singular Map<String, Object> parameters) {
        this.applicationId = applicationId;
        this.serviceInstanceId = serviceInstanceId;
        this.parameters = parameters;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.applicationId == null) {
            builder.message("application id must be specified");
        }

        if (this.serviceInstanceId == null) {
            builder.message("service instance id must be specified");
        }

        return builder.build();
    }

}
