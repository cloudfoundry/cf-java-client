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

package org.cloudfoundry.client.spring.v2.servicebrokers;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.GetServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.GetServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringServiceBrokersTest {

    public static final class Create extends AbstractApiTest<CreateServiceBrokerRequest, CreateServiceBrokerResponse> {

        private final SpringServiceBrokers serviceBrokers = new SpringServiceBrokers(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateServiceBrokerRequest getInvalidRequest() {
            return CreateServiceBrokerRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("v2/service_brokers")
                    .requestPayload("v2/service_brokers/POST_request.json")
                    .status(CREATED)
                    .responsePayload("v2/service_brokers/POST_response.json");
        }

        @Override
        protected CreateServiceBrokerResponse getResponse() {
            return CreateServiceBrokerResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:23Z")
                            .id("1e86a649-e4a2-4bed-830d-b12435ed4cd9")
                            .url("/v2/service_brokers/1e86a649-e4a2-4bed-830d-b12435ed4cd9")
                            .build())
                    .entity(ServiceBrokerEntity.builder()
                            .name("service-broker-name")
                            .brokerUrl("https://broker.example.com")
                            .authenticationUsername("admin")
                            .build())
                    .build();
        }

        @Override
        protected CreateServiceBrokerRequest getValidRequest() throws Exception {
            return CreateServiceBrokerRequest.builder()
                    .name("service-broker-name")
                    .authenticationPassword("secretpassw0rd")
                    .authenticationUsername("admin")
                    .brokerUrl("https://broker.example.com")
                    .build();
        }

        @Override
        protected Mono<CreateServiceBrokerResponse> invoke(CreateServiceBrokerRequest request) {
            return this.serviceBrokers.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteServiceBrokerRequest, Void> {

        private final SpringServiceBrokers serviceBrokers = new SpringServiceBrokers(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteServiceBrokerRequest getInvalidRequest() {
            return DeleteServiceBrokerRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v2/service_brokers/test-id")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteServiceBrokerRequest getValidRequest() throws Exception {
            return DeleteServiceBrokerRequest.builder().id("test-id")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteServiceBrokerRequest request) {
            return this.serviceBrokers.delete(request);
        }
    }

    public static final class Get extends AbstractApiTest<GetServiceBrokerRequest, GetServiceBrokerResponse> {

        private final SpringServiceBrokers serviceBrokers = new SpringServiceBrokers(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetServiceBrokerRequest getInvalidRequest() {
            return GetServiceBrokerRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/service_brokers/test-id")
                    .status(OK)
                    .responsePayload("v2/service_brokers/GET_{id}_response.json");
        }

        @Override
        protected GetServiceBrokerResponse getResponse() {
            return GetServiceBrokerResponse.builder()
                    .metadata(Resource.Metadata.builder()
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
                    .build();
        }

        @Override
        protected GetServiceBrokerRequest getValidRequest() throws Exception {
            return GetServiceBrokerRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Publisher<GetServiceBrokerResponse> invoke(GetServiceBrokerRequest request) {
            return this.serviceBrokers.get(request);
        }
    }

    public static final class List extends AbstractApiTest<ListServiceBrokersRequest, ListServiceBrokersResponse> {

        private final SpringServiceBrokers serviceBrokers = new SpringServiceBrokers(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListServiceBrokersRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/service_brokers?q=name%20IN%20test-name&page=-1")
                    .status(OK)
                    .responsePayload("v2/service_brokers/GET_response.json");
        }

        @Override
        protected ListServiceBrokersResponse getResponse() {
            return ListServiceBrokersResponse.builder()
                    .totalResults(3)
                    .totalPages(1)
                    .resource(ServiceBrokerResource.builder()
                            .metadata(Resource.Metadata.builder()
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
                            .metadata(Resource.Metadata.builder()
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
                            .metadata(Resource.Metadata.builder()
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
                    .build();
        }

        @Override
        protected ListServiceBrokersRequest getValidRequest() throws Exception {
            return ListServiceBrokersRequest.builder()
                    .name("test-name")
                    .page(-1)
                    .build();
        }

        @Override
        protected Publisher<ListServiceBrokersResponse> invoke(ListServiceBrokersRequest request) {
            return this.serviceBrokers.list(request);
        }
    }

}
