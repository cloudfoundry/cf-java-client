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

package org.cloudfoundry.client.v2.servicebrokers;

import reactor.Mono;

/**
 * Main entry point to the Cloud Foundry Service Brokers Client API
 */
public interface ServiceBrokers {

    /**
     * Makes the <a href="apidocs.cloudfoundry.org/214/service_brokers/create_a_service_broker.html">Create Service Broker</a> request
     *
     * @param request the Create Service Broker request
     * @return the response from the Create Service Broker request
     */
    Mono<CreateServiceBrokerResponse> create(CreateServiceBrokerRequest request);

}
