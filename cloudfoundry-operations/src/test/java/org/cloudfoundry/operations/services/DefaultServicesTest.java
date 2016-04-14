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
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceEntity;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultServicesTest {

    private static void requestApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String application) {
        when(cloudFoundryClient.applicationsV2().get(GetApplicationRequest.builder()
            .applicationId(applicationId)
            .build()))
            .thenReturn(Mono
                .just(fill(GetApplicationResponse.builder())
                    .metadata(Resource.Metadata.builder().id(applicationId).build())
                    .entity(ApplicationEntity.builder()
                        .name(application)
                        .build())
                    .build())
            );
    }

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

    private static void requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String planId, String serviceInstance, Map<String, Object> parameters, List<String> tags,
                                                     String serviceInstanceId, String state) {
        when(cloudFoundryClient.serviceInstances()
            .create(org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .name(serviceInstance)
                .servicePlanId(planId)
                .spaceId(spaceId)
                .parameters(parameters)
                .tags(tags)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceInstanceResponse.builder())
                    .metadata(Resource.Metadata.builder()
                        .id(serviceInstanceId)
                        .build())
                    .entity(fill(ServiceInstanceEntity.builder())
                        .lastOperation(LastOperation.builder()
                            .type("create")
                            .state(state)
                            .build())
                        .build())
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

    private static void requestGetServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String state) {
        when(cloudFoundryClient.serviceInstances()
            .get(GetServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServiceInstanceResponse.builder())
                    .metadata(Resource.Metadata.builder()
                        .id(serviceInstanceId)
                        .build())
                    .entity(fill(ServiceInstanceEntity.builder())
                        .lastOperation(LastOperation.builder()
                            .type("create")
                            .state(state)
                            .build())
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

    private static void requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.serviceBindings()
            .list(ListServiceBindingsRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListServiceBindingsResponse.builder())
                    .build()));
    }

    private static void requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String applicationId) {
        when(cloudFoundryClient.serviceBindings()
            .list(ListServiceBindingsRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListServiceBindingsResponse.builder())
                    .resource(fill(ServiceBindingResource.builder())
                        .entity(ServiceBindingEntity.builder()
                            .applicationId(applicationId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId, String plan, String planId) {
        when(cloudFoundryClient.servicePlans()
            .list(ListServicePlansRequest.builder()
                .serviceId(serviceId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListServicePlansResponse.builder())
                    .resource(ServicePlanResource.builder()
                        .metadata(Resource.Metadata.builder()
                            .id(planId)
                            .build())
                        .entity(fill(ServicePlanEntity.builder())
                            .name(plan)
                            .build())
                        .build())
                    .build())

            );
    }

    private static void requestListSpaceServices(CloudFoundryClient cloudFoundryClient, String spaceId, String service, String serviceId) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .label(service)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServicesResponse.builder())
                    .resource(ServiceResource.builder()
                        .metadata(Resource.Metadata.builder()
                            .id(serviceId)
                            .build())
                        .entity(fill(ServiceEntity.builder())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestService(CloudFoundryClient cloudFoundryClient, String serviceId, String service) {
        when(cloudFoundryClient.services()
            .get(GetServiceRequest.builder()
                .serviceId(serviceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServiceResponse.builder())
                    .entity(fill(ServiceEntity.builder())
                        .extra("{\"displayName\":\"test-value\",\"longDescription\":\"test-value\",\"documentationUrl\":\"test-documentation-url\",\"supportUrl\":\"test-value\"}")
                        .label(service)
                        .build())
                    .build()));
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

    private static void requestServiceEmpty(CloudFoundryClient cloudFoundryClient, String serviceId, String service) {
        when(cloudFoundryClient.services()
            .get(GetServiceRequest.builder()
                .serviceId(serviceId)
                .build()))
            .thenReturn(Mono
                .just(GetServiceResponse.builder()
                    .entity(ServiceEntity.builder()
                        .label(service)
                        .build())
                    .build()));
    }

    private static void requestServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId, String servicePlan, String service) {
        when(cloudFoundryClient.servicePlans()
            .get(GetServicePlanRequest.builder()
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServicePlanResponse.builder())
                    .entity(ServicePlanEntity.builder()
                        .name(servicePlan)
                        .serviceId(service + "-id")
                        .build())
                    .build()));
    }

    private static void requestSpaceServiceByName(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceLabel) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .page(1)
                .label(serviceLabel)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServicesResponse.builder())
                    .resource(ServiceResource.builder()
                        .metadata(Resource.Metadata.builder().id(serviceLabel + "-id").build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel + "-description")
                            .label(serviceLabel)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSpaceServiceInstanceByName(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
                    .resource(fill(UnionServiceInstanceResource.builder(), "service-instance-")
                        .build())
                    .build()));
    }

    private static void requestSpaceServiceInstanceByNameEmpty(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
                    .build()));
    }

    private static void requestSpaceServiceInstanceByNameManaged(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(Resource.Metadata.builder().id("test-service-instance-id").build())
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .name(serviceName)
                            .servicePlanId("test-service-plan-id")
                            .tags(Collections.singletonList("test-tag"))
                            .type("managed_service_instance")
                            .lastOperation(LastOperation.builder()
                                .createdAt("test-startedAt")
                                .description("test-message")
                                .state("test-status")
                                .type("test-type")
                                .updatedAt("test-updatedAt")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSpaceServiceInstanceByNameUserProvided(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(Resource.Metadata.builder()
                            .id("test-service-instance-id")
                            .build())
                        .entity(UnionServiceInstanceEntity.builder()
                            .type("user_provided_service_instance")
                            .name(serviceName)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSpaceServiceInstancesEmpty(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
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

    private static void requestSpaceServiceInstancesTwo(CloudFoundryClient cloudFoundryClient, String spaceId, String instanceName1, String instanceName2) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(Resource.Metadata.builder().id(instanceName1 + "-id").build())
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .type("user_provided_service_instance")
                            .dashboardUrl(null)
                            .name(instanceName1)
                            .servicePlanId(null)
                            .lastOperation(null)
                            .build())
                        .build())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(Resource.Metadata.builder().id(instanceName2 + "-id").build())
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .type("managed_service_instance")
                            .name(instanceName2)
                            .tag("test-tag")
                            .servicePlanId(instanceName2 + "-plan-id")
                            .lastOperation(LastOperation.builder()
                                .createdAt("test-startedAt")
                                .description("test-message")
                                .state("test-status")
                                .type("test-type")
                                .updatedAt("test-updatedAt")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSpaceServicesTwo(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceLabel1, String serviceLabel2) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServicesResponse.builder())
                    .resource(ServiceResource.builder()
                        .metadata(Resource.Metadata.builder().id(serviceLabel1 + "-id").build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel1 + "-description")
                            .label(serviceLabel1)
                            .build())
                        .build())
                    .resource(ServiceResource.builder()
                        .metadata(Resource.Metadata.builder().id(serviceLabel2 + "-id").build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel2 + "-description")
                            .label(serviceLabel2)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestUserProvidedCreateServiceInstance(CloudFoundryClient cloudFoundryClient,
                                                                 String spaceId,
                                                                 String name,
                                                                 Map<String, Object> credentials,
                                                                 String routeServiceUrl,
                                                                 String syslogDrainUrl,
                                                                 String userProvidedServiceInstanceId) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .create(org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest.builder()
                .name(name)
                .credentials(credentials)
                .routeServiceUrl(routeServiceUrl)
                .spaceId(spaceId)
                .syslogDrainUrl(syslogDrainUrl)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateUserProvidedServiceInstanceResponse.builder())
                    .metadata(Resource.Metadata.builder()
                        .id(userProvidedServiceInstanceId)
                        .build())
                    .entity(fill(UserProvidedServiceInstanceEntity.builder())
                        .build())
                    .build()));
    }

    public static final class BindServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestSpaceServiceInstanceByName(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
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
            requestSpaceServiceInstanceByName(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
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

    public static final class CreateServiceInstanceDelay extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID, "test-service", "test-service-id");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
            requestCreateServiceInstance(this.cloudFoundryClient, TEST_SPACE_ID, "test-plan-id", "test-service-instance", Collections.emptyMap(), Collections.emptyList(),
                "test-service-instance-id", "in progress");
            requestGetServiceInstance(this.cloudFoundryClient, "test-service-instance-id", "successful");
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .createInstance(CreateServiceInstanceRequest.builder()
                    .serviceInstanceName("test-service-instance")
                    .serviceName("test-service")
                    .planName("test-plan")
                    .build());
        }

    }

    public static final class CreateServiceInstanceInstant extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID, "test-service", "test-service-id");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
            requestCreateServiceInstance(this.cloudFoundryClient, TEST_SPACE_ID, "test-plan-id", "test-service-instance", Collections.singletonMap("test-parameter-key", "test-parameter-value"),
                Collections.singletonList("test-tag"), "test-service-instance-id", "successful");
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .createInstance(CreateServiceInstanceRequest.builder()
                    .serviceInstanceName("test-service-instance")
                    .serviceName("test-service")
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class CreateServiceInstanceNoService extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: service name must be specified");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .createInstance(CreateServiceInstanceRequest.builder()
                    .serviceInstanceName("test-service-instance")
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class CreateServiceInstanceNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .createInstance(CreateServiceInstanceRequest.builder()
                    .serviceInstanceName("test-service-instance")
                    .serviceName("test-service")
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class CreateUserProvidedServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestUserProvidedCreateServiceInstance(this.cloudFoundryClient,
                TEST_SPACE_ID,
                "test-user-provided-service-instance",
                Collections.singletonMap("test-credential-key", "test-credential-value"),
                "test-route-url",
                "test-syslog-url",
                "test-user-provided-service-instance-id");
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest.builder()
                    .name("test-user-provided-service-instance")
                    .credential("test-credential-key", "test-credential-value")
                    .routeServiceUrl("test-route-url")
                    .syslogDrainUrl("test-syslog-url")
                    .build());
        }

    }

    public static final class GetServiceInstanceManaged extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceServiceInstanceByNameManaged(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service");
            requestListServiceBindings(this.cloudFoundryClient, "test-service-instance-id", "test-application-id");
            requestService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestApplication(this.cloudFoundryClient, "test-application-id", "test-application");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertEquals(fill(ServiceInstance.builder())
                    .application("test-application")
                    .documentationUrl("test-documentation-url")
                    .id("test-service-instance-id")
                    .lastOperation("test-type")
                    .plan("test-service-plan")
                    .name("test-service-instance-name")
                    .tag("test-tag")
                    .type(ServiceInstanceType.MANAGED)
                    .build());
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .getInstance(org.cloudfoundry.operations.services.GetServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .build());
        }

    }

    public static final class GetServiceInstanceNoInstances extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceServiceInstanceByNameEmpty(this.cloudFoundryClient, "test-invalid-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Service instance test-invalid-name does not exist");
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .getInstance(org.cloudfoundry.operations.services.GetServiceInstanceRequest.builder()
                    .name("test-invalid-name")
                    .build());
        }

    }

    public static final class GetServiceInstanceNoSpace extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .getInstance(org.cloudfoundry.operations.services.GetServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .build());
        }

    }

    public static final class GetServiceInstanceUserProvided extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceServiceInstanceByNameUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceBindings(this.cloudFoundryClient, "test-service-instance-id", "test-application-id");
            requestApplication(this.cloudFoundryClient, "test-application-id", "test-application");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceInstance.builder()
                    .application("test-application")
                    .id("test-service-instance-id")
                    .name("test-service-instance-name")
                    .type(ServiceInstanceType.USER_PROVIDED)
                    .build());
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .getInstance(org.cloudfoundry.operations.services.GetServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .build());
        }

    }

    public static final class ListInstances extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceServiceInstancesTwo(this.cloudFoundryClient, TEST_SPACE_ID, "test-service-instance1", "test-service-instance2");
            requestListServiceBindings(this.cloudFoundryClient, "test-service-instance1-id");
            requestListServiceBindings(this.cloudFoundryClient, "test-service-instance2-id", "test-application-id");
            requestServicePlan(this.cloudFoundryClient, "test-service-instance1-plan-id", "test-service-plan", "test-service");
            requestServicePlan(this.cloudFoundryClient, "test-service-instance2-plan-id", "test-service-plan", "test-service");
            requestService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestApplication(this.cloudFoundryClient, "test-application-id", "test-application");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceInstance.builder()
                    .name("test-service-instance1")
                    .id("test-service-instance1-id")
                    .type(ServiceInstanceType.USER_PROVIDED)
                    .build())
                .assertEquals(fill(ServiceInstance.builder())
                    .application("test-application")
                    .documentationUrl("test-documentation-url")
                    .id("test-service-instance2-id")
                    .lastOperation("test-type")
                    .plan("test-service-plan")
                    .name("test-service-instance2")
                    .tag("test-tag")
                    .type(ServiceInstanceType.MANAGED)
                    .build());
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .listInstances();
        }

    }

    public static final class ListInstancesNoInstances extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceServiceInstancesEmpty(this.cloudFoundryClient, TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .listInstances();
        }

    }

    public static final class ListInstancesNoSpace extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .listInstances();
        }

    }

    public static final class ListOffering extends AbstractOperationsApiTest<ServiceOffering> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceServicesTwo(this.cloudFoundryClient, TEST_SPACE_ID, "test-service1", "test-service2");
            requestListServicePlans(this.cloudFoundryClient, "test-service1-id", "test-service1-plan", "test-service1-plan-id");
            requestListServicePlans(this.cloudFoundryClient, "test-service2-id", "test-service2-plan", "test-service2-plan-id");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceOffering> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceOffering.builder()
                    .description("test-service1-description")
                    .id("test-service1-id")
                    .label("test-service1")
                    .planName("test-service1-plan")
                    .build())
                .assertEquals(ServiceOffering.builder()
                    .description("test-service2-description")
                    .id("test-service2-id")
                    .label("test-service2")
                    .planName("test-service2-plan")
                    .build());
        }

        @Override
        protected Publisher<ServiceOffering> invoke() {
            return this.services
                .listOfferings(GetMarketplaceRequest.builder().build());
        }

    }

    public static final class ListOfferingByService extends AbstractOperationsApiTest<ServiceOffering> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceServiceByName(this.cloudFoundryClient, TEST_SPACE_ID, "test-service");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-service-plan", "test-service-plan-id");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceOffering> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceOffering.builder()
                    .description("test-service-description")
                    .id("test-service-id")
                    .label("test-service")
                    .planName("test-service-plan")
                    .build());
        }

        @Override
        protected Publisher<ServiceOffering> invoke() {
            return this.services
                .listOfferings(GetMarketplaceRequest.builder()
                    .serviceName("test-service")
                    .build());
        }

    }

    public static final class UnbindServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestSpaceServiceInstanceByName(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
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
            requestSpaceServiceInstanceByName(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
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
