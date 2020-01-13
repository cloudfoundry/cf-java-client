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

package org.cloudfoundry.reactor;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class DelegatingRootProviderTest extends AbstractRestTest {

    private final DelegatingRootProvider rootProvider = DelegatingRootProvider.builder()
        .apiHost("localhost")
        .port(this.mockWebServer.getPort())
        .secure(false)
        .objectMapper(CONNECTION_CONTEXT.getObjectMapper())
        .build();

    @Test
    public void getRoot() {
        this.rootProvider
            .getRoot(CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext(String.format("http://localhost:%d", this.mockWebServer.getPort()))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootCloudFoundryClientV2() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("cloud_controller_v2", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext(String.format("http://api.run.pivotal.io:%d/v2", this.mockWebServer.getPort()))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootCloudFoundryClientV2Fallback() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("cloud_controller_v2", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext(String.format("http://localhost:%d/v2", this.mockWebServer.getPort()))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootCloudFoundryClientV3() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("cloud_controller_v3", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext(String.format("http://api.run.pivotal.io:%d/v3", this.mockWebServer.getPort()))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootCloudFoundryClientV3Fallback() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("cloud_controller_v3", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext(String.format("http://localhost:%d/v3", this.mockWebServer.getPort()))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootDopplerClient() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("logging", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext("http://doppler.run.pivotal.io:443")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootDopplerClientFallback() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("logging", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext("http://doppler.vcap.me:80")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootKeyNoKey() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("invalid-key", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectError(IllegalArgumentException.class)
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootKeyNoValue() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("empty_value", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectError(IllegalArgumentException.class)
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootNetworkingClient() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("network_policy_v1", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext(String.format("http://api.run.pivotal.io:%d/networking/v1/external", this.mockWebServer.getPort()))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootRoutingClientFallback() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("routing", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext("http://localhost:3000")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootTokenProviderFallback() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("authorization_endpoint", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext("http://localhost:8080/uaa")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootUaaClient() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("uaa", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext(String.format("http://uaa.run.pivotal.io:%d", this.mockWebServer.getPort()))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getRootUaaClientFallback() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(NOT_FOUND)
                .payload("fixtures/client/v2/error_response.json")
                .build())
            .build());

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("uaa", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext("http://localhost:8080/uaa")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getWithInvalidRoot() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(HttpResponseStatus.FOUND)
                .build())
            .build());

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.rootProvider
            .getRoot("uaa", CONNECTION_CONTEXT)
            .as(StepVerifier::create)
            .expectNext("http://localhost:8080/uaa")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

