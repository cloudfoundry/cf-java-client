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
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.GetServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
import org.cloudfoundry.client.v2.serviceplanvisibilities.UpdateServicePlanVisibilityRequest;
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
import reactor.util.function.Tuples;

import java.time.Duration;

import static org.cloudfoundry.ServiceBrokerUtils.createServiceBroker;
import static org.cloudfoundry.ServiceBrokerUtils.deleteServiceBroker;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServicePlanVisibilitiesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Test
    public void create() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .delayUntil(function((organizationId, servicePlanId) -> this.cloudFoundryClient.servicePlanVisibilities()
                .create(CreateServicePlanVisibilityRequest.builder()
                    .organizationId(organizationId)
                    .servicePlanId(servicePlanId)
                    .build())))
            .flatMapMany(function((organizationId, servicePlanId) -> requestListServicePlanVisibilities(this.cloudFoundryClient, servicePlanId)
                .single()
                .flatMap(response -> Mono.just(Tuples.of(ResourceUtils.getEntity(response).getOrganizationId(), organizationId)))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .flatMap(function((organizationId, servicePlanId) -> Mono.zip(
                Mono.just(servicePlanId),
                createServicePlanVisibilityId(this.cloudFoundryClient, organizationId, servicePlanId)
            )))
            .flatMap(function((servicePlanId, servicePlanIdVisibilityId) -> this.cloudFoundryClient.servicePlanVisibilities()
                .delete(DeleteServicePlanVisibilityRequest.builder()
                    .async(true)
                    .servicePlanVisibilityId(servicePlanIdVisibilityId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
                .thenReturn(servicePlanId)))
            .flatMapMany(servicePlanId -> requestListServicePlanVisibilities(this.cloudFoundryClient, servicePlanId))
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
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .flatMap(function((organizationId, servicePlanId) -> Mono.zip(
                Mono.just(servicePlanId),
                createServicePlanVisibilityId(this.cloudFoundryClient, organizationId, servicePlanId)
            )))
            .flatMap(function((servicePlanId, servicePlanIdVisibilityId) -> this.cloudFoundryClient.servicePlanVisibilities()
                .delete(DeleteServicePlanVisibilityRequest.builder()
                    .async(false)
                    .servicePlanVisibilityId(servicePlanIdVisibilityId)
                    .build())
                .thenReturn(servicePlanId)))
            .flatMapMany(servicePlanId -> requestListServicePlanVisibilities(this.cloudFoundryClient, servicePlanId))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .flatMap(function((organizationId, servicePlanId) -> Mono.zip(
                Mono.just(organizationId),
                createServicePlanVisibilityId(this.cloudFoundryClient, organizationId, servicePlanId)
            )))
            .flatMap(function((organizationId, servicePlanVisibilityId) -> Mono.zip(
                Mono.just(organizationId),
                this.cloudFoundryClient.servicePlanVisibilities()
                    .get(GetServicePlanVisibilityRequest.builder()
                        .servicePlanVisibilityId(servicePlanVisibilityId)
                        .build())
                    .map(response -> ResourceUtils.getEntity(response).getOrganizationId())
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .flatMap(function((organizationId, servicePlanId) -> Mono.zip(
                Mono.just(organizationId),
                createServicePlanVisibilityId(this.cloudFoundryClient, organizationId, servicePlanId)
            )))
            .flatMapMany(function((organizationId, servicePlanVisibilityId) -> Mono.zip(
                Mono.just(organizationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlanVisibilities()
                        .list(ListServicePlanVisibilitiesRequest.builder()
                            .page(page)
                            .build()))
                    .filter(response -> servicePlanVisibilityId.equals(ResourceUtils.getId(response)))
                    .single()
                    .map(response -> ResourceUtils.getEntity(response).getOrganizationId())
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .flatMap(function((organizationId, servicePlanId) -> Mono.zip(
                Mono.just(organizationId),
                createServicePlanVisibilityId(this.cloudFoundryClient, organizationId, servicePlanId)
            )))
            .flatMapMany(function((organizationId, servicePlanVisibilityId) -> Mono.zip(
                Mono.just(organizationId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlanVisibilities()
                        .list(ListServicePlanVisibilitiesRequest.builder()
                            .organizationId(organizationId)
                            .page(page)
                            .build()))
                    .filter(response -> servicePlanVisibilityId.equals(ResourceUtils.getId(response)))
                    .single()
                    .map(response -> ResourceUtils.getEntity(response).getOrganizationId())
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByServicePlanId() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .flatMap(function((organizationId, servicePlanId) -> Mono.zip(
                Mono.just(servicePlanId),
                createServicePlanVisibilityId(this.cloudFoundryClient, organizationId, servicePlanId)
            )))
            .flatMapMany(function((servicePlanId, servicePlanVisibilityId) -> Mono.zip(
                Mono.just(servicePlanId),
                PaginationUtils
                    .requestClientV2Resources(page -> this.cloudFoundryClient.servicePlanVisibilities()
                        .list(ListServicePlanVisibilitiesRequest.builder()
                            .servicePlanId(servicePlanId)
                            .page(page)
                            .build()))
                    .single()
                    .map(response -> ResourceUtils.getEntity(response).getServicePlanId())
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String organizationName = this.nameFactory.getOrganizationName();
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        Mono
            .zip(
                this.organizationId,
                getServicePlanId(this.cloudFoundryClient, serviceBrokerMetadata.serviceBrokerId)
            )
            .flatMap(function((organizationId, servicePlanId) -> Mono.zip(
                createOrganizationId(this.cloudFoundryClient, organizationName),
                Mono.just(servicePlanId),
                createServicePlanVisibilityId(this.cloudFoundryClient, organizationId, servicePlanId)
            )))
            .flatMap(function((newOrganizationId, servicePlanId, servicePlanIdVisibilityId) -> this.cloudFoundryClient.servicePlanVisibilities()
                .update(UpdateServicePlanVisibilityRequest.builder()
                    .organizationId(newOrganizationId)
                    .servicePlanId(servicePlanId)
                    .servicePlanVisibilityId(servicePlanIdVisibilityId)
                    .build())
                .thenReturn(Tuples.of(newOrganizationId, servicePlanId))))
            .flatMapMany(function((newOrganizationId, servicePlanId) -> Mono.zip(
                Mono.just(newOrganizationId),
                requestListServicePlanVisibilities(this.cloudFoundryClient, servicePlanId)
                    .single()
                    .map(response -> ResourceUtils.getEntity(response).getOrganizationId())
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServicePlanVisibilityId(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        return requestCreateServicePlanVisibility(cloudFoundryClient, organizationId, servicePlanId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getServicePlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return requestListServicePlans(cloudFoundryClient, serviceBrokerId)
            .filter(resource -> "test-plan-description".equals(ResourceUtils.getEntity(resource).getDescription()))
            .map(ResourceUtils::getId)
            .single();
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build());
    }

    private static Mono<CreateServicePlanVisibilityResponse> requestCreateServicePlanVisibility(CloudFoundryClient cloudFoundryClient, String organizationId, String servicePlanId) {
        return cloudFoundryClient.servicePlanVisibilities()
            .create(CreateServicePlanVisibilityRequest.builder()
                .organizationId(organizationId)
                .servicePlanId(servicePlanId)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Flux<ServicePlanVisibilityResource> requestListServicePlanVisibilities(CloudFoundryClient cloudFoundryClient, String servicePlanId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlanVisibilities()
                .list(ListServicePlanVisibilitiesRequest.builder()
                    .servicePlanId(servicePlanId)
                    .build()));
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

}
