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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.ServiceBrokerUtils;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.operations.serviceadmin.Access;
import org.cloudfoundry.operations.serviceadmin.DisableServiceAccessRequest;
import org.cloudfoundry.operations.serviceadmin.EnableServiceAccessRequest;
import org.cloudfoundry.operations.serviceadmin.ListServiceAccessSettingsRequest;
import org.cloudfoundry.operations.serviceadmin.ServiceAccess;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.ServiceBrokerUtils.createServiceBroker;
import static org.cloudfoundry.ServiceBrokerUtils.deleteServiceBroker;

public final class ServiceAdminTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private String organizationName;

    @Autowired
    private String planName;

    @Autowired
    private String serviceBrokerName;

    @Autowired
    private String serviceName;

    @Test
    public void disableServiceAccess() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToEnabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.NONE)
                .brokerName(serviceBrokerName)
                .organizationName()
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void disableServiceAccessSpecifyAll() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToEnabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .servicePlanName(planName)
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(serviceBrokerName)
                .organizationName()
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void disableServiceAccessSpecifyOrganization() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToEnabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(serviceBrokerName)
                .organizationName()
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void disableServiceAccessSpecifyServicePlan() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToEnabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .servicePlanName(planName)
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(serviceBrokerName)
                .organizationName()
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void enableServiceAccess() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToDisabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(serviceBrokerName)
                .organizationName()
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void enableServiceAccessSpecifyAll() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToDisabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .servicePlanName(planName)
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.LIMITED)
                .brokerName(serviceBrokerName)
                .organizationName(this.organizationName)
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void enableServiceAccessSpecifyOrganization() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToDisabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.LIMITED)
                .brokerName(serviceBrokerName)
                .organizationName(this.organizationName)
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void enableServiceAccessSpecifyServicePlan() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, false))
            .block(Duration.ofMinutes(5));

        resetToDisabled(this.cloudFoundryOperations, serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .servicePlanName(planName)
                    .serviceName(serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, serviceName))
            .filter(response -> serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(serviceBrokerName)
                .organizationName()
                .planName(planName)
                .serviceName(serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceAccessSettings() {
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .thenMany(this.cloudFoundryOperations.serviceAdmin()
                .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                    .build()))
            .filter(response -> this.serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(this.serviceBrokerName)
                .organizationName()
                .planName(this.planName)
                .serviceName(this.serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyBroker() {
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .thenMany(this.cloudFoundryOperations.serviceAdmin()
                .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                    .brokerName(this.serviceBrokerName)
                    .build()))
            .single()
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(this.serviceBrokerName)
                .organizationName()
                .planName(this.planName)
                .serviceName(this.serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyOrganization() {
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .thenMany(this.cloudFoundryOperations.serviceAdmin()
                .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                    .organizationName(this.organizationName)
                    .build()))
            .filter(response -> this.serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(this.serviceBrokerName)
                .organizationName()
                .planName(this.planName)
                .serviceName(this.serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceAccessSettingsSpecifyService() {
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .thenMany(this.cloudFoundryOperations.serviceAdmin()
                .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                    .serviceName(this.serviceName)
                    .build()))
            .single()
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.ALL)
                .brokerName(this.serviceBrokerName)
                .organizationName()
                .planName(this.planName)
                .serviceName(this.serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Flux<ServiceAccess> requestListServiceAccessSettings(CloudFoundryOperations cloudFoundryOperations, String serviceName) {
        return cloudFoundryOperations.serviceAdmin()
            .listServiceAccessSettings(ListServiceAccessSettingsRequest.builder()
                .serviceName(serviceName)
                .build());
    }

    private static Mono<Void> resetToDisabled(CloudFoundryOperations cloudFoundryOperations, String serviceName) {
        return cloudFoundryOperations.serviceAdmin()
            .disableServiceAccess(DisableServiceAccessRequest.builder()
                .serviceName(serviceName)
                .build());
    }

    private static Mono<Void> resetToEnabled(CloudFoundryOperations cloudFoundryOperations, String serviceName) {
        return cloudFoundryOperations.serviceAdmin()
            .enableServiceAccess(EnableServiceAccessRequest.builder()
                .serviceName(serviceName)
                .build());
    }

}
