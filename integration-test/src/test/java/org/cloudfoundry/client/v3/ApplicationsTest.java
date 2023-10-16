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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.ApplicationFeatureResource;
import org.cloudfoundry.client.v3.applications.ApplicationRelationships;
import org.cloudfoundry.client.v3.applications.ApplicationResource;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRelationshipRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentVariablesResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationFeatureRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationFeatureResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationPermissionsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationPermissionsResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationSshEnabledRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationSshEnabledResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationFeaturesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.RestartApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationFeatureRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.builds.BuildState;
import org.cloudfoundry.client.v3.builds.CreateBuildRequest;
import org.cloudfoundry.client.v3.builds.CreateBuildResponse;
import org.cloudfoundry.client.v3.builds.GetBuildRequest;
import org.cloudfoundry.client.v3.builds.GetBuildResponse;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.PackageRelationships;
import org.cloudfoundry.client.v3.packages.PackageState;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
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
import org.cloudfoundry.util.DelayUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.client.v3.applications.ApplicationState.STARTED;
import static org.cloudfoundry.client.v3.applications.ApplicationState.STOPPED;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ApplicationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void create() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> this.cloudFoundryClient.applicationsV3()
                .create(CreateApplicationRequest.builder()
                    .environmentVariable("test-create-env-key", "test-create-env-value")
                    .metadata(Metadata.builder()
                        .label("test-create-label-key", "test-create-label-value")
                        .build())
                    .name(applicationName)
                    .relationships(ApplicationRelationships.builder()
                        .space(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id(spaceId)
                                .build())
                            .build())
                        .build())
                    .build())
                .map(CreateApplicationResponse::getId))
            .flatMap(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId))
            .map(response -> response.getMetadata().getLabels().get(("test-create-label-key")))
            .as(StepVerifier::create)
            .expectNext("test-create-label-value")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .delayUntil(applicationId -> this.cloudFoundryClient.applicationsV3()
                .delete(DeleteApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMap(applicationId -> requestGetApplications(this.cloudFoundryClient, applicationId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV3Exception.class).hasMessageMatching("CF-ResourceNotFound\\([0-9]+\\): App not found.*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV3()
                .get(GetApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .map(GetApplicationResponse::getName)
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getDropletAssociation() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                uploadPackageAndCreateDropletId(this.cloudFoundryClient, applicationId)
            ))
            .delayUntil(function((applicationId, dropletId) -> requestSetDroplet(this.cloudFoundryClient, applicationId, dropletId)))
            .flatMap(function((applicationId, dropletId) -> Mono.zip(
                Mono.just(dropletId),
                this.cloudFoundryClient.applicationsV3()
                    .getCurrentDropletRelationship(GetApplicationCurrentDropletRelationshipRequest.builder()
                        .applicationId(applicationId)
                        .build())
                    .map(response -> response.getData().getId()))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getEnvironment() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV3()
                .getEnvironment(GetApplicationEnvironmentRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .map(env -> ((Map<String, Object>) env.getApplicationEnvironmentVariables().get("VCAP_APPLICATION")).get("name"))
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getFeature() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV3()
                .getFeature(GetApplicationFeatureRequest.builder()
                    .applicationId(applicationId)
                    .featureName("ssh")
                    .build()))
            .as(StepVerifier::create)
            .expectNext(GetApplicationFeatureResponse.builder()
                .description("Enable SSHing into the app.")
                .enabled(true)
                .name("ssh")
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_10)
    @Test
    public void getPermissions() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV3()
                .getPermissions(GetApplicationPermissionsRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .as(StepVerifier::create)
            .expectNext(GetApplicationPermissionsResponse.builder()
                .readBasicData(true)
                .readSensitiveData(true)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getSshEnabled() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV3()
                .getSshEnabled(GetApplicationSshEnabledRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .as(StepVerifier::create)
            .expectNext(GetApplicationSshEnabledResponse.builder()
                .enabled(true)
                .reason("")
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMapMany(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .list(ListApplicationsRequest.builder()
                        .page(page)
                        .build()))
                .filter(resource -> applicationId.equals(resource.getId())))
            .map(ApplicationResource::getName)
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

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

    @Test
    public void listFeatures() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMapMany(applicationId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.applicationsV3()
                    .listFeatures(ListApplicationFeaturesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .build())))
            .filter(resource -> "revisions".equals(resource.getName()))
            .map(ApplicationFeatureResource::getDescription)
            .as(StepVerifier::create)
            .consumeNextWith(description -> assertThat(description).startsWith("Enable versioning of an application"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBySpaceId() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, applicationName, spaceId)
            ))
            .flatMapMany(function((spaceId, applicationId) -> PaginationUtils
                .requestClientV3Resources(page ->
                    this.cloudFoundryClient.applicationsV3()
                        .list(ListApplicationsRequest.builder()
                            .spaceId(spaceId)
                            .page(page)
                            .build()))))
            .map(ApplicationResource::getName)
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void scale() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV3()
                .scale(ScaleApplicationRequest.builder()
                    .applicationId(applicationId)
                    .diskInMb(404)
                    .type("web")
                    .build())
                .thenReturn(applicationId))
            .flatMap(applicationId -> requestApplicationProcess(this.cloudFoundryClient, applicationId))
            .map(GetApplicationProcessResponse::getDiskInMb)
            .as(StepVerifier::create)
            .expectNext(404)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setAndGetDroplet() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                uploadPackageAndCreateDropletId(this.cloudFoundryClient, applicationId)
            ))
            .delayUntil(function((applicationId, dropletId) -> this.cloudFoundryClient.applicationsV3()
                .setCurrentDroplet(SetApplicationCurrentDropletRequest.builder()
                    .applicationId(applicationId)
                    .data(Relationship.builder()
                        .id(dropletId)
                        .build())
                    .build())))
            .flatMap(function((applicationId, dropletId) -> Mono.zip(
                Mono.just(dropletId),
                this.cloudFoundryClient.applicationsV3()
                    .getCurrentDroplet(GetApplicationCurrentDropletRequest.builder()
                        .applicationId(applicationId)
                        .build())
                    .map(GetApplicationCurrentDropletResponse::getId))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void start() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .delayUntil(applicationId -> prepareApplicationToStart(this.cloudFoundryClient, applicationId))
            .delayUntil((applicationId -> this.cloudFoundryClient.applicationsV3()
                .start(StartApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())))
            .flatMap(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId))
            .map(GetApplicationResponse::getState)
            .as(StepVerifier::create)
            .expectNext(STARTED)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void restart() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .delayUntil(applicationId -> prepareApplicationToStart(this.cloudFoundryClient, applicationId))
            .delayUntil(applicationId -> requestStartApplication(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> requestApplicationProcess(this.cloudFoundryClient, applicationId)
                .map(process -> Instant.parse(process.getUpdatedAt()))
                .map(updatedAt -> Tuples.of(applicationId, updatedAt)))
            .delayUntil(function((applicationId, oldUpdatedAt) -> this.cloudFoundryClient.applicationsV3()
                .restart(RestartApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build())
                .delaySubscription(Duration.ofSeconds(2))))
            .flatMap(function((applicationId, oldUpdatedAt) -> requestApplicationProcess(this.cloudFoundryClient, applicationId)
                .map(process -> Instant.parse(process.getUpdatedAt()))
                .map(newUpdatedAt -> Tuples.of(oldUpdatedAt, newUpdatedAt))
                .delaySubscription(Duration.ofSeconds(2))))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((oldUpdatedAt, newUpdatedAt) -> assertThat(newUpdatedAt).isAfter(oldUpdatedAt)))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void stop() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .delayUntil(applicationId -> prepareApplicationToStart(this.cloudFoundryClient, applicationId))
            .delayUntil((applicationId -> requestStartApplication(this.cloudFoundryClient, applicationId)))
            .delayUntil(applicationId -> this.cloudFoundryClient.applicationsV3()
                .stop(StopApplicationRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .flatMap(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId))
            .map(GetApplicationResponse::getState)
            .as(StepVerifier::create)
            .expectNext(STOPPED)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .delayUntil(applicationId -> this.cloudFoundryClient.applicationsV3()
                .update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .metadata(Metadata.builder()
                        .label("test-update-key", "test-update-value")
                        .build())
                    .build()))
            .flatMap(applicationId -> requestGetApplication(this.cloudFoundryClient, applicationId))
            .map(response -> response.getMetadata().getLabels())
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-update-key", "test-update-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateAndGetEnvironmentVariables() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .delayUntil(applicationId -> this.cloudFoundryClient.applicationsV3()
                .updateEnvironmentVariables(UpdateApplicationEnvironmentVariablesRequest.builder()
                    .applicationId(applicationId)
                    .var("test-updateEnv-key", "test-updateEnv-key")
                    .build()))
            .flatMap(applicationId -> this.cloudFoundryClient.applicationsV3()
                .getEnvironmentVariables(GetApplicationEnvironmentVariablesRequest.builder()
                    .applicationId(applicationId)
                    .build()))
            .map(GetApplicationEnvironmentVariablesResponse::getVars)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-updateEnv-key", "test-updateEnv-key"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateFeature() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> createApplicationId(this.cloudFoundryClient, applicationName, spaceId))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getFeatureEnabled(this.cloudFoundryClient, applicationId, "ssh")
            ))
            .delayUntil(function((applicationId, enabled) -> this.cloudFoundryClient.applicationsV3()
                .updateFeature(UpdateApplicationFeatureRequest.builder()
                    .applicationId(applicationId)
                    .enabled(!enabled)
                    .featureName("ssh")
                    .build())))
            .flatMap(function((applicationId, enabled) -> Mono.zip(
                Mono.just(!enabled),
                getFeatureEnabled(this.cloudFoundryClient, applicationId, "ssh"))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, applicationName, spaceId)
            .map(CreateApplicationResponse::getId);
    }

    private static Mono<String> createBuildId(CloudFoundryClient cloudFoundryClient, String packageId) {
        return requestCreateBuild(cloudFoundryClient, packageId)
            .map(CreateBuildResponse::getId);
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return requestCreateDomain(cloudFoundryClient, domainName, organizationId)
            .map(CreateDomainResponse::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(CreateOrganizationResponse::getId);
    }

    private static Mono<String> createPackageId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestCreatePackage(cloudFoundryClient, applicationId).map(CreatePackageResponse::getId);
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

    private static Mono<Boolean> getFeatureEnabled(CloudFoundryClient cloudFoundryClient, String applicationId, String featureName) {
        return requestGetFeature(cloudFoundryClient, applicationId, featureName)
            .map(GetApplicationFeatureResponse::getEnabled);
    }

    private static Mono<GetApplicationProcessResponse> requestApplicationProcess(CloudFoundryClient cloudFoundryClient, String applicationId) {
        System.out.println(Instant.now());
        return cloudFoundryClient.applicationsV3()
            .getProcess(GetApplicationProcessRequest.builder()
                .applicationId(applicationId)
                .type("web")
                .build());
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

    private static Mono<CreateBuildResponse> requestCreateBuild(CloudFoundryClient cloudFoundryClient, String packageId) {
        return cloudFoundryClient.builds()
            .create(CreateBuildRequest.builder()
                .getPackage(Relationship.builder()
                    .id(packageId)
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

    private static Mono<CreatePackageResponse> requestCreatePackage(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.packages()
            .create(CreatePackageRequest.builder()
                .relationships(PackageRelationships.builder()
                    .application(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(applicationId)
                            .build())
                        .build())
                    .build())
                .type(PackageType.BITS)
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

    private static Mono<GetApplicationResponse> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV3()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<GetApplicationResponse> requestGetApplications(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV3()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<GetBuildResponse> requestGetBuild(CloudFoundryClient cloudFoundryClient, String buildId) {
        return cloudFoundryClient.builds()
            .get(GetBuildRequest.builder()
                .buildId(buildId)
                .build());
    }

    private static Mono<GetApplicationFeatureResponse> requestGetFeature(CloudFoundryClient cloudFoundryClient, String applicationId, String featureName) {
        return cloudFoundryClient.applicationsV3()
            .getFeature(GetApplicationFeatureRequest.builder()
                .applicationId(applicationId)
                .featureName(featureName)
                .build());
    }

    private static Mono<GetPackageResponse> requestGetPackage(CloudFoundryClient cloudFoundryClient, String packageId) {
        return cloudFoundryClient.packages()
            .get(GetPackageRequest.builder()
                .packageId(packageId)
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

    private static Mono<SetApplicationCurrentDropletResponse> requestSetDroplet(CloudFoundryClient cloudFoundryClient, String applicationId, String dropletId) {
        return cloudFoundryClient.applicationsV3()
            .setCurrentDroplet(SetApplicationCurrentDropletRequest.builder()
                .applicationId(applicationId)
                .data(Relationship.builder()
                    .id(dropletId)
                    .build())
                .build());
    }

    private static Mono<StartApplicationResponse> requestStartApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV3()
            .start(StartApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<UploadPackageResponse> requestUploadPackage(CloudFoundryClient cloudFoundryClient, String packageId) {
        try {
            return cloudFoundryClient.packages()
                .upload(UploadPackageRequest.builder()
                    .bits(new ClassPathResource("test-application.zip").getFile().toPath())
                    .packageId(packageId)
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<String> uploadPackageAndCreateDropletId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return createPackageId(cloudFoundryClient, applicationId)
            .delayUntil(packageId -> requestUploadPackage(cloudFoundryClient, packageId))
            .delayUntil(packageId -> waitForPackageUpload(cloudFoundryClient, packageId))
            .flatMap(packageId -> createBuildId(cloudFoundryClient, packageId))
            .delayUntil(buildId -> waitForBuild(cloudFoundryClient, buildId))
            .flatMap(buildId -> requestGetBuild(cloudFoundryClient, buildId))
            .map(build -> build.getDroplet().getId());
    }

    private static Mono<GetBuildResponse> waitForBuild(CloudFoundryClient cloudFoundryClient, String buildId) {
        return requestGetBuild(cloudFoundryClient, buildId)
            .filter(response -> BuildState.STAGED.equals(response.getState()))
            .repeatWhenEmpty(DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private static Mono<GetPackageResponse> waitForPackageUpload(CloudFoundryClient cloudFoundryClient, String packageId) {
        return requestGetPackage(cloudFoundryClient, packageId)
            .filter(response -> PackageState.READY.equals(response.getState()))
            .repeatWhenEmpty(DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
    }

    private Mono<SetApplicationCurrentDropletResponse> prepareApplicationToStart(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return uploadPackageAndCreateDropletId(cloudFoundryClient, applicationId)
            .flatMap(dropletId -> requestSetDroplet(cloudFoundryClient, applicationId, dropletId));
    }

}
