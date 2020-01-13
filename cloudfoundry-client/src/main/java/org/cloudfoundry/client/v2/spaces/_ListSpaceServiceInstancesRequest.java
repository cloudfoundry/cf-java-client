/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List all Service Instances for the Space operation
 */
@Value.Immutable
abstract class _ListSpaceServiceInstancesRequest extends PaginatedRequest {

    /**
     * The gateway names
     */
    @FilterParameter("gateway_name")
    @Nullable
    abstract List<String> getGatewayNames();

    /**
     * The names
     */
    @FilterParameter("name")
    @Nullable
    abstract List<String> getNames();

    /**
     * The organization ids
     */
    @FilterParameter("organization_guid")
    @Nullable
    abstract List<String> getOrganizationIds();

    /**
     * The return user provided service instances
     */
    @Nullable
    @QueryParameter("return_user_provided_service_instances")
    abstract Boolean getReturnUserProvidedServiceInstances();

    /**
     * The service binding ids
     */
    @FilterParameter("service_binding_guid")
    @Nullable
    abstract List<String> getServiceBindingIds();

    /**
     * The service key ids
     */
    @FilterParameter("service_key_guid")
    @Nullable
    abstract List<String> getServiceKeyIds();

    /**
     * The service plan ids
     */
    @FilterParameter("service_plan_guid")
    @Nullable
    abstract List<String> getServicePlanIds();

    /**
     * The space id
     */
    @JsonIgnore
    abstract String getSpaceId();

}
