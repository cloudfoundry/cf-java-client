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

package org.cloudfoundry.operations.domains;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.client.v3.domains.ListDomainsRequest;
import org.cloudfoundry.client.v3.domains.ListDomainsResponse;
import org.cloudfoundry.client.v3.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v3.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v3.organizations.OrganizationResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

final class DefaultDomainsTest extends AbstractOperationsTest {

    private final DefaultDomains domains =
            new DefaultDomains(this.cloudFoundryClient, this.routingClient);

    @Test
    void createDomain() {
        requestOrganizations(this.cloudFoundryClient, "test-organization");
        requestCreateDomain(this.cloudFoundryClient, "test-domain", "test-organization-id");

        this.domains
                .create(
                        CreateDomainRequest.builder()
                                .domain("test-domain")
                                .organization("test-organization")
                                .build())
                .as(StepVerifier::create)
                .verifyComplete();

        verify(this.cloudFoundryClient.domainsV3())
                .create(
                        argThat(
                                a ->
                                        a.getName().equals("test-domain")
                                                && a.getRelationships()
                                                        .getOrganization()
                                                        .getData()
                                                        .getId()
                                                        .equals("test-organization-id")));
        verifyNoInteractions(this.routingClient.routerGroups().list(any()));
    }

    @Test
    void createSharedDomain() {
        requestCreateDomain(this.cloudFoundryClient, "test-domain");

        this.domains
                .createShared(CreateSharedDomainRequest.builder().domain("test-domain").build())
                .as(StepVerifier::create)
                .verifyComplete();

        verify(this.cloudFoundryClient.domainsV3())
                .create(argThat(a -> a.getName().equals("test-domain")));
        verifyNoInteractions(this.cloudFoundryClient.organizationsV3().list(any()));
        verifyNoInteractions(this.routingClient.routerGroups().list(any()));
    }

    @Test
    void createDomainRouterGroup() {
        requestCreateDomain(this.cloudFoundryClient, "test-domain");
        requestListRouterGroups(this.routingClient, "test-router-group");

        this.domains
                .create(
                        CreateDomainRequest.builder()
                                .domain("test-domain")
                                .routerGroup("test-router-group")
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));

        verifyNoInteractions(this.cloudFoundryClient.organizationsV3().list(any()));
        verify(this.routingClient.routerGroups()).list(any());
        verify(this.cloudFoundryClient.domainsV3())
                .create(argThat(a -> a.getRouterGroup().getId().equals("test-routerGroupId")));
    }

    @Test
    void createSharedDomainRouterGroup() {
        requestCreateDomain(this.cloudFoundryClient, "test-domain");
        requestListRouterGroups(this.routingClient, "test-router-group");

        this.domains
                .createShared(
                        CreateSharedDomainRequest.builder()
                                .domain("test-domain")
                                .routerGroup("test-router-group")
                                .build())
                .as(StepVerifier::create)
                .verifyComplete();

        verifyNoInteractions(this.cloudFoundryClient.organizationsV3().list(any()));
        verify(this.routingClient.routerGroups()).list(any());
        verify(this.cloudFoundryClient.domainsV3())
                .create(argThat(a -> a.getRouterGroup().getId().equals("test-routerGroupId")));
    }

    @Test
    void listDomains() {
        requestListRouterGroups(this.routingClient, "test-router-group");
        requestListDomains(this.cloudFoundryClient, "test-organization-id", null);

        this.domains
                .list()
                .as(StepVerifier::create)
                .expectNext(
                        Domain.builder()
                                .id("test-domain-id")
                                .name("test-domain-name")
                                .status(Status.OWNED)
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void listDomainsShared() {
        requestListRouterGroups(this.routingClient, "test-router-group");
        requestListDomains(this.cloudFoundryClient, null, null);

        this.domains
                .list()
                .as(StepVerifier::create)
                .expectNext(
                        Domain.builder()
                                .id("test-domain-id")
                                .name("test-domain-name")
                                .status(Status.SHARED)
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void listDomainsTcp() {
        requestListDomains(this.cloudFoundryClient, null, "test-routerGroupId");
        requestListRouterGroups(this.routingClient, "test-tcp-group");

        this.domains
                .list()
                .as(StepVerifier::create)
                .expectNext(
                        Domain.builder()
                                .id("test-domain-id")
                                .name("test-domain-name")
                                .status(Status.SHARED)
                                .type("tcp")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void listRouterGroups() {
        requestListRouterGroups(this.routingClient, "test-router-group");

        this.domains
                .listRouterGroups()
                .as(StepVerifier::create)
                .expectNext(
                        RouterGroup.builder()
                                .id("test-routerGroupId")
                                .name("test-router-group")
                                .type("tcp")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void shareDomain() {
        requestOrganizations(this.cloudFoundryClient, "test-organization");
        requestListDomains(this.cloudFoundryClient, "test-organization-id", null);
        requestShareDomain(this.cloudFoundryClient, "test-domain-id", "test-organization-id");

        this.domains
                .share(
                        ShareDomainRequest.builder()
                                .domain("test-domain-name")
                                .organization("test-organization")
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void shareDomainDoesNotExist() {
        requestOrganizations(this.cloudFoundryClient, "test-organization");
        requestListDomains(this.cloudFoundryClient, "test-organization-id", null);
        requestShareDomain(this.cloudFoundryClient, "test-domain-id", "test-organization-id");

        this.domains
                .share(
                        ShareDomainRequest.builder()
                                .domain("invalid-domain-name")
                                .organization("test-organization")
                                .build())
                .as(StepVerifier::create)
                .consumeErrorWith(
                        t ->
                                assertThat(t)
                                        .isInstanceOf(IllegalArgumentException.class)
                                        .hasMessage(
                                                "Private domain invalid-domain-name does not"
                                                        + " exist"))
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void unshareDomain() {
        requestListDomains(this.cloudFoundryClient, "test-organization-id", null);
        requestOrganizations(this.cloudFoundryClient, "test-organization");
        requestUnshareDomain(this.cloudFoundryClient, "test-domain-id", "test-organization-id");

        this.domains
                .unshare(
                        UnshareDomainRequest.builder()
                                .domain("test-domain-name")
                                .organization("test-organization")
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    private static void requestShareDomain(
            CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        when(cloudFoundryClient
                        .domainsV3()
                        .share(
                                org.cloudfoundry.client.v3.domains.ShareDomainRequest.builder()
                                        .domainId(domainId)
                                        .data(Relationship.builder().id(organizationId).build())
                                        .build()))
                .thenReturn(Mono.empty());
    }

    private static void requestUnshareDomain(
            CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        when(cloudFoundryClient
                        .domainsV3()
                        .unshare(
                                org.cloudfoundry.client.v3.domains.UnshareDomainRequest.builder()
                                        .domainId(domainId)
                                        .organizationId(organizationId)
                                        .build()))
                .thenReturn(Mono.empty());
    }

    private static void requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        when(cloudFoundryClient.domainsV3().create(any()))
                .thenReturn(
                        Mono.just(
                                fill(CreateDomainResponse.builder(), "domain-")
                                        .isInternal(false)
                                        .build()));
    }

    private static void requestCreateDomain(
            CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        when(cloudFoundryClient
                        .domainsV3()
                        .create(
                                org.cloudfoundry.client.v3.domains.CreateDomainRequest.builder()
                                        .name(domain)
                                        .relationships(
                                                DomainRelationships.builder()
                                                        .organization(
                                                                ToOneRelationship.builder()
                                                                        .data(
                                                                                Relationship
                                                                                        .builder()
                                                                                        .id(
                                                                                                organizationId)
                                                                                        .build())
                                                                        .build())
                                                        .build())
                                        .build()))
                .thenReturn(
                        Mono.just(
                                fill(CreateDomainResponse.builder(), "domain-")
                                        .isInternal(false)
                                        .build()));
    }

    private static void requestListDomains(
            CloudFoundryClient cloudFoundryClient, String organizationId, String routerGroupId) {
        ToOneRelationship organizationRelationShip =
                organizationId != null
                        ? ToOneRelationship.builder()
                                .data(Relationship.builder().id(organizationId).build())
                                .build()
                        : ToOneRelationship.builder().build();
        org.cloudfoundry.client.v3.domains.RouterGroup routerGroup =
                routerGroupId != null
                        ? org.cloudfoundry.client.v3.domains.RouterGroup.builder()
                                .id(routerGroupId)
                                .build()
                        : null;

        when(cloudFoundryClient.domainsV3().list(ListDomainsRequest.builder().page(1).build()))
                .thenReturn(
                        Mono.just(
                                fill(ListDomainsResponse.builder())
                                        .resource(
                                                fill(DomainResource.builder(), "domain-")
                                                        .isInternal(false)
                                                        .relationships(
                                                                DomainRelationships.builder()
                                                                        .organization(
                                                                                organizationRelationShip)
                                                                        .build())
                                                        .routerGroup(routerGroup)
                                                        .build())
                                        .pagination(Pagination.builder().totalPages(1).build())
                                        .build()));
    }

    private static void requestListRouterGroups(
            RoutingClient routingClient, String routerGroupName) {
        when(routingClient.routerGroups().list(any()))
                .thenReturn(
                        Mono.just(
                                ListRouterGroupsResponse.builder()
                                        .routerGroup(
                                                fill(org.cloudfoundry.routing.v1.routergroups
                                                                .RouterGroup.builder())
                                                        .name(routerGroupName)
                                                        .type("tcp")
                                                        .build())
                                        .build()));
    }

    private static void requestOrganizations(
            CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient
                        .organizationsV3()
                        .list(
                                ListOrganizationsRequest.builder()
                                        .name(organization)
                                        .page(1)
                                        .build()))
                .thenReturn(
                        Mono.just(
                                fill(ListOrganizationsResponse.builder())
                                        .resource(
                                                fill(
                                                                OrganizationResource.builder(),
                                                                "organization-")
                                                        .build())
                                        .build()));
    }
}
