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

package org.cloudfoundry.client.v2.serviceplans;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.IsFilterParameter;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List Service Plans operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServicePlansRequest extends PaginatedRequest implements Validatable {

    /**
     * The active flag
     *
     * @param active the active flag
     * @return the active flag
     */
    @Getter(onMethod = @__(@IsFilterParameter("active")))
    private final Boolean active;

    /**
     * The service broker ids
     *
     * @param serviceBrokerIds the service broker ids
     * @return the service broker ids
     */
    @Getter(onMethod = @__(@InFilterParameter("service_broker_guid")))
    private final List<String> serviceBrokerIds;

    /**
     * The service ids
     *
     * @param serviceIds the service ids
     * @return the service ids
     */
    @Getter(onMethod = @__(@InFilterParameter("service_guid")))
    private final List<String> serviceIds;

    /**
     * The service instance ids
     *
     * @param serviceInstanceIds the service instance ids
     * @return the service instance ids
     */
    @Getter(onMethod = @__(@InFilterParameter("service_instance_guid")))
    private final List<String> serviceInstanceIds;

    @Builder
    ListServicePlansRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                            Boolean active,
                            @Singular List<String> serviceBrokerIds,
                            @Singular List<String> serviceIds,
                            @Singular List<String> serviceInstanceIds) {
        super(orderDirection, page, resultsPerPage);
        this.active = active;
        this.serviceBrokerIds = serviceBrokerIds;
        this.serviceIds = serviceIds;
        this.serviceInstanceIds = serviceInstanceIds;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
