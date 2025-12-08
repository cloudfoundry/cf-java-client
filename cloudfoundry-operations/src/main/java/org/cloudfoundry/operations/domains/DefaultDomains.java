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

import java.util.NoSuchElementException;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
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
        return getOrganizationId(request.getOrganization())
                .flatMap(organizationId -> requestCreateDomain(request.getDomain(), organizationId))
                .then()
                .transform(OperationsLogging.log("Create Domain"))
                .checkpoint();
    }

    @Override
    public Mono<Void> createShared(CreateSharedDomainRequest request) {
        if (request.getRouterGroup() == null) {
            return requestCreateSharedDomain(request.getDomain(), null)
                    .then()
                    .transform(OperationsLogging.log("Create Shared Domain"))
                    .checkpoint();
        } else {
            return getRouterGroupId(routingClient, request.getRouterGroup())
                    .flatMap(
                            routerGroupId ->
                                    requestCreateSharedDomain(request.getDomain(), routerGroupId))
                    .then()
                    .transform(OperationsLogging.log("Create Shared Domain"))
                    .checkpoint();
        }
    }

    @Override
    public Flux<Domain> list() {
        return requestListPrivateDomains()
                .map(DefaultDomains::toDomain)
                .mergeWith(requestListSharedDomains().map(DefaultDomains::toDomain))
                .transform(OperationsLogging.log("List Domains"))
                .checkpoint();
    }

    @Override
    public Flux<RouterGroup> listRouterGroups() {
        return requestListRouterGroups(routingClient)
                .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
                .map(DefaultDomains::toRouterGroup)
                .transform(OperationsLogging.log("List Router Groups"))
                .checkpoint();
    }

    @Override
    public Mono<Void> share(ShareDomainRequest request) {
        return Mono.zip(
                        getPrivateDomainId(request.getDomain()),
                        getOrganizationId(request.getOrganization()))
                .flatMap(function(this::requestAssociateOrganizationPrivateDomainRequest))
                .then()
                .transform(OperationsLogging.log("Share Domain"))
                .checkpoint();
    }

    @Override
    public Mono<Void> unshare(UnshareDomainRequest request) {
        return Mono.zip(
                        getPrivateDomainId(request.getDomain()),
                        getOrganizationId(request.getOrganization()))
                .flatMap(function(this::requestRemoveOrganizationPrivateDomainRequest))
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
        return getOrganization(organization).map(ResourceUtils::getId);
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

    private static Mono<String> getRouterGroupId(RoutingClient routingClient, String routerGroup) {
        return requestListRouterGroups(routingClient)
                .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
                .filter(group -> routerGroup.equals(group.getName()))
                .single()
                .map(org.cloudfoundry.routing.v1.routergroups.RouterGroup::getRouterGroupId);
    }

    private Mono<AssociateOrganizationPrivateDomainResponse>
            requestAssociateOrganizationPrivateDomainRequest(
                    String domainId, String organizationId) {
        return this.cloudFoundryClient
                .organizations()
                .associatePrivateDomain(
                        AssociateOrganizationPrivateDomainRequest.builder()
                                .organizationId(organizationId)
                                .privateDomainId(domainId)
                                .build());
    }

    private Mono<CreatePrivateDomainResponse> requestCreateDomain(
            String domain, String organizationId) {
        return this.cloudFoundryClient
                .privateDomains()
                .create(
                        CreatePrivateDomainRequest.builder()
                                .name(domain)
                                .owningOrganizationId(organizationId)
                                .build());
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

    private Flux<PrivateDomainResource> requestListPrivateDomains() {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .privateDomains()
                                .list(ListPrivateDomainsRequest.builder().page(page).build()));
    }

    private static Mono<ListRouterGroupsResponse> requestListRouterGroups(
            RoutingClient routingClient) {
        return routingClient
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
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .organizations()
                                .list(
                                        ListOrganizationsRequest.builder()
                                                .name(organization)
                                                .page(page)
                                                .build()));
    }

    private Mono<Void> requestRemoveOrganizationPrivateDomainRequest(
            String domainId, String organizationId) {
        return this.cloudFoundryClient
                .organizations()
                .removePrivateDomain(
                        RemoveOrganizationPrivateDomainRequest.builder()
                                .organizationId(organizationId)
                                .privateDomainId(domainId)
                                .build());
    }

    private static Domain toDomain(PrivateDomainResource resource) {
        PrivateDomainEntity entity = ResourceUtils.getEntity(resource);

        return Domain.builder()
                .id(ResourceUtils.getId(resource))
                .name(entity.getName())
                .status(Status.OWNED)
                .build();
    }

    private static Domain toDomain(SharedDomainResource resource) {
        SharedDomainEntity entity = ResourceUtils.getEntity(resource);

        return Domain.builder()
                .id(ResourceUtils.getId(resource))
                .name(entity.getName())
                .status(Status.SHARED)
                .type(entity.getRouterGroupType())
                .build();
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
