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

import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.organizationquotadefinitions.*;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class ReactorOrganizationQuotasV3Test extends AbstractClientApiTest {

    private final ReactorOrganizationQuotasV3 organizationQuotasV3 =
            new ReactorOrganizationQuotasV3(
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

        this.organizationQuotasV3
                .create(CreateOrganizationQuotaRequest.builder().name("my-quota").build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateOrganizationQuotaResponse.builder()
                                .from(expectedOrganizationQuotaResource1())
                                .build())
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

        this.organizationQuotasV3
                .delete(
                        DeleteOrganizationQuotaRequest.builder()
                                .organizationQuotaId("test-organization-quota-id")
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
                                        .path(
                                                "/organization_quotas/24637893-3b77-489d-bb79-8466f0d88b52")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/GET_{id}_response.json")
                                        .build())
                        .build());

        this.organizationQuotasV3
                .get(
                        GetOrganizationQuotaRequest.builder()
                                .organizationQuotaId("24637893-3b77-489d-bb79-8466f0d88b52")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetOrganizationQuotaResponse.builder()
                                .from(expectedOrganizationQuotaResource1())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/organization_quotas")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/organization_quotas/GET_response.json")
                                        .build())
                        .build());

        this.organizationQuotasV3
                .list(ListOrganizationQuotasRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListOrganizationQuotasResponse.builder()
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
                                        OrganizationQuotaResource.builder()
                                                .from(expectedOrganizationQuotaResource1())
                                                .build())
                                .resource(
                                        OrganizationQuotaResource.builder()
                                                .from(expectedOrganizationQuotaResource2())
                                                .build())
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
                                        .path(
                                                "/organization_quotas/24637893-3b77-489d-bb79-8466f0d88b52")
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

        this.organizationQuotasV3
                .update(
                        UpdateOrganizationQuotaRequest.builder()
                                .organizationQuotaId("24637893-3b77-489d-bb79-8466f0d88b52")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateOrganizationQuotaResponse.builder()
                                .from(expectedOrganizationQuotaResource1())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @NotNull
    private static OrganizationQuotaResource expectedOrganizationQuotaResource1() {
        return buildOrganizationQuotaResource(
                "24637893-3b77-489d-bb79-8466f0d88b52",
                "my-quota",
                "9b370018-c38e-44c9-86d6-155c76801104");
    }

    private static OrganizationQuotaResource expectedOrganizationQuotaResource2() {
        return buildOrganizationQuotaResource(
                "bb49bf20-ad98-4729-93ae-38fbc564b630",
                "my-quota-2",
                "144251f2-a202-4ffe-ab47-9046c4077e99");
    }

    @NotNull
    private static OrganizationQuotaResource buildOrganizationQuotaResource(
            String id, String name, String relatedOrganizationId) {

        Apps apps =
                Apps.builder()
                        .totalMemoryInMb(5120)
                        .perProcessMemoryInMb(1024)
                        .logRateLimitInBytesPerSecond(1024)
                        .totalInstances(10)
                        .perAppTasks(5)
                        .build();
        Services services =
                Services.builder()
                        .isPaidServicesAllowed(true)
                        .totalServiceInstances(10)
                        .totalServiceKeys(20)
                        .build();
        Routes routes = Routes.builder().totalRoutes(8).totalReservedPorts(4).build();
        Domains domains = Domains.builder().totalDomains(7).build();
        ToManyRelationship organizationRelationships =
                ToManyRelationship.builder()
                        .data(
                                Collections.singletonList(
                                        Relationship.builder().id(relatedOrganizationId).build()))
                        .build();
        OrganizationQuotaRelationships relationships =
                OrganizationQuotaRelationships.builder()
                        .organizations(organizationRelationships)
                        .build();

        return OrganizationQuotaResource.builder()
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
