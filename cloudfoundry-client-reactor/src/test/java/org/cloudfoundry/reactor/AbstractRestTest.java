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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.ComparisonFailure;
import org.slf4j.bridge.SLF4JBridgeHandler;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public abstract class AbstractRestTest {

    protected static final ConnectionContext CONNECTION_CONTEXT = DefaultConnectionContext.builder()
        .apiHost("localhost")
        .secure(false)
        .problemHandler(new FailingDeserializationProblemHandler())  // Test-only problem handler
        .build();

    protected static final TokenProvider TOKEN_PROVIDER = connectionContext -> Mono.just("test-authorization");

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    protected final Mono<String> root;

    final MockWebServer mockWebServer;

    private MultipleRequestDispatcher multipleRequestDispatcher = new MultipleRequestDispatcher();

    protected AbstractRestTest() {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.setDispatcher(this.multipleRequestDispatcher);

        this.root = Mono.just(this.mockWebServer.url("/").uri().toString());
    }

    @After
    public final void shutdown() throws IOException {
        this.mockWebServer.shutdown();
    }

    @After
    public final void verify() {
        this.multipleRequestDispatcher.verify();
    }

    protected final void mockRequest(InteractionContext interactionContext) {
        this.multipleRequestDispatcher.add(interactionContext);
    }

    private static final class FailingDeserializationProblemHandler extends DeserializationProblemHandler {

        @Override
        public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
            fail(String.format("Found unexpected property %s in payload for %s", propertyName, beanOrClass.getClass().getName()));
            return false;
        }

    }

    private static final class MultipleRequestDispatcher extends Dispatcher {

        private Queue<InteractionContext> responses = new LinkedList<>();

        private List<InteractionContext> verifications = new ArrayList<>();

        @Override
        public MockResponse dispatch(RecordedRequest request) {
            InteractionContext interactionContext = this.responses.poll();

            if (interactionContext == null) {
                throw new IllegalStateException(String.format("Unexpected request for %s %s received", request.getMethod(), request.getPath()));
            }

            interactionContext.setDone(true);

            try {
                interactionContext.getRequest().assertEquals(request);
                return interactionContext.getResponse().getMockResponse();
            } catch (ComparisonFailure e) {
                e.printStackTrace();
                return new MockResponse().setResponseCode(400);
            }
        }

        private void add(InteractionContext interactionContext) {
            this.responses.add(interactionContext);
            this.verifications.add(interactionContext);
        }

        private void verify() {
            for (InteractionContext interactionContext : this.verifications) {
                TestRequest request = interactionContext.getRequest();

                assertThat(interactionContext.isDone())
                    .as("Expected request to %s %s not received", request.getMethod(), request.getPath())
                    .isTrue();
            }
        }

    }

}
