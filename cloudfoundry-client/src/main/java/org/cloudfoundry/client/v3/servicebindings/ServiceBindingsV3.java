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

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Bindings V3 Client API
 */
public interface ServiceBindingsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#create-a-service-credential-binding">Create a service credential binding</a> request
     *
     * @param request the Create Service Binding request
     * @return the response from the Create Service Binding request
     */
    Mono<CreateServiceBindingResponse> create(CreateServiceBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#delete-a-service-credential-binding">Delete a service credential binding</a> request
     *
     * @param request the Delete Service Binding request
     * @return the response from the Delete Service Binding request
     */
    Mono<String> delete(DeleteServiceBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-a-service-credential-binding">Get a service credential binding</a> request
     *
     * @param request the Get Service Binding request
     * @return the response from the Get Service Binding request
     */
    Mono<GetServiceBindingResponse> get(GetServiceBindingRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-a-service-credential-binding-detials">Get a service credential binding details</a> request
     *
     * @param request the Get Service Binding Details request
     * @return the response from the Get Service Binding Details request
     */
    Mono<GetServiceBindingDetailsResponse> getDetails(GetServiceBindingDetailsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-parameters-for-a-service-credential-binding">Get parameters a service credential binding</a> request
     *
     * @param request the Get Service Binding Parameters request
     * @return the response from the Get Service Binding Parameters request
     */
    Mono<GetServiceBindingParametersResponse> getParameters(
            GetServiceBindingParametersRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#list-service-credential-bindings">List service credential bindings</a> request
     *
     * @param request the List Service Bindings request
     * @return the response from the List Service Bindings request
     */
    Mono<ListServiceBindingsResponse> list(ListServiceBindingsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#update-a-service-credential-binding">Update a service credential bindings</a> request
     *
     * @param request the Update Service Bindings request
     * @return the response from the Update Service Bindings request
     */
    Mono<UpdateServiceBindingResponse> update(UpdateServiceBindingRequest request);
}
