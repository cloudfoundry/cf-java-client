/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v2.routes;

import org.reactivestreams.Publisher;

/**
 * Main entry point to the Cloud Foundry Routes V2 Client API
 */
public interface Routes {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/associate_app_with_the_route.html">Associate Application with the Route</a> request
     *
     * @param request the Associate an Application with the Route request
     * @return the response from the Associate an Application with the Route request
     */
    Publisher<AssociateRouteApplicationResponse> associateApplication(AssociateRouteApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/creating_a_route.html">Creating a Route</a> request
     *
     * @param request the Creating a Route request
     * @return the response from the Creating a Route request
     */
    Publisher<CreateRouteResponse> create(CreateRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/delete_a_particular_route.html">Delete a Particular Route</a> request
     *
     * @param request the Delete a Particular Route request
     * @return the response from the Delete a Particular Route request
     */
    Publisher<Void> delete(DeleteRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/check_a_route_exists.html">Check a Route exists</a> request
     *
     * @param request the Check a Route exists request
     * @return the response from the Check a Route exists request
     */
    Publisher<Void> exists(RouteExistsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/retrieve_a_particular_route.html">Retrieve a Particular Route</a> request
     *
     * @param request the Retrieve a Particular Route request
     * @return the response from the Retrieve a Particular Route request
     */
    Publisher<GetRouteResponse> get(GetRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/routes/list_all_routes.html">List all Routes</a> request
     *
     * @param request the List all Applications for the Route request
     * @return the response from the List all Applications for the Route request
     */
    Publisher<ListRoutesResponse> list(ListRoutesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/list_all_apps_for_the_route.html">List all Applications for the Route</a> request
     *
     * @param request the List all Applications for the Route request
     * @return the response from the List all Applications for the Route request
     */
    Publisher<ListRouteApplicationsResponse> listApplications(ListRouteApplicationsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/remove_app_from_the_route.html">Remove Application from the Route</a> request
     *
     * @param request the Remove Application from the Route request
     * @return the response from the Remove Application from the Route request
     */
    Publisher<Void> removeApplication(RemoveRouteApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/226/routes/update_a_route.html">Update a Route</a> request
     *
     * @param request the Update a Route request
     * @return the response from the Update a Route request
     */
    Publisher<UpdateRouteResponse> update(UpdateRouteRequest request);

}
