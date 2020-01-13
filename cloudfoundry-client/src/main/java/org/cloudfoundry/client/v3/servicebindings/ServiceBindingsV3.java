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

package org.cloudfoundry.client.v3.servicebindings;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Bindings V3 Client API
 */
public interface ServiceBindingsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#create-a-service-binding">Create a service binding</a> request
     *
     * @param request the Create Service Binding request
     * @return the response from the Create Service Binding request
     */
    Mono<CreateServiceBindingResponse> create(CreateServiceBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#delete-a-service-binding">Delete a service binding</a> request
     *
     * @param request the Delete Service Binding request
     * @return the response from the Delete Service Binding request
     */
    Mono<Void> delete(DeleteServiceBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-a-service-binding">Get a Service Binding</a> request
     *
     * @param request the Get Service Binding request
     * @return the response from the Get Service Binding request
     */
    Mono<GetServiceBindingResponse> get(GetServiceBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#list-service-bindings">List service bindings</a> request
     *
     * @param request the List Service Bindings request
     * @return the response from the List Service Bindings request
     */
    Mono<ListServiceBindingsResponse> list(ListServiceBindingsRequest request);

}
