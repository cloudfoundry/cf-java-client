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

package org.cloudfoundry.operations.spaces;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
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
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rx.Fluxion;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultSpaces implements Spaces {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> organizationId;

    private final Mono<String> username;

    public DefaultSpaces(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, Mono<String> username) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
        this.username = username;
    }

    @Override
    public Mono<Void> allowSsh(AllowSpaceSshRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function((request1, organizationId) -> getOrganizationSpaceIdWhere(this.cloudFoundryClient, organizationId, request1.getName(), sshEnabled(false))))
            .then(spaceId -> requestUpdateSpaceSsh(this.cloudFoundryClient, spaceId, true))
            .after();
    }

    @Override
    public Mono<Void> create(CreateSpaceRequest request) {
        return ValidationUtils.validate(request)
            .then(request1 -> Mono
                .when(
                    Mono.just(request1),
                    getOrganizationOrDefault(this.cloudFoundryClient, request1, this.organizationId)
                ))
            .then(function((request1, organizationId) -> Mono
                .when(
                    Mono.just(request),
                    Mono.just(organizationId),
                    getOptionalSpaceQuotaId(this.cloudFoundryClient, organizationId, request1.getSpaceQuota())
                )))
            .then(function((request1, organizationId, spaceQuotaId) -> Mono
                .when(
                    Mono.just(organizationId),
                    requestCreateSpace(this.cloudFoundryClient, organizationId, request1.getName(), spaceQuotaId.orElse(null))
                        .map(ResourceUtils::getId),
                    this.username
                )))
            .as(thenKeep(function((organizationId, spaceId, username) -> requestAssociateOrganizationUserByUsername(
                this.cloudFoundryClient, organizationId, username))))
            .then(function((organizationId, spaceId, username) -> Mono
                .when(
                    requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, spaceId, username),
                    requestAssociateSpaceDeveloperByUsername(this.cloudFoundryClient, spaceId, username)
                )))
            .after();
    }

    @Override
    public Mono<Void> delete(DeleteSpaceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function((request1, organizationId) -> getOrganizationSpaceId(this.cloudFoundryClient, organizationId, request1.getName())))
            .then(spaceId -> deleteSpace(this.cloudFoundryClient, spaceId));
    }

    @Override
    public Mono<Void> disallowSsh(DisallowSpaceSshRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function((request1, organizationId) -> getOrganizationSpaceIdWhere(this.cloudFoundryClient, organizationId, request1.getName(), sshEnabled(true))))
            .then(spaceId -> requestUpdateSpaceSsh(this.cloudFoundryClient, spaceId, false))
            .after();
    }

    @Override
    public Mono<SpaceDetail> get(GetSpaceRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.organizationId)
            .then(function((request1, organizationId1) -> Mono
                .just(request1)
                .and(getOrganizationSpace(this.cloudFoundryClient, organizationId1, request1.getName()))))
            .then(function((request1, resource) -> getSpaceDetail(this.cloudFoundryClient, resource, request1)));
    }

    @Override
    public Flux<SpaceSummary> list() {
        return this.organizationId
            .flatMap(organizationId1 -> requestSpaces(this.cloudFoundryClient, organizationId1))
            .map(DefaultSpaces::toSpaceSummary);
    }

    @Override
    public Mono<Void> rename(RenameSpaceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function((request1, organizationId1) -> getOrganizationSpaceId(this.cloudFoundryClient, organizationId1, request.getName())
                .and(Mono.just(request1))))
            .then(function((spaceId, request1) -> requestUpdateSpace(this.cloudFoundryClient, spaceId, request1.getNewName())))
            .after();

    }

    @Override
    public Mono<Boolean> sshAllowed(SpaceSshAllowedRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function((request1, organizationId1) -> getOrganizationSpace(this.cloudFoundryClient, organizationId1, request1.getName())))
            .map(resource -> ResourceUtils.getEntity(resource).getAllowSsh());
    }

    private static Mono<Void> deleteSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestDeleteSpace(cloudFoundryClient, spaceId)
            .map(ResourceUtils::getId)
            .then(jobId -> JobUtils.waitForCompletion(cloudFoundryClient, jobId));
    }

    private static Mono<List<String>> getApplicationNames(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        return requestSpaceApplications(cloudFoundryClient, ResourceUtils.getId(spaceResource))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .toList();
    }

    private static Mono<List<String>> getDomainNames(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestSpaceDomains(cloudFoundryClient, ResourceUtils.getId(resource))
            .map(resource1 -> ResourceUtils.getEntity(resource1).getName())
            .toList();
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
            .otherwise(ExceptionUtils.<OrganizationResource>convert("Organization %s does not exist", organization));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getOrganizationName(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestOrganization(cloudFoundryClient, ResourceUtils.getEntity(resource).getOrganizationId())
            .map(response -> ResourceUtils.getEntity(response).getName());
    }

    private static Mono<String> getOrganizationOrDefault(CloudFoundryClient cloudFoundryClient, CreateSpaceRequest request, Mono<String> organizationId) {
        if (request.getOrganization() != null) {
            return getOrganizationId(cloudFoundryClient, request.getOrganization());
        } else {
            return organizationId;
        }
    }

    private static Mono<SpaceResource> getOrganizationSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestOrganizationSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .otherwise(ExceptionUtils.<SpaceResource>convert("Space %s does not exist", space));
    }

    private static Mono<String> getOrganizationSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getOrganizationSpace(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getOrganizationSpaceIdWhere(CloudFoundryClient cloudFoundryClient, String organizationId, String space, Predicate<SpaceResource> predicate) {
        return getOrganizationSpace(cloudFoundryClient, organizationId, space)
            .where(predicate)
            .map(ResourceUtils::getId);
    }

    private static Mono<List<SecurityGroupEntity>> getSecurityGroups(CloudFoundryClient cloudFoundryClient, SpaceResource resource, boolean withRules) {
        return requestSpaceSecurityGroups(cloudFoundryClient, ResourceUtils.getId(resource))
            .map(resource1 -> {
                    SecurityGroupEntity entity = ResourceUtils.getEntity(resource1);
                    if (!withRules) {
                        entity = SecurityGroupEntity.builder()
                            .name(entity.getName())
                            .runningDefault(entity.getRunningDefault())
                            .spacesUrl(entity.getSpacesUrl())
                            .stagingDefault(entity.getStagingDefault())
                            .build();
                    }
                    return entity;
                }

            )
            .toList();
    }

    private static Mono<List<String>> getServiceNames(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestSpaceServices(cloudFoundryClient, ResourceUtils.getId(resource))
            .map(resource1 -> ResourceUtils.getEntity(resource1).getLabel())
            .toList();
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
            .map(function((applications, domains, organization, securityGroups, services, spaceQuota) -> {
                return toSpaceDetail(applications, domains, organization, resource, securityGroups, services, spaceQuota);
            }));
    }

    private static Mono<SpaceQuotaDefinitionResource> getSpaceQuota(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceQuota) {
        return requestOrganizationSpaceQuotas(cloudFoundryClient, organizationId, spaceQuota)
            .single()
            .otherwise(ExceptionUtils.<SpaceQuotaDefinitionResource>convert("Space quota definition %s does not exist", spaceQuota));
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

    private static Mono<GetOrganizationResponse> requestOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build());
    }

    private static Fluxion<SpaceQuotaDefinitionResource> requestOrganizationSpaceQuotas(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceQuota) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                    .page(page)
                    .organizationId(organizationId)
                    .build()))
            .filter(resource -> ResourceUtils.getEntity(resource).getName().equals(spaceQuota));
    }

    private static Fluxion<SpaceResource> requestOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .name(space)
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Fluxion<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .name(organizationName)
                    .page(page)
                    .build()));
    }

    private static Fluxion<ApplicationResource> requestSpaceApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listApplications(ListSpaceApplicationsRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Fluxion<DomainResource> requestSpaceDomains(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listDomains(ListSpaceDomainsRequest.builder()
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

    private static Fluxion<SecurityGroupResource> requestSpaceSecurityGroups(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listSecurityGroups(ListSpaceSecurityGroupsRequest.builder()
                    .spaceId(spaceId)
                    .page(page)
                    .build()));
    }

    private static Fluxion<ServiceResource> requestSpaceServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listServices(ListSpaceServicesRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Fluxion<SpaceResource> requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
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

    private static List<SpaceDetail.SecurityGroup.Rule> toSpaceDetailSecurityGroupRules(List<SecurityGroupEntity.RuleEntity> rules) {
        return rules.stream()
            .map(ruleEntity -> SpaceDetail.SecurityGroup.Rule.builder()
                .destination(ruleEntity.getDestination())
                .ports(ruleEntity.getPorts())
                .protocol(ruleEntity.getProtocol())
                .build())
            .collect(Collectors.toList());
    }

    private static List<SpaceDetail.SecurityGroup> toSpaceDetailSecurityGroups(List<SecurityGroupEntity> securityGroups) {
        return securityGroups.stream()
            .map(entity -> SpaceDetail.SecurityGroup.builder()
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
