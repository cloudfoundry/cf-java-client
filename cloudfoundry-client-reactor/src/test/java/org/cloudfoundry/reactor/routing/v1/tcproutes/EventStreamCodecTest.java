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

package org.cloudfoundry.reactor.routing.v1.tcproutes;

import org.cloudfoundry.reactor.AbstractRestTest;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientResponse;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class EventStreamCodecTest extends AbstractRestTest {

    @Test
    public void allData() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("org/cloudfoundry/reactor/routing/v1/tcproutes/all-data.txt")
                .build())
            .build());

        CONNECTION_CONTEXT.getHttpClient()
            .get()
            .uri(this.root.block())
            .responseConnection(EventStreamCodecTest::toEventsFlux)
            .as(StepVerifier::create)
            .expectNext(ServerSentEvent.builder()
                .data("This is the first message.")
                .build())
            .expectNext(ServerSentEvent.builder()
                .data("This is the second message, it")
                .data("has two lines.")
                .build())
            .expectNext(ServerSentEvent.builder()
                .data("This is the third message.")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void colonSpacing() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("org/cloudfoundry/reactor/routing/v1/tcproutes/colon-spacing.txt")
                .build())
            .build());

        CONNECTION_CONTEXT.getHttpClient()
            .get()
            .uri(this.root.block())
            .responseConnection(EventStreamCodecTest::toEventsFlux)
            .as(StepVerifier::create)
            .expectNext(ServerSentEvent.builder()
                .data("test")
                .build())
            .expectNext(ServerSentEvent.builder()
                .data("test")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void randomColons() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("org/cloudfoundry/reactor/routing/v1/tcproutes/random-colons.txt")
                .build())
            .build());

        CONNECTION_CONTEXT.getHttpClient()
            .get()
            .uri(this.root.block())
            .responseConnection(EventStreamCodecTest::toEventsFlux)
            .as(StepVerifier::create)
            .expectNext(ServerSentEvent.builder()
                .data("")
                .build())
            .expectNext(ServerSentEvent.builder()
                .data("")
                .data("")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void threeLines() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("org/cloudfoundry/reactor/routing/v1/tcproutes/three-lines.txt")
                .build())
            .build());

        CONNECTION_CONTEXT.getHttpClient()
            .get()
            .uri(this.root.block())
            .responseConnection(EventStreamCodecTest::toEventsFlux)
            .as(StepVerifier::create)
            .expectNext(ServerSentEvent.builder()
                .data("YHOO")
                .data("+2")
                .data("10")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void withComment() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("org/cloudfoundry/reactor/routing/v1/tcproutes/with-comment.txt")
                .build())
            .build());

        CONNECTION_CONTEXT.getHttpClient()
            .get()
            .uri(this.root.block())
            .responseConnection(EventStreamCodecTest::toEventsFlux)
            .as(StepVerifier::create)
            .expectNext(ServerSentEvent.builder()
                .id("1")
                .data("first event")
                .build())
            .expectNext(ServerSentEvent.builder()
                .id("")
                .data("second event")
                .build())
            .expectNext(ServerSentEvent.builder()
                .data(" third event")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void withEventTypes() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("org/cloudfoundry/reactor/routing/v1/tcproutes/with-event-types.txt")
                .build())
            .build());

        CONNECTION_CONTEXT.getHttpClient()
            .get()
            .uri(this.root.block())
            .responseConnection(EventStreamCodecTest::toEventsFlux)
            .as(StepVerifier::create)
            .expectNext(ServerSentEvent.builder()
                .eventType("add")
                .data("73857293")
                .build())
            .expectNext(ServerSentEvent.builder()
                .eventType("remove")
                .data("2153")
                .build())
            .expectNext(ServerSentEvent.builder()
                .eventType("add")
                .data("113411")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static Flux<ServerSentEvent> toEventsFlux(HttpClientResponse response, Connection connection) {
        connection.addHandler(EventStreamCodec.createDecoder(response));
        ByteBufFlux body = connection.inbound().receive();

        return EventStreamCodec.decode(body);
    }

}
