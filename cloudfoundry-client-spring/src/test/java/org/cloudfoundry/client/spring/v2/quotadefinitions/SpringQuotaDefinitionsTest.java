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

package org.cloudfoundry.client.spring.v2.quotadefinitions;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.quotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.quotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.quotadefinitions.OrganizationQuotaDefinitionEntity;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringQuotaDefinitionsTest {

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
                .method(GET).path("v2/quota_definitions/test-quota-definition-id")
                .status(OK)
                .responsePayload("v2/quota_definitions/GET_{id}_response.json");
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
                    .build())
                .build();
        }

        @Override
        protected GetOrganizationQuotaDefinitionRequest getValidRequest() throws Exception {
            return GetOrganizationQuotaDefinitionRequest.builder()
                .quotaDefinitionId("test-quota-definition-id")
                .build();
        }

        @Override
        protected Mono<GetOrganizationQuotaDefinitionResponse> invoke(GetOrganizationQuotaDefinitionRequest request) {
            return this.quotaDefinitions.get(request);
        }

    }

}
