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

package org.cloudfoundry.client.spring.v2.organizations.auditors;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.auditors.AuditorResource.AuditorEntity;
import org.cloudfoundry.client.v2.organizations.auditors.CreateAuditorRequest;
import org.cloudfoundry.client.v2.organizations.auditors.CreateAuditorResponse;
import org.junit.Test;
import reactor.rx.Streams;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;

public final class SpringAuditorsTest extends AbstractRestTest {

    private final SpringAuditors auditors = new SpringAuditors(this.restTemplate, this.root);

    @Test
    public void create() throws IOException {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors/uaa-id-71")
                .status(CREATED)
                .responsePayload("v2/organizations/auditors/PUT_{id}_response.json"));

        CreateAuditorRequest request = new CreateAuditorRequest()
                .withAuditorId("uaa-id-71")
                .withOrganizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef");

        CreateAuditorResponse response = Streams.wrap(this.auditors.create(request)).next().get();

        Resource.Metadata metadata = response.getMetadata();
        assertEquals("2015-07-27T22:43:10Z", metadata.getCreatedAt());
        assertEquals("83c4fac5-cd9e-41ee-96df-b4f50fff4aef", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef", metadata.getUrl());

        AuditorEntity entity = response.getEntity();
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/app_events",
                entity.getApplicationEventsUrl());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors", entity.getAuditorsUrl());
        assertFalse(entity.getBillingEnabled());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/billing_managers",
                entity.getBillingManagersUrl());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/domains", entity.getDomainsUrl());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/managers", entity.getManagersUrl());
        assertEquals("name-187", entity.getName());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/private_domains",
                entity.getPrivateDomainsUrl());
        assertEquals("1d18a00b-4e36-412b-9308-2f5f2402e880", entity.getQuotaDefinitionId());
        assertEquals("/v2/quota_definitions/1d18a00b-4e36-412b-9308-2f5f2402e880", entity.getQuotaDefinitionUrl());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/space_quota_definitions",
                entity.getSpaceQuotaDefinitionsUrl());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/spaces", entity.getSpacesUrl());
        assertEquals("active", entity.getStatus());
        assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/users", entity.getUsersUrl());

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() throws IOException {

        mockRequest(new RequestContext()
                .method(PUT).path("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors/uaa-id-71")
                .errorResponse());

        CreateAuditorRequest request = new CreateAuditorRequest()
                .withAuditorId("uaa-id-71")
                .withOrganizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef");

        Streams.wrap(this.auditors.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() throws Throwable {
        Streams.wrap(this.auditors.create(new CreateAuditorRequest())).next().get();
    }

}
