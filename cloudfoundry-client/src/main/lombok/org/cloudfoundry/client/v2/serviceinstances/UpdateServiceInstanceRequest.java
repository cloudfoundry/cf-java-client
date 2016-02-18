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

package org.cloudfoundry.client.v2.serviceinstances;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

public final class UpdateServiceInstanceRequest implements Validatable {

    /**
     * The accept incomplete flag
     *
     * @param acceptsIncomplete Set to `true` if the client allows asynchronous provisioning. The cloud controller may respond before the service is ready for use.
     * @return the accept incomplete flag
     */
    @Getter(onMethod = @__(@QueryParameter("accepts_incomplete")))
    private final boolean acceptsIncomplete;

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
    @Getter(onMethod = @__(@JsonIgnore))
    private final String serviceInstanceId;

    /**
     * The service plan id
     *
     * @param servicePlanId the service plan id
     * @return the service plan id
     */
    @Getter(onMethod = @__(@JsonProperty("service_plan_guid")))
    private final String servicePlanId;

    /**
     * A list of tags for the service instance
     *
     * @return the list of tags for the service instance
     */
    @Getter(onMethod = @__({@JsonProperty("tags"), @JsonInclude(NON_EMPTY)}))
    private final List<String> tags;


    @Builder
    UpdateServiceInstanceRequest(boolean acceptsIncomplete,
                                 String name,
                                 @Singular Map<String, Object> parameters,
                                 String serviceInstanceId,
                                 String servicePlanId,
                                 @Singular List<String> tags) {
        this.acceptsIncomplete = acceptsIncomplete;
        this.name = name;
        this.parameters = parameters;
        this.serviceInstanceId = serviceInstanceId;
        this.servicePlanId = servicePlanId;
        this.tags = tags;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.serviceInstanceId == null) {
            builder.message("service instance id must be specified");
        }

        return builder.build();
    }

}
