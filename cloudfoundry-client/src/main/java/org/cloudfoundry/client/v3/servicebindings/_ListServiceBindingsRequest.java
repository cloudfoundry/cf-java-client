/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.servicebindings;


import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Service Bindings operation.
 */
@Value.Immutable
abstract class _ListServiceBindingsRequest extends PaginatedRequest {

    /**
     * The names
     */
    @FilterParameter("names")
    abstract List<String> getNames();

    /**
     * The service instance ids
     */
    @FilterParameter("service_instance_guids")
    abstract List<String> getServiceInstanceIds();

    /**
     * The service instance names
     */
    @FilterParameter("service_instance_names")
    abstract List<String> getServiceInstanceNames();

    /**
     * The application ids
     */
    @FilterParameter("app_guids")
    abstract List<String> getApplicationIds();

    /**
     * The application names
     */
    @FilterParameter("app_names")
    abstract List<String> getAppNames();

    /**
     * The service plan ids
     */
    @FilterParameter("service_plan_guids")
    abstract List<String> getServicePlanIds();

    /**
     * The service plan names
     */
    @FilterParameter("service_plan_names")
    abstract List<String> getServicePlanNames();

    /**
     * The service offering ids
     */
    @FilterParameter("service_offering_guids")
    abstract List<String> getServiceOfferingIds();

    /**
     * The service offering names
     */
    @FilterParameter("service_offering_names")
    abstract List<String> getServiceOfferingNames();

    /**
     * The type
     */
    @FilterParameter("type")
    @Nullable
    abstract ServiceBindingType getType();

    /**
     * The ids
     */
    @FilterParameter("guids")
    abstract List<String> getIds();
}
