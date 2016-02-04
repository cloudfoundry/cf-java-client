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
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
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
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.operations.util.Exceptions;
import org.cloudfoundry.operations.util.Function2;
import org.cloudfoundry.operations.util.Function6;
import org.cloudfoundry.operations.util.Optional;
import org.cloudfoundry.operations.util.Optionals;
import org.cloudfoundry.operations.util.Validators;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

import java.util.List;

import static org.cloudfoundry.operations.util.Tuples.function;

public final class DefaultSpaces implements Spaces {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> organizationId;

    public DefaultSpaces(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
    }

    @Override
    public Mono<SpaceDetail> get(GetSpaceRequest request) {
        return Validators
            .validate(request)
            .and(this.organizationId)
            .then(function(new Function2<GetSpaceRequest, String, Mono<Tuple2<SpaceResource, GetSpaceRequest>>>() {

                @Override
                public Mono<Tuple2<SpaceResource, GetSpaceRequest>> apply(GetSpaceRequest request, String organizationId) {
                    return requestOrganizationSpace(DefaultSpaces.this.cloudFoundryClient, organizationId, request.getName())
                        .and(Mono.just(request));
                }

            }))
            .then(getAuxiliaryContent(this.cloudFoundryClient));
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

    private static Mono<List<String>> getApplicationNames(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        return requestSpaceApplications(cloudFoundryClient, Resources.getId(spaceResource))
            .map(new Function<ApplicationResource, String>() {

                @Override
                public String apply(ApplicationResource resource) {
                    return Resources.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Function<Tuple2<SpaceResource, GetSpaceRequest>, Mono<SpaceDetail>> getAuxiliaryContent(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<SpaceResource, GetSpaceRequest, Mono<SpaceDetail>>() {

            @Override
            public Mono<SpaceDetail> apply(final SpaceResource resource, GetSpaceRequest request) {
                return Mono
                    .when(
                        getApplicationNames(cloudFoundryClient, resource),
                        getDomainNames(cloudFoundryClient, resource),
                        getOrganizationName(cloudFoundryClient, resource),
                        getSecurityGroupNames(cloudFoundryClient, resource),
                        getServiceNames(cloudFoundryClient, resource),
                        getOptionalSpaceQuotaDefinition(cloudFoundryClient, request, resource)
                    )
                    .map(function(new Function6<List<String>, List<String>, String, List<String>, List<String>, Optional<SpaceQuota>, SpaceDetail>() {

                        @Override
                        public SpaceDetail apply(List<String> applications, List<String> domains, String organization, List<String> securityGroups, List<String> services, Optional<SpaceQuota> spaceQuota) {
                            return toSpaceDetail(applications, domains, organization, resource, securityGroups, services, spaceQuota);
                        }

                    }));
            }
        });
    }

    private static Mono<List<String>> getDomainNames(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestSpaceDomains(cloudFoundryClient, Resources.getId(resource))
            .map(new Function<DomainResource, String>() {

                @Override
                public String apply(DomainResource resource) {
                    return Resources.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<Optional<SpaceQuota>> getOptionalSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, GetSpaceRequest getSpaceRequest, SpaceResource spaceResource) {
        if (!getSpaceRequest.getSecurityGroupRules()) {
            return Mono.just(Optional.<SpaceQuota>empty());
        }

        return requestSpaceQuotaDefinition(cloudFoundryClient, Resources.getEntity(spaceResource).getSpaceQuotaDefinitionId())
            .map(new Function<GetSpaceQuotaDefinitionResponse, SpaceQuota>() {

                @Override
                public SpaceQuota apply(GetSpaceQuotaDefinitionResponse response) {
                    return toSpaceQuotaDefinition(response);
                }

            })
            .map(Optionals.<SpaceQuota>toOptional());
    }

    private static Mono<String> getOrganizationName(CloudFoundryClient cloudFoundryClient, SpaceResource resource) {
        return requestOrganization(cloudFoundryClient, Resources.getEntity(resource).getOrganizationId())
            .map(new Function<GetOrganizationResponse, String>() {

                @Override
                public String apply(GetOrganizationResponse response) {
                    return Resources.getEntity(response).getName();
                }

            });
    }

    private static Mono<List<String>> getSecurityGroupNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource resource) {
        return requestSpaceSecurityGroups(cloudFoundryClient, Resources.getId(resource))
            .map(new Function<SecurityGroupResource, String>() {

                @Override
                public String apply(SecurityGroupResource resource) {
                    return Resources.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<List<String>> getServiceNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource resource) {
        return requestSpaceServices(cloudFoundryClient, Resources.getId(resource))
            .map(new Function<ServiceResource, String>() {

                @Override
                public String apply(ServiceResource resource) {
                    return Resources.getEntity(resource).getLabel();
                }

            })
            .toList();
    }

    private static Mono<GetOrganizationResponse> requestOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build());
    }

    private static Mono<SpaceResource> requestOrganizationSpace(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String space) {
        return Paginated.requestResources(new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

                @Override
                public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listSpaces(ListOrganizationSpacesRequest.builder()
                            .name(space)
                            .organizationId(organizationId)
                            .page(page)
                            .build());
                }

            })
            .single()
            .otherwise(Exceptions.<SpaceResource>convert("Space %s does not exist", space));
    }

    private static Stream<ApplicationResource> requestSpaceApplications(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return Paginated.requestResources(new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

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
        return Paginated.requestResources(new Function<Integer, Mono<ListSpaceDomainsResponse>>() {

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
        return Paginated.requestResources(new Function<Integer, Mono<ListSpaceSecurityGroupsResponse>>() {

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
        return Paginated.requestResources(new Function<Integer, Mono<ListSpaceServicesResponse>>() {

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
        return Paginated.requestResources(new Function<Integer, Mono<ListSpacesResponse>>() {

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

    private static SpaceDetail toSpaceDetail(List<String> applications, List<String> domains, String organization, SpaceResource resource, List<String> securityGroups, List<String> services,
                                             Optional<SpaceQuota> spaceQuota) {
        return SpaceDetail.builder()
            .applications(applications)
            .domains(domains)
            .id(Resources.getId(resource))
            .name(Resources.getEntity(resource).getName())
            .organization(organization)
            .securityGroups(securityGroups)
            .services(services)
            .spaceQuota(spaceQuota)
            .build();
    }

    private static SpaceQuota toSpaceQuotaDefinition(Resource<SpaceQuotaDefinitionEntity> resource) {
        SpaceQuotaDefinitionEntity entity = Resources.getEntity(resource);

        return SpaceQuota.builder()
            .id(Resources.getId(resource))
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
            .id(Resources.getId(resource))
            .name(Resources.getEntity(resource).getName())
            .build();
    }

}
