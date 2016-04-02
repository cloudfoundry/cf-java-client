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

package org.cloudfoundry.operations.domains;

import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Domains Operations API
 */
public interface Domains {

    /**
     * Create a new domain
     *
     * @param request The Create Domain request
     * @return a completion indicator
     */
    Mono<Void> create(CreateDomainRequest request);

    /**
     * Create a new shared domain
     *
     * @param request the Create Shared Domain request
     * @return a completion indicator
     */
    Mono<Void> createShared(CreateSharedDomainRequest request);

    /**
     * Lists the domains
     *
     * @param request the List Routes request
     * @return the routes and the applications bound to those routes
     */
    Flux<Domain> list(ListDomainsRequest request);

}
