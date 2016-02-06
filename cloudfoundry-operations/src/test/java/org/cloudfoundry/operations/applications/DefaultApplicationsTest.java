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
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.StackEntity;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.utils.DateUtils;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.utils.test.TestObjects.fill;
import static org.cloudfoundry.utils.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultApplicationsTest {

    private static void requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(ApplicationInstancesResponse.builder()
                    .instance("instance-0", ApplicationInstanceInfo.builder()
                        .state("instance-0-state")
                        .since(1403140717.984577)
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
                    .instance("instance-0", ApplicationStatisticsResponse.InstanceStats.builder()
                        .statistics(ApplicationStatisticsResponse.InstanceStats.Statistics.builder()
                            .uri("test-stats-uri")
                            .usage(ApplicationStatisticsResponse.InstanceStats.Statistics.Usage.builder()
                                .cpu(1.2)
                                .memory(1_000_000L)
                                .disk(2_000_000L)
                                .build())
                            .memoryQuota(3_000_000L)
                            .diskQuota(4_000_000L)
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
                .just(fill(SummaryApplicationResponse.builder())
                    .id("test-application-id")
                    .route(fill(Route.builder(), "route-")
                        .domain(org.cloudfoundry.client.v2.domains.Domain.builder()
                            .name("routedomain")
                            .build())
                        .build())
                    .packageUpdatedAt("2015-06-01T14:35:40Z")
                    .build()));
    }

    private static void requestApplicationSummaryDetectedBuildpack(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(fill(SummaryApplicationRequest.builder())
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder())
                    .id("test-application-id")
                    .route(fill(Route.builder(), "route-")
                        .domain(org.cloudfoundry.client.v2.domains.Domain.builder()
                            .name("routedomain")
                            .build())
                        .build())
                    .buildpack(null)
                    .packageUpdatedAt("2015-06-01T14:35:40Z")
                    .build()));
    }

    private static void requestApplicationSummaryNoBuildpack(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .summary(fill(SummaryApplicationRequest.builder())
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(SummaryApplicationResponse.builder())
                    .id("test-application-id")
                    .route(fill(Route.builder(), "route-")
                        .domain(org.cloudfoundry.client.v2.domains.Domain.builder()
                            .name("routedomain")
                            .build())
                        .build())
                    .buildpack(null)
                    .detectedBuildpack(null)
                    .packageUpdatedAt("2015-06-01T14:35:40Z")
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
                    .resource(fill(ApplicationResource.builder(), "application-")
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

    private static void requestApplicationsStarted(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
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
                        .entity(fill(ApplicationEntity.builder())
                            .state("STARTED")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestApplicationsStopped(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
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
                        .entity(fill(ApplicationEntity.builder())
                            .state("STOPPED")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestDeleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .delete(fill(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder())
                .applicationId(applicationId)
                .build())).
            thenReturn(Mono.<Void>empty());
    }

    private static void requestDeleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        when(cloudFoundryClient.routes()
            .delete(fill(DeleteRouteRequest.builder())
                .async(null)
                .routeId(routeId)
                .build()))
            .thenReturn(Mono.<DeleteRouteResponse>empty());
    }

    private static void requestSpaceSummary(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(GetSpaceSummaryResponse.builder()
                    .id(spaceId)
                    .application(SpaceApplicationSummary.builder()
                        .spaceId(spaceId)
                        .diskQuota(1073741824)
                        .id("test-id-1")
                        .instances(2)
                        .memory(536870912)
                        .name("test-name-1")
                        .state("RUNNING")
                        .runningInstances(2)
                        .url("foo.com")
                        .build())
                    .application(SpaceApplicationSummary.builder()
                        .spaceId(spaceId)
                        .diskQuota(1073741824)
                        .id("test-id-2")
                        .instances(2)
                        .memory(536870912)
                        .name("test-name-2")
                        .state("RUNNING")
                        .runningInstances(2)
                        .url("bar.com")
                        .build())
                    .build()));
    }

    private static void requestStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        when(cloudFoundryClient.stacks()
            .get(GetStackRequest.builder()
                .stackId(stackId)
                .build()))
            .thenReturn(Mono
                .just(GetStackResponse.builder()
                    .entity(StackEntity.builder()
                        .name("test-stack")
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
                .just(UpdateApplicationResponse.builder()
                    .entity(fill(ApplicationEntity.builder())
                        .name(name)
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
                .just(UpdateApplicationResponse.builder()
                    .entity(fill(ApplicationEntity.builder())
                        .diskQuota(disk)
                        .instances(instances)
                        .memory(memory)
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
                .just(fill(UpdateApplicationResponse.builder())
                    .build()));
    }

    public static final class DeleteAndDeleteRoutes extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-name", TEST_SPACE_ID);
            requestApplicationSummary(this.cloudFoundryClient, "test-application-id");
            requestDeleteRoute(this.cloudFoundryClient, "test-route-id");
            requestDeleteApplication(this.cloudFoundryClient, "test-application-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .delete(fill(DeleteApplicationRequest.builder())
                    .build());
        }
    }

    public static final class DeleteAndDoNotDeleteRoutes extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Publisher<Void> invoke() {
            return this.applications
                .delete(fill(DeleteApplicationRequest.builder())
                    .deleteRoutes(false)
                    .build());
        }
    }

    public static final class Get extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
                .assertEquals(ApplicationDetail.builder()
                    .id("test-application-id")
                    .diskQuota(1)
                    .memoryLimit(1)
                    .requestedState("test-state")
                    .instances(1)
                    .url("test-route-host.routedomain")
                    .lastUploaded(DateUtils.parseFromIso8601("2015-06-01T14:35:40Z"))
                    .stack("test-stack")
                    .buildpack("test-buildpack")
                    .instanceDetail(ApplicationDetail.InstanceDetail.builder()
                        .state("instance-0-state")
                        .since(DateUtils.parseFromIso8601("2014-06-19T01:18:37Z"))
                        .cpu(1.2)
                        .memoryUsage(1_000_000L)
                        .diskUsage(2_000_000L)
                        .memoryQuota(3_000_000L)
                        .diskQuota(4_000_000L)
                        .build())
                    .build());
        }

        @Override
        protected Publisher<ApplicationDetail> invoke() {
            return this.applications
                .get(GetApplicationRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetDetectedBuildpack extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
                .assertEquals(ApplicationDetail.builder()
                    .id("test-application-id")
                    .diskQuota(1)
                    .memoryLimit(1)
                    .requestedState("test-state")
                    .instances(1)
                    .url("test-route-host.routedomain")
                    .lastUploaded(DateUtils.parseFromIso8601("2015-06-01T14:35:40Z"))
                    .stack("test-stack")
                    .buildpack("test-detectedBuildpack")
                    .instanceDetail(ApplicationDetail.InstanceDetail.builder()
                        .state("instance-0-state")
                        .since(DateUtils.parseFromIso8601("2014-06-19T01:18:37Z"))
                        .cpu(1.2)
                        .memoryUsage(1_000_000L)
                        .diskUsage(2_000_000L)
                        .memoryQuota(3_000_000L)
                        .diskQuota(4_000_000L)
                        .build())
                    .build());
        }

        @Override
        protected Publisher<ApplicationDetail> invoke() {
            return this.applications
                .get(GetApplicationRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class GetNoBuildpack extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
                .assertEquals(ApplicationDetail.builder()
                    .id("test-application-id")
                    .diskQuota(1)
                    .memoryLimit(1)
                    .requestedState("test-state")
                    .instances(1)
                    .url("test-route-host.routedomain")
                    .lastUploaded(DateUtils.parseFromIso8601("2015-06-01T14:35:40Z"))
                    .stack("test-stack")
                    .instanceDetail(ApplicationDetail.InstanceDetail.builder()
                        .state("instance-0-state")
                        .since(DateUtils.parseFromIso8601("2014-06-19T01:18:37Z"))
                        .cpu(1.2)
                        .memoryUsage(1_000_000L)
                        .diskUsage(2_000_000L)
                        .memoryQuota(3_000_000L)
                        .diskQuota(4_000_000L)
                        .build())
                    .build());
        }

        @Override
        protected Publisher<ApplicationDetail> invoke() {
            return this.applications
                .get(GetApplicationRequest.builder()
                    .name("test-app")
                    .build());
        }

    }

    public static final class List extends AbstractOperationsApiTest<ApplicationSummary> {

        private final Applications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceSummary(this.cloudFoundryClient, TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationSummary> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(ApplicationSummary.builder()
                    .diskQuota(1073741824)
                    .id("test-id-1")
                    .instances(2)
                    .memoryLimit(536870912)
                    .name("test-name-1")
                    .requestedState("RUNNING")
                    .runningInstances(2)
                    .url("foo.com")
                    .build())
                .assertEquals(ApplicationSummary.builder()
                    .diskQuota(1073741824)
                    .id("test-id-2")
                    .instances(2)
                    .memoryLimit(536870912)
                    .name("test-name-2")
                    .requestedState("RUNNING")
                    .runningInstances(2)
                    .url("bar.com")
                    .build());
        }

        @Override
        protected Publisher<ApplicationSummary> invoke() {
            return this.applications.list();
        }

    }

    public static final class Rename extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Publisher<Void> invoke() {
            return this.applications
                .rename(RenameApplicationRequest.builder()
                    .name("test-app-name")
                    .newName("test-new-app-name")
                    .build());
        }
    }

    public static final class RenameNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Publisher<Void> invoke() {
            return this.applications
                .rename(RenameApplicationRequest.builder()
                    .name("test-app-name")
                    .newName("test-new-app-name")
                    .build());
        }
    }

    public static final class ScaleDiskAndInstancesNotStarted extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsStopped(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
            requestUpdateApplicationScale(this.cloudFoundryClient, "test-application-id", 2, 2, null);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Nothing returned on success
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .diskLimit("2m")
                    .build());
        }
    }

    public static final class ScaleDiskAndInstancesStarted extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsStarted(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
            requestUpdateApplicationScale(this.cloudFoundryClient, "test-application-id", 2, 2, null);
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .diskLimit("2m")
                    .build());
        }
    }

    public static final class ScaleInstances extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Publisher<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .build());
        }
    }

    public static final class ScaleInstancesNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Publisher<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .instances(2)
                    .build());
        }
    }

    public static final class ScaleNoChange extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // nothing returned on success
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .scale(ScaleApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }
    }

    public static final class StartInvalidApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Publisher<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }
    }

    public static final class StartStartedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsStarted(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }
    }

    public static final class StartStoppedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsStopped(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }
    }

    public static final class StopInvalidApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Publisher<Void> invoke() {
            return this.applications
                .stop(fill(StopApplicationRequest.builder(), "application-")
                    .build());
        }
    }

    public static final class StopStartedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsStarted(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STOPPED");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .stop(fill(StopApplicationRequest.builder(), "application-")
                    .build());
        }
    }

    public static final class StopStoppedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsStopped(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.applications
                .stop(fill(StopApplicationRequest.builder(), "application-")
                    .build());
        }
    }

}
