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

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

final class CustomSslSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    private final HostnameVerifier hostNameVerifier;

    private final SSLContext sslContext;

    public CustomSslSimpleClientHttpRequestFactory(HostnameVerifier hostnameVerifier, SSLContext sslContext) {
        this.hostNameVerifier = hostnameVerifier;
        this.sslContext = sslContext;
    }

    @Override
    protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
        HttpURLConnection connection = super.openConnection(url, proxy);

        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;

            httpsConnection.setSSLSocketFactory(this.sslContext.getSocketFactory());
            httpsConnection.setHostnameVerifier(this.hostNameVerifier);
        }

        return connection;
    }

}
