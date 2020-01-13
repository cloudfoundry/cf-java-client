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

package org.cloudfoundry.reactor.client.v2.organizationquotadefinitions;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
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
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorOrganizationQuotaDefinitionsTest extends AbstractClientApiTest {

    private final ReactorOrganizationQuotaDefinitions quotaDefinitions = new ReactorOrganizationQuotaDefinitions(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @SuppressWarnings("deprecation")
    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/quota_definitions")
                .payload("fixtures/client/v2/quota_definitions/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/quota_definitions/POST_response.json")
                .build())
            .build());

        this.quotaDefinitions
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .applicationInstanceLimit(10)
                .applicationTaskLimit(5)
                .instanceMemoryLimit(10240)
                .memoryLimit(5120)
                .name("gold_quota")
                .nonBasicServicesAllowed(true)
                .totalReservedRoutePorts(3)
                .totalRoutes(4)
                .totalServices(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateOrganizationQuotaDefinitionResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:39Z")
                    .id("0a3df5cb-122e-4849-a6b1-abb70d1b1296")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/quota_definitions/0a3df5cb-122e-4849-a6b1-abb70d1b1296")
                    .build())
                .entity(OrganizationQuotaDefinitionEntity.builder()
                    .applicationInstanceLimit(10)
                    .applicationTaskLimit(5)
                    .instanceMemoryLimit(10240)
                    .memoryLimit(5120)
                    .name("gold_quota")
                    .nonBasicServicesAllowed(true)
                    .totalPrivateDomains(-1)
                    .totalReservedRoutePorts(3)
                    .totalRoutes(4)
                    .totalServiceKeys(-1)
                    .totalServices(-1)
                    .trialDatabaseAllowed(false)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/quota_definitions/test-quota-definition-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.quotaDefinitions
            .delete(DeleteOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId("test-quota-definition-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/quota_definitions/test-quota-definition-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/quota_definitions/GET_{id}_response.json")
                .build())
            .build());

        this.quotaDefinitions
            .get(GetOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId("test-quota-definition-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetOrganizationQuotaDefinitionResponse.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/quota_definitions?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/quota_definitions/GET_response.json")
                .build())
            .build());

        this.quotaDefinitions
            .list(ListOrganizationQuotaDefinitionsRequest.builder()
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListOrganizationQuotaDefinitionsResponse.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/quota_definitions/test-quota-definition-id")
                .payload("fixtures/client/v2/quota_definitions/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/quota_definitions/PUT_{id}_response.json")
                .build())
            .build());

        this.quotaDefinitions
            .update(UpdateOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId("test-quota-definition-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateOrganizationQuotaDefinitionResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:39Z")
                    .id("213b27a0-c937-4074-aade-bba2226980aa")
                    .updatedAt("2016-06-08T16:41:39Z")
                    .url("/v2/quota_definitions/213b27a0-c937-4074-aade-bba2226980aa")
                    .build())
                .entity(OrganizationQuotaDefinitionEntity.builder()
                    .applicationInstanceLimit(-1)
                    .applicationTaskLimit(-1)
                    .instanceMemoryLimit(-1)
                    .memoryLimit(20480)
                    .name("name-1998")
                    .nonBasicServicesAllowed(true)
                    .totalPrivateDomains(-1)
                    .totalReservedRoutePorts(5)
                    .totalRoutes(1000)
                    .totalServiceKeys(-1)
                    .totalServices(60)
                    .trialDatabaseAllowed(false)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
