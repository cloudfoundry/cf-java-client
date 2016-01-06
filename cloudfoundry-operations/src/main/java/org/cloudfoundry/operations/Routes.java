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

package org.cloudfoundry.operations;

import org.reactivestreams.Publisher;

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
    Publisher<Boolean> check(CheckRouteRequest request);

    /**
     * Lists the routes and the applications bound to those routes
     *
     * @param request the List Routes request
     * @return the routes and the applications bound to those routes
     */
    Publisher<Route> list(ListRoutesRequest request);

}
