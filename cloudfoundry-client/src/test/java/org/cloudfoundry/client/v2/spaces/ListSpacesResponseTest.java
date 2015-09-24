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

package org.cloudfoundry.client.v2.spaces;

import org.cloudfoundry.client.v2.spaces.ListSpacesResponse.ListSpacesResponseEntity;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse.ListSpacesResponseResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class ListSpacesResponseTest {

    @Test
    public void test() {
        ListSpacesResponseEntity entity = new ListSpacesResponseEntity()
                .allowSsh(true)
                .withApplicationEventsUrl("test-application-events-url")
                .withApplicationsUrl("test-applications-url")
                .withAuditorsUrl("test-auditors-url")
                .withDevelopersUrl("test-developers-url")
                .withDomainsUrl("test-domains-url")
                .withEventsUrl("test-events-url")
                .withManagersUrl("test-managers-url")
                .withName("test-name")
                .withOrganizationId("test-organization-id")
                .withOrganizationUrl("test-organization-url")
                .withRoutesUrl("test-routes-url")
                .withSecurityGroupsUrl("test-security-groups-url")
                .withServiceInstancesUrl("test-service-instances-url")
                .withSpaceQuotaDefinitionId("test-space-quota-definition-id");

        assertTrue(entity.getAllowSsh());
        assertEquals("test-application-events-url", entity.getApplicationEventsUrl());
        assertEquals("test-applications-url", entity.getApplicationsUrl());
        assertEquals("test-auditors-url", entity.getAuditorsUrl());
        assertEquals("test-developers-url", entity.getDevelopersUrl());
        assertEquals("test-domains-url", entity.getDomainsUrl());
        assertEquals("test-events-url", entity.getEventsUrl());
        assertEquals("test-managers-url", entity.getManagersUrl());
        assertEquals("test-name", entity.getName());
        assertEquals("test-organization-id", entity.getOrganizationId());
        assertEquals("test-organization-url", entity.getOrganizationUrl());
        assertEquals("test-routes-url", entity.getRoutesUrl());
        assertEquals("test-security-groups-url", entity.getSecurityGroupsUrl());
        assertEquals("test-service-instances-url", entity.getServiceInstancesUrl());
        assertEquals("test-space-quota-definition-id", entity.getSpaceQuotaDefinitionId());

        ListSpacesResponseResource resource = new ListSpacesResponseResource()
                .withEntity(entity);

        ListSpacesResponse response = new ListSpacesResponse()
                .withResource(resource);

        assertEquals(entity, response.getResources().get(0).getEntity());
    }

}
