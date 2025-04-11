/*
 * Copyright 2013-2021 the original author or authors.
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

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

final class RootPayloadRootProviderTest extends AbstractRestTest {

    private final RootPayloadRootProvider rootProvider =
            RootPayloadRootProvider.builder()
                    .apiHost("localhost")
                    .port(this.mockWebServer.getPort())
                    .secure(false)
                    .objectMapper(CONNECTION_CONTEXT.getObjectMapper())
                    .build();

    @Test
    void getRoot() {
        this.rootProvider
                .getRoot(CONNECTION_CONTEXT)
                .as(StepVerifier::create)
                .expectNext(String.format("http://localhost:%d", this.mockWebServer.getPort()))
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getLoggingKey() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/").build())
                        .response(
                                TestResponse.builder()
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
    void secureNormalizeUrlHttps() {
        RootPayloadRootProvider secureRootProvider =
                RootPayloadRootProvider.builder()
                        .apiHost("localhost")
                        .secure(true)
                        .objectMapper(CONNECTION_CONTEXT.getObjectMapper())
                        .build();
        UriComponents normalized =
                secureRootProvider.normalize(
                        UriComponentsBuilder.fromUriString("https://api.run.pivotal.io/v2"));
        assertEquals("https://api.run.pivotal.io:443/v2", normalized.toString());
    }

    @Test
    void secureNormalizeUrlNonHttps() {
        RootPayloadRootProvider secureRootProvider =
                RootPayloadRootProvider.builder()
                        .apiHost("localhost")
                        .secure(true)
                        .objectMapper(CONNECTION_CONTEXT.getObjectMapper())
                        .build();
        UriComponents normalized =
                secureRootProvider.normalize(
                        UriComponentsBuilder.fromUriString("wss://doppler.run.pivotal.io:8080"));
        // TODO: Is this expected behavior? Replacing "wss" (or any other protocol) with "https".
        assertEquals("https://doppler.run.pivotal.io:8080", normalized.toString());
    }

    @Test
    void getRootKey() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/GET_response.json")
                                        .build())
                        .build());

        this.rootProvider
                .getRoot("cloud_controller_v2", CONNECTION_CONTEXT)
                .as(StepVerifier::create)
                .expectNext(
                        String.format(
                                "http://api.run.pivotal.io:%d/v2", this.mockWebServer.getPort()))
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getEmptyKey() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/GET_response.json")
                                        .build())
                        .build());

        Queue<String> keyList = new LinkedList<String>(Arrays.asList("links", "empty_value"));
        this.rootProvider
                .getRootKey(keyList, CONNECTION_CONTEXT)
                .as(StepVerifier::create)
                .expectNext("")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getStructuredKey() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/GET_response.json")
                                        .build())
                        .build());
        Queue<String> keyList =
                new LinkedList<String>(
                        Arrays.asList("links", "cloud_controller_v2", "meta", "version"));

        this.rootProvider
                .getRootKey(keyList, CONNECTION_CONTEXT)
                .as(StepVerifier::create)
                .expectNext("2.93.0")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getRootKeyNoKey() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/GET_response.json")
                                        .build())
                        .build());

        this.rootProvider
                .getRoot("invalid-key", CONNECTION_CONTEXT)
                .as(StepVerifier::create)
                .expectError(IllegalArgumentException.class)
                .verify(Duration.ofSeconds(5));
    }
}
