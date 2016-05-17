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

package org.cloudfoundry.reactor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.io.netty.config.ClientOptions;
import reactor.io.netty.http.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static reactor.io.netty.config.NettyHandlerNames.SslHandler;

public final class DefaultConnectionContext implements ConnectionContext {

    private static final int DEFAULT_PORT = 443;

    private static final int UNDEFINED_PORT = -1;

    private final AuthorizationProvider authorizationProvider;

    private final String clientId;

    private final String clientSecret;

    private final HttpClient httpClient;

    private final Mono<Map<String, String>> info;

    private final ObjectMapper objectMapper;

    private final Mono<String> root;

    private final Optional<SslCertificateTruster> sslCertificateTruster;

    @Builder
    DefaultConnectionContext(@NonNull AuthorizationProvider authorizationProvider, String clientId, String clientSecret, @NonNull String host, ObjectMapper objectMapper, Integer port,
                             String proxyHost, String proxyPassword, Integer proxyPort, String proxyUsername, Boolean trustCertificates) {

        ProxyContext proxyContext = ProxyContext.builder()
            .host(proxyHost)
            .password(proxyPassword)
            .port(proxyPort)
            .username(proxyUsername)
            .build();

        this.sslCertificateTruster = createSslCertificateTruster(proxyContext, trustCertificates);
        this.httpClient = createHttpClient(proxyContext, this.sslCertificateTruster);

        this.authorizationProvider = authorizationProvider;
        this.clientId = Optional.ofNullable(clientId).orElse("cf");
        this.clientSecret = Optional.ofNullable(clientSecret).orElse("");
        this.root = getRoot(host, port, this.sslCertificateTruster);
        this.objectMapper = getObjectMapper(objectMapper);
        this.info = getInfo(this.httpClient, this.objectMapper, this.root);
    }

    @Override
    public AuthorizationProvider getAuthorizationProvider() {
        return this.authorizationProvider;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public String getClientSecret() {
        return this.clientSecret;
    }

    @Override
    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    public Mono<String> getRoot() {
        return this.root;
    }

    @Override
    public Mono<String> getRoot(String key) {
        return this.info
            .map(info -> normalize(UriComponentsBuilder.fromUriString(info.get(key))))
            .doOnSuccess(components -> trust(components, this.sslCertificateTruster))
            .map(UriComponents::toUriString)
            .cache();
    }

    private static HttpClient createHttpClient(ProxyContext proxyContext, Optional<SslCertificateTruster> sslCertificateTruster) {
        return HttpClient.create(ClientOptions.create()
            .sslSupport()
            .pipelineConfigurer(pipeline -> proxyContext.getHttpProxyHandler().ifPresent(handler -> pipeline.addBefore(SslHandler, null, handler)))
            .sslConfigurer(ssl -> sslCertificateTruster.ifPresent(trustManager -> ssl.trustManager(new StaticTrustManagerFactory(trustManager)))));
    }

    private static Optional<SslCertificateTruster> createSslCertificateTruster(ProxyContext proxyContext, Boolean trustCertificates) {
        if (Optional.ofNullable(trustCertificates).orElse(false)) {
            return Optional.of(new DefaultSslCertificateTruster(proxyContext));
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private static Mono<Map<String, String>> getInfo(HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        return root
            .map(uri -> UriComponentsBuilder.fromUriString(uri).pathSegment("v2", "info").build().toUriString())
            .then(httpClient::get)
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(objectMapper, Map.class))
            .map(m -> (Map<String, String>) m)
            .cache();
    }

    private static ObjectMapper getObjectMapper(ObjectMapper objectMapper) {
        return Optional.ofNullable(objectMapper).orElse(new ObjectMapper());
    }

    private static Mono<String> getRoot(String host, Integer port, Optional<SslCertificateTruster> sslCertificateTruster) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().scheme("https").host(host);
        if (port != null) {
            builder.port(port);
        }

        UriComponents components = normalize(builder);
        trust(components, sslCertificateTruster);

        return Mono.just(components.toUriString());
    }

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
