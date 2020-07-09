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
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.routes.Application;
import org.cloudfoundry.client.v3.routes.CreateRouteRequest;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.Destination;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.routes.RouteResource;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ApplicationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> spaceId;

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutes() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, "listApplicationRoutes", spaceId)
            )))
            .flatMapMany(function((applicationId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)
                .thenReturn(applicationId)))
            .flatMap(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .build())))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutes-key", "test-listApplicationRoutes-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutesByDomain() {
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
                createRouteId(this.cloudFoundryClient, domainId, "listApplicationRoutesByDomain", spaceId)
            )))
            .delayUntil(function((applicationId, domainId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, domainId, ignore) -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .domainId(domainId)
                        .page(page)
                        .build()))))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutesByDomain-key", "test-listApplicationRoutesByDomain-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutesByHost() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, hostName, "listApplicationRoutesByHost", null, null, spaceId)
            )))
            .flatMap(function((applicationId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)
                .thenReturn(applicationId)))
            .flatMapMany(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .host(hostName)
                        .page(page)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listApplicationRoutesByHost-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutesByHost-key", "test-listApplicationRoutesByHost-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutesByLabelSelector() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, "listApplicationRoutesByLabelSelector", spaceId)
            )))
            .flatMapMany(function((applicationId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)
                .thenReturn(applicationId)))
            .flatMap(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .labelSelector("test-listApplicationRoutesByLabelSelector-key=test-listApplicationRoutesByLabelSelector-value")
                        .page(page)
                        .build())))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutesByLabelSelector-key", "test-listApplicationRoutesByLabelSelector-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutesByOrganizationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))

            .flatMap(function((domainId, organizationId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                Mono.just(organizationId),
                createRouteId(this.cloudFoundryClient, domainId, "listApplicationRoutesByOrganizationId", spaceId)
            )))
            .delayUntil(function((applicationId, organizationId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)))
            .flatMapMany(function((applicationId, organizationId, ignore) -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .organizationId(organizationId)
                        .page(page)
                        .build()))))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutesByOrganizationId-key", "test-listApplicationRoutesByOrganizationId-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutesByPath() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String path = this.nameFactory.getPath();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                this.spaceId
            ))

            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, null, "listApplicationRoutesByPath", path, null, spaceId)
            )))
            .flatMapMany(function((applicationId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)
                .thenReturn(applicationId)))
            .flatMap(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .path(path)
                        .page(page)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listApplicationRoutesByPath-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutesByPath-key", "test-listApplicationRoutesByPath-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Test has not been validated
    @IfCloudFoundryVersion(greaterThan = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutesByPort() {
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
                createRouteId(this.cloudFoundryClient, domainId, null, "listApplicationRoutesByPort", null, port, spaceId)
            )))
            .flatMapMany(function((applicationId, routeId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)
                .thenReturn(applicationId)))
            .flatMap(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .port(port)
                        .page(page)
                        .build())))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listApplicationRoutesByPort-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutesByPort-key", "test-listApplicationRoutesByPort-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listApplicationRoutesBySpaceId() {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))

            .flatMap(function((domainId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                createRouteId(this.cloudFoundryClient, domainId, "listApplicationRoutesBySpaceId", spaceId),
                Mono.just(spaceId)
            )))
            .delayUntil(function((applicationId, routeId, spaceId) -> requestReplaceDestinations(this.cloudFoundryClient, applicationId, routeId)
                .thenReturn(applicationId)))
            .flatMapMany(function((applicationId, ignore, spaceId) -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listRoutes(ListApplicationRoutesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .filter(route -> route.getMetadata().getLabels().containsKey("test-listApplicationRoutesBySpaceId-key"))
            .map(RouteResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-listApplicationRoutesBySpaceId-key", "test-listApplicationRoutesBySpaceId-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, applicationName, spaceId)
            .map(CreateApplicationResponse::getId);
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

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String host, String label, String path, Integer port, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, host, label, path, port, spaceId)
            .map(CreateRouteResponse::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
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

}
