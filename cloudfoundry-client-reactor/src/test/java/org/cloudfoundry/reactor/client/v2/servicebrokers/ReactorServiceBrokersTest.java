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

package org.cloudfoundry.reactor.client.v2.servicebrokers;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.GetServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.GetServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServiceBrokersTest extends AbstractClientApiTest {

    private final ReactorServiceBrokers serviceBrokers = new ReactorServiceBrokers(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/service_brokers")
                .payload("fixtures/client/v2/service_brokers/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_brokers/POST_response.json")
                .build())
            .build());

        this.serviceBrokers
            .create(CreateServiceBrokerRequest.builder()
                .name("service-broker-name")
                .authenticationPassword("secretpassw0rd")
                .authenticationUsername("admin")
                .brokerUrl("https://broker.example.com")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateServiceBrokerResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:23Z")
                    .id("1e86a649-e4a2-4bed-830d-b12435ed4cd9")
                    .url("/v2/service_brokers/1e86a649-e4a2-4bed-830d-b12435ed4cd9")
                    .build())
                .entity(ServiceBrokerEntity.builder()
                    .name("service-broker-name")
                    .brokerUrl("https://broker.example.com")
                    .authenticationUsername("admin")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_brokers/test-service-broker-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceBrokers
            .delete(DeleteServiceBrokerRequest.builder()
                .serviceBrokerId("test-service-broker-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_brokers/test-service-broker-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_brokers/GET_{id}_response.json")
                .build())
            .build());

        this.serviceBrokers
            .get(GetServiceBrokerRequest.builder()
                .serviceBrokerId("test-service-broker-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceBrokerResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:23Z")
                    .id("1311f77f-cfb6-499e-bcba-82c7ef968ae6")
                    .updatedAt("2015-07-27T22:43:23Z")
                    .url("/v2/service_brokers/1311f77f-cfb6-499e-bcba-82c7ef968ae6")
                    .build())
                .entity(ServiceBrokerEntity.builder()
                    .name("name-974")
                    .brokerUrl("https://foo.com/url-36")
                    .authenticationUsername("auth_username-36")
                    .spaceId("7878cee1-a484-4148-92bf-84beae20842f")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_brokers?q=name%3Atest-name&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_brokers/GET_response.json")
                .build())
            .build());

        this.serviceBrokers
            .list(ListServiceBrokersRequest.builder()
                .name("test-name")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceBrokersResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(ServiceBrokerResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:23Z")
                        .id("b52de6f1-15dd-4069-8b42-f052cc9333fc")
                        .updatedAt("2015-07-27T22:43:23Z")
                        .url("/v2/service_brokers/b52de6f1-15dd-4069-8b42-f052cc9333fc")
                        .build())
                    .entity(ServiceBrokerEntity.builder()
                        .name("name-980")
                        .brokerUrl("https://foo.com/url-39")
                        .authenticationUsername("auth_username-39")
                        .spaceId("4f34c35e-be0d-409e-9279-1ccd7058c5d8")
                        .build())
                    .build())
                .resource(ServiceBrokerResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:23Z")
                        .id("812e9e7f-b5b0-4587-ba3c-b4a7c574fb88")
                        .url("/v2/service_brokers/812e9e7f-b5b0-4587-ba3c-b4a7c574fb88")
                        .build())
                    .entity(ServiceBrokerEntity.builder()
                        .name("name-981")
                        .brokerUrl("https://foo.com/url-40")
                        .authenticationUsername("auth_username-40")
                        .build())
                    .build())
                .resource(ServiceBrokerResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:23Z")
                        .id("93e760c4-ff3d-447a-9cff-a17f4454eaee")
                        .url("/v2/service_brokers/93e760c4-ff3d-447a-9cff-a17f4454eaee")
                        .build())
                    .entity(ServiceBrokerEntity.builder()
                        .name("name-982")
                        .brokerUrl("https://foo.com/url-41")
                        .authenticationUsername("auth_username-41")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/service_brokers/test-service-broker-id")
                .payload("fixtures/client/v2/service_brokers/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_brokers/PUT_{id}_response.json")
                .build())
            .build());

        this.serviceBrokers
            .update(UpdateServiceBrokerRequest.builder()
                .authenticationUsername("admin-user")
                .authenticationPassword("some-secret")
                .brokerUrl("https://mybroker.example.com")
                .serviceBrokerId("test-service-broker-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateServiceBrokerResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:23Z")
                    .id("92b935f5-20e2-4377-a7e2-f15faa110eab")
                    .updatedAt("2015-07-27T22:43:23Z")
                    .url("/v2/service_brokers/92b935f5-20e2-4377-a7e2-f15faa110eab")
                    .build())
                .entity(ServiceBrokerEntity.builder()
                    .name("name-998")
                    .brokerUrl("https://mybroker.example.com")
                    .authenticationUsername("admin-user")
                    .spaceId("85e59d96-b68b-4908-8ff5-8d54f4371f14")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
