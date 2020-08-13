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

package org.cloudfoundry.reactor.client.v3.spaces;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.spaces.AssignSpaceIsolationSegmentRequest;
import org.cloudfoundry.client.v3.spaces.AssignSpaceIsolationSegmentResponse;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v3.spaces.DeleteUnmappedRoutesRequest;
import org.cloudfoundry.client.v3.spaces.GetSpaceIsolationSegmentRequest;
import org.cloudfoundry.client.v3.spaces.GetSpaceIsolationSegmentResponse;
import org.cloudfoundry.client.v3.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v3.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v3.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.client.v3.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.UpdateSpaceResponse;
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
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorSpacesV3Test extends AbstractClientApiTest {

    private final ReactorSpacesV3 spaces = new ReactorSpacesV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void assignIsolationSegment() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/spaces/test-space-id/relationships/isolation_segment")
                .payload("fixtures/client/v3/spaces/PATCH_{id}_relationships_isolation_segment_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/spaces/PATCH_{id}_relationships_isolation_segment_response.json")
                .build())
            .build());

        this.spaces
            .assignIsolationSegment(AssignSpaceIsolationSegmentRequest.builder()
                .data(Relationship.builder()
                    .id("[iso-seg-guid]")
                    .build())
                .spaceId("test-space-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssignSpaceIsolationSegmentResponse.builder()
                .data(Relationship.builder()
                    .id("e4c91047-3b29-4fda-b7f9-04033e5a9c9f")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/spaces/885735b5-aea4-4cf5-8e44-961af0e41920/relationships/isolation_segment")
                    .build())
                .link("related", Link.builder()
                    .href("https://api.example.org/v3/isolation_segments/e4c91047-3b29-4fda-b7f9-04033e5a9c9f")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/spaces")
                .payload("fixtures/client/v3/spaces/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/spaces/POST_response.json")
                .build())
            .build());

        this.spaces
            .create(CreateSpaceRequest.builder()
                .name("my-space")
                .relationships(SpaceRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("e00705b9-7b42-4561-ae97-2520399d2133")
                            .build())
                        .build())
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateSpaceResponse.builder()
                .createdAt("2017-02-01T01:33:58Z")
                .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                .name("my-space")
                .relationships(SpaceRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("e00705b9-7b42-4561-ae97-2520399d2133")
                            .build())
                        .build())
                    .build())
                .updatedAt("2017-02-01T01:33:58Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/spaces/885735b5-aea4-4cf5-8e44-961af0e41920")
                    .build())
                .link("organization", Link.builder()
                    .href("https://api.example.org/v3/organizations/e00705b9-7b42-4561-ae97-2520399d2133")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/spaces/test-space-id")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .header("Location", "https://api.example.org/v3/jobs/test-job-id")
                .build())
            .build());

        this.spaces
            .delete(DeleteSpaceRequest.builder()
                .spaceId("test-space-id")
                .build())
            .as(StepVerifier::create)
            .expectNext("test-job-id")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteUnmappedRoutes() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/spaces/test-space-id/routes?unmapped=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .header("Location", "https://api.example.org/v3/jobs/test-job-id")
                .build())
            .build());

        this.spaces
            .deleteUnmappedRoutes(DeleteUnmappedRoutesRequest.builder()
                .spaceId("test-space-id")
                .build())
            .as(StepVerifier::create)
            .expectNext("test-job-id")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/spaces/test-space-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/spaces/GET_{id}_response.json")
                .build())
            .build());

        this.spaces
            .get(GetSpaceRequest.builder()
                .spaceId("test-space-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetSpaceResponse.builder()
                .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                .createdAt("2017-02-01T01:33:58Z")
                .updatedAt("2017-02-01T01:33:58Z")
                .name("space1")
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/spaces/885735b5-aea4-4cf5-8e44-961af0e41920")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getIsolationSegment() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/spaces/test-space-id/relationships/isolation_segment")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/spaces/GET_{id}_relationships_isolation_segment_response.json")
                .build())
            .build());

        this.spaces
            .getIsolationSegment(GetSpaceIsolationSegmentRequest.builder()
                .spaceId("test-space-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetSpaceIsolationSegmentResponse.builder()
                .data(Relationship.builder()
                    .id("e4c91047-3b29-4fda-b7f9-04033e5a9c9f")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/spaces/885735b5-aea4-4cf5-8e44-961af0e41920/relationships/isolation_segment")
                    .build())
                .link("related", Link.builder()
                    .href("https://api.example.org/v3/isolation_segments/e4c91047-3b29-4fda-b7f9-04033e5a9c9f")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/spaces")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/spaces/GET_response.json")
                .build())
            .build());

        this.spaces
            .list(ListSpacesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSpacesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/spaces?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/spaces?page=1&per_page=50")
                        .build())
                    .build())
                .resource(SpaceResource.builder()
                    .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                    .createdAt("2017-02-01T01:33:58Z")
                    .updatedAt("2017-02-01T01:33:58Z")
                    .name("space1")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/spaces/885735b5-aea4-4cf5-8e44-961af0e41920")
                        .build())
                    .build())
                .resource(SpaceResource.builder()
                    .id("d4c91047-7b29-4fda-b7f9-04033e5c9c9f")
                    .createdAt("2017-02-02T00:14:30Z")
                    .updatedAt("2017-02-02T00:14:30Z")
                    .name("space2")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/spaces/d4c91047-7b29-4fda-b7f9-04033e5c9c9f")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/spaces/test-space-id")
                .payload("fixtures/client/v3/spaces/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/spaces/PATCH_{id}_response.json")
                .build())
            .build());

        this.spaces
            .update(UpdateSpaceRequest.builder()
                .spaceId("test-space-id")
                .metadata(Metadata.builder()
                    .annotation("version", "1.2.4")
                    .label("dept", "1234")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateSpaceResponse.builder()
                .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                .createdAt("2017-02-01T01:33:58Z")
                .updatedAt("2017-02-01T01:33:58Z")
                .name("space1")
                .metadata(Metadata.builder()
                    .annotation("version", "1.2.4")
                    .label("dept", "1234")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/spaces/885735b5-aea4-4cf5-8e44-961af0e41920")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
