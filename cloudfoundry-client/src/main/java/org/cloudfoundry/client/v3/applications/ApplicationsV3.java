/*
 * Copyright 2013-2017 the original author or authors.
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

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Applications V3 Client API
 */
public interface ApplicationsV3 {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/assigning_a_droplet_as_a_an_apps_current_droplet.html">Assign Application Droplet</a> request
     *
     * @param request the Assign Application Droplet request
     * @return the response from the Assign Application Droplet request
     */
    Mono<AssignApplicationDropletResponse> assignDroplet(AssignApplicationDropletRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#cancel-an-app-39-s-task">Cancel Application Task</a> request
     *
     * @param request the Cancel Application Task request
     * @return the response from the Cancel Application Task request
     */
    Mono<CancelApplicationTaskResponse> cancelTask(CancelApplicationTaskRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/create_an_app.html">Create Application</a> request
     *
     * @param request the Create Application request
     * @return the response from the Create Application request
     */
    Mono<CreateApplicationResponse> create(CreateApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/delete_an_app.html">Delete Application</a> request
     *
     * @param request the Delete Application request
     * @return the response from the Delete Application request
     */
    Mono<Void> delete(DeleteApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/get_an_app.html">Get Application</a> request
     *
     * @param request the Get Application request
     * @return the response from the Get Application request
     */
    Mono<GetApplicationResponse> get(GetApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/get_the_env_for_an_app.html">Get Application Environment</a> request
     *
     * @param request the Get Application Environment request
     * @return the response from the Get Application Environment request
     */
    Mono<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/get_a_process_from_an_app.html">Get Application Process</a> request
     *
     * @param request the Get Application Process request
     * @return the response from the Get Application Process request
     */
    Mono<GetApplicationProcessResponse> getProcess(GetApplicationProcessRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-stats-for-a-process-for-an-app">Get Statistics for a Process for an Application</a> request
     *
     * @param request the Get Statistics for a Process for an Application request
     * @return the response from the Get Statistics for a Process for an Application request
     */
    Mono<GetApplicationProcessStatisticsResponse> getProcessStatistics(GetApplicationProcessStatisticsRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#get-an-app-39-s-task">Get Application Task</a> request
     *
     * @param request the Get Application Task request
     * @return the response from the Get Application Task request
     */
    Mono<GetApplicationTaskResponse> getTask(GetApplicationTaskRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/list_all_apps.html">List Applications</a> request
     *
     * @param request the List Applications request
     * @return the response from the List Applications request
     */
    Mono<ListApplicationsResponse> list(ListApplicationsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/app_droplets_(experimental)/list_droplets.html">List Application Droplets</a> request
     *
     * @param request the List Application Droplets request
     * @return the response from the List Application Droplets request
     */
    Mono<ListApplicationDropletsResponse> listDroplets(ListApplicationDropletsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/list_associated_packages.html">List Application Packages</a> request
     *
     * @param request the List Application Packages request
     * @return the response from the List Application Packages request
     */
    Mono<ListApplicationPackagesResponse> listPackages(ListApplicationPackagesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/list_associated_processes.html">List Application Processes</a> request
     *
     * @param request the List Application Processes request
     * @return the response from the List Application Processes request
     */
    Mono<ListApplicationProcessesResponse> listProcesses(ListApplicationProcessesRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#list-an-app-39-s-tasks">List Application Tasks</a> request
     *
     * @param request the List Application Tasks request
     * @return the response from the List Application Tasks request
     */
    Mono<ListApplicationTasksResponse> listTasks(ListApplicationTasksRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/scaling_a_process_from_its_app.html">Scale Application</a> request
     *
     * @param request the Scale Application request
     * @return the response from the Scale Application request
     */
    Mono<ScaleApplicationResponse> scale(ScaleApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/starting_an_app.html">Start Application</a> request
     *
     * @param request the Start Application request
     * @return the response from the Start Application request
     */
    Mono<StartApplicationResponse> start(StartApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/stopping_an_app.html">Stop Application</a> request
     *
     * @param request the Stop Application request
     * @return the response from the Stop Application request
     */
    Mono<StopApplicationResponse> stop(StopApplicationRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/terminating_a_process_instance_from_its_app.html">Delete Application Process</a> request
     *
     * @param request the Delete Application Process Instance request
     * @return the response from the Delete Application Process Instance request
     */
    Mono<Void> terminateInstance(TerminateApplicationInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/apps_(experimental)/updating_an_app.html">Update Application</a> request
     *
     * @param request the Update Application request
     * @return the response from the Update Application request
     */
    Mono<UpdateApplicationResponse> update(UpdateApplicationRequest request);

}
