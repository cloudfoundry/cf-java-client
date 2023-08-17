/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.v3.mapper;

import org.cloudfoundry.client.v3.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v3.organizations.ListOrganizationDomainsResponse;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.client.v3.ToOneRelationship;

import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.client.v3.Pagination;
import java.lang.IllegalStateException;
import org.cloudfoundry.client.v3.Link;

import reactor.core.publisher.Mono;
import java.time.Duration;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import reactor.test.StepVerifier;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DomainIdByNameTest extends AbstractOperationsTest {

    private final String TEST_DOMAIN_ID = "3a5d3d89-3f89-4f05-8188-8a2b298c79d5";
    private final String TEST_DOMAIN_NAME = "domain-name";

@Before
    public void settup() {
            when(this.cloudFoundryClient.organizationsV3().listDomains(
                    ListOrganizationDomainsRequest.builder()
                            .name(TEST_DOMAIN_NAME)
                            .page(1)
                            .organizationId(TEST_ORGANIZATION_ID).build()))
                    .thenReturn(Mono.just( ListOrganizationDomainsResponse.builder()
                    .pagination(Pagination.builder()
                        .totalResults(1)
                        .totalPages(1)
                        .first(Link.builder()
                            .href("https://api.example.org/v3/domains?page=1&per_page=2")
                            .build())
                        .last(Link.builder()
                            .href("https://api.example.org/v3/domains?page=1&per_page=2")
                            .build())
                        .build())
                    .resource(DomainResource.builder()
                        .id(TEST_DOMAIN_ID)
                        .createdAt("2019-03-08T01:06:19Z")
                        .updatedAt("2019-03-08T01:06:19Z")
                        .name(TEST_DOMAIN_NAME)
                        .isInternal(false)
                        .relationships(DomainRelationships.builder()
                            .organization(ToOneRelationship.builder()
                                .data(Relationship.builder()
                                    .id(TEST_ORGANIZATION_ID)
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
                    .build()));
    }

    @Test
    public void getDomainIdByName() {
        MapperUtils.getDomainIdByName(
                this.cloudFoundryClient,
                TEST_ORGANIZATION_ID,
                TEST_DOMAIN_NAME)
                .as(StepVerifier::create)
                .expectNext(TEST_DOMAIN_ID)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

}
