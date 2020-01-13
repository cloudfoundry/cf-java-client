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

package org.cloudfoundry.client.v2.environmentvariablegroups;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry EnvironmentVariableGroups V2 Client API
 */
public interface EnvironmentVariableGroups {

    /**
     * Makes the <a href="apidocs.cloudfoundry.org/latest-release/environment_variable_groups/getting_the_contents_of_the_running_environment_variable_group.html">Get the Running Environment
     * Variables</a> request
     *
     * @param request the Get Running Environment Variables request
     * @return the response from the Get Running Environment Variables request
     */
    Mono<GetRunningEnvironmentVariablesResponse> getRunningEnvironmentVariables(GetRunningEnvironmentVariablesRequest request);

    /**
     * Makes the <a href="apidocs.cloudfoundry.org/latest-release/environment_variable_groups/getting_the_contents_of_the_staging_environment_variable_group.html">Get the Staging Environment
     * Variables</a> request
     *
     * @param request the Get Staging Environment Variables request
     * @return the response from the Get Staging Environment Variables request
     */
    Mono<GetStagingEnvironmentVariablesResponse> getStagingEnvironmentVariables(GetStagingEnvironmentVariablesRequest request);

    /**
     * Makes the <a href="apidocs.cloudfoundry.org/latest-release/environment_variable_groups/updating_the_contents_of_the_running_environment_variable_group.html">Update the Running Environment
     * Variables</a> request
     *
     * @param request the Update Running Environment Variables request
     * @return the response from the Update Running Environment Variables request
     */
    Mono<UpdateRunningEnvironmentVariablesResponse> updateRunningEnvironmentVariables(UpdateRunningEnvironmentVariablesRequest request);

    /**
     * Makes the <a href="apidocs.cloudfoundry.org/latest-releasehttps://apidocs.cloudfoundry.org/latest-release/environment_variable_groups/updating_the_contents_of_the_staging_environment_variable_group.html">Update
     * the Staging Environment Variables</a> request
     *
     * @param request the Update Staging Environment Variables request
     * @return the response from the Update Staging Environment Variables request
     */
    Mono<UpdateStagingEnvironmentVariablesResponse> updateStagingEnvironmentVariables(UpdateStagingEnvironmentVariablesRequest request);

}
