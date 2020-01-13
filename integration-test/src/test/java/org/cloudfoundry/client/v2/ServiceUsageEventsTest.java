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
import org.cloudfoundry.NameFactory;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ListServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.PurgeAndReseedServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEventResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServiceUsageEventsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void get() {
        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> seedEvents(this.cloudFoundryClient, this.nameFactory, serviceBrokerId, this.serviceName, spaceId)))
            .then(getFirstEvent(this.cloudFoundryClient))
            .flatMap(resource -> Mono.zip(
                Mono.just(resource)
                    .map(ResourceUtils::getId),
                this.cloudFoundryClient.serviceUsageEvents()
                    .get(GetServiceUsageEventRequest.builder()
                        .serviceUsageEventId(ResourceUtils.getId(resource))
                        .build())
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> seedEvents(this.cloudFoundryClient, this.nameFactory, serviceBrokerId, this.serviceName, spaceId)))
            .then(getFirstEvent(this.cloudFoundryClient))
            .flatMap(resource -> Mono.zip(
                Mono.just(resource),
                this.cloudFoundryClient.serviceUsageEvents()
                    .list(ListServiceUsageEventsRequest.builder()
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAfterServiceUsageEventId() {
        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> seedEvents(this.cloudFoundryClient, this.nameFactory, serviceBrokerId, this.serviceName, spaceId)))
            .then(getFirstEvent(this.cloudFoundryClient))
            .flatMap(resource -> Mono.zip(
                getSecondEvent(this.cloudFoundryClient),
                this.cloudFoundryClient.serviceUsageEvents()
                    .list(ListServiceUsageEventsRequest.builder()
                        .afterServiceUsageEventId(ResourceUtils.getId(resource))
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceId() {
        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> seedEvents(this.cloudFoundryClient, this.nameFactory, serviceBrokerId, this.serviceName, spaceId)))
            .then(getFirstEventWithServiceId(this.cloudFoundryClient))
            .flatMap(resource -> Mono.zip(
                Mono.just(resource),
                this.cloudFoundryClient.serviceUsageEvents()
                    .list(ListServiceUsageEventsRequest.builder()
                        .serviceId(ResourceUtils.getEntity(resource).getServiceId())
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceInstanceType() {
        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> seedEvents(this.cloudFoundryClient, this.nameFactory, serviceBrokerId, this.serviceName, spaceId)))
            .then(getFirstEvent(this.cloudFoundryClient))
            .flatMap(resource -> Mono.zip(
                Mono.just(resource),
                this.cloudFoundryClient.serviceUsageEvents()
                    .list(ListServiceUsageEventsRequest.builder()
                        .serviceInstanceType(ResourceUtils.getEntity(resource).getServiceInstanceType())
                        .build())
                    .flatMapMany(ResourceUtils::getResources)
                    .next()
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listNoneFound() {
        this.cloudFoundryClient.serviceUsageEvents()
            .list(ListServiceUsageEventsRequest.builder()
                .serviceId("test-service-id")
                .build())
            .flatMapMany(ResourceUtils::getResources)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void purgeAndReseed() {
        this.cloudFoundryClient.serviceUsageEvents()
            .purgeAndReseed(PurgeAndReseedServiceUsageEventsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<ServiceUsageEventResource> getFirstEvent(CloudFoundryClient cloudFoundryClient) {
        return listServiceUsageEvents(cloudFoundryClient)
            .next();
    }

    private static Mono<ServiceUsageEventResource> getFirstEventWithServiceId(CloudFoundryClient cloudFoundryClient) {
        return listServiceUsageEvents(cloudFoundryClient)
            .filter(resource -> ResourceUtils.getEntity(resource).getServiceId() != null)
            .next();
    }

    private static Mono<String> getPlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceBrokerId, serviceName)
            .single()
            .map(ResourceUtils::getId)
            .flatMapMany(serviceId -> requestListServicePlans(cloudFoundryClient, serviceId))
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<ServiceUsageEventResource> getSecondEvent(CloudFoundryClient cloudFoundryClient) {
        return listServiceUsageEvents(cloudFoundryClient)
            .skip(1)
            .next();
    }

    private static Flux<ServiceUsageEventResource> listServiceUsageEvents(CloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.serviceUsageEvents()
            .list(ListServiceUsageEventsRequest.builder()
                .build())
            .flatMapMany(ResourceUtils::getResources);
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String planId, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient.serviceInstances()
            .create(CreateServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .parameter("test-key", "test-value")
                .servicePlanId(planId)
                .spaceId(spaceId)
                .build());
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceId(serviceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .label(serviceName)
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

    private static Mono<Void> seedEvents(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory, String serviceBrokerId, String serviceName, String spaceId) {
        String serviceInstanceName1 = nameFactory.getServiceInstanceName();
        String serviceInstanceName2 = nameFactory.getServiceInstanceName();

        return getPlanId(cloudFoundryClient, serviceBrokerId, serviceName)
            .flatMap(planId -> requestCreateServiceInstance(cloudFoundryClient, planId, serviceInstanceName1, spaceId)
                .then(requestCreateServiceInstance(cloudFoundryClient, planId, serviceInstanceName2, spaceId)))
            .then();
    }

}
