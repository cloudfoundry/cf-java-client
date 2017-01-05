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

package org.cloudfoundry.operations.serviceadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultServiceAdminTest extends AbstractOperationsTest {

    private final DefaultServiceAdmin serviceAdmin = new DefaultServiceAdmin(Mono.just(this.cloudFoundryClient), Mono.just(TEST_SPACE_ID));

    @Test
    public void createServiceBroker() {
        requestCreateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username", "test-service-broker-password", null);

        this.serviceAdmin
            .create(CreateServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .url("test-service-broker-url")
                .username("test-service-broker-username")
                .password("test-service-broker-password")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createServiceBrokerWithSpaceScope() {
        requestCreateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username", "test-service-broker-password", TEST_SPACE_ID);

        this.serviceAdmin
            .create(CreateServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .url("test-service-broker-url")
                .username("test-service-broker-username")
                .password("test-service-broker-password")
                .spaceScoped(true)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceBroker() {
        requestListServiceBrokers(this.cloudFoundryClient, "test-service-broker-name");
        requestDeleteServiceBroker(this.cloudFoundryClient, "test-service-broker-id");

        this.serviceAdmin
            .delete(DeleteServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteServiceBrokerNoServiceBroker() {
        requestListServiceBrokersEmpty(this.cloudFoundryClient, "test-service-broker-name");
        requestDeleteServiceBroker(this.cloudFoundryClient, "test-service-broker-id");

        this.serviceAdmin
            .delete(DeleteServiceBrokerRequest.builder()
                .name("test-service-broker-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Service Broker test-service-broker-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceBrokers() {
        requestListServiceBrokers(this.cloudFoundryClient);

        this.serviceAdmin
            .list()
            .as(StepVerifier::create)
            .expectNext(ServiceBroker.builder()
                .id("test-service-broker-id")
                .name("test-service-broker-resource-name")
                .url("test-service-broker-resource-brokerUrl")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceBrokersNoBrokers() {
        requestListServiceBrokersEmpty(this.cloudFoundryClient);

        this.serviceAdmin
            .list()
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static void requestCreateServiceBroker(CloudFoundryClient cloudFoundryClient, String name, String url, String username, String password, String spaceId) {
        when(cloudFoundryClient.serviceBrokers()
            .create(org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest.builder()
                .name(name)
                .brokerUrl(url)
                .authenticationUsername(username)
                .authenticationPassword(password)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateServiceBrokerResponse.builder())
                    .build()));
    }

    private static void requestDeleteServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerId) {
        when(cloudFoundryClient.serviceBrokers()
            .delete(org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestListServiceBrokers(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .resource(fill(ServiceBrokerResource.builder(), "service-broker-")
                        .entity(fill(ServiceBrokerEntity.builder(), "service-broker-resource-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceBrokers(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .name(serviceBrokerName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .resource(fill(ServiceBrokerResource.builder(), "service-broker-")
                        .entity(fill(ServiceBrokerEntity.builder(), "service-broker-resource-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestListServiceBrokersEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .build()));
    }

    private static void requestListServiceBrokersEmpty(CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .name(serviceBrokerName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .build()));
    }

}
