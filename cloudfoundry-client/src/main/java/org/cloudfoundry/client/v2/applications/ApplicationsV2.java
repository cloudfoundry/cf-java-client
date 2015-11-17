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
     * Makes the
     * <a href="http://apidocs.cloudfoundry.org/214/apps/get_the_instance_information_for_a_started_app.html">Get
     * the instance information for a STARTED App</a> request
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

}
