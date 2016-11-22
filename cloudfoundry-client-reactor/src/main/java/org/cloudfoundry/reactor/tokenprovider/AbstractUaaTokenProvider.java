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

package org.cloudfoundry.reactor.tokenprovider;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class AbstractUaaTokenProvider implements TokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.token");

    private static final Duration REFRESH_MARGIN = Duration.ofSeconds(10);

    private final Object refreshTokenMonitor = new Object();

    private final ConcurrentMap<ConnectionContext, Mono<String>> tokens = new ConcurrentHashMap<>(1);

    private volatile String refreshToken;

    /**
     * The client id.  Defaults to {@code cf}.
     */
    @Value.Default
    public String getClientId() {
        return "cf";
    }

    /**
     * The client secret Defaults to {@code ""}.
     */
    @Value.Default
    public String getClientSecret() {
        return "";
    }

    @Override
    public final Mono<String> getToken(ConnectionContext connectionContext) {
        return this.tokens.computeIfAbsent(connectionContext, this::getTokenFlow);
    }

    protected abstract UriComponentsBuilder getAccessTokenUri(UriComponentsBuilder builder);

    private static Duration getRefreshDelay(Map<String, Integer> r) {
        return Duration.ofSeconds(r.get("expires_in")).minus(REFRESH_MARGIN);
    }

    private String getAuthorizationValue() {
        String encoded = Base64.getEncoder().encodeToString(new AsciiString(getClientId()).concat(":").concat(getClientSecret()).toByteArray());
        return String.format("Basic %s", encoded);
    }

    private UriComponentsBuilder getRefreshTokenUri(UriComponentsBuilder builder, String refreshToken) {
        return builder
            .queryParam("grant_type", "refresh_token")
            .queryParam("client_id", getClientId())
            .queryParam("client_secret", getClientSecret())
            .queryParam("refresh_token", refreshToken);
    }

    @SuppressWarnings("unchecked")
    private Mono<String> getTokenFlow(ConnectionContext connectionContext) {
        return connectionContext
            .getRoot("authorization_endpoint")
            .map(this::getTokenUri)
            .then(uri -> connectionContext.getHttpClient()
                .post(uri, outbound -> outbound
                    .header(HttpHeaderNames.CONTENT_LENGTH, "0")
                    .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)
                    .header(HttpHeaderNames.AUTHORIZATION, getAuthorizationValue())
                    .header(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON)
                    .removeTransferEncodingChunked()
                    .sendHeaders())
                .doOnSubscribe(NetworkLogging.post(uri))
                .compose(NetworkLogging.response(uri)))
            .then(i -> i.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(connectionContext.getObjectMapper(), Map.class))
            .doOnNext(r -> {
                synchronized (this.refreshTokenMonitor) {
                    this.refreshToken = (String) r.get("refresh_token");
                }
            })
            .flatMap(r -> Flux.merge(
                Mono.just(r.get("access_token")),
                Mono.delay(getRefreshDelay(r)).then()
            ))
            .repeat()
            .cast(String.class)
            .doOnNext(token -> LOGGER.debug("JWT Token: {}", token))
            .cache(1)
            .next();
    }

    private String getTokenUri(String root) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(root)
            .pathSegment("oauth", "token");

        synchronized (this.refreshTokenMonitor) {
            builder = this.refreshToken == null ? getAccessTokenUri(builder) : getRefreshTokenUri(builder, this.refreshToken);
        }

        return builder.build().encode().toUriString();
    }

}
