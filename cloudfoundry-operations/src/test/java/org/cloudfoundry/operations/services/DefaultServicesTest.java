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
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
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
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyEntity;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityEntity;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
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
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceEntity;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
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
import static org.mockito.Mockito.when;

public final class DefaultServicesTest {

    private static void requestApplications(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(applicationName)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .build())
                    .build()));
    }

    private static void requestApplicationsEmpty(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .name(applicationName)
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .build()));
    }

    private static void requestApplicationsListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        when(cloudFoundryClient.applicationsV2()
            .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                .page(1)
                .applicationId(applicationId)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListApplicationServiceBindingsResponse.builder())
                    .resource(fill(ServiceBindingResource.builder(), "service-binding-")
                        .entity(ServiceBindingEntity.builder()
                            .applicationId(applicationId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, Map<String, Object> parameters) {
        when(cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceBindingResponse.builder(), "service-binding-")
                    .build()));
    }

    private static void requestCreateServiceBindingError(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId, Map<String, Object> parameters, int code) {
        when(cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .parameters(parameters)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .error(new CloudFoundryException(code, "test-exception-description", "test-exception-errorCode")));
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
                    .metadata(fill(Metadata.builder())
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

    private static void requestCreateServiceKey(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey, Map<String, Object> parameters) {
        when(cloudFoundryClient.serviceKeys()
            .create(org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .name(serviceKey)
                .parameters(parameters)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceKeyResponse.builder(), "service-key")
                    .build()));
    }

    private static void requestCreateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String spaceId, String name, Map<String, Object> credentials, String routeServiceUrl,
                                                                 String syslogDrainUrl, String userProvidedServiceInstanceId) {
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
                    .metadata(fill(Metadata.builder())
                        .id(userProvidedServiceInstanceId)
                        .build())
                    .entity(fill(UserProvidedServiceInstanceEntity.builder())
                        .build())
                    .build()));
    }

    private static void requestDeleteServiceBinding(CloudFoundryClient cloudFoundryClient, String serviceBindingId) {
        when(cloudFoundryClient.serviceBindingsV2()
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

    private static void requestDeleteServiceKey(CloudFoundryClient cloudFoundryClient, String serviceKeyId) {
        when(cloudFoundryClient.serviceKeys()
            .delete(org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest.builder()
                .serviceKeyId(serviceKeyId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestDeleteUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                .userProvidedServiceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String application) {
        when(cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetApplicationResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(applicationId)
                        .build())
                    .entity(fill(ApplicationEntity.builder())
                        .name(application)
                        .build())
                    .build())
            );
    }

    private static void requestGetService(CloudFoundryClient cloudFoundryClient, String serviceId, String service) {
        when(cloudFoundryClient.services()
            .get(GetServiceRequest.builder()
                .serviceId(serviceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServiceResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(serviceId)
                        .build())
                    .entity(fill(ServiceEntity.builder())
                        .extra("{\"displayName\":\"test-value\",\"longDescription\":\"test-value\",\"documentationUrl\":\"test-documentation-url\",\"supportUrl\":\"test-value\"}")
                        .label(service)
                        .build())
                    .build()));
    }

    private static void requestGetServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String state) {
        when(cloudFoundryClient.serviceInstances()
            .get(org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServiceInstanceResponse.builder())
                    .metadata(fill(Metadata.builder())
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

    private static void requestGetServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId, String servicePlan, String service) {
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

    private static void requestGetServicePlanEmpty(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        when(cloudFoundryClient.servicePlans()
            .get(GetServicePlanRequest.builder()
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetServicePlanResponse.builder())
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

    private static void requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String applicationId) {
        when(cloudFoundryClient.serviceBindingsV2()
            .list(ListServiceBindingsRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBindingsResponse.builder())
                    .resource(fill(ServiceBindingResource.builder(), "service-binding")
                        .entity(ServiceBindingEntity.builder()
                            .applicationId(applicationId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceBindingsEmpty(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.serviceBindingsV2()
            .list(ListServiceBindingsRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBindingsResponse.builder())
                    .build()));
    }

    private static void requestListServiceInstanceServiceKeys(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey, String credentialKey, String credentialValue) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .name(serviceKey)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .resource(fill(ServiceKeyResource.builder(), "service-key-")
                        .entity(ServiceKeyEntity.builder()
                            .name(serviceKey)
                            .credential(credentialKey, credentialValue)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceInstanceServiceKeys(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String credentialKey, String credentialValue) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .resource(fill(ServiceKeyResource.builder(), "service-key-")
                        .entity(fill(ServiceKeyEntity.builder(), "service-key-entity-")
                            .credential(credentialKey, credentialValue)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceInstanceServiceKeysEmpty(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKey) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .name(serviceKey)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .build()));
    }

    private static void requestListServiceInstanceServiceKeysEmpty(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        when(cloudFoundryClient.serviceInstances()
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .page(1)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceInstanceServiceKeysResponse.builder())
                    .build()));
    }

    private static void requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(fill(UnionServiceInstanceResource.builder(), "service-instance-")
                        .build())
                    .build()));
    }

    private static void requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId, String servicePlanId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(fill(UnionServiceInstanceResource.builder(), "service-instance-")
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .servicePlanId(servicePlanId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceInstancesEmpty(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .build()));
    }

    private static void requestListServiceInstancesEmpty(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .returnUserProvidedServiceInstances(true)
                .spaceId(spaceId)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .build()));
    }

    private static void requestListServiceInstancesManaged(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id("test-service-instance-id")
                            .build())
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

    private static void requestListServiceInstancesTwo(CloudFoundryClient cloudFoundryClient, String spaceId, String instanceName1, String instanceName2) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(instanceName1 + "-id")
                            .build())
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .type("user_provided_service_instance")
                            .dashboardUrl(null)
                            .name(instanceName1)
                            .tags(null)
                            .servicePlanId(null)
                            .lastOperation(null)
                            .build())
                        .build())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(instanceName2 + "-id")
                            .build())
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

    private static void requestListServiceInstancesUserProvided(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(UnionServiceInstanceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id("test-service-instance-id")
                            .build())
                        .entity(UnionServiceInstanceEntity.builder()
                            .type("user_provided_service_instance")
                            .name(serviceName)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder())
                    .resource(fill(ServicePlanVisibilityResource.builder())
                        .entity(ServicePlanVisibilityEntity.builder()
                            .organizationId(organizationId)
                            .servicePlanId(servicePlanId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicePlanVisibilitiesEmpty(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        when(cloudFoundryClient.servicePlanVisibilities()
            .list(ListServicePlanVisibilitiesRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .servicePlanId(servicePlanId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlanVisibilitiesResponse.builder())
                    .build()));
    }

    private static void requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId, String plan, String planId) {
        when(cloudFoundryClient.servicePlans()
            .list(ListServicePlansRequest.builder()
                .serviceId(serviceId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlansResponse.builder())
                    .resource(ServicePlanResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(planId)
                            .build())
                        .entity(fill(ServicePlanEntity.builder())
                            .name(plan)
                            .build())
                        .build())
                    .build())

            );
    }

    private static void requestListServicePlansNotPublic(CloudFoundryClient cloudFoundryClient, String serviceId, String plan, String planId) {
        when(cloudFoundryClient.servicePlans()
            .list(ListServicePlansRequest.builder()
                .serviceId(serviceId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServicePlansResponse.builder())
                    .resource(ServicePlanResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(planId)
                            .build())
                        .entity(fill(ServicePlanEntity.builder())
                            .name(plan)
                            .publiclyVisible(false)
                            .build())
                        .build())
                    .build())

            );
    }

    private static void requestListServices(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceLabel) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .page(1)
                .label(serviceLabel)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServicesResponse.builder())
                    .resource(ServiceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(serviceLabel + "-id")
                            .build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel + "-description")
                            .label(serviceLabel)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServicesTwo(CloudFoundryClient cloudFoundryClient, String spaceId, String serviceLabel1, String serviceLabel2) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServicesResponse.builder())
                    .resource(ServiceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(serviceLabel1 + "-id")
                            .build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel1 + "-description")
                            .label(serviceLabel1)
                            .build())
                        .build())
                    .resource(ServiceResource.builder()
                        .metadata(fill(Metadata.builder())
                            .id(serviceLabel2 + "-id")
                            .build())
                        .entity(fill(ServiceEntity.builder())
                            .description(serviceLabel2 + "-description")
                            .label(serviceLabel2)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListUserProvidedServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceName, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .returnUserProvidedServiceInstances(true)
                .name(serviceName)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServiceInstancesResponse.builder())
                    .resource(fill(UnionServiceInstanceResource.builder(), "user-provided-service-instance-")
                        .entity(fill(UnionServiceInstanceEntity.builder())
                            .type("user_provided_service_instance")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestRenameServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String newName) {
        when(cloudFoundryClient.serviceInstances()
            .update(org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest.builder()
                .name(newName)
                .serviceInstanceId(serviceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateServiceInstanceResponse.builder())
                    .build()));
    }

    private static void requestUpdateServiceInstance(CloudFoundryClient cloudFoundryClient, Map<String, Object> parameter, String serviceInstanceId, String servicePlanId, List<String> tags) {
        when(cloudFoundryClient.serviceInstances()
            .update(org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .parameters(parameter)
                .servicePlanId(servicePlanId)
                .serviceInstanceId(serviceInstanceId)
                .tags(tags)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateServiceInstanceResponse.builder())
                    .build()));
    }

    private static void requestUpdateUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, Map<String, Object> credentials, String syslogDrainUrl, String userProvidedServiceInstanceId) {
        when(cloudFoundryClient.userProvidedServiceInstances()
            .update(org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest.builder()
                .credentials(credentials)
                .syslogDrainUrl(syslogDrainUrl)
                .userProvidedServiceInstanceId(userProvidedServiceInstanceId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateUserProvidedServiceInstanceResponse.builder())
                    .build()));
    }

    public static final class BindServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestCreateServiceBinding(this.cloudFoundryClient, "test-application-id", "test-service-instance-id", Collections.singletonMap("test-parameter-key", "test-parameter-value"));
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

    public static final class BindServiceInstanceAlreadyBound extends AbstractOperationsApiTest<Void> {

        private static final int CF_SERVICE_ALREADY_BOUND = 90003;

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestCreateServiceBindingError(this.cloudFoundryClient, "test-application-id", "test-service-instance-id", Collections.singletonMap("test-parameter-key", "test-parameter-value"),
                CF_SERVICE_ALREADY_BOUND);
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

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplicationsEmpty(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
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

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestListServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
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

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServices(this.cloudFoundryClient, TEST_SPACE_ID, "test-service");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
            requestCreateServiceInstance(this.cloudFoundryClient, TEST_SPACE_ID, "test-plan-id", "test-service-instance", null, null, "test-service-instance-id", "in progress");
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

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServices(this.cloudFoundryClient, TEST_SPACE_ID, "test-service");
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

    public static final class CreateServiceInstanceNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

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

    public static final class CreateServiceKey extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance", TEST_SPACE_ID);
            requestCreateServiceKey(this.cloudFoundryClient, "test-service-instance-id", "test-service-key",
                Collections.singletonMap("test-parameter-key", "test-parameter-value"));
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .createServiceKey(CreateServiceKeyRequest.builder()
                    .serviceInstanceName("test-service-instance")
                    .serviceKeyName("test-service-key")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .build());
        }

    }

    public static final class CreateServiceKeyNoServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-does-not-exist", TEST_SPACE_ID);
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber.assertError(IllegalArgumentException.class,
                String.format("Service instance %s does not exist", "test-service-instance-does-not-exist"));
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .createServiceKey(CreateServiceKeyRequest.builder()
                    .serviceInstanceName("test-service-instance-does-not-exist")
                    .serviceKeyName("test-service-key")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .build());
        }

    }

    public static final class CreateUserProvidedServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestCreateUserProvidedServiceInstance(this.cloudFoundryClient,
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

    public static final class DeleteServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestDeleteUserProvidedServiceInstance(this.cloudFoundryClient, "test-service-instance-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .deleteInstance(DeleteServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .build());
        }

    }

    public static final class DeleteServiceInstanceNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .deleteInstance(DeleteServiceInstanceRequest.builder()
                    .name("test-invalid-name")
                    .build());
        }

    }

    public static final class DeleteServiceInstanceNotFound extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesEmpty(this.cloudFoundryClient, "test-invalid-name", TEST_SPACE_ID);

        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Service instance test-invalid-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .deleteInstance(DeleteServiceInstanceRequest.builder()
                    .name("test-invalid-name")
                    .build());
        }
    }

    public static final class DeleteServiceKey extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceInstanceServiceKeys(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-name", "key", "val");
            requestDeleteServiceKey(this.cloudFoundryClient, "test-service-key-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .deleteServiceKey(DeleteServiceKeyRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .serviceKeyName("test-service-key-name")
                    .build());
        }

    }

    public static final class DeleteServiceKeyNoServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber.assertError(IllegalArgumentException.class,
                String.format("Service instance %s does not exist", "test-service-instance"));
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .deleteServiceKey(DeleteServiceKeyRequest.builder()
                    .serviceInstanceName("test-service-instance")
                    .serviceKeyName("test-service-key")
                    .build());
        }

    }

    public static final class DeleteServiceKeyNoServiceKey extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceInstanceServiceKeysEmpty(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-not-found");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber.assertError(IllegalArgumentException.class,
                String.format("Service key %s does not exist", "test-service-key-not-found"));
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .deleteServiceKey(DeleteServiceKeyRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .serviceKeyName("test-service-key-not-found")
                    .build());
        }

    }

    public static final class GetServiceInstanceManaged extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesManaged(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service");
            requestListServiceBindings(this.cloudFoundryClient, "test-service-instance-id", "test-application-id");
            requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestGetApplication(this.cloudFoundryClient, "test-application-id", "test-application");
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
                .getInstance(GetServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .build());
        }

    }

    public static final class GetServiceInstanceNoInstances extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesEmpty(this.cloudFoundryClient, "test-invalid-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Service instance test-invalid-name does not exist");
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .getInstance(GetServiceInstanceRequest.builder()
                    .name("test-invalid-name")
                    .build());
        }

    }

    public static final class GetServiceInstanceNoSpace extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

        @Override
        protected void assertions(TestSubscriber<ServiceInstance> testSubscriber) {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_SPACE_ID");
        }

        @Override
        protected Publisher<ServiceInstance> invoke() {
            return this.services
                .getInstance(GetServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .build());
        }

    }

    public static final class GetServiceInstanceUserProvided extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesUserProvided(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceBindings(this.cloudFoundryClient, "test-service-instance-id", "test-application-id");
            requestGetApplication(this.cloudFoundryClient, "test-application-id", "test-application");
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
                .getInstance(GetServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .build());
        }

    }

    public static final class GetServiceKey extends AbstractOperationsApiTest<ServiceKey> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceInstanceServiceKeys(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-name", "key", "val");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceKey> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceKey.builder()
                    .credential("key", "val")
                    .id("test-service-key-id")
                    .name("test-service-key-name")
                    .build());
        }

        @Override
        protected Publisher<ServiceKey> invoke() {
            return this.services
                .getServiceKey(GetServiceKeyRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .serviceKeyName("test-service-key-name")
                    .build());
        }

    }

    public static final class GetServiceKeyNoKeys extends AbstractOperationsApiTest<ServiceKey> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceInstanceServiceKeysEmpty(this.cloudFoundryClient, "test-service-instance-id", "test-service-key-not-found");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceKey> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Service key test-service-key-not-found does not exist");
        }

        @Override
        protected Publisher<ServiceKey> invoke() {
            return this.services
                .getServiceKey(GetServiceKeyRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .serviceKeyName("test-service-key-not-found")
                    .build());
        }

    }

    public static final class ListInstances extends AbstractOperationsApiTest<ServiceInstance> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesTwo(this.cloudFoundryClient, TEST_SPACE_ID, "test-service-instance1", "test-service-instance2");
            requestListServiceBindingsEmpty(this.cloudFoundryClient, "test-service-instance1-id");
            requestListServiceBindings(this.cloudFoundryClient, "test-service-instance2-id", "test-application-id");
            requestGetServicePlan(this.cloudFoundryClient, "test-service-instance1-plan-id", "test-service-plan", "test-service");
            requestGetServicePlan(this.cloudFoundryClient, "test-service-instance2-plan-id", "test-service-plan", "test-service");
            requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestGetApplication(this.cloudFoundryClient, "test-application-id", "test-application");
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

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesEmpty(this.cloudFoundryClient, TEST_SPACE_ID);
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

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

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

    public static final class ListServiceKeys extends AbstractOperationsApiTest<ServiceKey> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceInstanceServiceKeys(this.cloudFoundryClient, "test-service-instance-id", "key", "val");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceKey> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceKey.builder()
                    .credential("key", "val")
                    .id("test-service-key-id")
                    .name("test-service-key-entity-name")
                    .build());
        }

        @Override
        protected Publisher<ServiceKey> invoke() {
            return this.services
                .listServiceKeys(ListServiceKeysRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .build());
        }

    }

    public static final class ListServiceKeysEmpty extends AbstractOperationsApiTest<ServiceKey> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestListServiceInstanceServiceKeysEmpty(this.cloudFoundryClient, "test-service-instance-id");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceKey> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<ServiceKey> invoke() {
            return this.services
                .listServiceKeys(ListServiceKeysRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .build());
        }

    }

    public static final class ListServiceKeysNoServiceInstance extends AbstractOperationsApiTest<ServiceKey> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstancesEmpty(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<ServiceKey> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class,
                    String.format("Service instance %s does not exist", "test-service-instance-name"));
        }

        @Override
        protected Publisher<ServiceKey> invoke() {
            return this.services
                .listServiceKeys(ListServiceKeysRequest.builder()
                    .serviceInstanceName("test-service-instance-name")
                    .build());
        }

    }

    public static final class ListServiceOfferings extends AbstractOperationsApiTest<ServiceOffering> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServicesTwo(this.cloudFoundryClient, TEST_SPACE_ID, "test-service1", "test-service2");
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
                    .servicePlan(ServicePlan.builder()
                        .description("test-description")
                        .free(true)
                        .id("test-service1-plan-id")
                        .name("test-service1-plan")
                        .build())
                    .build())
                .assertEquals(ServiceOffering.builder()
                    .description("test-service2-description")
                    .id("test-service2-id")
                    .label("test-service2")
                    .servicePlan(ServicePlan.builder()
                        .description("test-description")
                        .free(true)
                        .id("test-service2-plan-id")
                        .name("test-service2-plan")
                        .build())
                    .build());
        }

        @Override
        protected Publisher<ServiceOffering> invoke() {
            return this.services
                .listServiceOfferings(ListServiceOfferingsRequest.builder().build());
        }

    }

    public static final class ListServiceOfferingsSingle extends AbstractOperationsApiTest<ServiceOffering> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServices(this.cloudFoundryClient, TEST_SPACE_ID, "test-service");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-service-plan", "test-service-plan-id");
        }

        @Override
        protected void assertions(TestSubscriber<ServiceOffering> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceOffering.builder()
                    .description("test-service-description")
                    .id("test-service-id")
                    .label("test-service")
                    .servicePlan(ServicePlan.builder()
                        .description("test-description")
                        .free(true)
                        .id("test-service-plan-id")
                        .name("test-service-plan")
                        .build())
                    .build());
        }

        @Override
        protected Publisher<ServiceOffering> invoke() {
            return this.services
                .listServiceOfferings(ListServiceOfferingsRequest.builder()
                    .serviceName("test-service")
                    .build());
        }

    }

    public static final class RenameServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestRenameServiceInstance(this.cloudFoundryClient, "test-service-instance-id", "test-service-instance-new-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .renameInstance(RenameServiceInstanceRequest.builder()
                    .name("test-service-instance-name")
                    .newName("test-service-instance-new-name")
                    .build());
        }

    }

    public static final class UnbindServiceInstance extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestApplicationsListServiceBindings(this.cloudFoundryClient, "test-application-id", "test-service-instance-id");
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

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestApplications(this.cloudFoundryClient, "test-application-name", TEST_SPACE_ID);
            requestListServiceInstances(this.cloudFoundryClient, "test-service-instance-name", TEST_SPACE_ID);
            requestApplicationsListServiceBindings(this.cloudFoundryClient, "test-application-id", "test-service-instance-id");
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

    public static final class UnbindServiceNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), MISSING_SPACE_ID);

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

    public static final class UpdateService extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
            requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service");
            requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
            requestUpdateServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-parameter-key", "test-parameter-value"), "test-service-instance-id", "test-plan-id",
                Collections.singletonList("test-tag"));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .serviceInstanceName("test-service")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class UpdateServiceNoParameters extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
            requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service");
            requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-service-plan-id");
            requestUpdateServiceInstance(this.cloudFoundryClient, null, "test-service-instance-id", "test-service-plan-id", Collections.singletonList("test-tag"));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .planName("test-plan")
                    .serviceInstanceName("test-service")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class UpdateServiceNoPlan extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .parameter("test-parameter-key", "test-parameter-value")
                    .serviceInstanceName("test-service")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class UpdateServiceNoTags extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
            requestGetServicePlan(this.cloudFoundryClient, "test-service-plan-id", "test-service-plan", "test-service");
            requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestListServicePlans(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
            requestUpdateServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-parameter-key", "test-parameter-value"), "test-service-instance-id", "test-plan-id", null);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .serviceInstanceName("test-service")
                    .build());
        }

    }

    public static final class UpdateServiceNotPublic extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-plan-id");
            requestGetServicePlan(this.cloudFoundryClient, "test-plan-id", "test-service-plan", "test-service");
            requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestListServicePlansNotPublic(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
            requestListServicePlanVisibilities(this.cloudFoundryClient, "test-organization-id", "test-plan-id");
            requestUpdateServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-parameter-key", "test-parameter-value"), "test-service-instance-id", "test-plan-id",
                Collections.singletonList("test-tag"));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .serviceInstanceName("test-service")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class UpdateServiceNotVisible extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-plan-id");
            requestGetServicePlan(this.cloudFoundryClient, "test-plan-id", "test-service-plan", "test-service");
            requestGetService(this.cloudFoundryClient, "test-service-id", "test-service");
            requestListServicePlansNotPublic(this.cloudFoundryClient, "test-service-id", "test-plan", "test-plan-id");
            requestListServicePlanVisibilitiesEmpty(this.cloudFoundryClient, "test-organization-id", "test-plan-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Service Plan test-plan is not visible to your organization");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .serviceInstanceName("test-service")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class UpdateServiceUserProvided extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "");
            requestGetServicePlanEmpty(this.cloudFoundryClient, "test-service-plan-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Plan does not exist for the test-name service");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .planName("test-plan")
                    .parameter("test-parameter-key", "test-parameter-value")
                    .serviceInstanceName("test-service")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class UpdateServiceUserProvidedNoPlan extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }


        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateInstance(UpdateServiceInstanceRequest.builder()
                    .parameter("test-parameter-key", "test-parameter-value")
                    .serviceInstanceName("test-service")
                    .tag("test-tag")
                    .build());
        }

    }

    public static final class UpdateUserProvidedService extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListUserProvidedServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID);
            requestUpdateUserProvidedServiceInstance(this.cloudFoundryClient, Collections.singletonMap("test-credential-key", "test-credential-value"), "syslog-url",
                "test-user-provided-service-instance-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateUserProvidedInstance(UpdateUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceName("test-service")
                    .credential("test-credential-key", "test-credential-value")
                    .syslogDrainUrl("syslog-url")
                    .build());
        }

    }

    public static final class UpdateUserProvidedServiceNotUserProvided extends AbstractOperationsApiTest<Void> {

        private final DefaultServices services = new DefaultServices(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceInstances(this.cloudFoundryClient, "test-service", TEST_SPACE_ID, "test-service-plan-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "User provided service instance test-service does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.services
                .updateUserProvidedInstance(UpdateUserProvidedServiceInstanceRequest.builder()
                    .userProvidedServiceInstanceName("test-service")
                    .build());
        }

    }

}
