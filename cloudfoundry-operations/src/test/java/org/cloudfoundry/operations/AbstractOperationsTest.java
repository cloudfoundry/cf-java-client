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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractOperationsTest {

    protected static final String TEST_ORGANIZATION = "test-organization-id";

    protected static final String TEST_SPACE = "test-space-id";

    protected final ApplicationsV2 applications = mock(ApplicationsV2.class);

    protected final CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class);

    protected final Organizations organizations = mock(Organizations.class);

    protected final Spaces spaces = mock(Spaces.class);

    @Before
    public void mockClient() throws Exception {
        when(this.cloudFoundryClient.applicationsV2()).thenReturn(this.applications);
        when(this.cloudFoundryClient.organizations()).thenReturn(this.organizations);
        when(this.cloudFoundryClient.spaces()).thenReturn(this.spaces);
    }
}
