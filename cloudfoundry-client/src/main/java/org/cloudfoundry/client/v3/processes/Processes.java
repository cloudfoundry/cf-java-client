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

package org.cloudfoundry.client.v3.processes;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Processes Client API
 */
public interface Processes {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/processes_%28experimental%29/get_a_process.html">Get Process</a> request
     *
     * @param request the Get Process request
     * @return the response from the Get Process request
     */
    Mono<GetProcessResponse> get(GetProcessRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/230/processes_(experimental)/get_detailed_stats_for_a_process.html">Get Detailed Stats for a Process</a> request
     *
     * @param request the Get Detailed Stats for a Process request
     * @return the response from the Get Detailed Stats for a Process request
     */
    Mono<GetProcessDetailedStatisticsResponse> getDetailedStatistics(GetProcessDetailedStatisticsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/processes_%28experimental%29/list_all_processes.html">List Process</a> request
     *
     * @param request the List Processes request
     * @return the response from the List Processes request
     */
    Mono<ListProcessesResponse> list(ListProcessesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/processes_%28experimental%29/scaling_a_process.html">Scale Application</a> request
     *
     * @param request the Scale Process request
     * @return the response from the Scale Process request
     */
    Mono<ScaleProcessResponse> scale(ScaleProcessRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/processes_%28experimental%29/terminating_a_process_instance.html">Terminate Process Instance</a> request
     *
     * @param request the Terminate Process Instance request
     * @return the response from the Terminate Process Instance request
     */
    Mono<Void> terminateInstance(TerminateProcessInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/processes_%28experimental%29/updating_a_process.html">Update Process</a> request
     *
     * @param request the Update Process request
     * @return the response from the Update Process request
     */
    Mono<UpdateProcessResponse> update(UpdateProcessRequest request);

}
