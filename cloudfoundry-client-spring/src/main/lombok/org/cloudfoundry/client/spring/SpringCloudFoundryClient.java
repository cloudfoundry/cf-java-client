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

package org.cloudfoundry.client.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.loggregator.LoggregatorMessageHttpMessageConverter;
import org.cloudfoundry.client.spring.util.CertificateCollectingSslCertificateTruster;
import org.cloudfoundry.client.spring.util.FallbackHttpMessageConverter;
import org.cloudfoundry.client.spring.util.LoggingClientHttpRequestInterceptor;
import org.cloudfoundry.client.spring.util.SslCertificateTruster;
import org.cloudfoundry.client.spring.v2.applications.SpringApplicationsV2;
import org.cloudfoundry.client.spring.v2.domains.SpringDomains;
import org.cloudfoundry.client.spring.v2.events.SpringEvents;
import org.cloudfoundry.client.spring.v2.featureflags.SpringFeatureFlags;
import org.cloudfoundry.client.spring.v2.info.SpringInfo;
import org.cloudfoundry.client.spring.v2.jobs.SpringJobs;
import org.cloudfoundry.client.spring.v2.organizations.SpringOrganizations;
import org.cloudfoundry.client.spring.v2.privatedomains.SpringPrivateDomains;
import org.cloudfoundry.client.spring.v2.quotadefinitions.SpringOrganizationQuotaDefinitions;
import org.cloudfoundry.client.spring.v2.routes.SpringRoutes;
import org.cloudfoundry.client.spring.v2.servicebindings.SpringServiceBindings;
import org.cloudfoundry.client.spring.v2.servicebrokers.SpringServiceBrokers;
import org.cloudfoundry.client.spring.v2.serviceinstances.SpringServiceInstances;
import org.cloudfoundry.client.spring.v2.servicekeys.SpringServiceKeys;
import org.cloudfoundry.client.spring.v2.serviceplans.SpringServicePlans;
import org.cloudfoundry.client.spring.v2.serviceplanvisibilities.SpringServicePlanVisibilities;
import org.cloudfoundry.client.spring.v2.services.SpringServices;
import org.cloudfoundry.client.spring.v2.shareddomains.SpringSharedDomains;
import org.cloudfoundry.client.spring.v2.spacequotadefinitions.SpringSpaceQuotaDefinitions;
import org.cloudfoundry.client.spring.v2.spaces.SpringSpaces;
import org.cloudfoundry.client.spring.v2.stacks.SpringStacks;
import org.cloudfoundry.client.spring.v2.users.SpringUsers;
import org.cloudfoundry.client.spring.v3.applications.SpringApplicationsV3;
import org.cloudfoundry.client.spring.v3.droplets.SpringDroplets;
import org.cloudfoundry.client.spring.v3.packages.SpringPackages;
import org.cloudfoundry.client.spring.v3.processes.SpringProcesses;
import org.cloudfoundry.client.spring.v3.tasks.SpringTasks;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.domains.Domains;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.featureflags.FeatureFlags;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.job.Jobs;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindings;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokers;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeys;
import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilities;
import org.cloudfoundry.client.v2.services.Services;
import org.cloudfoundry.client.v2.shareddomains.SharedDomains;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitions;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v2.stacks.Stacks;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.processes.Processes;
import org.cloudfoundry.client.v3.tasks.Tasks;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.util.PlatformDependent;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * The Spring-based implementation of {@link CloudFoundryClient}
 */
@ToString
public final class SpringCloudFoundryClient implements CloudFoundryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCloudFoundryClient.class);

    private final ApplicationsV2 applicationsV2;

    private final ApplicationsV3 applicationsV3;

    private final Domains domains;

    private final Droplets droplets;

    private final Events events;

    private final FeatureFlags featureFlags;

    private final Info info;

    private final Jobs jobs;

    private final OrganizationQuotaDefinitions organizationQuotaDefinitions;

    private final Organizations organizations;

    private final Packages packages;

    private final PrivateDomains privateDomains;

    private final Processes processes;

    private final OAuth2RestOperations restOperations;

    private final URI root;

    private final Routes routes;

    private final ServiceBindings serviceBindings;

    private final ServiceBrokers serviceBrokers;

    private final ServiceInstances serviceInstances;

    private final ServiceKeys serviceKeys;

    private final ServicePlanVisibilities servicePlanVisibilities;

    private final ServicePlans servicePlans;

    private final Services services;

    private final SharedDomains sharedDomains;

    private final SpaceQuotaDefinitions spaceQuotaDefinitions;

    private final Spaces spaces;

    private final Stacks stacks;

    private final Tasks tasks;

    private final String username;

    private final Users users;

    @Builder
    SpringCloudFoundryClient(@NonNull String host,
                             Boolean skipSslValidation,
                             String clientId,
                             String clientSecret,
                             @NonNull String username,
                             @NonNull String password,
                             @Singular List<DeserializationProblemHandler> deserializationProblemHandlers) {
        this(host, skipSslValidation, clientId, clientSecret, username, password, new RestTemplate(), new CertificateCollectingSslCertificateTruster(), deserializationProblemHandlers);
    }

    SpringCloudFoundryClient(String host,
                             Boolean skipSslValidation,
                             String clientId,
                             String clientSecret,
                             String username,
                             String password,
                             RestOperations bootstrapRestOperations,
                             SslCertificateTruster sslCertificateTruster,
                             List<DeserializationProblemHandler> deserializationProblemHandlers) {

        LOGGER.debug("Cloud Foundry Connection: {}, skipSslValidation={}", host, skipSslValidation);
        LOGGER.debug("Cloud Foundry Credentials: {} / {}", username, password);
        LOGGER.debug("OAuth2 Credentials: {} / {}", clientId, clientSecret);

        trustHost(host, skipSslValidation, sslCertificateTruster);

        this.root = getRoot(host);

        this.restOperations = createRestOperations(clientId, clientSecret, host, username, password, bootstrapRestOperations, deserializationProblemHandlers, skipSslValidation, sslCertificateTruster);
        SchedulerGroup schedulerGroup = createSchedulerGroup();

        this.applicationsV2 = new SpringApplicationsV2(this.restOperations, this.root, schedulerGroup);
        this.applicationsV3 = new SpringApplicationsV3(this.restOperations, this.root, schedulerGroup);
        this.domains = new SpringDomains(this.restOperations, this.root, schedulerGroup);
        this.droplets = new SpringDroplets(this.restOperations, this.root, schedulerGroup);
        this.events = new SpringEvents(this.restOperations, this.root, schedulerGroup);
        this.featureFlags = new SpringFeatureFlags(this.restOperations, this.root, schedulerGroup);
        this.info = new SpringInfo(this.restOperations, this.root, schedulerGroup);
        this.jobs = new SpringJobs(this.restOperations, this.root, schedulerGroup);
        this.organizations = new SpringOrganizations(this.restOperations, this.root, schedulerGroup);
        this.organizationQuotaDefinitions = new SpringOrganizationQuotaDefinitions(this.restOperations, this.root, schedulerGroup);
        this.packages = new SpringPackages(this.restOperations, this.root, schedulerGroup);
        this.privateDomains = new SpringPrivateDomains(this.restOperations, this.root, schedulerGroup);
        this.processes = new SpringProcesses(this.restOperations, this.root, schedulerGroup);
        this.routes = new SpringRoutes(this.restOperations, this.root, schedulerGroup);
        this.sharedDomains = new SpringSharedDomains(this.restOperations, this.root, schedulerGroup);
        this.serviceBindings = new SpringServiceBindings(this.restOperations, this.root, schedulerGroup);
        this.serviceBrokers = new SpringServiceBrokers(this.restOperations, this.root, schedulerGroup);
        this.serviceInstances = new SpringServiceInstances(this.restOperations, this.root, schedulerGroup);
        this.serviceKeys = new SpringServiceKeys(this.restOperations, this.root, schedulerGroup);
        this.servicePlanVisibilities = new SpringServicePlanVisibilities(this.restOperations, this.root, schedulerGroup);
        this.servicePlans = new SpringServicePlans(this.restOperations, this.root, schedulerGroup);
        this.services = new SpringServices(this.restOperations, this.root, schedulerGroup);
        this.spaceQuotaDefinitions = new SpringSpaceQuotaDefinitions(this.restOperations, this.root, schedulerGroup);
        this.spaces = new SpringSpaces(this.restOperations, this.root, schedulerGroup);
        this.stacks = new SpringStacks(this.restOperations, this.root, schedulerGroup);
        this.tasks = new SpringTasks(this.restOperations, this.root, schedulerGroup);
        this.users = new SpringUsers(this.restOperations, this.root, schedulerGroup);
        this.username = username;
    }

    SpringCloudFoundryClient(OAuth2RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        this.restOperations = restOperations;
        this.root = root;

        this.applicationsV2 = new SpringApplicationsV2(this.restOperations, this.root, schedulerGroup);
        this.applicationsV3 = new SpringApplicationsV3(this.restOperations, this.root, schedulerGroup);
        this.domains = new SpringDomains(this.restOperations, this.root, schedulerGroup);
        this.droplets = new SpringDroplets(this.restOperations, this.root, schedulerGroup);
        this.events = new SpringEvents(this.restOperations, this.root, schedulerGroup);
        this.featureFlags = new SpringFeatureFlags(this.restOperations, this.root, schedulerGroup);
        this.info = new SpringInfo(this.restOperations, this.root, schedulerGroup);
        this.jobs = new SpringJobs(this.restOperations, this.root, schedulerGroup);
        this.organizations = new SpringOrganizations(this.restOperations, this.root, schedulerGroup);
        this.organizationQuotaDefinitions = new SpringOrganizationQuotaDefinitions(this.restOperations, this.root, schedulerGroup);
        this.packages = new SpringPackages(this.restOperations, this.root, schedulerGroup);
        this.privateDomains = new SpringPrivateDomains(this.restOperations, this.root, schedulerGroup);
        this.processes = new SpringProcesses(this.restOperations, this.root, schedulerGroup);
        this.routes = new SpringRoutes(this.restOperations, this.root, schedulerGroup);
        this.sharedDomains = new SpringSharedDomains(this.restOperations, this.root, schedulerGroup);
        this.serviceBindings = new SpringServiceBindings(this.restOperations, this.root, schedulerGroup);
        this.serviceBrokers = new SpringServiceBrokers(this.restOperations, this.root, schedulerGroup);
        this.serviceInstances = new SpringServiceInstances(this.restOperations, this.root, schedulerGroup);
        this.serviceKeys = new SpringServiceKeys(this.restOperations, this.root, schedulerGroup);
        this.servicePlanVisibilities = new SpringServicePlanVisibilities(this.restOperations, this.root, schedulerGroup);
        this.servicePlans = new SpringServicePlans(this.restOperations, this.root, schedulerGroup);
        this.services = new SpringServices(this.restOperations, this.root, schedulerGroup);
        this.spaceQuotaDefinitions = new SpringSpaceQuotaDefinitions(this.restOperations, this.root, schedulerGroup);
        this.spaces = new SpringSpaces(this.restOperations, this.root, schedulerGroup);
        this.stacks = new SpringStacks(this.restOperations, this.root, schedulerGroup);
        this.tasks = new SpringTasks(this.restOperations, this.root, schedulerGroup);
        this.users = new SpringUsers(this.restOperations, this.root, schedulerGroup);
        this.username = null;
    }

    @Override
    public ApplicationsV2 applicationsV2() {
        return this.applicationsV2;
    }

    @Override
    public ApplicationsV3 applicationsV3() {
        return this.applicationsV3;
    }

    @Override
    public Domains domains() {
        return this.domains;
    }

    @Override
    public Droplets droplets() {
        return this.droplets;
    }

    @Override
    public Events events() {
        return this.events;
    }

    @Override
    public FeatureFlags featureFlags() {
        return this.featureFlags;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Info info() {
        return this.info;
    }

    @Override
    public Jobs jobs() {
        return this.jobs;
    }

    @Override
    public OrganizationQuotaDefinitions organizationQuotaDefinitions() {
        return this.organizationQuotaDefinitions;
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
    public PrivateDomains privateDomains() {
        return this.privateDomains;
    }

    @Override
    public Processes processes() {
        return processes;
    }

    @Override
    public Routes routes() {
        return this.routes;
    }

    @Override
    public ServiceBindings serviceBindings() {
        return this.serviceBindings;
    }

    @Override
    public ServiceBrokers serviceBrokers() {
        return this.serviceBrokers;
    }

    @Override
    public ServiceInstances serviceInstances() {
        return this.serviceInstances;
    }

    @Override
    public ServiceKeys serviceKeys() {
        return this.serviceKeys;
    }

    @Override
    public ServicePlanVisibilities servicePlanVisibilities() {
        return this.servicePlanVisibilities;
    }

    @Override
    public ServicePlans servicePlans() {
        return this.servicePlans;
    }

    @Override
    public Services services() {
        return this.services;
    }

    @Override
    public SharedDomains sharedDomains() {
        return this.sharedDomains;
    }

    @Override
    public SpaceQuotaDefinitions spaceQuotaDefinitions() {
        return this.spaceQuotaDefinitions;
    }

    @Override
    public Spaces spaces() {
        return this.spaces;
    }

    @Override
    public Stacks stacks() {
        return this.stacks;
    }

    @Override
    public Tasks tasks() {
        return tasks;
    }

    @Override
    public Users users() {
        return this.users;
    }

    String getAccessToken() {
        return this.restOperations.getAccessToken().getValue();
    }

    OAuth2RestOperations getRestOperations() {
        return this.restOperations;
    }

    URI getRoot() {
        return this.root;
    }

    private static OAuth2RestOperations createRestOperations(String clientId, String clientSecret, String host, String username, String password, RestOperations bootstrapRestOperations,
                                                             List<DeserializationProblemHandler> deserializationProblemHandlers, Boolean skipSslValidation,
                                                             SslCertificateTruster sslCertificateTruster) {
        OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails = getOAuth2ProtectedResourceDetails(clientId, clientSecret, host, username, password, bootstrapRestOperations,
            skipSslValidation, sslCertificateTruster);

        OAuth2ClientContext oAuth2ClientContext = getOAuth2ClientContext();

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(oAuth2ProtectedResourceDetails, oAuth2ClientContext);

        restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());

        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();

        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                LOGGER.debug("Modifying ObjectMapper configuration");

                ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) messageConverter).getObjectMapper()
                    .setSerializationInclusion(NON_NULL);

                for (DeserializationProblemHandler deserializationProblemHandler : deserializationProblemHandlers) {
                    objectMapper.addHandler(deserializationProblemHandler);
                }
            }
        }

        messageConverters.add(new LoggregatorMessageHttpMessageConverter());
        messageConverters.add(new FallbackHttpMessageConverter());

        return restTemplate;
    }

    private static SchedulerGroup createSchedulerGroup() {
        return SchedulerGroup.io("cloud-foundry", PlatformDependent.MEDIUM_BUFFER_SIZE, SchedulerGroup.DEFAULT_POOL_SIZE, false);
    }

    @SuppressWarnings("unchecked")
    private static String getAccessTokenUri(String host, RestOperations bootstrapRestOperations, Boolean skipSslValidation, SslCertificateTruster sslCertificateTruster) {
        String infoUri = UriComponentsBuilder.newInstance()
            .scheme("https").host(host).pathSegment("info")
            .build().toUriString();

        Map<String, String> results = bootstrapRestOperations.getForObject(infoUri, Map.class);

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(results.get("token_endpoint"))
            .pathSegment("oauth", "token")
            .build();

        trustHost(uriComponents.getHost(), skipSslValidation, sslCertificateTruster);

        return uriComponents.toUriString();
    }

    private static OAuth2ClientContext getOAuth2ClientContext() {
        return new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest());
    }

    private static OAuth2ProtectedResourceDetails getOAuth2ProtectedResourceDetails(String clientId, String clientSecret, String host, String username, String password,
                                                                                    RestOperations bootstrapRestOperations, Boolean skipSslValidation, SslCertificateTruster sslCertificateTruster) {
        ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
        details.setClientId(clientId != null ? clientId : "cf");
        details.setClientSecret(clientSecret != null ? clientSecret : "");
        details.setAccessTokenUri(getAccessTokenUri(host, bootstrapRestOperations, skipSslValidation, sslCertificateTruster));
        details.setUsername(username);
        details.setPassword(password);

        return details;
    }

    private static URI getRoot(String host) {
        return UriComponentsBuilder.newInstance().scheme("https").host(host).build().toUri();
    }

    private static void trustHost(String host, Boolean skipSslValidation, SslCertificateTruster sslCertificateTruster) {
        if (skipSslValidation != null && skipSslValidation) {
            try {
                sslCertificateTruster.trust(host, 443, 5, SECONDS);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
