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

import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class InfoPayloadRootProviderTest extends AbstractRestTest {

    private final InfoPayloadRootProvider rootProvider = InfoPayloadRootProvider.builder()
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
    public void getRootKey() {
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
    public void getRootKeyNoKey() {
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

}
