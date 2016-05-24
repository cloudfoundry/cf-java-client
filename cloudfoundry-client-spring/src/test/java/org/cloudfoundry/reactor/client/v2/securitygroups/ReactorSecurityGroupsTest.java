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

package org.cloudfoundry.reactor.client.v2.securitygroups;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorSecurityGroupsTest {

    public static final class DeleteRunning extends AbstractClientApiTest<DeleteSecurityGroupRunningDefaultRequest, Void> {

        private final ReactorSecurityGroups securityGroups = new ReactorSecurityGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/config/running_security_groups/test-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteSecurityGroupRunningDefaultRequest getValidRequest() throws Exception {
            return DeleteSecurityGroupRunningDefaultRequest.builder()
                .securityGroupRunningDefaultId("test-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteSecurityGroupRunningDefaultRequest request) {
            return this.securityGroups.deleteRunningDefault(request);
        }

    }

    public static final class DeleteStaging extends AbstractClientApiTest<DeleteSecurityGroupStagingDefaultRequest, Void> {

        private final ReactorSecurityGroups securityGroups = new ReactorSecurityGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/config/staging_security_groups/test-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteSecurityGroupStagingDefaultRequest getValidRequest() throws Exception {
            return DeleteSecurityGroupStagingDefaultRequest.builder()
                .securityGroupStagingDefaultId("test-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteSecurityGroupStagingDefaultRequest request) {
            return this.securityGroups.deleteStagingDefault(request);
        }

    }

    public static final class ListRunning extends AbstractClientApiTest<ListSecurityGroupRunningDefaultsRequest, ListSecurityGroupRunningDefaultsResponse> {

        private final ReactorSecurityGroups securityGroups = new ReactorSecurityGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/config/running_security_groups")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/config/GET_running_security_groups_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListSecurityGroupRunningDefaultsResponse getResponse() {
            return ListSecurityGroupRunningDefaultsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-04-06T00:17:17Z")
                        .id("1f2f24f8-f68c-4a3b-b51a-8134fe2626d8")
                        .url("/v2/config/running_security_groups/1f2f24f8-f68c-4a3b-b51a-8134fe2626d8")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-114")
                        .rule(SecurityGroupEntity.RuleEntity.builder()
                            .destination("198.41.191.47/1")
                            .ports("8080")
                            .protocol("udp")
                            .build())
                        .runningDefault(true)
                        .stagingDefault(false)
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSecurityGroupRunningDefaultsRequest getValidRequest() throws Exception {
            return ListSecurityGroupRunningDefaultsRequest.builder().build();
        }

        @Override
        protected Mono<ListSecurityGroupRunningDefaultsResponse> invoke(ListSecurityGroupRunningDefaultsRequest request) {
            return this.securityGroups.listRunningDefaults(request);
        }

    }

    public static final class ListStaging extends AbstractClientApiTest<ListSecurityGroupStagingDefaultsRequest, ListSecurityGroupStagingDefaultsResponse> {

        private final ReactorSecurityGroups securityGroups = new ReactorSecurityGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/config/staging_security_groups")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/config/GET_staging_security_groups_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListSecurityGroupStagingDefaultsResponse getResponse() {
            return ListSecurityGroupStagingDefaultsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(SecurityGroupResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-04-16T01:23:52Z")
                        .id("c0bb3afb-ae01-4af0-96cf-a5b0d2dca894")
                        .url("/v2/config/staging_security_groups/c0bb3afb-ae01-4af0-96cf-a5b0d2dca894")
                        .build())
                    .entity(SecurityGroupEntity.builder()
                        .name("name-570")
                        .rule(SecurityGroupEntity.RuleEntity.builder()
                            .destination("198.41.191.47/1")
                            .ports("8080")
                            .protocol("udp")
                            .build())
                        .runningDefault(false)
                        .stagingDefault(true)
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSecurityGroupStagingDefaultsRequest getValidRequest() throws Exception {
            return ListSecurityGroupStagingDefaultsRequest.builder().build();
        }

        @Override
        protected Mono<ListSecurityGroupStagingDefaultsResponse> invoke(ListSecurityGroupStagingDefaultsRequest request) {
            return this.securityGroups.listStagingDefaults(request);
        }

    }

    public static final class SetRunning extends AbstractClientApiTest<SetSecurityGroupRunningDefaultRequest, SetSecurityGroupRunningDefaultResponse> {

        private final ReactorSecurityGroups securityGroups = new ReactorSecurityGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/config/running_security_groups/test-security-group-default-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/config/PUT_{id}_running_security_groups_response.json")
                    .build())
                .build();
        }

        @Override
        protected SetSecurityGroupRunningDefaultResponse getResponse() {
            return SetSecurityGroupRunningDefaultResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-04-06T00:17:17Z")
                    .id("9aa7ab9c-997f-4f87-be50-87105521881a")
                    .url("/v2/config/running_security_groups/9aa7ab9c-997f-4f87-be50-87105521881a")
                    .updatedAt("2016-04-06T00:17:17Z")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("name-109")
                    .rule(SecurityGroupEntity.RuleEntity.builder()
                        .destination("198.41.191.47/1")
                        .ports("8080")
                        .protocol("udp")
                        .build())
                    .runningDefault(true)
                    .stagingDefault(false)
                    .build())
                .build();
        }

        @Override
        protected SetSecurityGroupRunningDefaultRequest getValidRequest() throws Exception {
            return SetSecurityGroupRunningDefaultRequest.builder()
                .securityGroupRunningDefaultId("test-security-group-default-id")
                .build();
        }

        @Override
        protected Mono<SetSecurityGroupRunningDefaultResponse> invoke(SetSecurityGroupRunningDefaultRequest request) {
            return this.securityGroups.setRunningDefault(request);
        }

    }

    public static final class SetStaging extends AbstractClientApiTest<SetSecurityGroupStagingDefaultRequest, SetSecurityGroupStagingDefaultResponse> {

        private final ReactorSecurityGroups securityGroups = new ReactorSecurityGroups(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/config/staging_security_groups/test-security-group-default-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/config/PUT_{id}_staging_security_groups_response.json")
                    .build())
                .build();
        }

        @Override
        protected SetSecurityGroupStagingDefaultResponse getResponse() {
            return SetSecurityGroupStagingDefaultResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-04-16T01:23:52Z")
                    .id("50165fce-6c41-4c35-a4d8-3858ee217d36")
                    .url("/v2/config/staging_security_groups/50165fce-6c41-4c35-a4d8-3858ee217d36")
                    .updatedAt("2016-04-16T01:23:52Z")
                    .build())
                .entity(SecurityGroupEntity.builder()
                    .name("name-567")
                    .rule(SecurityGroupEntity.RuleEntity.builder()
                        .destination("198.41.191.47/1")
                        .ports("8080")
                        .protocol("udp")
                        .build())
                    .runningDefault(false)
                    .stagingDefault(true)
                    .build())
                .build();
        }

        @Override
        protected SetSecurityGroupStagingDefaultRequest getValidRequest() throws Exception {
            return SetSecurityGroupStagingDefaultRequest.builder()
                .securityGroupStagingDefaultId("test-security-group-default-id")
                .build();
        }

        @Override
        protected Mono<SetSecurityGroupStagingDefaultResponse> invoke(SetSecurityGroupStagingDefaultRequest request) {
            return this.securityGroups.setStagingDefault(request);
        }

    }

}
