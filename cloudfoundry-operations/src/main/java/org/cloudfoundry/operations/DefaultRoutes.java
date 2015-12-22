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
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.operations.v2.PageUtils;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple3;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.List;

final class DefaultRoutes extends AbstractOperations implements Routes {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultRoutes(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId) {
        super(organizationId, spaceId);
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Publisher<Route> list(ListRoutesRequest listRoutesRequest) {
        checkRequestValid(listRoutesRequest);
        return getRouteResources(listRoutesRequest)
                .flatMap(new Function<RouteResource, Publisher<Route>>() {

                    @Override
                    public Publisher<Route> apply(RouteResource routeResource) {
                        final RouteEntity routeEntity = routeResource.getEntity();
                        final Resource.Metadata routeMetadata = routeResource.getMetadata();

                        return Streams.zip(getApplicationNames(routeMetadata), getDomainName(routeEntity),
                                getSpaceName(routeEntity),
                                new Function<Tuple3<List<String>, String, String>, Route>() {

                                    @Override
                                    public Route apply(Tuple3<List<String>, String, String> tuple) {
                                        return Route.builder()
                                                .applications(tuple.getT1())
                                                .domain(tuple.getT2())
                                                .host(routeEntity.getHost())
                                                .routeId(routeMetadata.getId())
                                                .space(tuple.getT3())
                                                .build();
                                    }
                                });
                    }
                });
    }

    private Stream<List<String>> getApplicationNames(final Resource.Metadata metadata) {
        return PageUtils.resourceStream(new Function<Integer, Publisher<ListRouteApplicationsResponse>>() {

            @Override
            public Publisher<ListRouteApplicationsResponse> apply(Integer page) {
                return DefaultRoutes.this.cloudFoundryClient.routes().listApplications(
                        ListRouteApplicationsRequest.builder()
                                .id(metadata.getId())
                                .page(page)
                                .build()
                );
            }
        }).map(new Function<ApplicationResource, String>() {

            @Override
            public String apply(ApplicationResource applicationResource) {
                return applicationResource.getEntity().getName();
            }
        }).toList().stream();
    }

    private Stream<String> getDomainName(RouteEntity routeEntity) {
        GetDomainRequest request = GetDomainRequest.builder()
                .id(routeEntity.getDomainId())
                .build();

        return Streams.wrap(this.cloudFoundryClient.domains().get(request))
                .map(new Function<GetDomainResponse, String>() {

                    @Override
                    public String apply(GetDomainResponse getDomainResponse) {
                        return getDomainResponse.getEntity().getName();
                    }
                });
    }

    private Stream<RouteResource> getRouteResources(ListRoutesRequest listRoutesRequest) {
        return (ListRoutesRequest.Level.Organization == listRoutesRequest.getLevel()) ?
                getTargetedOrganizationRouteResources() : getTargetedSpaceRouteResources();
    }

    private Stream<String> getSpaceName(RouteEntity routeEntity) {
        GetSpaceRequest request = GetSpaceRequest.builder()
                .id(routeEntity.getSpaceId())
                .build();

        return Streams.wrap(this.cloudFoundryClient.spaces().get(request))
                .map(new Function<GetSpaceResponse, String>() {

                    @Override
                    public String apply(GetSpaceResponse getSpaceResponse) {
                        return getSpaceResponse.getEntity().getName();
                    }
                });
    }

    private Stream<RouteResource> getTargetedOrganizationRouteResources() {
        return getTargetedOrganization()
                .flatMap(new Function<String, Publisher<RouteResource>>() {

                    @Override
                    public Publisher<RouteResource> apply(final String organizationId) {
                        return PageUtils.resourceStream(new Function<Integer,
                                Publisher<ListRoutesResponse>>() {

                            @Override
                            public Publisher<ListRoutesResponse> apply(Integer page) {
                                return DefaultRoutes.this.cloudFoundryClient.routes().list(
                                        org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                                                .organizationId(organizationId)
                                                .page(page)
                                                .build());
                            }

                        });
                    }

                });
    }

    private Stream<RouteResource> getTargetedSpaceRouteResources() {
        return getTargetedSpace()
                .flatMap(new Function<String, Publisher<RouteResource>>() {

                    @Override
                    public Publisher<RouteResource> apply(final String targetedSpace) {
                        return PageUtils.resourceStream(new Function<Integer,
                                Publisher<ListSpaceRoutesResponse>>() {

                            @Override
                            public Publisher<ListSpaceRoutesResponse> apply(Integer page) {
                                return DefaultRoutes.this.cloudFoundryClient.spaces().listRoutes(
                                        ListSpaceRoutesRequest.builder()
                                                .id(targetedSpace)
                                                .page(page)
                                                .build());
                            }

                        });
                    }

                });
    }

}
