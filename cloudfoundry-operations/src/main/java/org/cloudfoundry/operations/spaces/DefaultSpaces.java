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
import org.cloudfoundry.operations.util.Optional;
import org.cloudfoundry.operations.util.Optionals;
import org.cloudfoundry.operations.util.Validators;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple6;
import reactor.rx.Stream;

import java.util.List;

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
                .then(requestSpaceResourceWithContext(this.cloudFoundryClient))
                .then(getAuxiliaryContent(this.cloudFoundryClient));
    }

    @Override
    public Publisher<SpaceSummary> list() {
        return this.organizationId
                .flatMap(requestSpaceResources(this.cloudFoundryClient))
                .map(toSpaceSummary());
    }

    private static Function<ApplicationResource, String> extractApplicationName() {
        return new Function<ApplicationResource, String>() {

            @Override
            public String apply(ApplicationResource resource) {
                return Resources.getEntity(resource).getName();
            }

        };
    }

    private static Function<DomainResource, String> extractDomainName() {
        return new Function<DomainResource, String>() {

            @Override
            public String apply(DomainResource resource) {
                return Resources.getEntity(resource).getName();
            }

        };
    }

    private static Function<GetOrganizationResponse, String> extractOrganizationName() {
        return new Function<GetOrganizationResponse, String>() {

            @Override
            public String apply(GetOrganizationResponse getOrganizationResponse) {
                return Resources.getEntity(getOrganizationResponse).getName();
            }

        };
    }

    private static Function<SecurityGroupResource, String> extractSecurityGroupName() {
        return new Function<SecurityGroupResource, String>() {

            @Override
            public String apply(SecurityGroupResource resource) {
                return Resources.getEntity(resource).getName();
            }

        };
    }

    private static Function<ServiceResource, String> extractServiceName() {
        return new Function<ServiceResource, String>() {

            @Override
            public String apply(ServiceResource resource) {
                return Resources.getEntity(resource).getLabel();
            }

        };
    }

    private static Function<Tuple2<SpaceResource, GetSpaceRequest>, Mono<SpaceDetail>> getAuxiliaryContent(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<SpaceResource, GetSpaceRequest>, Mono<SpaceDetail>>() {

            @Override
            public Mono<SpaceDetail> apply(Tuple2<SpaceResource, GetSpaceRequest> tuple) {
                SpaceResource spaceResource = tuple.t1;
                GetSpaceRequest request = tuple.t2;

                return Mono
                        .when(requestApplicationNames(cloudFoundryClient, spaceResource), requestDomainNames(cloudFoundryClient, spaceResource),
                                requestOrganizationName(cloudFoundryClient, spaceResource), requestSecurityGroups(cloudFoundryClient, spaceResource),
                                requestServiceNames(cloudFoundryClient, spaceResource), requestSpaceQuotaDefinition(cloudFoundryClient, request, spaceResource))
                        .map(toSpaceDetail(spaceResource));
            }

        };
    }

    private static Mono<List<String>> requestApplicationNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceApplicationPage(cloudFoundryClient, spaceResource))
                .map(extractApplicationName())
                .toList();
    }

    private static Mono<List<String>> requestDomainNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceDomainPage(cloudFoundryClient, spaceResource))
                .map(extractDomainName())
                .toList();
    }

    private static Mono<String> requestOrganizationName(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        GetOrganizationRequest request = GetOrganizationRequest.builder()
                .id(Resources.getEntity(spaceResource).getOrganizationId())
                .build();

        return cloudFoundryClient.organizations().get(request)
                .map(extractOrganizationName());
    }

    private static Function<Integer, Mono<ListOrganizationSpacesResponse>> requestOrganizationSpacePage(final CloudFoundryClient cloudFoundryClient, final String organizationId,
                                                                                                        final GetSpaceRequest getSpaceRequest) {
        return new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

            @Override
            public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                ListOrganizationSpacesRequest listOrganizationSpacesRequest = ListOrganizationSpacesRequest.builder()
                        .organizationId(organizationId)
                        .name(getSpaceRequest.getName())
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().listSpaces(listOrganizationSpacesRequest);
            }

        };
    }

    private static Mono<List<String>> requestSecurityGroups(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceSecurityGroupsPage(cloudFoundryClient, spaceResource))
                .map(extractSecurityGroupName())
                .toList();
    }

    private static Mono<List<String>> requestServiceNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceServicesPage(cloudFoundryClient, spaceResource))
                .map(extractServiceName())
                .toList();
    }

    private static Function<Integer, Mono<ListSpaceApplicationsResponse>> requestSpaceApplicationPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

            @Override
            public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                        .id(Resources.getId(spaceResource))
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listApplications(request);
            }

        };
    }

    private static Function<Integer, Mono<ListSpaceDomainsResponse>> requestSpaceDomainPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Mono<ListSpaceDomainsResponse>>() {

            @Override
            public Mono<ListSpaceDomainsResponse> apply(Integer page) {
                ListSpaceDomainsRequest request = ListSpaceDomainsRequest.builder()
                        .id(Resources.getId(spaceResource))
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listDomains(request);
            }

        };
    }

    private static Function<Integer, Mono<ListSpacesResponse>> requestSpacePage(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return new Function<Integer, Mono<ListSpacesResponse>>() {

            @Override
            public Mono<ListSpacesResponse> apply(Integer page) {
                ListSpacesRequest request = ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().list(request);
            }

        };
    }

    private static Mono<Optional<SpaceQuota>> requestSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, GetSpaceRequest getSpaceRequest, SpaceResource spaceResource) {
        if (!getSpaceRequest.getSecurityGroupRules()) {
            return Mono.just(Optional.<SpaceQuota>empty());
        }

        GetSpaceQuotaDefinitionRequest request = GetSpaceQuotaDefinitionRequest.builder()
                .id(Resources.getEntity(spaceResource).getSpaceQuotaDefinitionId())
                .build();

        return cloudFoundryClient.spaceQuotaDefinitions().get(request)
                .map(toSpaceQuotaDefinition())
                .map(Optionals.<SpaceQuota>toOptional());
    }

    private static Function<Tuple2<GetSpaceRequest, String>, Mono<Tuple2<SpaceResource, GetSpaceRequest>>> requestSpaceResourceWithContext(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<GetSpaceRequest, String>, Mono<Tuple2<SpaceResource, GetSpaceRequest>>>() {

            @Override
            public Mono<Tuple2<SpaceResource, GetSpaceRequest>> apply(Tuple2<GetSpaceRequest, String> tuple) {
                GetSpaceRequest request = tuple.t1;
                String organizationId = tuple.t2;

                return Paginated
                        .requestResources(requestOrganizationSpacePage(cloudFoundryClient, organizationId, request))
                        .single()
                        .and(Mono.just(request));
            }

        };
    }

    private static Function<String, Stream<SpaceResource>> requestSpaceResources(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Stream<SpaceResource>>() {

            @Override
            public Stream<SpaceResource> apply(String organizationId) {
                return Paginated.requestResources(requestSpacePage(cloudFoundryClient, organizationId));
            }

        };
    }

    private static Function<Integer, Mono<ListSpaceSecurityGroupsResponse>> requestSpaceSecurityGroupsPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Mono<ListSpaceSecurityGroupsResponse>>() {

            @Override
            public Mono<ListSpaceSecurityGroupsResponse> apply(Integer page) {
                ListSpaceSecurityGroupsRequest request = ListSpaceSecurityGroupsRequest.builder()
                        .id(Resources.getId(spaceResource))
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listSecurityGroups(request);
            }

        };
    }

    private static Function<Integer, Mono<ListSpaceServicesResponse>> requestSpaceServicesPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Mono<ListSpaceServicesResponse>>() {

            @Override
            public Mono<ListSpaceServicesResponse> apply(Integer page) {
                ListSpaceServicesRequest request = ListSpaceServicesRequest.builder()
                        .id(Resources.getId(spaceResource))
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listServices(request);
            }

        };
    }

    private static Function<Tuple6<List<String>, List<String>, String, List<String>, List<String>, Optional<SpaceQuota>>, SpaceDetail> toSpaceDetail(final SpaceResource spaceResource) {
        return new Function<Tuple6<List<String>, List<String>, String, List<String>, List<String>, Optional<SpaceQuota>>, SpaceDetail>() {

            @Override
            public SpaceDetail apply(Tuple6<List<String>, List<String>, String, List<String>, List<String>, Optional<SpaceQuota>> tuple) {
                List<String> applications = tuple.t1;
                List<String> domains = tuple.t2;
                String organization = tuple.t3;
                List<String> securityGroups = tuple.t4;
                List<String> services = tuple.t5;
                Optional<SpaceQuota> spaceQuota = tuple.t6;

                return SpaceDetail.builder()
                        .applications(applications)
                        .domains(domains)
                        .id(Resources.getId(spaceResource))
                        .name(Resources.getEntity(spaceResource).getName())
                        .organization(organization)
                        .securityGroups(securityGroups)
                        .services(services)
                        .spaceQuota(spaceQuota)
                        .build();
            }

        };
    }

    private static Function<GetSpaceQuotaDefinitionResponse, SpaceQuota> toSpaceQuotaDefinition() {
        return new Function<GetSpaceQuotaDefinitionResponse, SpaceQuota>() {

            @Override
            public SpaceQuota apply(GetSpaceQuotaDefinitionResponse getSpaceQuotaDefinitionResponse) {
                SpaceQuotaDefinitionEntity entity = Resources.getEntity(getSpaceQuotaDefinitionResponse);
                return SpaceQuota.builder()
                        .id(Resources.getId(getSpaceQuotaDefinitionResponse))
                        .instanceMemoryLimit(entity.getInstanceMemoryLimit())
                        .name(entity.getName())
                        .organizationId(entity.getOrganizationId())
                        .paidServicePlans(entity.getNonBasicServicesAllowed())
                        .totalMemoryLimit(entity.getMemoryLimit())
                        .totalRoutes(entity.getTotalRoutes())
                        .totalServiceInstances(entity.getTotalServices())
                        .build();
            }

        };
    }

    private static Function<SpaceResource, SpaceSummary> toSpaceSummary() {
        return new Function<SpaceResource, SpaceSummary>() {

            @Override
            public SpaceSummary apply(SpaceResource resource) {
                return SpaceSummary.builder()
                        .id(Resources.getId(resource))
                        .name(Resources.getEntity(resource).getName())
                        .build();
            }

        };
    }

}
