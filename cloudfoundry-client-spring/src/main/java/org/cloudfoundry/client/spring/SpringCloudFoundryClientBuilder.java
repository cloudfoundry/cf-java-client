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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.loggregator.LoggregatorMessageHttpMessageConverter;
import org.cloudfoundry.client.spring.util.CertificateCollectingSslCertificateTruster;
import org.cloudfoundry.client.spring.util.FallbackHttpMessageConverter;
import org.cloudfoundry.client.spring.util.LoggingDeserializationProblemHandler;
import org.cloudfoundry.client.spring.util.ResourceOwnerPasswordResourceDetailsBuilder;
import org.cloudfoundry.client.spring.util.SslCertificateTruster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A builder API for creating a Spring-backed implementation of the {@link CloudFoundryClient}.  By default it uses
 * {@code cf} and an empty string for the {@code clientId} and {@code clientSecret} respectively.
 *
 * <p><b>This class is NOT threadsafe.  The {@link CloudFoundryClient} created by it, is threadsafe.</b>
 */
public final class SpringCloudFoundryClientBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestTemplate restTemplate;

    private final SslCertificateTruster sslCertificateTruster;

    private volatile Optional<String> clientId = Optional.empty();

    private volatile Optional<String> clientSecret = Optional.empty();

    private volatile Optional<String> host = Optional.empty();

    private volatile Optional<String> username = Optional.empty();

    private volatile Optional<String> password = Optional.empty();

    private volatile Optional<Boolean> skipSslValidation = Optional.empty();

    /**
     * Creates a new instance of the builder
     */
    public SpringCloudFoundryClientBuilder() {
        this(new RestTemplate(), new CertificateCollectingSslCertificateTruster());
    }

    SpringCloudFoundryClientBuilder(RestTemplate restTemplate, SslCertificateTruster sslCertificateTruster) {
        this.restTemplate = restTemplate;
        this.sslCertificateTruster = sslCertificateTruster;
    }

    /**
     * Configure the API endpoint to connect to
     *
     * @param host The host to connect to.  This is typically something like {@code api.run.pivotal.io}.
     * @return {@code this}
     */
    public SpringCloudFoundryClientBuilder withApi(String host) {
        this.host = Optional.of(host);
        return this;
    }

    /**
     * Configure the OAuth2 client information to use when connecting
     *
     * @param clientId     the client id to use. Defaults to {@code cf}.
     * @param clientSecret the client secret to use.  Defaults to {@code ''}.
     * @return {@code this}
     */
    public SpringCloudFoundryClientBuilder withClient(String clientId, String clientSecret) {
        this.clientId = Optional.of(clientId);
        this.clientSecret = Optional.of(clientSecret);
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
        this.username = Optional.of(username);
        this.password = Optional.of(password);
        return this;
    }

    /**
     * Configure whether to skip SSL validation
     *
     * @param skipSslValidation whether to skip SSL validation
     * @return {@code this}
     */
    public SpringCloudFoundryClientBuilder withSkipSslValidation(Boolean skipSslValidation) {
        this.skipSslValidation = Optional.of(skipSslValidation);
        return this;
    }

    /**
     * Builds a new instance of a Spring-backed implementation of the {@link CloudFoundryClient} using the information
     * provided
     *
     * @return a new instance of a Spring-backed implementation of the {@link CloudFoundryClient}
     * @throws IllegalArgumentException if {@code host}, {@code username}, or {@code password} has not been set
     */
    public SpringCloudFoundryClient build() {
        String clientId = this.clientId.orElse("cf");
        String clientSecret = this.clientSecret.orElse("");
        String host = this.host
                .orElseThrow(() -> new IllegalArgumentException("host must be set"));
        String username = this.username
                .orElseThrow(() -> new IllegalArgumentException("username must be set"));
        String password = this.password
                .orElseThrow(() -> new IllegalArgumentException("password must be set"));
        Boolean skipSslValidation = this.skipSslValidation.orElse(false);

        if (skipSslValidation) {
            try {
                this.sslCertificateTruster.trust(host, 443, 5, SECONDS);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        URI root = UriComponentsBuilder.newInstance().scheme("https").host(host).build().toUri();
        return new SpringCloudFoundryClient(getRestOperations(clientId, clientSecret, host, username, password), root);
    }

    private OAuth2ClientContext getOAuth2ClientContext() {
        return new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest());
    }

    private OAuth2ProtectedResourceDetails getOAuth2ProtectedResourceDetails(String clientId, String clientSecret,
                                                                             String host, String username,
                                                                             String password) {
        return new ResourceOwnerPasswordResourceDetailsBuilder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withAccessTokenUri(getAccessTokenUri(host))
                .withUsername(username)
                .withPassword(password)
                .build();
    }

    private OAuth2RestOperations getRestOperations(String clientId, String clientSecret, String host, String username,
                                                   String password) {
        OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails = getOAuth2ProtectedResourceDetails(clientId,
                clientSecret, host, username, password);
        OAuth2ClientContext oAuth2ClientContext = getOAuth2ClientContext();

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(oAuth2ProtectedResourceDetails, oAuth2ClientContext);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();

        messageConverters.stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .map(converter -> (MappingJackson2HttpMessageConverter) converter)
                .findFirst()
                .ifPresent(converter -> {
                    this.logger.debug("Modifying ObjectMapper configuration");
                    converter.getObjectMapper()
                            .addHandler(new LoggingDeserializationProblemHandler())
                            .setSerializationInclusion(NON_NULL);
                });

        messageConverters.add(new LoggregatorMessageHttpMessageConverter());
        messageConverters.add(new FallbackHttpMessageConverter());

        return restTemplate;
    }

    @SuppressWarnings("unchecked")
    private String getAccessTokenUri(String host) {
        String infoUri = UriComponentsBuilder.newInstance()
                .scheme("https").host(host).pathSegment("info")
                .build().toUriString();

        Map<String, String> results = this.restTemplate.getForObject(infoUri, Map.class);

        return UriComponentsBuilder.fromUriString(results.get("token_endpoint"))
                .pathSegment("oauth", "token")
                .build().toUriString();
    }

}
