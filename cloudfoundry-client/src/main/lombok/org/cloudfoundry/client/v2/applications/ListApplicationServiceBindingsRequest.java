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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Service Bindings for the App operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListApplicationServiceBindingsRequest extends PaginatedRequest implements Validatable {

    /**
     * The application id
     *
     * @param applicationId the application id
     * @return the application id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String applicationId;

    /**
     * The ids of the service instances
     *
     * @param serviceInstanceIds the ids of the service instances to filter on
     * @return the ids of the service instances to filter on
     */
    @Getter(onMethod = @__(@InFilterParameter("service_instance_guid")))
    private final List<String> serviceInstanceIds;

    @Builder
    ListApplicationServiceBindingsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                          String applicationId,
                                          @Singular List<String> serviceInstanceIds) {
        super(orderDirection, page, resultsPerPage);
        this.applicationId = applicationId;
        this.serviceInstanceIds = serviceInstanceIds;
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
