/*
 * Copyright 2013-2017 the original author or authors.
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
import org.cloudfoundry.reactor.util.UserAgent;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest.Form;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An abstract base class for all token providers that interact with the UAA.  It encapsulates the logic to refresh the token before expiration.
 */
public abstract class AbstractUaaTokenProvider implements TokenProvider {

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

    /**
     * Returns the current refresh token.  May be {@code null} if there is no valid refresh token.
     */
    public final String getRefreshToken() {
        synchronized (this.refreshTokenMonitor) {
            return this.refreshToken;
        }
    }

    @Override
    public final Mono<String> getToken(ConnectionContext connectionContext) {
        return this.tokens.computeIfAbsent(connectionContext, this::getTokenFlow);
    }

    /**
     * Augment the form with the payload required to request and access token
     *
     * @param form the form to augment
     */
    protected abstract void accessTokenPayload(Form form);

    private static Duration getRefreshDelay(Map<String, Integer> r) {
        return Duration.ofSeconds(r.get("expires_in")).minus(REFRESH_MARGIN);
    }

    private static String getTokenUri(String root) {
        return UriComponentsBuilder.fromUriString(root)
            .pathSegment("oauth", "token")
            .build().encode().toUriString();
    }

    private String getAuthorizationValue() {
        String encoded = Base64.getEncoder().encodeToString(new AsciiString(getClientId()).concat(":").concat(getClientSecret()).toByteArray());
        return String.format("Basic %s", encoded);
    }

    @SuppressWarnings("unchecked")
    private Mono<String> getTokenFlow(ConnectionContext connectionContext) {
        return connectionContext
            .getRoot("authorization_endpoint")
            .map(AbstractUaaTokenProvider::getTokenUri)
            .then(uri -> connectionContext.getHttpClient()
                .post(uri, request -> Mono.just(request)
                    .transform(UserAgent::addUserAgent)
                    .flatMap(r -> r
                        .header(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON)
                        .header(HttpHeaderNames.AUTHORIZATION, getAuthorizationValue())
                        .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)
                        .sendForm(this::tokenPayload)
                        .then()))
                .doOnSubscribe(NetworkLogging.post(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(JsonCodec.decode(connectionContext.getObjectMapper(), Map.class))
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

    private void refreshTokenPayload(Form form, String refreshToken) {
        form
            .multipart(false)
            .attr("grant_type", "refresh_token")
            .attr("client_id", getClientId())
            .attr("client_secret", getClientSecret())
            .attr("refresh_token", refreshToken);
    }

    private void tokenPayload(Form form) {
        synchronized (this.refreshTokenMonitor) {
            if (this.refreshToken == null) {
                accessTokenPayload(form);
            } else {
                refreshTokenPayload(form, this.refreshToken);
            }
        }
    }

}
