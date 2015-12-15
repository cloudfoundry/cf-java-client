/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * An {@link SslCertificateTruster} that collects the SSL certificate presented by a connection
 */
public final class CertificateCollectingSslCertificateTruster implements SslCertificateTruster {

    @Override
    public void trust(String host, int port, int timeout, TimeUnit timeUnit) throws GeneralSecurityException,
            IOException {
        X509Certificate[] untrusted = getUntrustedCertificate(host, port, timeout, timeUnit);

        if (untrusted != null) {
            appendToTruststore(untrusted);
        }

        HttpsURLConnection.setDefaultHostnameVerifier(new ExplicitHostnameVerifier(host));
    }

    private void appendToTruststore(X509Certificate[] chain) throws KeyStoreException, NoSuchAlgorithmException,
            IOException, CertificateException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);

        int count = 0;
        for (X509Certificate cert : getDefaultTrustManager().getAcceptedIssuers()) {
            trustStore.setCertificateEntry(String.valueOf(count++), cert);
        }
        for (X509Certificate cert : chain) {
            trustStore.setCertificateEntry("" + count++, cert);
        }

        String password = UUID.randomUUID().toString();

        File trustStoreOutputFile = File.createTempFile("truststore", null);
        trustStoreOutputFile.deleteOnExit();
        trustStore.store(new FileOutputStream(trustStoreOutputFile), password.toCharArray());

        System.setProperty("javax.net.ssl.trustStore", trustStoreOutputFile.getAbsolutePath());
        System.setProperty("javax.net.ssl.trustStorePassword", password);
    }

    private X509TrustManager getDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init((KeyStore) null);
        return (X509TrustManager) factory.getTrustManagers()[0];
    }

    private X509Certificate[] getUntrustedCertificate(String host, int port, int timeout, TimeUnit timeUnit)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException,
            CertificateException {
        CertificateCollectingTrustManager collector = new CertificateCollectingTrustManager(getDefaultTrustManager());

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
            throw new CertificateException("Could not obtain server certificate chain");
        }

        if (collector.isTrusted()) {
            return null;
        } else {
            return chain;
        }
    }

}
