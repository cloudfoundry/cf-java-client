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
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.operations.ListRoutesRequest.Level;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.Publishers;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.List;

final class DefaultRoutes implements Routes {

    private final CloudFoundryClient cloudFoundryClient;

    private final Stream<String> organizationId;

    private final Stream<String> spaceId;

    public DefaultRoutes(CloudFoundryClient cloudFoundryClient, Stream<String> organizationId, Stream<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
        this.spaceId = spaceId;
    }

    @Override
    public Publisher<Boolean> check(CheckRouteRequest request) {
        return Validators
                .stream(request)
                .zipWith(this.organizationId)
                .flatMap(requestDomainId(this.cloudFoundryClient))
                .flatMap(requestCheckRoute(this.cloudFoundryClient))
                .defaultIfEmpty(false)
                .take(1);  // TODO: Remove after switchIfEmpty() propagates onComplete() in a non-empty case
    }

    @Override
    public Publisher<Route> list(ListRoutesRequest request) {
        return Validators
                .stream(request)
                .flatMap(requestRouteResources(this.cloudFoundryClient, this.organizationId, this.spaceId))
                .flatMap(requestAuxiliaryContent(this.cloudFoundryClient));
    }

    private static Function<ApplicationResource, String> extractApplicationName() {
        return new Function<ApplicationResource, String>() {

            @Override
            public String apply(ApplicationResource resource) {
                return Resources.getEntity(resource).getName();
            }

        };
    }

    private static Function<GetDomainResponse, String> extractDomainName() {
        return new Function<GetDomainResponse, String>() {

            @Override
            public String apply(GetDomainResponse response) {
                return Resources.getEntity(response).getName();
            }

        };
    }

    private static Function<GetSpaceResponse, String> extractSpaceName() {
        return new Function<GetSpaceResponse, String>() {

            @Override
            public String apply(GetSpaceResponse response) {
                return Resources.getEntity(response).getName();
            }

        };
    }

    private static Stream<String> getDomainName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        GetDomainRequest request = GetDomainRequest.builder()
                .id(Resources.getEntity(resource).getDomainId())
                .build();

        return Streams
                .wrap(cloudFoundryClient.domains().get(request))
                .map(extractDomainName());
    }

    private static Stream<String> getSpaceName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        GetSpaceRequest request = GetSpaceRequest.builder()
                .id(Resources.getEntity(resource).getSpaceId())
                .build();

        return Streams
                .wrap(cloudFoundryClient.spaces().get(request))
                .map(extractSpaceName());
    }

    private static Stream<List<String>> requestApplicationNames(CloudFoundryClient cloudFoundryClient, RouteResource routeResource) {
        return Paginated
                .requestResources(requestApplicationPage(cloudFoundryClient, routeResource))
                .map(extractApplicationName())
                .toList()
                .stream();
    }

    private static Function<Integer, Publisher<ListRouteApplicationsResponse>> requestApplicationPage(final CloudFoundryClient cloudFoundryClient, final RouteResource resource) {
        return new Function<Integer, Publisher<ListRouteApplicationsResponse>>() {

            @Override
            public Publisher<ListRouteApplicationsResponse> apply(Integer page) {
                ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                        .id(Resources.getId(resource))
                        .page(page)
                        .build();

                return cloudFoundryClient.routes().listApplications(request);
            }

        };
    }

    private static Function<RouteResource, Publisher<Route>> requestAuxiliaryContent(final CloudFoundryClient cloudFoundryClient) {
        return new Function<RouteResource, Publisher<Route>>() {

            @Override
            public Publisher<Route> apply(RouteResource routeResource) {
                return Streams
                        .zip(requestApplicationNames(cloudFoundryClient, routeResource), getDomainName(cloudFoundryClient, routeResource), getSpaceName(cloudFoundryClient, routeResource),
                                toRoute(routeResource));
            }

        };
    }

    private static Function<Tuple2<String, CheckRouteRequest>, Publisher<Boolean>> requestCheckRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<String, CheckRouteRequest>, Publisher<Boolean>>() {

            @Override
            public Publisher<Boolean> apply(Tuple2<String, CheckRouteRequest> tuple) {
                String domainId = tuple.t1;
                CheckRouteRequest checkRouteRequest = tuple.t2;

                RouteExistsRequest request = RouteExistsRequest.builder()
                        .domainId(domainId)
                        .host(checkRouteRequest.getHost())
                        .build();

                return cloudFoundryClient.routes().exists(request);
            }

        };
    }

    private static Function<Tuple2<CheckRouteRequest, String>, Publisher<Tuple2<String, CheckRouteRequest>>> requestDomainId(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<CheckRouteRequest, String>, Publisher<Tuple2<String, CheckRouteRequest>>>() {

            @Override
            public Publisher<Tuple2<String, CheckRouteRequest>> apply(Tuple2<CheckRouteRequest, String> tuple) {
                CheckRouteRequest request = tuple.t1;
                String organizationId = tuple.t2;

                return requestPrivateDomains(cloudFoundryClient, request.getDomain(), organizationId)
                        .switchIfEmpty(requestSharedDomains(cloudFoundryClient, request.getDomain()))
                        .map(Resources.extractId())
                        .zipWith(Publishers.just(request));
            }

        };
    }

    private static Function<Integer, Publisher<ListRoutesResponse>> requestOrganizationRoutePage(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return new Function<Integer, Publisher<ListRoutesResponse>>() {

            @Override
            public Publisher<ListRoutesResponse> apply(Integer page) {
                org.cloudfoundry.client.v2.routes.ListRoutesRequest request = org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build();

                return cloudFoundryClient.routes().list(request);
            }

        };
    }

    private static Function<String, Publisher<RouteResource>> requestOrganizationRoutes(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Publisher<RouteResource>>() {

            @Override
            public Publisher<RouteResource> apply(String organizationId) {
                return Paginated.requestResources(requestOrganizationRoutePage(cloudFoundryClient, organizationId));
            }

        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends Resource<?>> Stream<T> requestPrivateDomains(final CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return (Stream<T>) Paginated.requestResources(requestPrivateDomainsPage(cloudFoundryClient, organizationId, domain));
    }

    private static Function<Integer, Publisher<ListOrganizationPrivateDomainsResponse>> requestPrivateDomainsPage(final CloudFoundryClient cloudFoundryClient, final String organizationId, final
    String domain) {
        return new Function<Integer, Publisher<ListOrganizationPrivateDomainsResponse>>() {

            @Override
            public Publisher<ListOrganizationPrivateDomainsResponse> apply(Integer page) {
                ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                        .id(organizationId)
                        .name(domain)
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().listPrivateDomains(request);
            }

        };
    }

    private static Function<ListRoutesRequest, Publisher<RouteResource>> requestRouteResources(final CloudFoundryClient cloudFoundryClient, final Stream<String> organizationId,
                                                                                               final Stream<String> spaceId) {
        return new Function<ListRoutesRequest, Publisher<RouteResource>>() {

            @Override
            public Publisher<RouteResource> apply(ListRoutesRequest request) {
                if (Level.Organization == request.getLevel()) {
                    return organizationId
                            .flatMap(requestOrganizationRoutes(cloudFoundryClient));
                } else {
                    return spaceId
                            .flatMap(requestSpaceRoutes(cloudFoundryClient));
                }
            }

        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends Resource<?>> Stream<T> requestSharedDomains(CloudFoundryClient cloudFoundryClient, String domain) {
        return (Stream<T>) Paginated.requestResources(requestSharedDomainsPage(cloudFoundryClient, domain));
    }

    private static Function<Integer, Publisher<ListSharedDomainsResponse>> requestSharedDomainsPage(final CloudFoundryClient cloudFoundryClient, final String domain) {
        return new Function<Integer, Publisher<ListSharedDomainsResponse>>() {

            @Override
            public Publisher<ListSharedDomainsResponse> apply(Integer page) {
                ListSharedDomainsRequest request = ListSharedDomainsRequest.builder()
                        .name(domain)
                        .page(page)
                        .build();

                return cloudFoundryClient.sharedDomains().list(request);
            }

        };
    }

    private static Function<Integer, Publisher<ListSpaceRoutesResponse>> requestSpaceRoutePage(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return new Function<Integer, Publisher<ListSpaceRoutesResponse>>() {

            @Override
            public Publisher<ListSpaceRoutesResponse> apply(Integer page) {
                ListSpaceRoutesRequest request = ListSpaceRoutesRequest.builder()
                        .id(spaceId)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listRoutes(request);
            }

        };
    }

    private static Function<String, Publisher<RouteResource>> requestSpaceRoutes(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Publisher<RouteResource>>() {

            @Override
            public Publisher<RouteResource> apply(String spaceId) {
                return Paginated.requestResources(requestSpaceRoutePage(cloudFoundryClient, spaceId));
            }

        };
    }

    private static Function<Tuple3<List<String>, String, String>, Route> toRoute(final RouteResource resource) {
        return new Function<Tuple3<List<String>, String, String>, Route>() {

            @Override
            public Route apply(Tuple3<List<String>, String, String> tuple) {
                List<String> applications = tuple.t1;
                String domainId = tuple.t2;
                String spaceId = tuple.t3;

                return Route.builder()
                        .applications(applications)
                        .domain(domainId)
                        .host(Resources.getEntity(resource).getHost())
                        .routeId(Resources.getId(resource))
                        .space(spaceId)
                        .build();
            }
        };
    }

}
