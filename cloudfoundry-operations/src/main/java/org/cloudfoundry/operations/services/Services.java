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
     * Create a service instance
     * 
     * @param request the create service instance request
     * @return a completion indicator
     */
    Mono<Void> createInstance(CreateServiceInstanceRequest request);
    
    /**
     * List the service instances in the targeted space
     *
     * @return the service instances
     */
    Flux<ServiceInstance> listInstances();

    /**
     * Unbind a service instance from an application
     *
     * @param request the unbind service instance request
     * @return a completion indicator
     */
    Mono<Void> unbind(UnbindServiceInstanceRequest request);
}
