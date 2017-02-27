/*
 * Copyright 2013-2017 the original author or authors.
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
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.LastOperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServiceInstancesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private Mono<String> spaceId;

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void bindToRoute() {
        //
    }

    @Test
    public void create() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .then(serviceBrokerId -> Mono.when(
                getPlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .then(function((planId, spaceId) -> this.cloudFoundryClient.serviceInstances()
                .create(CreateServiceInstanceRequest.builder()
                    .name(serviceInstanceName)
                    .servicePlanId(planId)
                    .spaceId(spaceId)
                    .build())))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .name(serviceInstanceName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createAcceptsIncomplete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.serviceBrokerId
            .then(serviceBrokerId -> Mono.when(
                getPlanId(this.cloudFoundryClient, serviceBrokerId),
                this.spaceId
            ))
            .then(function((planId, spaceId) -> this.cloudFoundryClient.serviceInstances()
                .create(CreateServiceInstanceRequest.builder()
                    .acceptsIncomplete(true)
                    .name(serviceInstanceName)
                    .servicePlanId(planId)
                    .spaceId(spaceId)
                    .build())))
            .as(StepVerifier::create)
            .assertNext(response -> assertThat(ResourceUtils.getEntity(response).getLastOperation().getType()).isEqualTo("create"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        createServiceInstance(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.spaceId)
            .then(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .async(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, (JobEntity) ResourceUtils.getEntity(job)))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .name(serviceInstanceName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAcceptsIncomplete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        createServiceInstance(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.spaceId)
            .then(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .acceptsIncomplete(true)
                    .async(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .then(response -> LastOperationUtils.waitForCompletion(() -> this.cloudFoundryClient.serviceInstances()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(ResourceUtils.getId(response))
                    .build())
                .map(r2 -> ResourceUtils.getEntity(r2).getLastOperation())))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .name(serviceInstanceName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAcceptsIncompleteAsyncFalse() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        createServiceInstance(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.spaceId)
            .then(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .acceptsIncomplete(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .then(response -> LastOperationUtils.waitForCompletion(() -> this.cloudFoundryClient.serviceInstances()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(ResourceUtils.getId(response))
                    .build())
                .map(r2 -> ResourceUtils.getEntity(r2).getLastOperation())))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .name(serviceInstanceName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        createServiceInstance(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.spaceId)
            .then(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .name(serviceInstanceName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void deletePurge() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void deleteRecursive() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void get() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void getNotFound() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void list() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByGatewayName() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByName() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByOrganizationId() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByServiceBindingId() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByServiceKeyId() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByServicePlanId() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterBySpaceId() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServiceBindings() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServiceBindingsFilterByApplicationId() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void update() {
        // TODO: ensure empty collections are tested on request
    }

    private static Mono<String> createServiceInstance(CloudFoundryClient cloudFoundryClient, Mono<String> serviceBrokerId, String serviceInstanceName, Mono<String> spaceId) {
        return serviceBrokerId
            .then(s -> Mono.when(
                getPlanId(cloudFoundryClient, s),
                spaceId
            ))
            .then(function((planId, s) -> cloudFoundryClient.serviceInstances()
                .create(CreateServiceInstanceRequest.builder()
                    .name(serviceInstanceName)
                    .servicePlanId(planId)
                    .spaceId(s)
                    .build())))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getPlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return requestListServices(cloudFoundryClient, serviceBrokerId)
            .single()
            .map(ResourceUtils::getId)
            .flatMap(serviceId -> requestListServicePlans(cloudFoundryClient, serviceId))
            .single()
            .map(ResourceUtils::getId);
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .serviceId(serviceId)
                    .page(page)
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

}
