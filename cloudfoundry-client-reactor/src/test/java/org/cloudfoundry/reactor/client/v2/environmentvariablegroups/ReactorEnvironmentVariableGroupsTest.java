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

package org.cloudfoundry.reactor.client.v2.environmentvariablegroups;

import org.cloudfoundry.client.v2.environmentvariablegroups.GetRunningEnvironmentVariablesRequest;
import org.cloudfoundry.client.v2.environmentvariablegroups.GetRunningEnvironmentVariablesResponse;
import org.cloudfoundry.client.v2.environmentvariablegroups.GetStagingEnvironmentVariablesRequest;
import org.cloudfoundry.client.v2.environmentvariablegroups.GetStagingEnvironmentVariablesResponse;
import org.cloudfoundry.client.v2.environmentvariablegroups.UpdateRunningEnvironmentVariablesRequest;
import org.cloudfoundry.client.v2.environmentvariablegroups.UpdateRunningEnvironmentVariablesResponse;
import org.cloudfoundry.client.v2.environmentvariablegroups.UpdateStagingEnvironmentVariablesRequest;
import org.cloudfoundry.client.v2.environmentvariablegroups.UpdateStagingEnvironmentVariablesResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorEnvironmentVariableGroupsTest extends AbstractClientApiTest {

    private ReactorEnvironmentVariableGroups environmentVariableGroups = new ReactorEnvironmentVariableGroups(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void getRunningEnvironmentVariables() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/config/environment_variable_groups/running")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/environment_variable_groups/GET_running_response.json")
                .build())
            .build());

        this.environmentVariableGroups
            .getRunningEnvironmentVariables(GetRunningEnvironmentVariablesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(GetRunningEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getStagingEnvironmentVariables() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/config/environment_variable_groups/staging")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/environment_variable_groups/GET_staging_response.json")
                .build())
            .build());

        this.environmentVariableGroups
            .getStagingEnvironmentVariables(GetStagingEnvironmentVariablesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(GetStagingEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateRunningEnvironmentVariables() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/config/environment_variable_groups/running")
                .payload("fixtures/client/v2/environment_variable_groups/PUT_running_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/environment_variable_groups/PUT_running_response.json")
                .build())
            .build());

        this.environmentVariableGroups
            .updateRunningEnvironmentVariables(UpdateRunningEnvironmentVariablesRequest.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "fa-so-la-tee")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateRunningEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "fa-so-la-tee")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateRunningEnvironmentVariablesEmpty() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/config/environment_variable_groups/running")
                .payload("fixtures/client/v2/environment_variable_groups/PUT_running_request_empty.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/environment_variable_groups/PUT_running_response_empty.json")
                .build())
            .build());

        this.environmentVariableGroups
            .updateRunningEnvironmentVariables(UpdateRunningEnvironmentVariablesRequest.builder()
                .environmentVariables(Collections.emptyMap())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateRunningEnvironmentVariablesResponse.builder()
                .environmentVariables(Collections.emptyMap())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateStagingEnvironmentVariables() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/config/environment_variable_groups/staging")
                .payload("fixtures/client/v2/environment_variable_groups/PUT_staging_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/environment_variable_groups/PUT_staging_response.json")
                .build())
            .build());

        this.environmentVariableGroups
            .updateStagingEnvironmentVariables(UpdateStagingEnvironmentVariablesRequest.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateStagingEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateStagingEnvironmentVariablesEmpty() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/config/environment_variable_groups/staging")
                .payload("fixtures/client/v2/environment_variable_groups/PUT_staging_request_empty.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/environment_variable_groups/PUT_staging_response_empty.json")
                .build())
            .build());

        this.environmentVariableGroups
            .updateStagingEnvironmentVariables(UpdateStagingEnvironmentVariablesRequest.builder()
                .environmentVariables(Collections.emptyMap())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateStagingEnvironmentVariablesResponse.builder()
                .environmentVariables(Collections.emptyMap())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
