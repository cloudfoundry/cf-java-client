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
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


public class ReactorEnvironmentVariableGroupsTest {

    public static final class GetRunningEnvironmentVariables extends AbstractClientApiTest<GetRunningEnvironmentVariablesRequest, GetRunningEnvironmentVariablesResponse> {

        private ReactorEnvironmentVariableGroups environmentVariableGroups = new ReactorEnvironmentVariableGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/config/environment_variable_groups/running")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/environment_variable_groups/GET_running_response.json")
                    .build())
                .build();
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

    public static final class GetStagingEnvironmentVariables extends AbstractClientApiTest<GetStagingEnvironmentVariablesRequest, GetStagingEnvironmentVariablesResponse> {

        private ReactorEnvironmentVariableGroups environmentVariableGroups = new ReactorEnvironmentVariableGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/config/environment_variable_groups/staging")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/environment_variable_groups/GET_staging_response.json")
                    .build())
                .build();
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

    public static final class UpdateRunningEnvironmentVariables extends AbstractClientApiTest<UpdateRunningEnvironmentVariablesRequest, UpdateRunningEnvironmentVariablesResponse> {

        private ReactorEnvironmentVariableGroups environmentVariableGroups = new ReactorEnvironmentVariableGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/config/environment_variable_groups/running")
                    .payload("fixtures/client/v2/environment_variable_groups/PUT_running_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/environment_variable_groups/PUT_running_response.json")
                    .build())
                .build();
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

    public static final class UpdateStagingEnvironmentVariables extends AbstractClientApiTest<UpdateStagingEnvironmentVariablesRequest, UpdateStagingEnvironmentVariablesResponse> {

        private ReactorEnvironmentVariableGroups environmentVariableGroups = new ReactorEnvironmentVariableGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/config/environment_variable_groups/staging")
                    .payload("fixtures/client/v2/environment_variable_groups/PUT_staging_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/environment_variable_groups/PUT_staging_response.json")
                    .build())
                .build();
        }

        @Override
        protected UpdateStagingEnvironmentVariablesResponse getResponse() {
            return UpdateStagingEnvironmentVariablesResponse.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build();
        }

        @Override
        protected UpdateStagingEnvironmentVariablesRequest getValidRequest() throws Exception {
            return UpdateStagingEnvironmentVariablesRequest.builder()
                .environmentVariable("abc", 123)
                .environmentVariable("do-re-me", "far-so-la-tee")
                .build();
        }

        @Override
        protected Mono<UpdateStagingEnvironmentVariablesResponse> invoke(UpdateStagingEnvironmentVariablesRequest request) {
            return this.environmentVariableGroups.updateStagingEnvironmentVariables(request);
        }
    }

}
