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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.ServiceBrokerUtils;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.ServiceBrokerUtils.createServiceBroker;
import static org.cloudfoundry.ServiceBrokerUtils.deleteServiceBroker;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServicePlansTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private String planName;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void delete() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlans()
                .delete(DeleteServicePlanRequest.builder()
                    .async(true)
                    .servicePlanId(servicePlanId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), ResourceUtils.getEntity(job)))
            .thenMany(requestListServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(response -> serviceName.equals(ResourceUtils.getEntity(response).getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlans()
                .delete(DeleteServicePlanRequest.builder()
                    .async(false)
                    .servicePlanId(servicePlanId)
                    .build()))
            .thenMany(requestListServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(response -> serviceName.equals(ResourceUtils.getEntity(response).getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> getServicePlanId(this.cloudFoundryClient, serviceBrokerId))
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlans()
                .get(GetServicePlanRequest.builder()
                    .servicePlanId(servicePlanId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        PaginationUtils
            .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .filter(name -> this.planName.equals(name))
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByActive() {
        PaginationUtils
            .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .active(true)
                    .page(page)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .filter(name -> this.planName.equals(name))
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceBrokerId() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .list(ListServicePlansRequest.builder()
                        .page(page)
                        .serviceBrokerId(serviceBrokerId)
                        .build())))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .filter(planName -> this.planName.equals(planName))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceId() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> getServiceId(this.cloudFoundryClient, this.serviceName))
            .flatMapMany(serviceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .list(ListServicePlansRequest.builder()
                        .page(page)
                        .serviceId(serviceId)
                        .build())))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceInstanceId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> Mono.zip(
                getServicePlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .flatMap(function((servicePlanId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, servicePlanId, spaceId)))
            .flatMapMany(serviceInstanceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .list(ListServicePlansRequest.builder()
                        .page(page)
                        .serviceInstanceId(serviceInstanceId)
                        .build())))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstances() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> Mono.zip(
                getServicePlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .flatMap(function((servicePlanId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, servicePlanId, spaceId)
                .thenReturn(servicePlanId)))
            .flatMapMany(servicePlanId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                        .page(page)
                        .servicePlanId(servicePlanId)
                        .build())))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .filter(serviceInstanceName::equals)
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByName() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> Mono.zip(
                getServicePlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .flatMap(function((servicePlanId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, servicePlanId, spaceId)
                .thenReturn(servicePlanId)))
            .flatMapMany(servicePlanId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                        .page(page)
                        .name(serviceInstanceName)
                        .servicePlanId(servicePlanId)
                        .build())))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByServiceBindingId() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> Mono.zip(
                getServicePlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .flatMap(function((servicePlanId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, servicePlanId, spaceId),
                Mono.just(servicePlanId)
            )))
            .flatMap(function((applicationId, serviceInstanceId, servicePlanId) -> Mono.zip(
                createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId),
                Mono.just(servicePlanId)
            )))
            .flatMapMany(function((serviceBindingId, servicePlanId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                        .page(page)
                        .serviceBindingId(serviceBindingId)
                        .servicePlanId(servicePlanId)
                        .build()))))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByServiceKeyId() {
        String serviceKeyName = this.nameFactory.getServiceKeyName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> Mono.zip(
                getServicePlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .flatMap(function((servicePlanId, spaceId) -> Mono.zip(
                createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, servicePlanId, spaceId),
                Mono.just(servicePlanId)
            )))
            .flatMap(function((serviceInstanceId, servicePlanId) -> Mono.zip(
                createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName),
                Mono.just(servicePlanId)
            )))
            .flatMapMany(function((serviceKeyId, servicePlanId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                        .page(page)
                        .serviceKeyId(serviceKeyId)
                        .servicePlanId(servicePlanId)
                        .build()))))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterBySpaceId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> Mono.zip(
                getServicePlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .delayUntil(function((servicePlanId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, servicePlanId, spaceId)))
            .flatMapMany(function((servicePlanId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlans()
                    .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .servicePlanId(servicePlanId)
                        .build()))))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .filter(serviceInstanceName::equals)
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlans()
                .update(UpdateServicePlanRequest.builder()
                    .publiclyVisible(false)
                    .servicePlanId(servicePlanId)
                    .build()))
            .thenMany(requestListServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(response -> planName.equals(ResourceUtils.getEntity(response).getName()))
            .map(response -> ResourceUtils.getEntity(response).getPubliclyVisible())
            .as(StepVerifier::create)
            .expectNext(false)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceBindingId(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceInstanceId(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String servicePlanId, String spaceId) {
        return requestCreateServiceInstance(cloudFoundryClient, serviceInstanceName, servicePlanId, spaceId)
            .then(requestListServiceInstances(cloudFoundryClient, serviceInstanceName)
                .single()
                .map(ResourceUtils::getId));
    }

    private static Mono<String> createServiceKeyId(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return requestCreateServiceKey(cloudFoundryClient, serviceInstanceId, serviceKeyName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getServiceId(CloudFoundryClient cloudFoundryClient, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceName)
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getServicePlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return requestListServicePlans(cloudFoundryClient, serviceBrokerId)
            .filter(resource -> "test-plan-description".equals(ResourceUtils.getEntity(resource).getDescription()))
            .map(ResourceUtils::getId)
            .single();
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                .memory(64)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateServiceBindingResponse> requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String servicePlanId, String spaceId) {
        return cloudFoundryClient.serviceInstances()
            .create(CreateServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .servicePlanId(servicePlanId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateServiceKeyResponse> requestCreateServiceKey(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return cloudFoundryClient.serviceKeys()
            .create(CreateServiceKeyRequest.builder()
                .name(serviceKeyName)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Flux<ServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceInstanceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .list(ListServiceInstancesRequest.builder()
                    .name(serviceInstanceName)
                    .build()));
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .page(page)
                    .label(serviceName)
                    .build()));
    }

}
