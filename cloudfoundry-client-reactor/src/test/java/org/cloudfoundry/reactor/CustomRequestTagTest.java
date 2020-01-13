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

import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.Operator;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class CustomRequestTagTest extends AbstractRestTest {

    private static final String CUSTOM_REQUEST_TAG_NAME = "test-header";

    private static final String CUSTOM_REQUEST_TAG_VALUE = "test-header-value";

    private final DefaultConnectionContext connectionContext = DefaultConnectionContext.builder()
        .apiHost(this.mockWebServer.getHostName())
        .port(this.mockWebServer.getPort())
        .secure(false)
        .build();

    @Test
    public void addCustomHttpHeader() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .header(CUSTOM_REQUEST_TAG_NAME, CUSTOM_REQUEST_TAG_VALUE)
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .build())
            .build());

        createOperator()
            .flatMap(operator -> operator.get()
                .uri(uri -> uri.path("/"))
                .response()
                .get())
            .as(StepVerifier::create)
            .expectNextMatches(httpClientResponse -> httpClientResponse.status()
                .equals(OK))
            .expectComplete()
            .verify(Duration.ofSeconds(5));

    }

    private Mono<Operator> createOperator() {
        return new AbstractReactorOperations(this.connectionContext, this.root, TOKEN_PROVIDER, Collections.singletonMap(CUSTOM_REQUEST_TAG_NAME, CUSTOM_REQUEST_TAG_VALUE)) {

            private Mono<Operator> getOperator() {
                return createOperator();
            }

        }.getOperator();
    }

}
