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

package org.cloudfoundry.operations.routes;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Routes Operations API
 */
public interface Routes {

    /**
     * Checks whether a route exists
     *
     * @param request the Check Route request
     * @return whether the route exists
     */
    Mono<Boolean> check(CheckRouteRequest request);

    /**
     * Create a new route
     *
     * @param request The Create Route request
     * @return a completion indicator
     */
    Mono<Void> create(CreateRouteRequest request);

    /**
     * Delete orphaned routes.
     *
     * Warning: this operation is not atomic and may delete routes which are in the process of being associated with applications.
     * @return a completion indicator
     */
    Mono<Void> deleteOrphanedRoutes();

    /**
     * Lists the routes and the applications bound to those routes
     *
     * @param request the List Routes request
     * @return the routes and the applications bound to those routes
     */
    Publisher<Route> list(ListRoutesRequest request);

    /**
     * Add a URL route to an application
     *
     * @param request the Map Route request
     * @return a completion indicator
     */
    Mono<Void> map(MapRouteRequest request);

    /**
     * Remove a URL route from an application
     *
     * @param request the Unmap Route request
     * @return a completion indicator
     */
    Mono<Void> unmap(UnmapRouteRequest request);

}
