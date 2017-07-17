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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.util.AsciiString;
import org.cloudfoundry.Nullable;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
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

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final ConcurrentMap<ConnectionContext, Mono<String>> accessTokens = new ConcurrentHashMap<>(1);

    private final ConcurrentMap<ConnectionContext, ReplayProcessor<String>> refreshTokenStreams = new ConcurrentHashMap<>(1);

    private final ConcurrentMap<ConnectionContext, Mono<String>> refreshTokens = new ConcurrentHashMap<>(1);

    /**
     * The client id. Defaults to {@code cf}.
     */
    @Value.Default
    public String getClientId() {
        return "cf";
    }

    /**
     * The client secret. Defaults to {@code ""}.
     */
    @Value.Default
    public String getClientSecret() {
        return "";
    }

    /**
     * Returns a {@link Flux} of refresh tokens for a connection
     *
     * @param connectionContext A {@link ConnectionContext} to be used to identity which connection the refresh tokens be retrieved for
     * @return a {@link Flux} that emits the last token on subscribe and new refresh tokens as they are negotiated
     */
    public Flux<String> getRefreshTokens(ConnectionContext connectionContext) {
        return getRefreshTokenStream(connectionContext);
    }

    @Override
    public final Mono<String> getToken(ConnectionContext connectionContext) {
        return this.accessTokens.computeIfAbsent(connectionContext, this::token);
    }

    @Override
    public void invalidate(ConnectionContext connectionContext) {
        this.accessTokens.put(connectionContext, token(connectionContext));
    }

    /**
     * The identity zone subdomain
     */
    @Nullable
    abstract String getIdentityZoneSubdomain();

    /**
     * Transforms a {@code Mono} in order to make a request to negotiate an access token
     *
     * @param outbound the {@link Mono} to transform to perform the token request
     */
    abstract Mono<Void> tokenRequestTransformer(Mono<HttpClientRequest> outbound);

    private static HttpClientRequest addContentType(HttpClientRequest request) {
        return request
            .header(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
    }

    private static HttpClientRequest disableChunkedTransfer(HttpClientRequest request) {
        return request.chunkedTransfer(false);
    }

    private static HttpClientRequest disableFailOnError(HttpClientRequest request) {
        return request
            .failOnClientError(false)
            .failOnServerError(false);
    }

    private static String extractAccessToken(Map<String, String> payload) {
        String accessToken = payload.get(ACCESS_TOKEN);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Access Token: {}", accessToken);

            parseToken(accessToken)
                .ifPresent(claims -> {
                    LOGGER.debug("Access Token Issued At:  {} UTC", toLocalDateTime(claims.getIssuedAt()));
                    LOGGER.debug("Access Token Expires At: {} UTC", toLocalDateTime(claims.getExpiration()));
                });
        }

        return String.format("%s %s", payload.get(TOKEN_TYPE), accessToken);
    }

    private static String getTokenUri(String root, String identityZoneId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(root);

        if (identityZoneId != null) {
            builder.host(String.format("%s.%s", identityZoneId, builder.build().getHost()));
        }

        return builder
            .pathSegment("oauth", "token")
            .build().encode().toUriString();
    }

    private static Optional<Claims> parseToken(String token) {
        try {
            String jws = token.substring(0, token.lastIndexOf('.') + 1);
            return Optional.of(Jwts.parser().parseClaimsJwt(jws).getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.from(date.toInstant().atZone(UTC));
    }

    private HttpClientRequest addAuthorization(HttpClientRequest request) {
        String encoded = Base64.getEncoder().encodeToString(new AsciiString(getClientId()).concat(":").concat(getClientSecret()).toByteArray());
        return request.header(AUTHORIZATION, String.format("Basic %s", encoded));
    }

    private Consumer<Map<String, String>> extractRefreshToken(ConnectionContext connectionContext) {
        return payload -> Optional.ofNullable(payload.get(REFRESH_TOKEN))
            .ifPresent(refreshToken -> {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Refresh Token: {}", refreshToken);

                    parseToken(refreshToken)
                        .ifPresent(claims -> {
                            LOGGER.debug("Refresh Token Issued At:  {} UTC", toLocalDateTime(claims.getIssuedAt()));
                            LOGGER.debug("Refresh Token Expires At: {} UTC", toLocalDateTime(claims.getExpiration()));
                        });
                }

                this.refreshTokens.put(connectionContext, Mono.just(refreshToken));
                getRefreshTokenStream(connectionContext).onNext(refreshToken);
            });
    }

    @SuppressWarnings("unchecked")
    private Function<Mono<HttpClientResponse>, Mono<String>> extractTokens(ConnectionContext connectionContext) {
        return inbound -> inbound
            .transform(JsonCodec.decode(connectionContext.getObjectMapper(), Map.class))
            .map(payload -> (Map<String, String>) payload)
            .doOnNext(extractRefreshToken(connectionContext))
            .map(AbstractUaaTokenProvider::extractAccessToken);
    }

    private ReplayProcessor<String> getRefreshTokenStream(ConnectionContext connectionContext) {
        return this.refreshTokenStreams.computeIfAbsent(connectionContext, c -> ReplayProcessor.create(1));
    }

    private Mono<HttpClientResponse> primaryToken(ConnectionContext connectionContext) {
        return requestToken(connectionContext, this::tokenRequestTransformer);
    }

    private Mono<HttpClientResponse> refreshToken(ConnectionContext connectionContext, String refreshToken) {
        return requestToken(connectionContext, refreshTokenGrantTokenRequestTransformer(refreshToken))
            .onErrorResume(t -> t instanceof UaaException && ((UaaException) t).getStatusCode() == UNAUTHORIZED.code(), t -> Mono.empty());
    }

    private Function<Mono<HttpClientRequest>, Mono<Void>> refreshTokenGrantTokenRequestTransformer(String refreshToken) {
        return outbound -> outbound
            .flatMap(request -> request
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
            .map(root -> getTokenUri(root, getIdentityZoneSubdomain()))
            .flatMap(uri -> connectionContext.getHttpClient()
                .post(uri, request -> Mono.just(request)
                    .map(AbstractUaaTokenProvider::disableChunkedTransfer)
                    .map(AbstractUaaTokenProvider::disableFailOnError)
                    .map(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .map(AbstractUaaTokenProvider::addContentType)
                    .map(JsonCodec::addDecodeHeaders)
                    .transform(tokenRequestTransformer))
                .doOnSubscribe(NetworkLogging.post(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(ErrorPayloadMapper.uaa(connectionContext.getObjectMapper()));
    }

    private Mono<String> token(ConnectionContext connectionContext) {
        return this.refreshTokens.getOrDefault(connectionContext, Mono.empty())
            .flatMap(refreshToken -> refreshToken(connectionContext, refreshToken)
                .doOnSubscribe(s -> LOGGER.debug("Negotiating using refresh token")))
            .switchIfEmpty(primaryToken(connectionContext)
                .doOnSubscribe(s -> LOGGER.debug("Negotiating using token provider")))
            .transform(ErrorPayloadMapper.fallback())
            .transform(extractTokens(connectionContext))
            .cache()
            .checkpoint();
    }

}
