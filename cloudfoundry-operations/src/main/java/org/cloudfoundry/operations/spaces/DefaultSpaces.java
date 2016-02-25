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
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
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
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.Optional;
import org.cloudfoundry.util.OptionalUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import org.cloudfoundry.util.tuple.Function2;
import org.cloudfoundry.util.tuple.Function3;
import org.cloudfoundry.util.tuple.Function6;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;
import reactor.rx.Stream;

import java.util.ArrayList;
import java.util.List;

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
            .then(function(new Function2<AllowSpaceSshRequest, String, Mono<String>>() {

                @Override
                public Mono<String> apply(AllowSpaceSshRequest request, String organizationId) {
                    return getOrganizationSpaceIdWhere(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName(), sshEnabled(false));
                }

            }))
            .then(new Function<String, Mono<UpdateSpaceResponse>>() {

                @Override
                public Mono<UpdateSpaceResponse> apply(String spaceId) {
                    return requestUpdateSpaceSsh(DefaultSpaces.this.cloudFoundryClient, spaceId, true);
                }

            })
            .after();
    }

    @Override
    public Mono<Void> create(CreateSpaceRequest request) {
        return ValidationUtils.validate(request)
            .then(new Function<CreateSpaceRequest, Mono<Tuple2<CreateSpaceRequest, String>>>() {

                @Override
                public Mono<Tuple2<CreateSpaceRequest, String>> apply(CreateSpaceRequest request) {
                    return Mono
                        .when(
                            Mono.just(request),
                            getOrganizationOrDefault(DefaultSpaces.this.cloudFoundryClient, request, DefaultSpaces.this.organizationId)
                        );
                }

            })
            .then(function(new Function2<CreateSpaceRequest, String, Mono<Tuple3<CreateSpaceRequest, String, Optional<String>>>>() {

                @Override
                public Mono<Tuple3<CreateSpaceRequest, String, Optional<String>>> apply(CreateSpaceRequest request, String organizationId) {
                    return Mono
                        .when(
                            Mono.just(request),
                            Mono.just(organizationId),
                            getOptionalSpaceQuotaId(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getSpaceQuota())
                        );
                }

            }))
            .then(function(new Function3<CreateSpaceRequest, String, Optional<String>, Mono<Tuple3<String, String, String>>>() {

                @Override
                public Mono<Tuple3<String, String, String>> apply(CreateSpaceRequest request, String organizationId, Optional<String> spaceQuotaId) {
                    return Mono
                        .when(
                            Mono.just(organizationId),
                            requestCreateSpace(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName(), spaceQuotaId.orElse(null))
                                .map(ResourceUtils.extractId()),
                            DefaultSpaces.this.username
                        );
                }

            }))
            .as(thenKeep((function(new Function3<String, String, String, Mono<AssociateOrganizationUserByUsernameResponse>>() {

                @Override
                public Mono<AssociateOrganizationUserByUsernameResponse> apply(String organizationId, String spaceId, String username) {
                    return requestAssociateOrganizationUserByUsername(DefaultSpaces.this.cloudFoundryClient, organizationId, username);
                }

            }))))
            .as(thenKeep((function(new Function3<String, String, String, Mono<Tuple2<AssociateSpaceManagerByUsernameResponse, AssociateSpaceDeveloperByUsernameResponse>>>() {

                @Override
                public Mono<Tuple2<AssociateSpaceManagerByUsernameResponse, AssociateSpaceDeveloperByUsernameResponse>> apply(String organizationId, String spaceId, String username) {
                    return Mono
                        .when(
                            requestAssociateSpaceManagerByUsername(DefaultSpaces.this.cloudFoundryClient, spaceId, username),
                            requestAssociateSpaceDeveloperByUsername(DefaultSpaces.this.cloudFoundryClient, spaceId, username)
                        );
                }

            }))))
            .after();
    }

    @Override
    public Mono<Void> delete(DeleteSpaceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function(new Function2<DeleteSpaceRequest, String, Mono<String>>() {

                @Override
                public Mono<String> apply(DeleteSpaceRequest request, String organizationId) {
                    return getOrganizationSpaceId(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName());
                }

            }))
            .then(new Function<String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String spaceId) {
                    return deleteSpace(DefaultSpaces.this.cloudFoundryClient, spaceId);
                }

            });
    }

    @Override
    public Mono<Void> disallowSsh(DisallowSpaceSshRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function(new Function2<DisallowSpaceSshRequest, String, Mono<String>>() {

                @Override
                public Mono<String> apply(DisallowSpaceSshRequest request, String organizationId) {
                    return getOrganizationSpaceIdWhere(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName(), sshEnabled(true));
                }

            }))
            .then(new Function<String, Mono<UpdateSpaceResponse>>() {

                @Override
                public Mono<UpdateSpaceResponse> apply(String spaceId) {
                    return requestUpdateSpaceSsh(DefaultSpaces.this.cloudFoundryClient, spaceId, false);
                }

            })
            .after();
    }

    @Override
    public Mono<SpaceDetail> get(GetSpaceRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.organizationId)
            .then(function(new Function2<GetSpaceRequest, String, Mono<Tuple2<GetSpaceRequest, SpaceResource>>>() {

                @Override
                public Mono<Tuple2<GetSpaceRequest, SpaceResource>> apply(GetSpaceRequest request, String organizationId) {
                    return Mono.just(request)
                        .and(getOrganizationSpace(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName()));
                }

            }))
            .then(function(new Function2<GetSpaceRequest, SpaceResource, Mono<SpaceDetail>>() {

                @Override
                public Mono<SpaceDetail> apply(GetSpaceRequest request, SpaceResource resource) {
                    return getSpaceDetail(DefaultSpaces.this.cloudFoundryClient, resource, request);
                }

            }));
    }

    @Override
    public Publisher<SpaceSummary> list() {
        return this.organizationId
            .flatMap(new Function<String, Stream<SpaceResource>>() {

                @Override
                public Stream<SpaceResource> apply(String organizationId) {
                    return requestSpaces(DefaultSpaces.this.cloudFoundryClient, organizationId);
                }

            })
            .map(new Function<SpaceResource, SpaceSummary>() {

                @Override
                public SpaceSummary apply(SpaceResource resource) {
                    return toSpaceSummary(resource);
                }

            });
    }

    @Override
    public Mono<Void> rename(RenameSpaceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function(new Function2<RenameSpaceRequest, String, Mono<Tuple2<String, RenameSpaceRequest>>>() {

                @Override
                public Mono<Tuple2<String, RenameSpaceRequest>> apply(RenameSpaceRequest request, String organizationId) {
                    return getOrganizationSpaceId(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName())
                        .and(Mono.just(request));
                }

            }))
            .then(function(new Function2<String, RenameSpaceRequest, Mono<UpdateSpaceResponse>>() {

                @Override
                public Mono<UpdateSpaceResponse> apply(String spaceId, RenameSpaceRequest request) {
                    return requestUpdateSpace(DefaultSpaces.this.cloudFoundryClient, spaceId, request.getNewName());
                }

            }))
            .after();

    }

    @Override
    public Mono<Boolean> sshAllowed(SpaceSshAllowedRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId)
            .then(function(new Function2<SpaceSshAllowedRequest, String, Mono<SpaceResource>>() {

                @Override
                public Mono<SpaceResource> apply(SpaceSshAllowedRequest request, String organizationId) {
                    return getOrganizationSpace(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName());
                }

            }))
            .map(new Function<SpaceResource, Boolean>() {

                @Override
                public Boolean apply(SpaceResource resource) {
                    return ResourceUtils.getEntity(resource).getAllowSsh();
                }

            });
    }

    private static Mono<Void> deleteSpace(final CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestDeleteSpace(cloudFoundryClient, spaceId)
            .map(ResourceUtils.extractId())
            .then(new Function<String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String jobId) {
                    return JobUtils.waitForCompletion(cloudFoundryClient, jobId);
                }

            });
    }

    private static Mono<List<String>> getApplicationNames(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        return requestSpaceApplications(cloudFoundryClient, ResourceUtils.getId(spaceResource))
            .map(new Function<ApplicationResource, String>() {

                @Override
                public String apply(ApplicationResource resource) {
                    return ResourceUtils.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<List<String>> getDomainNames(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestSpaceDomains(cloudFoundryClient, ResourceUtils.getId(resource))
            .map(new Function<DomainResource, String>() {

                @Override
                public String apply(DomainResource resource) {
                    return ResourceUtils.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<Optional<SpaceQuota>> getOptionalSpaceQuotaDefinition(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        String spaceQuotaDefinitionId = ResourceUtils.getEntity(spaceResource).getSpaceQuotaDefinitionId();

        if (spaceQuotaDefinitionId == null) {
            return Mono.just(Optional.<SpaceQuota>empty());
        }

        return requestSpaceQuotaDefinition(cloudFoundryClient, spaceQuotaDefinitionId)
            .map(new Function<GetSpaceQuotaDefinitionResponse, SpaceQuota>() {

                @Override
                public SpaceQuota apply(GetSpaceQuotaDefinitionResponse response) {
                    return toSpaceQuotaDefinition(response);
                }

            })
            .map(OptionalUtils.<SpaceQuota>toOptional());
    }

    private static Mono<Optional<String>> getOptionalSpaceQuotaId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceQuota) {
        if (spaceQuota == null) {
            return Mono.just(Optional.<String>empty());
        }
        return getSpaceQuota(cloudFoundryClient, organizationId, spaceQuota)
            .map(ResourceUtils.extractId())
            .map(OptionalUtils.<String>toOptional());
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

    private static Mono<String> getOrganizationName(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestOrganization(cloudFoundryClient, ResourceUtils.getEntity(resource).getOrganizationId())
            .map(new Function<GetOrganizationResponse, String>() {

                @Override
                public String apply(GetOrganizationResponse response) {
                    return ResourceUtils.getEntity(response).getName();
                }

            });
    }

    private static Mono<String> getOrganizationOrDefault(CloudFoundryClient cloudFoundryClient, CreateSpaceRequest request, Mono<String> organizationId) {
        return request.getOrganization() != null
            ? getOrganizationId(cloudFoundryClient, request.getOrganization())
            : organizationId;
    }

    private static Mono<SpaceResource> getOrganizationSpace(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String space) {
        return requestOrganizationSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .otherwise(ExceptionUtils.<SpaceResource>convert("Space %s does not exist", space));
    }

    private static Mono<String> getOrganizationSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getOrganizationSpace(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils.extractId());
    }

    private static Mono<String> getOrganizationSpaceIdWhere(CloudFoundryClient cloudFoundryClient, String organizationId, String space, Predicate<SpaceResource> predicate) {
        return getOrganizationSpace(cloudFoundryClient, organizationId, space)
            .where(predicate)
            .map(ResourceUtils.extractId());
    }

    private static Mono<List<SecurityGroupEntity>> getSecurityGroups(final CloudFoundryClient cloudFoundryClient, final SpaceResource resource, final boolean withRules) {
        return requestSpaceSecurityGroups(cloudFoundryClient, ResourceUtils.getId(resource))
            .map(new Function<SecurityGroupResource, SecurityGroupEntity>() {

                     @Override
                     public SecurityGroupEntity apply(SecurityGroupResource resource) {
                         SecurityGroupEntity entity = ResourceUtils.getEntity(resource);
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
                 }

            )
            .toList();
    }

    private static Mono<List<String>> getServiceNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource resource) {
        return requestSpaceServices(cloudFoundryClient, ResourceUtils.getId(resource))
            .map(new Function<ServiceResource, String>() {

                @Override
                public String apply(ServiceResource resource) {
                    return ResourceUtils.getEntity(resource).getLabel();
                }

            })
            .toList();
    }

    private static Mono<SpaceDetail> getSpaceDetail(CloudFoundryClient cloudFoundryClient, final SpaceResource resource, GetSpaceRequest request) {
        return Mono
            .when(
                getApplicationNames(cloudFoundryClient, resource),
                getDomainNames(cloudFoundryClient, resource),
                getOrganizationName(cloudFoundryClient, resource),
                getSecurityGroups(cloudFoundryClient, resource, Optional.ofNullable(request.getSecurityGroupRules()).orElse(false)),
                getServiceNames(cloudFoundryClient, resource),
                getOptionalSpaceQuotaDefinition(cloudFoundryClient, resource)
            )
            .map(function(new Function6<List<String>, List<String>, String, List<SecurityGroupEntity>, List<String>, Optional<SpaceQuota>, SpaceDetail>() {

                @Override
                public SpaceDetail apply(List<String> applications, List<String> domains, String organization, List<SecurityGroupEntity> securityGroups, List<String> services,
                                         Optional<SpaceQuota>
                                             spaceQuota) {
                    return toSpaceDetail(applications, domains, organization, resource, securityGroups, services, spaceQuota);
                }

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

    private static Stream<SpaceQuotaDefinitionResource> requestOrganizationSpaceQuotas(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String spaceQuota) {
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

            })
            .filter(new Predicate<SpaceQuotaDefinitionResource>() {

                @Override
                public boolean test(SpaceQuotaDefinitionResource resource) {
                    return ResourceUtils.getEntity(resource).getName().equals(spaceQuota);
                }

            });
    }

    private static Stream<SpaceResource> requestOrganizationSpaces(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String space) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

                @Override
                public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listSpaces(ListOrganizationSpacesRequest.builder()
                            .name(space)
                            .organizationId(organizationId)
                            .page(page)
                            .build());
                }

            });
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

    private static Stream<ApplicationResource> requestSpaceApplications(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

                @Override
                public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                    ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build();

                    return cloudFoundryClient.spaces().listApplications(request);
                }

            });
    }

    private static Stream<DomainResource> requestSpaceDomains(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceDomainsResponse>>() {

                @Override
                public Mono<ListSpaceDomainsResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listDomains(ListSpaceDomainsRequest.builder()
                            .page(page)
                            .spaceId(spaceId)
                            .build());
                }

            });
    }

    private static Mono<GetSpaceQuotaDefinitionResponse> requestSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, String spaceQuotaDefinitionId) {
        return cloudFoundryClient.spaceQuotaDefinitions()
            .get(GetSpaceQuotaDefinitionRequest.builder()
                .spaceQuotaDefinitionId(spaceQuotaDefinitionId)
                .build());
    }

    private static Stream<SecurityGroupResource> requestSpaceSecurityGroups(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceSecurityGroupsResponse>>() {

                @Override
                public Mono<ListSpaceSecurityGroupsResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listSecurityGroups(ListSpaceSecurityGroupsRequest.builder()
                            .spaceId(spaceId)
                            .page(page)
                            .build());
                }

            });
    }

    private static Stream<ServiceResource> requestSpaceServices(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceServicesResponse>>() {

                @Override
                public Mono<ListSpaceServicesResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listServices(ListSpaceServicesRequest.builder()
                            .page(page)
                            .spaceId(spaceId)
                            .build());
                }

            });
    }

    private static Stream<SpaceResource> requestSpaces(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpacesResponse>>() {

                @Override
                public Mono<ListSpacesResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                            .organizationId(organizationId)
                            .page(page)
                            .build());
                }

            });
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

    private static Predicate<SpaceResource> sshEnabled(final Boolean enabled) {
        return new Predicate<SpaceResource>() {

            @Override
            public boolean test(SpaceResource resource) {
                return enabled.equals(ResourceUtils.getEntity(resource).getAllowSsh());
            }

        };
    }

    /**
     * Produces a Mono transformer that preserves the type of the source {@code Mono<IN>}.
     *
     * <p> The Mono produced expects a single element from the source, passes this to the function (as in {@code .then}) and requests an element from the resulting {@code Mono<OUT>}. When successful,
     * the result is discarded and input value is signalled. </p>
     *
     * <p> <b>Summary:</b> does a {@code .then} on the new Mono but keeps the input to pass on unchanged. </p>
     *
     * <p> <b>Usage:</b> Can be used inline thus: {@code .as(thenKeep(in -> funcOf(in)))} </p>
     *
     * @param thenFunction from source input element to some {@code Mono<OUT>}
     * @param <IN>         the source element type
     * @param <OUT>        the element type of the Mono produced by {@code thenFunction}
     * @return a Mono transformer
     */
    private static <IN, OUT> Function<Mono<IN>, Mono<IN>> thenKeep(final Function<IN, Mono<OUT>> thenFunction) {
        return new Function<Mono<IN>, Mono<IN>>() {

            @Override
            public Mono<IN> apply(Mono<IN> source) {
                return source
                    .then(new Function<IN, Mono<? extends IN>>() {

                        @Override
                        public Mono<? extends IN> apply(final IN in) {
                            return thenFunction
                                .apply(in)
                                .map(new Function<OUT, IN>() {

                                    @Override
                                    public IN apply(OUT ignore) {
                                        return in;
                                    }
                                });
                        }
                    });
            }
        };
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
        List<SpaceDetail.SecurityGroup.Rule> result = new ArrayList<>();
        for (SecurityGroupEntity.RuleEntity ruleEntity : rules) {
            result.add(SpaceDetail.SecurityGroup.Rule.builder()
                .destination(ruleEntity.getDestination())
                .ports(ruleEntity.getPorts())
                .protocol(ruleEntity.getProtocol())
                .build());
        }
        return result;
    }

    private static List<SpaceDetail.SecurityGroup> toSpaceDetailSecurityGroups(List<SecurityGroupEntity> securityGroups) {
        List<SpaceDetail.SecurityGroup> result = new ArrayList<>();
        for (SecurityGroupEntity entity : securityGroups) {
            result.add(SpaceDetail.SecurityGroup.builder()
                .name(entity.getName())
                .rules(toSpaceDetailSecurityGroupRules(entity.getRules()))
                .build());
        }
        return result;
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
