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

package org.cloudfoundry.operations.domains;

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

import java.util.NoSuchElementException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultDomains implements Domains {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<RoutingClient> routingClient;

    public DefaultDomains(Mono<CloudFoundryClient> cloudFoundryClient, Mono<RoutingClient> routingClient) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.routingClient = routingClient;
    }

    @Override
    public Mono<Void> create(CreateDomainRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getOrganizationId(cloudFoundryClient, request.getOrganization())
                ))
            .flatMap(function((cloudFoundryClient, organizationId) -> requestCreateDomain(cloudFoundryClient, request.getDomain(), organizationId)))
            .then()
            .transform(OperationsLogging.log("Create Domain"))
            .checkpoint();
    }

    @Override
    public Mono<Void> createShared(CreateSharedDomainRequest request) {
        if (request.getRouterGroup() == null) {
            return this.cloudFoundryClient
                .flatMap(cloudFoundryClient -> requestCreateSharedDomain(cloudFoundryClient, request.getDomain(), null))
                .then()
                .transform(OperationsLogging.log("Create Shared Domain"))
                .checkpoint();
        } else {
            return Mono.when(this.cloudFoundryClient, this.routingClient)
                .flatMap(function((cloudFoundryClient, routingClient) -> Mono
                    .when(
                        Mono.just(cloudFoundryClient),
                        getRouterGroupId(routingClient, request.getRouterGroup())
                    )))
                .flatMap(function((cloudFoundryClient, routerGroupId) -> requestCreateSharedDomain(cloudFoundryClient, request.getDomain(), routerGroupId)))
                .then()
                .transform(OperationsLogging.log("Create Shared Domain"))
                .checkpoint();
        }
    }

    @Override
    public Flux<Domain> list() {
        return this.cloudFoundryClient
            .flatMapMany(cloudFoundryClient -> requestListPrivateDomains(cloudFoundryClient)
                .map(DefaultDomains::toDomain)
                .mergeWith(requestListSharedDomains(cloudFoundryClient)
                    .map(DefaultDomains::toDomain)))
            .transform(OperationsLogging.log("List Domains"))
            .checkpoint();
    }

    @Override
    public Flux<RouterGroup> listRouterGroups() {
        return this.routingClient
            .flatMapMany(DefaultDomains::requestListRouterGroups)
            .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
            .map(DefaultDomains::toRouterGroup)
            .transform(OperationsLogging.log("List Router Groups"))
            .checkpoint();
    }

    @Override
    public Mono<Void> share(ShareDomainRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getPrivateDomainId(cloudFoundryClient, request.getDomain()),
                getOrganizationId(cloudFoundryClient, request.getOrganization())
            ))
            .flatMap(function(DefaultDomains::requestAssociateOrganizationPrivateDomainRequest))
            .then()
            .transform(OperationsLogging.log("Share Domain"))
            .checkpoint();
    }

    @Override
    public Mono<Void> unshare(UnshareDomainRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getPrivateDomainId(cloudFoundryClient, request.getDomain()),
                getOrganizationId(cloudFoundryClient, request.getOrganization())
            ))
            .flatMap(function(DefaultDomains::requestRemoveOrganizationPrivateDomainRequest))
            .transform(OperationsLogging.log("Unshare Domain"))
            .checkpoint();
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Organization %s does not exist", organization));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId);
    }

    private static Mono<PrivateDomainResource> getPrivateDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return requestListPrivateDomains(cloudFoundryClient, domain)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Private domain %s does not exist", domain));
    }

    private static Mono<String> getPrivateDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
        return getPrivateDomain(cloudFoundryClient, domain)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getRouterGroupId(RoutingClient routingClient, String routerGroup) {
        return requestListRouterGroups(routingClient)
            .flatMapIterable(ListRouterGroupsResponse::getRouterGroups)
            .filter(group -> routerGroup.equals(group.getName()))
            .single()
            .map(org.cloudfoundry.routing.v1.routergroups.RouterGroup::getRouterGroupId);
    }

    private static Mono<AssociateOrganizationPrivateDomainResponse> requestAssociateOrganizationPrivateDomainRequest(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        return cloudFoundryClient.organizations()
            .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                .organizationId(organizationId)
                .privateDomainId(domainId)
                .build());
    }

    private static Mono<CreatePrivateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(domain)
                .owningOrganizationId(organizationId)
                .build());
    }

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String domain, String routerGroupId) {
        return cloudFoundryClient.sharedDomains()
            .create(org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest.builder()
                .name(domain)
                .routerGroupId(routerGroupId)
                .build());
    }

    private static Flux<PrivateDomainResource> requestListPrivateDomains(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .name(domain)
                    .page(page)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestListPrivateDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Mono<ListRouterGroupsResponse> requestListRouterGroups(RoutingClient routingClient) {
        return routingClient.routerGroups()
            .list(org.cloudfoundry.routing.v1.routergroups.ListRouterGroupsRequest.builder()
                .build());
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations().list(
                ListOrganizationsRequest.builder()
                    .name(organization)
                    .page(page)
                    .build()));
    }

    private static Mono<Void> requestRemoveOrganizationPrivateDomainRequest(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        return cloudFoundryClient.organizations()
            .removePrivateDomain(RemoveOrganizationPrivateDomainRequest.builder()
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

    private static RouterGroup toRouterGroup(org.cloudfoundry.routing.v1.routergroups.RouterGroup group) {
        return RouterGroup.builder()
            .id(group.getRouterGroupId())
            .name(group.getName())
            .type(group.getType())
            .build();
    }

}
