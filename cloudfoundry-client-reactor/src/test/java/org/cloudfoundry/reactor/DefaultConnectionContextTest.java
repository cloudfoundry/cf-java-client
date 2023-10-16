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

import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

final class DefaultConnectionContextTest extends AbstractRestTest {

    private final DefaultConnectionContext connectionContext = DefaultConnectionContext.builder()
        .apiHost(this.mockWebServer.getHostName())
        .port(this.mockWebServer.getPort())
        .secure(false)
        .build();

    @AfterEach
    void dispose() {
        this.connectionContext.dispose();
    }

    @Test
    void getInfo() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/GET_response.json")
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

        this.connectionContext.getRootProvider()
            .getRoot("token_endpoint", this.connectionContext)
            .as(StepVerifier::create)
            .expectNext("http://localhost:8080/uaa")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void multipleInstances() {
        DefaultConnectionContext first = DefaultConnectionContext.builder()
            .apiHost("test-host")
            .build();

        DefaultConnectionContext second = DefaultConnectionContext.builder()
            .apiHost("test-host")
            .build();

        first.monitorByteBufAllocator();
        second.monitorByteBufAllocator();

        first.dispose();
        second.dispose();
    }

    @Test
    void configurationAlwaysApplied() {
        DefaultConnectionContext ctx = DefaultConnectionContext.builder()
            .connectionPoolSize(24)
            .apiHost("api.example.com")
            .keepAlive(true)
            .proxyConfiguration(
                ProxyConfiguration.builder()
                    .host("proxy.example.com")
                    .port(8080)
                    .username("foo")
                    .password("bar")
                    .build())
            .skipSslValidation(true)
            .build();

        assertThat(ctx.getConnectionPoolSize()).isEqualTo(24);
        assertThat(ctx.getApiHost()).isEqualTo("api.example.com");
        assertThat(ctx.getSkipSslValidation()).isEqualTo(Optional.of(true));

        HttpClient client = ctx.getHttpClient();
        assertThat(client.configuration().isSecure()).isEqualTo(true);

        InetSocketAddress addr = client.configuration().proxyProvider().getAddress().get();
        assertThat(addr.getHostName()).isEqualTo("proxy.example.com");
        assertThat(addr.getPort()).isEqualTo(8080);
        assertThat(client.configuration().proxyProvider().getType()).isEqualTo(ProxyProvider.Proxy.HTTP);

        assertThat(client.configuration().loggingHandler().level()).isEqualTo(LogLevel.TRACE);
        assertThat(client.configuration().loggingHandler().byteBufFormat()).isEqualTo(ByteBufFormat.HEX_DUMP);
    }

}
