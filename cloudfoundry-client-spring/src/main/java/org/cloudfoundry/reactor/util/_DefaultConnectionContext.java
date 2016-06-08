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
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.io.netty.config.HttpClientOptions;
import reactor.io.netty.http.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static reactor.io.netty.config.NettyHandlerNames.SslHandler;

@Value.Immutable
abstract class _DefaultConnectionContext implements ConnectionContext {

    private static final int DEFAULT_PORT = 443;

    private static final int UNDEFINED_PORT = -1;

    public abstract AuthorizationProvider getAuthorizationProvider();

    @Value.Default
    public String getClientId() {
        return "cf";
    }

    @Value.Default
    public String getClientSecret() {
        return "";
    }

    @Value.Derived
    public HttpClient getHttpClient() {
        return HttpClient.create(HttpClientOptions.create()
            .followRedirects(true)
            .sslSupport()
            .pipelineConfigurer(pipeline -> getProxyContext().getHttpProxyHandler().ifPresent(handler -> pipeline.addBefore(SslHandler, null, handler)))
            .sslConfigurer(ssl -> getSslCertificateTruster().ifPresent(trustManager -> ssl.trustManager(new StaticTrustManagerFactory(trustManager)))));
    }

    @Value.Default
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Value.Derived
    public Mono<String> getRoot() {
        Integer port = getPort();
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().scheme("https").host(getHost());
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

    abstract String getHost();

    @SuppressWarnings("unchecked")
    @Value.Derived
    Mono<Map<String, String>> getInfo() {
        return getRoot()
            .map(uri -> UriComponentsBuilder.fromUriString(uri).pathSegment("v2", "info").build().toUriString())
            .then(getHttpClient()::get)
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(getObjectMapper(), Map.class))
            .map(m -> (Map<String, String>) m)
            .cache();
    }

    @Nullable
    abstract Integer getPort();

    @Value.Derived
    ProxyContext getProxyContext() {
        return ProxyContext.builder()
            .host(getProxyHost())
            .password(getProxyPassword())
            .port(getProxyPort())
            .username(getProxyUsername())
            .build();
    }

    @Nullable
    abstract String getProxyHost();

    @Nullable
    abstract String getProxyPassword();

    @Nullable
    abstract Integer getProxyPort();

    @Nullable
    abstract String getProxyUsername();

    @Value.Derived
    Optional<SslCertificateTruster> getSslCertificateTruster() {
        if (Optional.ofNullable(getTrustCertificates()).orElse(false)) {
            return Optional.of(new DefaultSslCertificateTruster(getProxyContext()));
        } else {
            return Optional.empty();
        }
    }

    @Nullable
    abstract Boolean getTrustCertificates();

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
