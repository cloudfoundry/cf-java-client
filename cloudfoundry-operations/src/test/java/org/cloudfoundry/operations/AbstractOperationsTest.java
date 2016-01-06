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
import org.cloudfoundry.client.v2.job.Jobs;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.shareddomains.SharedDomains;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitions;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.junit.Before;
import reactor.Mono;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractOperationsTest {

    protected static final Mono<String> MISSING_ID = Mono.error(new java.lang.IllegalStateException());

    protected static final String TEST_ORGANIZATION = "test-organization-id";

    protected static final String TEST_SPACE = "test-space-id";

    protected final ApplicationsV2 applications = mock(ApplicationsV2.class, RETURNS_SMART_NULLS);

    protected final CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class, RETURNS_SMART_NULLS);

    protected final Domains domains = mock(Domains.class, RETURNS_SMART_NULLS);

    protected final Jobs jobs = mock(Jobs.class, RETURNS_SMART_NULLS);

    protected final Organizations organizations = mock(Organizations.class, RETURNS_SMART_NULLS);

    protected final Routes routes = mock(Routes.class, RETURNS_SMART_NULLS);

    protected final SharedDomains sharedDomains = mock(SharedDomains.class, RETURNS_SMART_NULLS);

    protected final SpaceQuotaDefinitions spaceQuotaDefinitions = mock(SpaceQuotaDefinitions.class, RETURNS_SMART_NULLS);

    protected final Spaces spaces = mock(Spaces.class, RETURNS_SMART_NULLS);

    @Before
    public final void mockClient() {
        when(this.cloudFoundryClient.applicationsV2()).thenReturn(this.applications);
        when(this.cloudFoundryClient.domains()).thenReturn(this.domains);
        when(this.cloudFoundryClient.jobs()).thenReturn(this.jobs);
        when(this.cloudFoundryClient.organizations()).thenReturn(this.organizations);
        when(this.cloudFoundryClient.routes()).thenReturn(this.routes);
        when(this.cloudFoundryClient.sharedDomains()).thenReturn(this.sharedDomains);
        when(this.cloudFoundryClient.spaceQuotaDefinitions()).thenReturn(this.spaceQuotaDefinitions);
        when(this.cloudFoundryClient.spaces()).thenReturn(this.spaces);
    }

}
