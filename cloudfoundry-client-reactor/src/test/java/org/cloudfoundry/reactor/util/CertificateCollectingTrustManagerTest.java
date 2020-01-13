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

import org.junit.Test;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class CertificateCollectingTrustManagerTest {

    private final X509Certificate[] chain = new X509Certificate[0];

    private final X509TrustManager delegate = mock(X509TrustManager.class, RETURNS_SMART_NULLS);

    private final CertificateCollectingTrustManager trustManager = new CertificateCollectingTrustManager(this.delegate);

    @Test(expected = IllegalStateException.class)
    public void checkClientTrustedAlreadyCollected() {
        this.trustManager.checkClientTrusted(this.chain, null);
        this.trustManager.checkClientTrusted(this.chain, null);
    }

    @Test
    public void checkClientTrustedNotTrusted() throws CertificateException {
        doThrow(new CertificateException()).when(this.delegate).checkClientTrusted(this.chain, null);

        this.trustManager.checkClientTrusted(this.chain, null);

        assertThat(this.trustManager.isTrusted()).isFalse();
    }

    @Test
    public void checkClientTrustedTrusted() {
        this.trustManager.checkClientTrusted(this.chain, null);

        assertThat(this.trustManager.isTrusted()).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void checkServerTrustedAlreadyCollected() {
        this.trustManager.checkServerTrusted(this.chain, null);
        this.trustManager.checkServerTrusted(this.chain, null);
    }

    @Test
    public void checkServerTrustedNotTrusted() throws CertificateException {
        doThrow(new CertificateException()).when(this.delegate).checkServerTrusted(this.chain, null);

        this.trustManager.checkServerTrusted(this.chain, null);

        assertThat(this.trustManager.isTrusted()).isFalse();
    }

    @Test
    public void checkServerTrustedTrusted() {
        this.trustManager.checkServerTrusted(this.chain, null);

        assertThat(this.trustManager.isTrusted()).isTrue();
    }

    @Test
    public void getAcceptedIssuers() {
        this.trustManager.getAcceptedIssuers();

        verify(this.delegate).getAcceptedIssuers();
    }

    @Test
    public void getCollectedCertificateChain() {
        this.trustManager.checkServerTrusted(this.chain, null);

        X509Certificate[] collectedCertificateChain = this.trustManager.getCollectedCertificateChain();
        assertThat(collectedCertificateChain).isNotNull();
        assertThat(collectedCertificateChain).hasSize(0);
        assertThat(collectedCertificateChain).isNotSameAs(this.chain);
    }

    @Test
    public void getCollectedCertificateChainNotCollected() {
        assertThat(this.trustManager.getCollectedCertificateChain()).isNull();
    }

    @Test
    public void isTrusted() {
        assertThat(this.trustManager.isTrusted()).isFalse();
    }
}
