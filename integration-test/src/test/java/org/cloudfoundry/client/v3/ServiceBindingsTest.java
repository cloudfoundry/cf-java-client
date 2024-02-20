/*
 * Copyright 2013-2022 the original author or authors.
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

package org.cloudfoundry.client.v3;

import static org.cloudfoundry.client.v3.ApplicationsTest.createApplicationId;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.GetServiceBindingDetailsRequest;
import org.cloudfoundry.client.v3.servicebindings.GetServiceBindingDetailsResponse;
import org.cloudfoundry.client.v3.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v3.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v3.servicebindings.ServiceBinding;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingRelationships;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingType;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstance;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceRelationships;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceType;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v3.serviceplans.ServicePlan;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_11)
public class ServiceBindingsTest extends AbstractIntegrationTest {

    @Autowired private CloudFoundryClient cloudFoundryClient;

    @Autowired private Mono<String> serviceBrokerId;

    @Autowired private String serviceName;

    @Autowired private Mono<String> spaceId;

    @Test
    public void createServiceKeyFromManagedServiceInstance() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        this.spaceId
                .flatMap(
                        spaceId ->
                                createManagedServiceInstanceId(
                                        this.cloudFoundryClient,
                                        this.serviceBrokerId,
                                        serviceInstanceName,
                                        this.serviceName,
                                        spaceId))
                .flatMap(
                        serviceInstanceId ->
                                this.cloudFoundryClient
                                        .serviceBindingsV3()
                                        .create(
                                                CreateServiceBindingRequest.builder()
                                                        .type(ServiceBindingType.KEY)
                                                        .name(serviceKeyName)
                                                        .relationships(
                                                                ServiceBindingRelationships
                                                                        .builder()
                                                                        .serviceInstance(
                                                                                ToOneRelationship
                                                                                        .builder()
                                                                                        .data(
                                                                                                Relationship
                                                                                                        .builder()
                                                                                                        .id(
                                                                                                                serviceInstanceId)
                                                                                                        .build())
                                                                                        .build())
                                                                        .build())
                                                        .build())
                                        .map(response -> response.getJobId().get())
                                        .flatMap(
                                                jobId ->
                                                        JobUtils.waitForCompletion(
                                                                this.cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                jobId)))
                .thenMany(requestListServiceBindings(this.cloudFoundryClient, serviceInstanceName))
                .map(ServiceBinding::getName)
                .as(StepVerifier::create)
                .expectNext(serviceKeyName)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createAppBindingFromUserProvidedServiceInstance() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
                .flatMap(
                        spaceId ->
                                Mono.zip(
                                        createUserProvidedServiceInstanceId(
                                                this.cloudFoundryClient,
                                                serviceInstanceName,
                                                Collections.emptyMap(),
                                                spaceId),
                                        createApplicationId(
                                                this.cloudFoundryClient, applicationName, spaceId)))
                .flatMap(
                        function(
                                (serviceInstanceId, appId) ->
                                        this.cloudFoundryClient
                                                .serviceBindingsV3()
                                                .create(
                                                        CreateServiceBindingRequest.builder()
                                                                .type(
                                                                        ServiceBindingType
                                                                                .APPLICATION)
                                                                .metadata(
                                                                        Metadata.builder()
                                                                                .label(
                                                                                        "test-label",
                                                                                        "test-label-value")
                                                                                .build())
                                                                .relationships(
                                                                        ServiceBindingRelationships
                                                                                .builder()
                                                                                .serviceInstance(
                                                                                        ToOneRelationship
                                                                                                .builder()
                                                                                                .data(
                                                                                                        Relationship
                                                                                                                .builder()
                                                                                                                .id(
                                                                                                                        serviceInstanceId)
                                                                                                                .build())
                                                                                                .build())
                                                                                .application(
                                                                                        ToOneRelationship
                                                                                                .builder()
                                                                                                .data(
                                                                                                        Relationship
                                                                                                                .builder()
                                                                                                                .id(
                                                                                                                        appId)
                                                                                                                .build())
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .map(
                                                        response ->
                                                                response.getServiceBinding()
                                                                        .get())))
                .map(ServiceBindingResource::getMetadata)
                .map(Metadata::getLabels)
                .as(StepVerifier::create)
                .expectNext(Collections.singletonMap("test-label", "test-label-value"))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteForManagedService() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();

        this.spaceId
                .flatMap(
                        spaceId ->
                                createManagedServiceInstanceId(
                                        this.cloudFoundryClient,
                                        this.serviceBrokerId,
                                        serviceInstanceName,
                                        this.serviceName,
                                        spaceId))
                .flatMap(
                        serviceInstanceId ->
                                createServiceKey(
                                        this.cloudFoundryClient,
                                        serviceKeyName,
                                        serviceInstanceName,
                                        serviceInstanceId))
                .map(ServiceBinding::getId)
                .flatMap(
                        bindingId ->
                                this.cloudFoundryClient
                                        .serviceBindingsV3()
                                        .delete(
                                                DeleteServiceBindingRequest.builder()
                                                        .serviceBindingId(bindingId)
                                                        .build()))
                .hasElement()
                .as(StepVerifier::create)
                .expectNext(true)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteForUserProvidedService() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
                .flatMap(
                        spaceId ->
                                Mono.zip(
                                        createUserProvidedServiceInstanceId(
                                                this.cloudFoundryClient,
                                                serviceInstanceName,
                                                Collections.emptyMap(),
                                                spaceId),
                                        createApplicationId(
                                                this.cloudFoundryClient, applicationName, spaceId)))
                .flatMap(
                        function(
                                (serviceInstanceId, appId) ->
                                        createServiceBindingForUserProvidedService(
                                                this.cloudFoundryClient, appId, serviceInstanceId)))
                .map(ServiceBinding::getId)
                .flatMap(
                        bindingId ->
                                this.cloudFoundryClient
                                        .serviceBindingsV3()
                                        .delete(
                                                DeleteServiceBindingRequest.builder()
                                                        .serviceBindingId(bindingId)
                                                        .build()))
                .hasElement()
                .as(StepVerifier::create)
                .expectNext(false)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listForApplication() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
                .flatMap(
                        spaceId ->
                                Mono.zip(
                                        createUserProvidedServiceInstanceId(
                                                this.cloudFoundryClient,
                                                serviceInstanceName,
                                                Collections.emptyMap(),
                                                spaceId),
                                        createApplicationId(
                                                this.cloudFoundryClient, applicationName, spaceId)))
                .flatMap(
                        function(
                                (serviceInstanceId, appId) ->
                                        createServiceBindingForUserProvidedService(
                                                this.cloudFoundryClient, appId, serviceInstanceId)))
                .thenMany(
                        this.cloudFoundryClient
                                .serviceBindingsV3()
                                .list(
                                        ListServiceBindingsRequest.builder()
                                                .appName(applicationName)
                                                .build()))
                .map(ListServiceBindingsResponse::getResources)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
                .flatMap(
                        spaceId ->
                                Mono.zip(
                                        createUserProvidedServiceInstanceId(
                                                this.cloudFoundryClient,
                                                serviceInstanceName,
                                                Collections.emptyMap(),
                                                spaceId),
                                        createApplicationId(
                                                this.cloudFoundryClient, applicationName, spaceId)))
                .flatMap(
                        function(
                                (serviceInstanceId, appId) ->
                                        createServiceBindingForUserProvidedService(
                                                this.cloudFoundryClient, appId, serviceInstanceId)))
                .map(ServiceBinding::getId)
                .flatMap(
                        bindingId ->
                                this.cloudFoundryClient
                                        .serviceBindingsV3()
                                        .get(
                                                GetServiceBindingRequest.builder()
                                                        .serviceBindingId(bindingId)
                                                        .build()))
                .map(ServiceBinding::getMetadata)
                .map(Metadata::getLabels)
                .as(StepVerifier::create)
                .expectNext(Collections.singletonMap("test-label", "test-label-value"))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getDetails() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
                .flatMap(
                        spaceId ->
                                Mono.zip(
                                        createUserProvidedServiceInstanceId(
                                                this.cloudFoundryClient,
                                                serviceInstanceName,
                                                Collections.singletonMap("foo", "bar"),
                                                spaceId),
                                        createApplicationId(
                                                this.cloudFoundryClient, applicationName, spaceId)))
                .flatMap(
                        function(
                                (serviceInstanceId, appId) ->
                                        createServiceBindingForUserProvidedService(
                                                this.cloudFoundryClient, appId, serviceInstanceId)))
                .map(ServiceBinding::getId)
                .flatMap(
                        bindingId ->
                                this.cloudFoundryClient
                                        .serviceBindingsV3()
                                        .getDetails(
                                                GetServiceBindingDetailsRequest.builder()
                                                        .serviceBindingId(bindingId)
                                                        .build()))
                .map(GetServiceBindingDetailsResponse::getCredentials)
                .as(StepVerifier::create)
                .expectNext(Collections.singletonMap("foo", "bar"))
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createManagedServiceInstanceId(
            CloudFoundryClient cloudFoundryClient,
            Mono<String> serviceBrokerId,
            String serviceInstanceName,
            String serviceName,
            String spaceId) {
        return serviceBrokerId
                .flatMap(brokerId -> getPlanId(cloudFoundryClient, brokerId, serviceName))
                .flatMap(
                        planId ->
                                requestCreateManagedServiceInstance(
                                        cloudFoundryClient, planId, serviceInstanceName, spaceId))
                .map(response -> response.getJobId().get())
                .flatMap(
                        jobId ->
                                JobUtils.waitForCompletion(
                                        cloudFoundryClient, Duration.ofMinutes(5), jobId))
                .thenMany(requestListServiceInstances(cloudFoundryClient, serviceInstanceName))
                .single()
                .map(ServiceInstance::getId);
    }

    private static Mono<String> getPlanId(
            CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return requestListServicePlans(cloudFoundryClient, serviceName, serviceBrokerId)
                .single()
                .map(ServicePlan::getId);
    }

    private static Flux<? extends ServicePlan> requestListServicePlans(
            CloudFoundryClient cloudFoundryClient, String serviceName, String serviceBrokerId) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .servicePlansV3()
                                .list(
                                        ListServicePlansRequest.builder()
                                                .page(page)
                                                .serviceOfferingName(serviceName)
                                                .serviceBrokerId(serviceBrokerId)
                                                .build()));
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateManagedServiceInstance(
            CloudFoundryClient cloudFoundryClient,
            String planId,
            String serviceInstanceName,
            String spaceId) {
        return cloudFoundryClient
                .serviceInstancesV3()
                .create(
                        CreateServiceInstanceRequest.builder()
                                .name(serviceInstanceName)
                                .type(ServiceInstanceType.MANAGED)
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(spaceId)
                                                                                .build())
                                                                .build())
                                                .servicePlan(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(planId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Mono<String> createUserProvidedServiceInstanceId(
            CloudFoundryClient cloudFoundryClient,
            String serviceInstanceName,
            Map<String, Object> credentials,
            String spaceId) {
        return requestCreateUserProvidedServiceInstance(
                        cloudFoundryClient, serviceInstanceName, credentials, spaceId)
                .map(response -> response.getServiceInstance().get())
                .map(ServiceInstance::getId);
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateUserProvidedServiceInstance(
            CloudFoundryClient cloudFoundryClient,
            String serviceInstanceName,
            Map<String, Object> credentials,
            String spaceId) {
        return cloudFoundryClient
                .serviceInstancesV3()
                .create(
                        CreateServiceInstanceRequest.builder()
                                .type(ServiceInstanceType.USER_PROVIDED)
                                .name(serviceInstanceName)
                                .credentials(credentials)
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(spaceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Flux<? extends ServiceInstance> requestListServiceInstances(
            CloudFoundryClient cloudFoundryClient, String serviceInstanceName) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .serviceInstancesV3()
                                .list(
                                        ListServiceInstancesRequest.builder()
                                                .page(page)
                                                .serviceInstanceName(serviceInstanceName)
                                                .build()));
    }

    private static Flux<? extends ServiceBinding> requestListServiceBindings(
            CloudFoundryClient cloudFoundryClient, String serviceInstanceName) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .serviceBindingsV3()
                                .list(
                                        ListServiceBindingsRequest.builder()
                                                .page(page)
                                                .serviceInstanceName(serviceInstanceName)
                                                .build()));
    }

    private static Mono<? extends ServiceBinding> createServiceKey(
            CloudFoundryClient cloudFoundryClient,
            String serviceKeyName,
            String serviceInstanceName,
            String serviceInstanceId) {
        return cloudFoundryClient
                .serviceBindingsV3()
                .create(
                        CreateServiceBindingRequest.builder()
                                .type(ServiceBindingType.KEY)
                                .name(serviceKeyName)
                                .relationships(
                                        ServiceBindingRelationships.builder()
                                                .serviceInstance(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        serviceInstanceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build())
                .map(response -> response.getJobId().get())
                .flatMap(
                        jobId ->
                                JobUtils.waitForCompletion(
                                        cloudFoundryClient, Duration.ofMinutes(5), jobId))
                .thenMany(requestListServiceBindings(cloudFoundryClient, serviceInstanceName))
                .single();
    }

    private static Mono<ServiceBinding> createServiceBindingForUserProvidedService(
            CloudFoundryClient cloudFoundryClient, String appId, String serviceInstanceId) {
        return cloudFoundryClient
                .serviceBindingsV3()
                .create(
                        CreateServiceBindingRequest.builder()
                                .type(ServiceBindingType.APPLICATION)
                                .metadata(
                                        Metadata.builder()
                                                .label("test-label", "test-label-value")
                                                .build())
                                .relationships(
                                        ServiceBindingRelationships.builder()
                                                .serviceInstance(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        serviceInstanceId)
                                                                                .build())
                                                                .build())
                                                .application(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(appId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build())
                .map(response -> response.getServiceBinding().get());
    }
}
