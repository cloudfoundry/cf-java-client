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

package org.cloudfoundry.reactor.client.v2.servicekeys;

import org.cloudfoundry.client.v2.Metadata;
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
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServiceKeysTest extends AbstractClientApiTest {

    private final ReactorServiceKeys serviceKeys = new ReactorServiceKeys(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/service_keys")
                .payload("fixtures/client/v2/service_keys/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_keys/POST_response.json")
                .build())
            .build());

        this.serviceKeys
            .create(CreateServiceKeyRequest.builder()
                .name("name-960")
                .serviceInstanceId("132944c8-c31d-4bb8-9155-ae4e2ebe1a0c")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateServiceKeyResponse.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_keys/test-service-key-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceKeys
            .delete(DeleteServiceKeyRequest.builder()
                .serviceKeyId("test-service-key-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_keys/test-service-key-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_keys/GET_{id}_response.json")
                .build())
            .build());

        this.serviceKeys
            .get(GetServiceKeyRequest.builder()
                .serviceKeyId("test-service-key-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceKeyResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:23Z")
                    .id("6ad2cc9b-1996-49a3-9538-dfc0da3b1f32")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/service_keys/6ad2cc9b-1996-49a3-9538-dfc0da3b1f32")
                    .build())
                .entity(ServiceKeyEntity.builder()
                    .credential("creds-key-7", "creds-val-7")
                    .name("name-140")
                    .serviceInstanceId("ca567b3d-e142-4139-94e3-1e0c010ba728")
                    .serviceInstanceUrl("/v2/service_instances/ca567b3d-e142-4139-94e3-1e0c010ba728")
                    .serviceKeyParametersUrl("/v2/service_keys/6ad2cc9b-1996-49a3-9538-dfc0da3b1f32/parameters")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_keys?q=name%3Atest-name&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_keys/GET_response.json")
                .build())
            .build());

        this.serviceKeys
            .list(ListServiceKeysRequest.builder()
                .name("test-name")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceKeysResponse.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
