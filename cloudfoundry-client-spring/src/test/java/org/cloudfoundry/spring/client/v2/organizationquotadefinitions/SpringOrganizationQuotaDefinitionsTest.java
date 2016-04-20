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

package org.cloudfoundry.spring.client.v2.organizationquotadefinitions;

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
import org.cloudfoundry.spring.AbstractApiTest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringOrganizationQuotaDefinitionsTest {

    public static final class CreateQuotaDefinition extends AbstractApiTest<CreateOrganizationQuotaDefinitionRequest, CreateOrganizationQuotaDefinitionResponse> {

        private final SpringOrganizationQuotaDefinitions quotaDefinitions = new SpringOrganizationQuotaDefinitions(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateOrganizationQuotaDefinitionRequest getInvalidRequest() {
            return CreateOrganizationQuotaDefinitionRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/quota_definitions")
                .requestPayload("fixtures/client/v2/quota_definitions/POST_request.json")
                .status(CREATED)
                .responsePayload("fixtures/client/v2/quota_definitions/POST_response.json");
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
                    .trialDbAllowed(false)
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

    public static final class DeleteQuotaDefinition extends AbstractApiTest<DeleteOrganizationQuotaDefinitionRequest, DeleteOrganizationQuotaDefinitionResponse> {

        private final SpringOrganizationQuotaDefinitions quotaDefinitions = new SpringOrganizationQuotaDefinitions(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteOrganizationQuotaDefinitionRequest getInvalidRequest() {
            return DeleteOrganizationQuotaDefinitionRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/quota_definitions/test-quota-definition-id")
                .status(NO_CONTENT);
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

    public static final class GetQuotaDefinition extends AbstractApiTest<GetOrganizationQuotaDefinitionRequest, GetOrganizationQuotaDefinitionResponse> {

        private final SpringOrganizationQuotaDefinitions quotaDefinitions = new SpringOrganizationQuotaDefinitions(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetOrganizationQuotaDefinitionRequest getInvalidRequest() {
            return GetOrganizationQuotaDefinitionRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/quota_definitions/test-quota-definition-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/quota_definitions/GET_{id}_response.json");
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
                    .trialDbAllowed(false)
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

    public static final class ListOrganizationQuotaDefinitions extends AbstractApiTest<ListOrganizationQuotaDefinitionsRequest, ListOrganizationQuotaDefinitionsResponse> {

        private final SpringOrganizationQuotaDefinitions quotaDefinitions = new SpringOrganizationQuotaDefinitions(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListOrganizationQuotaDefinitionsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/quota_definitions?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/quota_definitions/GET_response.json");
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
                        .trialDbAllowed(false)
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

}
