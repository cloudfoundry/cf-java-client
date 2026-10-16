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

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Route Bindings V3 Client API
 *
 * see https://v3-apidocs.cloudfoundry.org/index.html#service-route-binding
 */
public interface ServiceRouteBindingsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#create-a-service-route-binding">Create a service route binding</a> request
     *
     * @param request the Create Service Route Binding request
     * @return the response from the Create Service Route Binding request
     */
    Mono<CreateServiceRouteBindingResponse> create(CreateServiceRouteBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#delete-a-service-route-binding">Delete a service route binding</a> request
     *
     * @param request the Delete Service Route Binding request
     * @return the response from the Delete Service Route Binding request
     */
    Mono<DeleteServiceRouteBindingResponse> delete(DeleteServiceRouteBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#get-a-service-route-binding">Get a service route binding</a> request
     *
     * @param request the Get Service Route Binding request
     * @return the response from the Get Service Route Binding request
     */
    Mono<GetServiceRouteBindingResponse> get(GetServiceRouteBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#get-parameters-for-a-service-route-binding">Get parameters for a service route binding</a> request
     *
     * @param request the Get Service Route Binding Parameters request
     * @return the response from the Get Service Route Binding Parameters request
     */
    Mono<GetServiceRouteBindingParametersResponse> getParameters(
            GetServiceRouteBindingParametersRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#list-service-route-bindings">List service route bindings</a> request
     *
     * @param request the List Service Route Bindings request
     * @return the response from the List Service Route Bindings request
     */
    Mono<ListServiceRouteBindingsResponse> list(ListServiceRouteBindingsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/#update-a-service-route-binding">Update a service route binding</a> request
     *
     * @param request the Update Service Route Binding request
     * @return the response from the Update Service Route Binding request
     */
    Mono<UpdateServiceRouteBindingResponse> update(UpdateServiceRouteBindingRequest request);
}
