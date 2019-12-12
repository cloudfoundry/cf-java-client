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
                .method(GET)
                .path("/")
                .header(CUSTOM_REQUEST_TAG_NAME, CUSTOM_REQUEST_TAG_VALUE)
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .build())
            .build());

        createOperator().flatMap(operator -> operator.get()
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
        return new AbstractReactorOperations(connectionContext,
            root,
            TOKEN_PROVIDER,
            Collections.singletonMap(CUSTOM_REQUEST_TAG_NAME, CUSTOM_REQUEST_TAG_VALUE)) {

            public Mono<Operator> getOperator() {
                return createOperator();
            }
        }.getOperator();
    }

}
