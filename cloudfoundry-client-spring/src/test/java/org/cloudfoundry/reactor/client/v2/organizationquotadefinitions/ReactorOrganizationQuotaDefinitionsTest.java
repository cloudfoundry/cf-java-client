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

package org.cloudfoundry.reactor.client.v2.organizationquotadefinitions;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorOrganizationQuotaDefinitionsTest {

    public static final class CreateQuotaDefinition extends AbstractClientApiTest<CreateOrganizationQuotaDefinitionRequest, CreateOrganizationQuotaDefinitionResponse> {

        private final ReactorOrganizationQuotaDefinitions quotaDefinitions = new ReactorOrganizationQuotaDefinitions(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/quota_definitions")
                    .payload("fixtures/client/v2/quota_definitions/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/quota_definitions/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateOrganizationQuotaDefinitionResponse getResponse() {
            return CreateOrganizationQuotaDefinitionResponse.builder()
                .metadata(Metadata.builder()
                    .id("27a0466e-53c0-439a-ab9f-3e56854302f9")
                    .url("/v2/quota_definitions/27a0466e-53c0-439a-ab9f-3e56854302f9")
                    .createdAt("2016-04-06T00:17:26Z")
                    .build())
                .entity(OrganizationQuotaDefinitionEntity.builder()
                    .name("gold_quota")
                    .nonBasicServicesAllowed(true)
                    .totalServices(-1)
                    .totalRoutes(-1)
                    .totalPrivateDomains(-1)
                    .memoryLimit(5120)
                    .trialDatabaseAllowed(false)
                    .instanceMemoryLimit(10240)
                    .applicationInstanceLimit(10)
                    .applicationTaskLimit(5)
                    .totalServiceKeys(-1)
                    .build())
                .build();
        }

        @Override
        protected CreateOrganizationQuotaDefinitionRequest getValidRequest() throws Exception {
            return CreateOrganizationQuotaDefinitionRequest.builder()
                .name("gold_quota")
                .nonBasicServicesAllowed(true)
                .totalServices(-1)
                .totalRoutes(-1)
                .memoryLimit(5120)
                .instanceMemoryLimit(10240)
                .applicationInstanceLimit(10)
                .applicationTaskLimit(5)
                .build();
        }

        @Override
        protected Mono<CreateOrganizationQuotaDefinitionResponse> invoke(CreateOrganizationQuotaDefinitionRequest request) {
            return this.quotaDefinitions.create(request);
        }

    }

    public static final class DeleteQuotaDefinition extends AbstractClientApiTest<DeleteOrganizationQuotaDefinitionRequest, DeleteOrganizationQuotaDefinitionResponse> {

        private final ReactorOrganizationQuotaDefinitions quotaDefinitions = new ReactorOrganizationQuotaDefinitions(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/quota_definitions/test-quota-definition-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected DeleteOrganizationQuotaDefinitionResponse getResponse() {
            return null;
        }

        @Override
        protected DeleteOrganizationQuotaDefinitionRequest getValidRequest() throws Exception {
            return DeleteOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId("test-quota-definition-id")
                .build();
        }

        @Override
        protected Mono<DeleteOrganizationQuotaDefinitionResponse> invoke(DeleteOrganizationQuotaDefinitionRequest request) {
            return this.quotaDefinitions.delete(request);
        }

    }

    public static final class GetQuotaDefinition extends AbstractClientApiTest<GetOrganizationQuotaDefinitionRequest, GetOrganizationQuotaDefinitionResponse> {

        private final ReactorOrganizationQuotaDefinitions quotaDefinitions = new ReactorOrganizationQuotaDefinitions(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/quota_definitions/test-quota-definition-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/quota_definitions/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetOrganizationQuotaDefinitionResponse getResponse() {
            return GetOrganizationQuotaDefinitionResponse.builder()
                .metadata(Metadata.builder()
                    .id("c1b8a422-e2b2-4e28-8a16-90ebef2a6922")
                    .url("/v2/quota_definitions/c1b8a422-e2b2-4e28-8a16-90ebef2a6922")
                    .createdAt("2016-01-26T22:20:36Z")
                    .build())
                .entity(OrganizationQuotaDefinitionEntity.builder()
                    .name("name-2527")
                    .nonBasicServicesAllowed(true)
                    .totalServices(60)
                    .totalRoutes(1000)
                    .totalPrivateDomains(-1)
                    .memoryLimit(20480)
                    .trialDatabaseAllowed(false)
                    .instanceMemoryLimit(-1)
                    .applicationInstanceLimit(-1)
                    .applicationTaskLimit(-1)
                    .totalServiceKeys(-1)
                    .build())
                .build();
        }

        @Override
        protected GetOrganizationQuotaDefinitionRequest getValidRequest() throws Exception {
            return GetOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId("test-quota-definition-id")
                .build();
        }

        @Override
        protected Mono<GetOrganizationQuotaDefinitionResponse> invoke(GetOrganizationQuotaDefinitionRequest request) {
            return this.quotaDefinitions.get(request);
        }

    }

    public static final class ListOrganizationQuotaDefinitions extends AbstractClientApiTest<ListOrganizationQuotaDefinitionsRequest, ListOrganizationQuotaDefinitionsResponse> {

        private final ReactorOrganizationQuotaDefinitions quotaDefinitions = new ReactorOrganizationQuotaDefinitions(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/quota_definitions?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/quota_definitions/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListOrganizationQuotaDefinitionsResponse getResponse() {
            return ListOrganizationQuotaDefinitionsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(OrganizationQuotaDefinitionResource.builder()
                    .metadata(Metadata.builder()
                        .id("9a76e262-9dc1-4316-87ad-a8b3bfbb11d4")
                        .url("/v2/quota_definitions/9a76e262-9dc1-4316-87ad-a8b3bfbb11d4")
                        .createdAt("2016-01-26T22:20:04Z")
                        .build())
                    .entity(OrganizationQuotaDefinitionEntity.builder()
                        .applicationInstanceLimit(-1)
                        .instanceMemoryLimit(-1)
                        .memoryLimit(10240)
                        .name("default")
                        .nonBasicServicesAllowed(true)
                        .totalPrivateDomains(-1)
                        .totalRoutes(1000)
                        .totalServices(100)
                        .trialDatabaseAllowed(false)
                        .applicationTaskLimit(-1)
                        .totalServiceKeys(-1)
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListOrganizationQuotaDefinitionsRequest getValidRequest() throws Exception {
            return ListOrganizationQuotaDefinitionsRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected Publisher<ListOrganizationQuotaDefinitionsResponse> invoke(ListOrganizationQuotaDefinitionsRequest request) {
            return this.quotaDefinitions.list(request);
        }

    }

    public static final class UpdateQuotaDefinition extends AbstractClientApiTest<UpdateOrganizationQuotaDefinitionRequest, UpdateOrganizationQuotaDefinitionResponse> {

        private final ReactorOrganizationQuotaDefinitions quotaDefinitions = new ReactorOrganizationQuotaDefinitions(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v2/quota_definitions/test-quota-definition-id")
                    .payload("fixtures/client/v2/quota_definitions/PUT_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/quota_definitions/PUT_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected UpdateOrganizationQuotaDefinitionResponse getResponse() {
            return UpdateOrganizationQuotaDefinitionResponse.builder()
                .metadata(Metadata.builder()
                    .id("cd10a1dd-f372-4b19-8ff6-60214b265f6f")
                    .url("/v2/quota_definitions/cd10a1dd-f372-4b19-8ff6-60214b265f6f")
                    .createdAt("2016-04-06T00:17:26Z")
                    .updatedAt("2016-04-06T00:17:26Z")
                    .build())
                .entity(OrganizationQuotaDefinitionEntity.builder()
                    .name("name-601")
                    .nonBasicServicesAllowed(true)
                    .totalServices(60)
                    .totalRoutes(1000)
                    .totalPrivateDomains(-1)
                    .memoryLimit(20480)
                    .trialDatabaseAllowed(false)
                    .instanceMemoryLimit(-1)
                    .applicationInstanceLimit(-1)
                    .applicationTaskLimit(-1)
                    .totalServiceKeys(-1)
                    .build())
                .build();
        }

        @Override
        protected UpdateOrganizationQuotaDefinitionRequest getValidRequest() throws Exception {
            return UpdateOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId("test-quota-definition-id")
                .build();
        }

        @Override
        protected Mono<UpdateOrganizationQuotaDefinitionResponse> invoke(UpdateOrganizationQuotaDefinitionRequest request) {
            return this.quotaDefinitions.update(request);
        }

    }


}
