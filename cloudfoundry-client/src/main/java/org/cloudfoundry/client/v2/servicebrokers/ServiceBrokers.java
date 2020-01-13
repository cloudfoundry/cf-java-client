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

package org.cloudfoundry.client.v2.servicebrokers;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Brokers Client API
 */
public interface ServiceBrokers {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_brokers/create_a_service_broker.html">Create Service Broker</a> request
     *
     * @param request the Create Service Broker request
     * @return the response from the Create Service Broker request
     */
    Mono<CreateServiceBrokerResponse> create(CreateServiceBrokerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_brokers/delete_a_particular_service_broker.html">Delete the Service Broker</a> request
     *
     * @param request the Delete Service Broker request
     * @return the response from the Delete Service Broker request
     */
    Mono<Void> delete(DeleteServiceBrokerRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_brokers/retrieve_a_particular_service_broker.html">Retrieve a Particular Service Broker</a> request
     *
     * @param request the Get Service Broker request
     * @return the response from the Get Service Broker request
     */
    Mono<GetServiceBrokerResponse> get(GetServiceBrokerRequest request);


    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_brokers/list_all_service_brokers.html">List all Service Brokers</a> request
     *
     * @param request the List Service Brokers request
     * @return the response from the List Service Brokers request
     */
    Mono<ListServiceBrokersResponse> list(ListServiceBrokersRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_brokers/update_a_service_broker.html">Update Service Broker</a> request
     *
     * @param request the Update Service Broker request
     * @return the response from the Update Service Broker request
     */
    Mono<UpdateServiceBrokerResponse> update(UpdateServiceBrokerRequest request);

}
