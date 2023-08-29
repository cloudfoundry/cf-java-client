/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.v3.routes;

import org.cloudfoundry.client.CloudFoundryClient;

import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.client.v3.routes.Destination;
import org.cloudfoundry.client.v3.routes.Application;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.RemoveRouteDestinationsRequest;

import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.routes.RouteResource;
import org.cloudfoundry.client.v3.domains.CheckReservedRoutesRequest;
import org.cloudfoundry.client.v3.spaces.DeleteUnmappedRoutesRequest;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.NoSuchElementException;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.operations.v3.mapper.MapperUtils;

public final class DefaultRoutes implements Routes {

        private final Mono<CloudFoundryClient> cloudFoundryClient;

        private final Mono<String> organizationId;

        private final Mono<String> spaceId;

        public DefaultRoutes(Mono<CloudFoundryClient> cloudFoundryClient, Mono<String> organizationId,
                        Mono<String> spaceId) {
                this.cloudFoundryClient = cloudFoundryClient;
                this.organizationId = organizationId;
                this.spaceId = spaceId;
        }

        @Override
        public Mono<Boolean> check(CheckRouteRequest request) {
                return Mono.zip(this.cloudFoundryClient, this.organizationId)
                                .flatMap(function((client, organizationId) -> Mono.zip(this.cloudFoundryClient,
                                                MapperUtils.getOptionalDomainIdByName(
                                                                client, organizationId,
                                                                request.getDomain()))))
                                .flatMap(function((client, domainId) -> routeExists(client, domainId, request.getHost(),
                                                request.getPath())))
                                .defaultIfEmpty(false)
                                .transform(OperationsLogging.log("Check Route Exists"))
                                .checkpoint();
        }

        @Override
        public Mono<Integer> create(CreateRouteRequest request) {
                return Mono.zip(this.cloudFoundryClient, organizationId)
                                .flatMap(function((client, orgId) -> Mono.zip(
                                                this.cloudFoundryClient,
                                                MapperUtils.getSpaceIdByName(client, orgId,
                                                                request.getSpace()),
                                                MapperUtils.getDomainIdByName(client, orgId, request.getDomain()))))
                                .flatMap(function((client, spaceId, domainId) -> createRoute(client,
                                                spaceId, domainId, request.getPath(), request.getHost(),
                                                request.getPort())))
                                .flatMap(route -> Mono.justOrEmpty(route.getPort()))
                                .transform(OperationsLogging.log("Create Route"))
                                .checkpoint();
        }

        @Override
        public Mono<Void> delete(DeleteRouteRequest request) {
                return Mono.zip(
                                this.cloudFoundryClient, this.organizationId)
                                .flatMap(function((client, orgId) -> Mono.zip(this.cloudFoundryClient,
                                                MapperUtils.getRouteId(client, orgId, request.getDomain(),
                                                                request.getHost(),
                                                                request.getPort(), request.getPath()))))
                                .flatMap(function((client, routeId) -> deleteRoute(client,
                                                request.getCompletionTimeout(), routeId)))
                                .transform(OperationsLogging.log("Delete Route"))
                                .checkpoint();
        }

        @Override
        public Mono<Void> deleteOrphanedRoutes(DeleteOrphanedRoutesRequest request) {
                return Mono.zip(this.cloudFoundryClient, this.spaceId)
                                .flatMap(function((client, spaceId) -> Mono.zip(this.cloudFoundryClient,
                                                client.spacesV3().deleteUnmappedRoutes(
                                                                DeleteUnmappedRoutesRequest.builder().spaceId(spaceId)
                                                                                .build()))))
                                .flatMap(function((client, job) -> JobUtils.waitForCompletion(client,
                                                request.getCompletionTimeout(), job)))
                                .transform(OperationsLogging.log("Delete Orphaned Routes"))
                                .checkpoint();

        }

        @Override
        public Flux<Route> list(ListRoutesRequest request) {
                return Mono.zip(this.cloudFoundryClient, this.organizationId, this.spaceId)
                                .flatMapMany(function((client, organizationId, spaceId) -> MapperUtils.listRoutes(
                                                client,
                                                new String[] { organizationId },
                                                ((Level.SPACE == request.getLevel()) ? new String[] { spaceId }
                                                                : null),

                                                null, null, null, null, null)))
                                .map(route -> toRoute(route))
                                .transform(OperationsLogging.log("List Routes"))
                                .checkpoint();
        }

        @Override
        public Mono<Integer> map(MapRouteRequest request) {
                return Mono
                                .zip(this.cloudFoundryClient, this.organizationId, this.spaceId)
                                .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono.zip(
                                                this.cloudFoundryClient,
                                                getOrCreateRoute(cloudFoundryClient, organizationId, spaceId,
                                                                request.getDomain(),
                                                                request.getHost(), request.getPath(),
                                                                request.getPort()),
                                                MapperUtils.getApplicationIdByName(cloudFoundryClient,
                                                                request.getApplicationName(),
                                                                spaceId, organizationId))))
                                .flatMap(function((cloudFoundryClient, routeResource,
                                                applicationId) -> insertDestination(
                                                                cloudFoundryClient, routeResource.getId(),
                                                                Destination.builder()
                                                                                .application(Application.builder()
                                                                                                .applicationId(applicationId)
                                                                                                .build())
                                                                                .port(routeResource.getPort())
                                                                                .protocol(routeResource.getProtocol()
                                                                                                .toString())
                                                                                .build())))
                                .then(Mono.justOrEmpty(request.getPort()))
                                .transform(OperationsLogging.log("Map Route"))
                                .checkpoint();
        }

        private static Mono<org.cloudfoundry.client.v3.routes.Route> getOrCreateRoute(
                        CloudFoundryClient cloudFoundryClient,
                        String organizationId, String spaceId, String domain, String host, String path, Integer port) {
                // create will assign random port, thus we need to create route every time
                if (port == null) {
                        return createRoute(cloudFoundryClient, spaceId, domain, path, host, port)
                                        .cast(org.cloudfoundry.client.v3.routes.Route.class);

                }

                return MapperUtils.getDomainIdByName(cloudFoundryClient, organizationId, domain)
                                .flatMap(domainId -> MapperUtils
                                                .getRoute(cloudFoundryClient, organizationId, domainId, host,
                                                                port, path, null)
                                                .cast(org.cloudfoundry.client.v3.routes.Route.class)
                                                .switchIfEmpty(createRoute(cloudFoundryClient, spaceId, domain, path,
                                                                host, port)
                                                                .cast(org.cloudfoundry.client.v3.routes.Route.class)));
        }

        @Override
        public Mono<Void> unmap(UnmapRouteRequest request) {
                return Mono
                                .zip(this.cloudFoundryClient, this.organizationId, this.spaceId)
                                .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono.zip(
                                                this.cloudFoundryClient,
                                                this.organizationId,
                                                this.spaceId,
                                                MapperUtils.getApplicationIdByName(cloudFoundryClient,
                                                                request.getApplicationName(),
                                                                spaceId, organizationId))))
                                .flatMap(function((cloudFoundryClient, organizationId, spaceId, applicationId) -> Mono
                                                .zip(
                                                                this.cloudFoundryClient,
                                                                MapperUtils.getRoute(cloudFoundryClient, organizationId,
                                                                                request.getDomain(),
                                                                                request.getHost(),
                                                                                request.getPort(), request.getPath(),
                                                                                applicationId),
                                                                Mono.just(applicationId))))
                                .flatMap(function((cloudFoundryClient, route,
                                                applicationId) -> removeDestination(
                                                                cloudFoundryClient, route,
                                                                applicationId)))

                                .transform(OperationsLogging.log("Unmap Route"))
                                .checkpoint();

        }

        private static Mono<Void> deleteRoute(CloudFoundryClient cloudFoundryClient,
                        Duration completionTimeout,
                        String routeId) {
                return requestDeleteRoute(cloudFoundryClient, routeId)
                                .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient,
                                                completionTimeout, job));
        }

        private static Mono<String> requestDeleteRoute(CloudFoundryClient cloudFoundryClient,
                        String routeId) {
                return cloudFoundryClient.routesV3()
                                .delete(org.cloudfoundry.client.v3.routes.DeleteRouteRequest.builder()
                                                .routeId(routeId)
                                                .build());
        }

        private static Route toRoute(RouteResource resource) {
                List<String> applications = new ArrayList<String>();
                for (Destination destination : resource.getDestinations()) {
                        applications.add(destination.getApplication().getApplicationId());
                }
                // TODO service must be added
                return Route.builder()
                                .applications(applications)
                                .domain(resource.getRelationships().getDomain().getData().getId())
                                .host(resource.getHost())
                                .id(resource.getId())
                                .path(resource.getPath())
                                .space(resource.getRelationships().getSpace().getData().getId()).build();
        }

        private static Mono<Boolean> routeExists(CloudFoundryClient cloudFoundryClient, String domainId,
                        String host,
                        String path) {
                return cloudFoundryClient.domainsV3().checkReservedRoutes(CheckReservedRoutesRequest.builder()
                                .domainId(domainId)
                                .host(host)
                                .path(path)
                                .build())
                                .flatMap(response -> Mono.just(response.getMatchingRoute()));
        }

        private static Mono<org.cloudfoundry.client.v3.routes.CreateRouteResponse> createRoute(
                        CloudFoundryClient client,
                        String spaceId, String domainId, String path, String host, Integer port) {
                return client.routesV3().create(org.cloudfoundry.client.v3.routes.CreateRouteRequest
                                .builder()
                                .relationships(RouteRelationships
                                                .builder()
                                                .space(ToOneRelationship
                                                                .builder()
                                                                .data(Relationship
                                                                                .builder()
                                                                                .id(spaceId)
                                                                                .build())
                                                                .build())
                                                .domain(ToOneRelationship
                                                                .builder()
                                                                .data(Relationship
                                                                                .builder()
                                                                                .id(domainId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .path(path)
                                .host(host)
                                .port(port)

                                .build());
        }

        private static Mono<InsertRouteDestinationsResponse> insertDestination(
                        CloudFoundryClient cloudFoundryClient, String routeId, Destination destination) {
                return cloudFoundryClient.routesV3()
                                .insertDestinations(InsertRouteDestinationsRequest.builder()
                                                .routeId(routeId)
                                                .destination(destination)
                                                .build());
        }

        private static Mono<Void> removeDestination(
                        CloudFoundryClient cloudFoundryClient, RouteResource route, String applicationId) {
                String destinationId = route.getDestinations().stream()
                                .filter(destination -> destination.getApplication().getApplicationId() == applicationId)
                                .map(destination -> destination.getDestinationId())
                                .findAny().get();

                return cloudFoundryClient.routesV3()
                                .removeDestinations(RemoveRouteDestinationsRequest.builder()
                                                .routeId(route.getId())
                                                .destinationId(destinationId)
                                                .build());
        }
}
