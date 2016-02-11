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

package org.cloudfoundry.client.v2.services;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Services Client API
 */
public interface Services {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/services/delete_a_particular_service.html">Delete the Service</a> request
     *
     * @param request the Delete Service request
     * @return the response from the Delete Service request
     */
    Mono<Void> delete(DeleteServiceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/services/retrieve_a_particular_service.html">Retrieve a Particular Service</a> request
     *
     * @param request the Get Service request
     * @return the response from the Get Service request
     */
    Mono<GetServiceResponse> get(GetServiceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/services/list_all_services.html">List Services</a> request
     *
     * @param request the List Services request
     * @return the response from the List Services request
     */
    Mono<ListServicesResponse> list(ListServicesRequest request);

}
