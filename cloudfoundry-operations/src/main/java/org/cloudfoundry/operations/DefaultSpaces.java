/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.operations;

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
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.Publishers;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple6;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.List;

final class DefaultSpaces implements Spaces {

    private final CloudFoundryClient cloudFoundryClient;

    private final Stream<String> organizationId;

    DefaultSpaces(CloudFoundryClient cloudFoundryClient, Stream<String> organizationId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
    }

    @Override
    public Publisher<SpaceDetail> get(GetSpaceRequest getSpaceRequest) {
        return Validators
                .stream(getSpaceRequest)
                .zipWith(this.organizationId)
                .flatMap(requestSpaceResourcesWithContext(this.cloudFoundryClient))
                .flatMap(getAuxiliaryContent(this.cloudFoundryClient));
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

    private static Function<Tuple2<SpaceResource, GetSpaceRequest>, Publisher<SpaceDetail>> getAuxiliaryContent(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<SpaceResource, GetSpaceRequest>, Publisher<SpaceDetail>>() {

            @Override
            public Publisher<SpaceDetail> apply(Tuple2<SpaceResource, GetSpaceRequest> tuple) {
                SpaceResource spaceResource = tuple.t1;
                GetSpaceRequest request = tuple.t2;

                return Streams
                        .zip(requestApplicationNames(cloudFoundryClient, spaceResource), requestDomainNames(cloudFoundryClient, spaceResource),
                                requestOrganizationName(cloudFoundryClient, spaceResource), requestSecurityGroups(cloudFoundryClient, spaceResource),
                                requestServiceNames(cloudFoundryClient, spaceResource), requestSpaceQuotaDefinition(cloudFoundryClient, request, spaceResource), toSpaceDetail(spaceResource));
            }

        };
    }

    private static Stream<List<String>> requestApplicationNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceApplicationPage(cloudFoundryClient, spaceResource))
                .map(extractApplicationName())
                .toList()
                .stream();
    }

    private static Stream<List<String>> requestDomainNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceDomainPage(cloudFoundryClient, spaceResource))
                .map(extractDomainName())
                .toList()
                .stream();
    }

    private static Stream<String> requestOrganizationName(CloudFoundryClient cloudFoundryClient, SpaceResource spaceResource) {
        GetOrganizationRequest request = GetOrganizationRequest.builder()
                .id(Resources.getEntity(spaceResource).getOrganizationId())
                .build();

        return Streams
                .wrap(cloudFoundryClient.organizations().get(request))
                .map(extractOrganizationName());
    }

    private static Function<Integer, Publisher<ListOrganizationSpacesResponse>> requestOrganizationSpacePage(final CloudFoundryClient cloudFoundryClient, final String organizationId,
                                                                                                             final GetSpaceRequest getSpaceRequest) {
        return new Function<Integer, Publisher<ListOrganizationSpacesResponse>>() {

            @Override
            public Publisher<ListOrganizationSpacesResponse> apply(Integer page) {
                ListOrganizationSpacesRequest listOrganizationSpacesRequest = ListOrganizationSpacesRequest.builder()
                        .organizationId(organizationId)
                        .name(getSpaceRequest.getName())
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().listSpaces(listOrganizationSpacesRequest);
            }

        };
    }

    private static Function<Integer, Publisher<ListSpacesResponse>> requestPage(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return new Function<Integer, Publisher<ListSpacesResponse>>() {

            @Override
            public Publisher<ListSpacesResponse> apply(Integer page) {
                ListSpacesRequest request = ListSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().list(request);
            }

        };
    }

    private static Stream<List<String>> requestSecurityGroups(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceSecurityGroupsPage(cloudFoundryClient, spaceResource))
                .map(extractSecurityGroupName())
                .toList()
                .stream();
    }

    private static Stream<List<String>> requestServiceNames(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return Paginated
                .requestResources(requestSpaceServicesPage(cloudFoundryClient, spaceResource))
                .map(extractServiceName())
                .toList()
                .stream();
    }

    private static Function<Integer, Publisher<ListSpaceApplicationsResponse>> requestSpaceApplicationPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Publisher<ListSpaceApplicationsResponse>>() {

            @Override
            public Publisher<ListSpaceApplicationsResponse> apply(Integer page) {
                ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                        .id(Resources.getId(spaceResource))
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listApplications(request);
            }

        };
    }

    private static Function<Integer, Publisher<ListSpaceDomainsResponse>> requestSpaceDomainPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Publisher<ListSpaceDomainsResponse>>() {

            @Override
            public Publisher<ListSpaceDomainsResponse> apply(Integer page) {
                ListSpaceDomainsRequest request = ListSpaceDomainsRequest.builder()
                        .id(Resources.getId(spaceResource))
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listDomains(request);
            }

        };
    }

    private static Stream<Optional<SpaceQuota>> requestSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, GetSpaceRequest getSpaceRequest, SpaceResource spaceResource) {
        if (!getSpaceRequest.getSecurityGroupRules()) {
            return Streams.just(Optional.<SpaceQuota>empty());
        }

        GetSpaceQuotaDefinitionRequest request = GetSpaceQuotaDefinitionRequest.builder()
                .id(Resources.getEntity(spaceResource).getSpaceQuotaDefinitionId())
                .build();

        return Streams
                .wrap(cloudFoundryClient.spaceQuotaDefinitions().get(request))
                .map(toSpaceQuotaDefinition())
                .map(Optionals.<SpaceQuota>toOptional());
    }

    private static Function<String, Publisher<SpaceResource>> requestSpaceResources(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Publisher<SpaceResource>>() {

            @Override
            public Publisher<SpaceResource> apply(String organizationId) {
                return Paginated.requestResources(requestPage(cloudFoundryClient, organizationId));
            }

        };
    }

    private static Function<Tuple2<GetSpaceRequest, String>, Publisher<Tuple2<SpaceResource, GetSpaceRequest>>> requestSpaceResourcesWithContext(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<GetSpaceRequest, String>, Publisher<Tuple2<SpaceResource, GetSpaceRequest>>>() {

            @Override
            public Publisher<Tuple2<SpaceResource, GetSpaceRequest>> apply(Tuple2<GetSpaceRequest, String> tuple) {
                GetSpaceRequest request = tuple.t1;
                String organizationId = tuple.t2;

                return Paginated
                        .requestResources(requestOrganizationSpacePage(cloudFoundryClient, organizationId, request))
                        .zipWith(Publishers.just(request));
            }

        };
    }

    private static Function<Integer, Publisher<ListSpaceSecurityGroupsResponse>> requestSpaceSecurityGroupsPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Publisher<ListSpaceSecurityGroupsResponse>>() {

            @Override
            public Publisher<ListSpaceSecurityGroupsResponse> apply(Integer page) {
                ListSpaceSecurityGroupsRequest request = ListSpaceSecurityGroupsRequest.builder()
                        .id(Resources.getId(spaceResource))
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listSecurityGroups(request);
            }

        };
    }

    private static Function<Integer, Publisher<ListSpaceServicesResponse>> requestSpaceServicesPage(final CloudFoundryClient cloudFoundryClient, final SpaceResource spaceResource) {
        return new Function<Integer, Publisher<ListSpaceServicesResponse>>() {

            @Override
            public Publisher<ListSpaceServicesResponse> apply(Integer page) {
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
