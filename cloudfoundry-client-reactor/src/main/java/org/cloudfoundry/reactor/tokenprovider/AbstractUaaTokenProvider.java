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

import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.util.ErrorPayloadMapper;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.cloudfoundry.reactor.util.UserAgent;
import org.cloudfoundry.uaa.UaaException;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCEPT;
import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

/**
 * An abstract base class for all token providers that interact with the UAA.  It encapsulates the logic to refresh the token before expiration.
 */
public abstract class AbstractUaaTokenProvider implements TokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.token");

    private static final String ACCESS_TOKEN = "access_token";

    private static final String AUTHORIZATION_ENDPOINT = "authorization_endpoint";

    private static final String REFRESH_TOKEN = "refresh_token";

    private static final String TOKEN_TYPE = "token_type";

    private final ConcurrentMap<ConnectionContext, Mono<String>> accessTokens = new ConcurrentHashMap<>(1);

    private final ConcurrentMap<ConnectionContext, Mono<String>> refreshTokens = new ConcurrentHashMap<>(1);

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
        return this.accessTokens.getOrDefault(connectionContext, Mono.empty());
    }

    @Override
    public void invalidate(ConnectionContext connectionContext) {
        this.accessTokens.put(connectionContext, token(connectionContext));
    }

    /**
     * Transforms a {@code Mono} in order to make a request to negotiate an access token
     *
     * @param outbound the {@link Mono} to transform to perform the token request
     */
    abstract Mono<Void> tokenRequestTransformer(Mono<HttpClientRequest> outbound);

    private static HttpClientRequest addContentTypes(HttpClientRequest request) {
        return request
            .header(ACCEPT, APPLICATION_JSON)
            .header(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
    }

    private static HttpClientRequest disableFailOnError(HttpClientRequest request) {
        return request
            .failOnClientError(false)
            .failOnServerError(false);
    }

    private static String getTokenUri(String root) {
        return UriComponentsBuilder.fromUriString(root)
            .pathSegment("oauth", "token")
            .build().encode().toUriString();
    }

    private HttpClientRequest addAuthorization(HttpClientRequest request) {
        String encoded = Base64.getEncoder().encodeToString(new AsciiString(getClientId()).concat(":").concat(getClientSecret()).toByteArray());
        return request.header(AUTHORIZATION, String.format("Basic %s", encoded));
    }

    private Function<Mono<HttpClientResponse>, Mono<String>> extractTokens(ConnectionContext connectionContext) {
        return inbound -> inbound
            .transform(JsonCodec.decode(connectionContext.getObjectMapper(), Map.class))
            .doOnNext(payload -> Optional.ofNullable(payload.get(REFRESH_TOKEN))
                .map(s -> (String) s)
                .ifPresent(refreshToken -> {
                    LOGGER.debug("Refresh Token: {}", refreshToken);
                    this.refreshTokens.put(connectionContext, Mono.just(refreshToken));
                }))
            .map(payload -> {
                String token = String.format("%s %s", payload.get(TOKEN_TYPE), payload.get(ACCESS_TOKEN));
                LOGGER.debug("Access Token:  {}", token);
                return token;

            });
    }

    private Mono<HttpClientResponse> primaryToken(ConnectionContext connectionContext) {
        return requestToken(connectionContext, this::tokenRequestTransformer);
    }

    private Mono<HttpClientResponse> refreshToken(ConnectionContext connectionContext, String refreshToken) {
        return requestToken(connectionContext, refreshTokenGrantTokenRequestTransformer(refreshToken))
            .otherwise(t -> t instanceof UaaException && ((UaaException) t).getStatusCode() == UNAUTHORIZED.code(), t -> Mono.empty());
    }

    private Function<Mono<HttpClientRequest>, Mono<Void>> refreshTokenGrantTokenRequestTransformer(String refreshToken) {
        return outbound -> outbound
            .then(request -> request
                .sendForm(form -> form
                    .multipart(false)
                    .attr("client_id", getClientId())
                    .attr("client_secret", getClientSecret())
                    .attr("grant_type", "refresh_token")
                    .attr("refresh_token", refreshToken))
                .then());
    }

    private Mono<HttpClientResponse> requestToken(ConnectionContext connectionContext, Function<Mono<HttpClientRequest>, Mono<Void>> tokenRequestTransformer) {
        return connectionContext
            .getRoot(AUTHORIZATION_ENDPOINT)
            .map(AbstractUaaTokenProvider::getTokenUri)
            .then(uri -> connectionContext.getHttpClient()
                .post(uri, request -> Mono.just(request)
                    .map(AbstractUaaTokenProvider::disableFailOnError)
                    .map(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .map(AbstractUaaTokenProvider::addContentTypes)
                    .transform(tokenRequestTransformer))
                .doOnSubscribe(NetworkLogging.post(uri))
                .transform(NetworkLogging.response(uri))
                .transform(ErrorPayloadMapper.uaa(connectionContext.getObjectMapper())));
    }

    private Mono<String> token(ConnectionContext connectionContext) {
        return this.refreshTokens.getOrDefault(connectionContext, Mono.empty())
            .then(refreshToken -> refreshToken(connectionContext, refreshToken)
                .doOnSubscribe(s -> LOGGER.debug("Negotiating using refresh token")))
            .otherwiseIfEmpty(primaryToken(connectionContext)
                .doOnSubscribe(s -> LOGGER.debug("Negotiating using token provider")))
            .transform(ErrorPayloadMapper.fallback())
            .transform(extractTokens(connectionContext))
            .cache();
    }

}
