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

package org.cloudfoundry.client.v2.servicebindings;

import reactor.Mono;

/**
 * Main entry point to the Cloud Foundry Service Bindings Client API
 */
public interface ServiceBindings {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/223/service_bindings/create_a_service_binding.html">Create Service Binding</a> request
     *
     * @param request the Create Service Binding request
     * @return the response from the Create Service Binding request
     */
    Mono<CreateServiceBindingResponse> create(CreateServiceBindingRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/223/service_bindings/retrieve_a_particular_service_binding.html">Retrieve a Particular Service Binding</a> request
     *
     * @param request the Get Application request
     * @return the response from the Get Application request
     */
    Mono<GetServiceBindingResponse> get(GetServiceBindingRequest request);

}
