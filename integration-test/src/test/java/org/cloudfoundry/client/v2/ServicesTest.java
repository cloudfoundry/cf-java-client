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
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.DeleteServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.ListServiceServicePlansRequest;
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

public final class ServicesTest extends AbstractIntegrationTest {

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

        getServiceId(this.cloudFoundryClient, serviceName)
            .flatMapMany(serviceId -> deleteServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId, serviceId)
                .thenMany(Mono.just(serviceId)))
            .flatMap(serviceId -> this.cloudFoundryClient.services()
                .delete(DeleteServiceRequest.builder()
                    .async(true)
                    .serviceId(serviceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .thenMany(requestListServices(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(resource -> serviceName.equals(ResourceUtils.getEntity(resource).getLabel()))
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

        getServiceId(this.cloudFoundryClient, serviceName)
            .flatMapMany(serviceId -> deleteServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId, serviceId)
                .thenMany(Mono.just(serviceId)))
            .flatMap(serviceId -> this.cloudFoundryClient.services()
                .delete(DeleteServiceRequest.builder()
                    .async(false)
                    .serviceId(serviceId)
                    .build())
                .thenReturn(serviceBrokerMetadata.serviceBrokerId))
            .thenMany(requestListServices(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(resource -> serviceName.equals(ResourceUtils.getEntity(resource).getLabel()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(1));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void deletePurge() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        getServiceId(this.cloudFoundryClient, serviceName)
            .flatMap(serviceId -> this.cloudFoundryClient.services()
                .delete(DeleteServiceRequest.builder()
                    .async(true)
                    .purge(true)
                    .serviceId(serviceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .thenMany(requestListServices(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(resource -> serviceName.equals(ResourceUtils.getEntity(resource).getLabel()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(1));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> getServiceId(this.cloudFoundryClient, this.serviceName))
            .flatMap(serviceId -> this.cloudFoundryClient.services()
                .get(GetServiceRequest.builder()
                    .serviceId(serviceId)
                    .build()))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .page(page)
                        .build()))
                .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId())))
            .map(response -> response.getEntity().getLabel())
            .filter(label -> this.serviceName.equals(label))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByActive() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .active(true)
                        .page(page)
                        .build()))
                .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId())))
            .map(response -> response.getEntity().getLabel())
            .filter(label -> this.serviceName.equals(label))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByLabels() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .label(this.serviceName)
                        .page(page)
                        .build()))
                .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId())))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceBrokerIds() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .list(ListServicesRequest.builder()
                        .serviceBrokerId(serviceBrokerId)
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getLabel())
            .filter(label -> this.serviceName.equals(label))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listNoneFound() {
        PaginationUtils.requestClientV2Resources(page -> this.cloudFoundryClient.services()
            .list(ListServicesRequest.builder()
                .label("unmatched-filter")
                .page(page)
                .build()))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlans() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> getServiceId(this.cloudFoundryClient, this.serviceName))
            .flatMapMany(serviceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .serviceId(serviceId)
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlansFilterByActive() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> getServiceId(this.cloudFoundryClient, this.serviceName))
            .flatMapMany(serviceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .active(true)
                        .serviceId(serviceId)
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlansFilterByServiceInstanceIds() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .flatMap(serviceBrokerId -> Mono.zip(
                getServiceId(this.cloudFoundryClient, this.serviceName),
                this.spaceId
            ))
            .flatMap(function((serviceId, spaceId) -> Mono.zip(
                Mono.just(serviceId),
                createServiceInstanceId(this.cloudFoundryClient, serviceInstanceName, serviceId, spaceId)
            )))
            .flatMapMany(function((serviceId, serviceInstanceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .serviceId(serviceId)
                        .serviceInstanceId(serviceInstanceId)
                        .page(page)
                        .build()))))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicePlansNoneFound() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> getServiceId(this.cloudFoundryClient, this.serviceName))
            .flatMapMany(serviceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.services()
                    .listServicePlans(ListServiceServicePlansRequest.builder()
                        .serviceId(serviceId)
                        .serviceInstanceId("unmatched-filter")
                        .page(page)
                        .build())))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Flux<DeleteServicePlanResponse> deleteServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceId) {
        return listServicePlanIds(cloudFoundryClient, serviceBrokerId, serviceId)
            .flatMap(servicePlanId -> requestDeleteServicePlan(cloudFoundryClient, servicePlanId));
    }

    private static Mono<String> getServiceId(CloudFoundryClient cloudFoundryClient, String serviceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .page(page)
                    .build()))
            .filter(resource -> serviceName.equals(ResourceUtils.getEntity(resource).getLabel()))
            .single()
            .map(ResourceUtils::getId);
    }

    private static Flux<String> listServicePlanIds(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .serviceId(serviceId)
                    .build()))
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String name, String servicePlanId, String spaceId) {
        return cloudFoundryClient.serviceInstances()
            .create(CreateServiceInstanceRequest.builder()
                .name(name)
                .servicePlanId(servicePlanId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<DeleteServicePlanResponse> requestDeleteServicePlan(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return cloudFoundryClient.servicePlans()
            .delete(DeleteServicePlanRequest.builder()
                .servicePlanId(servicePlanId)
                .build());
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .listServicePlans(ListServiceServicePlansRequest.builder()
                    .page(page)
                    .serviceId(serviceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .serviceBrokerId(serviceBrokerId)
                    .page(page)
                    .build()));
    }

    private Mono<String> createServiceInstanceId(CloudFoundryClient cloudFoundryClient, String name, String serviceId, String spaceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .single()
            .map(ResourceUtils::getId)
            .flatMap(servicePlanId -> requestCreateServiceInstance(cloudFoundryClient, name, servicePlanId, spaceId)
                .map(ResourceUtils::getId));
    }

}
