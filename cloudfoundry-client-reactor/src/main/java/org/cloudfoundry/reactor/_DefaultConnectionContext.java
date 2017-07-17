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

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.reactor.util.DefaultSslCertificateTruster;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.cloudfoundry.reactor.util.SslCertificateTruster;
import org.cloudfoundry.reactor.util.StaticTrustManagerFactory;
import org.cloudfoundry.reactor.util.UserAgent;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.resources.LoopResources;
import reactor.ipc.netty.resources.PoolResources;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.channel.ChannelOption.SO_RCVBUF;
import static io.netty.channel.ChannelOption.SO_SNDBUF;

/**
 * The default implementation of the {@link ConnectionContext} interface.  This is the implementation that should be used for most non-testing cases.
 */
@Value.Immutable
abstract class _DefaultConnectionContext implements ConnectionContext {

    private static final int DEFAULT_PORT = 443;

    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-.]+$");

    private static final int RECEIVE_BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int SEND_BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int UNDEFINED_PORT = -1;

    /**
     * Disposes resources created to service this connection context
     */
    @PreDestroy
    public final void dispose() {
        getConnectionPool().ifPresent(PoolResources::dispose);
        getThreadPool().dispose();
    }

    /**
     * The number of connections to use when processing requests and responses.  Setting this to `null` disables connection pooling.
     */
    @Nullable
    @Value.Default
    public Integer getConnectionPoolSize() {
        return PoolResources.DEFAULT_POOL_MAX_CONNECTION;
    }

    @Override
    @Value.Default
    public HttpClient getHttpClient() {
        return HttpClient.create(options -> {
            options
                .compression(true)
                .loopResources(getThreadPool())
                .option(SO_SNDBUF, SEND_BUFFER_SIZE)
                .option(SO_RCVBUF, RECEIVE_BUFFER_SIZE)
                .disablePool();

            options.sslSupport(ssl -> getSslCertificateTruster().ifPresent(trustManager -> ssl.trustManager(new StaticTrustManagerFactory(trustManager))));

            getConnectionPool().ifPresent(options::poolResources);
            getConnectTimeout().ifPresent(socketTimeout -> options.option(CONNECT_TIMEOUT_MILLIS, (int) socketTimeout.toMillis()));
            getKeepAlive().ifPresent(keepAlive -> options.option(SO_KEEPALIVE, keepAlive));
            getSslHandshakeTimeout().ifPresent(options::sslHandshakeTimeout);

            getProxyConfiguration()
                .ifPresent(c -> options.proxy(proxyOptions -> {
                    proxyOptions.host(c.getHost());

                    c.getPort().ifPresent(proxyOptions::port);
                    c.getUsername().ifPresent(proxyOptions::username);
                    c.getPassword().map(password -> (Function<String, String>) s -> password).ifPresent(proxyOptions::password);
                }));
        });
    }

    @Override
    @Value.Default
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .registerModule(new Jdk8Module())
            .setSerializationInclusion(NON_NULL);

        getProblemHandlers().forEach(objectMapper::addHandler);

        return objectMapper;
    }

    /**
     * The port for the Cloud Foundry instance
     */
    @Value.Default
    public Integer getPort() {
        return DEFAULT_PORT;
    }

    @Value.Derived
    public Mono<String> getRoot() {
        Integer port = getPort();
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().scheme("https").host(getApiHost());
        if (port != null) {
            builder.port(port);
        }

        UriComponents components = normalize(builder, getScheme());
        trust(components, getSslCertificateTruster());

        return Mono.just(components.toUriString());
    }

    @Override
    public Mono<String> getRoot(String key) {
        return getInfo()
            .map(info -> normalize(UriComponentsBuilder.fromUriString(info.get(key)), getScheme()))
            .doOnNext(components -> trust(components, getSslCertificateTruster()))
            .map(UriComponents::toUriString)
            .cache();
    }

    /**
     * The number of worker threads to use when processing requests and responses
     */
    @Value.Default
    public Integer getThreadPoolSize() {
        return LoopResources.DEFAULT_IO_WORKER_COUNT;
    }

    @Value.Check
    void checkForValidApiHost() {
        Matcher matcher = HOSTNAME_PATTERN.matcher(getApiHost());

        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("API hostname %s is not correctly formatted (e.g. 'api.local.pcfdev.io')", getApiHost()));
        }
    }

    /**
     * The hostname of the API root.  Typically something like {@code api.run.pivotal.io}.
     */
    abstract String getApiHost();

    /**
     * The {@code CONNECT_TIMEOUT_MILLIS} value
     */
    abstract Optional<Duration> getConnectTimeout();

    @Value.Derived
    Optional<PoolResources> getConnectionPool() {
        return Optional.ofNullable(getConnectionPoolSize())
            .map(connectionPoolSize -> PoolResources.fixed("cloudfoundry-client", connectionPoolSize));
    }

    @SuppressWarnings("unchecked")
    @Value.Derived
    Mono<Map<String, String>> getInfo() {
        return getRoot()
            .map(uri -> UriComponentsBuilder.fromUriString(uri).pathSegment("v2", "info").build().encode().toUriString())
            .flatMap(uri -> getHttpClient()
                .get(uri, request -> Mono.just(request)
                    .map(UserAgent::addUserAgent)
                    .map(JsonCodec::addDecodeHeaders)
                    .flatMapMany(HttpClientRequest::send))
                .doOnSubscribe(NetworkLogging.get(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(JsonCodec.decode(getObjectMapper(), Map.class))
            .map(m -> (Map<String, String>) m)
            .cache();
    }

    /**
     * The {@code SO_KEEPALIVE} value
     */
    abstract Optional<Boolean> getKeepAlive();

    /**
     * Jackson deserialization problem handlers.  Typically only used for testing.
     */
    abstract List<DeserializationProblemHandler> getProblemHandlers();

    /**
     * The (optional) proxy configuration
     */
    abstract Optional<ProxyConfiguration> getProxyConfiguration();

    @Value.Derived
    String getScheme() {
        if (getSecure().orElse(true)) {
            return "https";
        } else {
            return "http";
        }
    }

    /**
     * Whether the connection to the root API should be secure (i.e. using HTTPS).
     */
    abstract Optional<Boolean> getSecure();

    /**
     * Whether to skip SSL certificate validation for all hosts reachable from the API host.  Defaults to {@code false}.
     */
    abstract Optional<Boolean> getSkipSslValidation();

    @Value.Derived
    Optional<SslCertificateTruster> getSslCertificateTruster() {
        if (getSkipSslValidation().orElse(false)) {
            return Optional.of(new DefaultSslCertificateTruster(getProxyConfiguration()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * The timeout for the SSL handshake negotiation
     */
    abstract Optional<Duration> getSslHandshakeTimeout();

    @Value.Derived
    LoopResources getThreadPool() {
        return LoopResources.create("cloudfoundry-client", getThreadPoolSize(), true);
    }

    private static void trust(UriComponents components, Optional<SslCertificateTruster> sslCertificateTruster) {
        sslCertificateTruster.ifPresent(t -> t.trust(components.getHost(), components.getPort(), Duration.ofSeconds(30)));
    }

    private UriComponents normalize(UriComponentsBuilder builder, String scheme) {
        UriComponents components = builder.build();

        builder.scheme(scheme);

        if (UNDEFINED_PORT == components.getPort()) {
            builder.port(getPort());
        }

        return builder.build().encode();
    }

}
