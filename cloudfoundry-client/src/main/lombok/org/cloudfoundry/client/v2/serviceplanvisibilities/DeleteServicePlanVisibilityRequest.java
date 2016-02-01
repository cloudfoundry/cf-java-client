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


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

public final class DeleteServicePlanVisibilityRequest implements Validatable {

    /**
     * The async flag
     *
     * @param async  Will run the delete request in a background job. Recommended: 'true'.
     * @return the async flag
     */
    @Getter(onMethod = @__(@QueryParameter("async")))
    private final Boolean async;

    /**
     * The service plan visibility id
     *
     * @param servicePlanVisibilityId the service plan visibility id
     * @return the service plan visibility id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String servicePlanVisibilityId;

    @Builder
    DeleteServicePlanVisibilityRequest(Boolean async, String servicePlanVisibilityId) {
        this.async = async;
        this.servicePlanVisibilityId = servicePlanVisibilityId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.servicePlanVisibilityId == null) {
            builder.message("service plan visibility id must be specified");
        }

        return builder.build();
    }

}
