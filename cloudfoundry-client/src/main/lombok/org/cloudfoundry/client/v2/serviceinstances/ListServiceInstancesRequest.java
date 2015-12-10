/*
 * Copyright 2013-2015 the original author or authors.
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

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List Service Instances operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServiceInstancesRequest extends PaginatedRequest implements Validatable {

    /**
     * The gateway names
     *
     * @param gatewayNames the gateway names
     * @return the gateway names
     */
    @Getter(onMethod = @__(@FilterParameter("gateway_name")))
    private final List<String> gatewayNames;

    /**
     * The names
     *
     * @param names the names
     * @return the names
     */
    @Getter(onMethod = @__(@FilterParameter("name")))
    private final List<String> names;

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("organization_guid")))
    private final List<String> organizationIds;

    /**
     * The service binding ids
     *
     * @param serviceBindingIds the service binding ids
     * @return the service binding ids
     */
    @Getter(onMethod = @__(@FilterParameter("service_binding_guid")))
    private final List<String> serviceBindingIds;

    /**
     * The service key ids
     *
     * @param serviceKeyIds the service key ids
     * @return the service key ids
     */
    @Getter(onMethod = @__(@FilterParameter("service_key_guid")))
    private final List<String> serviceKeyIds;

    /**
     * The service plan ids
     *
     * @param servicePlanIds the service plan ids
     * @return the service plan ids
     */
    @Getter(onMethod = @__(@FilterParameter("service_plan_guid")))
    private final List<String> servicePlanIds;

    /**
     * The space ids
     *
     * @param spaceIds the space ids
     * @return the space ids
     */
    @Getter(onMethod = @__(@FilterParameter("space_guid")))
    private final List<String> spaceIds;

    @Builder
    ListServiceInstancesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                @Singular List<String> gatewayNames,
                                @Singular List<String> names,
                                @Singular List<String> organizationIds,
                                @Singular List<String> serviceBindingIds,
                                @Singular List<String> serviceKeyIds,
                                @Singular List<String> servicePlanIds,
                                @Singular List<String> spaceIds) {
        super(orderDirection, page, resultsPerPage);
        this.gatewayNames = gatewayNames;
        this.names = names;
        this.organizationIds = organizationIds;
        this.serviceKeyIds = serviceKeyIds;
        this.serviceBindingIds = serviceBindingIds;
        this.servicePlanIds = servicePlanIds;
        this.spaceIds = spaceIds;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
