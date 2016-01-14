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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
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
import reactor.Flux;
import reactor.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;
import reactor.rx.Stream;

import java.util.List;

final class DefaultRoutes implements Routes {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> organizationId;

    private final Mono<String> spaceId;

    public DefaultRoutes(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Boolean> check(CheckRouteRequest request) {
        return Validators
                .validate(request)
                .and(this.organizationId)
                .then(requestDomainIdCheckRoute(this.cloudFoundryClient))
                .then(requestCheckRoute(this.cloudFoundryClient))
                .defaultIfEmpty(false);
    }

    public Mono<Void> create(CreateRouteRequest request) {
        return Validators
                .validate(request)
                .and(this.organizationId)
                .then(requestOrganizationSpaceId(this.cloudFoundryClient))
                .then(requestDomainIdCreateRoute(this.cloudFoundryClient))
                .then(requestCreateRoute(this.cloudFoundryClient));
    }

    @Override
    public Publisher<Route> list(ListRoutesRequest request) {
        return Validators
                .validate(request)
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

    private static Mono<String> getDomainName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        GetDomainRequest request = GetDomainRequest.builder()
                .id(Resources.getEntity(resource).getDomainId())
                .build();

        return cloudFoundryClient.domains().get(request)
                .map(extractDomainName());
    }

    private static Mono<String> getSpaceName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        GetSpaceRequest request = GetSpaceRequest.builder()
                .id(Resources.getEntity(resource).getSpaceId())
                .build();

        return cloudFoundryClient.spaces().get(request)
                .map(extractSpaceName());
    }

    private static Mono<List<String>> requestApplicationNames(CloudFoundryClient cloudFoundryClient, RouteResource routeResource) {
        return Paginated
                .requestResources(requestApplicationPage(cloudFoundryClient, routeResource))
                .map(extractApplicationName())
                .toList();
    }

    private static Function<Integer, Mono<ListRouteApplicationsResponse>> requestApplicationPage(final CloudFoundryClient cloudFoundryClient, final RouteResource resource) {
        return new Function<Integer, Mono<ListRouteApplicationsResponse>>() {

            @Override
            public Mono<ListRouteApplicationsResponse> apply(Integer page) {
                ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                        .id(Resources.getId(resource))
                        .page(page)
                        .build();

                return cloudFoundryClient.routes().listApplications(request);
            }

        };
    }

    private static Function<RouteResource, Mono<Route>> requestAuxiliaryContent(final CloudFoundryClient cloudFoundryClient) {
        return new Function<RouteResource, Mono<Route>>() {

            @Override
            public Mono<Route> apply(RouteResource routeResource) {
                return Mono
                        .when(requestApplicationNames(cloudFoundryClient, routeResource), getDomainName(cloudFoundryClient, routeResource), getSpaceName(cloudFoundryClient, routeResource))
                        .map(toRoute(routeResource));
            }

        };
    }

    private static Function<Tuple2<String, CheckRouteRequest>, Mono<Boolean>> requestCheckRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<String, CheckRouteRequest>, Mono<Boolean>>() {

            @Override
            public Mono<Boolean> apply(Tuple2<String, CheckRouteRequest> tuple) {
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

    private static Function<Tuple3<String, String, CreateRouteRequest>, Mono<Void>> requestCreateRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple3<String, String, CreateRouteRequest>, Mono<Void>>() {

            @Override
            public Mono<Void> apply(Tuple3<String, String, CreateRouteRequest> tuple) {
                String domainId = tuple.t1;
                String spaceId = tuple.t2;
                CreateRouteRequest createRouteRequest = tuple.t3;

                org.cloudfoundry.client.v2.routes.CreateRouteRequest request = org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                        .domainId(domainId)
                        .host(createRouteRequest.getHost())
                        .spaceId(spaceId)
                        .build();

                return cloudFoundryClient.routes().create(request)
                        .after();
            }

        };
    }

    private static Function<Tuple2<CheckRouteRequest, String>, Mono<Tuple2<String, CheckRouteRequest>>> requestDomainIdCheckRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<CheckRouteRequest, String>, Mono<Tuple2<String, CheckRouteRequest>>>() {

            @Override
            public Mono<Tuple2<String, CheckRouteRequest>> apply(Tuple2<CheckRouteRequest, String> tuple) {
                CheckRouteRequest request = tuple.t1;
                String organizationId = tuple.t2;

                return requestPrivateDomain(cloudFoundryClient, request.getDomain(), organizationId)
                        .otherwiseIfEmpty(requestSharedDomain(cloudFoundryClient, request.getDomain()))
                        .map(Resources.extractId())
                        .and(Mono.just(request));
            }

        };
    }

    private static Function<Tuple3<String, String, CreateRouteRequest>, Mono<Tuple3<String, String, CreateRouteRequest>>> requestDomainIdCreateRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple3<String, String, CreateRouteRequest>, Mono<Tuple3<String, String, CreateRouteRequest>>>() {

            @Override
            public Mono<Tuple3<String, String, CreateRouteRequest>> apply(Tuple3<String, String, CreateRouteRequest> tuple) {
                String spaceId = tuple.t1;
                String organizationId = tuple.t2;
                CreateRouteRequest request = tuple.t3;

                Mono<String> domainId = requestPrivateDomain(cloudFoundryClient, request.getDomain(), organizationId)
                        .otherwiseIfEmpty(requestSharedDomain(cloudFoundryClient, request.getDomain()))
                        .map(Resources.extractId())
                        .otherwiseIfEmpty(Mono.<String>error(new IllegalArgumentException(String.format("Domain %s does not exist", request.getDomain()))));

                return Mono.when(domainId, Mono.just(spaceId), Mono.just(request));
            }

        };
    }

    private static Function<Integer, Mono<ListRoutesResponse>> requestOrganizationRoutePage(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return new Function<Integer, Mono<ListRoutesResponse>>() {

            @Override
            public Mono<ListRoutesResponse> apply(Integer page) {
                org.cloudfoundry.client.v2.routes.ListRoutesRequest request = org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build();

                return cloudFoundryClient.routes().list(request);
            }

        };
    }

    private static Function<String, Stream<RouteResource>> requestOrganizationRoutes(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Stream<RouteResource>>() {

            @Override
            public Stream<RouteResource> apply(String organizationId) {
                return Paginated.requestResources(requestOrganizationRoutePage(cloudFoundryClient, organizationId));
            }

        };
    }

    private static Function<Tuple2<CreateRouteRequest, String>, Mono<Tuple3<String, String, CreateRouteRequest>>> requestOrganizationSpaceId(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<CreateRouteRequest, String>, Mono<Tuple3<String, String, CreateRouteRequest>>>() {

            @Override
            public Mono<Tuple3<String, String, CreateRouteRequest>> apply(Tuple2<CreateRouteRequest, String> tuple) {
                CreateRouteRequest request = tuple.t1;
                String organizationId = tuple.t2;

                Mono<String> spaceId = Paginated
                        .requestResources(requestOrganizationSpaceIdPage(cloudFoundryClient, organizationId, request.getSpace()))
                        .map(Resources.extractId())
                        .single()
                        .otherwise(Exceptions.<String>convert(String.format("Space %s does not exist", request.getSpace())));

                return Mono.when(spaceId, Mono.just(organizationId), Mono.just(request));
            }

        };
    }

    private static Function<Integer, Mono<ListOrganizationSpacesResponse>> requestOrganizationSpaceIdPage(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String
            spaceName) {
        return new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

            @Override
            public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                ListOrganizationSpacesRequest request = ListOrganizationSpacesRequest.builder()
                        .id(organizationId)
                        .name(spaceName)
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().listSpaces(request);
            }

        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends Resource<?>> Mono<T> requestPrivateDomain(final CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return (Mono<T>) Paginated.requestResources(requestPrivateDomainsPage(cloudFoundryClient, organizationId, domain))
                .singleOrEmpty();
    }

    private static Function<Integer, Mono<ListOrganizationPrivateDomainsResponse>> requestPrivateDomainsPage(final CloudFoundryClient cloudFoundryClient, final String organizationId, final
    String domain) {
        return new Function<Integer, Mono<ListOrganizationPrivateDomainsResponse>>() {

            @Override
            public Mono<ListOrganizationPrivateDomainsResponse> apply(Integer page) {
                ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                        .id(organizationId)
                        .name(domain)
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().listPrivateDomains(request);
            }

        };
    }

    private static Function<ListRoutesRequest, Flux<RouteResource>> requestRouteResources(final CloudFoundryClient cloudFoundryClient, final Mono<String> organizationId, final Mono<String> spaceId) {
        return new Function<ListRoutesRequest, Flux<RouteResource>>() {

            @Override
            public Flux<RouteResource> apply(ListRoutesRequest request) {
                if (Level.ORGANIZATION == request.getLevel()) {
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
    private static <T extends Resource<?>> Mono<T> requestSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return (Mono<T>) Paginated.requestResources(requestSharedDomainsPage(cloudFoundryClient, domain))
                .singleOrEmpty();
    }

    private static Function<Integer, Mono<ListSharedDomainsResponse>> requestSharedDomainsPage(final CloudFoundryClient cloudFoundryClient, final String domain) {
        return new Function<Integer, Mono<ListSharedDomainsResponse>>() {

            @Override
            public Mono<ListSharedDomainsResponse> apply(Integer page) {
                ListSharedDomainsRequest request = ListSharedDomainsRequest.builder()
                        .name(domain)
                        .page(page)
                        .build();

                return cloudFoundryClient.sharedDomains().list(request);
            }

        };
    }

    private static Function<Integer, Mono<ListSpaceRoutesResponse>> requestSpaceRoutePage(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return new Function<Integer, Mono<ListSpaceRoutesResponse>>() {

            @Override
            public Mono<ListSpaceRoutesResponse> apply(Integer page) {
                ListSpaceRoutesRequest request = ListSpaceRoutesRequest.builder()
                        .id(spaceId)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listRoutes(request);
            }

        };
    }

    private static Function<String, Stream<RouteResource>> requestSpaceRoutes(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Stream<RouteResource>>() {

            @Override
            public Stream<RouteResource> apply(String spaceId) {
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
