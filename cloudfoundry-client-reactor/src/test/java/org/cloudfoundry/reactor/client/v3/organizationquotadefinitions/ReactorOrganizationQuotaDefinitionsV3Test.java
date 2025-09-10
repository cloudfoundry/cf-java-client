/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.organizationquotadefinitions;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.organizationquotadefinitions.Apps;
import org.cloudfoundry.client.v3.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.Domains;
import org.cloudfoundry.client.v3.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v3.organizationquotadefinitions.OrganizationQuotaDefinitionRelationships;
import org.cloudfoundry.client.v3.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v3.organizationquotadefinitions.Routes;
import org.cloudfoundry.client.v3.organizationquotadefinitions.Services;
import org.cloudfoundry.client.v3.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

class ReactorOrganizationQuotaDefinitionsV3Test extends AbstractClientApiTest {

    private final ReactorOrganizationQuotaDefinitionsV3 organizationQuotaDefinitionsV3 =
            new ReactorOrganizationQuotaDefinitionsV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/organization_quotas")
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/POST_response.json")
                                        .build())
                        .build());

        this.organizationQuotaDefinitionsV3
                .create(CreateOrganizationQuotaDefinitionRequest.builder().name("my-quota").build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateOrganizationQuotaDefinitionResponse
                                .builder()
                                .from(expectedOrganizationQuotaDefinitionResource1())
                                .build()
                )
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void delete() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path("/organization_quotas/test-organization-quota-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/test-job-id")
                                        .build())
                        .build());

        this.organizationQuotaDefinitionsV3
                .delete(
                        DeleteOrganizationQuotaDefinitionRequest.builder()
                                .organizationQuotaDefinitionId("test-organization-quota-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext("test-job-id")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/organization_quotas/24637893-3b77-489d-bb79-8466f0d88b52")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/GET_{id}_response.json")
                                        .build())
                        .build());

        this.organizationQuotaDefinitionsV3
                .get(
                        GetOrganizationQuotaDefinitionRequest.builder()
                                .organizationQuotaDefinitionId("24637893-3b77-489d-bb79-8466f0d88b52")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetOrganizationQuotaDefinitionResponse.builder()
                                .from(expectedOrganizationQuotaDefinitionResource1())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/organization_quotas").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/GET_response.json")
                                        .build())
                        .build());

        this.organizationQuotaDefinitionsV3
                .list(ListOrganizationQuotaDefinitionsRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListOrganizationQuotaDefinitionsResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(2)
                                                .totalPages(1)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/organization_quotas?page=1&per_page=50")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/organization_quotas?page=1&per_page=50")
                                                                .build())
                                                .build())
                                .resource(
                                        OrganizationQuotaDefinitionResource
                                                .builder()
                                                .from(expectedOrganizationQuotaDefinitionResource1()).build()
                                )
                                .resource(
                                        OrganizationQuotaDefinitionResource
                                                .builder()
                                                .from(expectedOrganizationQuotaDefinitionResource2()).build()
                                )
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void update() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path("/organization_quotas/24637893-3b77-489d-bb79-8466f0d88b52")
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.organizationQuotaDefinitionsV3
                .update(
                        UpdateOrganizationQuotaDefinitionRequest.builder()
                                .organizationQuotaDefinitionId("24637893-3b77-489d-bb79-8466f0d88b52")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateOrganizationQuotaDefinitionResponse.builder()
                                .from(expectedOrganizationQuotaDefinitionResource1())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @NotNull
    private static OrganizationQuotaDefinitionResource expectedOrganizationQuotaDefinitionResource1() {
        return buildOrganizationQuotaDefinitionResource("24637893-3b77-489d-bb79-8466f0d88b52", "my-quota", "9b370018-c38e-44c9-86d6-155c76801104");
    }

    private static OrganizationQuotaDefinitionResource expectedOrganizationQuotaDefinitionResource2() {
        return buildOrganizationQuotaDefinitionResource("bb49bf20-ad98-4729-93ae-38fbc564b630", "my-quota-2", "144251f2-a202-4ffe-ab47-9046c4077e99");
    }

    @NotNull
    private static OrganizationQuotaDefinitionResource buildOrganizationQuotaDefinitionResource(String id, String name, String relatedOrganizationId) {

        Apps apps = Apps.builder()
                .totalMemoryInMb(5120)
                .perProcessMemoryInMb(1024)
                .logRateLimitInBytesPerSecond(1024)
                .totalInstances(10)
                .perAppTasks(5)
                .build();
        Services services = Services.builder()
                .isPaidServicesAllowed(true)
                .totalServiceInstances(10)
                .totalServiceKeys(20)
                .build();
        Routes routes = Routes.builder()
                .totalRoutes(8)
                .totalReservedPorts(4)
                .build();
        Domains domains = Domains.builder()
                .totalDomains(7)
                .build();
        ToManyRelationship organizationRelationships = ToManyRelationship.builder()
                .data(
                        Collections.singletonList(
                                Relationship
                                        .builder()
                                        .id(relatedOrganizationId)
                                        .build()))
                .build();
        OrganizationQuotaDefinitionRelationships relationships =
                OrganizationQuotaDefinitionRelationships
                        .builder()
                        .organizations(organizationRelationships)
                        .build();

        return OrganizationQuotaDefinitionResource.builder()
                .createdAt("2016-05-04T17:00:41Z")
                .id(id)
                .link(
                        "self",
                        Link.builder()
                                .href("https://api.example.org/v3/organization_quotas/" + id)
                                .build())
                .name(name)
                .updatedAt("2016-05-04T17:00:41Z")
                .apps(apps)
                .services(services)
                .routes(routes)
                .domains(domains)
                .relationships(relationships)
                .build();
    }
}
