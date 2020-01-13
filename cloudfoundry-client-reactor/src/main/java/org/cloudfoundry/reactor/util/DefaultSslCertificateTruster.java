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

package org.cloudfoundry.reactor.util;

import io.netty.handler.ssl.SslContextBuilder;
import org.cloudfoundry.reactor.ProxyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.SslProvider.SslContextSpec;
import reactor.netty.tcp.TcpClient;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class DefaultSslCertificateTruster implements SslCertificateTruster {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.trust");

    private final AtomicReference<X509TrustManager> delegate;

    private final Optional<ProxyConfiguration> proxyConfiguration;

    private final LoopResources threadPool;

    private final Set<Tuple2<String, Integer>> trustedHostsAndPorts;

    public DefaultSslCertificateTruster(Optional<ProxyConfiguration> proxyConfiguration, LoopResources threadPool) {
        this.proxyConfiguration = proxyConfiguration;
        this.threadPool = threadPool;
        this.delegate = new AtomicReference<>(getTrustManager(getTrustManagerFactory(null)));
        this.trustedHostsAndPorts = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
        this.delegate.get().checkClientTrusted(x509Certificates, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
        this.delegate.get().checkServerTrusted(x509Certificates, authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.delegate.get().getAcceptedIssuers();
    }

    @Override
    public Mono<Void> trust(String host, int port, Duration duration) {
        Tuple2<String, Integer> hostAndPort = Tuples.of(host, port);
        if (this.trustedHostsAndPorts.contains(hostAndPort)) {
            return Mono.empty();
        }

        this.logger.warn("Trusting SSL Certificate for {}:{}", host, port);

        X509TrustManager trustManager = this.delegate.get();

        return getUntrustedCertificates(duration, host, port, this.proxyConfiguration, this.threadPool, trustManager)
            .doOnNext(untrustedCertificates -> {
                KeyStore trustStore = addToTrustStore(untrustedCertificates, trustManager);
                this.delegate.set(getTrustManager(getTrustManagerFactory(trustStore)));
            })
            .doOnSuccess(untrustedCertificates -> {
                this.trustedHostsAndPorts.add(hostAndPort);
                this.logger.debug("Trusted SSL Certificate for {}:{}", host, port);
            })
            .then();
    }

    private static KeyStore addToTrustStore(X509Certificate[] untrustedCertificates, X509TrustManager trustManager) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);

            int count = 0;
            for (X509Certificate certificate : untrustedCertificates) {
                trustStore.setCertificateEntry(String.valueOf(count++), certificate);
            }
            for (X509Certificate certificate : trustManager.getAcceptedIssuers()) {
                trustStore.setCertificateEntry(String.valueOf(count++), certificate);
            }

            return trustStore;
        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static void configureSsl(SslContextSpec sslContextSpec, CertificateCollectingTrustManager collector) {
        sslContextSpec.sslContext(SslContextBuilder.forClient().trustManager(new StaticTrustManagerFactory(collector)));
    }

    private static TcpClient getTcpClient(Optional<ProxyConfiguration> proxyConfiguration, LoopResources threadPool, CertificateCollectingTrustManager collector, String host, int port) {
        TcpClient tcpClient = TcpClient.create()
            .runOn(threadPool)
            .host(host)
            .port(port)
            .secure(spec -> configureSsl(spec, collector));

        return proxyConfiguration.map(configuration -> configuration.configure(tcpClient))
            .orElse(tcpClient);
    }

    private static X509TrustManager getTrustManager(TrustManagerFactory trustManagerFactory) {
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }

        throw new IllegalStateException("No X509TrustManager in TrustManagerFactory");
    }

    private static TrustManagerFactory getTrustManagerFactory(KeyStore trustStore) {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            return trustManagerFactory;
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<X509Certificate[]> getUntrustedCertificates(Duration duration, String host, int port,
                                                                    Optional<ProxyConfiguration> proxyConfiguration,
                                                                    LoopResources threadPool, X509TrustManager delegate) {

        CertificateCollectingTrustManager collector = new CertificateCollectingTrustManager(delegate);
        TcpClient tcpClient = getTcpClient(proxyConfiguration, threadPool, collector, host, port);
        return tcpClient.handle((inbound, outbound) -> inbound.receive()
            .then())
            .connect()
            .timeout(duration)
            .handle((connection, sink) -> {
                X509Certificate[] chain = collector.getCollectedCertificateChain();

                if (chain == null) {
                    sink.error(new IllegalStateException("Could not obtain server certificate chain"));
                }

                if (collector.isTrusted()) {
                    sink.complete();
                } else {
                    sink.next(chain);
                }

                connection.dispose();
            });
    }

}
