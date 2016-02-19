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

package org.cloudfoundry.spring.client;

import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.CloudFoundryClient;
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
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEvents;
import org.cloudfoundry.client.v2.shareddomains.SharedDomains;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitions;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v2.stacks.Stacks;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstances;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.processes.Processes;
import org.cloudfoundry.client.v3.tasks.Tasks;
import org.cloudfoundry.spring.client.v2.applications.SpringApplicationsV2;
import org.cloudfoundry.spring.client.v2.domains.SpringDomains;
import org.cloudfoundry.spring.client.v2.events.SpringEvents;
import org.cloudfoundry.spring.client.v2.featureflags.SpringFeatureFlags;
import org.cloudfoundry.spring.client.v2.info.SpringInfo;
import org.cloudfoundry.spring.client.v2.jobs.SpringJobs;
import org.cloudfoundry.spring.client.v2.organizations.SpringOrganizations;
import org.cloudfoundry.spring.client.v2.privatedomains.SpringPrivateDomains;
import org.cloudfoundry.spring.client.v2.quotadefinitions.SpringOrganizationQuotaDefinitions;
import org.cloudfoundry.spring.client.v2.routes.SpringRoutes;
import org.cloudfoundry.spring.client.v2.servicebindings.SpringServiceBindings;
import org.cloudfoundry.spring.client.v2.servicebrokers.SpringServiceBrokers;
import org.cloudfoundry.spring.client.v2.serviceinstances.SpringServiceInstances;
import org.cloudfoundry.spring.client.v2.servicekeys.SpringServiceKeys;
import org.cloudfoundry.spring.client.v2.serviceplans.SpringServicePlans;
import org.cloudfoundry.spring.client.v2.serviceplanvisibilities.SpringServicePlanVisibilities;
import org.cloudfoundry.spring.client.v2.services.SpringServices;
import org.cloudfoundry.spring.client.v2.serviceusageevents.SpringServiceUsageEvents;
import org.cloudfoundry.spring.client.v2.shareddomains.SpringSharedDomains;
import org.cloudfoundry.spring.client.v2.spacequotadefinitions.SpringSpaceQuotaDefinitions;
import org.cloudfoundry.spring.client.v2.spaces.SpringSpaces;
import org.cloudfoundry.spring.client.v2.stacks.SpringStacks;
import org.cloudfoundry.spring.client.v2.userprovidedserviceinstances.SpringUserProvidedServiceInstances;
import org.cloudfoundry.spring.client.v2.users.SpringUsers;
import org.cloudfoundry.spring.client.v3.applications.SpringApplicationsV3;
import org.cloudfoundry.spring.client.v3.droplets.SpringDroplets;
import org.cloudfoundry.spring.client.v3.packages.SpringPackages;
import org.cloudfoundry.spring.client.v3.processes.SpringProcesses;
import org.cloudfoundry.spring.client.v3.tasks.SpringTasks;
import org.cloudfoundry.spring.util.CloudFoundryClientCompatibilityChecker;
import org.cloudfoundry.spring.util.SchedulerGroupBuilder;
import org.cloudfoundry.spring.util.network.ConnectionContext;
import org.cloudfoundry.spring.util.network.ConnectionContextFactory;
import org.cloudfoundry.spring.util.network.FallbackHttpMessageConverter;
import org.cloudfoundry.spring.util.network.OAuth2RestOperationsOAuth2TokenProvider;
import org.cloudfoundry.spring.util.network.OAuth2RestTemplateBuilder;
import org.cloudfoundry.spring.util.network.OAuth2TokenProvider;
import org.cloudfoundry.spring.util.network.SslCertificateTruster;
import org.cloudfoundry.util.Optional;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * The Spring-based implementation of {@link CloudFoundryClient}
 */
@ToString
public final class SpringCloudFoundryClient implements CloudFoundryClient {

    private final ApplicationsV2 applicationsV2;

    private final ApplicationsV3 applicationsV3;

    private final ConnectionContext connectionContext;

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

    private final Routes routes;

    private final ServiceBindings serviceBindings;

    private final ServiceBrokers serviceBrokers;

    private final ServiceInstances serviceInstances;

    private final ServiceKeys serviceKeys;

    private final ServicePlanVisibilities servicePlanVisibilities;

    private final ServicePlans servicePlans;

    private final ServiceUsageEvents serviceUsageEvents;

    private final Services services;

    private final SharedDomains sharedDomains;

    private final SpaceQuotaDefinitions spaceQuotaDefinitions;

    private final Spaces spaces;

    private final Stacks stacks;

    private final Tasks tasks;

    private final OAuth2TokenProvider tokenProvider;

    private final UserProvidedServiceInstances userProvidedServiceInstances;

    private final Users users;

    @Builder
    SpringCloudFoundryClient(@NonNull String host,
                             Integer port,
                             Boolean skipSslValidation,
                             String clientId,
                             String clientSecret,
                             @NonNull String username,
                             @NonNull String password,
                             @Singular List<DeserializationProblemHandler> problemHandlers) {

        this(getConnectionContext(host, port, skipSslValidation, clientId, clientSecret, username, password), host, port, getSchedulerGroup(), problemHandlers);
        new CloudFoundryClientCompatibilityChecker(this.info).check();
    }

    SpringCloudFoundryClient(ConnectionContext connectionContext, RestOperations restOperations, URI root, SchedulerGroup schedulerGroup, OAuth2TokenProvider tokenProvider) {
        this.applicationsV2 = new SpringApplicationsV2(restOperations, root, schedulerGroup);
        this.applicationsV3 = new SpringApplicationsV3(restOperations, root, schedulerGroup);
        this.domains = new SpringDomains(restOperations, root, schedulerGroup);
        this.droplets = new SpringDroplets(restOperations, root, schedulerGroup);
        this.events = new SpringEvents(restOperations, root, schedulerGroup);
        this.featureFlags = new SpringFeatureFlags(restOperations, root, schedulerGroup);
        this.info = new SpringInfo(restOperations, root, schedulerGroup);
        this.jobs = new SpringJobs(restOperations, root, schedulerGroup);
        this.organizations = new SpringOrganizations(restOperations, root, schedulerGroup);
        this.organizationQuotaDefinitions = new SpringOrganizationQuotaDefinitions(restOperations, root, schedulerGroup);
        this.packages = new SpringPackages(restOperations, root, schedulerGroup);
        this.privateDomains = new SpringPrivateDomains(restOperations, root, schedulerGroup);
        this.processes = new SpringProcesses(restOperations, root, schedulerGroup);
        this.routes = new SpringRoutes(restOperations, root, schedulerGroup);
        this.sharedDomains = new SpringSharedDomains(restOperations, root, schedulerGroup);
        this.serviceBindings = new SpringServiceBindings(restOperations, root, schedulerGroup);
        this.serviceBrokers = new SpringServiceBrokers(restOperations, root, schedulerGroup);
        this.serviceInstances = new SpringServiceInstances(restOperations, root, schedulerGroup);
        this.serviceKeys = new SpringServiceKeys(restOperations, root, schedulerGroup);
        this.servicePlanVisibilities = new SpringServicePlanVisibilities(restOperations, root, schedulerGroup);
        this.servicePlans = new SpringServicePlans(restOperations, root, schedulerGroup);
        this.serviceUsageEvents = new SpringServiceUsageEvents(restOperations, root, schedulerGroup);
        this.services = new SpringServices(restOperations, root, schedulerGroup);
        this.spaceQuotaDefinitions = new SpringSpaceQuotaDefinitions(restOperations, root, schedulerGroup);
        this.spaces = new SpringSpaces(restOperations, root, schedulerGroup);
        this.stacks = new SpringStacks(restOperations, root, schedulerGroup);
        this.tasks = new SpringTasks(restOperations, root, schedulerGroup);
        this.userProvidedServiceInstances = new SpringUserProvidedServiceInstances(restOperations, root, schedulerGroup);
        this.users = new SpringUsers(restOperations, root, schedulerGroup);

        this.connectionContext = connectionContext.toBuilder()
            .cloudFoundryClient(this)
            .build();

        this.tokenProvider = tokenProvider;
    }

    // Let's take a moment to reflect on the fact that this bridge constructor is needed to counter a useless compiler constraint
    private SpringCloudFoundryClient(ConnectionContext connectionContext, String host, Integer port, SchedulerGroup schedulerGroup, List<DeserializationProblemHandler> problemHandlers) {
        this(connectionContext, getRestOperations(connectionContext, problemHandlers), getRoot(host, port, connectionContext.getSslCertificateTruster()), schedulerGroup);
    }

    // Let's take a moment to reflect on the fact that this bridge constructor is needed to counter a useless compiler constraint
    private SpringCloudFoundryClient(ConnectionContext connectionContext, OAuth2RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        this(connectionContext, restOperations, root, schedulerGroup, new OAuth2RestOperationsOAuth2TokenProvider(restOperations));
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
    public Mono<String> getAccessToken() {
        return this.tokenProvider.getToken();
    }

    /**
     * Returns the Spring-based connection context
     *
     * @return the Spring-based connection context
     */
    public ConnectionContext getConnectionContext() {
        return this.connectionContext;
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
    public ServiceUsageEvents serviceUsageEvents() {
        return this.serviceUsageEvents;
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
    public UserProvidedServiceInstances userProvidedServiceInstances() {
        return this.userProvidedServiceInstances;
    }

    @Override
    public Users users() {
        return this.users;
    }

    private static ConnectionContext getConnectionContext(String host, Integer port, Boolean skipSslValidation, String clientId, String clientSecret, String username, String password) {
        return new ConnectionContextFactory()
            .trustCertificates(skipSslValidation)
            .host(host)
            .port(port)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .username(username)
            .password(password)
            .build();
    }

    private static OAuth2RestOperations getRestOperations(ConnectionContext connectionContext, List<DeserializationProblemHandler> problemHandlers) {
        return new OAuth2RestTemplateBuilder()
            .clientContext(connectionContext.getClientContext())
            .protectedResourceDetails(connectionContext.getProtectedResourceDetails())
            .hostnameVerifier(connectionContext.getHostnameVerifier())
            .sslContext(connectionContext.getSslContext())
            .messageConverter(new FallbackHttpMessageConverter())
            .problemHandlers(problemHandlers)
            .build();
    }

    private static URI getRoot(String host, Integer port, SslCertificateTruster sslCertificateTruster) {
        URI uri = UriComponentsBuilder.newInstance()
            .scheme("https").host(host).port(Optional.ofNullable(port).orElse(443))
            .build().toUri();

        sslCertificateTruster.trust(uri.getHost(), uri.getPort(), 5, SECONDS);
        return uri;
    }

    private static SchedulerGroup getSchedulerGroup() {
        return new SchedulerGroupBuilder()
            .name("cloud-foundry")
            .autoShutdown(false)
            .build();
    }

}
