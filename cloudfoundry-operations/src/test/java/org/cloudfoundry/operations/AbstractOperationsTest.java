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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.domains.Domains;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.featureflags.FeatureFlags;
import org.cloudfoundry.client.v2.job.Jobs;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.shareddomains.SharedDomains;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitions;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v2.stacks.Stacks;
import org.cloudfoundry.client.v2.users.Users;
import org.junit.Before;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractOperationsTest {

    protected static final Mono<String> MISSING_ID = Mono.error(new java.lang.IllegalStateException());

    protected static final String TEST_ORGANIZATION_ID = "test-organization-id";

    protected static final String TEST_ORGANIZATION_NAME = "test-organization-name";

    protected static final String TEST_SPACE_ID = "test-space-id";

    protected static final String TEST_SPACE_NAME = "test-space-name";

    protected final ApplicationsV2 applications = mock(ApplicationsV2.class, RETURNS_SMART_NULLS);

    protected final CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class, RETURNS_SMART_NULLS);

    protected final Domains domains = mock(Domains.class, RETURNS_SMART_NULLS);

    protected final Events events = mock(Events.class, RETURNS_SMART_NULLS);
    
    protected final FeatureFlags featureFlags = mock(FeatureFlags.class, RETURNS_SMART_NULLS);

    protected final Jobs jobs = mock(Jobs.class, RETURNS_SMART_NULLS);

    protected final OrganizationQuotaDefinitions organizationQuotaDefinitions = mock(OrganizationQuotaDefinitions.class, RETURNS_SMART_NULLS);

    protected final Organizations organizations = mock(Organizations.class, RETURNS_SMART_NULLS);

    protected final PrivateDomains privateDomains = mock(PrivateDomains.class, RETURNS_SMART_NULLS);

    protected final Routes routes = mock(Routes.class, RETURNS_SMART_NULLS);

    protected final SharedDomains sharedDomains = mock(SharedDomains.class, RETURNS_SMART_NULLS);

    protected final SpaceQuotaDefinitions spaceQuotaDefinitions = mock(SpaceQuotaDefinitions.class, RETURNS_SMART_NULLS);

    protected final Spaces spaces = mock(Spaces.class, RETURNS_SMART_NULLS);

    protected final Stacks stacks = mock(Stacks.class, RETURNS_SMART_NULLS);

    protected final Users users = mock(Users.class, RETURNS_SMART_NULLS);

    @Before
    public final void mockClient() {
        when(this.cloudFoundryClient.applicationsV2()).thenReturn(this.applications);
        when(this.cloudFoundryClient.domains()).thenReturn(this.domains);
        when(this.cloudFoundryClient.events()).thenReturn(this.events);
        when(this.cloudFoundryClient.featureFlags()).thenReturn(this.featureFlags);
        when(this.cloudFoundryClient.jobs()).thenReturn(this.jobs);
        when(this.cloudFoundryClient.organizations()).thenReturn(this.organizations);
        when(this.cloudFoundryClient.organizationQuotaDefinitions()).thenReturn(this.organizationQuotaDefinitions);
        when(this.cloudFoundryClient.privateDomains()).thenReturn(this.privateDomains);
        when(this.cloudFoundryClient.routes()).thenReturn(this.routes);
        when(this.cloudFoundryClient.sharedDomains()).thenReturn(this.sharedDomains);
        when(this.cloudFoundryClient.spaceQuotaDefinitions()).thenReturn(this.spaceQuotaDefinitions);
        when(this.cloudFoundryClient.spaces()).thenReturn(this.spaces);
        when(this.cloudFoundryClient.stacks()).thenReturn(this.stacks);
        when(this.cloudFoundryClient.users()).thenReturn(this.users);
    }

}
