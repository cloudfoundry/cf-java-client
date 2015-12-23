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
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.operations.ListRoutesRequest.Level;
import org.cloudfoundry.operations.v2.Paginated;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
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
    public Publisher<Route> list(ListRoutesRequest listRoutesRequest) {
        return Validators.stream(listRoutesRequest)
                .flatMap(requestRouteResources(this.cloudFoundryClient, this.organizationId, this.spaceId))
                .flatMap(requestAuxiliaryContent(this.cloudFoundryClient));
    }

    private static Function<ApplicationResource, String> extractApplicationName() {
        return new Function<ApplicationResource, String>() {

            @Override
            public String apply(ApplicationResource applicationResource) {
                return applicationResource.getEntity().getName();
            }

        };
    }

    private static Function<GetDomainResponse, String> extractDomainName() {
        return new Function<GetDomainResponse, String>() {

            @Override
            public String apply(GetDomainResponse getDomainResponse) {
                return getDomainResponse.getEntity().getName();
            }

        };
    }

    private static Function<GetSpaceResponse, String> extractSpaceName() {
        return new Function<GetSpaceResponse, String>() {

            @Override
            public String apply(GetSpaceResponse response) {
                return response.getEntity().getName();
            }

        };
    }

    private static Stream<String> getDomainName(CloudFoundryClient cloudFoundryClient, RouteResource routeResource) {
        GetDomainRequest request = GetDomainRequest.builder()
                .id(routeResource.getEntity().getDomainId())
                .build();

        return Streams.wrap(cloudFoundryClient.domains().get(request))
                .map(extractDomainName());
    }

    private static Stream<String> getSpaceName(CloudFoundryClient cloudFoundryClient, RouteResource routeResource) {
        GetSpaceRequest request = GetSpaceRequest.builder()
                .id(routeResource.getEntity().getSpaceId())
                .build();

        return Streams.wrap(cloudFoundryClient.spaces().get(request))
                .map(extractSpaceName());
    }

    private static Stream<List<String>> requestApplicationNames(CloudFoundryClient cloudFoundryClient, RouteResource routeResource) {
        return Paginated.requestResources(requestApplicationPage(cloudFoundryClient, routeResource))
                .map(extractApplicationName())
                .toList()
                .stream();
    }

    private static Function<Integer, Publisher<ListRouteApplicationsResponse>> requestApplicationPage(final CloudFoundryClient cloudFoundryClient, final RouteResource routeResource) {
        return new Function<Integer, Publisher<ListRouteApplicationsResponse>>() {

            @Override
            public Publisher<ListRouteApplicationsResponse> apply(Integer page) {
                ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                        .id(routeResource.getMetadata().getId())
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
                return Streams.zip(requestApplicationNames(cloudFoundryClient, routeResource), getDomainName(cloudFoundryClient, routeResource), getSpaceName(cloudFoundryClient, routeResource),
                        toRoute(routeResource));
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

    private static Function<Tuple3<List<String>, String, String>, Route> toRoute(final RouteResource routeResource) {
        return new Function<Tuple3<List<String>, String, String>, Route>() {

            @Override
            public Route apply(Tuple3<List<String>, String, String> tuple) {
                return Route.builder()
                        .applications(tuple.getT1())
                        .domain(tuple.getT2())
                        .host(routeResource.getEntity().getHost())
                        .routeId(routeResource.getMetadata().getId())
                        .space(tuple.getT3())
                        .build();
            }
        };
    }

}
