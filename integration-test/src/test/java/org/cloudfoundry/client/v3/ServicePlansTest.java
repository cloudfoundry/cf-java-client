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
import org.cloudfoundry.ServiceBrokerUtils;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v3.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static org.cloudfoundry.ServiceBrokerUtils.createServiceBroker;
import static org.cloudfoundry.ServiceBrokerUtils.deleteServiceBroker;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_10)
public final class ServicePlansTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private String planName;

    @Autowired
    private Mono<String> serviceBrokerId;

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
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlansV3()
                .delete(DeleteServicePlanRequest.builder()
                    .servicePlanId(servicePlanId)
                    .build()))
            .thenMany(requestListServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(response -> serviceName.equals(response.getName()))
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
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlansV3()
                .get(GetServicePlanRequest.builder()
                    .servicePlanId(servicePlanId)
                    .build()))
            .map(GetServicePlanResponse::getName)
            .as(StepVerifier::create)
            .expectNext(this.planName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.servicePlansV3()
            .list(ListServicePlansRequest.builder()
                .page(page)
                .build()))
            .filter(response -> this.planName.equals(response.getName()))
            .map(ServicePlanResource::getDescription)
            .as(StepVerifier::create)
            .expectNext("test-plan-description")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServiceBrokerId() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.servicePlansV3()
                    .list(ListServicePlansRequest.builder()
                        .page(page)
                        .serviceBrokerId(serviceBrokerId)
                        .build())))
            .map(ServicePlanResource::getName)
            .filter(planName -> this.planName.equals(planName))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUsingLabelSelector() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlansV3()
                .update(UpdateServicePlanRequest.builder()
                    .metadata(Metadata.builder()
                        .label("test-label-selector-key1", "test-label-selector-value1")
                        .label("test-label-selector-key2", "test-label-selector-value2")
                        .build())
                    .servicePlanId(servicePlanId)
                    .build()))
            .thenMany(Flux.merge(
                PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.servicePlansV3()
                    .list(ListServicePlansRequest.builder()
                        .labelSelector("test-label-selector-key1,test-label-selector-key1")
                        .page(page)
                        .serviceBrokerId(serviceBrokerMetadata.serviceBrokerId)
                        .build())),
                PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.servicePlansV3()
                    .list(ListServicePlansRequest.builder()
                        .labelSelector("test-label-selector-key1,key-that-causes-no-results-because-this-is-an-AND-query")
                        .page(page)
                        .serviceBrokerId(serviceBrokerMetadata.serviceBrokerId)
                        .build())
                )))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
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
            .flatMap(servicePlanId -> this.cloudFoundryClient.servicePlansV3()
                .update(UpdateServicePlanRequest.builder()
                    .metadata(Metadata.builder()
                        .label("test-update-key", "test-update-value")
                        .build())
                    .servicePlanId(servicePlanId)
                    .build()))
            .thenMany(requestListServicePlans(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(response -> planName.equals(response.getName()))
            .map(ServicePlanResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-update-key", "test-update-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
    }

    private static Mono<String> getServicePlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return requestListServicePlans(cloudFoundryClient, serviceBrokerId)
            .filter(resource -> "test-plan-description".equals(resource.getDescription()))
            .map(ServicePlanResource::getId)
            .single();
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

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.servicePlansV3()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

}
