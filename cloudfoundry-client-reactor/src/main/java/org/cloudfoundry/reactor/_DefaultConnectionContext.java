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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.cloudfoundry.reactor.util.DefaultSslCertificateTruster;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.cloudfoundry.reactor.util.SslCertificateTruster;
import org.cloudfoundry.reactor.util.StaticTrustManagerFactory;
import org.cloudfoundry.util.test.FailingDeserializationProblemHandler;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.config.ClientOptions;
import reactor.ipc.netty.config.HttpClientOptions;
import reactor.ipc.netty.http.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * The default implementation of the {@link ConnectionContext} interface.  This is the implementation that should be used for most non-testing cases.
 */
@Value.Immutable
abstract class _DefaultConnectionContext implements ConnectionContext {

    private static final int DEFAULT_PORT = 443;

    private static final int RECEIVE_BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int SEND_BUFFER_SIZE = 10 * 1024 * 1024;

    private static final int UNDEFINED_PORT = -1;

    @Override
    @Value.Default
    public HttpClient getHttpClient() {
        ClientOptions options = HttpClientOptions.create()
            .sslSupport()
            .sndbuf(SEND_BUFFER_SIZE)
            .rcvbuf(RECEIVE_BUFFER_SIZE);

        getProxyConfiguration().ifPresent(c -> options.proxy(ClientOptions.Proxy.HTTP, c.getHost(), c.getPort().orElse(null), c.getUsername().orElse(null), u -> c.getPassword().orElse(null)));
        getSocketTimeout().ifPresent(options::timeout);
        getSslCertificateTruster().ifPresent(trustManager -> options.ssl().trustManager(new StaticTrustManagerFactory(trustManager)));
        getSslHandshakeTimeout().ifPresent(options::sslHandshakeTimeout);

        return HttpClient.create(options);
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
        return DEFAULT_PORT;
    }

    @Value.Derived
    public Mono<String> getRoot() {
        Integer port = getPort();
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().scheme("https").host(getApiHost());
        if (port != null) {
            builder.port(port);
        }

        UriComponents components = normalize(builder);
        trust(components, getSslCertificateTruster());

        return Mono.just(components.toUriString());
    }

    @Override
    public Mono<String> getRoot(String key) {
        return getInfo()
            .map(info -> normalize(UriComponentsBuilder.fromUriString(info.get(key))))
            .doOnSuccess(components -> trust(components, getSslCertificateTruster()))
            .map(UriComponents::toUriString)
            .cache();
    }

    /**
     * The hostname of the API root.  Typically something like {@code api.run.pivotal.io}.
     */
    abstract String getApiHost();

    @SuppressWarnings("unchecked")
    @Value.Derived
    Mono<Map<String, String>> getInfo() {
        return getRoot()
            .map(uri -> UriComponentsBuilder.fromUriString(uri).pathSegment("v2", "info").build().toUriString())
            .then(uri -> getHttpClient()
                .get(uri)
                .doOnSubscribe(NetworkLogging.get(uri))
                .compose(NetworkLogging.response(uri)))
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(getObjectMapper(), Map.class))
            .map(m -> (Map<String, String>) m)
            .cache();
    }

    /**
     * Jackson deserialization problem handlers.  Typically only used for testing.
     */
    abstract List<FailingDeserializationProblemHandler> getProblemHandlers();

    /**
     * The (optional) proxy configuration
     */
    abstract Optional<ProxyConfiguration> getProxyConfiguration();

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

    private static UriComponents normalize(UriComponentsBuilder builder) {
        UriComponents components = builder.build();

        builder.scheme("https");

        if (UNDEFINED_PORT == components.getPort()) {
            builder.port(DEFAULT_PORT);
        }

        return builder.build().encode();
    }

    private static void trust(UriComponents components, Optional<SslCertificateTruster> sslCertificateTruster) {
        sslCertificateTruster.ifPresent(t -> t.trust(components.getHost(), components.getPort(), Duration.ofSeconds(30)));
    }

}
