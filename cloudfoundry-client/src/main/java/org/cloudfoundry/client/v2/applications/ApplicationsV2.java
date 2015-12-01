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

package org.cloudfoundry.client.v2.applications;

import org.reactivestreams.Publisher;

/**
 * Main entry point to the Cloud Foundry Applications V2 Client API
 */
public interface ApplicationsV2 {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/associate_route_with_the_app.html">Associate Route
     * with the Application</a> request
     *
     * @param request the Associate Route with the Application request
     * @return the response from the Associate Route with the Application request
     */
    Publisher<AssociateApplicationRouteResponse> associateRoute(AssociateApplicationRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/copy_the_app_bits_for_an_app.html">Copy the app bits
     * for an Application</a> request
     *
     * @param request the Copy Application request
     * @return the response from the Copy Application request
     */
    Publisher<CopyApplicationResponse> copy(CopyApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/creating_an_app.html">Creating an App</a> request
     *
     * @param request the Create Application request
     * @return the response from the Create Application request
     */
    Publisher<CreateApplicationResponse> create(CreateApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/delete_a_particular_app.html">Delete the App</a>
     * request
     *
     * @param request the Delete Application request
     * @return the response from the Delete Application request
     */
    Publisher<Void> delete(DeleteApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/get_the_env_for_an_app.html">Get the env for an
     * App</a> request
     *
     * @param request the Get Application Environment request
     * @return the response from the Get Application Environment request
     */
    Publisher<ApplicationEnvironmentResponse> environment(ApplicationEnvironmentRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/retrieve_a_particular_app.html">Retrieve a Particular
     * App</a> request
     *
     * @param request the Get Application request
     * @return the response from the Get Application request
     */
    Publisher<GetApplicationResponse> get(GetApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/get_the_instance_information_for_a_started_app.html">
     * Get the instance information for a STARTED App</a> request
     *
     * @param request the Get Instance Information request
     * @return the response from the Get Instance Information request
     */
    Publisher<ApplicationInstancesResponse> instances(ApplicationInstancesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/list_all_apps.html">List all Apps</a> request
     *
     * @param request the List Applications request
     * @return the response from the List Applications request
     */
    Publisher<ListApplicationsResponse> list(ListApplicationsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/list_all_routes_for_the_app.html">List all Routes for
     * the Application</a> request
     *
     * @param request the List all Routes for the Application request
     * @return the response from the List all Routes for the Application request
     */
    Publisher<ListApplicationRoutesResponse> listRoutes(ListApplicationRoutesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/list_all_service_bindings_for_the_app.html">List all
     * Service Bindings for the App</a> request
     *
     * @param request the List Service Bindings request
     * @return the response from the List Service Bindings request
     */
    Publisher<ListApplicationServiceBindingsResponse> listServiceBindings(ListApplicationServiceBindingsRequest
                                                                                  request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/223/apps/remove_route_from_the_app.html">Remove Route from the
     * Application</a> request
     *
     * @param request the Remove Route from the Application request
     * @return the response from the Remove Route from the Application request
     */
    Publisher<Void> removeRoute(RemoveApplicationRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/223/apps/remove_service_binding_from_the_app.html">Remove
     * Service Binding from the Application</a> request
     *
     * @param request the Remove a Service Binding from an Application request
     * @return the response from the Remove a Service Binding from an Application request
     */
    Publisher<Void> removeServiceBinding(RemoveApplicationServiceBindingRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/restage_an_app.html">Restage an App</a> request
     *
     * @param request the Restage an Application request
     * @return the response from the Restage an Application request
     */
    Publisher<RestageApplicationResponse> restage(RestageApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/get_detailed_stats_for_a_started_app.html">Get
     * detailed stats for a STARTED App</a> request
     *
     * @param request the Get Statistics request
     * @return the response from the Get Statistics request
     */
    Publisher<ApplicationStatisticsResponse> statistics(ApplicationStatisticsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/get_app_summary.html">Get Application Summary</a>
     * request
     *
     * @param request the Get Application Summary request
     * @return the response from the Get Application Summary request
     */
    Publisher<SummaryApplicationResponse> summary(SummaryApplicationRequest request);

    /**
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/apps/terminate_the_running_app_instance_at_the_given_index.html">
     * Terminate Application Instance</a> request
     *
     * @param request the Terminate Application Instance request
     * @return the response form the Terminate Application Instance request
     */
    Publisher<Void> terminateInstance(TerminateApplicationInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/updating_an_app.html">Updating an App</a> request
     *
     * @param request the Update Application request
     * @return the response from the Update Application request
     */
    Publisher<UpdateApplicationResponse> update(UpdateApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/apps/uploads_the_bits_for_an_app.html">Upload the bits for
     * an App</a> request
     *
     * @param request the Upload Application request
     * @return the response from the Upload Application request
     */
    Publisher<UploadApplicationResponse> upload(UploadApplicationRequest request);

}
