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

package org.cloudfoundry.operations.routes;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.routes.ListRoutesRequest.Level;
import org.cloudfoundry.utils.ExceptionUtils;
import org.cloudfoundry.utils.JobUtils;
import org.cloudfoundry.utils.OperationUtils;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.cloudfoundry.utils.tuple.Function2;
import org.cloudfoundry.utils.tuple.Function3;
import org.cloudfoundry.utils.tuple.Function4;
import org.cloudfoundry.utils.tuple.Predicate2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;
import reactor.fn.tuple.Tuple4;
import reactor.rx.Stream;

import java.util.List;

import static org.cloudfoundry.utils.tuple.TupleUtils.function;
import static org.cloudfoundry.utils.tuple.TupleUtils.predicate;

public final class DefaultRoutes implements Routes {

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
        return ValidationUtils
            .validate(request)
            .and(this.organizationId)
            .then(function(new Function2<CheckRouteRequest, String, Mono<Tuple2<String, CheckRouteRequest>>>() {

                @Override
                public Mono<Tuple2<String, CheckRouteRequest>> apply(CheckRouteRequest request, String organizationId) {
                    return getOptionalDomainId(DefaultRoutes.this.cloudFoundryClient, organizationId, request.getDomain())
                        .and(Mono.just(request));
                }

            }))
            .then(function(new Function2<String, CheckRouteRequest, Mono<Boolean>>() {

                @Override
                public Mono<Boolean> apply(String domainId, CheckRouteRequest checkRouteRequest) {
                    return requestRouteExists(DefaultRoutes.this.cloudFoundryClient, domainId, checkRouteRequest.getHost(), checkRouteRequest.getPath());
                }

            }))
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Void> create(CreateRouteRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.organizationId)
            .then(function(new Function2<CreateRouteRequest, String, Mono<Tuple3<String, String, CreateRouteRequest>>>() {

                @Override
                public Mono<Tuple3<String, String, CreateRouteRequest>> apply(CreateRouteRequest request, String organizationId) {
                    return Mono
                        .when(
                            getSpaceId(DefaultRoutes.this.cloudFoundryClient, organizationId, request.getSpace()),
                            getDomainId(DefaultRoutes.this.cloudFoundryClient, organizationId, request.getDomain()),
                            Mono.just(request)
                        );
                }

            }))
            .then(function(new Function3<String, String, CreateRouteRequest, Mono<CreateRouteResponse>>() {

                @Override
                public Mono<CreateRouteResponse> apply(String spaceId, String domainId, CreateRouteRequest request) {
                    return requestCreateRoute(DefaultRoutes.this.cloudFoundryClient, domainId, request.getHost(), request.getPath(), spaceId);
                }

            }))
            .after();
    }

    @Override
    public Mono<Void> delete(DeleteRouteRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.organizationId)
            .then(function(new Function2<DeleteRouteRequest, String, Mono<Tuple2<String, DeleteRouteRequest>>>() {

                @Override
                public Mono<Tuple2<String, DeleteRouteRequest>> apply(DeleteRouteRequest request, String organizationId) {
                    return getDomainId(DefaultRoutes.this.cloudFoundryClient, organizationId, request.getDomain())
                        .and(Mono.just(request));
                }

            }))
            .then(function(new Function2<String, DeleteRouteRequest, Mono<String>>() {

                @Override
                public Mono<String> apply(String domainId, DeleteRouteRequest request) {
                    return getRouteId(DefaultRoutes.this.cloudFoundryClient, request.getHost(), request.getDomain(), domainId, request.getPath());
                }

            }))
            .then(new Function<String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String routeId) {
                    return deleteRoute(DefaultRoutes.this.cloudFoundryClient, routeId);
                }
            });
    }

    @Override
    public Mono<Void> deleteOrphanedRoutes() {
        return this.spaceId
            .flatMap(new Function<String, Stream<RouteResource>>() {

                @Override
                public Stream<RouteResource> apply(String spaceId) {
                    return requestSpaceRoutes(DefaultRoutes.this.cloudFoundryClient, spaceId);
                }

            })
            .map(ResourceUtils.extractId())
            .flatMap(new Function<String, Mono<Tuple2<List<ApplicationResource>, String>>>() {

                @Override
                public Mono<Tuple2<List<ApplicationResource>, String>> apply(String routeId) {
                    return getApplications(DefaultRoutes.this.cloudFoundryClient, routeId)
                        .and(Mono.just(routeId));
                }

            })
            .as(OperationUtils.<Tuple2<List<ApplicationResource>, String>>stream())
            .filter(predicate(new Predicate2<List<ApplicationResource>, String>() {

                @Override
                public boolean test(List<ApplicationResource> applicationResources, String routeId) {
                    return isOrphan(applicationResources);
                }

            }))
            .flatMap(function(new Function2<List<ApplicationResource>, String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(List<ApplicationResource> applicationResources, String routeId) {
                    return deleteRoute(DefaultRoutes.this.cloudFoundryClient, routeId);
                }

            }))
            .after();
    }

    @Override
    public Publisher<Route> list(ListRoutesRequest request) {
        return ValidationUtils
            .validate(request)
            .flatMap(new Function<ListRoutesRequest, Stream<RouteResource>>() {

                @Override
                public Stream<RouteResource> apply(ListRoutesRequest request) {
                    return getRoutes(DefaultRoutes.this.cloudFoundryClient, DefaultRoutes.this.organizationId, DefaultRoutes.this.spaceId, request.getLevel());
                }

            })
            .flatMap(new Function<RouteResource, Mono<Tuple4<List<String>, String, RouteResource, String>>>() {

                @Override
                public Mono<Tuple4<List<String>, String, RouteResource, String>> apply(RouteResource resource) {
                    return getAuxiliaryContent(DefaultRoutes.this.cloudFoundryClient, resource);
                }

            })
            .map(function(new Function4<List<String>, String, RouteResource, String, Route>() {

                @Override
                public Route apply(List<String> applications, String domain, RouteResource resource, String space) {
                    return toRoute(applications, domain, resource, space);
                }

            }));
    }

    @Override
    public Mono<Void> map(MapRouteRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId, this.organizationId)
            .then(function(new Function3<MapRouteRequest, String, String, Mono<Tuple2<String, String>>>() {

                @Override
                public Mono<Tuple2<String, String>> apply(MapRouteRequest request, String spaceId, String organizationId) {
                    return Mono
                        .when(
                            getOrCreateRoute(DefaultRoutes.this.cloudFoundryClient, organizationId, spaceId, request.getDomain(), request.getHost(), request.getPath()),
                            getApplicationId(DefaultRoutes.this.cloudFoundryClient, request.getApplicationName(), spaceId)
                        );
                }

            }))
            .then(function(new Function2<String, String, Mono<AssociateApplicationRouteResponse>>() {

                @Override
                public Mono<AssociateApplicationRouteResponse> apply(String routeId, String applicationId) {
                    return requestAssociateRoute(DefaultRoutes.this.cloudFoundryClient, applicationId, routeId);
                }

            }))
            .after();
    }

    @Override
    public Mono<Void> unmap(UnmapRouteRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId, this.spaceId)
            .then(function(new Function3<UnmapRouteRequest, String, String, Mono<Tuple2<String, String>>>() {

                @Override
                public Mono<Tuple2<String, String>> apply(final UnmapRouteRequest unmapRouteRequest, final String orgId, final String spaceId) {
                    return Mono
                        .when(
                            getApplicationId(DefaultRoutes.this.cloudFoundryClient, unmapRouteRequest.getApplicationName(), spaceId),
                            getDomainId(DefaultRoutes.this.cloudFoundryClient, orgId, unmapRouteRequest.getDomain())
                                .then(new Function<String, Mono<String>>() {

                                    @Override
                                    public Mono<String> apply(String domainId) {
                                        return getRouteId(DefaultRoutes.this.cloudFoundryClient, unmapRouteRequest.getHost(), unmapRouteRequest.getDomain(), domainId, unmapRouteRequest.getPath());
                                    }

                                })
                        );
                }

            }))
            .then(function(new Function2<String, String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String applicationId, String routeId) {
                    return requestRemoveApplication(DefaultRoutes.this.cloudFoundryClient, applicationId, routeId);
                }

            }));
    }

    private static Mono<Void> deleteRoute(final CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestDeleteRoute(cloudFoundryClient, routeId)
            .map(ResourceUtils.extractId())
            .then(new Function<String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String jobId) {
                    return JobUtils.waitForCompletion(cloudFoundryClient, jobId);
                }

            });
    }

    private static Mono<ApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return requestApplications(cloudFoundryClient, application, spaceId)
            .single()
            .otherwise(ExceptionUtils.<ApplicationResource>convert("Application %s does not exist", application));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .map(ResourceUtils.extractId());
    }

    private static Mono<List<String>> getApplicationNames(final CloudFoundryClient cloudFoundryClient, final RouteResource routeResource) {
        return requestApplications(cloudFoundryClient, ResourceUtils.getId(routeResource))
            .map(new Function<ApplicationResource, String>() {

                @Override
                public String apply(ApplicationResource resource) {
                    return ResourceUtils.getEntity(resource).getName();
                }

            })
            .toList();
    }

    private static Mono<List<ApplicationResource>> getApplications(CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestApplications(cloudFoundryClient, routeId)
            .toList();
    }

    private static Mono<Tuple4<List<String>, String, RouteResource, String>> getAuxiliaryContent(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        return Mono
            .when(
                getApplicationNames(cloudFoundryClient, resource),
                getDomainName(cloudFoundryClient, resource),
                Mono.just(resource),
                getSpaceName(cloudFoundryClient, ResourceUtils.getEntity(resource).getSpaceId())
            );
    }

    private static Mono<String> getDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomains(cloudFoundryClient, organizationId, domain)
            .single()
            .otherwise(ExceptionUtils.<DomainResource>convert("Domain %s does not exist", domain))
            .map(ResourceUtils.extractId());
    }

    private static Mono<String> getDomainName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        return requestDomain(cloudFoundryClient, ResourceUtils.getEntity(resource).getDomainId())
            .map(new Function<GetDomainResponse, String>() {

                @Override
                public String apply(GetDomainResponse response) {
                    return ResourceUtils.getEntity(response).getName();
                }

            });
    }

    private static Stream<DomainResource> getDomains(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return requestPrivateDomains(cloudFoundryClient, organizationId, domain)
            .cast(DomainResource.class)
            .switchIfEmpty(requestSharedDomains(cloudFoundryClient, domain)
                .cast(DomainResource.class));
    }

    private static Mono<String> getOptionalDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomains(cloudFoundryClient, organizationId, domain)
            .singleOrEmpty()
            .map(ResourceUtils.extractId());
    }

    private static Mono<String> getOrCreateRoute(final CloudFoundryClient cloudFoundryClient, String organizationId, final String spaceId, String domain, final String host, final String path) {
        return getDomainId(cloudFoundryClient, organizationId, domain)
            .then(new Function<String, Mono<Resource<RouteEntity>>>() {

                @Override
                public Mono<Resource<RouteEntity>> apply(String domainId) {
                    return requestRoutes(cloudFoundryClient, domainId, host, path)
                        .singleOrEmpty()
                        .map(OperationUtils.<RouteResource, Resource<RouteEntity>>cast())
                        .otherwiseIfEmpty(requestCreateRoute(cloudFoundryClient, domainId, host, path, spaceId));
                }

            })
            .map(ResourceUtils.extractId());
    }

    private static Mono<RouteResource> getRoute(CloudFoundryClient cloudFoundryClient, String host, String domain, String domainId, String path) {
        return requestRoutes(cloudFoundryClient, domainId, host, path)
            .single()
            .otherwise(ExceptionUtils.<RouteResource>convert("Route %s.%s does not exist", host, domain));
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String host, String domain, String domainId, String path) {
        return getRoute(cloudFoundryClient, host, domain, domainId, path)
            .map(ResourceUtils.extractId());
    }

    private static Stream<RouteResource> getRoutes(final CloudFoundryClient cloudFoundryClient, final Mono<String> organizationId, final Mono<String> spaceId, Level level) {
        return Mono
            .just(level)
            .where(new Predicate<Level>() {

                @Override
                public boolean test(Level level) {
                    return Level.ORGANIZATION == level;
                }

            })
            .as(OperationUtils.<Level>stream())
            .flatMap(new Function<Level, Stream<RouteResource>>() {

                @Override
                public Stream<RouteResource> apply(Level level) {
                    return organizationId
                        .flatMap(new Function<String, Stream<RouteResource>>() {

                            @Override
                            public Stream<RouteResource> apply(String organizationId) {
                                return requestOrganizationsRoutes(cloudFoundryClient, organizationId);
                            }

                        })
                        .as(OperationUtils.<RouteResource>stream());
                }

            })
            .switchIfEmpty(spaceId
                .flatMap(new Function<String, Stream<RouteResource>>() {

                    @Override
                    public Stream<RouteResource> apply(String spaceId) {
                        return requestSpaceRoutes(cloudFoundryClient, spaceId);
                    }

                }));
    }

    private static Mono<SpaceResource> getSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .otherwise(ExceptionUtils.<SpaceResource>convert("Space %s does not exist", space));
    }

    private static Mono<String> getSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getSpace(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils.extractId());
    }

    private static Mono<String> getSpaceName(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestSpace(cloudFoundryClient, spaceId)
            .map(new Function<GetSpaceResponse, String>() {

                @Override
                public String apply(GetSpaceResponse response) {
                    return ResourceUtils.getEntity(response).getName();
                }

            });
    }

    private static boolean isOrphan(List<ApplicationResource> applications) {
        return applications.isEmpty();
    }

    private static Stream<ApplicationResource> requestApplications(final CloudFoundryClient cloudFoundryClient, final String routeId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListRouteApplicationsResponse>>() {

                @Override
                public Mono<ListRouteApplicationsResponse> apply(Integer page) {
                    return cloudFoundryClient.routes()
                        .listApplications(ListRouteApplicationsRequest.builder()
                            .routeId(routeId)
                            .page(page)
                            .build());
                }

            });
    }

    private static Stream<ApplicationResource> requestApplications(final CloudFoundryClient cloudFoundryClient, final String application, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

                @Override
                public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listApplications(ListSpaceApplicationsRequest.builder()
                            .name(application)
                            .page(page)
                            .spaceId(spaceId)
                            .build());
                }

            });
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path, String spaceId) {
        return cloudFoundryClient.routes()
            .create(org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(path)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<DeleteRouteResponse> requestDeleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        return cloudFoundryClient.routes()
            .delete(org.cloudfoundry.client.v2.routes.DeleteRouteRequest.builder()
                .async(true)
                .routeId(routeId)
                .build());
    }

    private static Mono<GetDomainResponse> requestDomain(CloudFoundryClient cloudFoundryClient, String domainId) {
        return cloudFoundryClient.domains()
            .get(GetDomainRequest.builder()
                .domainId(domainId)
                .build());
    }

    private static Stream<RouteResource> requestOrganizationsRoutes(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListRoutesResponse>>() {

                @Override
                public Mono<ListRoutesResponse> apply(Integer page) {
                    return cloudFoundryClient.routes()
                        .list(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                            .organizationId(organizationId)
                            .page(page)
                            .build());
                }

            });
    }

    private static Stream<PrivateDomainResource> requestPrivateDomains(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String domain) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationPrivateDomainsResponse>>() {

                @Override
                public Mono<ListOrganizationPrivateDomainsResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                            .organizationId(organizationId)
                            .name(domain)
                            .page(page)
                            .build());
                }

            });
    }

    private static Mono<Void> requestRemoveApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.routes()
            .removeApplication(RemoveRouteApplicationRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<Boolean> requestRouteExists(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        return cloudFoundryClient.routes()
            .exists(RouteExistsRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(path)
                .build());
    }

    private static Stream<RouteResource> requestRoutes(final CloudFoundryClient cloudFoundryClient, final String domainId, final String host, final String path) {
        return PaginationUtils.
            requestResources(new Function<Integer, Mono<ListRoutesResponse>>() {

                @Override
                public Mono<ListRoutesResponse> apply(Integer page) {
                    return cloudFoundryClient.routes()
                        .list(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                            .domainId(domainId)
                            .host(host)
                            .page(page)
                            .path(path)
                            .build());
                }

            });
    }

    private static Stream<SharedDomainResource> requestSharedDomains(final CloudFoundryClient cloudFoundryClient, final String domain) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSharedDomainsResponse>>() {

                @Override
                public Mono<ListSharedDomainsResponse> apply(Integer page) {
                    return cloudFoundryClient.sharedDomains()
                        .list(ListSharedDomainsRequest.builder()
                            .name(domain)
                            .page(page)
                            .build());
                }

            });
    }

    private static Mono<GetSpaceResponse> requestSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.spaces()
            .get(GetSpaceRequest.builder()
                .spaceId(spaceId)
                .build());
    }

    private static Stream<RouteResource> requestSpaceRoutes(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceRoutesResponse>>() {

                @Override
                public Mono<ListSpaceRoutesResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listRoutes(ListSpaceRoutesRequest.builder()
                            .spaceId(spaceId)
                            .page(page)
                            .build());
                }

            });
    }

    private static Stream<SpaceResource> requestSpaces(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String space) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

                @Override
                public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                    return cloudFoundryClient.organizations()
                        .listSpaces(ListOrganizationSpacesRequest.builder()
                            .organizationId(organizationId)
                            .name(space)
                            .page(page)
                            .build());
                }

            });
    }

    private static Route toRoute(List<String> applications, String domain, RouteResource resource, String space) {
        RouteEntity entity = ResourceUtils.getEntity(resource);

        return Route.builder()
            .applications(applications)
            .domain(domain)
            .host(entity.getHost())
            .path(entity.getPath())
            .routeId(ResourceUtils.getId(resource))
            .space(space)
            .build();
    }

}
