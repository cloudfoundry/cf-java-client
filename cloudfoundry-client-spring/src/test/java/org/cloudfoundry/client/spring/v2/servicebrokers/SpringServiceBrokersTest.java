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
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import reactor.Mono;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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

}
