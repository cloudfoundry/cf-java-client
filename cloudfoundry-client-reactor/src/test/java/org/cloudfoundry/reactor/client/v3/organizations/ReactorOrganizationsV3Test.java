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

package org.cloudfoundry.reactor.client.v3.organizations;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.client.v3.organizations.AssignOrganizationDefaultIsolationSegmentRequest;
import org.cloudfoundry.client.v3.organizations.AssignOrganizationDefaultIsolationSegmentResponse;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.organizations.GetOrganizationDefaultIsolationSegmentRequest;
import org.cloudfoundry.client.v3.organizations.GetOrganizationDefaultIsolationSegmentResponse;
import org.cloudfoundry.client.v3.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v3.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v3.organizations.ListOrganizationDomainsResponse;
import org.cloudfoundry.client.v3.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v3.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v3.organizations.OrganizationResource;
import org.cloudfoundry.client.v3.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorOrganizationsV3Test extends AbstractClientApiTest {

    private final ReactorOrganizationsV3 organizations = new ReactorOrganizationsV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void assignDefaultIsolationSegment() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/organizations/test-organization-id/relationships/default_isolation_segment")
                .payload("fixtures/client/v3/organizations/PATCH_{id}_relationships_default_isolation_segment_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/organizations/PATCH_{id}_relationships_default_isolation_segment_response.json")
                .build())
            .build());

        this.organizations
            .assignDefaultIsolationSegment(AssignOrganizationDefaultIsolationSegmentRequest.builder()
                .data(Relationship.builder()
                    .id("[iso-seg-guid]")
                    .build())
                .organizationId("test-organization-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssignOrganizationDefaultIsolationSegmentResponse.builder()
                .data(Relationship.builder()
                    .id("9d8e007c-ce52-4ea7-8a57-f2825d2c6b39")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/organizations/d4c91047-7b29-4fda-b7f9-04033e5c9c9f/relationships/default_isolation_segment")
                    .build())
                .link("related", Link.builder()
                    .href("https://api.example.org/v3/isolation_segments/9d8e007c-ce52-4ea7-8a57-f2825d2c6b39")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/organizations")
                .payload("fixtures/client/v3/organizations/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/organizations/POST_response.json")
                .build())
            .build());

        this.organizations
            .create(CreateOrganizationRequest.builder()
                .name("my-organization")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateOrganizationResponse.builder()
                .createdAt("2017-02-01T01:33:58Z")
                .id("24637893-3b77-489d-bb79-8466f0d88b52")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/organizations/24637893-3b77-489d-bb79-8466f0d88b52")
                    .build())
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .name("my-organization")
                .updatedAt("2017-02-01T01:33:58Z")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/organizations/24637893-3b77-489d-bb79-8466f0d88b52")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/organizations/GET_{id}_response.json")
                .build())
            .build());

        this.organizations
            .get(GetOrganizationRequest.builder()
                .organizationId("24637893-3b77-489d-bb79-8466f0d88b52")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetOrganizationResponse.builder()
                .createdAt("2017-02-01T01:33:58Z")
                .id("24637893-3b77-489d-bb79-8466f0d88b52")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/organizations/24637893-3b77-489d-bb79-8466f0d88b52")
                    .build())
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .name("my-organization")
                .updatedAt("2017-02-01T01:33:58Z")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getDefaultIsolationSegment() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/organizations/test-organization-id/relationships/default_isolation_segment")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/organizations/GET_{id}_relationships_default_isolation_segment_response.json")
                .build())
            .build());

        this.organizations
            .getDefaultIsolationSegment(GetOrganizationDefaultIsolationSegmentRequest.builder()
                .organizationId("test-organization-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetOrganizationDefaultIsolationSegmentResponse.builder()
                .data(Relationship.builder()
                    .id("9d8e007c-ce52-4ea7-8a57-f2825d2c6b39")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/organizations/d4c91047-7b29-4fda-b7f9-04033e5c9c9f/relationships/default_isolation_segment")
                    .build())
                .link("related", Link.builder()
                    .href("https://api.example.org/v3/isolation_segments/9d8e007c-ce52-4ea7-8a57-f2825d2c6b39")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/organizations")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/organizations/GET_response.json")
                .build())
            .build());

        this.organizations
            .list(ListOrganizationsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListOrganizationsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/isolation_segments/933b4c58-120b-499a-b85d-4b6fc9e2903b/organizations?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/isolation_segments/933b4c58-120b-499a-b85d-4b6fc9e2903b/organizations?page=1&per_page=50")
                        .build())
                    .build())
                .resource(OrganizationResource.builder()
                    .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                    .metadata(Metadata.builder()
                        .annotations(Collections.emptyMap())
                        .labels(Collections.emptyMap())
                        .build())
                    .createdAt("2017-02-01T01:33:58Z")
                    .updatedAt("2017-02-01T01:33:58Z")
                    .name("org1")
                    .build())
                .resource(OrganizationResource.builder()
                    .id("d4c91047-7b29-4fda-b7f9-04033e5c9c9f")
                    .metadata(Metadata.builder()
                        .annotations(Collections.emptyMap())
                        .labels(Collections.emptyMap())
                        .build())
                    .createdAt("2017-02-02T00:14:30Z")
                    .updatedAt("2017-02-02T00:14:30Z")
                    .name("org2")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDomains() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/organizations/test-organization-id/domains")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/organizations/GET_{id}_domains_response.json")
                .build())
            .build());

        this.organizations
            .listDomains(ListOrganizationDomainsRequest.builder()
                .organizationId("test-organization-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListOrganizationDomainsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/domains?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/domains?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/domains?page=2&per_page=2")
                        .build())
                    .build())
                .resource(DomainResource.builder()
                    .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                    .metadata(Metadata.builder()
                        .annotations(Collections.emptyMap())
                        .labels(Collections.emptyMap())
                        .build())
                    .createdAt("2019-03-08T01:06:19Z")
                    .updatedAt("2019-03-08T01:06:19Z")
                    .name("test-domain.com")
                    .isInternal(false)
                    .relationships(DomainRelationships.builder()
                        .organization(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("test-organization-id")
                                .build())
                            .build())
                        .sharedOrganizations(ToManyRelationship.builder().build())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                        .build())
                    .link("organization", Link.builder()
                        .href("https://api.example.org/v3/organizations/test-organization-id")
                        .build())
                    .link("route_reservations", Link.builder()
                        .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5/route_reservations")
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
                .method(PATCH).path("/organizations/test-organization-id")
                .payload("fixtures/client/v3/organizations/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/organizations/PATCH_{id}_response.json")
                .build())
            .build());

        this.organizations
            .update(UpdateOrganizationRequest.builder()
                .organizationId("test-organization-id")
                .metadata(Metadata.builder()
                    .annotation("version", "1.2.4")
                    .label("dept", "1234")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateOrganizationResponse.builder()
                .createdAt("2017-02-01T01:33:58Z")
                .id("24637893-3b77-489d-bb79-8466f0d88b52")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/organizations/24637893-3b77-489d-bb79-8466f0d88b52")
                    .build())
                .metadata(Metadata.builder()
                    .annotation("version", "1.2.4")
                    .label("dept", "1234")
                    .build())
                .name("my-organization")
                .updatedAt("2017-02-01T01:33:58Z")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
