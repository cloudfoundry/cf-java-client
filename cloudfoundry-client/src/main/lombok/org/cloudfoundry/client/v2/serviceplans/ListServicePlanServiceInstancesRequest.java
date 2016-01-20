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
 * The request payload for the List all Service Instances for the Service Plan operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServicePlanServiceInstancesRequest extends PaginatedRequest implements Validatable {

    /**
     * The gateway names
     *
     * @param gatewayNames the gateway names to filter on
     * @return the gateway names to filter on
     */
    @Getter(onMethod = @__(@InFilterParameter("gateway_name")))
    private final List<String> gatewayNames;

    /**
     * The id of the Service Plan
     *
     * @param id the id of the Service Plan
     * @return the id of the Service Plan
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String id;

    /**
     * The names of the service instances
     *
     * @param names the names of the service instances to filter on
     * @return the names of the service instances to filter on
     */
    @Getter(onMethod = @__(@InFilterParameter("names")))
    private final List<String> names;

    /**
     * The service binding ids
     *
     * @param serviceBindingIds the service binding ids to filter on
     * @return the service binding ids to filter on
     */
    @Getter(onMethod = @__(@InFilterParameter("service_binding_guid")))
    private final List<String> serviceBindingIds;

    /**
     * The service key ids
     *
     * @param serviceKeyIds the service key ids to filter on
     * @return the service key ids to filter on
     */
    @Getter(onMethod = @__(@InFilterParameter("service_key_guid")))
    private final List<String> serviceKeyIds;

    /**
     * The space ids
     *
     * @param spaceIds the space ids to filter on
     * @return the space ids to filter on
     */
    @Getter(onMethod = @__(@InFilterParameter("space_guid")))
    private final List<String> spaceIds;

    @Builder
    ListServicePlanServiceInstancesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                           @Singular List<String> gatewayNames,
                                           String id,
                                           @Singular List<String> names,
                                           @Singular List<String> serviceBindingIds,
                                           @Singular List<String> serviceKeyIds,
                                           @Singular List<String> spaceIds
    ) {
        super(orderDirection, page, resultsPerPage);
        this.gatewayNames = gatewayNames;
        this.id = id;
        this.names = names;
        this.serviceBindingIds = serviceBindingIds;
        this.serviceKeyIds = serviceKeyIds;
        this.spaceIds = spaceIds;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
