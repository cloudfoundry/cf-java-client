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

package org.cloudfoundry.reactor.client.v3.domains;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.client.v3.domains.GetDomainRequest;
import org.cloudfoundry.client.v3.domains.GetDomainResponse;
import org.cloudfoundry.client.v3.domains.ListDomainsRequest;
import org.cloudfoundry.client.v3.domains.ListDomainsResponse;
import org.cloudfoundry.client.v3.domains.ShareDomainRequest;
import org.cloudfoundry.client.v3.domains.ShareDomainResponse;
import org.cloudfoundry.client.v3.domains.UnshareDomainRequest;
import org.cloudfoundry.client.v3.domains.UpdateDomainRequest;
import org.cloudfoundry.client.v3.domains.UpdateDomainResponse;
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
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorDomainsV3Test extends AbstractClientApiTest {

    private final ReactorDomainsV3 domains = new ReactorDomainsV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/domains")
                .payload("fixtures/client/v3/domains/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/domains/POST_response.json")
                .build())
            .build());

        this.domains
            .create(CreateDomainRequest.builder()
                .name("test-domain.com")
                .internal(false)
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateDomainResponse.builder()
                .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                .name("test-domain.com")
                .createdAt("2019-03-08T01:06:19Z")
                .updatedAt("2019-03-08T01:06:19Z")
                .isInternal(false)
                .metadata(Metadata.builder()
                    .labels(Collections.emptyMap())
                    .annotations(Collections.emptyMap())
                    .build())
                .relationships(DomainRelationships.builder()
                    .organization(ToOneRelationship.builder().build())
                    .sharedOrganizations(ToManyRelationship.builder().build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                    .build())
                .link("route_reservations", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5/route_reservations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/domains/test-domain-id")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .header("Location", "https://api.example.org/v3/jobs/[guid]")
                .build())
            .build());

        this.domains
            .delete(DeleteDomainRequest.builder()
                .domainId("test-domain-id")
                .build())
            .as(StepVerifier::create)
            .expectNext("[guid]")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/domains/test-domain-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/domains/GET_{id}_response.json")
                .build())
            .build());

        this.domains
            .get(GetDomainRequest.builder()
                .domainId("test-domain-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetDomainResponse.builder()
                .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                .name("test-domain.com")
                .createdAt("2019-03-08T01:06:19Z")
                .updatedAt("2019-03-08T01:06:19Z")
                .isInternal(false)
                .metadata(Metadata.builder()
                    .labels(Collections.emptyMap())
                    .annotations(Collections.emptyMap())
                    .build())
                .relationships(DomainRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("3a3f3d89-3f89-4f05-8188-751b298c79d5")
                            .build())
                        .build())
                    .sharedOrganizations(ToManyRelationship.builder()
                        .data(Relationship.builder()
                            .id("404f3d89-3f89-6z72-8188-751b298d88d5")
                            .build())
                        .data(Relationship.builder()
                            .id("416d3d89-3f89-8h67-2189-123b298d3592")
                            .build())
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                    .build())
                .link("organization", Link.builder()
                    .href("https://api.example.org/v3/organizations/3a3f3d89-3f89-4f05-8188-751b298c79d5")
                    .build())
                .link("route_reservations", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5/route_reservations")
                    .build())
                .link("shared_organizations", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5/relationships/shared_organizations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/domains")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/domains/GET_response.json")
                .build())
            .build());

        this.domains
            .list(ListDomainsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListDomainsResponse.builder()
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
                        .organization(ToOneRelationship.builder().data(null).build())
                        .sharedOrganizations(ToManyRelationship.builder().build())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
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
    public void share() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/domains/test-domain-id/relationships/shared_organizations")
                .payload("fixtures/client/v3/domains/POST_{id}_relationships_shared_organizations_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/domains/POST_{id}_relationships_shared_organizations_response.json")
                .build())
            .build());

        this.domains
            .share(ShareDomainRequest.builder()
                .domainId("test-domain-id")
                .data(Relationship.builder()
                    .id("404f3d89-3f89-6z72-8188-751b298d88d5")
                    .build())
                .data(Relationship.builder()
                    .id("416d3d89-3f89-8h67-2189-123b298d3592")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(ShareDomainResponse.builder()
                .data(Relationship.builder()
                    .id("404f3d89-3f89-6z72-8188-751b298d88d5")
                    .build())
                .data(Relationship.builder()
                    .id("416d3d89-3f89-8h67-2189-123b298d3592")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unshare() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/domains/test-domain-id/relationships/shared_organizations/test-org-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.domains
            .unshare(UnshareDomainRequest.builder()
                .domainId("test-domain-id")
                .organizationId("test-org-id")
                .build())
            .as(StepVerifier::create)
            .expectNext()
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/domains/test-domain-id")
                .payload("fixtures/client/v3/domains/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/domains/PATCH_{id}_response.json")
                .build())
            .build());

        this.domains
            .update(UpdateDomainRequest.builder()
                .domainId("test-domain-id")
                .metadata(Metadata.builder()
                    .annotation("note", "detailed information")
                    .label("key", "value")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateDomainResponse.builder()
                .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                .name("test-domain.com")
                .createdAt("2019-03-08T01:06:19Z")
                .updatedAt("2019-03-08T01:06:19Z")
                .isInternal(false)
                .metadata(Metadata.builder()
                    .label("key", "value")
                    .annotation("note", "detailed information")
                    .build())
                .relationships(DomainRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("3a3f3d89-3f89-4f05-8188-751b298c79d5")
                            .build())
                        .build())
                    .sharedOrganizations(ToManyRelationship.builder()
                        .data(Relationship.builder()
                            .id("404f3d89-3f89-6z72-8188-751b298d88d5")
                            .build())
                        .data(Relationship.builder()
                            .id("416d3d89-3f89-8h67-2189-123b298d3592")
                            .build())
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                    .build())
                .link("organization", Link.builder()
                    .href("https://api.example.org/v3/organizations/3a3f3d89-3f89-4f05-8188-751b298c79d5")
                    .build())
                .link("route_reservations", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5/route_reservations")
                    .build())
                .link("shared_organizations", Link.builder()
                    .href("https://api.example.org/v3/domains/3a5d3d89-3f89-4f05-8188-8a2b298c79d5/relationships/shared_organizations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}
