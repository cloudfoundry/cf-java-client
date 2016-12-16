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
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public abstract class AbstractRestTest {

    protected static final ConnectionContext CONNECTION_CONTEXT = DefaultConnectionContext.builder()
        .apiHost("localhost")
        .httpClient(HttpClient.create())
        .problemHandler(new FailingDeserializationProblemHandler())  // Test-only problem handler
        .build();

    protected static final TokenProvider TOKEN_PROVIDER = connectionContext -> Mono.just("test-authorization");

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    protected final MockWebServer mockWebServer = new MockWebServer();

    protected final Mono<String> root = Mono.just(UriComponentsBuilder.newInstance()
        .scheme("http").host(this.mockWebServer.getHostName()).port(this.mockWebServer.getPort())
        .build().encode().toUriString());

    private InteractionContext interactionContext;

    @After
    public final void verify() {
        if (this.interactionContext != null) {
            assertThat(this.interactionContext.isDone()).as("Expected request not received").isTrue();
        }
    }

    protected final void mockRequest(InteractionContext interactionContext) {
        this.interactionContext = interactionContext;
        this.mockWebServer.setDispatcher(new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (interactionContext.isDone()) {
                    throw new IllegalStateException("Additional request received: " + request);
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
        });
    }

    private static final class FailingDeserializationProblemHandler extends DeserializationProblemHandler {

        @Override
        public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
            fail(String.format("Found unexpected property %s in payload for %s", propertyName, beanOrClass.getClass().getSimpleName()));
            return false;
        }

    }

}
