/*
 * Copyright 2013-2026 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceroutebindings;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Service Route Bindings operation
 * 
 * see https://v3-apidocs.cloudfoundry.org/index.html#list-service-route-bindings
 */
@Value.Immutable
abstract class _ListServiceRouteBindingsRequest extends PaginatedRequest {

    /**
     * The ids
     */
    @FilterParameter("guids")
    abstract List<String> getIds();

    /**
     * A query string containing a list of label selector requirements
     */
    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();

    /**
     * The route ids
     */
    @FilterParameter("route_guids")
    abstract List<String> getRouteIds();

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
}
