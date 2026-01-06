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

package org.cloudfoundry.reactor.client.v3.quotas.spaces;

import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import org.cloudfoundry.client.v3.*;
import org.cloudfoundry.client.v3.quotas.Apps;
import org.cloudfoundry.client.v3.quotas.Routes;
import org.cloudfoundry.client.v3.quotas.Services;
import org.cloudfoundry.client.v3.quotas.spaces.*;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class ReactorSpaceQuotasV3Test extends AbstractClientApiTest {

    public static final String EXPECTED_SPACE_QUOTA_ID_1 = "f919ef8a-e333-472a-8172-baaf2c30d301";

    private final ReactorSpaceQuotasV3 spaceQuotasV3 =
            new ReactorSpaceQuotasV3(
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
                                                "fixtures/client/v3/quotas/spaces/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/quotas/spaces/POST_response.json")
                                        .build())
                        .build());

        SpaceQuotaRelationships relationships =
                SpaceQuotaRelationships.builder()
                        .organization(
                                ToOneRelationship.builder()
                                        .data(
                                                Relationship.builder()
                                                        .id("9b370018-c38e-44c9-86d6-155c76801104")
                                                        .build())
                                        .build())
                        .spaces(
                                ToManyRelationship.builder()
                                        .data(
                                                Collections.singletonList(
                                                        Relationship.builder()
                                                                .id(
                                                                        "dcfd6a55-62b9-496e-a26f-0064cec076bf")
                                                                .build()))
                                        .build())
                        .build();
        this.spaceQuotasV3
                .create(
                        CreateSpaceQuotaRequest.builder()
                                .name("my-quota")
                                .relationships(relationships)
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateSpaceQuotaResponse.builder()
                                .from(expectedSpaceQuotaResource1())
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

        this.spaceQuotasV3
                .delete(
                        DeleteSpaceQuotaRequest.builder()
                                .spaceQuotaId("test-space-quota-id")
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
                                                "fixtures/client/v3/quotas/spaces/GET_{id}_response.json")
                                        .build())
                        .build());

        this.spaceQuotasV3
                .get(GetSpaceQuotaRequest.builder().spaceQuotaId(EXPECTED_SPACE_QUOTA_ID_1).build())
                .as(StepVerifier::create)
                .expectNext(
                        GetSpaceQuotaResponse.builder().from(expectedSpaceQuotaResource1()).build())
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
                                                "fixtures/client/v3/quotas/spaces/GET_response.json")
                                        .build())
                        .build());

        this.spaceQuotasV3
                .list(ListSpaceQuotasRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListSpaceQuotasResponse.builder()
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
                                        SpaceQuotaResource.builder()
                                                .from(expectedSpaceQuotaResource1())
                                                .build())
                                .resource(
                                        SpaceQuotaResource.builder()
                                                .from(expectedSpaceQuotaResource2())
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
                                        .path("/space_quotas/" + EXPECTED_SPACE_QUOTA_ID_1)
                                        .payload(
                                                "fixtures/client/v3/quotas/spaces/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/quotas/spaces/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.spaceQuotasV3
                .update(
                        UpdateSpaceQuotaRequest.builder()
                                .spaceQuotaId(EXPECTED_SPACE_QUOTA_ID_1)
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateSpaceQuotaResponse.builder()
                                .from(expectedSpaceQuotaResource1())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void apply() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path(
                                                "/space_quotas/24637893-3b77-489d-bb79-8466f0d88b52/relationships/spaces")
                                        .payload(
                                                "fixtures/client/v3/quotas/spaces/relationships/POST_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/quotas/spaces/relationships/POST_{id}_response.json")
                                        .build())
                        .build());

        Relationship space1 = Relationship.builder().id("space-guid1").build();
        Relationship space2 = Relationship.builder().id("space-guid2").build();

        ToManyRelationship organizationRelationships =
                ToManyRelationship.builder().data(space1, space2).build();

        this.spaceQuotasV3
                .apply(
                        ApplySpaceQuotaRequest.builder()
                                .spaceQuotaId("24637893-3b77-489d-bb79-8466f0d88b52")
                                .spaceRelationships(organizationRelationships)
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        ApplySpaceQuotaResponse.builder()
                                .from(expectedApplySpaceQuotaResponse())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void remove() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path(
                                                "/space_quotas/test-space-quota-id/relationships/spaces/test-space-guid")
                                        .build())
                        .response(TestResponse.builder().status(NO_CONTENT).build())
                        .build());

        this.spaceQuotasV3
                .remove(
                        RemoveSpaceQuotaRequest.builder()
                                .spaceQuotaId("test-space-quota-id")
                                .spaceId("test-space-guid")
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @NotNull
    private static SpaceQuotaResource expectedSpaceQuotaResource1() {
        return buildSpaceQuotaResource(
                EXPECTED_SPACE_QUOTA_ID_1,
                "my-quota",
                "9b370018-c38e-44c9-86d6-155c76801104",
                "dcfd6a55-62b9-496e-a26f-0064cec076bf");
    }

    private static SpaceQuotaResource expectedSpaceQuotaResource2() {
        return buildSpaceQuotaResource(
                "bb49bf20-ad98-4729-93ae-38fbc564b630",
                "my-quota-2",
                "9b370018-c38e-44c9-86d6-155c76801104",
                null);
    }

    @NotNull
    private static ApplySpaceQuotaResponse expectedApplySpaceQuotaResponse() {

        Relationship space1 = Relationship.builder().id("space-guid1").build();
        Relationship space2 = Relationship.builder().id("space-guid2").build();
        Relationship existingSpace = Relationship.builder().id("previous-space-guid").build();

        ToManyRelationship spaceRelationships =
                ToManyRelationship.builder().data(space1, space2, existingSpace).build();
        Link selfLink =
                Link.builder()
                        .href(
                                "https://api.example.org/v3/space_quotas/24637893-3b77-489d-bb79-8466f0d88b52/relationships/spaces")
                        .build();
        Map<String, Link> links = Collections.singletonMap("self", selfLink);

        return ApplySpaceQuotaResponse.builder()
                .spaceRelationships(spaceRelationships)
                .links(links)
                .build();
    }

    @NotNull
    private static SpaceQuotaResource buildSpaceQuotaResource(
            String id, String name, String relatedOrganizationId, String relatedSpaceId) {

        Apps apps =
                Apps.builder()
                        .totalMemoryInMb(5120)
                        .perProcessMemoryInMb(1024)
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

        ToOneRelationship organizationRelationship =
                ToOneRelationship.builder()
                        .data(Relationship.builder().id(relatedOrganizationId).build())
                        .build();
        ToManyRelationship spaceRelationships =
                ToManyRelationship.builder().data(Collections.emptyList()).build();
        if (relatedSpaceId != null) {
            spaceRelationships =
                    ToManyRelationship.builder()
                            .data(
                                    Collections.singletonList(
                                            Relationship.builder().id(relatedSpaceId).build()))
                            .build();
        }
        SpaceQuotaRelationships relationships =
                SpaceQuotaRelationships.builder()
                        .organization(organizationRelationship)
                        .spaces(spaceRelationships)
                        .build();

        return SpaceQuotaResource.builder()
                .createdAt("2016-05-04T17:00:41Z")
                .id(id)
                .link(
                        "self",
                        Link.builder()
                                .href("https://api.example.org/v3/space_quotas/" + id)
                                .build())
                .link(
                        "organization",
                        Link.builder()
                                .href(
                                        "https://api.example.org/v3/organizations/"
                                                + relatedOrganizationId)
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
