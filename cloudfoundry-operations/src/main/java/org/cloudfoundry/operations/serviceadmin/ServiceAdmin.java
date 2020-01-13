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

package org.cloudfoundry.operations.serviceadmin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Admin API
 */
public interface ServiceAdmin {

    /**
     * Create a new service broker
     *
     * @param request The Create Service Broker request
     * @return a completion indicator
     */
    Mono<Void> create(CreateServiceBrokerRequest request);

    /**
     * Deletes a service broker
     *
     * @param request the Delete Service Broker request
     * @return a completion indicator
     */
    Mono<Void> delete(DeleteServiceBrokerRequest request);

    /**
     * Disable service access
     *
     * @param request the Disable Service Access request
     * @return a completion indicator
     */
    Mono<Void> disableServiceAccess(DisableServiceAccessRequest request);

    /**
     * Enable service access
     *
     * @param request the Enable Service Access request
     * @return a completion indicator
     */
    Mono<Void> enableServiceAccess(EnableServiceAccessRequest request);

    /**
     * Lists the service brokers
     *
     * @return the service brokers
     */
    Flux<ServiceBroker> list();

    /**
     * Lists the service access settings
     *
     * @param request the List Service Access Settings request
     * @return the service access settings
     */
    Flux<ServiceAccess> listServiceAccessSettings(ListServiceAccessSettingsRequest request);

    /**
     * Update an existing service broker
     *
     * @param request The Update Service Broker request
     * @return a completion indicator
     */

    Mono<Void> update(UpdateServiceBrokerRequest request);

}
