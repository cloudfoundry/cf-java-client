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
import org.cloudfoundry.reactor.util.DefaultSslCertificateTruster;
import org.cloudfoundry.reactor.util.SslCertificateTruster;
import org.cloudfoundry.reactor.util.StaticTrustManagerFactory;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.options.ClientOptions;
import reactor.ipc.netty.resources.PoolResources;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;
import static io.netty.channel.ChannelOption.SO_RCVBUF;
import static io.netty.channel.ChannelOption.SO_SNDBUF;
import static io.netty.channel.ChannelOption.SO_TIMEOUT;

abstract class AbstractConnectionContext implements ConnectionContext {

    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9-.]+$");

    private static final int RECEIVE_BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int SEND_BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int UNDEFINED_PORT = -1;

    private final Integer defaultPort;

    private final String name;

    AbstractConnectionContext(Integer defaultPort, String name) {
        this.defaultPort = defaultPort;
        this.name = name;
    }

    @Value.Default
    public Integer getConnectionPoolSize() {
        return 2 * PoolResources.DEFAULT_POOL_MAX_CONNECTION;
    }

    @Override
    @Value.Default
    public HttpClient getHttpClient() {
        return HttpClient.create(options -> {
            options
                .option(SO_SNDBUF, SEND_BUFFER_SIZE)
                .option(SO_RCVBUF, RECEIVE_BUFFER_SIZE)
                .poolResources(PoolResources.fixed(this.name, getConnectionPoolSize()));

            getKeepAlive().ifPresent(keepAlive -> options.option(SO_KEEPALIVE, keepAlive));
            getProxyConfiguration().ifPresent(c -> options.proxy(ClientOptions.Proxy.HTTP, c.getHost(), c.getPort().orElse(null), c.getUsername().orElse(null), u -> c.getPassword().orElse(null)));
            getSocketTimeout().ifPresent(socketTimeout -> options.option(SO_TIMEOUT, (int) socketTimeout.toMillis()));

            options.sslSupport(ssl -> getSslCertificateTruster().ifPresent(trustManager -> ssl.trustManager(new StaticTrustManagerFactory(trustManager))));
            getSslHandshakeTimeout().ifPresent(options::sslHandshakeTimeout);
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

    @Value.Default
    public Integer getPort() {
        return this.defaultPort;
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

    static void trust(UriComponents components, Optional<SslCertificateTruster> sslCertificateTruster) {
        sslCertificateTruster.ifPresent(t -> t.trust(components.getHost(), components.getPort(), Duration.ofSeconds(30)));
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

    /**
     * The {@code SO_TIMEOUT} value
     */
    abstract Optional<Duration> getSocketTimeout();

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

    final UriComponents normalize(UriComponentsBuilder builder, String scheme) {
        UriComponents components = builder.build();

        builder.scheme(scheme);

        if (UNDEFINED_PORT == components.getPort()) {
            builder.port(getPort());
        }

        return builder.build().encode();
    }

}
