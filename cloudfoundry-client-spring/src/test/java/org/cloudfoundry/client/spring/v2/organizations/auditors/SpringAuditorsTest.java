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

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.spring.ExpectedExceptionSubscriber;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.auditors.AuditorResource.AuditorEntity;
import org.cloudfoundry.client.v2.organizations.auditors.CreateAuditorRequest;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringAuditorsTest extends AbstractRestTest {

    private final SpringAuditors auditors = new SpringAuditors(this.restTemplate, this.root);

    @Test
    public void create() throws IOException {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v2/organizations/83c4fac5-cd9e-41ee-96df" +
                        "-b4f50fff4aef/auditors/uaa-id-71"))
                .andRespond(withStatus(CREATED)
                        .body(new ClassPathResource("v2/organizations/auditors/PUT_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        CreateAuditorRequest request = new CreateAuditorRequest()
                .withAuditorId("uaa-id-71")
                .withOrganizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef");

        Streams.wrap(this.auditors.create(request)).consume(response -> {
            Resource.Metadata metadata = response.getMetadata();
            assertEquals("2015-07-27T22:43:10Z", metadata.getCreatedAt());
            assertEquals("83c4fac5-cd9e-41ee-96df-b4f50fff4aef", metadata.getId());
            assertNull(metadata.getUpdatedAt());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef", metadata.getUrl());

            AuditorEntity entity = response.getEntity();
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/app_events", entity.getApplicationEventsUrl());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors", entity.getAuditorsUrl());
            assertFalse(entity.getBillingEnabled());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/billing_managers", entity.getBillingManagersUrl());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/domains", entity.getDomainsUrl());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/managers", entity.getManagersUrl());
            assertEquals("name-187", entity.getName());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/private_domains", entity.getPrivateDomainsUrl());
            assertEquals("1d18a00b-4e36-412b-9308-2f5f2402e880", entity.getQuotaDefinitionId());
            assertEquals("/v2/quota_definitions/1d18a00b-4e36-412b-9308-2f5f2402e880", entity.getQuotaDefinitionUrl());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/space_quota_definitions", entity.getSpaceQuotaDefinitionsUrl());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/spaces", entity.getSpacesUrl());
            assertEquals("active", entity.getStatus());
            assertEquals("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/users", entity.getUsersUrl());

            this.mockServer.verify();
        });
    }

    @Test
    public void createError() throws IOException {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v2/organizations/83c4fac5-cd9e-41ee-96df" +
                        "-b4f50fff4aef/auditors/uaa-id-71"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        CreateAuditorRequest request = new CreateAuditorRequest()
                .withAuditorId("uaa-id-71")
                .withOrganizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef");

        this.auditors.create(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void createInvalidRequest() throws Throwable {
        this.auditors.create(new CreateAuditorRequest()).subscribe(new ExpectedExceptionSubscriber());
    }
}
