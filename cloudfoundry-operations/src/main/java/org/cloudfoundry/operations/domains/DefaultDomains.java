/*
 * Copyright 2013-2016 the original author or authors.
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
import org.cloudfoundry.operations.domains.Domain.Status;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultDomains implements Domains {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultDomains(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    public Mono<Void> create(CreateDomainRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> getOrganizationId(this.cloudFoundryClient, request1.getOrganization())
                .and(Mono.just(request1)))
            .then(function((domainId, request1) -> requestCreateDomain(this.cloudFoundryClient, request1.getDomain(), domainId)))
            .after();
    }

    @Override
    public Mono<Void> createShared(CreateSharedDomainRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> requestCreateSharedDomain(this.cloudFoundryClient, request1.getDomain()))
            .after();
    }

    @Override
    public Flux<Domain> list() {
        return requestListPrivateDomains(this.cloudFoundryClient)
            .map(DefaultDomains::toDomain)
            .mergeWith(requestListSharedDomains(this.cloudFoundryClient)
                .map(DefaultDomains::toDomain));

    }

    @Override
    public Mono<Void> share(ShareDomainRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> Mono
                .when(
                    getPrivateDomainId(this.cloudFoundryClient, request1.getDomain()),
                    getOrganizationId(this.cloudFoundryClient, request1.getOrganization())
                ))
            .then(function((domainId, organizationId) -> requestAssociateOrganizationPrivateDomainRequest(this.cloudFoundryClient, domainId, organizationId)))
            .after();
    }

    @Override
    public Mono<Void> unshare(UnshareDomainRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> Mono
                .when(
                    getPrivateDomainId(this.cloudFoundryClient, request1.getDomain()),
                    getOrganizationId(this.cloudFoundryClient, request1.getOrganization())
                ))
            .then(function((domainId, organizationId) -> requestRemoveOrganizationPrivateDomainRequest(this.cloudFoundryClient, domainId, organizationId)))
            .after();
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Organization %s does not exist", organization)));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId);
    }

    private static Mono<PrivateDomainResource> getPrivateDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return requestListPrivateDomains(cloudFoundryClient, domain)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Private domain %s does not exist", domain)));
    }

    private static Mono<String> getPrivateDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
        return getPrivateDomain(cloudFoundryClient, domain)
            .map(ResourceUtils::getId);
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

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return cloudFoundryClient.sharedDomains()
            .create(org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest.builder()
                .name(domain)
                .build());
    }

    private static Flux<PrivateDomainResource> requestListPrivateDomains(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .name(domain)
                    .page(page)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestListPrivateDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations().list(
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
            .build();
    }

}
