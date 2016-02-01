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

package org.cloudfoundry.client.v2.serviceplanvisibilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Create Service Plan Visibility
 */
public final class CreateServicePlanVisibilityRequest implements Validatable {

    /**
     * The organization id
     *
     * @param organizationId the organization id
     * @return the organization id
     */
    @Getter(onMethod = @__(@JsonProperty("organization_guid")))
    private final String organizationId;

    /**
     * The service plan id
     *
     * @param servicePlanId the service plan id
     * @return the service plan id
     */
    @Getter(onMethod = @__(@JsonProperty("service_plan_guid")))
    private final String servicePlanId;

    @Builder
    CreateServicePlanVisibilityRequest(String organizationId,
                                       String servicePlanId) {
        this.organizationId = organizationId;
        this.servicePlanId = servicePlanId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.organizationId == null) {
            builder.message("organization id must be specified");
        }

        if (this.servicePlanId == null) {
            builder.message("service plan id must be specified");
        }

        return builder.build();
    }

}
