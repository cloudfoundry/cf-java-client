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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Service Keys for the Service Instance operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServiceInstanceServiceKeysRequest extends PaginatedRequest implements Validatable {

    /**
     * The names of the gateway
     *
     * @param gatewayNames the names of the gateway
     * @return the names of the gateway
     */
    @Getter(onMethod = @__(@InFilterParameter("gateway_name")))
    private final List<String> gatewayNames;

    /**
     * The names of the service keys
     *
     * @param names the names of the service keys
     * @return the names of the service keys
     */
    @Getter(onMethod = @__(@InFilterParameter("name")))
    private final List<String> names;

    /**
     * The ids of the organizations
     *
     * @param organizationIds the ids of the organizations
     * @return the ids of the organizations
     */
    @Getter(onMethod = @__(@InFilterParameter("organization_guid")))
    private final List<String> organizationIds;

    /**
     * The ids of the service bindings
     *
     * @param serviceBindingIds the ids of the service bindings
     * @return the ids of the service bindings
     */
    @Getter(onMethod = @__(@InFilterParameter("service_binding_guid")))
    private final List<String> serviceBindingIds;

    /**
     * The service instance id
     *
     * @param serviceInstanceId the service instance id
     * @return the service instance id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String serviceInstanceId;

    /**
     * The ids of the service keys
     *
     * @param serviceKeyIds the ids of the service keys
     * @return the ids of the service keys
     */
    @Getter(onMethod = @__(@InFilterParameter("service_key_guid")))
    private final List<String> serviceKeyIds;

    /**
     * The ids of the service plans
     *
     * @param servicePlanIds the ids of the service plans
     * @return the ids of the service plans
     */
    @Getter(onMethod = @__(@InFilterParameter("service_plan_guid")))
    private final List<String> servicePlanIds;

    /**
     * The ids of the spaces
     *
     * @param spaceIds the ids of the spaces
     * @return the ids of the spaces
     */
    @Getter(onMethod = @__(@InFilterParameter("space_guid")))
    private final List<String> spaceIds;


    @Builder
    ListServiceInstanceServiceKeysRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                          @Singular List<String> gatewayNames,
                                          @Singular List<String> names,
                                          @Singular List<String> organizationIds,
                                          @Singular List<String> serviceBindingIds,
                                          String serviceInstanceId,
                                          @Singular List<String> serviceKeyIds,
                                          @Singular List<String> servicePlanIds,
                                          @Singular List<String> spaceIds) {

        super(orderDirection, page, resultsPerPage);
        this.gatewayNames = gatewayNames;
        this.names = names;
        this.organizationIds = organizationIds;
        this.serviceBindingIds = serviceBindingIds;
        this.serviceInstanceId = serviceInstanceId;
        this.serviceKeyIds = serviceKeyIds;
        this.servicePlanIds = servicePlanIds;
        this.spaceIds = spaceIds;
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
