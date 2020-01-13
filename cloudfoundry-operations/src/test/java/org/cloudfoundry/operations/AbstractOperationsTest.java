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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.buildpacks.Buildpacks;
import org.cloudfoundry.client.v2.domains.Domains;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.featureflags.FeatureFlags;
import org.cloudfoundry.client.v2.jobs.Jobs;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.client.v2.resourcematch.ResourceMatch;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
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
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstances;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.tasks.Tasks;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.routing.RoutingClient;
import org.cloudfoundry.routing.v1.routergroups.RouterGroups;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.junit.Before;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractOperationsTest {

    protected static final Mono<String> MISSING_ID = Mono.error(new java.lang.IllegalStateException("MISSING_ID"));

    protected static final Mono<String> MISSING_ORGANIZATION_ID = Mono.error(new java.lang.IllegalStateException("MISSING_ORGANIZATION_ID"));

    protected static final Mono<String> MISSING_SPACE_ID = Mono.error(new java.lang.IllegalStateException("MISSING_SPACE_ID"));

    protected static final Mono<String> MISSING_USERNAME = Mono.error(new java.lang.IllegalStateException("MISSING_USERNAME"));

    protected static final String TEST_ORGANIZATION_ID = "test-organization-id";

    protected static final String TEST_ORGANIZATION_NAME = "test-organization-name";

    protected static final String TEST_SPACE_ID = "test-space-id";

    protected static final String TEST_SPACE_NAME = "test-space-name";

    protected static final String TEST_USERNAME = "test-username";

    protected final ApplicationsV2 applications = mock(ApplicationsV2.class, RETURNS_SMART_NULLS);

    protected final ApplicationsV3 applicationsV3 = mock(ApplicationsV3.class, RETURNS_SMART_NULLS);

    protected final Authorizations authorizations = mock(Authorizations.class, RETURNS_SMART_NULLS);

    protected final Buildpacks buildpacks = mock(Buildpacks.class, RETURNS_SMART_NULLS);

    protected final CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class, RETURNS_SMART_NULLS);

    protected final Domains domains = mock(Domains.class, RETURNS_SMART_NULLS);

    protected final DopplerClient dopplerClient = mock(DopplerClient.class, RETURNS_SMART_NULLS);

    protected final Events events = mock(Events.class, RETURNS_SMART_NULLS);

    protected final FeatureFlags featureFlags = mock(FeatureFlags.class, RETURNS_SMART_NULLS);

    protected final Jobs jobs = mock(Jobs.class, RETURNS_SMART_NULLS);

    protected final OrganizationQuotaDefinitions organizationQuotaDefinitions = mock(OrganizationQuotaDefinitions.class, RETURNS_SMART_NULLS);

    protected final Organizations organizations = mock(Organizations.class, RETURNS_SMART_NULLS);

    protected final PrivateDomains privateDomains = mock(PrivateDomains.class, RETURNS_SMART_NULLS);

    protected final ResourceMatch resourceMatch = mock(ResourceMatch.class, RETURNS_SMART_NULLS);

    protected final RouterGroups routerGroups = mock(RouterGroups.class, RETURNS_SMART_NULLS);

    protected final Routes routes = mock(Routes.class, RETURNS_SMART_NULLS);

    protected final RoutingClient routingClient = mock(RoutingClient.class, RETURNS_SMART_NULLS);

    protected final ServiceBindingsV2 serviceBindingsV2 = mock(ServiceBindingsV2.class, RETURNS_SMART_NULLS);

    protected final ServiceBrokers serviceBrokers = mock(ServiceBrokers.class, RETURNS_SMART_NULLS);

    protected final ServiceInstances serviceInstances = mock(ServiceInstances.class, RETURNS_SMART_NULLS);

    protected final ServiceKeys serviceKeys = mock(ServiceKeys.class, RETURNS_SMART_NULLS);

    protected final ServicePlanVisibilities servicePlanVisibilities = mock(ServicePlanVisibilities.class, RETURNS_SMART_NULLS);

    protected final ServicePlans servicePlans = mock(ServicePlans.class, RETURNS_SMART_NULLS);

    protected final Services services = mock(Services.class, RETURNS_SMART_NULLS);

    protected final SharedDomains sharedDomains = mock(SharedDomains.class, RETURNS_SMART_NULLS);

    protected final SpaceQuotaDefinitions spaceQuotaDefinitions = mock(SpaceQuotaDefinitions.class, RETURNS_SMART_NULLS);

    protected final Spaces spaces = mock(Spaces.class, RETURNS_SMART_NULLS);

    protected final Stacks stacks = mock(Stacks.class, RETURNS_SMART_NULLS);

    protected final Tasks tasks = mock(Tasks.class, RETURNS_SMART_NULLS);

    protected final Tokens tokens = mock(Tokens.class, RETURNS_SMART_NULLS);

    protected final UaaClient uaaClient = mock(UaaClient.class, RETURNS_SMART_NULLS);

    protected final org.cloudfoundry.uaa.users.Users uaaUsers = mock(org.cloudfoundry.uaa.users.Users.class, RETURNS_SMART_NULLS);

    protected final UserProvidedServiceInstances userProvidedServiceInstances = mock(UserProvidedServiceInstances.class, RETURNS_SMART_NULLS);

    protected final Users users = mock(Users.class, RETURNS_SMART_NULLS);

    @Before
    public final void mockClient() {
        when(this.cloudFoundryClient.applicationsV2()).thenReturn(this.applications);
        when(this.cloudFoundryClient.applicationsV3()).thenReturn(this.applicationsV3);
        when(this.cloudFoundryClient.buildpacks()).thenReturn(this.buildpacks);
        when(this.cloudFoundryClient.domains()).thenReturn(this.domains);
        when(this.cloudFoundryClient.events()).thenReturn(this.events);
        when(this.cloudFoundryClient.featureFlags()).thenReturn(this.featureFlags);
        when(this.cloudFoundryClient.jobs()).thenReturn(this.jobs);
        when(this.cloudFoundryClient.organizations()).thenReturn(this.organizations);
        when(this.cloudFoundryClient.organizationQuotaDefinitions()).thenReturn(this.organizationQuotaDefinitions);
        when(this.cloudFoundryClient.privateDomains()).thenReturn(this.privateDomains);
        when(this.cloudFoundryClient.resourceMatch()).thenReturn(this.resourceMatch);
        when(this.cloudFoundryClient.routes()).thenReturn(this.routes);
        when(this.cloudFoundryClient.serviceBindingsV2()).thenReturn(this.serviceBindingsV2);
        when(this.cloudFoundryClient.serviceBrokers()).thenReturn(this.serviceBrokers);
        when(this.cloudFoundryClient.serviceInstances()).thenReturn(this.serviceInstances);
        when(this.cloudFoundryClient.serviceKeys()).thenReturn(this.serviceKeys);
        when(this.cloudFoundryClient.servicePlans()).thenReturn(this.servicePlans);
        when(this.cloudFoundryClient.servicePlanVisibilities()).thenReturn(this.servicePlanVisibilities);
        when(this.cloudFoundryClient.services()).thenReturn(this.services);
        when(this.cloudFoundryClient.sharedDomains()).thenReturn(this.sharedDomains);
        when(this.cloudFoundryClient.spaceQuotaDefinitions()).thenReturn(this.spaceQuotaDefinitions);
        when(this.cloudFoundryClient.spaces()).thenReturn(this.spaces);
        when(this.cloudFoundryClient.stacks()).thenReturn(this.stacks);
        when(this.cloudFoundryClient.tasks()).thenReturn(this.tasks);
        when(this.cloudFoundryClient.userProvidedServiceInstances()).thenReturn(this.userProvidedServiceInstances);
        when(this.cloudFoundryClient.users()).thenReturn(this.users);

        when(this.routingClient.routerGroups()).thenReturn(this.routerGroups);

        when(this.uaaClient.authorizations()).thenReturn(this.authorizations);
        when(this.uaaClient.tokens()).thenReturn(this.tokens);
        when(this.uaaClient.users()).thenReturn(this.uaaUsers);
    }

}
