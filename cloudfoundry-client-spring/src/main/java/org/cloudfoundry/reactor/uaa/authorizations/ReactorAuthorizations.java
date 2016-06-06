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

package org.cloudfoundry.reactor.uaa.authorizations;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.io.netty.config.HttpClientOptions;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpException;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * The Reactor-based implementation of {@link Authorizations}
 */
public final class ReactorAuthorizations extends AbstractUaaOperations implements Authorizations {

    private static final AsciiString LOCATION = new AsciiString("Location");

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorAuthorizations(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, getHttpClient(), objectMapper, root);
    }

    @Override
    public Mono<String> authorizeByAuthorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize"))
            .cast(String.class)
            .otherwise(HttpException.class, t -> {
                Field field = ReflectionUtils.findField(t.getClass(), "location");
                ReflectionUtils.makeAccessible(field);
                return Mono.just((String) ReflectionUtils.getField(field, t));
            })
            .map(location -> UriComponentsBuilder.fromUriString(location).build().getQueryParams().getFirst("code"));
    }

    private static HttpClient getHttpClient() {  // TODO: I'm going to hell for this
        return HttpClient.create(HttpClientOptions.create()
            .sslSupport()
            .sslConfigurer(ssl -> ssl.trustManager(new SimpleTrustManagerFactory() {

                @Override
                protected TrustManager[] engineGetTrustManagers() {
                    return new TrustManager[]{new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }};
                }

                @Override
                protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {

                }

                @Override
                protected void engineInit(KeyStore keyStore) throws Exception {

                }
            })));
    }

}
