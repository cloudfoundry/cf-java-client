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

package org.cloudfoundry.client.v2.servicebindings;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Bindings V2 Client API
 */
public interface ServiceBindingsV2 {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_bindings/create_a_service_binding.html">Create Service Binding</a> request
     *
     * @param request the Create Service Binding request
     * @return the response from the Create Service Binding request
     */
    Mono<CreateServiceBindingResponse> create(CreateServiceBindingRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_bindings/delete_a_particular_service_binding.html">Delete the Service Binding</a> request
     *
     * @param request the Delete Service Binding request
     * @return the response from the Delete Service Binding request
     */
    Mono<DeleteServiceBindingResponse> delete(DeleteServiceBindingRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_bindings/retrieve_a_particular_service_binding.html">Retrieve a Particular Service Binding</a> request
     *
     * @param request the Get Service Binding request
     * @return the response from the Get Service Binding request
     */
    Mono<GetServiceBindingResponse> get(GetServiceBindingRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_bindings/retrieve_a_particular_service_binding_parameters.html">Retrieve a Particular Service Binding's Parameters</a>
     * request
     *
     * @param request the Get Parameters request
     * @return the response from the Get Parameters request
     */
    Mono<GetServiceBindingParametersResponse> getParameters(GetServiceBindingParametersRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_bindings/list_all_service_bindings.html">List all Service Bindings</a> request
     *
     * @param request the List Service Bindings request
     * @return the response from the List Service Bindings request
     */
    Mono<ListServiceBindingsResponse> list(ListServiceBindingsRequest request);

}
