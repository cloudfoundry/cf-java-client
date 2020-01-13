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

package org.cloudfoundry.client.v2.routemappings;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Route Mappings V2 Client API
 */
public interface RouteMappings {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/routes_mapping/mapping_an_app_and_a_route.html">Creating a Route Mapping</a> request
     *
     * @param request the Creating a Route Mapping request
     * @return the response from the Creating a Route Mapping request
     */
    Mono<CreateRouteMappingResponse> create(CreateRouteMappingRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/routes_mapping/delete_a_particular_route_mapping.html">Deleting a Route Mapping</a> request
     *
     * @param request the Delete a Route Mapping request
     * @return the response from deleting a Route Mapping request
     */
    Mono<DeleteRouteMappingResponse> delete(DeleteRouteMappingRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/routes_mapping/retrieve_a_particular_route_mapping.html">Retrieve a Particular Route Mapping</a> request
     *
     * @param request the Get a Particular Route Mapping request
     * @return the response from the Get a Particular Route Mapping request
     */
    Mono<GetRouteMappingResponse> get(GetRouteMappingRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/routes_mapping/list_all_route_mappings.html">List Route Mappings</a> request
     *
     * @param request the List Route Mappings request
     * @return the response from the List Route Mappings request
     */
    Mono<ListRouteMappingsResponse> list(ListRouteMappingsRequest request);

}
