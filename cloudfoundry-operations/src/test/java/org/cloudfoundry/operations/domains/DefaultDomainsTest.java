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

package org.cloudfoundry.operations.domains;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultDomainsTest extends AbstractOperationsTest {

    private final DefaultDomains domains = new DefaultDomains(Mono.just(this.cloudFoundryClient), Mono.just(this.routingClient));

    @Test
    public void createDomain() {
        requestOrganizations(this.cloudFoundryClient, "test-organization");
        requestCreatePrivateDomain(this.cloudFoundryClient, "test-domain", "test-organization-id");

        this.domains
            .create(CreateDomainRequest.builder()
                .domain("test-domain")
                .organization("test-organization")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createSharedDomain() {
        requestCreateSharedDomain(this.cloudFoundryClient, "test-domain");

        this.domains
            .createShared(CreateSharedDomainRequest.builder()
                .domain("test-domain")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDomains() {
        requestPrivateDomains(this.cloudFoundryClient);
        requestSharedDomains(this.cloudFoundryClient);

        this.domains
            .list()
            .as(StepVerifier::create)
            .expectNext(Domain.builder()
                    .id("test-private-domain-id")
                    .name("test-private-domain-name")
                    .status(Status.OWNED)
                    .build(),
                Domain.builder()
                    .id("test-shared-domain-id")
                    .name("test-shared-domain-name")
                    .status(Status.SHARED)
                    .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDomainsOnlyPrivate() {
        requestPrivateDomains(this.cloudFoundryClient);
        requestSharedDomainsEmpty(this.cloudFoundryClient);

        this.domains
            .list()
            .as(StepVerifier::create)
            .expectNext(Domain.builder()
                .id("test-private-domain-id")
                .name("test-private-domain-name")
                .status(Status.OWNED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDomainsOnlyShared() {
        requestSharedDomains(this.cloudFoundryClient);
        requestPrivateDomainsEmpty(this.cloudFoundryClient);

        this.domains
            .list()
            .as(StepVerifier::create)
            .expectNext(Domain.builder()
                .id("test-shared-domain-id")
                .name("test-shared-domain-name")
                .status(Status.SHARED)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDomainsTcp() {
        requestPrivateDomains(this.cloudFoundryClient);
        requestSharedDomainsTcp(this.cloudFoundryClient);

        this.domains
            .list()
            .as(StepVerifier::create)
            .expectNext(Domain.builder()
                    .id("test-private-domain-id")
                    .name("test-private-domain-name")
                    .status(Status.OWNED)
                    .build(),
                Domain.builder()
                    .id("test-shared-domain-id")
                    .name("test-shared-domain-name")
                    .status(Status.SHARED)
                    .type("test-shared-domain-type")
                    .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listRouterGroups() {
        requestListRouterGroups(this.routingClient);

        this.domains
            .listRouterGroups()
            .as(StepVerifier::create)
            .expectNext(RouterGroup.builder()
                .id("test-routerGroupId")
                .name("test-name")
                .type("test-type")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void shareDomain() {
        requestListPrivateDomains(this.cloudFoundryClient, "test-domain", "test-domain-id");
        requestOrganizations(this.cloudFoundryClient, "test-organization");
        requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, "test-domain-id", "test-organization-id");

        this.domains
            .share(ShareDomainRequest.builder()
                .domain("test-domain")
                .organization("test-organization")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void shareDomainSharedDomain() {
        requestListPrivateDomainsEmpty(this.cloudFoundryClient, "test-domain");
        requestOrganizations(this.cloudFoundryClient, "test-organization");

        this.domains
            .share(ShareDomainRequest.builder()
                .domain("test-domain")
                .organization("test-organization")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Private domain test-domain does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unshareDomain() {
        requestListPrivateDomains(this.cloudFoundryClient, "test-domain", "test-domain-id");
        requestOrganizations(this.cloudFoundryClient, "test-organization");
        requestRemoveOrganizationPrivateDomain(this.cloudFoundryClient, "test-domain-id", "test-organization-id");

        this.domains
            .unshare(UnshareDomainRequest.builder()
                .domain("test-domain")
                .organization("test-organization")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static void requestAssociateOrganizationPrivateDomain(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        when(cloudFoundryClient.organizations()
            .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                .privateDomainId(domainId)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestCreatePrivateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        when(cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(domain)
                .owningOrganizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreatePrivateDomainResponse.builder(), "private-domain-")
                    .build()));
    }

    private static void requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        when(cloudFoundryClient.sharedDomains()
            .create(org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest.builder()
                .name(domain)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateSharedDomainResponse.builder(), "shared-domain-")
                    .build()));
    }

    private static void requestListPrivateDomains(CloudFoundryClient cloudFoundryClient, String domain, String domainId) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .name(domain)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder())
                        .metadata(fill(Metadata.builder(), "private-domain-")
                            .id(domainId)
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestListPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient, String domain) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .name(domain)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestListRouterGroups(RoutingClient routingClient) {
        when(routingClient.routerGroups()
            .list(ListRouterGroupsRequest.builder()
                .build()))
            .thenReturn(Mono
                .just(ListRouterGroupsResponse.builder()
                    .routerGroup(fill(org.cloudfoundry.routing.v1.routergroups.RouterGroup.builder())
                        .build())
                    .build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .build())
                    .build()));
    }

    private static void requestPrivateDomains(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "private-domain-")
                        .build())
                    .build()));
    }

    private static void requestPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestRemoveOrganizationPrivateDomain(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        when(cloudFoundryClient.organizations()
            .removePrivateDomain(RemoveOrganizationPrivateDomainRequest.builder()
                .privateDomainId(domainId)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestSharedDomains(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .resource(fill(SharedDomainResource.builder(), "shared-domain-")
                        .entity(fill(SharedDomainEntity.builder(), "shared-domain-")
                            .routerGroupType(null)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSharedDomainsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .build()));
    }

    private static void requestSharedDomainsTcp(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .resource(fill(SharedDomainResource.builder(), "shared-domain-")
                        .entity(fill(SharedDomainEntity.builder(), "shared-domain-")
                            .routerGroupType("test-shared-domain-type")
                            .build())
                        .build())
                    .build()));
    }

}
