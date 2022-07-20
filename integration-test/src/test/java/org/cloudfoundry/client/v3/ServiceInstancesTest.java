/*
 * Copyright 2013-2021 the original author or authors.
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

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetUserProvidedCredentialsRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceRelationships;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceType;
import org.cloudfoundry.client.v3.serviceinstances.ShareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.ShareServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.UnshareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceofferings.ListServiceOfferingsRequest;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingResource;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v3.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_1)
public final class ServiceInstancesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void createManagedServiceInstance() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        this.spaceId
            .flatMap(spaceId -> createManagedServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstancesV3()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .filter(resource -> serviceInstanceName.equals(resource.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getUserProvidedServiceCredentials() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("foo", "bar");
        this.spaceId
            .flatMap(spaceId -> createUserProvidedServiceInstance(this.cloudFoundryClient, serviceInstanceName, credentials, spaceId))
            .map(getServiceInstanceResponse -> getServiceInstanceResponse.getServiceInstance().get().getId())
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstancesV3()
                .getUserProvidedCredentials(GetUserProvidedCredentialsRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .filter(resource -> credentials.equals(resource.getCredentials()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        this.spaceId
            .flatMap(spaceId -> createManagedServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstancesV3()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .filter(resource -> serviceInstanceName.equals(resource.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        this.spaceId
            .flatMap(spaceId -> deleteServiceInstanceByName(this.cloudFoundryClient, serviceInstanceName, spaceId))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        this.spaceId
            .flatMap(spaceId -> createManagedServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName, spaceId))
            .flatMap(serviceInstanceId -> this.cloudFoundryClient.serviceInstancesV3()
                .get(GetServiceInstanceRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build()))
            .filter(resource -> serviceInstanceName.equals(resource.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        this.spaceId
            .flatMap(spaceId -> updateServiceInstanceByName(this.cloudFoundryClient, serviceInstanceName, spaceId))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        this.spaceId
            .flatMap(spaceId -> createManagedServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName, spaceId))
            .thenMany(PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.serviceInstancesV3()
                    .list(ListServiceInstancesRequest.builder()
                        .page(page)
                        .build())))
            .filter(resource -> serviceInstanceName.equals(resource.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedSpaces() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono.zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                createManagedServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName + "-shareable", spaceId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            ))
            .flatMap(function((serviceInstanceId, newSpaceId) -> requestShareServiceInstance(this.cloudFoundryClient, newSpaceId, serviceInstanceId)
                .then(Mono.just(serviceInstanceId))))
            .flatMapMany(serviceInstanceId -> this.cloudFoundryClient.serviceInstancesV3()
                .listSharedSpacesRelationship(ListSharedSpacesRelationshipRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .build())
                .flatMapIterable(ListSharedSpacesRelationshipResponse::getData))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void share() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono.zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                createManagedServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName + "-shareable", spaceId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            ))
            .flatMapMany(function((serviceInstanceId, newSpaceId) -> Mono.zip(
                Mono.just(newSpaceId),
                this.cloudFoundryClient.serviceInstancesV3()
                    .share(ShareServiceInstanceRequest.builder()
                        .data(Relationship.builder()
                            .id(newSpaceId)
                            .build())
                        .serviceInstanceId(serviceInstanceId)
                        .build())
                    .flatMapIterable(ShareServiceInstanceResponse::getData)
                    .map(Relationship::getId)
                    .next())))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unshare() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono.zip(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                createManagedServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName + "-shareable", spaceId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            ))
            .delayUntil(function((serviceInstanceId, newSpaceId) -> requestShareServiceInstance(this.cloudFoundryClient, newSpaceId, serviceInstanceId)))
            .delayUntil(function((serviceInstanceId, newSpaceId) -> this.cloudFoundryClient.serviceInstancesV3()
                .unshare(UnshareServiceInstanceRequest.builder()
                    .serviceInstanceId(serviceInstanceId)
                    .spaceId(newSpaceId)
                    .build())))
            .flatMapMany(function((serviceInstanceId, newSpaceId) -> requestListSharedSpacesRelationship(this.cloudFoundryClient, serviceInstanceId)
                .flatMapIterable(ListSharedSpacesRelationshipResponse::getData)
                .filter(data -> newSpaceId.equals(data.getId()))))
            .as(StepVerifier::create)
            .expectNextCount(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> deleteServiceInstanceByName(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .list(ListServiceInstancesRequest.builder()
                .spaceId(spaceId)
                .serviceInstanceName(serviceInstanceName)
                .build())
            .map(serviceInstances -> serviceInstances.getResources().get(0))
            .flatMap(serviceInstance -> deleteServiceInstanceById(cloudFoundryClient, serviceInstance.getId()));
    }

    private static Mono<String> deleteServiceInstanceById(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .delete(DeleteServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build())
            .map(Optional::get);
    }

    private static Mono<String> updateServiceInstanceByName(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .list(ListServiceInstancesRequest.builder()
                .spaceId(spaceId)
                .serviceInstanceName(serviceInstanceName)
                .build())
            .map(serviceInstances -> serviceInstances.getResources().get(0))
            .flatMap(serviceInstance -> updateServiceInstanceById(cloudFoundryClient, serviceInstance.getId()))
            .flatMap(serviceInstance -> waitForCompletionOnUpdate(cloudFoundryClient, serviceInstance, serviceInstanceName));
    }

    private static Mono<UpdateServiceInstanceResponse> updateServiceInstanceById(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .update(UpdateServiceInstanceRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .parameter("foo", "bar")
                .tag("baz")
                .build());
    }


    private static Mono<String> waitForCompletionOnUpdate(CloudFoundryClient cloudFoundryClient, UpdateServiceInstanceResponse updateServiceInstanceResponse, String serviceInstanceName) {
        if (updateServiceInstanceResponse.getJobId().isPresent()) {
            JobUtils.waitForCompletion(cloudFoundryClient, Duration.ofMinutes(1), updateServiceInstanceResponse.getJobId().get());
        }
        return getServiceInstanceIdByName(cloudFoundryClient, serviceInstanceName);
    }

    private static Mono<String> createManagedServiceInstanceId(CloudFoundryClient cloudFoundryClient, Mono<String> serviceBrokerId, String serviceInstanceName, String serviceName, String spaceId) {
        return serviceBrokerId
            .flatMap(brokerId -> getPlanId(cloudFoundryClient, brokerId, serviceName))
            .flatMap(planId -> requestCreateServiceInstance(cloudFoundryClient, planId, serviceInstanceName, spaceId))
            .flatMap(serviceInstance -> waitForCompletionOnCreate(cloudFoundryClient, serviceInstance, serviceInstanceName));
    }

    private static Mono<String> waitForCompletionOnCreate(CloudFoundryClient cloudFoundryClient, CreateServiceInstanceResponse createServiceInstanceResponse, String serviceInstanceName) {
        if (createServiceInstanceResponse.getJobId().isPresent()) {
            JobUtils.waitForCompletion(cloudFoundryClient, Duration.ofMinutes(1), createServiceInstanceResponse.getJobId().get());
        }
        return getServiceInstanceIdByName(cloudFoundryClient, serviceInstanceName);
    }

    private static Mono<String> getServiceInstanceIdByName(CloudFoundryClient cloudFoundryClient, String serviceInstanceName) {
        return cloudFoundryClient.serviceInstancesV3()
            .list(ListServiceInstancesRequest.builder()
                .serviceInstanceName(serviceInstanceName)
                .build())
            .map(serviceInstances -> serviceInstances.getResources()
                .get(0)
                .getId());
    }

    private static Mono<CreateServiceInstanceResponse> createUserProvidedServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, Map<String, Object> credentials,
                                                                                         String spaceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .create(CreateServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .type(ServiceInstanceType.USER_PROVIDED)
                .relationships(ServiceInstanceRelationships.builder()
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(spaceId)
                            .build())
                        .build())
                    .build())
                .credentials(credentials)
                .build());
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
    }

    private static Mono<String> getPlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return requestListServiceOfferings(cloudFoundryClient, serviceBrokerId)
            .filter(serviceOfferingResource -> serviceName.equals(serviceOfferingResource.getName()))
            .single()
            .map(ServiceOfferingResource::getId)
            .flatMapMany(serviceId -> requestListServicePlans(cloudFoundryClient, serviceId))
            .single()
            .map(ServicePlanResource::getId);
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String planId, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .create(CreateServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .type(ServiceInstanceType.MANAGED)
                .relationships(ServiceInstanceRelationships.builder()
                    .servicePlan(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(planId).build())
                        .build())
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(spaceId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spacesV3()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .relationships(SpaceRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(organizationId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.servicePlansV3()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceOfferingId(serviceId)
                    .build()));
    }

    private static Flux<ServiceOfferingResource> requestListServiceOfferings(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.serviceOfferingsV3()
                .list(ListServiceOfferingsRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

    private static Mono<ListSharedSpacesRelationshipResponse> requestListSharedSpacesRelationship(CloudFoundryClient cloudFoundryClient, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .listSharedSpacesRelationship(ListSharedSpacesRelationshipRequest.builder()
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<ShareServiceInstanceResponse> requestShareServiceInstance(CloudFoundryClient cloudFoundryClient, String newSpaceId, String serviceInstanceId) {
        return cloudFoundryClient.serviceInstancesV3()
            .share(ShareServiceInstanceRequest.builder()
                .data(Relationship.builder()
                    .id(newSpaceId)
                    .build())
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

}
