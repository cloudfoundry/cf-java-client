/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.isolationsegments;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementRequest;
import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementResponse;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentResponse;
import org.cloudfoundry.client.v3.isolationsegments.DeleteIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.GetIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.GetIsolationSegmentResponse;
import org.cloudfoundry.client.v3.isolationsegments.IsolationSegmentResource;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentEntitledOrganizationsRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentEntitledOrganizationsResponse;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentOrganizationsRelationshipRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentOrganizationsRelationshipResponse;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentsRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentsResponse;
import org.cloudfoundry.client.v3.isolationsegments.RemoveIsolationSegmentOrganizationEntitlementRequest;
import org.cloudfoundry.client.v3.isolationsegments.UpdateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.UpdateIsolationSegmentResponse;
import org.cloudfoundry.client.v3.organizations.OrganizationResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorIsolationSegmentsTest extends AbstractClientApiTest {

    private final ReactorIsolationSegments isolationSegments = new ReactorIsolationSegments(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void addOrganizationEntitlement() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/organizations")
                .payload("fixtures/client/v3/isolationsegments/POST_{id}_relationships_organizations_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/isolationsegments/POST_{id}_relationships_organizations_response.json")
                .build())
            .build());

        this.isolationSegments
            .addOrganizationEntitlement(AddIsolationSegmentOrganizationEntitlementRequest.builder()
                .isolationSegmentId("bdeg4371-cbd3-4155-b156-dc0c2a431b4c")
                .data(Relationship.builder()
                    .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                    .build())
                .data(Relationship.builder()
                    .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(AddIsolationSegmentOrganizationEntitlementResponse.builder()
                .data(Relationship.builder()
                    .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                    .build())
                .data(Relationship.builder()
                    .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/organizations")
                    .build())
                .link("related", Link.builder()
                    .href("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/organizations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v3/isolation_segments")
                .payload("fixtures/client/v3/isolationsegments/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/isolationsegments/POST_response.json")
                .build())
            .build());

        this.isolationSegments
            .create(CreateIsolationSegmentRequest.builder()
                .name("my_segment")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateIsolationSegmentResponse.builder()
                .createdAt("2016-10-19T20:25:04Z")
                .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .link("self", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("organizations", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c/organizations")
                    .build())
                .name("an_isolation_segment")
                .updatedAt("2016-11-08T16:41:26Z")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v3/isolation_segments/test-isolation-segment-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.isolationSegments
            .delete(DeleteIsolationSegmentRequest.builder()
                .isolationSegmentId("test-isolation-segment-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/isolationsegments/GET_{id}_response.json")
                .build())
            .build());

        this.isolationSegments
            .get(GetIsolationSegmentRequest.builder()
                .isolationSegmentId("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetIsolationSegmentResponse.builder()
                .createdAt("2016-10-19T20:25:04Z")
                .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .link("self", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("organizations", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c/organizations")
                    .build())
                .name("an_isolation_segment")
                .updatedAt("2016-11-08T16:41:26Z")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/isolation_segments")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/isolationsegments/GET_response.json")
                .build())
            .build());

        this.isolationSegments
            .list(ListIsolationSegmentsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListIsolationSegmentsResponse.builder()
                .pagination(Pagination.builder()
                    .first(Link.builder()
                        .href("/v3/isolation_segments?page=1&per_page=5")
                        .build())
                    .last(Link.builder()
                        .href("/v3/isolation_segments?page=3&per_page=5")
                        .build())
                    .next(Link.builder()
                        .href("/v3/isolation_segments?page=2&per_page=5")
                        .build())
                    .totalPages(3)
                    .totalResults(11)
                    .build())
                .resource(IsolationSegmentResource.builder()
                    .createdAt("2016-10-19T20:25:04Z")
                    .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .link("self", Link.builder()
                        .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                        .build())
                    .link("organizations", Link.builder()
                        .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c/organizations")
                        .build())
                    .name("an_isolation_segment")
                    .updatedAt("2016-11-08T16:41:26Z")
                    .build())
                .resource(IsolationSegmentResource.builder()
                    .createdAt("2016-10-19T20:29:19Z")
                    .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                    .link("self", Link.builder()
                        .href("/v3/isolation_segments/68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                        .build())
                    .link("organizations", Link.builder()
                        .href("/v3/isolation_segments/68d54d31-9b3a-463b-ba94-e8e4c32edbac/organizations")
                        .build())
                    .name("an_isolation_segment1")
                    .updatedAt("2016-11-08T16:41:26Z")
                    .build())
                .resource(IsolationSegmentResource.builder()
                    .createdAt("2016-10-19T20:29:22Z")
                    .id("ecdc67c3-a71e-43ff-bddf-048930b8cd03")
                    .link("self", Link.builder()
                        .href("/v3/isolation_segments/ecdc67c3-a71e-43ff-bddf-048930b8cd03")
                        .build())
                    .link("organizations", Link.builder()
                        .href("/v3/isolation_segments/ecdc67c3-a71e-43ff-bddf-048930b8cd03/organizations")
                        .build())
                    .name("an_isolation_segment2")
                    .updatedAt("2016-11-08T16:41:26Z")
                    .build())
                .resource(IsolationSegmentResource.builder()
                    .createdAt("2016-10-19T20:29:27Z")
                    .id("424c89e4-4353-46b7-9bf4-f90bd9bacac0")
                    .link("self", Link.builder()
                        .href("/v3/isolation_segments/424c89e4-4353-46b7-9bf4-f90bd9bacac0")
                        .build())
                    .link("organizations", Link.builder()
                        .href("/v3/isolation_segments/424c89e4-4353-46b7-9bf4-f90bd9bacac0/organizations")
                        .build())
                    .name("an_isolation_segment3")
                    .updatedAt("2016-11-08T16:41:26Z")
                    .build())
                .resource(IsolationSegmentResource.builder()
                    .createdAt("2016-10-19T20:29:33Z")
                    .id("0a79fcec-a648-4eb8-a6c3-2b5be39047c7")
                    .link("self", Link.builder()
                        .href("/v3/isolation_segments/0a79fcec-a648-4eb8-a6c3-2b5be39047c7")
                        .build())
                    .link("organizations", Link.builder()
                        .href("/v3/isolation_segments/0a79fcec-a648-4eb8-a6c3-2b5be39047c7/organizations")
                        .build())
                    .name("an_isolation_segment4")
                    .updatedAt("2016-11-08T16:41:26Z")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listEntitledOrganizations() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/isolation_segments/933b4c58-120b-499a-b85d-4b6fc9e2903b/organizations")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/isolationsegments/GET_{id}_organizations_response.json")
                .build())
            .build());

        this.isolationSegments
            .listEntitledOrganizations(ListIsolationSegmentEntitledOrganizationsRequest.builder()
                .isolationSegmentId("933b4c58-120b-499a-b85d-4b6fc9e2903b")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListIsolationSegmentEntitledOrganizationsResponse.builder()
                .pagination(Pagination.builder()
                    .first(Link.builder()
                        .href("/v3/isolation_segments/933b4c58-120b-499a-b85d-4b6fc9e2903b/organizations?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("/v3/isolation_segments/933b4c58-120b-499a-b85d-4b6fc9e2903b/organizations?page=1&per_page=50")
                        .build())
                    .totalPages(1)
                    .totalResults(2)
                    .build())
                .resource(OrganizationResource.builder()
                    .createdAt("2017-02-01T01:33:58Z")
                    .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                    .name("org1")
                    .updatedAt("2017-02-01T01:33:58Z")
                    .build())
                .resource(OrganizationResource.builder()
                    .createdAt("2017-02-02T00:14:30Z")
                    .id("d4c91047-7b29-4fda-b7f9-04033e5c9c9f")
                    .name("org2")
                    .updatedAt("2017-02-02T00:14:30Z")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listOrganizationsRelationship() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/organizations")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/isolationsegments/POST_{id}_relationships_organizations_response.json")
                .build())
            .build());

        this.isolationSegments
            .listOrganizationsRelationship(ListIsolationSegmentOrganizationsRelationshipRequest.builder()
                .isolationSegmentId("bdeg4371-cbd3-4155-b156-dc0c2a431b4c")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListIsolationSegmentOrganizationsRelationshipResponse.builder()
                .data(Relationship.builder()
                    .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                    .build())
                .data(Relationship.builder()
                    .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/organizations")
                    .build())
                .link("related", Link.builder()
                    .href("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/organizations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listSpacesRelationship() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/spaces")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/isolationsegments/POST_{id}_relationships_spaces_response.json")
                .build())
            .build());

        this.isolationSegments
            .listSpacesRelationship(ListIsolationSegmentSpacesRelationshipRequest.builder()
                .isolationSegmentId("bdeg4371-cbd3-4155-b156-dc0c2a431b4c")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListIsolationSegmentSpacesRelationshipResponse.builder()
                .data(Relationship.builder()
                    .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                    .build())
                .data(Relationship.builder()
                    .id("d4c91047-7b29-4fda-b7f9-04033e5c9c9f")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/isolation_segments/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/spaces")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeOrganizationEntitlement() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/v3/isolation_segments/test-isolation-segment-id/relationships/organizations/test-organization-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.isolationSegments
            .removeOrganizationEntitlement(RemoveIsolationSegmentOrganizationEntitlementRequest.builder()
                .isolationSegmentId("test-isolation-segment-id")
                .organizationId("test-organization-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .payload("fixtures/client/v3/isolationsegments/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/isolationsegments/PATCH_{id}_response.json")
                .build())
            .build());

        this.isolationSegments
            .update(UpdateIsolationSegmentRequest.builder()
                .isolationSegmentId("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .name("my_isolation_segment")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateIsolationSegmentResponse.builder()
                .createdAt("2016-10-19T20:25:04Z")
                .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .link("self", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("organizations", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c/organizations")
                    .build())
                .name("my_isolation_segment")
                .updatedAt("2016-11-08T16:41:26Z")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
