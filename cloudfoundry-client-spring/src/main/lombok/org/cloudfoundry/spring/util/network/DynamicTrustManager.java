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

package org.cloudfoundry.spring.util.network;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.tuple.Tuple2;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@ToString
public final class DynamicTrustManager implements HostnameVerifier, SslCertificateTruster, X509TrustManager {

    public static final int MAX_PORT = 65535;

    public static final int MIN_PORT = 1;

    public static final int SSL_PORT = 443;

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.trust");

    private final AtomicReference<X509TrustManager> delegate;

    private final boolean trustCertificates;

    private final Set<String> trustedHosts;

    private final Set<Tuple2<String, Integer>> trustedHostsAndPorts;

    public DynamicTrustManager(Boolean trustCertificates) {
        this.delegate = new AtomicReference<>(getTrustManager(getTrustManagerFactory(null)));
        this.trustCertificates = Optional.ofNullable(trustCertificates).orElse(false);
        this.trustedHosts = Collections.newSetFromMap(new ConcurrentHashMap<>());
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
    public void trust(String host, int port, int timeout, TimeUnit timeUnit) {
        if (!this.trustCertificates) {
            return;
        }

        int resolvedPort = resolvePort(port);

        Tuple2<String, Integer> hostAndPort = Tuple2.of(host, resolvedPort);
        if (this.trustedHostsAndPorts.contains(hostAndPort)) {
            return;
        }

        this.logger.warn("Trusting SSL Certificate for {}:{}", host, resolvedPort);

        X509TrustManager trustManager = this.delegate.get();
        X509Certificate[] untrustedCertificates = getUntrustedCertificates(host, resolvedPort, timeout, timeUnit, trustManager);

        if (untrustedCertificates != null) {
            KeyStore trustStore = addToTrustStore(untrustedCertificates, trustManager);
            this.delegate.set(getTrustManager(getTrustManagerFactory(trustStore)));
        }

        this.trustedHosts.add(host);
        this.trustedHostsAndPorts.add(hostAndPort);
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return this.trustedHosts.contains(hostname);
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

    private static X509Certificate[] getUntrustedCertificates(String host, int port, int timeout, TimeUnit timeUnit, X509TrustManager delegate) {
        try {
            CertificateCollectingTrustManager collector = new CertificateCollectingTrustManager(delegate);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{collector}, null);

            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
            socket.setSoTimeout((int) timeUnit.toMillis(timeout));

            try {
                socket.startHandshake();
                socket.close();
            } catch (SSLException e) {
                // Swallow exception
            }

            X509Certificate[] chain = collector.getCollectedCertificateChain();
            if (chain == null) {
                throw new IllegalStateException("Could not obtain server certificate chain");
            }

            if (collector.isTrusted()) {
                return null;
            } else {
                return chain;
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static int resolvePort(int candidate) {
        if (candidate < MIN_PORT || MAX_PORT < candidate) {
            return SSL_PORT;
        } else {
            return candidate;
        }
    }

}
