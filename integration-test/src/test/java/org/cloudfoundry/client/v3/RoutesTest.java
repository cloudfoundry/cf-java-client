/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.ApplicationRelationships;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.routes.Application;
import org.cloudfoundry.client.v3.routes.CreateRouteRequest;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.Destination;
import org.cloudfoundry.client.v3.routes.GetRouteRequest;
import org.cloudfoundry.client.v3.routes.GetRouteResponse;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ListRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ListRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.ListRoutesRequest;
import org.cloudfoundry.client.v3.routes.RemoveRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.routes.RouteResource;
import org.cloudfoundry.client.v3.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class RoutesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> spaceId;

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void create() {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) ->
                this.cloudFoundryClient.routesV3()
                    .create(CreateRouteRequest.builder()
                        .metadata(Metadata.builder()
                            .label("test-create-key", "test-create-value")
                            .build())
                        .relationships(RouteRelationships.builder()
                            .domain(ToOneRelationship.builder()
                                .data(Relationship.builder()
                                    .id(domainId)
                                    .build())
                                .build())
                            .space(ToOneRelationship.builder()
                                .data(Relationship.builder()
                                    .id(spaceId)
                                    .build())
                                .build())
                            .build())
                        .build())
                    .thenReturn(domainId)))
            .flatMapMany(domainId -> requestListRoutes(this.cloudFoundryClient, domainId))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-create-key", "test-create-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void get() {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, "get", spaceId)))
            .flatMapMany(routeId ->
                this.cloudFoundryClient.routesV3()
                    .get(GetRouteRequest.builder()
                        .routeId(routeId)
                        .build()))
            .map(GetRouteResponse::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-get-key", "test-get-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void insertDestinations() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        Integer port = this.nameFactory.getPort();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                Mono.just(domainId),
                createRouteId(this.cloudFoundryClient, domainId, "insertDestinations", spaceId)
            )))
            .flatMapMany(function((applicationId, domainId, routeId) -> this.cloudFoundryClient.routesV3()
                .insertDestinations(InsertRouteDestinationsRequest.builder()
                    .destination(Destination.builder()
                        .application(Application.builder()
                            .applicationId(applicationId)
                            .build())
                        .port(port)
                        .build())
                    .routeId(routeId)
                    .build())
                .thenReturn(domainId)))
            .flatMap(domainId -> requestListRoutes(this.cloudFoundryClient, domainId))
            .flatMapIterable(RouteResource::getDestinations)
            .map(Destination::getPort)
            .as(StepVerifier::create)
            .expectNext(port)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void list() {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, "list", spaceId)))
            .thenMany(PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .page(page)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-list-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-list-key", "test-list-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Test has not been validated
    @IfCloudFoundryVersion(greaterThan = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, "listByApplicationId", spaceId)
            )))
            .flatMapMany(function((applicationId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)
                .thenReturn(applicationId)))
            .flatMap(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .build())))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listByApplicationId-key", "test-listByApplicationId-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listByDomain() {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, "listByDomain", spaceId)
                .thenReturn(domainId)))
            .flatMapMany(domainId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .domainId(domainId)
                        .page(page)
                        .build())))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listByDomain-key", "test-listByDomain-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listByHost() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, hostName, "listByHost", null, null, spaceId)))
            .thenMany(PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .host(hostName)
                        .page(page)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listByHost-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listByHost-key", "test-listByHost-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listByLabelSelector() {
        String domainName = this.nameFactory.getDomainName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((domainId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, "listByLabelSelector", spaceId)
                .thenReturn(domainId)))
            .flatMapMany(domainId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .labelSelector("test-listByLabelSelector-key=test-listByLabelSelector-value")
                        .page(page)
                        .build())))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listByLabelSelector-key", "test-listByLabelSelector-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listByOrganizationId() {
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((domainId, organizationId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, "listByOrganizationId", spaceId)
                .thenReturn(organizationId)))
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listByOrganizationId-key", "test-listByOrganizationId-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listByPath() {
        String domainName = this.nameFactory.getDomainName();
        String path = this.nameFactory.getPath();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, null, "listByPath", path, null, spaceId)))
            .thenMany(PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .path(path)
                        .page(page)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listByPath-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listByPath-key", "test-listByPath-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Establish router group availability. This test has not been verified.
    public void listByPort() {
        String domainName = this.nameFactory.getDomainName();
        Integer port = this.nameFactory.getPort();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> requestCreateRoute(this.cloudFoundryClient, domainId, null, "listByPort", null, port, spaceId)))
            .thenMany(PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .port(port)
                        .page(page)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listByPort-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listByPort-key", "test-listByPort-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listBySpaceId() {
        String domainName = this.nameFactory.getDomainName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((domainId, spaceId) -> createRouteId(this.cloudFoundryClient, domainId, "listBySpaceId", spaceId)
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.routesV3()
                    .list(ListRoutesRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listBySpaceId-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listBySpaceId-key", "test-listBySpaceId-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDestinations() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                Mono.just(domainId),
                createRouteId(this.cloudFoundryClient, domainId, "removeDestinations", spaceId)
            )))
            .flatMapMany(function((applicationId, domainId, routeId) -> Mono.zip(
                Mono.just(applicationId),
                createDestinationId(this.cloudFoundryClient, applicationId, routeId),
                Mono.just(routeId)
            )))
            .flatMap(function((applicationId, ignore, routeId) -> Mono.zip(
                Mono.just(applicationId),
                this.cloudFoundryClient.routesV3()
                    .listDestinations(ListRouteDestinationsRequest.builder()
                        .routeId(routeId)
                        .build())
                    .map(response -> response.getDestinations().get(0).getApplication().getApplicationId()))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDestinationsByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                Mono.just(domainId),
                createRouteId(this.cloudFoundryClient, domainId, "removeDestinations", spaceId)
            )))
            .flatMapMany(function((applicationId, domainId, routeId) -> Mono.zip(
                Mono.just(applicationId),
                createDestinationId(this.cloudFoundryClient, applicationId, routeId),
                Mono.just(routeId)
            )))
            .flatMap(function((applicationId, destinationId, routeId) -> Mono.zip(
                Mono.just(destinationId),
                this.cloudFoundryClient.routesV3()
                    .listDestinations(ListRouteDestinationsRequest.builder()
                        .applicationId(applicationId)
                        .routeId(routeId)
                        .build())
                    .map(response -> response.getDestinations().get(0).getDestinationId()))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDestinationsById() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                Mono.just(domainId),
                createRouteId(this.cloudFoundryClient, domainId, "removeDestinations", spaceId)
            )))
            .flatMapMany(function((applicationId, domainId, routeId) -> Mono.zip(
                Mono.just(applicationId),
                createDestinationId(this.cloudFoundryClient, applicationId, routeId),
                Mono.just(routeId)
            )))
            .flatMap(function((applicationId, destinationId, routeId) -> Mono.zip(
                Mono.just(applicationId),
                this.cloudFoundryClient.routesV3()
                    .listDestinations(ListRouteDestinationsRequest.builder()
                        .destinationId(destinationId)
                        .routeId(routeId)
                        .build())
                    .map(response -> response.getDestinations().get(0).getApplication().getApplicationId()))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void removeDestinations() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                Mono.just(domainId),
                createRouteId(this.cloudFoundryClient, domainId, "removeDestinations", spaceId)
            )))
            .flatMapMany(function((applicationId, domainId, routeId) -> Mono.zip(
                createDestinationId(this.cloudFoundryClient, applicationId, routeId),
                Mono.just(routeId)
            )))
            .flatMap(function((destinationId, routeId) -> this.cloudFoundryClient.routesV3()
                .removeDestinations(RemoveRouteDestinationsRequest.builder()
                    .destinationId(destinationId)
                    .routeId(routeId)
                    .build())
                .thenReturn(routeId)))
            .flatMap(routeId -> getDestinations(this.cloudFoundryClient, routeId))
            .map(List::size)
            .as(StepVerifier::create)
            .expectNext(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void replaceDestinations() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        Integer port = this.nameFactory.getPort();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                Mono.just(domainId),
                createRouteId(this.cloudFoundryClient, domainId, "insertDestinations", spaceId)
            )))
            .flatMapMany(function((applicationId, domainId, routeId) -> this.cloudFoundryClient.routesV3()
                .replaceDestinations(ReplaceRouteDestinationsRequest.builder()
                    .destination(Destination.builder()
                        .application(Application.builder()
                            .applicationId(applicationId)
                            .build())
                        .port(port)
                        .build())
                    .routeId(routeId)
                    .build())
                .thenReturn(domainId)))
            .flatMap(domainId -> requestListRoutes(this.cloudFoundryClient, domainId))
            .flatMapIterable(RouteResource::getDestinations)
            .map(Destination::getPort)
            .as(StepVerifier::create)
            .expectNext(port)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void update() {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                Mono.just(domainId),
                createRouteId(this.cloudFoundryClient, domainId, "update", spaceId))
            ))
            .delayUntil(function((domainId, routeId) ->
                this.cloudFoundryClient.routesV3()
                    .update(UpdateRouteRequest.builder()
                        .routeId(routeId)
                        .metadata(Metadata.builder()
                            .label("test-update-key", "test-update-value")
                            .build())
                        .build())))
            .flatMapMany(function((domainId, ignore) -> requestListRoutes(this.cloudFoundryClient, domainId)))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-update-key", "test-update-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, applicationName, spaceId)
            .map(CreateApplicationResponse::getId);
    }

    private static Mono<String> createDestinationId(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return requestReplaceDestinations(cloudFoundryClient, applicationId, routeId)
            .flatMapIterable(ReplaceRouteDestinationsResponse::getDestinations)
            .single()
            .map(Destination::getDestinationId);
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return requestCreateDomain(cloudFoundryClient, domainName, organizationId)
            .map(CreateDomainResponse::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(CreateOrganizationResponse::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String label, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, label, spaceId)
            .map(CreateRouteResponse::getId);
    }

    private static Mono<List<Destination>> getDestinations(CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestListDestinations(cloudFoundryClient, routeId)
            .map(ListRouteDestinationsResponse::getDestinations);
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return cloudFoundryClient.applicationsV3()
            .create(CreateApplicationRequest.builder()
                .name(applicationName)
                .relationships(ApplicationRelationships.builder()
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(spaceId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<CreateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return cloudFoundryClient.domainsV3()
            .create(CreateDomainRequest.builder()
                .internal(false)
                .name(domainName)
                .relationships(DomainRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(organizationId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizationsV3()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String label, String path, Integer port, String spaceId) {
        String key = String.format("test-%s-key", label);
        String value = String.format("test-%s-value", label);

        return cloudFoundryClient.routesV3()
            .create(CreateRouteRequest.builder()
                .host(host)
                .metadata(Metadata.builder()
                    .label(key, value)
                    .build())
                .path(path)
                .port(port)
                .relationships(RouteRelationships.builder()
                    .domain(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(domainId)
                            .build())
                        .build())
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(spaceId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String label, String spaceId) {
        String key = String.format("test-%s-key", label);
        String value = String.format("test-%s-value", label);

        return cloudFoundryClient.routesV3()
            .create(CreateRouteRequest.builder()
                .metadata(Metadata.builder()
                    .label(key, value)
                    .build())
                .relationships(RouteRelationships.builder()
                    .domain(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(domainId)
                            .build())
                        .build())
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(spaceId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spacesV3()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .relationships(SpaceRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(organizationId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<ListRouteDestinationsResponse> requestListDestinations(CloudFoundryClient cloudFoundryClient, String routeId) {
        return cloudFoundryClient.routesV3()
            .listDestinations(ListRouteDestinationsRequest.builder()
                .routeId(routeId)
                .build());
    }

    private static Flux<RouteResource> requestListRoutes(CloudFoundryClient cloudFoundryClient, String domainId) {
        return PaginationUtils.requestClientV3Resources(page ->
            cloudFoundryClient.routesV3()
                .list(ListRoutesRequest.builder()
                    .domainId(domainId)
                    .page(page)
                    .build()));
    }

    private static Mono<ReplaceRouteDestinationsResponse> requestReplaceDestinations(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.routesV3()
            .replaceDestinations(ReplaceRouteDestinationsRequest.builder()
                .destination(Destination.builder()
                    .application(Application.builder()
                        .applicationId(applicationId)
                        .build())
                    .build())
                .routeId(routeId)
                .build());
    }

    private Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
    }

}
