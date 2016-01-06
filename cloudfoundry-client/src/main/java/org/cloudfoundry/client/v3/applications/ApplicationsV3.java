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

package org.cloudfoundry.client.v3.applications;

import org.reactivestreams.Publisher;

/**
 * Main entry point to the Cloud Foundry Applications V3 Client API
 */
public interface ApplicationsV3 {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/assigning_a_droplet_as_a_an_apps_current_droplet.html">Assign Application Droplet</a> request
     *
     * @param request the Assign Application Droplet request
     * @return the response from the Assign Application Droplet request
     */
    Publisher<AssignApplicationDropletResponse> assignDroplet(AssignApplicationDropletRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_(experimental)/create_an_app.html">Create Application</a> request
     *
     * @param request the Create Application request
     * @return the response from the Create Application request
     */
    Publisher<CreateApplicationResponse> create(CreateApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_(experimental)/delete_an_app.html">Delete Application</a> request
     *
     * @param request the Delete Application request
     * @return the response from the Delete Application request
     */
    Publisher<Void> delete(DeleteApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/terminating_a_process_instance_from_its_app.html">Delete Application Process</a> request
     *
     * @param request the Delete Application Process Instance request
     * @return the response from the Delete Application Process Instance request
     */
    Publisher<Void> deleteInstance(DeleteApplicationInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/215/apps_(experimental)/get_an_app.html">Get Application</a> request
     *
     * @param request the Get Application request
     * @return the response from the Get Application request
     */
    Publisher<GetApplicationResponse> get(GetApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/get_the_env_for_an_app.html">Get Application Environment</a> request
     *
     * @param request the Get Application Environment request
     * @return the response from the Get Application Environment request
     */
    Publisher<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/get_a_process_from_an_app.html">Get Application Process</a> request
     *
     * @param request the Get Application Process request
     * @return the response from the Get Application Process request
     */
    Publisher<GetApplicationProcessResponse> getProcess(GetApplicationProcessRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_(experimental)/list_all_apps.html">List Applications</a> request
     *
     * @param request the List Applications request
     * @return the response from the List Applications request
     */
    Publisher<ListApplicationsResponse> list(ListApplicationsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/215/app_droplets_%28experimental%29/list_droplets.html">List Application Droplets</a> request
     *
     * @param request the List Application Droplets request
     * @return the response from the List Application Droplets request
     */
    Publisher<ListApplicationDropletsResponse> listDroplets(ListApplicationDropletsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/list_associated_packages.html">List Application Packages</a> request
     *
     * @param request the List Application Packages request
     * @return the response from the List Application Packages request
     */
    Publisher<ListApplicationPackagesResponse> listPackages(ListApplicationPackagesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/list_associated_processes.html">List Application Processes</a> request
     *
     * @param request the List Application Processes request
     * @return the response from the List Application Processes request
     */
    Publisher<ListApplicationProcessesResponse> listProcesses(ListApplicationProcessesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/app_routes_(experimental)/list_routes.html">List Application Routes</a> request
     *
     * @param request the List Application Routes request
     * @return the response from the List Application Routes request
     */
    Publisher<ListApplicationRoutesResponse> listRoutes(ListApplicationRoutesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/app_routes_%28experimental%29/map_a_route.html">Map Application Route</a> request
     *
     * @param request the Map Application Route request
     * @return the response from the Map Application Route request
     */
    Publisher<Void> mapRoute(MapApplicationRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/scaling_a_process_from_its_app.html">Scale Application</a> request
     *
     * @param request the Scale Application request
     * @return the response from the Scale Application request
     */
    Publisher<ScaleApplicationResponse> scale(ScaleApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_(experimental)/starting_an_app.html">Start Application</a> request
     *
     * @param request the Start Application request
     * @return the response from the Start Application request
     */
    Publisher<StartApplicationResponse> start(StartApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/stopping_an_app.html">Stop Application</a> request
     *
     * @param request the Stop Application request
     * @return the response from the Stop Application request
     */
    Publisher<StopApplicationResponse> stop(StopApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/app_routes_%28experimental%29/unmap_a_route.html">Unmap Application Route</a> request
     *
     * @param request the Unmap Application Route request
     * @return the response from the Unmap Application Route request
     */
    Publisher<Void> unmapRoute(UnmapApplicationRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps_%28experimental%29/updating_an_app.html">Update Application</a> request
     *
     * @param request the Update Application request
     * @return the response from the Update Application request
     */
    Publisher<UpdateApplicationResponse> update(UpdateApplicationRequest request);

}
