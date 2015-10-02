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

package org.cloudfoundry.client.spring.v2.organizations;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseEntity;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseResource;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringOrganizationsTest extends AbstractRestTest {

    private final SpringOrganizations organizations = new SpringOrganizations(this.restTemplate, this.root);

    @Test
    public void list() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/organizations?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/organizations/GET_response.json"))
                        .contentType(APPLICATION_JSON));

        ListOrganizationsRequest request = new ListOrganizationsRequest()
                .withName("test-name")
                .withPage(-1);

        ListOrganizationsResponse response = Streams.wrap(this.organizations.list(request)).next()
                .get();

        assertNull(response.getNextUrl());
        assertNull(response.getPreviousUrl());
        assertEquals(Integer.valueOf(1), response.getTotalPages());
        assertEquals(Integer.valueOf(1), response.getTotalResults());

        assertEquals(1, response.getResources().size());
        ListOrganizationsResponseResource resource = response.getResources().get(0);

        Resource.Metadata metadata = resource.getMetadata();
        assertEquals("2015-07-27T22:43:05Z", metadata.getCreatedAt());
        assertEquals("deb3c359-2261-45ba-b34f-ee7487acd71a", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a", metadata.getUrl());

        ListOrganizationsResponseEntity entity = resource.getEntity();
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/app_events",
                entity.getApplicationEventsUrl());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/auditors", entity.getAuditorsUrl());
        assertFalse(entity.getBillingEnabled());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/billing_managers",
                entity.getBillingManagersUrl());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/domains", entity.getDomainsUrl());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/managers", entity.getManagersUrl());
        assertEquals("the-system_domain-org-name", entity.getName());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/private_domains",
                entity.getPrivateDomainsUrl());
        assertEquals("9b56a1ec-4981-4a1e-9348-0d78eeca842c", entity.getQuotaDefinitionId());
        assertEquals("/v2/quota_definitions/9b56a1ec-4981-4a1e-9348-0d78eeca842c", entity.getQuotaDefinitionUrl());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/space_quota_definitions",
                entity.getSpaceQuotaDefinitionsUrl());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/spaces", entity.getSpacesUrl());
        assertEquals("active", entity.getStatus());
        assertEquals("/v2/organizations/deb3c359-2261-45ba-b34f-ee7487acd71a/users", entity.getUsersUrl());

        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/organizations?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListOrganizationsRequest request = new ListOrganizationsRequest()
                .withName("test-name")
                .withPage(-1);

        Streams.wrap(this.organizations.list(request)).next().get();
    }

}
