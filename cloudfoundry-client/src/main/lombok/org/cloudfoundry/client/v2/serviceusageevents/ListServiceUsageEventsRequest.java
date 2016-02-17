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

package org.cloudfoundry.client.v2.serviceusageevents;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.QueryParameter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List Service Usage Events operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServiceUsageEventsRequest extends PaginatedRequest implements Validatable {

    /**
     * The after id: Restrict results to Service Usage Events after the one with the given id
     *
     * @param afterId the after id
     * @return the after id
     */
    @Getter(onMethod = @__(@QueryParameter("after_guid")))
    private final String afterId;

    /**
     * The service ids
     *
     * @param serviceIds the service ids
     * @return the service ids
     */
    @Getter(onMethod = @__(@InFilterParameter("service_guid")))
    private final List<String> serviceIds;

    /**
     * The service instance types
     *
     * @param serviceInstanceTypes the service instance types
     * @return the service instance types
     */
    @Getter(onMethod = @__(@InFilterParameter("service_instance_type")))
    private final List<String> serviceInstanceTypes;


    @Builder
    ListServiceUsageEventsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                  String afterId,
                                  @Singular List<String> serviceIds,
                                  @Singular List<String> serviceInstanceTypes) {
        super(orderDirection, page, resultsPerPage);
        this.afterId = afterId;
        this.serviceIds = serviceIds;
        this.serviceInstanceTypes = serviceInstanceTypes;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
