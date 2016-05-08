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

package org.cloudfoundry.reactor.client.v2.servicekeys;

import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.DeleteServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.GetServiceKeyResponse;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysRequest;
import org.cloudfoundry.client.v2.servicekeys.ListServiceKeysResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyEntity;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.client.v2.Resource.Metadata;

public final class ReactorServiceKeysTest {

    public static final class Create extends AbstractClientApiTest<CreateServiceKeyRequest, CreateServiceKeyResponse> {

        private final ReactorServiceKeys serviceKeys = new ReactorServiceKeys(this.authorizationProvider, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/service_keys")
                    .payload("fixtures/client/v2/service_keys/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/service_keys/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateServiceKeyRequest getInvalidRequest() {
            return CreateServiceKeyRequest.builder().build();
        }

        @Override
        protected CreateServiceKeyResponse getResponse() {
            return CreateServiceKeyResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:22Z")
                    .id("79aa4b11-99f3-484b-adfc-a63fa818c4d1")
                    .url("/v2/service_keys/79aa4b11-99f3-484b-adfc-a63fa818c4d1")
                    .build())
                .entity(ServiceKeyEntity.builder()
                    .credential("creds-key-392", "creds-val-392")
                    .name("name-960")
                    .serviceInstanceId("132944c8-c31d-4bb8-9155-ae4e2ebe1a0c")
                    .serviceInstanceUrl("/v2/service_instances/132944c8-c31d-4bb8-9155-ae4e2ebe1a0c")
                    .build())
                .build();
        }

        @Override
        protected CreateServiceKeyRequest getValidRequest() throws Exception {
            return CreateServiceKeyRequest.builder()
                .name("name-960")
                .serviceInstanceId("132944c8-c31d-4bb8-9155-ae4e2ebe1a0c")
                .build();
        }

        @Override
        protected Mono<CreateServiceKeyResponse> invoke(CreateServiceKeyRequest request) {
            return this.serviceKeys.create(request);
        }
    }

    public static final class Delete extends AbstractClientApiTest<DeleteServiceKeyRequest, Void> {

        private final ReactorServiceKeys serviceKeys = new ReactorServiceKeys(this.authorizationProvider, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/service_keys/test-service-key-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected DeleteServiceKeyRequest getInvalidRequest() {
            return DeleteServiceKeyRequest.builder().build();
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteServiceKeyRequest getValidRequest() throws Exception {
            return DeleteServiceKeyRequest.builder()
                .serviceKeyId("test-service-key-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteServiceKeyRequest request) {
            return this.serviceKeys.delete(request);
        }
    }

    public static final class Get extends AbstractClientApiTest<GetServiceKeyRequest, GetServiceKeyResponse> {

        private final ReactorServiceKeys serviceKeys = new ReactorServiceKeys(this.authorizationProvider, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_keys/test-service-key-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_keys/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetServiceKeyRequest getInvalidRequest() {
            return GetServiceKeyRequest.builder().build();
        }

        @Override
        protected GetServiceKeyResponse getResponse() {
            return GetServiceKeyResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:22Z")
                    .id("7f1f30d3-bed3-4ba7-bf88-fd3a678ff4f5")
                    .url("/v2/service_keys/7f1f30d3-bed3-4ba7-bf88-fd3a678ff4f5")
                    .build())
                .entity(ServiceKeyEntity.builder()
                    .credential("creds-key-388", "creds-val-388")
                    .name("name-947")
                    .serviceInstanceId("011457da-c205-4415-a578-de5df82b15a8")
                    .serviceInstanceUrl("/v2/service_instances/011457da-c205-4415-a578-de5df82b15a8")
                    .build())
                .build();
        }

        @Override
        protected GetServiceKeyRequest getValidRequest() throws Exception {
            return GetServiceKeyRequest.builder()
                .serviceKeyId("test-service-key-id")
                .build();
        }

        @Override
        protected Mono<GetServiceKeyResponse> invoke(GetServiceKeyRequest request) {
            return this.serviceKeys.get(request);
        }
    }

    public static final class List extends AbstractClientApiTest<ListServiceKeysRequest, ListServiceKeysResponse> {

        private final ReactorServiceKeys serviceKeys = new ReactorServiceKeys(this.authorizationProvider, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/service_keys?q=name%20IN%20test-name&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/service_keys/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListServiceKeysRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected ListServiceKeysResponse getResponse() {
            return ListServiceKeysResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceKeyResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:22Z")
                        .id("3936801c-9d3f-4b9f-8465-aa3bd263612e")
                        .url("/v2/service_keys/3936801c-9d3f-4b9f-8465-aa3bd263612e")
                        .build())
                    .entity(ServiceKeyEntity.builder()
                        .credential("creds-key-383", "creds-val-383")
                        .name("name-934")
                        .serviceInstanceId("84d384d9-42c2-4e4b-a8c6-865e9446e024")
                        .serviceInstanceUrl("/v2/service_instances/84d384d9-42c2-4e4b-a8c6-865e9446e024")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListServiceKeysRequest getValidRequest() throws Exception {
            return ListServiceKeysRequest.builder()
                .name("test-name")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListServiceKeysResponse> invoke(ListServiceKeysRequest request) {
            return this.serviceKeys.list(request);
        }
    }

}
