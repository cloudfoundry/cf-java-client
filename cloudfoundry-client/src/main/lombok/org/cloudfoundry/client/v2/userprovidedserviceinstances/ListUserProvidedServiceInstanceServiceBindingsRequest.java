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

package org.cloudfoundry.client.v2.userprovidedserviceinstances;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Service Bindings for the User Provided Service Instance operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListUserProvidedServiceInstanceServiceBindingsRequest extends PaginatedRequest implements Validatable {

    /**
     * The ids of the applications
     *
     * @param applicationIds the ids of the applications to filter on
     * @return the ids of the applications to filter on
     */
    @Getter(onMethod = @__(@InFilterParameter("app_guid")))
    private final List<String> applicationIds;

    /**
     * The user provided service instance id
     *
     * @param userProvidedServiceInstanceId the user provided service instance id
     * @return the user provided  service instance id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String userProvidedServiceInstanceId;

    @Builder
    ListUserProvidedServiceInstanceServiceBindingsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                                          @Singular List<String> applicationIds,
                                                          String userProvidedServiceInstanceId) {
        super(orderDirection, page, resultsPerPage);
        this.applicationIds = applicationIds;
        this.userProvidedServiceInstanceId = userProvidedServiceInstanceId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.userProvidedServiceInstanceId == null) {
            builder.message("user provided service instance id must be specified");
        }

        return builder.build();
    }

}
