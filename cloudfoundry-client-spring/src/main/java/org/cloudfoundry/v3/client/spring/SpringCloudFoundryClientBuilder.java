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

package org.cloudfoundry.v3.client.spring;

import org.cloudfoundry.v3.client.CloudFoundryClient;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * A builder API for creating a Spring-backed implementation of the {@link CloudFoundryClient}.
 * <p/>
 * <b>This class is NOT threadsafe.  The {@link CloudFoundryClient} created by it, is threadsafe.</b>
 */
public final class SpringCloudFoundryClientBuilder {

    private volatile String clientId = "cf";

    private volatile String clientSecret = "";

    private volatile String host;

    private volatile String username;

    private volatile String password;

    /**
     * Configure the API endpoint to connect to
     *
     * @param host The host to connect to.  This is typically something like {@code api.run.pivotal.io}.
     * @return {@code this}
     */
    public SpringCloudFoundryClientBuilder withApi(String host) {
        this.host = host;
        return this;
    }

    /**
     * Configure the credentials to use when connecting
     *
     * @param username the username to use
     * @param password the password to use
     * @return {@code this}
     */
    public SpringCloudFoundryClientBuilder withCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    /**
     * Builds a new instance of a Spring-backed implementation of the {@link CloudFoundryClient} using the information
     * provided
     *
     * @return a new instance of a Spring-backed implementation of the {@link CloudFoundryClient}
     * @throws IllegalArgumentException if {@code host}, {@code username}, or {@code password} has not been set
     */
    public CloudFoundryClient build() {
        Assert.notNull(this.host, "host must be set");
        Assert.hasText(this.username, "username must be set");
        Assert.hasText(this.password, "password must be set");

        OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails = getOAuth2ProtectedResourceDetails();
        OAuth2ClientContext oAuth2ClientContext = getOAuth2ClientContext();
        RestOperations restOperations = new OAuth2RestTemplate(oAuth2ProtectedResourceDetails, oAuth2ClientContext);

        return new SpringCloudFoundryClient(restOperations);
    }

    private OAuth2ClientContext getOAuth2ClientContext() {
        return new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest());
    }

    private OAuth2ProtectedResourceDetails getOAuth2ProtectedResourceDetails() {
        return new ResourceOwnerPasswordResourceDetailsBuilder()
                .withClientId(this.clientId)
                .withClientSecret(this.clientSecret)
                .withAccessTokenUri(getAccessTokenUri())
                .withCredentials(this.username, this.password)
                .build();
    }

    @SuppressWarnings("unchecked")
    private String getAccessTokenUri() {
        String infoUri = UriComponentsBuilder.newInstance()
                .scheme("https").host(this.host).pathSegment("info")
                .build().toUriString();

        Map<String, String> results = new RestTemplate().getForObject(infoUri, Map.class);

        return UriComponentsBuilder.fromUriString(results.get("token_endpoint"))
                .pathSegment("oauth", "token")
                .build().toUriString();
    }

}
