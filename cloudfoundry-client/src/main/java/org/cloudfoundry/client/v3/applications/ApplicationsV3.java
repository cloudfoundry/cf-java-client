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

package org.cloudfoundry.client.v3.applications;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Applications V3 Client API
 */
public interface ApplicationsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#create-an-app">Create Application</a> request
     *
     * @param request the Create Application request
     * @return the response from the Create Application request
     */
    Mono<CreateApplicationResponse> create(CreateApplicationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#delete-an-app">Delete Application</a> request
     *
     * @param request the Delete Application request
     * @return the response from the Delete Application request
     */
    Mono<String> delete(DeleteApplicationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-an-app">Get Application</a> request
     *
     * @param request the Get Application request
     * @return the response from the Get Application request
     */
    Mono<GetApplicationResponse> get(GetApplicationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-current-droplet">Get Current Droplet</a> request
     *
     * @param request the Get Current Droplet request
     * @return the response from the Get Current Droplet request
     */
    Mono<GetApplicationCurrentDropletResponse> getCurrentDroplet(GetApplicationCurrentDropletRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-current-droplet-association-for-an-app">Get Current Droplet Relationship</a> request
     *
     * @param request the Get Current Droplet Relationship request
     * @return the response from the Get Current Droplet Relationship request
     */
    Mono<GetApplicationCurrentDropletRelationshipResponse> getCurrentDropletRelationship(GetApplicationCurrentDropletRelationshipRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-environment-for-an-app">Get Application Environment</a> request
     *
     * @param request the Get Application Environment request
     * @return the response from the Get Application Environment request
     */
    Mono<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#get-environment-variables-for-an-app">Get Application Environment Variables</a> request
     *
     * @param request the Get Application Environment Variables request
     * @return the response from the Get Application Environment Variables request
     */
    Mono<GetApplicationEnvironmentVariablesResponse> getEnvironmentVariables(GetApplicationEnvironmentVariablesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#get-a-process">Get Application Process</a> request
     *
     * @param request the Get Application Process request
     * @return the response from the Get Application Process request
     */
    Mono<GetApplicationProcessResponse> getProcess(GetApplicationProcessRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#get-stats-for-a-process">Get Statistics for a Process for an Application</a> request
     *
     * @param request the Get Statistics for a Process for an Application request
     * @return the response from the Get Statistics for a Process for an Application request
     */
    Mono<GetApplicationProcessStatisticsResponse> getProcessStatistics(GetApplicationProcessStatisticsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-apps">List Applications</a> request
     *
     * @param request the List Applications request
     * @return the response from the List Applications request
     */
    Mono<ListApplicationsResponse> list(ListApplicationsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.47.0/#list-builds-for-an-app">List Application Builds</a> request
     *
     * @param request the List Application Builds request
     * @return the response from the List Application Builds request
     */
    Mono<ListApplicationBuildsResponse> listBuilds(ListApplicationBuildsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-droplets-for-an-app">List Application Droplets</a> request
     *
     * @param request the List Application Droplets request
     * @return the response from the List Application Droplets request
     */
    Mono<ListApplicationDropletsResponse> listDroplets(ListApplicationDropletsRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#list-packages-for-an-app">List Application Packages</a> request
     *
     * @param request the List Application Packages request
     * @return the response from the List Application Packages request
     */
    Mono<ListApplicationPackagesResponse> listPackages(ListApplicationPackagesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#list-processes-for-app">List Application Processes</a> request
     *
     * @param request the List Application Processes request
     * @return the response from the List Application Processes request
     */
    Mono<ListApplicationProcessesResponse> listProcesses(ListApplicationProcessesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#list-routes-for-an-app">List Application Routes</a> request
     *
     * @param request the List Application Routes request
     * @return the response from the List Application Routes request
     */
    Mono<ListApplicationRoutesResponse> listRoutes(ListApplicationRoutesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-tasks-for-an-app">List Application Tasks</a> request
     *
     * @param request the List Application Tasks request
     * @return the response from the List Application Tasks request
     */
    Mono<ListApplicationTasksResponse> listTasks(ListApplicationTasksRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#scale-a-process">Scale Application</a> request
     *
     * @param request the Scale Application request
     * @return the response from the Scale Application request
     */
    Mono<ScaleApplicationResponse> scale(ScaleApplicationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#set-current-droplet">Set Current Droplet</a> request
     *
     * @param request the Set Current Droplet request
     * @return the response from the Set Current Droplet request
     */
    Mono<SetApplicationCurrentDropletResponse> setCurrentDroplet(SetApplicationCurrentDropletRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#start-an-app">Start Application</a> request
     *
     * @param request the Start Application request
     * @return the response from the Start Application request
     */
    Mono<StartApplicationResponse> start(StartApplicationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#stop-an-app">Stop Application</a> request
     *
     * @param request the Stop Application request
     * @return the response from the Stop Application request
     */
    Mono<StopApplicationResponse> stop(StopApplicationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#terminate-a-process-instance">Delete Application Process</a> request
     *
     * @param request the Delete Application Process Instance request
     * @return the response from the Delete Application Process Instance request
     */
    Mono<Void> terminateInstance(TerminateApplicationInstanceRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#update-an-app">Update Application</a> request
     *
     * @param request the Update Application request
     * @return the response from the Update Application request
     */
    Mono<UpdateApplicationResponse> update(UpdateApplicationRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#update-environment-variables-for-an-app">Update Application Environment Variables</a> request
     *
     * @param request the Update Application Environment Variables request
     * @return the response from the Update Application Environment Variables request
     */
    Mono<UpdateApplicationEnvironmentVariablesResponse> updateEnvironmentVariables(UpdateApplicationEnvironmentVariablesRequest request);

}
