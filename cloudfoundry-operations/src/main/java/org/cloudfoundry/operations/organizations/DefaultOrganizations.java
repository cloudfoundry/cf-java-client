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
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.quotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.quotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.utils.ExceptionUtils;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.cloudfoundry.utils.tuple.Function2;
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

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultOrganizations(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
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

    private static Mono<GetOrganizationQuotaDefinitionResponse> requestOrganizationQuotaDefinition(CloudFoundryClient cloudFoundryClient, String quotaDefinitionId) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .get(GetOrganizationQuotaDefinitionRequest.builder()
                .quotaDefinitionId(quotaDefinitionId)
                .build());
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

    private Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .otherwise(ExceptionUtils.<OrganizationResource>convert("Organizations %s does not exist", organization));
    }

    private OrganizationDetail toOrganizationDetail(List<String> domains, OrganizationQuota organizationQuota, List<SpaceQuota> spacesQuotas, List<String> spaces,
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

}
