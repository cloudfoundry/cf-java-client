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

package org.cloudfoundry.spring.client.v2.userprovidedserviceinstances;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceEntity;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceResource;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public final class SpringUserProvidedServiceInstancesTest {

    public static final class Create extends AbstractApiTest<CreateUserProvidedServiceInstanceRequest, CreateUserProvidedServiceInstanceResponse> {

        private final SpringUserProvidedServiceInstances userProvidedServiceInstances = new SpringUserProvidedServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateUserProvidedServiceInstanceRequest getInvalidRequest() {
            return CreateUserProvidedServiceInstanceRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/user_provided_service_instances")
                .requestPayload("client/v2/user_provided_service_instances/POST_request.json")
                .status(CREATED)
                .responsePayload("client/v2/user_provided_service_instances/POST_response.json");
        }

        @Override
        protected CreateUserProvidedServiceInstanceResponse getResponse() {
            return CreateUserProvidedServiceInstanceResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .createdAt("2015-07-27T22:43:35Z")
                    .id("34d5500e-712d-49ef-8bbe-c9ac349532da")
                    .url("/v2/user_provided_service_instances/34d5500e-712d-49ef-8bbe-c9ac349532da")
                    .build())
                .entity(UserProvidedServiceInstanceEntity.builder()
                    .name("my-user-provided-instance")
                    .credential("somekey", "somevalue")
                    .spaceId("0d45d43f-7d50-43c6-9981-b32ce8d5a373")
                    .type("user_provided_service_instance")
                    .syslogDrainUrl("syslog://example.com")
                    .spaceUrl("/v2/spaces/0d45d43f-7d50-43c6-9981-b32ce8d5a373")
                    .serviceBindingsUrl("/v2/user_provided_service_instances/34d5500e-712d-49ef-8bbe-c9ac349532da/service_bindings")
                    .build())
                .build();
        }

        @Override
        protected CreateUserProvidedServiceInstanceRequest getValidRequest() throws Exception {
            return CreateUserProvidedServiceInstanceRequest.builder()
                .spaceId("0d45d43f-7d50-43c6-9981-b32ce8d5a373")
                .name("my-user-provided-instance")
                .credential("somekey", "somevalue")
                .syslogDrainUrl("syslog://example.com")
                .build();
        }

        @Override
        protected Mono<CreateUserProvidedServiceInstanceResponse> invoke(CreateUserProvidedServiceInstanceRequest request) {
            return this.userProvidedServiceInstances.create(request);
        }
    }

    public static final class List extends AbstractApiTest<ListUserProvidedServiceInstancesRequest, ListUserProvidedServiceInstancesResponse> {

        private final SpringUserProvidedServiceInstances userProvidedServiceInstances = new SpringUserProvidedServiceInstances(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListUserProvidedServiceInstancesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/user_provided_service_instances?page=-1")
                .status(OK)
                .responsePayload("client/v2/user_provided_service_instances/GET_response.json");
        }

        @Override
        protected ListUserProvidedServiceInstancesResponse getResponse() {
            return ListUserProvidedServiceInstancesResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(UserProvidedServiceInstanceResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2015-07-27T22:43:34Z")
                        .id("8db6d37b-1ca8-4d0a-b1d3-2a6aaceae866")
                        .url("/v2/user_provided_service_instances/8db6d37b-1ca8-4d0a-b1d3-2a6aaceae866")
                        .build())
                    .entity(UserProvidedServiceInstanceEntity.builder()
                        .name("name-2365")
                        .credential("creds-key-665", "creds-val-665")
                        .spaceId("2fff6e71-d329-4991-9c89-7fa8abca70df")
                        .type("user_provided_service_instance")
                        .syslogDrainUrl("https://foo.com/url-90")
                        .spaceUrl("/v2/spaces/2fff6e71-d329-4991-9c89-7fa8abca70df")
                        .serviceBindingsUrl("/v2/user_provided_service_instances/8db6d37b-1ca8-4d0a-b1d3-2a6aaceae866/service_bindings")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListUserProvidedServiceInstancesRequest getValidRequest() throws Exception {
            return ListUserProvidedServiceInstancesRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListUserProvidedServiceInstancesResponse> invoke(ListUserProvidedServiceInstancesRequest request) {
            return this.userProvidedServiceInstances.list(request);
        }
    }

}