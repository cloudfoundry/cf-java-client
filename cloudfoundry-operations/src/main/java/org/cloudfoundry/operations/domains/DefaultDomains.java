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

package org.cloudfoundry.operations.domains;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.client.v3.domains.ListDomainsRequest;
import org.cloudfoundry.client.v3.domains.ShareDomainResponse;
import org.cloudfoundry.client.v3.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v3.organizations.OrganizationResource;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsResponse;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class DefaultDomains implements Domains {

    private final CloudFoundryClient cloudFoundryClient;

    private final RoutingClient routingClient;

    public DefaultDomains(CloudFoundryClient cloudFoundryClient, RoutingClient routingClient) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.routingClient = routingClient;
    }

    /**
     * @deprecated use {@link DefaultDomains(CloudFoundryClient, RoutingClient)} instead.
     */
    @Deprecated
    public DefaultDomains(
            Mono<CloudFoundryClient> cloudFoundryClient, Mono<RoutingClient> routingClient) {
        this.cloudFoundryClient = cloudFoundryClient.block();
        this.routingClient = routingClient.block();
    }

    @Override
    public Mono<Void> create(CreateDomainRequest request) {
        Mono<Optional<String>> organizationId =
                Mono.justOrEmpty(request.getOrganization())
                        .flatMap(this::getOrganization)
                        .map(OrganizationResource::getId)
                        .map(Optional::of)
                        .defaultIfEmpty(Optional.empty());

        Mono<Optional<String>> groupId =
                Mono.justOrEmpty(request.getRouterGroup())
                        .flatMap(this::getRouterGroupId)
                        .map(Optional::of)
                        .defaultIfEmpty(Optional.empty());

        return Mono.zip(organizationId, groupId)
                .flatMap(
                        function(
                                (oId, gId) ->
                                        requestCreateDomain(
                                                request.getDomain(),
                                                oId.orElse(null),
                                                gId.orElse(null))))
                .then()
                .transform(OperationsLogging.log("Create Domain"))
                .checkpoint();
    }

    @Override
    @Deprecated
    public Mono<Void> createShared(CreateSharedDomainRequest request) {
        return create(
                CreateDomainRequest.builder()
                        .domain(request.getDomain())
                        .routerGroup(request.getRouterGroup())
                        .build());
    }

    @Override
    public Flux<Domain> list() {
        return requestListRouterGroups()
                .map(ListRouterGroupsResponse::getRouterGroups)
                .map(DefaultDomains::indexRouterGroupsById)
                .flatMapMany(
                        routerGroupsIndexedById ->
                                requestListDomains()
                                        .map(
                                                domain ->
                                                        DefaultDomains.toDomain(
                                                                domain, routerGroupsIndexedById)))
                .transform(OperationsLogging.log("List Domains"))
                .checkpoint();
    }

    @Override
    public Flux<RouterGroup> listRouterGroups() {
        return requestListRouterGroups()
                .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
                .map(DefaultDomains::toRouterGroup)
                .transform(OperationsLogging.log("List Router Groups"))
                .checkpoint();
    }

    @Override
    public Mono<Void> share(ShareDomainRequest request) {
        return Mono.zip(
                        getDomainId(request.getDomain()),
                        getOrganizationId(request.getOrganization()))
                .flatMap(function(this::requestShareDomain))
                .then()
                .transform(OperationsLogging.log("Share Domain"))
                .checkpoint();
    }

    @Override
    public Mono<Void> unshare(UnshareDomainRequest request) {
        return Mono.zip(
                        getDomainId(request.getDomain()),
                        getOrganizationId(request.getOrganization()))
                .flatMap(function(this::requestUnshareDomain))
                .transform(OperationsLogging.log("Unshare Domain"))
                .checkpoint();
    }

    private Mono<OrganizationResource> getOrganization(String organization) {
        return requestOrganizations(organization)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Organization %s does not exist", organization));
    }

    private Mono<String> getOrganizationId(String organization) {
        return getOrganization(organization).map(OrganizationResource::getId);
    }

    private Mono<String> getDomainId(String domain) {
        return this.requestListDomains()
                .filter(d -> d.getName().equals(domain))
                .map(DomainResource::getId)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Private domain %s does not exist", domain));
    }

    private Mono<PrivateDomainResource> getPrivateDomain(String domain) {
        return requestListPrivateDomains(domain)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Private domain %s does not exist", domain));
    }

    private Mono<String> getPrivateDomainId(String domain) {
        return getPrivateDomain(domain).map(ResourceUtils::getId);
    }

    private Mono<String> getRouterGroupId(String routerGroup) {
        return requestListRouterGroups()
                .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
                .filter(group -> routerGroup.equals(group.getName()))
                .single()
                .map(org.cloudfoundry.routing.v1.routergroups.RouterGroup::getRouterGroupId);
    }

    private Mono<ShareDomainResponse> requestShareDomain(String domainId, String organizationId) {
        return this.cloudFoundryClient
                .domainsV3()
                .share(
                        org.cloudfoundry.client.v3.domains.ShareDomainRequest.builder()
                                .domainId(domainId)
                                .data(Relationship.builder().id(organizationId).build())
                                .build());
    }

    private Mono<CreateDomainResponse> requestCreateDomain(
            String domain, String organizationId, String routerGroupId) {
        org.cloudfoundry.client.v3.domains.CreateDomainRequest.Builder createDomainRequest =
                org.cloudfoundry.client.v3.domains.CreateDomainRequest.builder().name(domain);
        if (organizationId != null) {
            createDomainRequest.relationships(
                    DomainRelationships.builder()
                            .organization(
                                    ToOneRelationship.builder()
                                            .data(Relationship.builder().id(organizationId).build())
                                            .build())
                            .build());
        }
        if (routerGroupId != null) {
            createDomainRequest.routerGroup(
                    org.cloudfoundry.client.v3.domains.RouterGroup.builder()
                            .id(routerGroupId)
                            .build());
        }
        return this.cloudFoundryClient.domainsV3().create(createDomainRequest.build());
    }

    private Mono<CreateSharedDomainResponse> requestCreateSharedDomain(
            String domain, String routerGroupId) {
        return this.cloudFoundryClient
                .sharedDomains()
                .create(
                        org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest.builder()
                                .name(domain)
                                .routerGroupId(routerGroupId)
                                .build());
    }

    private Flux<PrivateDomainResource> requestListPrivateDomains(String domain) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .privateDomains()
                                .list(
                                        ListPrivateDomainsRequest.builder()
                                                .name(domain)
                                                .page(page)
                                                .build()));
    }

    private Flux<DomainResource> requestListDomains() {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .domainsV3()
                                .list(ListDomainsRequest.builder().page(page).build()));
    }

    private Mono<ListRouterGroupsResponse> requestListRouterGroups() {
        return this.routingClient
                .routerGroups()
                .list(
                        org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest.builder()
                                .build());
    }

    private Flux<SharedDomainResource> requestListSharedDomains() {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .sharedDomains()
                                .list(ListSharedDomainsRequest.builder().page(page).build()));
    }

    private Flux<OrganizationResource> requestOrganizations(String organization) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .organizationsV3()
                                .list(
                                        ListOrganizationsRequest.builder()
                                                .name(organization)
                                                .page(page)
                                                .build()));
    }

    private Mono<Void> requestUnshareDomain(String domainId, String organizationId) {
        return this.cloudFoundryClient
                .domainsV3()
                .unshare(
                        org.cloudfoundry.client.v3.domains.UnshareDomainRequest.builder()
                                .organizationId(organizationId)
                                .domainId(domainId)
                                .build());
    }

    private static Domain toDomain(DomainResource entity, Map<String, String> type) {
        return Domain.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(
                        entity.getRelationships().getOrganization().getData() != null
                                ? Status.OWNED
                                : Status.SHARED)
                .type(
                        entity.getRouterGroup() != null
                                ? type.get(entity.getRouterGroup().getId())
                                : null)
                .build();
    }

    private static Map<String, String> indexRouterGroupsById(
            List<org.cloudfoundry.routing.v1.routergroups.RouterGroup> routeGroups) {
        return routeGroups.stream()
                .collect(
                        Collectors.toMap(
                                org.cloudfoundry.routing.v1.routergroups.RouterGroup
                                        ::getRouterGroupId,
                                org.cloudfoundry.routing.v1.routergroups.RouterGroup::getType));
    }

    private static RouterGroup toRouterGroup(
            org.cloudfoundry.routing.v1.routergroups.RouterGroup group) {
        return RouterGroup.builder()
                .id(group.getRouterGroupId())
                .name(group.getName())
                .type(group.getType())
                .build();
    }
}
