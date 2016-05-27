/*
 * Copyright 2013-2016 the original author or authors.
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
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultServiceAdminTest {

    private static void requestCreateServiceBroker(CloudFoundryClient cloudFoundryClient, String name, String url, String username, String password, String spaceId){
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

    private static void requestListServiceBrokersEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.serviceBrokers()
            .list(ListServiceBrokersRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListServiceBrokersResponse.builder())
                    .build()));
    }

    public static final class CreateServiceBroker extends AbstractOperationsApiTest<Void> {

        private final DefaultServiceAdmin serviceAdmin = new DefaultServiceAdmin(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestCreateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username",
                "test-service-broker-password", null);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.serviceAdmin
                .create(CreateServiceBrokerRequest.builder()
                    .name("test-service-broker-name")
                    .url("test-service-broker-url")
                    .username("test-service-broker-username")
                    .password("test-service-broker-password")
                    .build());
        }

    }

    public static final class CreateServiceBrokerWithSpaceScope extends AbstractOperationsApiTest<Void> {

        private final DefaultServiceAdmin serviceAdmin = new DefaultServiceAdmin(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestCreateServiceBroker(this.cloudFoundryClient, "test-service-broker-name", "test-service-broker-url", "test-service-broker-username",
                "test-service-broker-password", TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.serviceAdmin
                .create(CreateServiceBrokerRequest.builder()
                    .name("test-service-broker-name")
                    .url("test-service-broker-url")
                    .username("test-service-broker-username")
                    .password("test-service-broker-password")
                    .isSpaceScoped(true)
                    .build());
        }

    }

    public static final class ListServiceBrokers extends AbstractOperationsApiTest<ServiceBroker> {

        private final DefaultServiceAdmin serviceAdmin = new DefaultServiceAdmin(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceBrokers(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<ServiceBroker> testSubscriber) {
            testSubscriber
                .assertEquals(ServiceBroker.builder()
                    .id("test-service-broker-id")
                    .name("test-service-broker-resource-name")
                    .url("test-service-broker-resource-brokerUrl")
                    .build());
        }

        @Override
        protected Publisher<ServiceBroker> invoke() {
            return this.serviceAdmin
                .listServiceBrokers();
        }

    }

    public static final class ListServiceBrokersNoBrokers extends AbstractOperationsApiTest<ServiceBroker> {

        private final DefaultServiceAdmin serviceAdmin = new DefaultServiceAdmin(this.cloudFoundryClient, Mono.just(TEST_SPACE_ID));

        @Before
        public void setUp() throws Exception {
            requestListServiceBrokersEmpty(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<ServiceBroker> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<ServiceBroker> invoke() {
            return this.serviceAdmin
                .listServiceBrokers();
        }

    }

}
