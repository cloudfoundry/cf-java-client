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

package org.cloudfoundry.operations.organizations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.spaceadmin.SpaceQuota;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class DefaultOrganizations implements Organizations {

    private static final String SET_ROLES_BY_USERNAME_FEATURE_FLAG = "set_roles_by_username";

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<String> username;

    public DefaultOrganizations(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> username) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.username = username;
    }

    @Override
    public Mono<Void> create(CreateOrganizationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.username)
            .flatMap(function((cloudFoundryClient, username) -> Mono.when(
                Mono.just(cloudFoundryClient),
                createOrganization(cloudFoundryClient, request),
                getFeatureFlagEnabled(cloudFoundryClient, SET_ROLES_BY_USERNAME_FEATURE_FLAG),
                Mono.just(username)
            )))
            .filter(predicate((cloudFoundryClient, organizationId, setRolesByUsernameEnabled, username) -> setRolesByUsernameEnabled))
            .flatMap(function((cloudFoundryClient, organizationId, setRolesByUsernameEnabled, username) -> setOrganizationManager(cloudFoundryClient, organizationId, username)))
            .transform(OperationsLogging.log("Create Organization"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteOrganizationRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                Mono.just(request.getCompletionTimeout()),
                getOrganizationId(cloudFoundryClient, request.getName())
            ))
            .flatMap(function(DefaultOrganizations::deleteOrganization))
            .transform(OperationsLogging.log("Delete Organization"))
            .checkpoint();
    }

    @Override
    public Mono<OrganizationDetail> get(OrganizationInfoRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getOrganization(cloudFoundryClient, request.getName())
            ))
            .flatMap(function((cloudFoundryClient, organizationResource) -> getAuxiliaryContent(cloudFoundryClient, organizationResource)
                .map(function((domains, organizationQuota, spacesQuotas, spaces) -> toOrganizationDetail(domains, organizationQuota, spacesQuotas, spaces, organizationResource, request)))))
            .transform(OperationsLogging.log("Get Organization"))
            .checkpoint();
    }

    @Override
    public Flux<OrganizationSummary> list() {
        return this.cloudFoundryClient
            .flatMapMany(DefaultOrganizations::requestOrganizations)
            .map(DefaultOrganizations::toOrganizationSummary)
            .transform(OperationsLogging.log("List Organizations"))
            .checkpoint();
    }

    @Override
    public Mono<Void> rename(RenameOrganizationRequest request) {
        return this.cloudFoundryClient
            .flatMap(cloudFoundryClient -> Mono.when(
                Mono.just(cloudFoundryClient),
                getOrganizationId(cloudFoundryClient, request.getName())
            ))
            .flatMap(function((cloudFoundryClient, organizationId) -> requestUpdateOrganization(cloudFoundryClient, organizationId, request.getNewName())))
            .then()
            .transform(OperationsLogging.log("Rename Organization"))
            .checkpoint();
    }

    private static Mono<String> createOrganization(CloudFoundryClient cloudFoundryClient, CreateOrganizationRequest request) {
        return Mono
            .justOrEmpty(request.getQuotaDefinitionName())
            .flatMap(quotaDefinitionName -> getOrganizationQuotaDefinitionId(cloudFoundryClient, quotaDefinitionName))
            .flatMap(organizationQuotaDefinitionId -> getCreateOrganizationId(cloudFoundryClient, request.getOrganizationName(), organizationQuotaDefinitionId))
            .switchIfEmpty(getCreateOrganizationId(cloudFoundryClient, request.getOrganizationName(), null));
    }

    private static Mono<Void> deleteOrganization(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String organizationId) {
        return requestDeleteOrganization(cloudFoundryClient, organizationId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, job));
    }

    private static Mono<Tuple4<List<String>, OrganizationQuota, List<SpaceQuota>, List<String>>> getAuxiliaryContent(CloudFoundryClient cloudFoundryClient, OrganizationResource organizationResource) {
        String organizationId = ResourceUtils.getId(organizationResource);

        return Mono
            .when(
                getDomainNames(cloudFoundryClient, organizationId),
                getOrganizationQuota(cloudFoundryClient, organizationResource),
                getSpaceQuotas(cloudFoundryClient, organizationId),
                getSpaceNames(cloudFoundryClient, organizationId)
            );
    }

    private static Mono<String> getCreateOrganizationId(CloudFoundryClient cloudFoundryClient, String organization, String quotaDefinitionId) {
        return requestCreateOrganization(cloudFoundryClient, organization, quotaDefinitionId)
            .map(ResourceUtils::getId);
    }

    private static Mono<List<String>> getDomainNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListPrivateDomains(cloudFoundryClient, organizationId)
            .map(resource -> resource.getEntity().getName())
            .mergeWith(requestListSharedDomains(cloudFoundryClient)
                .map(resource -> resource.getEntity().getName()))
            .collectList();
    }

    private static Mono<Boolean> getFeatureFlagEnabled(CloudFoundryClient cloudFoundryClient, String featureFlag) {
        return requestGetFeatureFlag(cloudFoundryClient, featureFlag)
            .map(GetFeatureFlagResponse::getEnabled);
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

    private static Mono<OrganizationQuota> getOrganizationQuota(CloudFoundryClient cloudFoundryClient, OrganizationResource resource) {
        return requestOrganizationQuotaDefinition(cloudFoundryClient, ResourceUtils.getEntity(resource).getQuotaDefinitionId())
            .map(response -> toOrganizationQuota(response, resource));

    }

    private static Mono<OrganizationQuotaDefinitionResource> getOrganizationQuotaDefinition(CloudFoundryClient cloudFoundryClient, String quotaDefinitionName) {
        return requestOrganizationQuotaDefinitions(cloudFoundryClient, quotaDefinitionName)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Organization quota %s does not exist", quotaDefinitionName));
    }

    private static Mono<String> getOrganizationQuotaDefinitionId(CloudFoundryClient cloudFoundryClient, String quotaDefinitionName) {
        return getOrganizationQuotaDefinition(cloudFoundryClient, quotaDefinitionName)
            .map(ResourceUtils::getId);
    }

    private static Mono<List<String>> getSpaceNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestSpaces(cloudFoundryClient, organizationId)
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .collectList();
    }

    private static Mono<List<SpaceQuota>> getSpaceQuotas(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestSpaceQuotaDefinitions(cloudFoundryClient, organizationId)
            .map(DefaultOrganizations::toSpaceQuota)
            .collectList();
    }

    private static Mono<AssociateOrganizationManagerByUsernameResponse> requestAssociateOrganizationManagerByUsername(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        return cloudFoundryClient.organizations()
            .associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build());
    }

    private static Mono<AssociateOrganizationUserByUsernameResponse> requestAssociateOrganizationUserByUsername(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        return cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organization, String quotaDefinitionId) {
        return cloudFoundryClient.organizations()
            .create(org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest
                .builder()
                .name(organization)
                .quotaDefinitionId(quotaDefinitionId)
                .build());
    }

    private static Mono<DeleteOrganizationResponse> requestDeleteOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
            .delete(org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest.builder()
                .organizationId(organizationId)
                .async(true)
                .build());
    }

    private static Mono<GetFeatureFlagResponse> requestGetFeatureFlag(CloudFoundryClient cloudFoundryClient, String featureFlag) {
        return cloudFoundryClient.featureFlags()
            .get(GetFeatureFlagRequest.builder()
                .name(featureFlag)
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

    private static Mono<GetOrganizationQuotaDefinitionResponse> requestOrganizationQuotaDefinition(CloudFoundryClient cloudFoundryClient, String quotaDefinitionId) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .get(GetOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId(quotaDefinitionId)
                .build());
    }

    private static Flux<OrganizationQuotaDefinitionResource> requestOrganizationQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationQuotaDefinition) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizationQuotaDefinitions()
                .list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .name(organizationQuotaDefinition)
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

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceQuotaDefinitionResource> requestSpaceQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                    .page(page)
                    .organizationId(organizationId)
                    .build()));
    }

    private static Flux<SpaceResource> requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .page(page)
                    .organizationId(organizationId)
                    .build()));
    }

    private static Mono<UpdateOrganizationResponse> requestUpdateOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String newName) {
        return cloudFoundryClient.organizations()
            .update(UpdateOrganizationRequest.builder()
                .organizationId(organizationId)
                .name(newName)
                .build());
    }

    private static Mono<Void> setOrganizationManager(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        return Mono
            .when(
                requestAssociateOrganizationManagerByUsername(cloudFoundryClient, organizationId, username),
                requestAssociateOrganizationUserByUsername(cloudFoundryClient, organizationId, username)
            )
            .then();
    }

    private static OrganizationDetail toOrganizationDetail(List<String> domains, OrganizationQuota organizationQuota, List<SpaceQuota> spacesQuotas, List<String> spaces,
                                                           OrganizationResource organizationResource, OrganizationInfoRequest organizationInfoRequest) {
        return OrganizationDetail.builder()
            .domains(domains)
            .id(ResourceUtils.getId(organizationResource))
            .name(organizationInfoRequest.getName())
            .quota(organizationQuota)
            .spaceQuotas(spacesQuotas)
            .spaces(spaces)
            .build();
    }

    private static OrganizationQuota toOrganizationQuota(GetOrganizationQuotaDefinitionResponse response, OrganizationResource resource) {
        return OrganizationQuota.builder()
            .id(ResourceUtils.getId(response))
            .organizationId(ResourceUtils.getId(resource))
            .name(ResourceUtils.getEntity(response).getName())
            .totalMemoryLimit(ResourceUtils.getEntity(response).getMemoryLimit())
            .instanceMemoryLimit(ResourceUtils.getEntity(response).getInstanceMemoryLimit())
            .totalRoutes(ResourceUtils.getEntity(response).getTotalRoutes())
            .totalServiceInstances(ResourceUtils.getEntity(response).getTotalServices())
            .paidServicePlans(ResourceUtils.getEntity(response).getNonBasicServicesAllowed())
            .build();
    }

    private static OrganizationSummary toOrganizationSummary(OrganizationResource resource) {
        return OrganizationSummary.builder()
            .id(ResourceUtils.getId(resource))
            .name(ResourceUtils.getEntity(resource).getName())
            .build();
    }

    private static SpaceQuota toSpaceQuota(SpaceQuotaDefinitionResource resource) {
        return SpaceQuota.builder()
            .id(ResourceUtils.getId(resource))
            .organizationId(ResourceUtils.getEntity(resource).getOrganizationId())
            .name(ResourceUtils.getEntity(resource).getName())
            .totalMemoryLimit(ResourceUtils.getEntity(resource).getMemoryLimit())
            .instanceMemoryLimit(ResourceUtils.getEntity(resource).getInstanceMemoryLimit())
            .totalRoutes(ResourceUtils.getEntity(resource).getTotalRoutes())
            .totalServiceInstances(ResourceUtils.getEntity(resource).getTotalServices())
            .paidServicePlans(ResourceUtils.getEntity(resource).getNonBasicServicesAllowed())
            .build();
    }

}
