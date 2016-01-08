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

package org.cloudfoundry.client.spring.v2.servicebindings;


import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import reactor.Mono;

import java.util.Collections;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringServiceBindingsTest {

    public static final class Create extends AbstractApiTest<CreateServiceBindingRequest, CreateServiceBindingResponse> {

        private final SpringServiceBindings serviceBindings = new SpringServiceBindings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateServiceBindingRequest getInvalidRequest() {
            return CreateServiceBindingRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("v2/service_bindings")
                    .requestPayload("v2/service_bindings/POST_{id}_create_service_binding_request.json")
                    .status(CREATED)
                    .responsePayload("v2/service_bindings/POST_{id}_create_service_binding_response.json");
        }

        @Override
        protected CreateServiceBindingResponse getResponse() {
            return CreateServiceBindingResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-07-27T22:43:20Z")
                            .id("42eda707-fe4d-4eed-9b39-7cb5e665c226")
                            .url("/v2/service_bindings/42eda707-fe4d-4eed-9b39-7cb5e665c226")
                            .build())
                    .entity(ServiceBindingEntity.builder()
                            .applicationId("26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                            .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                            .credential("creds-key-356", "creds-val-356")
                            .gatewayName("")
                            .applicationUrl("/v2/apps/26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                            .serviceInstanceUrl("/v2/service_instances/650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                            .build())
                    .build();
        }

        @Override
        protected CreateServiceBindingRequest getValidRequest() throws Exception {
            return CreateServiceBindingRequest.builder()
                    .applicationId("26ddc1de-3eeb-424b-82f3-f7f30a38b610")
                    .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                    .parameters(Collections.singletonMap("the_service_broker", (Object) "wants this object"))
                    .build();
        }

        @Override
        protected Mono<CreateServiceBindingResponse> invoke(CreateServiceBindingRequest request) {
            return this.serviceBindings.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteServiceBindingRequest, Void> {

        private final SpringServiceBindings serviceBindings = new SpringServiceBindings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteServiceBindingRequest getInvalidRequest() {
            return DeleteServiceBindingRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v2/service_bindings/test-id")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteServiceBindingRequest getValidRequest() {
            return DeleteServiceBindingRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteServiceBindingRequest request) {
            return this.serviceBindings.delete(request);
        }

    }


    public static final class Get extends AbstractApiTest<GetServiceBindingRequest, GetServiceBindingResponse> {

        private final SpringServiceBindings serviceBindings = new SpringServiceBindings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetServiceBindingRequest getInvalidRequest() {
            return GetServiceBindingRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/service_bindings/test-id")
                    .status(OK)
                    .responsePayload("v2/service_bindings/GET_{id}_response.json");
        }

        @Override
        protected GetServiceBindingResponse getResponse() {
            return GetServiceBindingResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .createdAt("2015-11-03T00:53:50Z")
                            .id("925d8848-4808-47cf-a3e8-049aa0163328")
                            .updatedAt("2015-11-04T12:54:50Z")
                            .url("/v2/service_bindings/925d8848-4808-47cf-a3e8-049aa0163328")
                            .build())
                    .entity(ServiceBindingEntity.builder()
                            .applicationId("56ae4265-4c1c-43a9-9069-2c1fee7fd42f")
                            .serviceInstanceId("f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                            .credential("creds-key-108", "creds-val-108")
                            .gatewayName("")
                            .applicationUrl("/v2/apps/56ae4265-4c1c-43a9-9069-2c1fee7fd42f")
                            .serviceInstanceUrl("/v2/service_instances/f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                            .build())
                    .build();
        }

        @Override
        protected GetServiceBindingRequest getValidRequest() throws Exception {
            return GetServiceBindingRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<GetServiceBindingResponse> invoke(GetServiceBindingRequest request) {
            return this.serviceBindings.get(request);
        }

    }

}
