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

package org.cloudfoundry.reactor.client.v3.spacequotadefinition;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.spacequotadefinitions.Apps;
import org.cloudfoundry.client.v3.spacequotadefinitions.CreateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.CreateSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.spacequotadefinitions.DeleteSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.ListSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v3.spacequotadefinitions.Routes;
import org.cloudfoundry.client.v3.spacequotadefinitions.Services;
import org.cloudfoundry.client.v3.spacequotadefinitions.SpaceQuotaDefinitionRelationships;
import org.cloudfoundry.client.v3.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v3.spacequotadefinitions.UpdateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.UpdateSpaceQuotaDefinitionResponse;
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

class ReactorSpaceQuotaDefinitionsV3Test extends AbstractClientApiTest {

    public static final String EXPECTED_SPACE_QUOTA_ID_1 = "f919ef8a-e333-472a-8172-baaf2c30d301";

    private final ReactorSpaceQuotaDefinitionsV3 spaceQuotaDefinitionsV3 =
            new ReactorSpaceQuotaDefinitionsV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/space_quotas")
                                        .payload(
                                                "fixtures/client/v3/space_quotas/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/space_quotas/POST_response.json")
                                        .build())
                        .build());

        SpaceQuotaDefinitionRelationships relationships = SpaceQuotaDefinitionRelationships
                .builder()
                .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                                .id("9b370018-c38e-44c9-86d6-155c76801104")
                                .build())
                        .build())
                .spaces(ToManyRelationship.builder()
                        .data(Collections.singletonList(Relationship.builder()
                                .id("dcfd6a55-62b9-496e-a26f-0064cec076bf")
                                .build()))
                        .build())
                .build();
        this.spaceQuotaDefinitionsV3
                .create(CreateSpaceQuotaDefinitionRequest.builder().name("my-quota").relationships(relationships).build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateSpaceQuotaDefinitionResponse
                                .builder()
                                .from(expectedSpaceQuotaDefinitionResource1())
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
                                        .path("/space_quotas/test-space-quota-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/test-job-id")
                                        .build())
                        .build());

        this.spaceQuotaDefinitionsV3
                .delete(
                        DeleteSpaceQuotaDefinitionRequest.builder()
                                .spaceQuotaDefinitionId("test-space-quota-id")
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
                                        .path("/space_quotas/" + EXPECTED_SPACE_QUOTA_ID_1)
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/space_quotas/GET_{id}_response.json")
                                        .build())
                        .build());

        this.spaceQuotaDefinitionsV3
                .get(
                        GetSpaceQuotaDefinitionRequest.builder()
                                .spaceQuotaDefinitionId(EXPECTED_SPACE_QUOTA_ID_1)
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetSpaceQuotaDefinitionResponse.builder()
                                .from(expectedSpaceQuotaDefinitionResource1())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/space_quotas").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/space_quotas/GET_response.json")
                                        .build())
                        .build());

        this.spaceQuotaDefinitionsV3
                .list(ListSpaceQuotaDefinitionsRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListSpaceQuotaDefinitionsResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(2)
                                                .totalPages(1)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/space_quotas?page=1&per_page=50")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/space_quotas?page=1&per_page=50")
                                                                .build())
                                                .build())
                                .resource(
                                        SpaceQuotaDefinitionResource
                                                .builder()
                                                .from(expectedSpaceQuotaDefinitionResource1()).build()
                                )
                                .resource(
                                        SpaceQuotaDefinitionResource
                                                .builder()
                                                .from(expectedSpaceQuotaDefinitionResource2()).build()
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
                                        .path("/space_quotas/" + EXPECTED_SPACE_QUOTA_ID_1)
                                        .payload(
                                                "fixtures/client/v3/space_quotas/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/space_quotas/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.spaceQuotaDefinitionsV3
                .update(
                        UpdateSpaceQuotaDefinitionRequest.builder()
                                .spaceQuotaDefinitionId(EXPECTED_SPACE_QUOTA_ID_1)
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateSpaceQuotaDefinitionResponse.builder()
                                .from(expectedSpaceQuotaDefinitionResource1())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @NotNull
    private static SpaceQuotaDefinitionResource expectedSpaceQuotaDefinitionResource1() {
        return buildSpaceQuotaDefinitionResource(EXPECTED_SPACE_QUOTA_ID_1, "my-quota", "9b370018-c38e-44c9-86d6-155c76801104", "dcfd6a55-62b9-496e-a26f-0064cec076bf");
    }

    private static SpaceQuotaDefinitionResource expectedSpaceQuotaDefinitionResource2() {
        return buildSpaceQuotaDefinitionResource("bb49bf20-ad98-4729-93ae-38fbc564b630", "my-quota-2", "9b370018-c38e-44c9-86d6-155c76801104", null);
    }

    @NotNull
    private static SpaceQuotaDefinitionResource buildSpaceQuotaDefinitionResource(String id, String name, String relatedOrganizationId, String relatedSpaceId) {

        Apps apps = Apps.builder()
                .totalMemoryInMb(5120)
                .perProcessMemoryInMb(1024)
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

        ToOneRelationship organizationRelationship = ToOneRelationship.builder()
                .data(
                        Relationship
                                .builder()
                                .id(relatedOrganizationId)
                                .build())
                .build();
        ToManyRelationship spaceRelationships = ToManyRelationship.builder().data(Collections.emptyList()).build();
        if (relatedSpaceId != null) {
            spaceRelationships = ToManyRelationship.builder()
                    .data(
                            Collections.singletonList(
                                    Relationship
                                            .builder()
                                            .id(relatedSpaceId)
                                            .build()))
                    .build();
        }
        SpaceQuotaDefinitionRelationships relationships =
                SpaceQuotaDefinitionRelationships
                        .builder()
                        .organization(organizationRelationship)
                        .spaces(spaceRelationships)
                        .build();

        return SpaceQuotaDefinitionResource.builder()
                .createdAt("2016-05-04T17:00:41Z")
                .id(id)
                .link(
                        "self",
                        Link.builder()
                                .href("https://api.example.org/v3/space_quotas/" + id)
                                .build())
                .link("organization",
                        Link.builder()
                                .href("https://api.example.org/v3/organizations/" + relatedOrganizationId)
                                .build())
                .name(name)
                .updatedAt("2016-05-04T18:00:41Z")
                .apps(apps)
                .services(services)
                .routes(routes)
                .relationships(relationships)
                .build();
    }
}
