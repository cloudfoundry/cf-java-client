/*
 * Copyright 2013-2017 the original author or authors.
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
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.AbstractRouteResource;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.OperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class DefaultRoutes implements Routes {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<String> organizationId;

    private final Mono<String> spaceId;

    public DefaultRoutes(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> organizationId, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Boolean> check(CheckRouteRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getOptionalDomainId(cloudFoundryClient, organizationId, request.getDomain())
                )))
            .flatMap(function((cloudFoundryClient, domainId) -> requestRouteExists(cloudFoundryClient, domainId, request.getHost(), request.getPath())))
            .defaultIfEmpty(false)
            .transform(OperationsLogging.log("Check Route Exists"))
            .checkpoint();
    }

    @Override
    public Mono<Integer> create(CreateRouteRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getSpaceId(cloudFoundryClient, organizationId, request.getSpace()),
                    getDomainId(cloudFoundryClient, organizationId, request.getDomain())
                )))
            .flatMap(function((cloudFoundryClient, spaceId, domainId) ->
                requestCreateRoute(cloudFoundryClient, domainId, request.getHost(), request.getPath(), request.getPort(), request.getRandomPort(), spaceId)
                    .map(ResourceUtils::getEntity)
                    .flatMap(routeEntity -> Mono.justOrEmpty(routeEntity.getPort()))
            ))
            .transform(OperationsLogging.log("Create Route"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteRouteRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getDomainId(cloudFoundryClient, organizationId, request.getDomain())
                )))
            .flatMap(function((cloudFoundryClient, domainId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    Mono.just(request.getCompletionTimeout()),
                    getRouteId(cloudFoundryClient, request.getHost(), request.getDomain(), domainId, request.getPath(), request.getPort())
                )))
            .flatMap(function(DefaultRoutes::deleteRoute))
            .transform(OperationsLogging.log("Delete Route"))
            .checkpoint();
    }

    @Override
    public Mono<Void> deleteOrphanedRoutes(DeleteOrphanedRoutesRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMapMany(function((cloudFoundryClient, spaceId) -> requestSpaceRoutes(cloudFoundryClient, spaceId)
                .filter(route -> isRouteOrphan(ResourceUtils.getEntity(route)))
                .map(ResourceUtils::getId)
                .map(routeId -> Tuples.of(cloudFoundryClient, routeId))))
            .flatMap(function((cloudFoundryClient, routeId) -> getApplications(cloudFoundryClient, routeId)
                .map(applicationResources -> Tuples.of(cloudFoundryClient, applicationResources, routeId))))
            .filter(predicate((cloudFoundryClient, applicationResources, routeId) -> isApplicationOrphan(applicationResources)))
            .flatMap(function((cloudFoundryClient, applicationResources, routeId) -> deleteRoute(cloudFoundryClient, request.getCompletionTimeout(), routeId)))
            .then()
            .transform(OperationsLogging.log("Delete Orphaned Routes"))
            .checkpoint();
    }

    @Override
    public Flux<Route> list(ListRoutesRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId)
            .flatMap(function((cloudFoundryClient, organizationId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getAllDomains(cloudFoundryClient, organizationId),
                    getAllSpaces(cloudFoundryClient, organizationId)
                )))
            .flatMapMany(function((cloudFoundryClient, domains, spaces) -> getRoutes(cloudFoundryClient, request, this.organizationId, this.spaceId)
                .map(resource -> Tuples.of(cloudFoundryClient, domains, resource, spaces))))
            .flatMap(function((cloudFoundryClient, domains, resource, spaces) -> Mono
                .when(
                    getApplicationNames(cloudFoundryClient, ResourceUtils.getId(resource)),
                    getDomainName(domains, ResourceUtils.getEntity(resource).getDomainId()),
                    Mono.just(resource),
                    getSpaceName(spaces, ResourceUtils.getEntity(resource).getSpaceId())
                )))
            .map(function(DefaultRoutes::toRoute))
            .transform(OperationsLogging.log("List Routes"))
            .checkpoint();
    }

    @Override
    public Mono<Integer> map(MapRouteRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId, this.spaceId)
            .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getOrCreateRoute(cloudFoundryClient, organizationId, spaceId, request.getDomain(), request.getHost(), request.getPath(), request.getPort(), request.getRandomPort()),
                    getApplicationId(cloudFoundryClient, request.getApplicationName(), spaceId)
                )))
            .flatMap(function((cloudFoundryClient, routeResource, applicationId) -> requestAssociateRoute(cloudFoundryClient, applicationId, ResourceUtils.getId(routeResource))))
            .then(Mono.justOrEmpty(request.getPort()))
            .transform(OperationsLogging.log("Map Route"))
            .checkpoint();
    }

    @Override
    public Mono<Void> unmap(UnmapRouteRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.organizationId, this.spaceId)
            .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono
                .when(
                    Mono.just(cloudFoundryClient),
                    getApplicationId(cloudFoundryClient, request.getApplicationName(), spaceId),
                    getDomainId(cloudFoundryClient, organizationId, request.getDomain())
                        .flatMap(domainId -> getRouteId(cloudFoundryClient, request.getHost(), request.getDomain(), domainId, request.getPath(), request.getPort()))
                )))
            .flatMap(function(DefaultRoutes::requestRemoveRouteFromApplication))
            .transform(OperationsLogging.log("Unmap Route"))
            .checkpoint();
    }

    private static Mono<Void> deleteRoute(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String routeId) {
        return requestDeleteRoute(cloudFoundryClient, routeId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, job));
    }

    private static Mono<Map<String, String>> getAllDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestAllPrivateDomains(cloudFoundryClient, organizationId)
            .map(resource -> Tuples.of(ResourceUtils.getId(resource), ResourceUtils.getEntity(resource).getName()))
            .mergeWith(requestAllSharedDomains(cloudFoundryClient)
                .map(resource -> Tuples.of(ResourceUtils.getId(resource), ResourceUtils.getEntity(resource).getName())))
            .collectMap(function((id, name) -> id), function((id, name) -> name));
    }

    private static Mono<Map<String, String>> getAllSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestAllSpaces(cloudFoundryClient, organizationId)
            .map(resource -> Tuples.of(ResourceUtils.getId(resource), ResourceUtils.getEntity(resource).getName()))
            .collectMap(function((id, name) -> id), function((id, name) -> name));
    }

    private static Mono<ApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return requestApplications(cloudFoundryClient, application, spaceId)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Application %s does not exist", application));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<List<String>> getApplicationNames(CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestApplications(cloudFoundryClient, routeId)
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .collectList();
    }

    private static Mono<List<ApplicationResource>> getApplications(CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestApplications(cloudFoundryClient, routeId)
            .collectList();
    }

    private static Mono<Resource<?>> getDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomains(cloudFoundryClient, organizationId, domain)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Domain %s does not exist", domain));
    }

    private static Mono<String> getDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomain(cloudFoundryClient, organizationId, domain)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getDomainName(Map<String, String> domains, String domainId) {
        return Mono.just(domains.get(domainId));
    }

    private static Flux<Resource<?>> getDomains(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return requestPrivateDomains(cloudFoundryClient, organizationId, domain)
            .map(OperationUtils.<PrivateDomainResource, Resource<?>>cast())
            .switchIfEmpty(requestSharedDomains(cloudFoundryClient, domain));
    }

    private static Mono<String> getOptionalDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomains(cloudFoundryClient, organizationId, domain)
            .singleOrEmpty()
            .map(ResourceUtils::getId);
    }

    private static Mono<AbstractRouteResource> getOrCreateRoute(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId, String domain, String host, String path, Integer port,
                                                                Boolean randomPort) {
        if (randomPort != null) {
            return getDomainId(cloudFoundryClient, organizationId, domain)
                .flatMap(domainId -> requestCreateRoute(cloudFoundryClient, domainId, host, path, port, randomPort, spaceId));
        }

        return getDomainId(cloudFoundryClient, organizationId, domain)
            .flatMap(domainId -> getRoute(cloudFoundryClient, domainId, host, path, port)
                .cast(AbstractRouteResource.class)
                .switchIfEmpty(requestCreateRoute(cloudFoundryClient, domainId, host, path, port, randomPort, spaceId)));
    }

    private static Mono<RouteResource> getRoute(CloudFoundryClient cloudFoundryClient, String domainId, String domain, String host, String path, Integer port) {
        return getRoute(cloudFoundryClient, domainId, host, path, port)
            .switchIfEmpty(ExceptionUtils.illegalArgument("Route for %s does not exist", domain));
    }

    private static Mono<RouteResource> getRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path, Integer port) {
        if (port != null) {
            return requestRoutes(cloudFoundryClient, domainId, null, null, port)
                .singleOrEmpty();
        } else {
            return requestRoutes(cloudFoundryClient, domainId, host, path, port)
                .filter(resource -> isIdentical(nullSafe(host), ResourceUtils.getEntity(resource).getHost()))
                .filter(resource -> isIdentical(Optional.ofNullable(path).orElse(""), ResourceUtils.getEntity(resource).getPath()))
                .singleOrEmpty();
        }
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String host, String domain, String domainId, String path, Integer port) {
        return getRoute(cloudFoundryClient, domainId, domain, host, path, port)
            .map(ResourceUtils::getId);
    }

    private static Flux<RouteResource> getRoutes(CloudFoundryClient cloudFoundryClient, ListRoutesRequest request, Mono<String> organizationId, Mono<String> spaceId) {
        if (Level.ORGANIZATION == request.getLevel()) {
            return organizationId
                .flatMapMany(organizationId1 -> requestRoutes(cloudFoundryClient, builder -> builder.organizationId(organizationId1)));
        } else {
            return spaceId
                .flatMapMany(spaceId1 -> requestSpaceRoutes(cloudFoundryClient, spaceId1));
        }
    }

    private static Mono<SpaceResource> getSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Space %s does not exist", space));
    }

    private static Mono<String> getSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getSpace(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSpaceName(Map<String, String> spaces, String spaceId) {
        return Mono.just(spaces.get(spaceId));
    }

    private static boolean isApplicationOrphan(List<ApplicationResource> applications) {
        return applications.isEmpty();
    }

    private static boolean isIdentical(String s, String t) {
        return s == null ? t == null : s.equals(t);
    }

    private static String nullSafe(String host) {
        return host == null ? "" : host;
    }

    private static Flux<PrivateDomainResource> requestAllPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestAllSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestAllSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<ApplicationResource> requestApplications(CloudFoundryClient cloudFoundryClient, String routeId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .page(page)
                    .build()));
    }

    private static Flux<ApplicationResource> requestApplications(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listApplications(ListSpaceApplicationsRequest.builder()
                    .name(application)
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path, Integer port, Boolean randomPort, String spaceId) {

        org.cloudfoundry.client.v2.routes.CreateRouteRequest.Builder builder = org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder();

        if (randomPort != null && randomPort) {
            builder.generatePort(true);
        } else if (port != null) {
            builder.port(port);
        } else {
            builder.host(host);
            builder.path(path);
        }

        return cloudFoundryClient.routes()
            .create(builder
                .domainId(domainId)
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

    private static Flux<PrivateDomainResource> requestPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .organizationId(organizationId)
                    .name(domain)
                    .page(page)
                    .build()));
    }

    private static Mono<Void> requestRemoveRouteFromApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .removeRoute(RemoveApplicationRouteRequest.builder()
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

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, UnaryOperator<org.cloudfoundry.client.v2.routes.ListRoutesRequest.Builder> modifier) {

        org.cloudfoundry.client.v2.routes.ListRoutesRequest.Builder listBuilder = modifier.apply(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder());

        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.routes()
                .list(listBuilder
                    .page(page)
                    .build()));
    }

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path, Integer port) {
        return requestRoutes(cloudFoundryClient, builder -> builder
            .domainId(domainId)
            .hosts(Optional.ofNullable(host).map(Collections::singletonList).orElse(null))
            .paths(Optional.ofNullable(path).map(Collections::singletonList).orElse(null))
            .port(Optional.ofNullable(port).orElse(null))
        );
    }

    private static Flux<SharedDomainResource> requestSharedDomains(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .name(domain)
                    .page(page)
                    .build()));
    }

    private static Flux<RouteResource> requestSpaceRoutes(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listRoutes(ListSpaceRoutesRequest.builder()
                    .spaceId(spaceId)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .organizationId(organizationId)
                    .name(space)
                    .page(page)
                    .build()));
    }

    private static Route toRoute(List<String> applications, String domain, RouteResource resource, String space) {
        RouteEntity entity = ResourceUtils.getEntity(resource);

        return Route.builder()
            .applications(applications)
            .domain(domain)
            .host(entity.getHost())
            .id(ResourceUtils.getId(resource))
            .path(entity.getPath())
            .space(space)
            .build();
    }

    private boolean isRouteOrphan(RouteEntity entity) {
        return entity.getServiceInstanceId() == null || entity.getServiceInstanceId().isEmpty();
    }

}
