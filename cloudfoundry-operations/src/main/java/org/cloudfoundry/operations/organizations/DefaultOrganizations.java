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

package org.cloudfoundry.operations.organizations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.utils.ExceptionUtils;
import org.cloudfoundry.utils.Optional;
import org.cloudfoundry.utils.OptionalUtils;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.cloudfoundry.utils.tuple.Function2;
import org.cloudfoundry.utils.tuple.Function3;
import org.cloudfoundry.utils.tuple.Function4;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple4;
import reactor.rx.Stream;

import java.util.List;

import static org.cloudfoundry.utils.tuple.TupleUtils.function;

public final class DefaultOrganizations implements Organizations {

    public static final String SET_ROLES_BY_USERNAME_FEATURE_FLAG = "set_roles_by_username";

    private final Mono<String> clientusername;

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultOrganizations(CloudFoundryClient cloudFoundryClient, Mono<String> clientUsername) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.clientusername = clientUsername;
    }

    @Override
    public Mono<Void> create(CreateOrganizationRequest request) {
        return Mono
            .when(createOrganization(cloudFoundryClient, ValidationUtils.validate(request)),
                requestGetFeatureFlag(cloudFoundryClient, SET_ROLES_BY_USERNAME_FEATURE_FLAG),
                this.clientusername)
            .then(function(new Function3<String, Boolean, String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(final String organizationId, Boolean setRolesByUsernameEnabled, final String clientUsername) {
                    if (setRolesByUsernameEnabled) {
                        return setOrganizationManager(cloudFoundryClient, organizationId, clientUsername);
                    }
                    return Mono.empty();
                }

            }));
    }

    @Override
    public Mono<OrganizationDetail> get(OrganizationInfoRequest request) {

        return ValidationUtils
            .validate(request)
            .then(new Function<OrganizationInfoRequest, Mono<Tuple2<OrganizationResource, OrganizationInfoRequest>>>() {

                @Override
                public Mono<Tuple2<OrganizationResource, OrganizationInfoRequest>> apply(OrganizationInfoRequest request) {
                    return getOrganization(DefaultOrganizations.this.cloudFoundryClient, request.getName())
                        .and(Mono.just(request));
                }

            })
            .then(function(new Function2<OrganizationResource, OrganizationInfoRequest, Mono<OrganizationDetail>>() {

                @Override
                public Mono<OrganizationDetail> apply(final OrganizationResource organizationResource, final OrganizationInfoRequest organizationInfoRequest) {
                    return getAuxiliaryContent(DefaultOrganizations.this.cloudFoundryClient, organizationResource)
                        .map(function(new Function4<List<String>, OrganizationQuota, List<SpaceQuota>, List<String>, OrganizationDetail>() {

                            @Override
                            public OrganizationDetail apply(List<String> domains, OrganizationQuota organizationQuota, List<SpaceQuota> spacesQuotas, List<String> spaces) {
                                return toOrganizationDetail(domains, organizationQuota, spacesQuotas, spaces, organizationResource, organizationInfoRequest);
                            }

                        }));
                }
            }));
    }

    @Override
    public Publisher<OrganizationSummary> list() {
        return requestOrganizations(this.cloudFoundryClient)
            .map(new Function<OrganizationResource, OrganizationSummary>() {

                @Override
                public OrganizationSummary apply(OrganizationResource resource) {
                    return toOrganizationSummary(resource);
                }

            });
    }

    @Override
    public Mono<Void> rename(final RenameOrganizationRequest request) {
        return ValidationUtils
            .validate(request)
            .then(new Function<RenameOrganizationRequest, Mono<Tuple2<String, RenameOrganizationRequest>>>() {

                @Override
                public Mono<Tuple2<String, RenameOrganizationRequest>> apply(RenameOrganizationRequest renameOrganizationRequest) {
                    return getOrganizationId(DefaultOrganizations.this.cloudFoundryClient, request.getName())
                        .and(Mono.just(request));
                }

            })
            .then(function(new Function2<String, RenameOrganizationRequest, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String organizationId, RenameOrganizationRequest request) {
                    return requestUpdateOrganization(DefaultOrganizations.this.cloudFoundryClient, organizationId, request.getNewName())
                        .after();
                }

            }));
    }

    private static Mono<String> createOrganization(final CloudFoundryClient cloudFoundryClient, Mono<CreateOrganizationRequest> validatedRequest) {
        return validatedRequest
            .then(new Function<CreateOrganizationRequest, Mono<Tuple2<Optional<String>, CreateOrganizationRequest>>>() {

                @Override
                public Mono<Tuple2<Optional<String>, CreateOrganizationRequest>> apply(CreateOrganizationRequest createOrganizationRequest) {
                    final String quotaDefinitionName = createOrganizationRequest.getQuotaDefinitionName();

                    return (quotaDefinitionName == null ? Mono.just(Optional.<String>empty()) : requestOrganizationQuotaDefinitionId(cloudFoundryClient, quotaDefinitionName)
                        .map(OptionalUtils.<String>toOptional()))
                        .and(Mono.just(createOrganizationRequest));
                }

            })
            .then(function(new Function2<Optional<String>, CreateOrganizationRequest, Mono<String>>() {

                @Override
                public Mono<String> apply(Optional<String> optionalQuotaDefinitionId, CreateOrganizationRequest request) {
                    return requestCreateOrganization(cloudFoundryClient, request.getOrganizationName(), optionalQuotaDefinitionId);
                }

            }));
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

    private static Mono<List<String>> getDomainNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestDomains(cloudFoundryClient, organizationId)
            .map(new Function<DomainResource, String>() {

                @Override
                public String apply(DomainResource resource) {
                    return ResourceUtils.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .otherwise(ExceptionUtils.<OrganizationResource>convert("Organization %s does not exist", organization));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils.extractId());
    }

    private static Mono<OrganizationQuota> getOrganizationQuota(final CloudFoundryClient cloudFoundryClient, final OrganizationResource resource) {
        return requestOrganizationQuotaDefinition(cloudFoundryClient, ResourceUtils.getEntity(resource).getQuotaDefinitionId())
            .map(new Function<GetOrganizationQuotaDefinitionResponse, OrganizationQuota>() {

                @Override
                public OrganizationQuota apply(GetOrganizationQuotaDefinitionResponse response) {
                    return toOrganizationQuota(response, resource);
                }

            });

    }

    private static Mono<List<String>> getSpaceNames(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestSpaces(cloudFoundryClient, organizationId)
            .map(new Function<SpaceResource, String>() {

                @Override
                public String apply(SpaceResource resource) {
                    return ResourceUtils.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<List<SpaceQuota>> getSpaceQuotas(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestSpaceQuotaDefinitions(cloudFoundryClient, organizationId)
            .map(new Function<SpaceQuotaDefinitionResource, SpaceQuota>() {

                @Override
                public SpaceQuota apply(SpaceQuotaDefinitionResource resource) {
                    return toSpaceQuota(resource);
                }

            })
            .toList();
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

    private static Mono<String> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName, Optional<String> optionalQuotaDefinitionId) {
        org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest.CreateOrganizationRequestBuilder requestBuilder = org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest
            .builder()
            .name(organizationName);

        if (optionalQuotaDefinitionId.isPresent()) {
            requestBuilder.quotaDefinitionId(optionalQuotaDefinitionId.get());
        }

        return cloudFoundryClient.organizations()
            .create(requestBuilder.build())
            .map(ResourceUtils.extractId());
    }

    private static Stream<DomainResource> requestDomains(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationDomainsResponse>>() {

                @Override
                public Mono<ListOrganizationDomainsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listDomains(ListOrganizationDomainsRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build());
                }

            });
    }

    private static Mono<Boolean> requestGetFeatureFlag(CloudFoundryClient cloudFoundryClient, String featureFlag) {
        return cloudFoundryClient.featureFlags()
            .get(GetFeatureFlagRequest.builder()
                .name(featureFlag)
                .build())
            .map(new Function<GetFeatureFlagResponse, Boolean>() {

                @Override
                public Boolean apply(GetFeatureFlagResponse response) {
                    return response.getEnabled();
                }

            });
    }

    private static Mono<GetOrganizationQuotaDefinitionResponse> requestOrganizationQuotaDefinition(CloudFoundryClient cloudFoundryClient, String quotaDefinitionId) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .get(GetOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId(quotaDefinitionId)
                .build());
    }

    private static Mono<String> requestOrganizationQuotaDefinitionId(CloudFoundryClient cloudFoundryClient, String quotaDefinitionName) {
        return PaginationUtils
            .requestResources(requestOrganizationQuotaDefinitionPage(cloudFoundryClient, quotaDefinitionName))
            .single()
            .map(ResourceUtils.extractId())
            .otherwise(ExceptionUtils.<String>convert("Organization quota %s does not exist", quotaDefinitionName));
    }

    private static Function<Integer, Mono<ListOrganizationQuotaDefinitionsResponse>> requestOrganizationQuotaDefinitionPage(final CloudFoundryClient cloudFoundryClient,
                                                                                                                            final String quotaDefinitionName) {
        return new Function<Integer, Mono<ListOrganizationQuotaDefinitionsResponse>>() {

            @Override
            public Mono<ListOrganizationQuotaDefinitionsResponse> apply(Integer page) {
                return cloudFoundryClient.organizationQuotaDefinitions().list(ListOrganizationQuotaDefinitionsRequest.builder()
                    .name(quotaDefinitionName)
                    .page(page)
                    .build());
            }

        };
    }

    private static Stream<OrganizationResource> requestOrganizations(final CloudFoundryClient cloudFoundryClient, final String organizationName) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationsResponse>>() {

                @Override
                public Mono<ListOrganizationsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .list(ListOrganizationsRequest.builder()
                            .name(organizationName)
                            .page(page)
                            .build());
                }

            });
    }

    private static Stream<OrganizationResource> requestOrganizations(final CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationsResponse>>() {

                @Override
                public Mono<ListOrganizationsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .list(ListOrganizationsRequest.builder()
                            .page(page)
                            .build());
                }

            });
    }

    private static Stream<SpaceQuotaDefinitionResource> requestSpaceQuotaDefinitions(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationSpaceQuotaDefinitionsResponse>>() {

                @Override
                public Mono<ListOrganizationSpaceQuotaDefinitionsResponse> apply(Integer page) {

                    return cloudFoundryClient.organizations()
                        .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()
                        );
                }

            });
    }

    private static Stream<SpaceResource> requestSpaces(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

                @Override
                public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listSpaces(ListOrganizationSpacesRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()
                        );
                }

            });
    }

    private static Mono<UpdateOrganizationResponse> requestUpdateOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String newName) {
        return cloudFoundryClient.organizations()
            .update(UpdateOrganizationRequest.builder()
                .organizationId(organizationId)
                .name(newName)
                .build());
    }

    private static Mono<Void> setOrganizationManager(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String managerUsername) {
        return Mono
            .when(requestAssociateOrganizationManagerByUsername(cloudFoundryClient, organizationId, managerUsername),
                requestAssociateOrganizationUserByUsername(cloudFoundryClient, organizationId, managerUsername))
            .after();
    }

    private static OrganizationDetail toOrganizationDetail(List<String> domains, OrganizationQuota organizationQuota, List<SpaceQuota> spacesQuotas, List<String> spaces,
                                                           OrganizationResource organizationResource, OrganizationInfoRequest organizationInfoRequest) {
        return OrganizationDetail.builder()
            .domains(domains)
            .id(ResourceUtils.getId(organizationResource))
            .name(organizationInfoRequest.getName())
            .quota(organizationQuota)
            .spacesQuotas(spacesQuotas)
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
