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

package org.cloudfoundry.spring.client.v2.environmentvariablegroups;

import org.cloudfoundry.client.v2.environmentvariablegroups.GetRunningEnvironmentVariablesRequest;
import org.cloudfoundry.client.v2.environmentvariablegroups.GetRunningEnvironmentVariablesResponse;
import org.cloudfoundry.client.v2.environmentvariablegroups.GetStagingEnvironmentVariablesRequest;
import org.cloudfoundry.client.v2.environmentvariablegroups.GetStagingEnvironmentVariablesResponse;
import org.cloudfoundry.client.v2.environmentvariablegroups.UpdateRunningEnvironmentVariablesRequest;
import org.cloudfoundry.client.v2.environmentvariablegroups.UpdateRunningEnvironmentVariablesResponse;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

public class SpringEnvironmentVariableGroupsTest {

    public static final class GetRunningEnvironmentVariables extends AbstractApiTest<GetRunningEnvironmentVariablesRequest, GetRunningEnvironmentVariablesResponse> {

        private SpringEnvironmentVariableGroups environmentVariableGroups = new SpringEnvironmentVariableGroups(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetRunningEnvironmentVariablesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/config/environment_variable_groups/running")
                .status(OK)
                .responsePayload("fixtures/client/v2/environment_variable_groups/GET_running_response.json");
        }

        @Override
        protected GetRunningEnvironmentVariablesResponse getResponse() {
            return GetRunningEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build();
        }

        @Override
        protected GetRunningEnvironmentVariablesRequest getValidRequest() throws Exception {
            return GetRunningEnvironmentVariablesRequest.builder()
                .build();
        }

        @Override
        protected Mono<GetRunningEnvironmentVariablesResponse> invoke(GetRunningEnvironmentVariablesRequest request) {
            return this.environmentVariableGroups.getRunningEnvironmentVariables(request);
        }
    }

    public static final class GetStagingEnvironmentVariables extends AbstractApiTest<GetStagingEnvironmentVariablesRequest, GetStagingEnvironmentVariablesResponse> {

        private SpringEnvironmentVariableGroups environmentVariableGroups = new SpringEnvironmentVariableGroups(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetStagingEnvironmentVariablesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/config/environment_variable_groups/staging")
                .status(OK)
                .responsePayload("fixtures/client/v2/environment_variable_groups/GET_staging_response.json");
        }

        @Override
        protected GetStagingEnvironmentVariablesResponse getResponse() {
            return GetStagingEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build();
        }

        @Override
        protected GetStagingEnvironmentVariablesRequest getValidRequest() throws Exception {
            return GetStagingEnvironmentVariablesRequest.builder()
                .build();
        }

        @Override
        protected Mono<GetStagingEnvironmentVariablesResponse> invoke(GetStagingEnvironmentVariablesRequest request) {
            return this.environmentVariableGroups.getStagingEnvironmentVariables(request);
        }
    }

    public static final class UpdateEnvironmentVariables extends AbstractApiTest<UpdateRunningEnvironmentVariablesRequest, UpdateRunningEnvironmentVariablesResponse> {

        private SpringEnvironmentVariableGroups environmentVariableGroups = new SpringEnvironmentVariableGroups(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateRunningEnvironmentVariablesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/config/environment_variable_groups/running")
                .requestPayload("fixtures/client/v2/environment_variable_groups/PUT_running_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/environment_variable_groups/PUT_running_response.json");
        }

        @Override
        protected UpdateRunningEnvironmentVariablesResponse getResponse() {
            return UpdateRunningEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build();
        }

        @Override
        protected UpdateRunningEnvironmentVariablesRequest getValidRequest() throws Exception {
            return UpdateRunningEnvironmentVariablesRequest.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build();
        }

        @Override
        protected Mono<UpdateRunningEnvironmentVariablesResponse> invoke(UpdateRunningEnvironmentVariablesRequest request) {
            return this.environmentVariableGroups.updateRunningEnvironmentVariables(request);
        }
    }

}
