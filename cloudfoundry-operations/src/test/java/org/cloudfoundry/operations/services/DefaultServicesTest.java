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

package org.cloudfoundry.operations.services;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultServicesTest {

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(fillPage(ListSpaceApplicationsRequest.builder())
                .diego(null)
                .name(applicationName)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .build())
                    .build()));
    }

    private static void requestApplicationsEmpty(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(fillPage(ListSpaceApplicationsRequest.builder())
                .diego(null)
                .name(applicationName)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .build()));
    }

    private static void requestBindService(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, Map<String, Object> parameters) {
        when(cloudFoundryClient.serviceBindings()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceBindingResponse.builder(), "service-binding-")
                    .build()));
    }

    private static void requestDeleteServiceBinding(CloudFoundryClient cloudFoundryClient, String serviceBindingId) {
        when(cloudFoundryClient.serviceBindings()
            .delete(DeleteServiceBindingRequest.builder()
                .serviceBindingId(serviceBindingId)
                .async(true)
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteServiceBindingResponse.builder())
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

    private static void requestServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        when(cloudFoundryClient.applicationsV2()
            .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                .page(1)
                .applicationId(applicationId)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListApplicationServiceBindingsResponse.builder())
                    .resource(fill(ServiceBindingResource.builder(), "service-binding-")
                        .build())
                    .build()));
    }

    private static void requestSpaceServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
                    .resource(fill(ServiceInstanceResource.builder(), "service-instance-")
                        .build())
                    .build()));
    }

    private static void requestSpaceServiceInstancesEmpty(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .returnUserProvidedServiceInstances(true)
                .spaceId(spaceId)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
                    .build()));
    }

    public static final class BindServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestBindService(this.cloudFoundryClient, "test-application-id", "test-service-instance-id", Collections.singletonMap("test-parameter-key", "test-parameter-value"));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .bind(BindServiceInstanceRequest.builder()
                    .applicationName("test-application-name")
                    .serviceInstanceName("test-service-instance-name")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .build());
        }

    }

    public static final class BindServiceInstanceNoApplication extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Application test-application-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .bind(BindServiceInstanceRequest.builder()
                    .applicationName("test-application-name")
                    .serviceInstanceName("test-service-instance-name")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .build());
        }

    }

    public static final class BindServiceInstanceNoServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestSpaceServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Service instance test-service-instance-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .bind(BindServiceInstanceRequest.builder()
                    .applicationName("test-application-name")
                    .serviceInstanceName("test-service-instance-name")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .build());
        }

    }

    public static final class UnbindServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestServiceBinding(this.cloudFoundryClient, "test-application-id", "test-service-instance-id");
            requestDeleteServiceBinding(this.cloudFoundryClient, "test-service-binding-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .unbind(UnbindServiceInstanceRequest.builder()
                    .applicationName("test-application-name")
                    .serviceInstanceName("test-service-instance-name")
                    .build());
        }

    }

    public static final class UnbindServiceInstanceFailure extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestSpaceServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestServiceBinding(this.cloudFoundryClient, "test-application-id", "test-service-instance-id");
            requestDeleteServiceBinding(this.cloudFoundryClient, "test-service-binding-id");
            requestJobFailure(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(CloudFoundryException.class, "test-error-details-errorCode(1): test-error-details-description");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .unbind(UnbindServiceInstanceRequest.builder()
                    .applicationName("test-application-name")
                    .serviceInstanceName("test-service-instance-name")
                    .build());
        }

    }

    public static final class UnbindServiceInstanceInvalidRequest extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: application name must be specified");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .unbind(UnbindServiceInstanceRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .build());
        }

    }

    public static final class UnbindServiceNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .unbind(UnbindServiceInstanceRequest.builder()
                    .applicationName("test-application-name")
                    .serviceInstanceName("test-service-instance-name")
                    .build());
        }

    }

}
