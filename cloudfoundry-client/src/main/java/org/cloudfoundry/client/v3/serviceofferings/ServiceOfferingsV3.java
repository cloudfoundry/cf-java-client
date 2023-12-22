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

package org.cloudfoundry.client.v3.serviceofferings;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Offerings V3 Client API
 */
public interface ServiceOfferingsV3 {

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#delete-a-service-offering">Delete a service offering</a> request
     *
     * @param request the Delete a Service Offering request
     * @return the response from the Delete a Service Offering request
     */
    Mono<Void> delete(DeleteServiceOfferingRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#get-a-service-offering">Get a service offering</a> request
     *
     * @param request the Get a Service Offering request
     * @return the response from the Get a Service Offering request
     */
    Mono<GetServiceOfferingResponse> get(GetServiceOfferingRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#list-service-offerings">List service offerings</a> request
     *
     * @param request the List Service Offerings request
     * @return the response from the List Service Offerings request
     */
    Mono<ListServiceOfferingsResponse> list(ListServiceOfferingsRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#update-a-service-offering">Update a service offering</a> request
     *
     * @param request the Update a Service Offering request
     * @return the response from the Update a Service Offering request
     */
    Mono<UpdateServiceOfferingResponse> update(UpdateServiceOfferingRequest request);
}
