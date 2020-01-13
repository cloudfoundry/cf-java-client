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
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysRequest;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServiceKeysTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

    @Autowired
    private Mono<String> spaceId;

    @SuppressWarnings("unchecked")
    @Test
    public void create() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceKeys()
                .create(CreateServiceKeyRequest.builder()
                    .parameter("test-key", "test-value")
                    .name(serviceKeyName)
                    .serviceInstanceId(serviceInstanceId)
                    .build())
                .map(ResourceUtils::getId))
            .flatMap(serviceKeyId -> requestGetServiceKey(this.cloudFoundryClient, serviceKeyId))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(serviceKeyName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)))
            .flatMap(serviceInstanceId -> createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName))
            .flatMap(serviceKeyId -> this.cloudFoundryClient.serviceKeys()
                .delete(DeleteServiceKeyRequest.builder()
                    .serviceKeyId(serviceKeyId)
                    .build()))
            .thenMany(requestListServiceKeys(this.cloudFoundryClient))
            .filter(response -> serviceKeyName.equals(ResourceUtils.getEntity(response).getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)))
            .flatMap(serviceInstanceId -> createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName))
            .flatMap(serviceKeyId -> this.cloudFoundryClient.serviceKeys()
                .get(GetServiceKeyRequest.builder()
                    .serviceKeyId(serviceKeyId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(serviceKeyName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)))
            .flatMap(serviceInstanceId -> createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceKeys()
                    .list(ListServiceKeysRequest.builder()
                        .page(page)
                        .build()))
                .map(response -> ResourceUtils.getEntity(response).getName()))
            .filter(serviceKeyName::equals)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)))
            .flatMap(serviceInstanceId -> createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceKeys()
                    .list(ListServiceKeysRequest.builder()
                        .name(serviceKeyName)
                        .page(page)
                        .build()))
                .map(response -> ResourceUtils.getEntity(response).getName()))
            .as(StepVerifier::create)
            .expectNext(serviceKeyName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceInstanceId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)))
            .delayUntil(serviceInstanceId -> createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName))
            .flatMapMany(serviceInstanceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceKeys()
                    .list(ListServiceKeysRequest.builder()
                        .page(page)
                        .serviceInstanceId(serviceInstanceId)
                        .build()))
                .map(response -> ResourceUtils.getEntity(response).getName()))
            .as(StepVerifier::create)
            .expectNext(serviceKeyName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createServiceInstanceId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceInstanceName, String serviceName, String spaceId) {
        return getPlanId(cloudFoundryClient, serviceBrokerId, serviceName)
            .flatMap(planId -> requestCreateServiceInstance(cloudFoundryClient, planId, serviceInstanceName, spaceId))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceKeyId(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return requestCreateServiceKey(cloudFoundryClient, serviceInstanceId, serviceKeyName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getPlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceBrokerId, serviceName)
            .single()
            .map(ResourceUtils::getId)
            .flatMapMany(serviceId -> requestListServicePlans(cloudFoundryClient, serviceId))
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String planId, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient.serviceInstances()
            .create(CreateServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .servicePlanId(planId)
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

    private static Mono<GetServiceKeyResponse> requestGetServiceKey(CloudFoundryClient cloudFoundryClient, String serviceKeyId) {
        return cloudFoundryClient.serviceKeys()
            .get(GetServiceKeyRequest.builder()
                .serviceKeyId(serviceKeyId)
                .build());
    }

    private static Flux<ServiceKeyResource> requestListServiceKeys(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.serviceKeys()
            .list(ListServiceKeysRequest.builder()
                .page(page)
                .build()));
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
                    .page(page)
                    .label(serviceName)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

}
