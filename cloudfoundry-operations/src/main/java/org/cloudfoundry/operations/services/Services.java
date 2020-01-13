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

package org.cloudfoundry.operations.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Services Operations API
 */
public interface Services {

    /**
     * Bind a service instance to an application
     *
     * @param request the bind service instance request
     * @return a completion indicator
     */
    Mono<Void> bind(BindServiceInstanceRequest request);

    /**
     * Bind a service instance to a route
     *
     * @param request the bind service instance to a route request
     * @return a completion indicator
     */
    Mono<Void> bindRoute(BindRouteServiceInstanceRequest request);

    /**
     * Create a service instance
     *
     * @param request the create service instance request
     * @return a completion indicator
     */
    Mono<Void> createInstance(CreateServiceInstanceRequest request);

    /**
     * Create a service key
     *
     * @param request the create service key request
     * @return a completion indicator
     */
    Mono<Void> createServiceKey(CreateServiceKeyRequest request);

    /**
     * Create a user provided service instance
     *
     * @param request the create user provided service instance request
     * @return a completion indicator
     */
    Mono<Void> createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest request);

    /**
     * Delete a service instance
     *
     * @param request the delete service instance request
     * @return a completion indicator
     */
    Mono<Void> deleteInstance(DeleteServiceInstanceRequest request);

    /**
     * Delete a service key
     *
     * @param request the delete service key request
     * @return a completion indicator
     */
    Mono<Void> deleteServiceKey(DeleteServiceKeyRequest request);

    /**
     * Get a service instance
     *
     * @param request the get service instance request
     * @return the service instance
     */
    Mono<ServiceInstance> getInstance(GetServiceInstanceRequest request);

    /**
     * Get a service key
     *
     * @param request the get service key request
     * @return the service key
     */
    Mono<ServiceKey> getServiceKey(GetServiceKeyRequest request);

    /**
     * List the service instances in the targeted space
     *
     * @return the service instances
     */
    Flux<ServiceInstanceSummary> listInstances();

    /**
     * List the service keys for a service instance
     *
     * @param request the list service keys request
     * @return the service keys
     */
    Flux<ServiceKey> listServiceKeys(ListServiceKeysRequest request);

    /**
     * List available services offerings in the marketplace
     *
     * @param request The list service offerings request
     * @return the service offerings
     */
    Flux<ServiceOffering> listServiceOfferings(ListServiceOfferingsRequest request);

    /**
     * Rename a service instance
     *
     * @param request the rename service instance request
     * @return a completion indicator
     */
    Mono<Void> renameInstance(RenameServiceInstanceRequest request);

    /**
     * Unbind a service instance from an application
     *
     * @param request the unbind service instance request
     * @return a completion indicator
     */
    Mono<Void> unbind(UnbindServiceInstanceRequest request);

    /**
     * Unbind a service instance from a route
     *
     * @param request the unbind service instance from a route request
     * @return a completion indicator
     */
    Mono<Void> unbindRoute(UnbindRouteServiceInstanceRequest request);

    /**
     * Update a service instance
     *
     * @param request the update service instance request
     * @return a completion indicator
     */
    Mono<Void> updateInstance(UpdateServiceInstanceRequest request);

    /**
     * Update a user provided service instance
     *
     * @param request the update user provided service instance request
     * @return a completion indicator
     */
    Mono<Void> updateUserProvidedInstance(UpdateUserProvidedServiceInstanceRequest request);

}
