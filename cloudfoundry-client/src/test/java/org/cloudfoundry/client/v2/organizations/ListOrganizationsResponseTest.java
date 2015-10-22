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

package org.cloudfoundry.client.v2.organizations;

import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class ListOrganizationsResponseTest {

    @Test
    public void test() {
        ListOrganizationsResponseEntity entity = new ListOrganizationsResponseEntity()
                .withApplicationEventsUrl("test-application-events-url")
                .withAuditorsUrl("test-auditors-url")
                .withBillingEnabled(true)
                .withBillingManagersUrl("test-billing-managers-url")
                .withDomainsUrl("test-domains-url")
                .withManagersUrl("test-managers-url")
                .withName("test-name")
                .withPrivateDomainsUrl("test-private-domains-url")
                .withQuotaDefinitionId("test-quota-definition-id")
                .withQuotaDefinitionUrl("test-quota-definition-url")
                .withSpaceQuotaDefinitionsUrl("test-space-quota-definitions-url")
                .withSpacesUrl("test-spaces-url")
                .withStatus("test-status")
                .withUsersUrl("test-users-url");

        assertEquals("test-application-events-url", entity.getApplicationEventsUrl());
        assertEquals("test-auditors-url", entity.getAuditorsUrl());
        assertTrue(entity.getBillingEnabled());
        assertEquals("test-billing-managers-url", entity.getBillingManagersUrl());
        assertEquals("test-domains-url", entity.getDomainsUrl());
        assertEquals("test-managers-url", entity.getManagersUrl());
        assertEquals("test-name", entity.getName());
        assertEquals("test-private-domains-url", entity.getPrivateDomainsUrl());
        assertEquals("test-quota-definition-id", entity.getQuotaDefinitionId());
        assertEquals("test-quota-definition-url", entity.getQuotaDefinitionUrl());
        assertEquals("test-space-quota-definitions-url", entity.getSpaceQuotaDefinitionsUrl());
        assertEquals("test-spaces-url", entity.getSpacesUrl());
        assertEquals("test-status", entity.getStatus());
        assertEquals("test-users-url", entity.getUsersUrl());

        ListOrganizationsResponseResource resource = new ListOrganizationsResponseResource()
                .withEntity(entity);

        ListOrganizationsResponse response = new ListOrganizationsResponse()
                .withResource(resource);

        assertTrue(response.getResources() != null && response.getResources().size() == 1);
        assertEquals(entity, response.getResources().get(0).getEntity());
    }

}
