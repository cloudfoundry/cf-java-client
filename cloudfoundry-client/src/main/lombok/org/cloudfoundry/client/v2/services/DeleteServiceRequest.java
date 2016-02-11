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

package org.cloudfoundry.client.v2.services;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Delete Service operation.
 */
@Data
public final class DeleteServiceRequest implements Validatable {

    /**
     * The async flag
     *
     * @param async Will run the delete request in a background job. Recommended: 'true'.
     * @return the async flag
     */
    @Getter(onMethod = @__(@QueryParameter("async")))
    private final Boolean async;

    /**
     * The purge flag
     *
     * @param purge Recursively remove a service and child objects from Cloud Foundry database without making requests to a service broker
     * @return the purge flag
     */
    @Getter(onMethod = @__(@QueryParameter("purge")))
    private final Boolean purge;

    /**
     * The service id
     *
     * @param serviceId the service id
     * @return the binding id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String serviceId;

    @Builder
    DeleteServiceRequest(Boolean async,
                         Boolean purge,
                         String serviceId) {
        this.async = async;
        this.purge = purge;
        this.serviceId = serviceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.serviceId == null) {
            builder.message("service id must be specified");
        }

        return builder.build();
    }

}
