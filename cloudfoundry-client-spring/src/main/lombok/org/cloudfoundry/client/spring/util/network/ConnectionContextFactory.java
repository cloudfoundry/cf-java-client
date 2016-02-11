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

package org.cloudfoundry.client.spring.util.network;

import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.cloudfoundry.utils.Optional;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Accessors(chain = true, fluent = true)
@Setter
@ToString
public final class ConnectionContextFactory {

    private String clientId;

    private String clientSecret;

    private String host;

    private String password;

    private Integer port;

    /**
     * The bootstrap {@link RestOperations}.  This should not typically be used and is only exposed for testing.
     *
     * @param restOperations the bootstrap {@link RestOperations}
     */
    private RestOperations restOperations;

    private Boolean trustCertificates;

    private String username;

    public ConnectionContext build() {
        DynamicTrustManager dynamicTrustManager = getDynamicTrustManager(this.trustCertificates);
        SSLContext sslContext = getSslContext(dynamicTrustManager);
        RestOperations restOperations = Optional.ofNullable(this.restOperations).orElse(getRestOperations(dynamicTrustManager, sslContext));
        URI accessTokenUri = getAccessTokenUri(this.host, this.port, restOperations, dynamicTrustManager);

        return ConnectionContext.builder()
            .clientContext(getClientContext())
            .protectedResourceDetails(getProtectedResourceDetails(this.clientId, this.clientSecret, this.username, this.password, accessTokenUri))
            .sslCertificateTruster(dynamicTrustManager)
            .hostnameVerifier(dynamicTrustManager)
            .sslContext(sslContext)
            .build();
    }

    private static URI getAccessTokenUri(String host, Integer port, RestOperations restOperations, SslCertificateTruster sslCertificateTruster) {
        return new AccessTokenUriBuilder()
            .host(host)
            .port(port)
            .restOperations(restOperations)
            .sslCertificateTruster(sslCertificateTruster)
            .build();
    }

    private static OAuth2ClientContext getClientContext() {
        return new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest());
    }

    private static DynamicTrustManager getDynamicTrustManager(Boolean trustCertificates) {
        return new DynamicTrustManager(trustCertificates);
    }

    private static OAuth2ProtectedResourceDetails getProtectedResourceDetails(String clientId, String clientSecret, String username, String password, URI accessTokenUri) {
        return new ResourceOwnerPasswordResourceDetailsBuilder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .accessTokenUri(accessTokenUri)
            .username(username)
            .password(password)
            .build();
    }

    private static RestOperations getRestOperations(HostnameVerifier hostnameVerifier, SSLContext sslContext) {
        return new RestTemplate(new CustomSslSimpleClientHttpRequestFactory(hostnameVerifier, sslContext));
    }

    private static SSLContext getSslContext(TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return sslContext;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
