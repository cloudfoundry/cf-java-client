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

import io.netty.handler.codec.http.HttpHeaders;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.Operator;
import org.cloudfoundry.reactor.util.OperatorContext;
import org.cloudfoundry.reactor.util.UserAgent;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An abstract implementation of {@link RootProvider} that ensures that returned values are trusted (if configured) and cached.
 */
abstract class AbstractRootProvider implements RootProvider {

    private static final int DEFAULT_PORT = 443;

    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-.]+$");

    private static final int UNDEFINED_PORT = -1;

    @Value.Check
    public final void checkForValidApiHost() {
        Matcher matcher = HOSTNAME_PATTERN.matcher(getApiHost());

        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("API hostname %s is not correctly formatted (e.g. 'api.local.pcfdev.io')", getApiHost()));
        }
    }

    public Mono<Operator> createOperator(ConnectionContext connectionContext) {
        HttpClient httpClient = connectionContext.getHttpClient();
        return getRoot(connectionContext)
            .map(root -> OperatorContext.of(connectionContext, root))
            .map(operatorContext -> new Operator(operatorContext, httpClient))
            .map(operator -> operator.headers(this::addHeaders));
    }

    /**
     * The hostname of the API root. Typically something like {@code api.run.pivotal.io}.
     */
    public abstract String getApiHost();

    @Override
    public final Mono<String> getRoot(String key, ConnectionContext connectionContext) {
        Mono<String> cached = doGetRoot(key, connectionContext)
            .delayUntil(uri -> trust(uri.getHost(), uri.getPort(), connectionContext))
            .map(UriComponents::toUriString);

        return connectionContext.getCacheDuration()
            .map(cached::cache)
            .orElseGet(cached::cache);
    }

    @Override
    public final Mono<String> getRoot(ConnectionContext connectionContext) {
        Mono<String> cached = doGetRoot(connectionContext)
            .delayUntil(uri -> trust(uri.getHost(), uri.getPort(), connectionContext))
            .map(UriComponents::toUriString);

        return connectionContext.getCacheDuration()
            .map(cached::cache)
            .orElseGet(cached::cache);
    }

    protected abstract Mono<UriComponents> doGetRoot(ConnectionContext connectionContext);

    protected abstract Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext);

    protected final UriComponents getRoot() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().scheme("https").host(getApiHost());
        getPort().ifPresent(builder::port);

        return normalize(builder);
    }

    protected final UriComponents normalize(UriComponentsBuilder builder) {
        UriComponents components = builder.build();

        builder.scheme(getScheme());

        if (UNDEFINED_PORT == components.getPort()) {
            builder.port(getPort().orElse(DEFAULT_PORT));
        }

        return builder.build().encode();
    }

    /**
     * The port for the Cloud Foundry instance. Defaults to {@code 443}.
     */
    abstract Optional<Integer> getPort();

    /**
     * Whether the connection to the root API should be secure (i.e. using HTTPS). Defaults to {@code true}.
     */
    abstract Optional<Boolean> getSecure();

    private void addHeaders(HttpHeaders httpHeaders) {
        UserAgent.setUserAgent(httpHeaders);
        JsonCodec.setDecodeHeaders(httpHeaders);
    }

    private String getScheme() {
        if (getSecure().orElse(true)) {
            return "https";
        } else {
            return "http";
        }
    }

    private Mono<Void> trust(String host, int port, ConnectionContext connectionContext) {
        return connectionContext.trust(host, port);
    }

}
