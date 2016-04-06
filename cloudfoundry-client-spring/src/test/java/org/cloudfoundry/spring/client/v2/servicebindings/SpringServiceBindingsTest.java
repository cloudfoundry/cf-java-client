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

package org.cloudfoundry.spring.client.v2.servicebindings;


import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.ACCEPTED;
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
                .method(POST).path("/v2/service_bindings")
                .requestPayload("fixtures/client/v2/service_bindings/POST_request.json")
                .status(CREATED)
                .responsePayload("fixtures/client/v2/service_bindings/POST_response.json");
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

    public static final class Delete extends AbstractApiTest<DeleteServiceBindingRequest, DeleteServiceBindingResponse> {

        private final SpringServiceBindings serviceBindings = new SpringServiceBindings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteServiceBindingRequest getInvalidRequest() {
            return DeleteServiceBindingRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/service_bindings/test-service-binding-id")
                .status(NO_CONTENT);
        }

        @Override
        protected DeleteServiceBindingResponse getResponse() {
            return null;
        }

        @Override
        protected DeleteServiceBindingRequest getValidRequest() {
            return DeleteServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<DeleteServiceBindingResponse> invoke(DeleteServiceBindingRequest request) {
            return this.serviceBindings.delete(request);
        }

    }

    public static final class DeleteAsync extends AbstractApiTest<DeleteServiceBindingRequest, DeleteServiceBindingResponse> {

        private final SpringServiceBindings serviceBindings = new SpringServiceBindings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteServiceBindingRequest getInvalidRequest() {
            return DeleteServiceBindingRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/service_bindings/test-service-binding-id?async=true")
                .status(ACCEPTED)
                .responsePayload("fixtures/client/v2/service_bindings/DELETE_{id}_async_response.json");
        }

        @Override
        protected DeleteServiceBindingResponse getResponse() {
            return DeleteServiceBindingResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .createdAt("2016-03-14T22:30:51Z")
                    .url("/v2/jobs/c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .build())
                .entity(JobEntity.builder()
                    .id("c4faac01-5bbd-494f-8849-256a3bab06b8")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected DeleteServiceBindingRequest getValidRequest() {
            return DeleteServiceBindingRequest.builder()
                .async(true)
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<DeleteServiceBindingResponse> invoke(DeleteServiceBindingRequest request) {
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
                .method(GET).path("/v2/service_bindings/test-service-binding-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/service_bindings/GET_{id}_response.json");
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
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<GetServiceBindingResponse> invoke(GetServiceBindingRequest request) {
            return this.serviceBindings.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListServiceBindingsRequest, ListServiceBindingsResponse> {

        private final SpringServiceBindings serviceBindings = new SpringServiceBindings(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListServiceBindingsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/service_bindings?q=app_guid%20IN%20dd44fd4f-5e20-4c52-b66d-7af6e201f01e&page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/service_bindings/GET_response.json");
        }

        @Override
        protected ListServiceBindingsResponse getResponse() {
            return ListServiceBindingsResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(ServiceBindingResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2015-07-27T22:43:06Z")
                        .id("d6d87c3d-a38f-4b31-9bbe-2432d2faaa1d")
                        .url("/v2/service_bindings/d6d87c3d-a38f-4b31-9bbe-2432d2faaa1d")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("bbd1f170-bb1f-481d-bcf7-def2bbe6a3a2")
                        .credential("creds-key-3", "creds-val-3")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/bbd1f170-bb1f-481d-bcf7-def2bbe6a3a2")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2015-11-03T00:53:50Z")
                        .id("925d8848-4808-47cf-a3e8-049aa0163328")
                        .updatedAt("2015-11-04T12:54:50Z")
                        .url("/v2/service_bindings/925d8848-4808-47cf-a3e8-049aa0163328")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                        .credential("creds-key-108", "creds-val-108")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/f99b3d23-55f9-48b5-add3-d7ab08b2ff0c")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2015-07-27T22:43:20Z")
                        .id("42eda707-fe4d-4eed-9b39-7cb5e665c226")
                        .url("/v2/service_bindings/42eda707-fe4d-4eed-9b39-7cb5e665c226")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceId("650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                        .credential("creds-key-356", "creds-val-356")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                        .serviceInstanceUrl("/v2/service_instances/650d0eb7-3b83-414a-82a0-d503d1c8eb5f")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListServiceBindingsRequest getValidRequest() throws Exception {
            return ListServiceBindingsRequest.builder()
                .applicationId("dd44fd4f-5e20-4c52-b66d-7af6e201f01e")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListServiceBindingsResponse> invoke(ListServiceBindingsRequest request) {
            return this.serviceBindings.list(request);
        }

    }

}
