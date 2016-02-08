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
import org.cloudfoundry.client.v2.Resource;
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

import java.util.Date;

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
                .just(fill(ApplicationInstancesResponse.builder(), "application-instances-")
                    .instance("instance-0", fill(ApplicationInstanceInfo.builder(), "application-instance-info-")
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
                        .domain(fill(org.cloudfoundry.client.v2.domains.Domain.builder(), "domain-")
                            .build())
                        .build())
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

    private static void requestDeleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        when(cloudFoundryClient.applicationsV2()
            .delete(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
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
        protected Mono<Void> invoke() {
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
        protected Mono<Void> invoke() {
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

        private final Applications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Mono<Void> invoke() {
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
        protected Mono<Void> invoke() {
            return this.applications
                .rename(RenameApplicationRequest.builder()
                    .name("test-app-name")
                    .newName("test-new-app-name")
                    .build());
        }
    }

    public static final class RestartNoApp extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "unknown-state");
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
                .restart(RestartApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }
    }

    public static final class RestartStarted extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STARTED");
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
                .restart(RestartApplicationRequest.builder()
                    .name("test-app-name")
                    .build());
        }
    }

    public static final class RestartStopped extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-app-name", TEST_SPACE_ID, "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
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

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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
        protected Mono<Void> invoke() {
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
        protected Mono<Void> invoke() {
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
        protected Mono<Void> invoke() {
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
        protected Mono<Void> invoke() {
            return this.applications
                .start(fill(StartApplicationRequest.builder(), "application-")
                    .build());
        }
    }

    public static final class StartStartedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsSpecificState(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID, "STOPPED");
            requestUpdateApplicationState(this.cloudFoundryClient, "test-application-id", "STARTED");
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
        protected Mono<Void> invoke() {
            return this.applications
                .stop(fill(StopApplicationRequest.builder(), "application-")
                    .build());
        }
    }

    public static final class StopStartedApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

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

}
