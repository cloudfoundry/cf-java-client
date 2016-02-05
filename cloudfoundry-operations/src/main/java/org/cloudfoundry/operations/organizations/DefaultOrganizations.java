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
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.cloudfoundry.utils.tuple.Function2;
import org.cloudfoundry.utils.tuple.Function4;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

import java.util.List;

import static org.cloudfoundry.utils.tuple.TupleUtils.function;

public final class DefaultOrganizations implements Organizations {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultOrganizations(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<OrganizationInfo> info(OrganizationInfoRequest organizationInfoRequest) {
        return ValidationUtils
            .validate(organizationInfoRequest)
            .then(new Function<OrganizationInfoRequest, Mono<Tuple2<OrganizationResource, OrganizationInfoRequest>>>() {

                @Override
                public Mono<Tuple2<OrganizationResource, OrganizationInfoRequest>> apply(final OrganizationInfoRequest organizationInfoRequest) {
                    return addOrganizationResource(cloudFoundryClient, organizationInfoRequest);
                }

            })
            .then(getAuxiliaryContent(cloudFoundryClient));
    }

    @Override
    public Publisher<Organization> list() {
        return requestOrganizations(this.cloudFoundryClient)
            .map(new Function<OrganizationResource, Organization>() {

                @Override
                public Organization apply(OrganizationResource resource) {
                    return toOrganization(resource);
                }

            });
    }

    private static Mono<Tuple2<OrganizationResource, OrganizationInfoRequest>> addOrganizationResource(final CloudFoundryClient cloudFoundryClient,
                                                                                                       final OrganizationInfoRequest organizationInfoRequest) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationsResponse>>() {

                @Override
                public Mono<ListOrganizationsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .list(ListOrganizationsRequest.builder()
                            .name(organizationInfoRequest.getName())
                            .page(page)
                            .build());
                }

            })
            .single()
            .and(Mono.just(organizationInfoRequest));
    }

    private static Function<Tuple2<OrganizationResource, OrganizationInfoRequest>, Mono<OrganizationInfo>> getAuxiliaryContent(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<OrganizationResource, OrganizationInfoRequest, Mono<OrganizationInfo>>() {

            @Override
            public Mono<OrganizationInfo> apply(final OrganizationResource organizationResource, final OrganizationInfoRequest organizationInfoRequest) {
                return Mono.when(
                    getDomainNames(cloudFoundryClient, organizationResource),
                    getOrganizationQuota(cloudFoundryClient, organizationResource),
                    getSpaceQuotas(cloudFoundryClient, organizationResource),
                    getSpaces(cloudFoundryClient, organizationResource)
                )
                    .map(function(new Function4<List<String>, OrganizationQuota, List<SpaceQuota>, List<String>, OrganizationInfo>() {

                        @Override
                        public OrganizationInfo apply(List<String> domains, OrganizationQuota organizationQuota, List<SpaceQuota> spacesQuotas, List<String> spaces) {
                            return OrganizationInfo.builder()
                                .domains(domains)
                                .id(ResourceUtils.getId(organizationResource))
                                .name(organizationInfoRequest.getName())
                                .quota(organizationQuota)
                                .spacesQuotas(spacesQuotas)
                                .spaces(spaces)
                                .build();
                        }

                    }));
            }
        });

    }

    private static Mono<List<String>> getDomainNames(final CloudFoundryClient cloudFoundryClient, final OrganizationResource organizationResource) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationDomainsResponse>>() {

                @Override
                public Mono<ListOrganizationDomainsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listDomains(ListOrganizationDomainsRequest.builder()
                            .page(page)
                            .organizationId(ResourceUtils.getId(organizationResource))
                            .build());
                }

            }).map(new Function<DomainResource, String>() {

                @Override
                public String apply(DomainResource resource) {
                    return ResourceUtils.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<OrganizationQuota> getOrganizationQuota(final CloudFoundryClient cloudFoundryClient, final OrganizationResource organizationResource) {
        return cloudFoundryClient.organizationQuotaDefinitions()
            .get(GetOrganizationQuotaDefinitionRequest.builder()
                .quotaDefinitionId(ResourceUtils.getEntity(organizationResource).getQuotaDefinitionId())
                .build())
            .map(new Function<GetOrganizationQuotaDefinitionResponse, OrganizationQuota>() {

                @Override
                public OrganizationQuota apply(GetOrganizationQuotaDefinitionResponse response) {
                    return OrganizationQuota.builder()
                        .organizationId(ResourceUtils.getId(organizationResource))
                        .name(ResourceUtils.getEntity(response).getName())
                        .totalMemoryLimit(ResourceUtils.getEntity(response).getMemoryLimit())
                        .instanceMemoryLimit(ResourceUtils.getEntity(response).getInstanceMemoryLimit())
                        .totalRoutes(ResourceUtils.getEntity(response).getTotalRoutes())
                        .totalServiceInstances(ResourceUtils.getEntity(response).getTotalServices())
                        .paidServicePlans(ResourceUtils.getEntity(response).getNonBasicServicesAllowed())
                        .build();
                }

            });

    }

    private static Mono<List<SpaceQuota>> getSpaceQuotas(final CloudFoundryClient cloudFoundryClient, final OrganizationResource organizationResource) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationSpaceQuotaDefinitionsResponse>>() {

                @Override
                public Mono<ListOrganizationSpaceQuotaDefinitionsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                            .page(page)
                            .organizationId(ResourceUtils.getId(organizationResource))
                            .build()
                        );
                }

            }).map(new Function<SpaceQuotaDefinitionResource, SpaceQuota>() {

                @Override
                public SpaceQuota apply(SpaceQuotaDefinitionResource resource) {
                    return SpaceQuota.builder()
                        .organizationId(ResourceUtils.getEntity(resource).getOrganizationId())
                        .name(ResourceUtils.getEntity(resource).getName())
                        .totalMemoryLimit(ResourceUtils.getEntity(resource).getMemoryLimit())
                        .instanceMemoryLimit(ResourceUtils.getEntity(resource).getInstanceMemoryLimit())
                        .totalRoutes(ResourceUtils.getEntity(resource).getTotalRoutes())
                        .totalServiceInstances(ResourceUtils.getEntity(resource).getTotalServices())
                        .paidServicePlans(ResourceUtils.getEntity(resource).getNonBasicServicesAllowed())
                        .build();
                }

            })
            .toList();
    }

    private static Mono<List<String>> getSpaces(final CloudFoundryClient cloudFoundryClient, final OrganizationResource organizationResource) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

                @Override
                public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listSpaces(ListOrganizationSpacesRequest.builder()
                            .page(page)
                            .organizationId(ResourceUtils.getId(organizationResource))
                            .build()
                        );
                }

            }).map(new Function<SpaceResource, String>() {

                @Override
                public String apply(SpaceResource resource) {
                    return ResourceUtils.getEntity(resource).getName();
                }

            })
            .toList();
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

    private static Organization toOrganization(OrganizationResource resource) {
        return Organization.builder()
            .id(ResourceUtils.getId(resource))
            .name(ResourceUtils.getEntity(resource).getName())
            .build();
    }

}
