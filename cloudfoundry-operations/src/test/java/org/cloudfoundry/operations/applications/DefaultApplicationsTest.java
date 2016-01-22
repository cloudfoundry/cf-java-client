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
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.StackEntity;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.operations.RequestValidationException;
import org.cloudfoundry.operations.util.Dates;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

public final class DefaultApplicationsTest {

    private static void setupExpectations(CloudFoundryClient client, String spaceId) {
        ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                .id(spaceId)
                .name("test-app")
                .page(1)
                .build();
        ListSpaceApplicationsResponse response = ListSpaceApplicationsResponse.builder()
                .resource(ApplicationResource.builder()
                        .metadata(Resource.Metadata.builder()
                                .id("test-id")
                                .build())
                        .entity(ApplicationEntity.builder()
                                .stackId("stack-id")
                                .build())
                        .build())
                .totalPages(1)
                .build();
        when(client.spaces().listApplications(request)).thenReturn(Mono.just(response));

        ApplicationStatisticsRequest statsRequest = ApplicationStatisticsRequest.builder()
                .applicationId("test-id")
                .build();
        ApplicationStatisticsResponse statsResponse = ApplicationStatisticsResponse.builder()
                .instance("instance-0", ApplicationStatisticsResponse.InstanceStats.builder()
                        .statistics(ApplicationStatisticsResponse.InstanceStats.Statistics.builder()
                                .uri("test-stats-uri")
                                .usage(ApplicationStatisticsResponse.InstanceStats.Statistics.Usage.builder()
                                        .cpu(1.2)
                                        .memory(1000000L)
                                        .disk(2000000L)
                                        .build())
                                .memoryQuota(3000000L)
                                .diskQuota(4000000L)
                                .build())
                        .build())
                .build();
        when(client.applicationsV2().statistics(statsRequest)).thenReturn(Mono.just(statsResponse));

        GetStackRequest stackRequest = GetStackRequest.builder()
                .id("stack-id")
                .build();
        GetStackResponse stackResponse = GetStackResponse.builder()
                .entity(StackEntity.builder()
                        .name("test-stack")
                        .build())
                .build();
        when(client.stacks().get(stackRequest)).thenReturn(Mono.just(stackResponse));
    }

    public static final class Get extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            setupExpectations(this.cloudFoundryClient, TEST_SPACE_ID);

            SummaryApplicationRequest summaryRequest = SummaryApplicationRequest.builder()
                    .applicationId("test-id")
                    .build();
            SummaryApplicationResponse summaryResponse = SummaryApplicationResponse.builder()
                    .id("test-id")
                    .route(org.cloudfoundry.client.v2.routes.Route.builder()
                            .host("route-host")
                            .domain(org.cloudfoundry.client.v2.domains.Domain.builder()
                                    .name("routedomain")
                                    .build())
                            .build())
                    .packageUpdatedAt("2015-06-01T14:35:40Z")
                    .diskQuota(1073741824)
                    .memory(536870912)
                    .state("requested-state")
                    .instances(9)
                    .buildpack("buildpack")
                    .build();
            when(this.cloudFoundryClient.applicationsV2().summary(summaryRequest)).thenReturn(Mono.just(summaryResponse));

            ApplicationInstancesRequest instancesRequest = ApplicationInstancesRequest.builder()
                    .applicationId("test-id")
                    .build();
            ApplicationInstancesResponse instancesResponse = ApplicationInstancesResponse.builder()
                    .instance("instance-0", ApplicationInstanceInfo.builder()
                            .state("instance-0-state")
                            .since(1403140717.984577)
                            .build())
                    .build();
            when(this.cloudFoundryClient.applicationsV2().instances(instancesRequest)).thenReturn(Mono.just(instancesResponse));
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationDetail> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(ApplicationDetail.builder()
                            .id("test-id")
                            .diskQuota(1073741824)
                            .memoryLimit(536870912)
                            .requestedState("requested-state")
                            .instances(9)
                            .url("route-host.routedomain")
                            .lastUploaded(Dates.parse("2015-06-01T14:35:40Z"))
                            .stack("test-stack")
                            .buildpack("buildpack")
                            .instanceDetail(ApplicationDetail.InstanceDetail.builder()
                                    .state("instance-0-state")
                                    .since(Dates.parse("2014-06-19T01:18:37Z"))
                                    .cpu(1.2)
                                    .memoryUsage(1000000L)
                                    .diskUsage(2000000L)
                                    .diskQuota(4000000L)
                                    .memoryQuota(3000000L)
                                    .build())
                            .build());
        }

        @Override
        protected Publisher<ApplicationDetail> invoke() {
            GetApplicationRequest request = GetApplicationRequest.builder()
                    .name("test-app")
                    .build();
            return this.applications.get(request);
        }

    }

    public static final class GetDetectedBuildpack extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            setupExpectations(this.cloudFoundryClient, TEST_SPACE_ID);

            SummaryApplicationRequest summaryRequest = SummaryApplicationRequest.builder()
                    .applicationId("test-id")
                    .build();
            SummaryApplicationResponse summaryResponse = SummaryApplicationResponse.builder()
                    .id("test-id")
                    .route(org.cloudfoundry.client.v2.routes.Route.builder()
                            .host("route-host")
                            .domain(org.cloudfoundry.client.v2.domains.Domain.builder()
                                    .name("routedomain")
                                    .build())
                            .build())
                    .packageUpdatedAt("2015-06-01T14:35:40Z")
                    .diskQuota(1073741824)
                    .memory(536870912)
                    .state("requested-state")
                    .instances(9)
                    .detectedBuildpack("detected-buildpack")
                    .build();
            when(this.cloudFoundryClient.applicationsV2().summary(summaryRequest)).thenReturn(Mono.just(summaryResponse));

            ApplicationInstancesRequest instancesRequest = ApplicationInstancesRequest.builder()
                    .applicationId("test-id")
                    .build();
            ApplicationInstancesResponse instancesResponse = ApplicationInstancesResponse.builder()
                    .build();
            when(this.cloudFoundryClient.applicationsV2().instances(instancesRequest)).thenReturn(Mono.just(instancesResponse));
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationDetail> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(ApplicationDetail.builder()
                            .id("test-id")
                            .diskQuota(1073741824)
                            .memoryLimit(536870912)
                            .requestedState("requested-state")
                            .instances(9)
                            .url("route-host.routedomain")
                            .lastUploaded(Dates.parse("2015-06-01T14:35:40Z"))
                            .stack("test-stack")
                            .buildpack("detected-buildpack")
                            .build());
        }

        @Override
        protected Publisher<ApplicationDetail> invoke() {
            GetApplicationRequest request = GetApplicationRequest.builder()
                    .name("test-app")
                    .build();
            return this.applications.get(request);
        }

    }

    public static final class GetInvalid extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Override
        protected void assertions(TestSubscriber<ApplicationDetail> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(RequestValidationException.class);
        }

        @Override
        protected Publisher<ApplicationDetail> invoke() {
            GetApplicationRequest request = GetApplicationRequest.builder()
                    .build();
            return this.applications.get(request);
        }
    }

    public static final class GetNoBuildpack extends AbstractOperationsApiTest<ApplicationDetail> {

        private final DefaultApplications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            setupExpectations(this.cloudFoundryClient, TEST_SPACE_ID);

            SummaryApplicationRequest summaryRequest = SummaryApplicationRequest.builder()
                    .applicationId("test-id")
                    .build();
            SummaryApplicationResponse summaryResponse = SummaryApplicationResponse.builder()
                    .id("test-id")
                    .route(org.cloudfoundry.client.v2.routes.Route.builder()
                            .host("route-host")
                            .domain(org.cloudfoundry.client.v2.domains.Domain.builder()
                                    .name("routedomain")
                                    .build())
                            .build())
                    .packageUpdatedAt("2015-06-01T14:35:40Z")
                    .diskQuota(1073741824)
                    .memory(536870912)
                    .state("requested-state")
                    .instances(9)
                    .build();
            when(this.cloudFoundryClient.applicationsV2().summary(summaryRequest)).thenReturn(Mono.just(summaryResponse));

            ApplicationInstancesRequest instancesRequest = ApplicationInstancesRequest.builder()
                    .applicationId("test-id")
                    .build();
            ApplicationInstancesResponse instancesResponse = ApplicationInstancesResponse.builder()
                    .build();
            when(this.cloudFoundryClient.applicationsV2().instances(instancesRequest)).thenReturn(Mono.just(instancesResponse));
        }

        @Override
        protected void assertions(TestSubscriber<ApplicationDetail> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(ApplicationDetail.builder()
                            .id("test-id")
                            .diskQuota(1073741824)
                            .memoryLimit(536870912)
                            .requestedState("requested-state")
                            .instances(9)
                            .url("route-host.routedomain")
                            .lastUploaded(Dates.parse("2015-06-01T14:35:40Z"))
                            .stack("test-stack")
                            .buildpack("")
                            .build());
        }

        @Override
        protected Publisher<ApplicationDetail> invoke() {
            GetApplicationRequest request = GetApplicationRequest.builder()
                    .name("test-app")
                    .build();
            return this.applications.get(request);
        }

    }

    public static final class List extends AbstractOperationsApiTest<ApplicationSummary> {

        private final Applications applications = new DefaultApplications(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                    .id(TEST_SPACE_ID)
                    .build();

            GetSpaceSummaryResponse response = GetSpaceSummaryResponse.builder()
                    .id(TEST_SPACE_ID)
                    .application(SpaceApplicationSummary.builder()
                            .spaceId(TEST_SPACE_ID)
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
                            .spaceId(TEST_SPACE_ID)
                            .diskQuota(1073741824)
                            .id("test-id-2")
                            .instances(2)
                            .memory(536870912)
                            .name("test-name-2")
                            .state("RUNNING")
                            .runningInstances(2)
                            .url("bar.com")
                            .build())
                    .build();

            when(this.cloudFoundryClient.spaces().getSummary(request)).thenReturn(Mono.just(response));
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

    public static final class ListNoSpace extends AbstractOperationsApiTest<ApplicationSummary> {

        private Applications applications = new DefaultApplications(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<ApplicationSummary> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<ApplicationSummary> invoke() {
            return this.applications.list();
        }

    }

}
