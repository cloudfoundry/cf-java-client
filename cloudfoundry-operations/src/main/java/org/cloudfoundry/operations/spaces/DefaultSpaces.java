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

package org.cloudfoundry.operations.spaces;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.securitygroups.RuleEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.operations.spaceadmin.SpaceQuota;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultSpaces implements Spaces {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<String> organizationId;

    private final Mono<String> username;

    public DefaultSpaces(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> organizationId, Mono<String> username) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
        this.username = username;
    }

    @Override
    public Mono<Void> allowSsh(AllowSpaceSshRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getOrganizationSpaceIdWhere(cloudFoundryClient, organizationId, request.getName(), sshEnabled(false))
                )))
            .flatMap(function((cloudFoundryClient, spaceId) -> requestUpdateSpaceSsh(cloudFoundryClient, spaceId, true)))
            .then()
            .transform(OperationsLogging.log("Allow Space SSH"))
            .checkpoint();
    }

    @Override
    public Mono<Void> create(CreateSpaceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.username)
            .flatMap(function((cloudFoundryClient, username) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(username),
                    getOrganizationIdOrDefault(cloudFoundryClient, request.getOrganization(), this.organizationId)
                )))
            .flatMap(function((cloudFoundryClient, username, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(username),
                    Mono.just(organizationId),
                    getOptionalSpaceQuotaId(cloudFoundryClient, organizationId, request.getSpaceQuota())
                )))
            .flatMap(function((cloudFoundryClient, username, organizationId, spaceQuotaId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(organizationId),
                    requestCreateSpace(cloudFoundryClient, organizationId, request.getName(), spaceQuotaId.orElse(null))
                        .map(ResourceUtils::getId),
                    Mono.just(username)
                )))
            .flatMap(function((cloudFoundryClient, organizationId, spaceId, username) -> requestAssociateOrganizationUserByUsername(cloudFoundryClient, organizationId, username)
                .then(Mono.just(Tuples.of(cloudFoundryClient, organizationId, spaceId, username)))))
            .flatMap(function((cloudFoundryClient, organizationId, spaceId, username) -> Mono
                .when(
                    requestAssociateSpaceManagerByUsername(cloudFoundryClient, spaceId, username),
                    requestAssociateSpaceDeveloperByUsername(cloudFoundryClient, spaceId, username)
                )))
            .then()
            .transform(OperationsLogging.log("Create Space"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteSpaceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(request.getCompletionTimeout()),
                    getOrganizationSpaceId(cloudFoundryClient, organizationId, request.getName())
                )))
            .flatMap(function(DefaultSpaces::deleteSpace))
            .transform(OperationsLogging.log("Delete Space"))
            .checkpoint();
    }

    @Override
    public Mono<Void> disallowSsh(DisallowSpaceSshRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getOrganizationSpaceIdWhere(cloudFoundryClient, organizationId, request.getName(), sshEnabled(true))
                )))
            .flatMap(function((cloudFoundryClient, spaceId) -> requestUpdateSpaceSsh(cloudFoundryClient, spaceId, false)))
            .then()
            .transform(OperationsLogging.log("Disallow Space SSH"))
            .checkpoint();
    }

    @Override
    public Mono<SpaceDetail> get(GetSpaceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getOrganizationSpace(cloudFoundryClient, organizationId, request.getName())
                )))
            .flatMap(function((cloudFoundryClient, resource) -> getSpaceDetail(cloudFoundryClient, resource, request)))
            .transform(OperationsLogging.log("Get Space"))
            .checkpoint();
    }

    @Override
    public Flux<SpaceSummary> list() {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMapMany(function(DefaultSpaces::requestSpaces))
            .map(DefaultSpaces::toSpaceSummary)
            .transform(OperationsLogging.log("List Spaces"))
            .checkpoint();
    }

    @Override
    public Mono<Void> rename(RenameSpaceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getOrganizationSpaceId(cloudFoundryClient, organizationId, request.getName())
                )))
            .flatMap(function((cloudFoundryClient, spaceId) -> requestUpdateSpace(cloudFoundryClient, spaceId, request.getNewName())))
            .then()
            .transform(OperationsLogging.log("Rename Space"))
            .checkpoint();
    }

    @Override
    public Mono<Boolean> sshAllowed(SpaceSshAllowedRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> getOrganizationSpace(cloudFoundryClient, organizationId, request.getName())))
            .map(resource -> ResourceUtils.getEntity(resource).getAllowSsh())
            .transform(OperationsLogging.log("Is Space SSH Allowed"))
            .checkpoint();
    }

    private static Mono<Void> deleteSpace(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String spaceId) {
        return requestDeleteSpace(cloudFoundryClient, spaceId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, job));
    }

    private static Mono<List<String>> getApplicationNames(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        return requestSpaceApplications(cloudFoundryClient, ResourceUtils.getId(spaceResource))
            .map(applicationResource -> ResourceUtils.getEntity(applicationResource).getName())
            .collectList();
    }

    private static Mono<List<String>> getDomainNames(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        return requestListPrivateDomains(cloudFoundryClient, spaceResource.getEntity().getOrganizationId())
            .map(resource -> resource.getEntity().getName())
            .mergeWith(requestListSharedDomains(cloudFoundryClient)
                .map(resource -> resource.getEntity().getName()))
            .collectList();
    }

    private static Mono<Optional<SpaceQuota>> getOptionalSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        String spaceQuotaDefinitionId = ResourceUtils.getEntity(spaceResource).getSpaceQuotaDefinitionId();

        if (spaceQuotaDefinitionId == null) {
            return Mono.just(Optional.empty());
        }

        return requestSpaceQuotaDefinition(cloudFoundryClient, spaceQuotaDefinitionId)
            .map(DefaultSpaces::toSpaceQuotaDefinition)
            .map(Optional::of);
    }

    private static Mono<Optional<String>> getOptionalSpaceQuotaId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceQuota) {
        if (spaceQuota == null) {
            return Mono.just(Optional.empty());
        } else {
            return getSpaceQuota(cloudFoundryClient, organizationId, spaceQuota)
                .map(ResourceUtils::getId)
                .map(Optional::of);
        }
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

    private static Mono<String> getOrganizationIdOrDefault(CloudFoundryClient cloudFoundryClient, String organizationName, Mono<String> organizationId) {
        return Optional.ofNullable(organizationName)
            .map(organization -> getOrganizationId(cloudFoundryClient, organization))
            .orElse(organizationId);
    }

    private static Mono<String> getOrganizationName(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestOrganization(cloudFoundryClient, ResourceUtils.getEntity(resource).getOrganizationId())
            .map(response -> ResourceUtils.getEntity(response).getName());
    }

    private static Mono<SpaceResource> getOrganizationSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestOrganizationSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Space %s does not exist", space));
    }

    private static Mono<String> getOrganizationSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getOrganizationSpace(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getOrganizationSpaceIdWhere(CloudFoundryClient cloudFoundryClient, String organizationId, String space, Predicate<SpaceResource> predicate) {
        return getOrganizationSpace(cloudFoundryClient, organizationId, space)
            .filter(predicate)
            .map(ResourceUtils::getId);
    }

    private static Mono<List<SecurityGroupEntity>> getSecurityGroups(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource, boolean withRules) {
        return requestSpaceSecurityGroups(cloudFoundryClient, ResourceUtils.getId(spaceResource))
            .map(securityGroupResource -> {
                SecurityGroupEntity entity = ResourceUtils.getEntity(securityGroupResource);
                if (!withRules) {
                    entity = SecurityGroupEntity.builder()
                        .name(entity.getName())
                        .runningDefault(entity.getRunningDefault())
                        .spacesUrl(entity.getSpacesUrl())
                        .stagingDefault(entity.getStagingDefault())
                        .build();
                }
                return entity;
            })
            .collectList();
    }

    private static Mono<List<String>> getServiceNames(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        return requestSpaceServices(cloudFoundryClient, ResourceUtils.getId(spaceResource))
            .map(serviceResource -> ResourceUtils.getEntity(serviceResource).getLabel())
            .collectList();
    }

    private static Mono<SpaceDetail> getSpaceDetail(CloudFoundryClient cloudFoundryClient, SpaceResource resource, GetSpaceRequest request) {
        return Mono
            .when(
                getApplicationNames(cloudFoundryClient, resource),
                getDomainNames(cloudFoundryClient, resource),
                getOrganizationName(cloudFoundryClient, resource),
                getSecurityGroups(cloudFoundryClient, resource, Optional.ofNullable(request.getSecurityGroupRules()).orElse(false)),
                getServiceNames(cloudFoundryClient, resource),
                getOptionalSpaceQuotaDefinition(cloudFoundryClient, resource)
            )
            .map(function((applications, domains, organization, securityGroups, services, spaceQuota) ->
                toSpaceDetail(applications, domains, organization, resource, securityGroups, services, spaceQuota)));
    }

    private static Mono<SpaceQuotaDefinitionResource> getSpaceQuota(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceQuota) {
        return requestOrganizationSpaceQuotas(cloudFoundryClient, organizationId, spaceQuota)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Space quota definition %s does not exist", spaceQuota));
    }

    private static Mono<AssociateOrganizationUserByUsernameResponse> requestAssociateOrganizationUserByUsername(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        return cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build());
    }

    private static Mono<AssociateSpaceDeveloperByUsernameResponse> requestAssociateSpaceDeveloperByUsername(CloudFoundryClient cloudFoundryClient, String spaceId, String username) {
        return cloudFoundryClient.spaces()
            .associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest.builder()
                .spaceId(spaceId)
                .username(username)
                .build());
    }

    private static Mono<AssociateSpaceManagerByUsernameResponse> requestAssociateSpaceManagerByUsername(CloudFoundryClient cloudFoundryClient, String spaceId, String username) {
        return cloudFoundryClient.spaces()
            .associateManagerByUsername(AssociateSpaceManagerByUsernameRequest.builder()
                .spaceId(spaceId)
                .username(username)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space, String spaceQuotaId) {
        return cloudFoundryClient.spaces()
            .create(org.cloudfoundry.client.v2.spaces.CreateSpaceRequest.builder()
                .name(space)
                .organizationId(organizationId)
                .spaceQuotaDefinitionId(spaceQuotaId)
                .build());
    }

    private static Mono<DeleteSpaceResponse> requestDeleteSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.spaces()
            .delete(org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest.builder()
                .async(true)
                .spaceId(spaceId)
                .build());
    }

    private static Flux<PrivateDomainResource> requestListPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Mono<GetOrganizationResponse> requestOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build());
    }

    private static Flux<SpaceQuotaDefinitionResource> requestOrganizationSpaceQuotas(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceQuota) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                    .page(page)
                    .organizationId(organizationId)
                    .build()))
            .filter(resource -> ResourceUtils.getEntity(resource).getName().equals(spaceQuota));
    }

    private static Flux<SpaceResource> requestOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .name(space)
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .name(organizationName)
                    .page(page)
                    .build()));
    }

    private static Flux<ApplicationResource> requestSpaceApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listApplications(ListSpaceApplicationsRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Mono<GetSpaceQuotaDefinitionResponse> requestSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, String spaceQuotaDefinitionId) {
        return cloudFoundryClient.spaceQuotaDefinitions()
            .get(GetSpaceQuotaDefinitionRequest.builder()
                .spaceQuotaDefinitionId(spaceQuotaDefinitionId)
                .build());
    }

    private static Flux<SecurityGroupResource> requestSpaceSecurityGroups(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listSecurityGroups(ListSpaceSecurityGroupsRequest.builder()
                    .spaceId(spaceId)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceResource> requestSpaceServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listServices(ListSpaceServicesRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<SpaceResource> requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .list(ListSpacesRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Mono<UpdateSpaceResponse> requestUpdateSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String newName) {
        return cloudFoundryClient.spaces()
            .update(UpdateSpaceRequest.builder()
                .name(newName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<UpdateSpaceResponse> requestUpdateSpaceSsh(CloudFoundryClient cloudFoundryClient, String spaceId, Boolean allowed) {
        return cloudFoundryClient.spaces()
            .update(UpdateSpaceRequest.builder()
                .allowSsh(allowed)
                .spaceId(spaceId)
                .build());
    }

    private static Predicate<SpaceResource> sshEnabled(Boolean enabled) {
        return resource -> enabled.equals(ResourceUtils.getEntity(resource).getAllowSsh());
    }

    private static SpaceDetail toSpaceDetail(List<String> applications,
                                             List<String> domains,
                                             String organization,
                                             SpaceResource resource,
                                             List<SecurityGroupEntity> securityGroups,
                                             List<String> services,
                                             Optional<SpaceQuota> spaceQuota) {
        return SpaceDetail.builder()
            .applications(applications)
            .domains(domains)
            .id(ResourceUtils.getId(resource))
            .name(ResourceUtils.getEntity(resource).getName())
            .organization(organization)
            .securityGroups(toSpaceDetailSecurityGroups(securityGroups))
            .services(services)
            .spaceQuota(spaceQuota)
            .build();
    }

    private static List<Rule> toSpaceDetailSecurityGroupRules(List<RuleEntity> rules) {
        return Optional.ofNullable(rules)
            .map(r -> r.stream()
                .map(ruleEntity -> Rule.builder()
                    .destination(ruleEntity.getDestination())
                    .ports(ruleEntity.getPorts())
                    .protocol(ruleEntity.getProtocol())
                    .build())
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
    }

    private static List<SecurityGroup> toSpaceDetailSecurityGroups(List<SecurityGroupEntity> securityGroups) {
        return securityGroups.stream()
            .map(entity -> SecurityGroup.builder()
                .name(entity.getName())
                .rules(toSpaceDetailSecurityGroupRules(entity.getRules()))
                .build())
            .collect(Collectors.toList());
    }

    private static SpaceQuota toSpaceQuotaDefinition(Resource<SpaceQuotaDefinitionEntity> resource) {
        SpaceQuotaDefinitionEntity entity = ResourceUtils.getEntity(resource);

        return SpaceQuota.builder()
            .id(ResourceUtils.getId(resource))
            .instanceMemoryLimit(entity.getInstanceMemoryLimit())
            .name(entity.getName())
            .organizationId(entity.getOrganizationId())
            .paidServicePlans(entity.getNonBasicServicesAllowed())
            .totalMemoryLimit(entity.getMemoryLimit())
            .totalRoutes(entity.getTotalRoutes())
            .totalServiceInstances(entity.getTotalServices())
            .build();
    }

    private static SpaceSummary toSpaceSummary(SpaceResource resource) {
        return SpaceSummary.builder()
            .id(ResourceUtils.getId(resource))
            .name(ResourceUtils.getEntity(resource).getName())
            .build();
    }

}
