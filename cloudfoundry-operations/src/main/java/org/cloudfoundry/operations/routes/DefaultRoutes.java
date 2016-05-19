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
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.OperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

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
            .then(function((validRequest, organizationId) -> getOptionalDomainId(this.cloudFoundryClient, organizationId, validRequest.getDomain())
                .and(Mono.just(validRequest))))
            .then(function((domainId, checkRouteRequest) -> requestRouteExists(this.cloudFoundryClient, domainId, checkRouteRequest.getHost(), checkRouteRequest.getPath())))
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Void> create(CreateRouteRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.organizationId)
            .then(function((validRequest, organizationId) -> Mono
                .when(
                    getSpaceId(this.cloudFoundryClient, organizationId, validRequest.getSpace()),
                    getDomainId(this.cloudFoundryClient, organizationId, validRequest.getDomain()),
                    Mono.just(validRequest)
                )))
            .then(function((spaceId, domainId, validRequest) -> requestCreateRoute(this.cloudFoundryClient, domainId, validRequest.getHost(), validRequest.getPath(), spaceId)))
            .then();
    }

    @Override
    public Mono<Void> delete(DeleteRouteRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.organizationId)
            .then(function((validRequest, organizationId) -> getDomainId(this.cloudFoundryClient, organizationId, validRequest.getDomain())
                .and(Mono.just(validRequest))))
            .then(function((domainId, validRequest) -> getRouteId(this.cloudFoundryClient, validRequest.getHost(), validRequest.getDomain(), domainId, validRequest.getPath())))
            .then(routeId -> deleteRoute(this.cloudFoundryClient, routeId));
    }

    @Override
    public Mono<Void> deleteOrphanedRoutes() {
        return this.spaceId
            .flatMap(spaceId -> requestSpaceRoutes(this.cloudFoundryClient, spaceId))
            .map(ResourceUtils::getId)
            .flatMap(routeId -> getApplications(this.cloudFoundryClient, routeId)
                .and(Mono.just(routeId)))
            .filter(predicate((applicationResources, routeId) -> isOrphan(applicationResources)))
            .flatMap(function((applicationResources, routeId) -> deleteRoute(this.cloudFoundryClient, routeId)))
            .then();
    }

    @Override
    public Flux<Route> list(ListRoutesRequest request) {
        return ValidationUtils
            .validate(request)
            .flatMap(validRequest -> getRoutes(this.cloudFoundryClient, validRequest, this.organizationId, this.spaceId))
            .flatMap(resource -> Mono
                .when(
                    getApplicationNames(this.cloudFoundryClient, resource),
                    getDomainName(this.cloudFoundryClient, resource),
                    Mono.just(resource),
                    getSpaceName(this.cloudFoundryClient, ResourceUtils.getEntity(resource).getSpaceId())
                ))
            .map(function(DefaultRoutes::toRoute));
    }

    @Override
    public Mono<Void> map(MapRouteRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId, this.organizationId)
            .then(function((validRequest, spaceId, organizationId) -> Mono
                .when(
                    getOrCreateRoute(this.cloudFoundryClient, organizationId, spaceId, validRequest.getDomain(), validRequest.getHost(), validRequest.getPath()),
                    getApplicationId(this.cloudFoundryClient, validRequest.getApplicationName(), spaceId)
                )))
            .then(function((routeId, applicationId) -> requestAssociateRoute(this.cloudFoundryClient, applicationId, routeId)))
            .then();
    }

    @Override
    public Mono<Void> unmap(UnmapRouteRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.organizationId, this.spaceId)
            .then(function((unmapRouteRequest, organizationId, spaceId) -> Mono
                .when(
                    getApplicationId(this.cloudFoundryClient, unmapRouteRequest.getApplicationName(), spaceId),
                    getDomainId(this.cloudFoundryClient, organizationId, unmapRouteRequest.getDomain())
                        .then(domainId -> getRouteId(this.cloudFoundryClient, unmapRouteRequest.getHost(), unmapRouteRequest.getDomain(), domainId, unmapRouteRequest.getPath()))
                )))
            .then(function((applicationId, routeId) -> requestRemoveApplication(this.cloudFoundryClient, applicationId, routeId)));
    }

    private static Mono<Void> deleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestDeleteRoute(cloudFoundryClient, routeId)
            .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<ApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return requestApplications(cloudFoundryClient, application, spaceId)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Application %s does not exist", application)));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<List<String>> getApplicationNames(CloudFoundryClient cloudFoundryClient, RouteResource routeResource) {
        return requestApplications(cloudFoundryClient, ResourceUtils.getId(routeResource))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .toList();
    }

    private static Mono<List<ApplicationResource>> getApplications(CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestApplications(cloudFoundryClient, routeId)
            .toList();
    }

    private static Mono<Resource<?>> getDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomains(cloudFoundryClient, organizationId, domain)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Domain %s does not exist", domain)));
    }

    private static Mono<String> getDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomain(cloudFoundryClient, organizationId, domain)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getDomainName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        return requestDomain(cloudFoundryClient, ResourceUtils.getEntity(resource).getDomainId())
            .map(response -> ResourceUtils.getEntity(response).getName());
    }

    private static Flux<Resource<?>> getDomains(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return requestPrivateDomains(cloudFoundryClient, organizationId, domain)
            .map(OperationUtils.<PrivateDomainResource, Resource<?>>cast())
            .switchIfEmpty(requestSharedDomains(cloudFoundryClient, domain)
                .map(OperationUtils.<SharedDomainResource, Resource<?>>cast()));
    }

    private static Mono<String> getOptionalDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return getDomains(cloudFoundryClient, organizationId, domain)
            .singleOrEmpty()
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getOrCreateRoute(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId, String domain, String host, String path) {
        return getDomainId(cloudFoundryClient, organizationId, domain)
            .then(domainId -> requestRoutes(cloudFoundryClient, domainId, host, path)
                .singleOrEmpty()
                .map(OperationUtils.<RouteResource, Resource<RouteEntity>>cast())
                .otherwiseIfEmpty(requestCreateRoute(cloudFoundryClient, domainId, host, path, spaceId)))
            .map(ResourceUtils::getId);
    }

    private static Mono<RouteResource> getRoute(CloudFoundryClient cloudFoundryClient, String host, String domain, String domainId, String path) {
        return requestRoutes(cloudFoundryClient, domainId, host, path)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Route %s.%s does not exist", host, domain)));
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String host, String domain, String domainId, String path) {
        return getRoute(cloudFoundryClient, host, domain, domainId, path)
            .map(ResourceUtils::getId);
    }

    private static Flux<RouteResource> getRoutes(CloudFoundryClient cloudFoundryClient, ListRoutesRequest request, Mono<String> organizationId, Mono<String> spaceId) {
        if (Level.ORGANIZATION == request.getLevel()) {
            return organizationId
                .flatMap(organizationId1 -> requestOrganizationRoutes(cloudFoundryClient, organizationId1));
        } else {
            return spaceId
                .flatMap(spaceId1 -> requestSpaceRoutes(cloudFoundryClient, spaceId1));
        }
    }

    private static Mono<SpaceResource> getSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestSpaces(cloudFoundryClient, organizationId, space)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Space %s does not exist", space)));
    }

    private static Mono<String> getSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getSpace(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSpaceName(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestSpace(cloudFoundryClient, spaceId)
            .map(response -> ResourceUtils.getEntity(response).getName());
    }

    private static boolean isOrphan(List<ApplicationResource> applications) {
        return applications.isEmpty();
    }

    private static Flux<ApplicationResource> requestApplications(CloudFoundryClient cloudFoundryClient, String routeId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.routes()
                .listApplications(ListRouteApplicationsRequest.builder()
                    .routeId(routeId)
                    .page(page)
                    .build()));
    }

    private static Flux<ApplicationResource> requestApplications(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
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

    private static Flux<RouteResource> requestOrganizationRoutes(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.routes()
                .list(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .organizationId(organizationId)
                    .name(domain)
                    .page(page)
                    .build()));
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

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path) {
        return PaginationUtils.
            requestResources(page -> cloudFoundryClient.routes()
                .list(org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                    .domainId(domainId)
                    .host(host)
                    .page(page)
                    .path(path)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestSharedDomains(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .name(domain)
                    .page(page)
                    .build()));
    }

    private static Mono<GetSpaceResponse> requestSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.spaces()
            .get(GetSpaceRequest.builder()
                .spaceId(spaceId)
                .build());
    }

    private static Flux<RouteResource> requestSpaceRoutes(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listRoutes(ListSpaceRoutesRequest.builder()
                    .spaceId(spaceId)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
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
            .routeId(ResourceUtils.getId(resource))
            .space(space)
            .build();
    }

}
