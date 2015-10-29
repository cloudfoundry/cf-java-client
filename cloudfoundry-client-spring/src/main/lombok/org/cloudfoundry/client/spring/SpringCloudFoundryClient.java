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

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.loggregator.LoggregatorMessageHttpMessageConverter;
import org.cloudfoundry.client.spring.util.CertificateCollectingSslCertificateTruster;
import org.cloudfoundry.client.spring.util.FallbackHttpMessageConverter;
import org.cloudfoundry.client.spring.util.LoggingDeserializationProblemHandler;
import org.cloudfoundry.client.spring.util.SslCertificateTruster;
import org.cloudfoundry.client.spring.v2.events.SpringEvents;
import org.cloudfoundry.client.spring.v2.info.SpringInfo;
import org.cloudfoundry.client.spring.v2.organizations.SpringOrganizations;
import org.cloudfoundry.client.spring.v2.service_instances.SpringServiceInstances;
import org.cloudfoundry.client.spring.v2.spaces.SpringSpaces;
import org.cloudfoundry.client.spring.v3.applications.SpringApplications;
import org.cloudfoundry.client.spring.v3.droplets.SpringDroplets;
import org.cloudfoundry.client.spring.v3.packages.SpringPackages;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v3.applications.Applications;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.packages.Packages;
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
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.web.client.RestOperations;
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
 * The Spring-based implementation of {@link CloudFoundryClient}
 */
@ToString
public final class SpringCloudFoundryClient implements CloudFoundryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCloudFoundryClient.class);

    private final Applications applications;

    private final Droplets droplets;

    private final Events events;

    private final Info info;

    private final Organizations organizations;

    private final Packages packages;

    private final OAuth2RestOperations restOperations;

    private final ServiceInstances serviceInstances;

    private final Spaces spaces;

    @Builder
    SpringCloudFoundryClient(@NonNull String host, Boolean skipSslValidation, String clientId, String clientSecret,
                             @NonNull String username, @NonNull String password) {
        this(host, skipSslValidation, clientId, clientSecret, username, password, new RestTemplate(),
                new CertificateCollectingSslCertificateTruster());
    }

    SpringCloudFoundryClient(String host, Boolean skipSslValidation, String clientId, String clientSecret,
                             String username, String password, RestOperations bootstrapRestOperations,
                             SslCertificateTruster sslCertificateTruster) {

        if (!Optional.of(skipSslValidation).orElse(false)) {
            try {
                sslCertificateTruster.trust(host, 443, 5, SECONDS);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        OAuth2RestOperations restOperations = getRestOperations(clientId, clientSecret, host, username, password,
                bootstrapRestOperations);
        URI root = getRoot(host);

        this.restOperations = restOperations;

        this.applications = new SpringApplications(restOperations, root);
        this.droplets = new SpringDroplets(restOperations, root);
        this.events = new SpringEvents(restOperations, root);
        this.info = new SpringInfo(restOperations, root);
        this.organizations = new SpringOrganizations(restOperations, root);
        this.packages = new SpringPackages(restOperations, root);
        this.serviceInstances = new SpringServiceInstances(restOperations, root);
        this.spaces = new SpringSpaces(restOperations, root);
    }

    SpringCloudFoundryClient(OAuth2RestOperations restOperations, URI root) {
        this.restOperations = restOperations;

        this.applications = new SpringApplications(restOperations, root);
        this.droplets = new SpringDroplets(restOperations, root);
        this.events = new SpringEvents(restOperations, root);
        this.info = new SpringInfo(restOperations, root);
        this.organizations = new SpringOrganizations(restOperations, root);
        this.packages = new SpringPackages(restOperations, root);
        this.serviceInstances = new SpringServiceInstances(restOperations, root);
        this.spaces = new SpringSpaces(restOperations, root);
    }

    OAuth2RestOperations getRestOperations() {
        return this.restOperations;
    }

    @Override
    public Applications applications() {
        return this.applications;
    }

    @Override
    public Droplets droplets() {
        return this.droplets;
    }

    @Override
    public Events events() {
        return this.events;
    }

    String getAccessToken() {
        return this.restOperations.getAccessToken().getValue();
    }

    @Override
    public Info info() {
        return this.info;
    }

    @Override
    public Organizations organizations() {
        return this.organizations;
    }

    @Override
    public Packages packages() {
        return this.packages;
    }

    @Override
    public ServiceInstances serviceInstances() {
        return this.serviceInstances;
    }

    @Override
    public Spaces spaces() {
        return this.spaces;
    }

    @SuppressWarnings("unchecked")
    private static String getAccessTokenUri(String host, RestOperations bootstrapRestOperations) {
        String infoUri = UriComponentsBuilder.newInstance()
                .scheme("https").host(host).pathSegment("info")
                .build().toUriString();

        Map<String, String> results = bootstrapRestOperations.getForObject(infoUri, Map.class);

        return UriComponentsBuilder.fromUriString(results.get("token_endpoint"))
                .pathSegment("oauth", "token")
                .build().toUriString();
    }

    private static OAuth2ClientContext getOAuth2ClientContext() {
        return new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest());
    }

    private static OAuth2ProtectedResourceDetails getOAuth2ProtectedResourceDetails(
            String clientId, String clientSecret, String host, String username, String password,
            RestOperations bootstrapRestOperations) {

        ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
        details.setClientId(Optional.of(clientId).orElse("cf"));
        details.setClientSecret(Optional.of(clientSecret).orElse(""));
        details.setAccessTokenUri(getAccessTokenUri(host, bootstrapRestOperations));
        details.setUsername(username);
        details.setPassword(password);

        return details;
    }

    private static OAuth2RestOperations getRestOperations(String clientId, String clientSecret, String host,
                                                          String username, String password,
                                                          RestOperations bootstrapRestOperations) {

        OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails = getOAuth2ProtectedResourceDetails(clientId,
                clientSecret, host, username, password, bootstrapRestOperations);
        OAuth2ClientContext oAuth2ClientContext = getOAuth2ClientContext();

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(oAuth2ProtectedResourceDetails, oAuth2ClientContext);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();

        messageConverters.stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .map(converter -> (MappingJackson2HttpMessageConverter) converter)
                .findFirst()
                .ifPresent(converter -> {
                    LOGGER.debug("Modifying ObjectMapper configuration");
                    converter.getObjectMapper()
                            .addHandler(new LoggingDeserializationProblemHandler())
                            .setSerializationInclusion(NON_NULL);
                });

        messageConverters.add(new LoggregatorMessageHttpMessageConverter());
        messageConverters.add(new FallbackHttpMessageConverter());

        return restTemplate;
    }

    private static URI getRoot(String host) {
        return UriComponentsBuilder.newInstance().scheme("https").host(host).build().toUri();
    }

}
