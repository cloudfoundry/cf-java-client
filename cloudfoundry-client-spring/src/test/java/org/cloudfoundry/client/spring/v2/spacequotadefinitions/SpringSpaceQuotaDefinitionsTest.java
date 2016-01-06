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

package org.cloudfoundry.client.spring.v2.spacequotadefinitions;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.spacequotadefinitions.AssociateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.AssociateSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.reactivestreams.Publisher;
import reactor.Mono;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringSpaceQuotaDefinitionsTest {

    public static final class AssociateSpace extends AbstractApiTest<AssociateSpaceQuotaDefinitionRequest, AssociateSpaceQuotaDefinitionResponse> {

        private final SpringSpaceQuotaDefinitions spaceQuotaDefinitions = new SpringSpaceQuotaDefinitions(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceQuotaDefinitionRequest getInvalidRequest() {
            return AssociateSpaceQuotaDefinitionRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v2/space_quota_definitions/test-id/spaces/test-space-id")
                    .status(OK)
                    .responsePayload("v2/space_quota_definitions/PUT_{id}_spaces_{id}_response.json");
        }

        @Override
        protected AssociateSpaceQuotaDefinitionResponse getResponse() {
            return AssociateSpaceQuotaDefinitionResponse.builder()
                    .metadata(Metadata.builder()
                            .id("ea82f16c-c21a-4a8a-947a-f7606e7f63fa")
                            .url("/v2/space_quota_definitions/ea82f16c-c21a-4a8a-947a-f7606e7f63fa")
                            .createdAt("2015-11-30T23:38:46Z")
                            .build())
                    .entity(SpaceQuotaDefinitionEntity.builder()
                            .name("name-1887")
                            .organizationId("e188543a-cb71-4786-8703-9addbebc5bbf")
                            .nonBasicServicesAllowed(true)
                            .totalServices(60)
                            .totalRoutes(1000)
                            .memoryLimit(20480)
                            .instanceMemoryLimit(-1)
                            .applicationInstanceLimit(-1)
                            .organizationUrl("/v2/organizations/e188543a-cb71-4786-8703-9addbebc5bbf")
                            .spacesUrl("/v2/space_quota_definitions/ea82f16c-c21a-4a8a-947a-f7606e7f63fa/spaces")
                            .build())
                    .build();
        }

        @Override
        protected AssociateSpaceQuotaDefinitionRequest getValidRequest() throws Exception {
            return AssociateSpaceQuotaDefinitionRequest.builder().id("test-id").spaceId("test-space-id").build();
        }

        @Override
        protected Publisher<AssociateSpaceQuotaDefinitionResponse> invoke(AssociateSpaceQuotaDefinitionRequest request) {
            return this.spaceQuotaDefinitions.associateSpace(request);
        }
    }

    public static final class GetSpaceQuotaDefinition extends AbstractApiTest<GetSpaceQuotaDefinitionRequest, GetSpaceQuotaDefinitionResponse> {

        private final SpringSpaceQuotaDefinitions spacequotadefinitions = new SpringSpaceQuotaDefinitions(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetSpaceQuotaDefinitionRequest getInvalidRequest() {
            return GetSpaceQuotaDefinitionRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("v2/space_quota_definitions/test-id")
                    .status(OK)
                    .responsePayload("v2/space_quota_definitions/GET_{id}_response.json");
        }

        @Override
        protected GetSpaceQuotaDefinitionResponse getResponse() {
            return GetSpaceQuotaDefinitionResponse.builder()
                    .metadata(Metadata.builder()
                            .id("4b8e7d14-71bd-4abb-b474-183375c75c84")
                            .url("/v2/space_quota_definitions/4b8e7d14-71bd-4abb-b474-183375c75c84")
                            .createdAt("2015-11-30T23:38:46Z")
                            .build())
                    .entity(SpaceQuotaDefinitionEntity.builder()
                            .name("name-1892")
                            .organizationId("0dbbac8c-16ac-4ba5-8f59-3d3a79874f5d")
                            .nonBasicServicesAllowed(true)
                            .totalServices(60)
                            .totalRoutes(1000)
                            .memoryLimit(20480)
                            .instanceMemoryLimit(-1)
                            .applicationInstanceLimit(-1)
                            .organizationUrl("/v2/organizations/0dbbac8c-16ac-4ba5-8f59-3d3a79874f5d")
                            .spacesUrl("/v2/space_quota_definitions/4b8e7d14-71bd-4abb-b474-183375c75c84/spaces")
                            .build())
                    .build();
        }

        @Override
        protected GetSpaceQuotaDefinitionRequest getValidRequest() throws Exception {
            return GetSpaceQuotaDefinitionRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<GetSpaceQuotaDefinitionResponse> invoke(GetSpaceQuotaDefinitionRequest request) {
            return this.spacequotadefinitions.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListSpaceQuotaDefinitionsRequest, ListSpaceQuotaDefinitionsResponse> {

        private final SpringSpaceQuotaDefinitions spaceQuotaDefinitions = new SpringSpaceQuotaDefinitions(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceQuotaDefinitionsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/space_quota_definitions?page=-1")
                    .status(OK)
                    .responsePayload("v2/space_quota_definitions/GET_response.json");
        }

        @Override
        protected ListSpaceQuotaDefinitionsResponse getResponse() {
            return ListSpaceQuotaDefinitionsResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(SpaceQuotaDefinitionResource.builder()
                            .metadata(Metadata.builder()
                                    .id("be2d5c01-3413-43db-bea2-49b0b60ec74d")
                                    .url("/v2/space_quota_definitions/be2d5c01-3413-43db-bea2-49b0b60ec74d")
                                    .createdAt("2015-07-27T22:43:32Z")
                                    .build())
                            .entity(SpaceQuotaDefinitionEntity.builder()
                                    .name("name-2236")
                                    .organizationId("a81d5218-b473-474e-9afb-3223a8b2ae9f")
                                    .nonBasicServicesAllowed(true)
                                    .totalServices(60)
                                    .totalRoutes(1000)
                                    .memoryLimit(20480)
                                    .instanceMemoryLimit(-1)
                                    .organizationUrl("/v2/organizations/a81d5218-b473-474e-9afb-3223a8b2ae9f")
                                    .spacesUrl
                                            ("/v2/space_quota_definitions/be2d5c01-3413-43db-bea2-49b0b60ec74d/spaces")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListSpaceQuotaDefinitionsRequest getValidRequest() {
            return ListSpaceQuotaDefinitionsRequest.builder()
                    .page(-1)
                    .build();
        }

        @Override
        protected Mono<ListSpaceQuotaDefinitionsResponse> invoke(ListSpaceQuotaDefinitionsRequest request) {
            return this.spaceQuotaDefinitions.list(request);
        }

    }

}
