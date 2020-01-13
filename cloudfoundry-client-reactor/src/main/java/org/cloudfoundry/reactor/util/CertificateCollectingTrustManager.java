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

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

final class CertificateCollectingTrustManager implements X509TrustManager {

    private final X509TrustManager delegate;

    private final Object monitor = new Object();

    private X509Certificate[] collected;

    private Boolean trusted = Boolean.FALSE;

    CertificateCollectingTrustManager(X509TrustManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        synchronized (this.monitor) {
            if (this.collected != null) {
                throw new IllegalStateException("A certificate chain has already been collected.");
            }

            this.collected = chain;

            try {
                this.delegate.checkClientTrusted(chain, authType);
                this.trusted = Boolean.TRUE;
            } catch (CertificateException e) {
                this.trusted = Boolean.FALSE;
            }
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        synchronized (this.monitor) {
            if (this.collected != null) {
                throw new IllegalStateException("A certificate chain has already been collected.");
            }

            this.collected = chain;

            try {
                this.delegate.checkServerTrusted(chain, authType);
                this.trusted = Boolean.TRUE;
            } catch (CertificateException e) {
                this.trusted = Boolean.FALSE;
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.delegate.getAcceptedIssuers();
    }

    X509Certificate[] getCollectedCertificateChain() {
        synchronized (this.monitor) {
            if (this.collected == null) {
                return null;

            }
            return Arrays.copyOf(this.collected, this.collected.length);
        }
    }

    Boolean isTrusted() {
        synchronized (this.monitor) {
            return this.trusted;
        }
    }

}
