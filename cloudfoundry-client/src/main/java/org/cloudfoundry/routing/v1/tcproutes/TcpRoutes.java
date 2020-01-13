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

package org.cloudfoundry.routing.v1.tcproutes;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the TCP Routes API
 */
public interface TcpRoutes {

    /**
     * Makes the <a href="https://github.com/cloudfoundry-incubator/routing-api/blob/master/docs/api_docs.md#register-tcp-routes">Create TCP Routes</a> request
     *
     * @param request the Create TCP Routes request
     * @return the response to the Create TCP Routes request
     */
    Mono<CreateTcpRoutesResponse> create(CreateTcpRoutesRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry-incubator/routing-api/blob/master/docs/api_docs.md#delete-tcp-routes">Delete TCP Routes</a> request
     *
     * @param request the Delete TCP Routes request
     * @return the response to the Delete TCP Routes request
     */
    Mono<Void> delete(DeleteTcpRoutesRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry-incubator/routing-api/blob/master/docs/api_docs.md#subscribe-to-events-for-tcp-routes">TCP Routes Events</a> request
     *
     * @param request the TCP Routes Events request
     * @return the response to the TCP Routes Events request
     */
    Flux<TcpRouteEvent> events(EventsRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry-incubator/routing-api/blob/master/docs/api_docs.md#list-tcp-routes">List TCP Routes</a> request
     *
     * @param request the List TCP Routes request
     * @return the response to the List TCP Routes request
     */
    Mono<ListTcpRoutesResponse> list(ListTcpRoutesRequest request);

}
