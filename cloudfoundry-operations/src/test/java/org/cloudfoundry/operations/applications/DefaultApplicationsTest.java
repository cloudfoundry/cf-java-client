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

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.StackEntity;
import org.cloudfoundry.logging.LogMessage;
import org.cloudfoundry.logging.LoggingClient;
import org.cloudfoundry.logging.RecentLogsRequest;
import org.cloudfoundry.logging.StreamLogsRequest;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.DateUtils;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.StringMap;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Supplier;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultApplicationsTest {

    public static void requestApplicationStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        when(cloudFoundryClient.stacks()
            .get(GetStackRequest.builder()
                .stackId(stackId)
                .build()))
            .thenReturn(Mono.just(fill(GetStackResponse.builder(), "stack-").build()));
    }

    private static void requestApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
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
                        return Mono.just(responses.poll());
                    }

                }));
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

    private static void requestApplicationEvents(CloudFoundryClient cloudFoundryClient, String applicationId, EventEntity... entities) {
        final ListEventsResponse.ListEventsResponseBuilder responseBuilder = fillPage(ListEventsResponse.builder());

        for (EventEntity entity : entities) {
            responseBuilder.resource(EventResource.builder()
                .metadata(Resource.Metadata.builder().id("test-event-id").build())
                .entity(entity)
                .build());
        }

        when(cloudFoundryClient.events()
            .list(ListEventsRequest.builder()
                .actee(applicationId)
                .orderDirection(PaginatedRequest.OrderDirection.DESC)
                .resultsPerPage(50)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(responseBuilder
                    .totalPages(1)
                    .build()));
    }

    private static void requestApplicationFailing(CloudFoundryClient cloudFoundryClient, String applicationId) {
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

    private static void requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(ApplicationInstancesResponse.builder(), "application-instances-")
                    .instance("instance-0", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
                        .build())
                    .build()));
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

    private static void requestApplicationStats(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .statistics(ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationStatisticsResponse.builder()
                    .instance("instance-0", fill(ApplicationStatisticsResponse.InstanceStats.builder(), "instance-statistics-")
                        .statistics(fill(ApplicationStatisticsResponse.InstanceStats.Statistics.builder(), "statistics-")
                            .usage(fill(ApplicationStatisticsResponse.InstanceStats.Statistics.Usage.builder(), "usage-")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(fill(SummaryApplicationRequest.builder())
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .route(fill(Route.builder(), "route-")
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-").build())
                        .build())
                    .service(fill(ServiceInstance.builder(), "service-instance-").build())
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .build()));
    }

    private static void requestApplicationSummaryDetectedBuildpack(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(fill(SummaryApplicationRequest.builder())
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .route(fill(Route.builder(), "route-")
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-")
                            .build())
                        .build())
                    .buildpack(null)
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .build()));
    }

    private static void requestApplicationSummaryNoBuildpack(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(fill(SummaryApplicationRequest.builder())
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .route(fill(Route.builder(), "route-")
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
            .summary(fill(SummaryApplicationRequest.builder())
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder(), "application-summary-")
                    .packageUpdatedAt(DateUtils.formatToIso8601(new Date(0)))
                    .build()));
    }

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(application)
                .spaceId(spaceId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(ApplicationResource.builder()
                        .metadata(Resource.Metadata.builder()
                            .id("test-application-id")
                            .build())
                        .entity(fill(ApplicationEntity.builder(), "application-")
                            .environmentJson("test-var", "test-value")
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestApplicationsEmpty(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(fillPage(ListSpaceApplicationsRequest.builder())
                .spaceId(spaceId)
                .diego(null)
                .name(application)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .build()));
    }

    private static void requestApplicationsSpecificState(CloudFoundryClient cloudFoundryClient, String application, String spaceId, String stateReturned) {
        when(cloudFoundryClient.spaces()
            .listApplications(fillPage(ListSpaceApplicationsRequest.builder())
                .spaceId(spaceId)
                .diego(null)
                .name(application)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .entity(fill(ApplicationEntity.builder(), "application-entity-")
                            .state(stateReturned)
                            .build())
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
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(ApplicationResource.builder()
                        .metadata(Resource.Metadata.builder()
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

    private static void requestDeleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .delete(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
                .applicationId(applicationId)
                .build())).
            thenReturn(Mono.<Void>empty());
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
                                .errorDetails(fill(JobEntity.ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(responses.poll());
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
                        return Mono.just(responses.poll());
                    }

                }));
    }

    private static void requestLogs(LoggingClient loggingClient, String applicationId) {
        when(loggingClient
            .stream(StreamLogsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(LogMessage.builder(), "log-message-")
                    .build()));
    }

    private static void requestLogsRecent(LoggingClient loggingClient, String applicationId) {
        when(loggingClient
            .recent(RecentLogsRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(LogMessage.builder(), "log-message-")
                    .build()));
    }

    private static void requestRestage(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .restage(org.cloudfoundry.client.v2.applications.RestageApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(RestageApplicationResponse.builder(), "application-")
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

    private static void requestTerminateApplicationInstance(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceIndex) {
        when(cloudFoundryClient.applicationsV2()
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId)
                .index(instanceIndex)
                .build())).
            thenReturn(Mono.<Void>empty());
    }

    private static void requestUpdateApplicationEnableSsh(CloudFoundryClient cloudFoundryClient, String applicationId, Boolean enabled) {
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

    private static void requestUpdateApplicationEnvironment(CloudFoundryClient cloudFoundryClient, String applicationId, Map<String, Object> environment) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .environmentJsons(environment)
                .build()))
            .thenReturn(Mono.just(fill(UpdateApplicationResponse.builder()).build()));
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

    private static void requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        when(cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state(state)
                .build()))
            .thenReturn(Mono
                .just(UpdateApplicationResponse.builder()
                    .metadata(fill(Resource.Metadata.builder())
                        .id(applicationId)
                        .build())
                    .entity(fill(ApplicationEntity.builder())
                        .state(state)
                        .build())
                    .build()));
    }

    public static final class DeleteAndDeleteRoutes extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
            requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestDeleteApplication(this.cloudFoundryClient, "test-application-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .delete(fill(DeleteApplicationRequest.builder())
                    .build());
        }

    }

    public static final class DeleteAndDeleteRoutesFailure extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
            requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestDeleteApplication(this.cloudFoundryClient, "test-application-id");
            requestJobFailure(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(CloudFoundryException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .delete(fill(DeleteApplicationRequest.builder())
                    .build());
        }

    }

    public static final class DeleteAndDoNotDeleteRoutes extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
            requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
            requestDeleteApplication(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .delete(fill(DeleteApplicationRequest.builder())
                    .deleteRoutes(false)
                    .build());
        }

    }

    public static final class DisableSsh extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
            requestUpdateApplicationEnableSsh(this.cloudFoundryClient, "test-application-id", false);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .disableSsh(DisableApplicationSshRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class DisableSshAlreadyDisabled extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsWithSsh(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, false);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .disableSsh(DisableApplicationSshRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class DisableSshNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .disableSsh(DisableApplicationSshRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class EnableSsh extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
            requestUpdateApplicationEnableSsh(this.cloudFoundryClient, "test-application-id", true);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .enableSsh(EnableApplicationSshRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class EnableSshAlreadyEnabled extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsWithSsh(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, true);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .enableSsh(EnableApplicationSshRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class EnableSshNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .enableSsh(EnableApplicationSshRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class Get extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationStats(this.cloudFoundryClient, "test-application-id");
            requestStack(this.cloudFoundryClient, "test-application-stackId");
            requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
            requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(ApplicationDetail.builder())
                    .buildpack("test-application-summary-buildpack")
                    .id("test-application-summary-id")
                    .instanceDetail(fill(ApplicationDetail.InstanceDetail.builder())
                        .since(new Date(1000))
                        .state("test-application-instance-info-state")
                        .build())
                    .lastUploaded(new Date(0))
                    .name("test-application-summary-name")
                    .requestedState("test-application-summary-state")
                    .stack("test-stack-entity-name")
                    .url("test-route-host.test-domain-name")
                    .build());
        }

        @Override
        protected Mono<ApplicationDetail> invoke() {
            return this.applications
                .get(GetApplicationRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetApplicationManifest extends AbstractOperationsApiTest<ApplicationManifest> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
            requestApplicationStack(this.cloudFoundryClient, "test-application-summary-stackId");
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationManifest> testSubscriber) throws Exception {
            testSubscriber.assertEquals(ApplicationManifest.builder()
                .buildpack("test-application-summary-buildpack")
                .command("test-application-summary-command")
                .diskQuotaMB(1)
                .domain("test-domain-name")
                .host("test-route-host")
                .instances(1)
                .memoryMB(1)
                .name("test-application-summary-name")
                .service("test-service-instance-name")
                .stack("test-stack-name")
                .timeout(1)
                .build());
        }

        @Override
        protected Mono<ApplicationManifest> invoke() {
            return this.applications
                .getApplicationManifest(GetApplicationManifestRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetApplicationManifestInvalidRequest extends AbstractOperationsApiTest<ApplicationManifest> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Override
        protected void assertions(TestSubscriber<ApplicationManifest> testSubscriber) throws Exception {
            testSubscriber.
                assertError(RequestValidationException.class);
        }

        @Override
        protected Mono<ApplicationManifest> invoke() {
            return this.applications
                .getApplicationManifest(GetApplicationManifestRequest.builder()
                    .build());
        }

    }

    public static final class GetApplicationManifestNoRoutes extends AbstractOperationsApiTest<ApplicationManifest> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationSummaryNoRoutes(this.cloudFoundryClient, "test-application-id");
            requestApplicationStack(this.cloudFoundryClient, "test-application-summary-stackId");
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationManifest> testSubscriber) throws Exception {
            testSubscriber.assertEquals(ApplicationManifest.builder()
                .buildpack("test-application-summary-buildpack")
                .command("test-application-summary-command")
                .diskQuotaMB(1)
                .instances(1)
                .memoryMB(1)
                .name("test-application-summary-name")
                .stack("test-stack-name")
                .timeout(1)
                .build());
        }

        @Override
        protected Mono<ApplicationManifest> invoke() {
            return this.applications
                .getApplicationManifest(GetApplicationManifestRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetDetectedBuildpack extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationStats(this.cloudFoundryClient, "test-application-id");
            requestStack(this.cloudFoundryClient, "test-application-stackId");
            requestApplicationSummaryDetectedBuildpack(this.cloudFoundryClient, "test-application-id");
            requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(ApplicationDetail.builder())
                    .buildpack("test-application-summary-detectedBuildpack")
                    .id("test-application-summary-id")
                    .instanceDetail(fill(ApplicationDetail.InstanceDetail.builder())
                        .since(new Date(1000))
                        .state("test-application-instance-info-state")
                        .build())
                    .lastUploaded(new Date(0))
                    .name("test-application-summary-name")
                    .requestedState("test-application-summary-state")
                    .stack("test-stack-entity-name")
                    .url("test-route-host.test-domain-name")
                    .build());
        }

        @Override
        protected Mono<ApplicationDetail> invoke() {
            return this.applications
                .get(GetApplicationRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetEnvironments extends AbstractOperationsApiTest<ApplicationEnvironments> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationEnvironment(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationEnvironments> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(ApplicationEnvironments.builder()
                    .running(StringMap.builder()
                        .entry("running-env-name", "running-env-value")
                        .build())
                    .staging(StringMap.builder()
                        .entry("staging-env-name", "staging-env-value")
                        .build())
                    .systemProvided(StringMap.builder()
                        .entry("system-env-name", "system-env-value")
                        .build())
                    .userProvided(StringMap.builder()
                        .entry("env-name", "env-value")
                        .build())
                    .build());
        }

        @Override
        protected Publisher<ApplicationEnvironments> invoke() {
            return this.applications
                .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetEnvironmentsNoApp extends AbstractOperationsApiTest<ApplicationEnvironments> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationEnvironments> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<ApplicationEnvironments> invoke() {
            return this.applications
                .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetEvents extends AbstractOperationsApiTest<ApplicationEvent> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationEvents(this.cloudFoundryClient,
                "test-application-id",
                fill(EventEntity.builder(), "event-")
                    .timestamp("2016-02-08T15:45:59Z")
                    .metadata("request", StringMap.builder()
                        .entry("instances", 1)
                        .entry("memory", 2)
                        .entry("environment_json", "test-data")
                        .entry("state", "test-state")
                        .build())
                    .build());
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationEvent> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(ApplicationEvent.builder()
                    .description("instances: 1, memory: 2, state: test-state, environment_json: test-data")
                    .event("test-event-type")
                    .actor("test-event-actorName")
                    .time(DateUtils.parseFromIso8601("2016-02-08T15:45:59Z"))
                    .build());
        }

        @Override
        protected Publisher<ApplicationEvent> invoke() {
            return this.applications
                .getEvents(GetApplicationEventsRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetEventsBadTimeSparseMetadata extends AbstractOperationsApiTest<ApplicationEvent> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationEvents(this.cloudFoundryClient,
                "test-application-id",
                fill(EventEntity.builder(), "event-")
                    .timestamp("BAD-TIMESTAMP")
                    .metadata("request", StringMap.builder()
                        .entry("memory", 2)
                        .entry("environment_json", "test-data")
                        .entry("state", "test-state")
                        .build())
                    .build());
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationEvent> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(ApplicationEvent.builder()
                    .description("memory: 2, state: test-state, environment_json: test-data")
                    .event("test-event-type")
                    .actor("test-event-actorName")
                    .build());
        }

        @Override
        protected Publisher<ApplicationEvent> invoke() {
            return this.applications
                .getEvents(GetApplicationEventsRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetEventsFoundZero extends AbstractOperationsApiTest<ApplicationEvent> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationEvents(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationEvent> testSubscriber) throws Exception {
            // expect successful empty result
        }

        @Override
        protected Publisher<ApplicationEvent> invoke() {
            return this.applications
                .getEvents(GetApplicationEventsRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetEventsLimitZero extends AbstractOperationsApiTest<ApplicationEvent> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationEvents(this.cloudFoundryClient,
                "test-application-id",
                fill(EventEntity.builder(), "event-")
                    .timestamp("2016-02-08T15:45:59Z")
                    .metadata("request", StringMap.builder()
                        .entry("instances", 1)
                        .entry("memory", 2)
                        .entry("environment_json", "test-data")
                        .entry("state", "test-state")
                        .build())
                    .build());
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationEvent> testSubscriber) throws Exception {
            // expect successful empty result
        }

        @Override
        protected Publisher<ApplicationEvent> invoke() {
            return this.applications
                .getEvents(GetApplicationEventsRequest.builder()
                    .name("test-app")
                    .maxNumberOfEvents(0)
                    .build());
        }

    }

    public static final class GetEventsTwo extends AbstractOperationsApiTest<ApplicationEvent> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationEvents(this.cloudFoundryClient,
                "test-application-id",
                fill(EventEntity.builder(), "event-")
                    .timestamp("2016-02-08T15:45:59Z")
                    .metadata("request", StringMap.builder()
                        .entry("instances", 1)
                        .entry("memory", 2)
                        .entry("environment_json", "test-data")
                        .entry("state", "test-state")
                        .build())
                    .build(),
                fill(EventEntity.builder(), "event-")
                    .timestamp("2016-02-08T15:49:07Z")
                    .metadata("request", StringMap.builder()
                        .entry("state", "test-state-two")
                        .build())
                    .build()
            );
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationEvent> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(ApplicationEvent.builder()
                    .description("instances: 1, memory: 2, state: test-state, environment_json: test-data")
                    .event("test-event-type")
                    .actor("test-event-actorName")
                    .time(DateUtils.parseFromIso8601("2016-02-08T15:45:59Z"))
                    .build())
                .assertEquals(ApplicationEvent.builder()
                    .description("state: test-state-two")
                    .event("test-event-type")
                    .actor("test-event-actorName")
                    .time(DateUtils.parseFromIso8601("2016-02-08T15:49:07Z"))
                    .build())
            ;
        }

        @Override
        protected Publisher<ApplicationEvent> invoke() {
            return this.applications
                .getEvents(GetApplicationEventsRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetNoBuildpack extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestApplicationStats(this.cloudFoundryClient, "test-application-id");
            requestStack(this.cloudFoundryClient, "test-application-stackId");
            requestApplicationSummaryNoBuildpack(this.cloudFoundryClient, "test-application-id");
            requestApplicationInstances(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(ApplicationDetail.builder())
                    .buildpack(null)
                    .id("test-application-summary-id")
                    .instanceDetail(fill(ApplicationDetail.InstanceDetail.builder())
                        .since(new Date(1000))
                        .state("test-application-instance-info-state")
                        .build())
                    .lastUploaded(new Date(0))
                    .name("test-application-summary-name")
                    .requestedState("test-application-summary-state")
                    .stack("test-stack-entity-name")
                    .url("test-route-host.test-domain-name")
                    .build());
        }

        @Override
        protected Mono<ApplicationDetail> invoke() {
            return this.applications
                .get(GetApplicationRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class List extends AbstractOperationsApiTest<ApplicationSummary> {

        private final Applications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceSummary(this.cloudFoundryClient, TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationSummary> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(ApplicationSummary.builder())
                    .id("test-application-summary-id")
                    .name("test-application-summary-name")
                    .requestedState("test-application-summary-state")
                    .build());
        }

        @Override
        protected Publisher<ApplicationSummary> invoke() {
            return this.applications.list();
        }

    }

    public static final class Logs extends AbstractOperationsApiTest<LogMessage> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestLogs(this.loggingClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<LogMessage> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(LogMessage.builder(), "log-message-")
                    .build());
        }

        @Override
        protected Publisher<LogMessage> invoke() {
            return this.applications
                .logs(LogsRequest.builder()
                    .name("test-application-name")
                    .recent(false)
                    .build());
        }

    }

    public static final class LogsNoApp extends AbstractOperationsApiTest<LogMessage> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<LogMessage> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<LogMessage> invoke() {
            return this.applications
                .logs(LogsRequest.builder()
                    .name("test-application-name")
                    .build());
        }

    }

    public static final class LogsRecent extends AbstractOperationsApiTest<LogMessage> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestLogsRecent(this.loggingClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<LogMessage> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(LogMessage.builder(), "log-message-")
                    .build());
        }

        @Override
        protected Publisher<LogMessage> invoke() {
            return this.applications
                .logs(LogsRequest.builder()
                    .name("test-application-name")
                    .recent(true)
                    .build());
        }

    }

    public static final class LogsRecentNotSet extends AbstractOperationsApiTest<LogMessage> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));


        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestLogs(this.loggingClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<LogMessage> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(LogMessage.builder(), "log-message-")
                    .build());
        }

        @Override
        protected Publisher<LogMessage> invoke() {
            return this.applications
                .logs(LogsRequest.builder()
                    .name("test-application-name")
                    .build());
        }

    }

    public static final class Rename extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
            requestUpdateApplicationRename(this.cloudFoundryClient, "test-application-id", "test-new-app-name");

        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .rename(RenameApplicationRequest.builder()
                    .name("test-app-name")
                    .newName("test-new-app-name")
                    .build());
        }

    }

    public static final class RenameNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .rename(RenameApplicationRequest.builder()
                    .name("test-app-name")
                    .newName("test-new-app-name")
                    .build());
        }

    }

    public static final class Restage extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestRestage(this.cloudFoundryClient, "test-application-id");
            requestApplication(this.cloudFoundryClient, "test-application-id");
            requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restage(RestageApplicationRequest.builder()
                    .name("test-application-name")
                    .build());
        }

    }

    public static final class RestageInvalidApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restage(RestageApplicationRequest.builder()
                    .name("test-application-name")
                    .build());
        }

    }

    public static final class RestageStagingFailure extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestRestage(this.cloudFoundryClient, "test-application-id");
            requestApplicationFailing(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restage(RestageApplicationRequest.builder()
                    .name("test-application-name")
                    .build());
        }

    }

    public static final class RestageStartingFailurePartial extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestRestage(this.cloudFoundryClient, "test-application-id");
            requestApplication(this.cloudFoundryClient, "test-application-id");
            requestApplicationInstancesFailingPartial(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restage(RestageApplicationRequest.builder()
                    .name("test-application-name")
                    .build());
        }

    }

    public static final class RestageStartingFailureTotal extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestRestage(this.cloudFoundryClient, "test-application-id");
            requestApplication(this.cloudFoundryClient, "test-application-id");
            requestApplicationInstancesFailingTotal(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restage(RestageApplicationRequest.builder()
                    .name("test-application-name")
                    .build());
        }

    }

    public static final class RestartFailurePartial extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesFailingPartial(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restart(RestartApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class RestartFailureTotal extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesFailingTotal(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restart(RestartApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class RestartInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestTerminateApplicationInstance(this.cloudFoundryClient, "test-application-id", "0");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restartInstance(RestartApplicationInstanceRequest.builder()
                    .name("test-application-name")
                    .instanceIndex(0)
                    .build());
        }

    }

    public static final class RestartNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-non-existent-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restart(RestartApplicationRequest.builder()
                    .name("test-non-existent-app-name")
                    .build());
        }

    }

    public static final class RestartNotStartedAndNotStopped extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "unknown-state");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restart(RestartApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class RestartStarted extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restart(RestartApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class RestartStopped extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .restart(RestartApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class ScaleDiskAndInstancesNotStarted extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STOPPED");
            requestUpdateApplicationScale(this.cloudFoundryClient, "test-application-id", 2, 2, null);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .diskLimit("2m")
                    .build());
        }

    }

    public static final class ScaleDiskAndInstancesStarted extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
            requestUpdateApplicationScale(this.cloudFoundryClient, "test-application-id", 2, 2, null);
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .diskLimit("2m")
                    .build());
        }

    }

    public static final class ScaleInstances extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
            requestUpdateApplicationScale(this.cloudFoundryClient, "test-application-id", null, 2, null);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .build());
        }

    }

    public static final class ScaleInstancesNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .build());
        }

    }

    public static final class ScaleNoChange extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class SetEnvironmentVariable extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestUpdateApplicationEnvironment(this.cloudFoundryClient, "test-application-id", StringMap.builder()
                .entry("test-var", "test-value")
                .entry("test-var-name", "test-var-value")
                .build());
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name("test-app")
                    .variableName("test-var-name")
                    .variableValue("test-var-value")
                    .build());
        }

    }

    public static final class SetEnvironmentVariableNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name("test-app")
                    .variableName("test-var-name")
                    .variableValue("test-var-value")
                    .build());
        }

    }

    public static final class SshEnabled extends AbstractOperationsApiTest<Boolean> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(true);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.applications
                .sshEnabled(ApplicationSshEnabledRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class SshEnabledNoApp extends AbstractOperationsApiTest<Boolean> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.applications
                .sshEnabled(ApplicationSshEnabledRequest.builder()
                    .name("test-app-name")
                    .build());
        }

    }

    public static final class StartApplicationFailurePartial extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesFailingPartial(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class StartApplicationFailureTotal extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesFailingTotal(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class StartInvalidApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class StartStartedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STARTED");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class StartStoppedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
            requestApplicationInstancesRunning(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class StopInvalidApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .stop(fill(StopApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class StopStartedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STARTED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .stop(fill(StopApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class StopStoppedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .stop(fill(StopApplicationRequest.builder(), "application-")
                    .build());
        }

    }

    public static final class UnsetEnvironmentVariable extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
            requestUpdateApplicationEnvironment(this.cloudFoundryClient, "test-application-id", StringMap.builder().build());
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Nothing returned on success
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                    .name("test-app")
                    .variableName("test-var")
                    .build());
        }

    }

    public static final class UnsetEnvironmentVariableNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, this.loggingClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-app", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.applications
                .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                    .name("test-app")
                    .variableName("test-var")
                    .build());
        }

    }

}
