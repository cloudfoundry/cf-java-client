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
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceRouteResponse;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceRoutesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UnbindServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.LastOperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServiceInstancesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void bindRoute() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.when(
                createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId),
                createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId),
                Mono.just(spaceId)
            )))
            .flatMap(function((domainId, serviceInstanceId, spaceId) -> Mono.when(
                createRouteId(this.cloudFoundryClient, domainId, hostName, spaceId),
                Mono.just(serviceInstanceId))
            ))
            .flatMap(function((routeId, serviceInstanceId) -> this.cloudFoundryClient.serviceInstances()
                .bindRoute(BindServiceInstanceRouteRequest.builder()
                    .routeId(routeId)
                    .serviceInstanceId(serviceInstanceId)
                    .build())
                .then(Mono.just(serviceInstanceId))))
            .flatMapMany(serviceInstanceId -> requestListRoutes(this.cloudFoundryClient, serviceInstanceId)
                .filter(route -> serviceInstanceId.equals(route.getEntity().getServiceInstanceId())))
            .map(route -> ResourceUtils.getEntity(route).getHost())
            .as(StepVerifier::create)
            .expectNext(hostName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> Mono.when(
                getPlanId(this.cloudFoundryClient, serviceBrokerId),
                Mono.just(spaceId)
            )))
            .flatMap(function((planId, spaceId) -> this.cloudFoundryClient.serviceInstances()
                .create(CreateServiceInstanceRequest.builder()
                    .name(serviceInstanceName)
                    .servicePlanId(planId)
                    .spaceId(spaceId)
                    .build())))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createAcceptsIncomplete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> Mono.when(
                getPlanId(this.cloudFoundryClient, serviceBrokerId),
                Mono.just(spaceId)
            )))
            .flatMap(function((planId, spaceId) -> this.cloudFoundryClient.serviceInstances()
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

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .async(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), (JobEntity) ResourceUtils.getEntity(job)))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAcceptsIncomplete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .acceptsIncomplete(true)
                    .async(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .flatMap(response -> LastOperationUtils.waitForCompletion(Duration.ofMinutes(5), () -> this.cloudFoundryClient.serviceInstances()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(ResourceUtils.getId(response))
                    .build())
                .map(r2 -> ResourceUtils.getEntity(r2).getLastOperation())))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAcceptsIncompleteAsyncFalse() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .acceptsIncomplete(true)
                    .async(false)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .flatMap(response -> LastOperationUtils.waitForCompletion(Duration.ofMinutes(5), () -> this.cloudFoundryClient.serviceInstances()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(ResourceUtils.getId(response))
                    .build())
                .map(r2 -> ResourceUtils.getEntity(r2).getLastOperation())))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .async(false)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deletePurge() {
        String domainName = this.nameFactory.getDomainName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono
                .when(
                    Mono.just(organizationId),
                    createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId),
                    Mono.just(spaceId)
                )))
            .flatMap(function((organizationId, serviceInstanceId, spaceId) -> createAndBindRoute(this.cloudFoundryClient, domainName, organizationId, spaceId, serviceInstanceId)
                .then(Mono.just(serviceInstanceId))))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .async(true)
                    .purge(true)
                    .recursive(false)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), (JobEntity) ResourceUtils.getEntity(job)))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName)
                .filter(resource -> serviceInstanceName.equals(resource.getEntity().getName())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteRecursive() {
        String domainName = this.nameFactory.getDomainName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono
                .when(
                    Mono.just(organizationId),
                    createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId),
                    Mono.just(spaceId)
                )))
            .flatMap(function((organizationId, serviceInstanceId, spaceId) -> createAndBindRoute(this.cloudFoundryClient, domainName, organizationId, spaceId, serviceInstanceId)
                .then(Mono.just(serviceInstanceId))))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .delete(DeleteServiceInstanceRequest.builder()
                    .async(true)
                    .purge(false)
                    .recursive(true)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), (JobEntity) ResourceUtils.getEntity(job)))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, serviceInstanceName)
                .filter(resource -> serviceInstanceName.equals(resource.getEntity().getName())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .map(response -> response.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getNotFound() {
        this.cloudFoundryClient.serviceInstances()
            .get(GetServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-ServiceInstanceNotFound\\([0-9]+\\): The service instance could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .page(page)
                        .build())))
            .filter(resource -> serviceInstanceName.equals(ResourceUtils.getEntity(resource).getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .name(serviceInstanceName)
                        .page(page)
                        .build())))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId)
                .then(Mono.just(organizationId))))
            .flatMapMany(organizationId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .filter(resource -> serviceInstanceName.equals(ResourceUtils.getEntity(resource).getName()))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceBindingId() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                    createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId)
                ))
            .flatMap(function((applicationId, serviceInstanceId) -> createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)))
            .flatMapMany(serviceBindingId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .serviceBindingId(serviceBindingId)
                        .page(page)
                        .build())))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceKeyId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName))
            .flatMapMany(serviceKeyId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .serviceKeyId(serviceKeyId)
                        .page(page)
                        .build())))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServicePlanId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.serviceBrokerId, this.spaceId)
            .flatMap(function((serviceBrokerId, spaceId) -> Mono.when(
                getPlanId(this.cloudFoundryClient, serviceBrokerId),
                Mono.just(spaceId)
            )))
            .flatMap(function((planId, spaceId) -> requestCreateServiceInstance(this.cloudFoundryClient, planId, serviceInstanceName, spaceId)
                .then(Mono.just(planId))))
            .flatMapMany(planId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .servicePlanId(planId)
                        .page(page)
                        .build())))
            .filter(resource -> serviceInstanceName.equals(ResourceUtils.getEntity(resource).getName()))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBySpaceId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId)
                .then(Mono.just(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .list(ListServiceInstancesRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .build())))
            .filter(resource -> serviceInstanceName.equals(ResourceUtils.getEntity(resource).getName()))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(serviceInstanceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindings() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                    createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId)
                ))
            .flatMap(function((applicationId, serviceInstanceId) -> createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)
                .then(Mono.just(Tuples.of(applicationId, serviceInstanceId)))))
            .flatMapMany(function((applicationId, serviceInstanceId) -> Mono
                .when(
                    Mono.just(applicationId),
                    PaginationUtils
                        .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                            .listServiceBindings(ListServiceInstanceServiceBindingsRequest.builder()
                                .serviceInstanceId(serviceInstanceId)
                                .page(page)
                                .build()))
                        .single()
                        .map(resource -> resource.getEntity().getApplicationId())
                )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceBindingsFilterByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                    createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId)
                ))
            .flatMap(function((applicationId, serviceInstanceId) -> createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId)
                .then(Mono.just(Tuples.of(applicationId, serviceInstanceId)))))
            .flatMapMany(function((applicationId, serviceInstanceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceInstances()
                    .listServiceBindings(ListServiceInstanceServiceBindingsRequest.builder()
                        .applicationId(applicationId)
                        .serviceInstanceId(serviceInstanceId)
                        .page(page)
                        .build()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unbindRoute() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        Mono.when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.when(
                createPrivateDomainId(this.cloudFoundryClient, domainName, organizationId),
                createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, spaceId),
                Mono.just(spaceId)
            )))
            .flatMap(function((domainId, serviceInstanceId, spaceId) -> Mono.when(
                createRouteId(this.cloudFoundryClient, domainId, hostName, spaceId),
                Mono.just(serviceInstanceId))
            ))
            .flatMap(function((routeId, serviceInstanceId) -> requestBindServiceInstanceRoute(this.cloudFoundryClient, routeId, serviceInstanceId)
                .then(this.cloudFoundryClient.serviceInstances()
                    .unbindRoute(UnbindServiceInstanceRouteRequest.builder()
                        .routeId(routeId)
                        .serviceInstanceId(serviceInstanceId)
                        .build()))
                .then(Mono.just(serviceInstanceId))))
            .flatMapMany(serviceInstanceId -> requestListRoutes(this.cloudFoundryClient, serviceInstanceId)
                .filter(route -> serviceInstanceId.equals(route.getEntity().getServiceInstanceId())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String oldServiceInstanceName = this.nameFactory.getServiceInstanceName();
        String newServiceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, oldServiceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .update(UpdateServiceInstanceRequest.builder()
                    .name(newServiceInstanceName)
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, newServiceInstanceName))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateEmptyCollections() {
        String oldServiceInstanceName = this.nameFactory.getServiceInstanceName();
        String newServiceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, oldServiceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstances()
                .update(UpdateServiceInstanceRequest.builder()
                    .name(newServiceInstanceName)
                    .parameters(Collections.emptyMap())
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .thenMany(requestListServiceInstances(this.cloudFoundryClient, newServiceInstanceName))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<BindServiceInstanceRouteResponse> createAndBindRoute(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId, String spaceId, String
        serviceInstanceId) {
        return createPrivateDomainId(cloudFoundryClient, domainName, organizationId)
            .flatMap(domainId -> createRouteId(cloudFoundryClient, domainId, spaceId))
            .flatMap(routeId -> requestBindServiceInstanceRoute(cloudFoundryClient, routeId, serviceInstanceId));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createPrivateDomainId(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        return requestCreatePrivateDomain(cloudFoundryClient, name, organizationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String hostName, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, hostName, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceBindingId(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceInstanceId(CloudFoundryClient cloudFoundryClient, Mono<String> serviceBrokerId, String serviceInstanceName, String spaceId) {
        return serviceBrokerId
            .flatMap(s -> getPlanId(cloudFoundryClient, s))
            .flatMap(planId -> requestCreateServiceInstance(cloudFoundryClient, planId, serviceInstanceName, spaceId))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceKeyId(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return requestCreateServiceKey(cloudFoundryClient, serviceInstanceId, serviceKeyName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getPlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return requestListServices(cloudFoundryClient, serviceBrokerId)
            .single()
            .map(ResourceUtils::getId)
            .flatMapMany(serviceId -> requestListServicePlans(cloudFoundryClient, serviceId))
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<BindServiceInstanceRouteResponse> requestBindServiceInstanceRoute(CloudFoundryClient cloudFoundryClient, String routeId, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstances()
            .bindRoute(BindServiceInstanceRouteRequest.builder()
                .routeId(routeId)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreatePrivateDomainResponse> requestCreatePrivateDomain(CloudFoundryClient cloudFoundryClient, String name, String organizationId) {
        return cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(name)
                .owningOrganizationId(organizationId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String hostName, String spaceId) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .host(hostName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
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

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String planId, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient.serviceInstances()
            .create(CreateServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .parameter("test-key", "test-value")
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

    private static Flux<RouteResource> requestListRoutes(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .listRoutes(ListServiceInstanceRoutesRequest.builder()
                    .page(page)
                    .serviceInstanceId(serviceInstanceId)
                    .build()));
    }

    private static Flux<ServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceInstanceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.serviceInstances()
                .list(ListServiceInstancesRequest.builder()
                    .name(serviceInstanceName)
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

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

}
