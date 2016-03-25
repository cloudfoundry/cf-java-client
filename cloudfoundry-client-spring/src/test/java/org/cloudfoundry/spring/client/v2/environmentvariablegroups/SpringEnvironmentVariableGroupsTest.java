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

import org.cloudfoundry.client.v2.environmentvariablegroups.UpdateRunningEnvironmentVariablesRequest;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

public class SpringEnvironmentVariableGroupsTest {

    public static final class UpdateEnvironmentVariables extends AbstractApiTest<UpdateRunningEnvironmentVariablesRequest, Void> {

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
        protected Void getResponse() {
            return null;
        }

        @Override
        protected UpdateRunningEnvironmentVariablesRequest getValidRequest() throws Exception {
            return UpdateRunningEnvironmentVariablesRequest.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build();
        }

        @Override
        protected Mono<Void> invoke(UpdateRunningEnvironmentVariablesRequest request) {
            return this.environmentVariableGroups.updateRunningEnvironmentVariables(request);
        }
    }

}
