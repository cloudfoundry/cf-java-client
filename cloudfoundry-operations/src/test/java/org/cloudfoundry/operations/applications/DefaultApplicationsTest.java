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

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DockerCredentials;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.InstanceStatistics;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.Statistics;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.applications.Usage;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesRequest;
import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesResponse;
import org.cloudfoundry.client.v2.resourcematch.Resource;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksResponse;
import org.cloudfoundry.client.v2.stacks.StackEntity;
import org.cloudfoundry.client.v3.BuildpackData;
import org.cloudfoundry.client.v3.DockerData;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.applications.ApplicationState;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.tasks.CancelTaskRequest;
import org.cloudfoundry.client.v3.tasks.CancelTaskResponse;
import org.cloudfoundry.client.v3.tasks.CreateTaskRequest;
import org.cloudfoundry.client.v3.tasks.CreateTaskResponse;
import org.cloudfoundry.client.v3.tasks.TaskResource;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.util.DateUtils;
import org.cloudfoundry.util.FluentMap;
import org.cloudfoundry.util.ResourceMatchingUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.client.v3.LifecycleType.BUILDPACK;
import static org.cloudfoundry.client.v3.LifecycleType.DOCKER;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DefaultApplicationsTest extends AbstractOperationsTest {

    private RandomWords randomWords = mock(RandomWords.class, RETURNS_SMART_NULLS);

    private final DefaultApplications applications = new DefaultApplications(Mono.just(this.cloudFoundryClient), Mono.just(this.dopplerClient), this.randomWords, Mono.just(TEST_SPACE_ID));

    @Test
    public void copySourceNoRestartOrgSpace() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestOrganizations(this.cloudFoundryClient, "test-target-organization");
        requestOrganizationSpacesByName(this.cloudFoundryClient, "test-organization-resource-metadata-id", "test-target-space");
        requestApplications(this.cloudFoundryClient, "test-target-application-name", "test-space-resource-metadata-id", "test-metadata-id");
        requestCopyBits(this.cloudFoundryClient, "test-metadata-id", "test-metadata-id");
        requestJobSuccess(this.cloudFoundryClient, "test-copy-bits-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .copySource(CopySourceApplicationRequest.builder()
                .name("test-application-name")
                .targetName("test-target-application-name")
                .targetSpace("test-target-space")
                .targetOrganization("test-target-organization")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void copySourceNoRestartSpace() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestOrganizationSpacesByName(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-target-space");
        requestApplications(this.cloudFoundryClient, "test-target-application-name", "test-space-resource-metadata-id", "test-metadata-id");
        requestCopyBits(this.cloudFoundryClient, "test-metadata-id", "test-metadata-id");
        requestJobSuccess(this.cloudFoundryClient, "test-copy-bits-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .copySource(CopySourceApplicationRequest.builder()
                .name("test-application-name")
                .targetName("test-target-application-name")
                .targetSpace("test-target-space")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void copySourceOrganizationNotFound() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestOrganizationsNotFound(this.cloudFoundryClient, "test-target-organization");

        this.applications
            .copySource(CopySourceApplicationRequest.builder()
                .name("test-application-name")
                .targetName("test-target-application-name")
                .targetOrganization("test-target-organization")
                .targetSpace("test-target-space")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Organization test-target-organization not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void copySourceRestart() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplications(this.cloudFoundryClient, "test-target-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestCopyBits(this.cloudFoundryClient, "test-metadata-id", "test-metadata-id");
        requestJobSuccess(this.cloudFoundryClient, "test-copy-bits-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-metadata-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-metadata-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-metadata-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-metadata-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .copySource(CopySourceApplicationRequest.builder()
                .name("test-application-name")
                .targetName("test-target-application-name")
                .restart(true)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void copySourceSpaceNotFound() {
        requestApplications(this.cloudFoundryClient, "test-application-name", "test-space-id", "test-metadata-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestOrganizationSpacesByNameNotFound(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-target-space");

        this.applications
            .copySource(CopySourceApplicationRequest.builder()
                .name("test-application-name")
                .targetName("test-target-application-name")
                .targetSpace("test-target-space")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space test-target-space not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAndDeleteRoutes() {
        requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID, "test-metadata-id");
        requestApplicationSummary(this.cloudFoundryClient, "test-metadata-id");
        requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
        requestApplicationServiceBindingsEmpty(this.cloudFoundryClient, "test-metadata-id");
        requestDeleteApplication(this.cloudFoundryClient, "test-metadata-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .delete(DeleteApplicationRequest.builder()
                .deleteRoutes(true)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAndDeleteRoutesFailure() {
        requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID, "test-metadata-id");
        requestApplicationSummary(this.cloudFoundryClient, "test-metadata-id");
        requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
        requestDeleteApplication(this.cloudFoundryClient, "test-metadata-id");
        requestJobFailure(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .delete(DeleteApplicationRequest.builder()
                .deleteRoutes(true)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-error-details-errorCode(1): test-error-details-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAndDoNotDeleteRoutes() {
        requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID, "test-metadata-id");
        requestApplicationSummary(this.cloudFoundryClient, "test-metadata-id");
        requestApplicationServiceBindingsEmpty(this.cloudFoundryClient, "test-metadata-id");
        requestDeleteApplication(this.cloudFoundryClient, "test-metadata-id");

        this.applications
            .delete(DeleteApplicationRequest.builder()
                .name("test-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteWithBoundRoutes() {
        requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID, "test-metadata-id");
        requestApplicationSummary(this.cloudFoundryClient, "test-metadata-id");
        requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
        requestApplicationServiceBindings(this.cloudFoundryClient, "test-metadata-id");
        requestRemoveServiceBinding(this.cloudFoundryClient, "test-metadata-id", "test-service-binding-id");
        requestDeleteApplication(this.cloudFoundryClient, "test-metadata-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .delete(DeleteApplicationRequest.builder()
                .deleteRoutes(true)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disableSsh() {
        requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "test-metadata-id");
        requestUpdateApplicationSsh(this.cloudFoundryClient, "test-metadata-id", false);

        this.applications
            .disableSsh(DisableApplicationSshRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disableSshAlreadyDisabled() {
        requestApplicationsWithSsh(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, false);

        this.applications
            .disableSsh(DisableApplicationSshRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disableSshNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);

        this.applications
            .disableSsh(DisableApplicationSshRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableSsh() {
        requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "test-metadata-id");
        requestUpdateApplicationSsh(this.cloudFoundryClient, "test-application-id", true);

        this.applications
            .enableSsh(EnableApplicationSshRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableSshAlreadyEnabled() {
        requestApplicationsWithSsh(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, true);

        this.applications
            .enableSsh(EnableApplicationSshRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void enableSshNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);

        this.applications
            .enableSsh(EnableApplicationSshRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .instanceDetail(fill(InstanceDetail.builder())
                    .index("instance-0")
                    .since(new Date(1000))
                    .state("test-application-instance-info-state")
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getApplicationManifest() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-summary-stackId");
        requestGetApplicationV3BuildpackMultiple(this.cloudFoundryClient, "test-application-id");

        this.applications
            .getApplicationManifest(GetApplicationManifestRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationManifest.builder()
                .buildpacks("test-buildpack-1", "test-buildpack-2")
                .command("test-application-summary-command")
                .disk(1)
                .environmentVariables(Collections.emptyMap())
                .healthCheckHttpEndpoint("test-application-summary-healthCheckHttpEndpoint")
                .healthCheckType(ApplicationHealthCheck.PORT)
                .instances(1)
                .memory(1)
                .name("test-application-summary-name")
                .route(Route.builder()
                    .route("test-route-host.test-domain-name/test-path")
                    .build())
                .service("test-service-instance-name")
                .stack("test-stack-entity-name")
                .timeout(1)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getApplicationManifestDocker() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationSummaryDocker(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-summary-stackId");
        requestGetApplicationV3Docker(this.cloudFoundryClient, "test-application-id");

        this.applications
            .getApplicationManifest(GetApplicationManifestRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationManifest.builder()
                .command("test-application-summary-command")
                .disk(1)
                .docker(Docker.builder()
                    .image("cloudfoundry/test-app")
                    .username("admin")
                    .password("Abcd5")
                    .build())
                .environmentVariables(Collections.emptyMap())
                .healthCheckHttpEndpoint("test-application-summary-healthCheckHttpEndpoint")
                .healthCheckType(ApplicationHealthCheck.PORT)
                .instances(1)
                .memory(1)
                .name("test-application-summary-name")
                .route(Route.builder()
                    .route("test-route-host.test-domain-name/test-path")
                    .build())
                .service("test-service-instance-name")
                .stack("test-stack-entity-name")
                .timeout(1)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getApplicationManifestNoRoutes() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationSummaryNoRoutes(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-summary-stackId");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .getApplicationManifest(GetApplicationManifestRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationManifest.builder()
                .buildpack("test-buildpack")
                .command("test-application-summary-command")
                .disk(1)
                .environmentVariables(Collections.emptyMap())
                .healthCheckHttpEndpoint("test-application-summary-healthCheckHttpEndpoint")
                .healthCheckType(ApplicationHealthCheck.PORT)
                .instances(1)
                .memory(1)
                .name("test-application-summary-name")
                .noRoute(true)
                .stack("test-stack-entity-name")
                .timeout(1)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getApplicationManifestTcp() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationSummaryTcp(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-summary-stackId");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .getApplicationManifest(GetApplicationManifestRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationManifest.builder()
                .buildpack("test-buildpack")
                .command("test-application-summary-command")
                .disk(1)
                .environmentVariables(Collections.emptyMap())
                .healthCheckHttpEndpoint("test-application-summary-healthCheckHttpEndpoint")
                .healthCheckType(ApplicationHealthCheck.PORT)
                .instances(1)
                .memory(1)
                .name("test-application-summary-name")
                .route(Route.builder()
                    .route("test-route-host.test-domain-name:999")
                    .build())
                .service("test-service-instance-name")
                .stack("test-stack-entity-name")
                .timeout(1)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getBuildpackError() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesError(this.cloudFoundryClient, "test-application-id", 170004);
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getDetectedBuildpack() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummaryDetectedBuildpack(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Docker(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-application-summary-detectedBuildpack")
                .id("test-application-summary-id")
                .instanceDetail(fill(InstanceDetail.builder())
                    .index("instance-0")
                    .since(new Date(1000))
                    .state("test-application-instance-info-state")
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name:1")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEnvironments() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id");
        requestApplicationEnvironment(this.cloudFoundryClient, "test-metadata-id");

        this.applications
            .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationEnvironments.builder()
                .running(FluentMap.<String, Object>builder()
                    .entry("running-env-name", "running-env-value")
                    .build())
                .staging(FluentMap.<String, Object>builder()
                    .entry("staging-env-name", "staging-env-value")
                    .build())
                .systemProvided(FluentMap.<String, Object>builder()
                    .entry("system-env-name", "system-env-value")
                    .build())
                .userProvided(FluentMap.<String, Object>builder()
                    .entry("env-name", "env-value")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEnvironmentsNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);

        this.applications
            .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEvents() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id");
        requestEvents(this.cloudFoundryClient, "test-metadata-id",
            fill(EventEntity.builder(), "event-")
                .timestamp("2016-02-08T15:45:59Z")
                .metadata("request", Optional.of(FluentMap.builder()
                    .entry("instances", 1)
                    .entry("memory", 2)
                    .entry("environment_json", "test-data")
                    .entry("state", "test-state")
                    .build()))
                .build());

        this.applications
            .getEvents(GetApplicationEventsRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationEvent.builder()
                .actor("test-event-actorName")
                .description("instances: 1, memory: 2, state: test-state, environment_json: test-data")
                .event("test-event-type")
                .id("test-event-id")
                .time(DateUtils.parseFromIso8601("2016-02-08T15:45:59Z"))
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEventsBadTimeSparseMetadata() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id");
        requestEvents(this.cloudFoundryClient, "test-metadata-id",
            fill(EventEntity.builder(), "event-")
                .timestamp("BAD-TIMESTAMP")
                .metadata("request", Optional.of(FluentMap.builder()
                    .entry("memory", 2)
                    .entry("environment_json", "test-data")
                    .entry("state", "test-state")
                    .build()))
                .build());

        this.applications
            .getEvents(GetApplicationEventsRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationEvent.builder()
                .actor("test-event-actorName")
                .description("memory: 2, state: test-state, environment_json: test-data")
                .event("test-event-type")
                .id("test-event-id")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEventsFoundZero() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id");
        requestEvents(this.cloudFoundryClient, "test-metadata-id");

        this.applications
            .getEvents(GetApplicationEventsRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEventsLimitZero() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id");
        requestEvents(this.cloudFoundryClient, "test-metadata-id",
            fill(EventEntity.builder(), "event-")
                .timestamp("2016-02-08T15:45:59Z")
                .metadata("request", Optional.of(FluentMap.builder()
                    .entry("instances", 1)
                    .entry("memory", 2)
                    .entry("environment_json", "test-data")
                    .entry("state", "test-state")
                    .build()))
                .build());

        this.applications
            .getEvents(GetApplicationEventsRequest.builder()
                .name("test-app")
                .maxNumberOfEvents(0)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEventsNoRequestMetadata() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id");
        requestEvents(this.cloudFoundryClient, "test-metadata-id",
            fill(EventEntity.builder(), "event-")
                .timestamp("2016-02-08T15:45:59Z")
                .metadata("index", Optional.of(1))
                .build());

        this.applications
            .getEvents(GetApplicationEventsRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationEvent.builder()
                .actor("test-event-actorName")
                .description("")
                .event("test-event-type")
                .time(DateUtils.parseFromIso8601("2016-02-08T15:45:59Z"))
                .id("test-event-id")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEventsTwo() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id");
        requestEvents(this.cloudFoundryClient, "test-metadata-id",
            fill(EventEntity.builder(), "event-")
                .timestamp("2016-02-08T15:45:59Z")
                .metadata("request", Optional.of(FluentMap.builder()
                    .entry("instances", 1)
                    .entry("memory", 2)
                    .entry("environment_json", "test-data")
                    .entry("state", "test-state")
                    .build()))
                .build(),
            fill(EventEntity.builder(), "event-")
                .timestamp("2016-02-08T15:49:07Z")
                .metadata("request", Optional.of(FluentMap.builder()
                    .entry("state", "test-state-two")
                    .build()))
                .build()
        );

        this.applications
            .getEvents(GetApplicationEventsRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationEvent.builder()
                    .actor("test-event-actorName")
                    .description("instances: 1, memory: 2, state: test-state, environment_json: test-data")
                    .event("test-event-type")
                    .id("test-event-id")
                    .time(DateUtils.parseFromIso8601("2016-02-08T15:45:59Z"))
                    .build(),
                ApplicationEvent.builder()
                    .actor("test-event-actorName")
                    .description("state: test-state-two")
                    .event("test-event-type")
                    .id("test-event-id")
                    .time(DateUtils.parseFromIso8601("2016-02-08T15:49:07Z"))
                    .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getHealthCheck() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");

        this.applications
            .getHealthCheck(GetApplicationHealthCheckRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(ApplicationHealthCheck.PORT)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getInstancesError() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesError(this.cloudFoundryClient, "test-application-id", 220001);
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getNoBuildpack() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummaryNoBuildpack(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Docker(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack(null)
                .id("test-application-summary-id")
                .instanceDetail(fill(InstanceDetail.builder())
                    .index("instance-0")
                    .since(new Date(1000))
                    .state("test-application-instance-info-state")
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name:1")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getStagingError() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesError(this.cloudFoundryClient, "test-application-id", 170002);
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getStoppedError() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatisticsError(this.cloudFoundryClient, "test-application-id", 200003);
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .instanceDetail(InstanceDetail.builder()
                    .index("instance-0")
                    .since(new Date(1000))
                    .state("test-application-instance-info-state")
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getWithEmptyInstance() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationEmptyInstance(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .instanceDetail(fill(InstanceDetail.builder())
                    .index("instance-0")
                    .since(null)
                    .state(null)
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getWithEmptyInstanceStats() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationEmptyStats(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .instanceDetail(InstanceDetail.builder()
                    .index("instance-0")
                    .since(new Date(1000))
                    .state("test-application-instance-info-state")
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getWithNoInstances() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationStatistics(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationNoInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getWithNullStats() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationNullStats(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .instanceDetail(InstanceDetail.builder()
                    .index("instance-0")
                    .since(new Date(1000))
                    .state("test-application-instance-info-state")
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getWithNullUsage() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-application-id");
        requestApplicationNullUsage(this.cloudFoundryClient, "test-application-id");
        requestStack(this.cloudFoundryClient, "test-application-stackId");
        requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        requestGetApplicationV3Buildpack(this.cloudFoundryClient, "test-application-id");

        this.applications
            .get(GetApplicationRequest.builder()
                .name("test-app")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationDetail.builder())
                .buildpack("test-buildpack")
                .id("test-application-summary-id")
                .instanceDetail(InstanceDetail.builder()
                    .index("instance-0")
                    .diskQuota(1L)
                    .memoryQuota(1L)
                    .since(new Date(1000))
                    .state("test-application-instance-info-state")
                    .build())
                .lastUploaded(new Date(0))
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .stack("test-stack-entity-name")
                .url("test-route-host.test-domain-name/test-path")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        requestSpaceSummary(this.cloudFoundryClient, TEST_SPACE_ID);

        this.applications.list()
            .as(StepVerifier::create)
            .expectNext(fill(ApplicationSummary.builder())
                .id("test-application-summary-id")
                .name("test-application-summary-name")
                .requestedState("test-application-summary-state")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listTasks() {
        requestApplicationsV3(this.cloudFoundryClient, "test-name", TEST_SPACE_ID, "test-metadata-id");
        requestListTasks(this.cloudFoundryClient, "test-metadata-id");

        this.applications
            .listTasks(ListApplicationTasksRequest.builder()
                .name("test-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(Task.builder())
                .command("test-command")
                .state(TaskState.CANCELING)
                .name("test-name")
                .sequenceId(1)
                .startTime("test-createdAt")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void logs() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestLogsStream(this.dopplerClient, "test-metadata-id");

        this.applications
            .logs(LogsRequest.builder()
                .name("test-application-name")
                .recent(false)
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(LogMessage.builder(), "log-message-")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void logsNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);

        this.applications
            .logs(LogsRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-application-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void logsRecent() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestLogsRecent(this.dopplerClient, "test-metadata-id");

        this.applications
            .logs(LogsRequest.builder()
                .name("test-application-name")
                .recent(true)
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(LogMessage.builder(), "log-message-")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void logsRecentNotSet() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestLogsStream(this.dopplerClient, "test-metadata-id");

        this.applications
            .logs(LogsRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(LogMessage.builder(), "log-message-")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushDocker() {
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateDockerApplication(this.cloudFoundryClient, PushApplicationRequest.builder()
            .dockerImage("cloudfoundry/lattice-app")
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestPrivateDomainNotFound(this.cloudFoundryClient, "test-shared-domain", TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomain(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestGetSharedDomain(this.cloudFoundryClient, "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .dockerImage("cloudfoundry/lattice-app")
                .domain("test-shared-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushDockerPrivateRepository() {
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateDockerApplication(this.cloudFoundryClient, PushApplicationRequest.builder()
            .dockerImage("cloudfoundry/lattice-app")
            .dockerPassword("test-docker-password")
            .dockerUsername("test-docker-username")
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestPrivateDomainNotFound(this.cloudFoundryClient, "test-shared-domain", TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomain(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestGetSharedDomain(this.cloudFoundryClient, "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .dockerImage("cloudfoundry/lattice-app")
                .dockerPassword("test-docker-password")
                .dockerUsername("test-docker-username")
                .domain("test-shared-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushDomainNotFound() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-application-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomainsEmpty(this.cloudFoundryClient);
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));

        this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Domain test-shared-domain not found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushExistingApplication() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID, "test-application-id");
        requestUpdateApplication(this.cloudFoundryClient, "test-application-id", ApplicationManifest.builder()
            .path(testApplication)
            .environmentVariable("test-var", "test-value")
            .domain("test-shared-domain")
            .name("test-name")
            .build(), null);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushExistingApplicationWithEnvironmentVariables() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID, "test-application-id", Collections.singletonMap("test-key-1", "test-value-1"));
        requestUpdateApplication(this.cloudFoundryClient, "test-application-id", ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .environmentVariable("test-key-1", "test-value-1")
            .environmentVariable("test-key-2", "test-value-2")
            .name("test-name")
            .build(), null);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(testApplication)
                    .domain("test-shared-domain")
                    .environmentVariable("test-key-2", "test-value-2")
                    .name("test-name")
                    .build())
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushExistingRouteWithHost() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .host("test-host")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutes(this.cloudFoundryClient, "test-shared-domain-id", "test-host", null, "test-route-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .host("test-host")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushExistingRouteWithNoHost() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .noHostname(true)
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutes(this.cloudFoundryClient, "test-shared-domain-id", "", null, "test-route-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .noHostname(true)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushInvalidStack() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestStackIdEmpty(this.cloudFoundryClient, "invalid-stack");

        this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .name("test-name")
                .stack("invalid-stack")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Stack invalid-stack does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushMultipleRoutes() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test1", null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test1", null, null, TEST_SPACE_ID, "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test2", null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test2", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(testApplication)
                    .name("test-name")
                    .route(Route.builder()
                        .route("test1.test-shared-domain")
                        .build())
                    .route(Route.builder()
                        .route("test2.test-shared-domain")
                        .build())
                    .build())
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNewApplication() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .healthCheckHttpEndpoint("/health")
            .healthCheckType(ApplicationHealthCheck.HTTP)
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-name")
                .healthCheckHttpEndpoint("/health")
                .healthCheckType(ApplicationHealthCheck.HTTP)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNewApplicationWithEnvironmentVariables() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplicationsEmpty(this.cloudFoundryClient, "test.name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .environmentVariable("test-key", "test-value")
            .domain("test-shared-domain")
            .name("test.name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "testname", null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "testname", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(testApplication)
                    .domain("test-shared-domain")
                    .environmentVariable("test-key", "test-value")
                    .name("test.name")
                    .build())
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNewRouteWithHost() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .host("test-host")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-host", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-host", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .host("test-host")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNewRouteWithNoHost() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .noHostname(true)
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "", null, null);
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .noHostname(true)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoDomainNoneFound() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomainsEmpty(this.cloudFoundryClient);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutesEmpty(this.cloudFoundryClient, "test-application-id");

        this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .name("test-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("No default domain found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoDomainPrivate() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-private-domain-id");
        requestSharedDomainsEmpty(this.cloudFoundryClient);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutesEmpty(this.cloudFoundryClient, "test-application-id");

        this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .name("test-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("No default domain found"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoDomainShared() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutesEmpty(this.cloudFoundryClient, "test-application-id");
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoHostnameFlag() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutesEmpty(this.cloudFoundryClient, "test-application-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "", null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(testApplication)
                    .name("test-name")
                    .noHostname(true)
                    .build())
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoInstances() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .name("test-name")
            .instances(0)
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutesEmpty(this.cloudFoundryClient, "test-application-id");
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED", 0);

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .name("test-name")
                .instances(0)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoRoute() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .noRoute(true)
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutesEmpty(this.cloudFoundryClient, "test-application-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .noRoute(true)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoRouteFlag() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRemoveApplicationRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(testApplication)
                    .name("test-name")
                    .noRoute(true)
                    .build())
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushNoStart() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutesEmpty(this.cloudFoundryClient, "test-application-id");
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .name("test-name")
                .noStart(true)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushPrivateDomain() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-private-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-private-domain-id");
        requestSharedDomainsEmpty(this.cloudFoundryClient);
        requestRoutesEmpty(this.cloudFoundryClient, "test-private-domain-id", "test-name", null, null);
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-private-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-private-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushRandomRoute() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-application.name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-application.name")
            .randomRoute(true)
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        provideRandomWords(this.randomWords);
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-applicationname-test-adjective-test-noun", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(
            new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-applicationname-test-adjective-test-noun", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-application.name")
                .randomRoute(true)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushSharedDomain() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushStartFailsRunning() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesFailingTotal(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-name failed during start"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushStartFailsStaging() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-private-domain-id");
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplicationFailing(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-name failed during staging"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushTcpRandomRoute() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestTcpDomains(this.cloudFoundryClient, "test-tcp-domain", "test-tcp-domain-id");
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-tcp-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-tcp-domain-id", null, null, null);
        requestCreateRoute(this.cloudFoundryClient, "test-tcp-domain-id", true, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-tcp-domain")
                .randomRoute(true)
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushTcpRoute() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestTcpDomains(this.cloudFoundryClient, "test-tcp-domain", "test-tcp-domain-id");
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-tcp-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-tcp-domain-id", null, 61001, null);
        requestCreateRoute(this.cloudFoundryClient, "test-tcp-domain-id", null, 61001, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(testApplication)
                    .route(Route.builder()
                        .route("test-tcp-domain:61001")
                        .build())
                    .name("test-name")
                    .build())
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void pushUploadFails() throws IOException {
        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        requestApplicationsEmpty(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
        requestCreateApplication(this.cloudFoundryClient, ApplicationManifest.builder()
            .path(testApplication)
            .domain("test-shared-domain")
            .name("test-name")
            .build(), TEST_SPACE_ID, null, "test-application-id");
        requestSpace(this.cloudFoundryClient, TEST_SPACE_ID, TEST_ORGANIZATION_ID);
        requestPrivateDomainsEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestApplicationRoutes(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestSharedDomains(this.cloudFoundryClient, "test-shared-domain", "test-shared-domain-id");
        requestRoutesEmpty(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null);
        requestListMatchingResources(this.cloudFoundryClient, Arrays.asList(new ResourceMatchingUtils.ArtifactMetadata("da39a3ee5e6b4b0d3255bfef95601890afd80709", "Staticfile", "100644", 0),
            new ResourceMatchingUtils.ArtifactMetadata("45044a6ddbfe11415a8f8a6219de68a2c66b496b", "index.html", "100644", 178)));
        requestCreateRoute(this.cloudFoundryClient, "test-shared-domain-id", "test-name", null, null, TEST_SPACE_ID, "test-route-id");
        requestAssociateRoute(this.cloudFoundryClient, "test-application-id", "test-route-id");
        requestUpload(this.cloudFoundryClient, "test-application-id", testApplication, "test-job-id");
        requestJobFailure(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .push(PushApplicationRequest.builder()
                .path(testApplication)
                .domain("test-shared-domain")
                .name("test-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-error-details-errorCode(1): test-error-details-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void rename() {
        requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "test-metadata-id");
        requestUpdateApplicationRename(this.cloudFoundryClient, "test-metadata-id", "test-new-app-name");

        this.applications
            .rename(RenameApplicationRequest.builder()
                .name("test-app-name")
                .newName("test-new-app-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void renameNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);

        this.applications
            .rename(RenameApplicationRequest.builder()
                .name("test-app-name")
                .newName("test-new-app-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restage() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestRestageApplication(this.cloudFoundryClient, "test-metadata-id");
        requestGetApplication(this.cloudFoundryClient, "test-metadata-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-metadata-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restage(RestageApplicationRequest.builder()
                .name("test-application-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restageInvalidApplication() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);

        this.applications
            .restage(RestageApplicationRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-application-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restageStagingFailure() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestRestageApplication(this.cloudFoundryClient, "test-metadata-id");
        requestGetApplicationFailing(this.cloudFoundryClient, "test-metadata-id");

        this.applications
            .restage(RestageApplicationRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-application-name failed during staging"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restageStartingFailurePartial() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestRestageApplication(this.cloudFoundryClient, "test-metadata-id");
        requestGetApplication(this.cloudFoundryClient, "test-metadata-id");
        requestApplicationInstancesFailingPartial(this.cloudFoundryClient, "test-metadata-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restage(RestageApplicationRequest.builder()
                .name("test-application-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restageStartingFailureTotal() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestRestageApplication(this.cloudFoundryClient, "test-metadata-id");
        requestGetApplication(this.cloudFoundryClient, "test-metadata-id");
        requestApplicationInstancesFailingTotal(this.cloudFoundryClient, "test-metadata-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restage(RestageApplicationRequest.builder()
                .name("test-application-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-application-name failed during start"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restageTimeout() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestRestageApplication(this.cloudFoundryClient, "test-metadata-id");
        requestGetApplicationTimeout(this.cloudFoundryClient, "test-metadata-id");

        this.applications
            .restage(RestageApplicationRequest.builder()
                .name("test-application-name")
                .stagingTimeout(Duration.ofSeconds(1))
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-application-name timed out during staging"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restartFailurePartial() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesFailingPartial(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restart(RestartApplicationRequest.builder()
                .name("test-app-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restartFailureTotal() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesFailingTotal(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restart(RestartApplicationRequest.builder()
                .name("test-app-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-app-name failed during start"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restartInstance() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestTerminateApplicationInstance(this.cloudFoundryClient, "test-metadata-id", "0");

        StepVerifier.withVirtualTime(() -> this.applications
            .restartInstance(RestartApplicationInstanceRequest.builder()
                .name("test-application-name")
                .instanceIndex(0)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restartNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-non-existent-app-name", TEST_SPACE_ID);

        this.applications
            .restart(RestartApplicationRequest.builder()
                .name("test-non-existent-app-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-non-existent-app-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restartNotStartedAndNotStopped() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "unknown-state");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restart(RestartApplicationRequest.builder()
                .name("test-app-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restartStarted() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restart(RestartApplicationRequest.builder()
                .name("test-app-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void restartStopped() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .restart(RestartApplicationRequest.builder()
                .name("test-app-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void runTask() {
        requestApplicationsV3(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestRunTask(this.cloudFoundryClient, "test-metadata-id");

        this.applications
            .runTask(RunApplicationTaskRequest.builder()
                .applicationName("test-application-name")
                .command("test-command")
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(Task.builder())
                .command("test-command")
                .state(TaskState.CANCELING)
                .name("test-name")
                .sequenceId(1)
                .startTime("test-createdAt")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void scaleDiskAndInstancesNotStarted() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STOPPED");
        requestUpdateApplicationScale(this.cloudFoundryClient, "test-application-id", 2048, 2, null);

        this.applications
            .scale(ScaleApplicationRequest.builder()
                .name("test-app-name")
                .instances(2)
                .diskLimit(2048)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void scaleDiskAndInstancesStarted() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
        requestUpdateApplicationScale(this.cloudFoundryClient, "test-application-id", 2048, 2, null);
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");

        this.applications
            .scale(ScaleApplicationRequest.builder()
                .name("test-app-name")
                .instances(2)
                .diskLimit(2048)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void scaleInstances() {
        requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "test-metadata-id");
        requestUpdateApplicationScale(this.cloudFoundryClient, "test-metadata-id", null, 2, null);

        this.applications
            .scale(ScaleApplicationRequest.builder()
                .name("test-app-name")
                .instances(2)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void scaleInstancesNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);

        this.applications
            .scale(ScaleApplicationRequest.builder()
                .name("test-app-name")
                .instances(2)
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void scaleNoChange() {
        requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "test-metadata-id");

        this.applications
            .scale(ScaleApplicationRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setEnvironmentVariable() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id",
            FluentMap.<String, Object>builder()
                .entry("test-var", "test-value")
                .entry("test-var2", "test-value2")
                .build());
        requestUpdateApplicationEnvironment(this.cloudFoundryClient, "test-metadata-id",
            FluentMap.<String, Object>builder()
                .entry("test-var", "test-value")
                .entry("test-var2", "test-value2")
                .entry("test-var-name", "test-var-value")
                .build()
        );

        this.applications
            .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                .name("test-app")
                .variableName("test-var-name")
                .variableValue("test-var-value")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setEnvironmentVariableNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);

        this.applications
            .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                .name("test-app")
                .variableName("test-var-name")
                .variableValue("test-var-value")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setHealthCheck() {
        requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-application-id");
        requestUpdateApplicationHealthCheck(this.cloudFoundryClient, "test-application-id", ApplicationHealthCheck.PORT);

        this.applications
            .setHealthCheck(SetApplicationHealthCheckRequest.builder()
                .name("test-application-name")
                .type(ApplicationHealthCheck.PORT)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void sshEnabled() {
        requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "test-metadata-id");

        this.applications
            .sshEnabled(ApplicationSshEnabledRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void sshEnabledNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);

        this.applications
            .sshEnabled(ApplicationSshEnabledRequest.builder()
                .name("test-app-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void startApplicationFailurePartial() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesFailingPartial(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .start(StartApplicationRequest.builder()
                .name("test-application-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void startApplicationFailureTotal() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesFailingTotal(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .start(StartApplicationRequest.builder()
                .name("test-application-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-application-name failed during start"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void startApplicationTimeout() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesTimeout(this.cloudFoundryClient, "test-application-id");

        this.applications
            .start(StartApplicationRequest.builder()
                .name("test-application-name")
                .startupTimeout(Duration.ofSeconds(1))
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalStateException.class).hasMessage("Application test-application-name timed out during start"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void startInvalidApplication() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);

        this.applications
            .start(StartApplicationRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-application-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void startStartedApplication() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STARTED");

        this.applications
            .start(StartApplicationRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void startStoppedApplication() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        requestGetApplication(this.cloudFoundryClient, "test-application-id");
        requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");

        StepVerifier.withVirtualTime(() -> this.applications
            .start(StartApplicationRequest.builder()
                .name("test-application-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void stopInvalidApplication() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);

        this.applications
            .stop(StopApplicationRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-application-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void stopStartedApplication() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STARTED");
        requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");

        this.applications
            .stop(StopApplicationRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void stopStoppedApplication() {
        requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");

        this.applications
            .stop(StopApplicationRequest.builder()
                .name("test-application-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void terminateTask() {
        requestApplicationsV3(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestListTasks(this.cloudFoundryClient, "test-metadata-id", 1);
        requestCancelTask(this.cloudFoundryClient, "test-id");

        this.applications
            .terminateTask(TerminateApplicationTaskRequest.builder()
                .applicationName("test-application-name")
                .sequenceId(1)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void terminateTaskNoTask() {
        requestApplicationsV3(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "test-metadata-id");
        requestListTasksEmpty(this.cloudFoundryClient, "test-metadata-id", 1);
        requestCancelTask(this.cloudFoundryClient, "test-id");

        this.applications
            .terminateTask(TerminateApplicationTaskRequest.builder()
                .applicationName("test-application-name")
                .sequenceId(1)
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Task with sequence id of 1 does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetEnvironmentVariable() {
        requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID, "test-metadata-id",
            FluentMap.<String, Object>builder()
                .entry("test-var", "test-value")
                .entry("test-var2", "test-value2")
                .entry("test-var-name", "test-var-value")
                .build());
        requestUpdateApplicationEnvironment(this.cloudFoundryClient, "test-metadata-id",
            FluentMap.<String, Object>builder()
                .entry("test-var2", "test-value2")
                .entry("test-var-name", "test-var-value")
                .build());

        this.applications
            .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                .name("test-app")
                .variableName("test-var")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unsetEnvironmentVariableNoApp() {
        requestApplicationsEmpty(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);

        this.applications
            .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                .name("test-app")
                .variableName("test-var")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Application test-app does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    private static void provideRandomWords(RandomWords randomWords) {
        when(randomWords.getAdjective()).thenReturn("test-adjective");
        when(randomWords.getNoun()).thenReturn("test-noun");
    }

    private static void requestApplicationEmptyInstance(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationInstancesResponse.builder()
                    .instance("instance-0", ApplicationInstanceInfo.builder()
                        .build())
                    .build()));
    }

    private static void requestApplicationEmptyStats(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .statistics(ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationStatisticsResponse.builder()
                    .build()));
    }

    private static void requestApplicationEnvironment(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .environment(ApplicationEnvironmentRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationEnvironmentResponse.builder()
                    .runningEnvironmentJson("running-env-name", "running-env-value")
                    .applicationEnvironmentJson("application-env-name", "application-env-value")
                    .stagingEnvironmentJson("staging-env-name", "staging-env-value")
                    .environmentJson("env-name", "env-value")
                    .systemEnvironmentJson("system-env-name", "system-env-value")
                    .build()));
    }

    private static void requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationInstancesResponse.builder()
                    .instance("instance-0", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
                        .build())
                    .build()));
    }

    private static void requestApplicationInstancesError(CloudFoundryClient cloudFoundryClient, String applicationId, Integer code) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .error(new ClientV2Exception(null, code, "test-exception-description", "test-exception-errorCode")));
    }

    private static void requestApplicationInstancesFailingPartial(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(ApplicationInstancesResponse.builder(), "application-instances-")
                    .instance("instance-0", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
                        .state("RUNNING")
                        .build())
                    .instance("instance-1", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
                        .state("FLAPPING")
                        .build())
                    .build()));
    }

    private static void requestApplicationInstancesFailingTotal(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(ApplicationInstancesResponse.builder(), "application-instances-")
                    .instance("instance-0", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
                        .state("FLAPPING")
                        .build())
                    .build()));
    }

    private static void requestApplicationInstancesRunning(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(ApplicationInstancesResponse.builder(), "application-instances-")
                    .instance("instance-0", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
                        .state("RUNNING")
                        .build())
                    .build()));
    }

    private static void requestApplicationInstancesTimeout(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(ApplicationInstancesResponse.builder(), "application-instances-")
                    .instance("instance-0", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
                        .state("STARTING")
                        .build())
                    .build()));
    }

    private static void requestApplicationNoInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationInstancesResponse.builder().build()));
    }

    private static void requestApplicationNullStats(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .statistics(ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationStatisticsResponse.builder()
                    .instance("instance-0", fill(InstanceStatistics.builder(), "instance-statistics-")
                        .statistics(null)
                        .build())
                    .build()));
    }

    private static void requestApplicationNullUsage(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .statistics(ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationStatisticsResponse.builder()
                    .instance("instance-0", fill(InstanceStatistics.builder(), "instance-statistics-")
                        .statistics(fill(Statistics.builder(), "statistics-")
                            .usage(null)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestApplicationRoutes(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        when(cloudFoundryClient.applicationsV2()
            .listRoutes(ListApplicationRoutesRequest.builder()
                .applicationId(applicationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListApplicationRoutesResponse.builder())
                    .resource(fill(RouteResource.builder())
                        .metadata(fill(Metadata.builder())
                            .id(routeId)
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestApplicationRoutesEmpty(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .listRoutes(ListApplicationRoutesRequest.builder()
                .applicationId(applicationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListApplicationRoutesResponse.builder())
                    .totalPages(1)
                    .build()));
    }

    private static void requestApplicationServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                .applicationId(applicationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListApplicationServiceBindingsResponse.builder(), "test-service-binding-")
                    .resource(fill(ServiceBindingResource.builder())
                        .metadata(fill(Metadata.builder())
                            .id("test-service-binding-id")
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestApplicationServiceBindingsEmpty(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                .applicationId(applicationId)
                .page(1)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestApplicationStatistics(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .statistics(ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationStatisticsResponse.builder()
                    .instance("instance-0", fill(InstanceStatistics.builder(), "instance-statistics-")
                        .statistics(fill(Statistics.builder(), "statistics-")
                            .usage(fill(Usage.builder(), "usage-")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestApplicationStatisticsError(CloudFoundryClient cloudFoundryClient, String applicationId, Integer code) {
        when(cloudFoundryClient.applicationsV2()
            .statistics(ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .error(new ClientV2Exception(null, code, "test-exception-description", "test-exception-errorCode")));
    }

    private static void requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .buildpack("test-buildpack")
                    .healthCheckType("port")
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .route(fill(org.cloudfoundry.client.v2.routes.Route.builder(), "route-")
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-").build())
                        .path("/test-path")
                        .port(null)
                        .build())
                    .dockerImage(null)
                    .dockerCredentials(null)
                    .service(ServiceInstance.builder()
                        .name("test-service-instance-name")
                        .build())
                    .build()));
    }

    private static void requestApplicationSummaryDetectedBuildpack(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .route(fill(org.cloudfoundry.client.v2.routes.Route.builder(), "route-")
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-")
                            .build())
                        .build())
                    .buildpack(null)
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .build()));
    }

    private static void requestApplicationSummaryDocker(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .buildpack(null)
                    .healthCheckType("port")
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .route(fill(org.cloudfoundry.client.v2.routes.Route.builder(), "route-")
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-").build())
                        .path("/test-path")
                        .port(null)
                        .build())
                    .dockerImage("cloudfoundry/test-app")
                    .dockerCredentials(DockerCredentials.builder()
                        .username("admin")
                        .password("Abcd5")
                        .build())
                    .service(ServiceInstance.builder()
                        .name("test-service-instance-name")
                        .build())
                    .build()));
    }

    private static void requestApplicationSummaryNoBuildpack(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .route(fill(org.cloudfoundry.client.v2.routes.Route.builder(), "route-")
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-")
                            .build())
                        .build())
                    .buildpack(null)
                    .detectedBuildpack(null)
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .build()));
    }

    private static void requestApplicationSummaryNoRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .buildpack("test-buildpack")
                    .healthCheckType("port")
                    .dockerImage(null)
                    .dockerCredentials(null)
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .build()));
    }

    private static void requestApplicationSummaryTcp(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .buildpack("test-buildpack")
                    .healthCheckType("port")
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .route(fill(org.cloudfoundry.client.v2.routes.Route.builder(), "route-")
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-").build())
                        .port(999)
                        .build())
                    .dockerImage(null)
                    .dockerCredentials(null)
                    .service(ServiceInstance.builder()
                        .name("test-service-instance-name")
                        .build())
                    .build()));
    }

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, String application, String spaceId, String applicationId) {
        requestApplications(cloudFoundryClient, application, spaceId, applicationId, Collections.singletonMap("test-var", "test-value"));
    }

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, String application, String spaceId, String applicationId, Map<String, Object> envResponse) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(application)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .resource(ApplicationResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(applicationId)
                            .build())
                        .entity(fill(ApplicationEntity.builder(), "application-")
                            .environmentJsons(envResponse)
                            .healthCheckType(ApplicationHealthCheck.PORT.getValue())
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestApplicationsEmpty(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(application)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .build()));
    }

    private static void requestApplicationsSpecificState(CloudFoundryClient cloudFoundryClient, String application, String spaceId, String stateReturned) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(application)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .entity(fill(ApplicationEntity.builder(), "application-entity-")
                            .state(stateReturned)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestApplicationsV3(CloudFoundryClient cloudFoundryClient, String application, String spaceId, String applicationId) {
        when(cloudFoundryClient.applicationsV3()
            .list(ListApplicationsRequest.builder()
                .name(application)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListApplicationsResponse.builder())
                    .resource(org.cloudfoundry.client.v3.applications.ApplicationResource.builder()
                        .createdAt("test-created-at")
                        .id(applicationId)
                        .lifecycle(Lifecycle.builder()
                            .data(BuildpackData.builder()
                                .buildpack("test-buildpack")
                                .build())
                            .type(BUILDPACK)
                            .build())
                        .name("test-name")
                        .state(ApplicationState.STOPPED)
                        .updatedAt("test-updated-at")
                        .build())
                    .build()));
    }

    private static void requestApplicationsWithSsh(CloudFoundryClient cloudFoundryClient, String application, String spaceId, Boolean sshEnabled) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(application)
                .spaceId(spaceId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .resource(ApplicationResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id("test-application-id")
                            .build())
                        .entity(fill(ApplicationEntity.builder(), "application-")
                            .environmentJson("test-var", "test-value")
                            .enableSsh(sshEnabled)
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        when(cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestCancelTask(CloudFoundryClient cloudFoundryClient, String taskId) {
        when(cloudFoundryClient.tasks()
            .cancel(CancelTaskRequest.builder()
                .taskId(taskId)
                .build()))
            .thenReturn(Mono
                .just(fill(CancelTaskResponse.builder())
                    .build()));
    }

    private static void requestCopyBits(CloudFoundryClient cloudFoundryClient, String sourceApplicationId, String targetApplicationId) {
        when(cloudFoundryClient.applicationsV2()
            .copy(CopyApplicationRequest.builder()
                .applicationId(targetApplicationId)
                .sourceApplicationId(sourceApplicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(CopyApplicationResponse.builder(), "copy-bits-")
                    .build()));
    }

    private static void requestCreateApplication(CloudFoundryClient cloudFoundryClient, ApplicationManifest manifest, String spaceId, String stackId, String applicationId) {
        CreateApplicationRequest.Builder builder = CreateApplicationRequest.builder()
            .command(manifest.getCommand())
            .diskQuota(manifest.getDisk())
            .environmentJsons(manifest.getEnvironmentVariables())
            .healthCheckTimeout(manifest.getTimeout())
            .healthCheckHttpEndpoint(manifest.getHealthCheckHttpEndpoint())
            .healthCheckType(Optional.ofNullable(manifest.getHealthCheckType()).map(ApplicationHealthCheck::getValue).orElse(null))
            .instances(manifest.getInstances())
            .memory(manifest.getMemory())
            .name(manifest.getName())
            .spaceId(spaceId)
            .stackId(stackId);

        if (manifest.getBuildpacks() != null) {
            builder.buildpack(manifest.getBuildpacks().get(0));
        }

        when(cloudFoundryClient.applicationsV2()
            .create(builder.build()))
            .thenReturn(Mono
                .just(fill(CreateApplicationResponse.builder(), "create-")
                    .metadata(fill(Metadata.builder())
                        .id(applicationId)
                        .build())
                    .build()));
    }

    private static void requestCreateDockerApplication(CloudFoundryClient cloudFoundryClient, PushApplicationRequest request, String spaceId, String stackId, String applicationId) {
        CreateApplicationRequest.Builder requestBuilder = CreateApplicationRequest.builder();
        requestBuilder.dockerCredentials(DockerCredentials.builder().username(request.getDockerUsername()).password(request.getDockerPassword()).build());

        when(cloudFoundryClient.applicationsV2()
            .create(requestBuilder
                .command(request.getCommand())
                .diego(true)
                .diskQuota(request.getDiskQuota())
                .dockerImage(request.getDockerImage())
                .healthCheckTimeout(request.getTimeout())
                .healthCheckType(Optional.ofNullable(request.getHealthCheckType()).map(ApplicationHealthCheck::getValue).orElse(null))
                .instances(request.getInstances())
                .memory(request.getMemory())
                .name(request.getName())
                .spaceId(spaceId)
                .stackId(stackId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateApplicationResponse.builder(), "create-")
                    .metadata(fill(Metadata.builder())
                        .id(applicationId)
                        .build())
                    .build()));
    }

    private static void requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path, Integer port, String spaceId, String routeId) {
        when(cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(path)
                .port(port)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateRouteResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(routeId)
                        .build())
                    .entity(fill(RouteEntity.builder())
                        .build())
                    .build()));
    }

    private static void requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, Boolean generatePort, Integer port, String spaceId, String routeId) {
        when(cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .generatePort(generatePort)
                .port(port)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateRouteResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(routeId)
                        .build())
                    .entity(fill(RouteEntity.builder())
                        .build())
                    .build()));
    }

    private static void requestDeleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .delete(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
                .applicationId(applicationId)
                .build())).
            thenReturn(Mono.empty());
    }

    private static void requestDeleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        when(cloudFoundryClient.routes()
            .delete(org.cloudfoundry.client.v2.routes.DeleteRouteRequest.builder()
                .async(true)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteRouteResponse.builder())
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));
    }

    private static void requestEvents(CloudFoundryClient cloudFoundryClient, String applicationId, EventEntity... entities) {
        ListEventsResponse.Builder responseBuilder = fill(ListEventsResponse.builder());

        for (EventEntity entity : entities) {
            responseBuilder.resource(EventResource.builder()
                .metadata(fill(Metadata.builder())
                    .id("test-event-id")
                    .build())
                .entity(entity)
                .build());
        }

        when(cloudFoundryClient.events()
            .list(ListEventsRequest.builder()
                .actee(applicationId)
                .orderDirection(OrderDirection.DESCENDING)
                .resultsPerPage(50)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(responseBuilder
                    .totalPages(1)
                    .build()));
    }

    private static void requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .get(org.cloudfoundry.client.v2.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetApplicationResponse>>() {

                    private final Queue<GetApplicationResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetApplicationResponse.builder(), "job-")
                            .entity(fill(ApplicationEntity.builder())
                                .packageState("STAGING")
                                .build())
                            .build(),
                        fill(GetApplicationResponse.builder(), "job-")
                            .entity(fill(ApplicationEntity.builder())
                                .packageState("STAGED")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetApplicationResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestGetApplicationFailing(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .get(org.cloudfoundry.client.v2.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetApplicationResponse.builder())
                    .entity(fill(ApplicationEntity.builder())
                        .packageState("FAILED")
                        .build())
                    .build()));
    }

    private static void requestGetApplicationTimeout(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .get(org.cloudfoundry.client.v2.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetApplicationResponse.builder())
                    .entity(fill(ApplicationEntity.builder())
                        .packageState("STAGING")
                        .build())
                    .build()));
    }

    private static void requestGetApplicationV3Buildpack(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV3()
            .get(org.cloudfoundry.client.v3.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.client.v3.applications.GetApplicationResponse.builder()
                    .createdAt("test-created-at")
                    .id(applicationId)
                    .lifecycle(Lifecycle.builder()
                        .data(BuildpackData.builder()
                            .buildpack("test-buildpack")
                            .build())
                        .type(BUILDPACK)
                        .build())
                    .name("test-name")
                    .state(ApplicationState.STOPPED)
                    .updatedAt("test-updated-at")
                    .build()));
    }

    private static void requestGetApplicationV3BuildpackMultiple(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV3()
            .get(org.cloudfoundry.client.v3.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.client.v3.applications.GetApplicationResponse.builder()
                    .createdAt("test-created-at")
                    .id(applicationId)
                    .lifecycle(Lifecycle.builder()
                        .data(BuildpackData.builder()
                            .buildpacks("test-buildpack-1", "test-buildpack-2")
                            .build())
                        .type(BUILDPACK)
                        .build())
                    .name("test-name")
                    .state(ApplicationState.STOPPED)
                    .updatedAt("test-updated-at")
                    .build()));
    }

    private static void requestGetApplicationV3Docker(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV3()
            .get(org.cloudfoundry.client.v3.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.client.v3.applications.GetApplicationResponse.builder()
                    .createdAt("test-created-at")
                    .id(applicationId)
                    .lifecycle(Lifecycle.builder()
                        .data(DockerData.builder()
                            .build())
                        .type(DOCKER)
                        .build())
                    .name("test-name")
                    .state(ApplicationState.STOPPED)
                    .updatedAt("test-updated-at")
                    .build()));

    }

    private static void requestGetSharedDomain(CloudFoundryClient cloudFoundryClient, String domainId) {
        when(cloudFoundryClient.sharedDomains()
            .get(GetSharedDomainRequest.builder()
                .sharedDomainId(domainId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSharedDomainResponse.builder())
                    .entity(SharedDomainEntity.builder()
                        .build())
                    .build()));
    }

    private static void requestJobFailure(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .errorDetails(fill(ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestListMatchingResources(CloudFoundryClient cloudFoundryClient, Collection<ResourceMatchingUtils.ArtifactMetadata> artifactMetadatas) {
        ListMatchingResourcesRequest request = artifactMetadatas.stream()
            .reduce(ListMatchingResourcesRequest.builder(), (builder, artifactMetadata) -> builder.resource(Resource.builder()
                .hash(artifactMetadata.getHash())
                .mode(artifactMetadata.getPermissions())
                .size(artifactMetadata.getSize())
                .build()), (a, b) -> a.addAllResources(b.build().getResources()))
            .build();

        when(cloudFoundryClient.resourceMatch()
            .list(request))
            .thenReturn(Mono
                .just(ListMatchingResourcesResponse.builder()
                    .build()));
    }

    private static void requestListTasks(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV3()
            .listTasks(org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest.builder()
                .applicationId(applicationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse.builder())
                    .resource(fill(TaskResource.builder())
                        .build())
                    .build()));
    }

    private static void requestListTasks(CloudFoundryClient cloudFoundryClient, String applicationId, Integer sequenceId) {
        when(cloudFoundryClient.applicationsV3()
            .listTasks(org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest.builder()
                .applicationId(applicationId)
                .page(1)
                .sequenceId(sequenceId.toString())
                .build()))
            .thenReturn(Mono
                .just(fill(org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse.builder())
                    .resource(fill(TaskResource.builder())
                        .build())
                    .build()));
    }

    private static void requestListTasksEmpty(CloudFoundryClient cloudFoundryClient, String applicationId, Integer sequenceId) {
        when(cloudFoundryClient.applicationsV3()
            .listTasks(org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest.builder()
                .applicationId(applicationId)
                .page(1)
                .sequenceId(sequenceId.toString())
                .build()))
            .thenReturn(Mono
                .just(fill(org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse.builder())
                    .build()));
    }

    private static void requestLogsRecent(DopplerClient dopplerClient, String applicationId) {
        when(dopplerClient
            .recentLogs(RecentLogsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Flux
                .just(Envelope.builder()
                    .eventType(EventType.LOG_MESSAGE)
                    .logMessage(fill(LogMessage.builder(), "log-message-")
                        .build())
                    .origin("rsp")
                    .build()));
    }

    private static void requestLogsStream(DopplerClient dopplerClient, String applicationId) {
        when(dopplerClient
            .stream(StreamRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Flux
                .just(Envelope.builder()
                    .eventType(EventType.LOG_MESSAGE)
                    .logMessage(fill(LogMessage.builder(), "log-message-")
                        .build())
                    .origin("rsp")
                    .build()));
    }

    private static void requestOrganizationSpacesByName(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .organizationId(organizationId)
                .name(space)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .resource(SpaceResource.builder()
                        .metadata(fill(Metadata.builder(), "space-resource-metadata-")
                            .build())
                        .entity(fill(SpaceEntity.builder())
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestOrganizationSpacesByNameNotFound(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .organizationId(organizationId)
                .name(space)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .totalPages(1)
                    .build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(OrganizationResource.builder()
                        .metadata(fill(Metadata.builder(), "organization-resource-metadata-")
                            .build())
                        .entity(fill(OrganizationEntity.builder())
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestOrganizationsNotFound(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .totalPages(1)
                    .build()));
    }

    private static void requestPrivateDomainNotFound(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                .name(domain)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId, String domainId) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder())
                        .metadata(fill(Metadata.builder())
                            .id(domainId)
                            .build())
                        .entity(fill(PrivateDomainEntity.builder())
                            .name("test-private-domain")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestRemoveApplicationRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        when(cloudFoundryClient.applicationsV2()
            .removeRoute(RemoveApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestRemoveServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceBindingId) {
        when(cloudFoundryClient.applicationsV2()
            .removeServiceBinding(RemoveApplicationServiceBindingRequest.builder()
                .applicationId(applicationId)
                .serviceBindingId(serviceBindingId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestRestageApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .restage(org.cloudfoundry.client.v2.applications.RestageApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(RestageApplicationResponse.builder(), "application-")
                    .build()));
    }

    private static void requestRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, String routePath, String routeId) {
        ListRoutesRequest.Builder requestBuilder = ListRoutesRequest.builder();

        Optional.ofNullable(host).ifPresent(requestBuilder::host);
        Optional.ofNullable(routePath).ifPresent(requestBuilder::path);

        when(cloudFoundryClient.routes()
            .list(requestBuilder
                .domainId(domainId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListRoutesResponse.builder())
                    .resource(RouteResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(routeId)
                            .build())
                        .entity(RouteEntity.builder()
                            .host(host)
                            .path(routePath == null ? "" : routePath)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestRoutesEmpty(CloudFoundryClient cloudFoundryClient, String domainId, String host, Integer port, String routePath) {
        ListRoutesRequest.Builder requestBuilder = ListRoutesRequest.builder();

        Optional.ofNullable(domainId).ifPresent(requestBuilder::domainId);
        Optional.ofNullable(host).ifPresent(requestBuilder::host);
        Optional.ofNullable(port).ifPresent(requestBuilder::port);
        Optional.ofNullable(routePath).ifPresent(requestBuilder::path);

        when(cloudFoundryClient.routes()
            .list(requestBuilder
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListRoutesResponse.builder())
                    .build()));
    }

    private static void requestRunTask(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.tasks()
            .create(CreateTaskRequest.builder()
                .applicationId(applicationId)
                .command("test-command")
                .build()))
            .thenReturn(Mono
                .just(fill(CreateTaskResponse.builder())
                    .build()));
    }

    private static void requestSharedDomain(CloudFoundryClient cloudFoundryClient, String domain, String domainId) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .name(domain)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .resource(SharedDomainResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(domainId)
                            .build())
                        .entity(SharedDomainEntity.builder()
                            .name(domain)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSharedDomains(CloudFoundryClient cloudFoundryClient, String domain, String domainId) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .resource(SharedDomainResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(domainId)
                            .build())
                        .entity(fill(SharedDomainEntity.builder())
                            .internal(false)
                            .name(domain)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSharedDomainsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .build()));
    }

    private static void requestSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String organizationId) {
        when(cloudFoundryClient.spaces()
            .get(GetSpaceRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceResponse.builder())
                    .entity(fill(SpaceEntity.builder())
                        .organizationId(organizationId)
                        .build())
                    .build()));
    }

    private static void requestSpaceSummary(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceSummaryResponse.builder(), "space-summary-")
                    .application(fill(SpaceApplicationSummary.builder(), "application-summary-")
                        .build())
                    .build()));
    }

    private static void requestStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        when(cloudFoundryClient.stacks()
            .get(GetStackRequest.builder()
                .stackId(stackId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetStackResponse.builder())
                    .entity(fill(StackEntity.builder(), "stack-entity-")
                        .build())
                    .build()));
    }

    private static void requestStackIdEmpty(CloudFoundryClient cloudFoundryClient, String stack) {
        when(cloudFoundryClient.stacks()
            .list(ListStacksRequest.builder()
                .name(stack)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListStacksResponse.builder())
                    .build()));
    }

    private static void requestTcpDomains(CloudFoundryClient cloudFoundryClient, String domain, String domainId) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .resource(SharedDomainResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(domainId)
                            .build())
                        .entity(fill(SharedDomainEntity.builder())
                            .name(domain)
                            .routerGroupType("tcp")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestTerminateApplicationInstance(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceIndex) {
        when(cloudFoundryClient.applicationsV2()
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId)
                .index(instanceIndex)
                .build())).
            thenReturn(Mono.empty());
    }

    private static void requestUpdateApplication(CloudFoundryClient cloudFoundryClient, String applicationId, ApplicationManifest manifest, String stackId) {
        UpdateApplicationRequest.Builder builder = UpdateApplicationRequest.builder();
        if (manifest.getDocker() != null) {
            Optional.ofNullable(manifest.getDocker().getImage()).ifPresent(builder::dockerImage);
            String dockerUsername = manifest.getDocker().getUsername();
            String dockerPassword = manifest.getDocker().getPassword();
            builder.dockerCredentials(DockerCredentials.builder().username(dockerUsername).password(dockerPassword).build());
        }

        if (manifest.getBuildpacks() != null) {
            builder.buildpack(manifest.getBuildpacks().get(0));
        }

        when(cloudFoundryClient.applicationsV2()
            .update(builder
                .applicationId(applicationId)
                .command(manifest.getCommand())
                .diskQuota(manifest.getDisk())
                .environmentJsons(manifest.getEnvironmentVariables())
                .healthCheckTimeout(manifest.getTimeout())
                .healthCheckType(Optional.ofNullable(manifest.getHealthCheckType()).map(ApplicationHealthCheck::getValue).orElse(null))
                .instances(manifest.getInstances())
                .memory(manifest.getMemory())
                .name(manifest.getName())
                .stackId(stackId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateApplicationResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(applicationId)
                        .build())
                    .build()));
    }

    private static void requestUpdateApplicationEnvironment(CloudFoundryClient cloudFoundryClient, String applicationId, Map<String, Object> environment) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .environmentJsons(environment)
                .build()))
            .thenReturn(Mono.just(fill(UpdateApplicationResponse.builder())
                .entity(fill(ApplicationEntity.builder())
                    .environmentJsons(environment)
                    .build())
                .build()));
    }

    private static void requestUpdateApplicationHealthCheck(CloudFoundryClient cloudFoundryClient, String applicationId, ApplicationHealthCheck type) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .healthCheckType(type.getValue())
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateApplicationResponse.builder())
                    .entity(fill(ApplicationEntity.builder(), "application-entity-")
                        .build())
                    .build()));
    }

    private static void requestUpdateApplicationRename(CloudFoundryClient cloudFoundryClient, String applicationId, String name) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .name(name)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateApplicationResponse.builder())
                    .entity(fill(ApplicationEntity.builder(), "application-entity-")
                        .build())
                    .build()));
    }

    private static void requestUpdateApplicationScale(CloudFoundryClient cloudFoundryClient, String applicationId, Integer disk, Integer instances, Integer memory) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .diskQuota(disk)
                .instances(instances)
                .memory(memory)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateApplicationResponse.builder())
                    .entity(fill(ApplicationEntity.builder())
                        .build())
                    .build()));
    }

    private static void requestUpdateApplicationSsh(CloudFoundryClient cloudFoundryClient, String applicationId, Boolean enabled) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .enableSsh(enabled)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateApplicationResponse.builder())
                    .entity(fill(ApplicationEntity.builder(), "application-entity-")
                        .build())
                    .build()));
    }

    private static void requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        requestUpdateApplicationState(cloudFoundryClient, applicationId, state, 1);
    }

    private static void requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state, int instances) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state(state)
                .build()))
            .thenReturn(Mono
                .just(UpdateApplicationResponse.builder()
                    .metadata(fill(Metadata.builder())
                        .id(applicationId)
                        .build())
                    .entity(fill(ApplicationEntity.builder())
                        .state(state)
                        .instances(instances)
                        .build())
                    .build()));
    }

    private static void requestUpload(CloudFoundryClient cloudFoundryClient, String applicationId, Path application, String jobId) {
        when(cloudFoundryClient.applicationsV2()
            .upload(UploadApplicationRequest.builder()
                .applicationId(applicationId)
                .async(true)
                .application(application)
                .build()))
            .thenReturn(Mono
                .just(fill(UploadApplicationResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(jobId)
                        .build())
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));
    }

}
