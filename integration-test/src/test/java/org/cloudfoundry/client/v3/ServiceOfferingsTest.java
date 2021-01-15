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
import org.cloudfoundry.ServiceBrokerUtils;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.serviceofferings.DeleteServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.GetServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.GetServiceOfferingResponse;
import org.cloudfoundry.client.v3.serviceofferings.ListServiceOfferingsRequest;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingResource;
import org.cloudfoundry.client.v3.serviceofferings.UpdateServiceOfferingRequest;
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
public final class ServiceOfferingsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

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

        getServiceOfferingId(this.cloudFoundryClient, serviceName, serviceBrokerMetadata.serviceBrokerId)
            .flatMap(serviceOfferingId -> this.cloudFoundryClient.serviceOfferingsV3()
                .delete(DeleteServiceOfferingRequest.builder()
                    .purge(true)
                    .serviceOfferingId(serviceOfferingId)
                    .build()))
            .thenMany(requestListServiceOfferings(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(resource -> serviceName.equals(resource.getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> getServiceOfferingId(this.cloudFoundryClient, this.serviceName, serviceBrokerId))
            .flatMap(serviceOfferingId -> this.cloudFoundryClient.serviceOfferingsV3()
                .get(GetServiceOfferingRequest.builder()
                    .serviceOfferingId(serviceOfferingId)
                    .build()))
            .map(GetServiceOfferingResponse::getName)
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.serviceOfferingsV3()
            .list(ListServiceOfferingsRequest.builder()
                .page(page)
                .build()))
            .filter(resource -> this.serviceName.equals(resource.getName()))
            .map(ServiceOfferingResource::getDescription)
            .as(StepVerifier::create)
            .expectNext("test-service-description")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBy() {
        this.serviceBrokerId
            .flatMapMany(serviceBrokerId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.serviceOfferingsV3()
                    .list(ListServiceOfferingsRequest.builder()
                        .page(page)
                        .serviceBrokerId(serviceBrokerId)
                        .build())))
            .filter(resource -> this.serviceName.equals(resource.getName()))
            .map(ServiceOfferingResource::getDescription)
            .as(StepVerifier::create)
            .expectNext("test-service-description")
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

        getServiceOfferingId(this.cloudFoundryClient, serviceName, serviceBrokerMetadata.serviceBrokerId)
            .flatMap(serviceOfferingId -> this.cloudFoundryClient.serviceOfferingsV3()
                .update(UpdateServiceOfferingRequest.builder()
                    .metadata(Metadata.builder()
                        .label("test-service-offering-update-key", "test-service-offering-update-value")
                        .build())
                    .serviceOfferingId(serviceOfferingId)
                    .build()))
            .thenMany(requestListServiceOfferings(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId))
            .filter(response -> serviceName.equals(response.getName()))
            .map(ServiceOfferingResource::getMetadata)
            .map(Metadata::getLabels)
            .as(StepVerifier::create)
            .expectNext(Collections.singletonMap("test-service-offering-update-key", "test-service-offering-update-value"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
    }

    private static Mono<String> getServiceOfferingId(CloudFoundryClient cloudFoundryClient, String serviceName, String serviceBrokerId) {
        return requestListServiceOfferings(cloudFoundryClient, serviceBrokerId)
            .filter(resource -> serviceName.equals(resource.getName()))
            .map(ServiceOfferingResource::getId)
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

    private static Flux<ServiceOfferingResource> requestListServiceOfferings(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils.requestClientV3Resources(page ->
            cloudFoundryClient.serviceOfferingsV3()
                .list(ListServiceOfferingsRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

}
