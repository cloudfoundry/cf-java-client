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
import org.cloudfoundry.client.v2.organizations.auditors.AuditorResource.AuditorEntity;
import org.cloudfoundry.client.v2.organizations.auditors.CreateAuditorRequest;
import org.cloudfoundry.client.v2.organizations.auditors.CreateAuditorResponse;
import org.junit.Test;
import reactor.rx.Streams;

import java.io.IOException;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.junit.Assert.assertEquals;
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

        CreateAuditorRequest request = CreateAuditorRequest.builder()
                .auditorId("uaa-id-71")
                .organizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                .build();

        CreateAuditorResponse expected = CreateAuditorResponse.builder()
                .metadata(Metadata.builder()
                        .id("83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                        .url("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                        .createdAt("2015-07-27T22:43:10Z")
                        .build())
                .entity(AuditorEntity.builder()
                        .name("name-187")
                        .billingEnabled(false)
                        .quotaDefinitionId("1d18a00b-4e36-412b-9308-2f5f2402e880")
                        .status("active")
                        .quotaDefinitionUrl("/v2/quota_definitions/1d18a00b-4e36-412b-9308-2f5f2402e880")
                        .spacesUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/spaces")
                        .domainsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/domains")
                        .privateDomainsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/private_domains")
                        .usersUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/users")
                        .managersUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/managers")
                        .billingManagersUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/billing_managers")
                        .auditorsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors")
                        .applicationEventsUrl("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/app_events")
                        .spaceQuotaDefinitionsUrl
                                ("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/space_quota_definitions")
                        .build())
                .build();

        CreateAuditorResponse actual = Streams.wrap(this.auditors.create(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() throws IOException {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/organizations/83c4fac5-cd9e-41ee-96df-b4f50fff4aef/auditors/uaa-id-71")
                .errorResponse());

        CreateAuditorRequest request = CreateAuditorRequest.builder()
                .auditorId("uaa-id-71")
                .organizationId("83c4fac5-cd9e-41ee-96df-b4f50fff4aef")
                .build();

        Streams.wrap(this.auditors.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() throws Throwable {
        CreateAuditorRequest request = CreateAuditorRequest.builder()
                .build();

        Streams.wrap(this.auditors.create(request)).next().get();
    }

}
