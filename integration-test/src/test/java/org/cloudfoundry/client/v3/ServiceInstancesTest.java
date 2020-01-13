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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v3.serviceInstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v3.serviceInstances.ListSharedSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.serviceInstances.ListSharedSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.serviceInstances.ShareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceInstances.ShareServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceInstances.UnshareServiceInstanceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;

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
    public void list() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        this.spaceId
            .flatMap(spaceId -> createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName, spaceId))
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
                createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName + "-shareable", spaceId),
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
                createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName + "-shareable", spaceId),
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
                createServiceInstanceId(this.cloudFoundryClient, this.serviceBrokerId, serviceInstanceName, this.serviceName + "-shareable", spaceId),
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

    private static Mono<String> createServiceInstanceId(CloudFoundryClient cloudFoundryClient, Mono<String> serviceBrokerId, String serviceInstanceName, String serviceName, String spaceId) {
        return serviceBrokerId
            .flatMap(s -> getPlanId(cloudFoundryClient, s, serviceName))
            .flatMap(planId -> requestCreateServiceInstance(cloudFoundryClient, planId, serviceInstanceName, spaceId))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
    }

    private static Mono<String> getPlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceBrokerId)
            .filter(resource -> serviceName.equals(resource.getEntity().getLabel()))
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
