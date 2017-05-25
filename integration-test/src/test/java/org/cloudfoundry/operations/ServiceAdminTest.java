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
import org.cloudfoundry.operations.serviceadmin.Access;
import org.cloudfoundry.operations.serviceadmin.DisableServiceAccessRequest;
import org.cloudfoundry.operations.serviceadmin.EnableServiceAccessRequest;
import org.cloudfoundry.operations.serviceadmin.ListServiceAccessSettingsRequest;
import org.cloudfoundry.operations.serviceadmin.ServiceAccess;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public final class ServiceAdminTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

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
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
            .filter(response -> this.serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.NONE)
                .brokerName(this.serviceBrokerName)
                .organizationName()
                .planName(this.planName)
                .serviceName(this.serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void disableServiceAccessSpecifyAll() {
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .servicePlanName(this.planName)
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
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
    public void disableServiceAccessSpecifyOrganization() {
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
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
    public void disableServiceAccessSpecifyServicePlan() {
        resetToEnabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .disableServiceAccess(DisableServiceAccessRequest.builder()
                    .servicePlanName(this.planName)
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
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
    public void enableServiceAccess() {
        resetToDisabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
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
    public void enableServiceAccessSpecifyAll() {
        resetToDisabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .servicePlanName(this.planName)
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
            .filter(response -> this.serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.LIMITED)
                .brokerName(this.serviceBrokerName)
                .organizationName(this.organizationName)
                .planName(this.planName)
                .serviceName(this.serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void enableServiceAccessSpecifyOrganization() {
        resetToDisabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .organizationName(this.organizationName)
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
            .filter(response -> this.serviceName.equals(response.getServiceName()))
            .as(StepVerifier::create)
            .expectNext(ServiceAccess.builder()
                .access(Access.LIMITED)
                .brokerName(this.serviceBrokerName)
                .organizationName(this.organizationName)
                .planName(this.planName)
                .serviceName(this.serviceName)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void enableServiceAccessSpecifyServicePlan() {
        resetToDisabled(this.cloudFoundryOperations, this.serviceName)
            .then(this.cloudFoundryOperations.serviceAdmin()
                .enableServiceAccess(EnableServiceAccessRequest.builder()
                    .servicePlanName(this.planName)
                    .serviceName(this.serviceName)
                    .build()))
            .thenMany(requestListServiceAccessSettings(this.cloudFoundryOperations, this.serviceName))
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
