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

package org.cloudfoundry.client.spring.v2.spaces;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.spring.ExpectedExceptionSubscriber;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringSpacesTest extends AbstractRestTest {

    private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root);

    @Test
    public void list() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/spaces/GET_response.json"))
                        .contentType(APPLICATION_JSON));

        ListSpacesRequest request = new ListSpacesRequest()
                .filterByName("test-name")
                .withPage(-1);

        this.spaces.list(request).subscribe(response -> {
            assertNull(response.getNextUrl());
            assertNull(response.getPreviousUrl());
            assertEquals(Integer.valueOf(1), response.getTotalPages());
            assertEquals(Integer.valueOf(1), response.getTotalResults());

            assertEquals(1, response.getResources().size());
            Resource<ListSpacesResponse.Entity> resource = response.getResources().get(0);

            Resource.Metadata metadata = resource.getMetadata();
            assertEquals("2015-07-27T22:43:08Z", metadata.getCreatedAt());
            assertEquals("b4293b09-8316-472c-a29a-6468a3adff59", metadata.getId());
            assertNull(metadata.getUpdatedAt());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59", metadata.getUrl());

            ListSpacesResponse.Entity entity = resource.getEntity();
            assertTrue(entity.getAllowSsh());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/app_events", entity.getApplicationEventsUrl
                    ());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/apps", entity.getApplicationsUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/auditors", entity.getAuditorsUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/developers", entity.getDevelopersUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/domains", entity.getDomainsUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/events", entity.getEventsUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/managers", entity.getManagersUrl());
            assertEquals("name-111", entity.getName());
            assertEquals("3ce736dd-3b8c-4f64-acab-ed76488b79a3", entity.getOrganizationId());
            assertEquals("/v2/organizations/3ce736dd-3b8c-4f64-acab-ed76488b79a3", entity.getOrganizationUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/routes", entity.getRoutesUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/security_groups", entity
                    .getSecurityGroupsUrl());
            assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/service_instances",
                    entity.getServiceInstancesUrl());
            assertNull(entity.getSpaceQuotaDefinitionId());

            this.mockServer.verify();
        });
    }

    @Test
    public void listError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListSpacesRequest request = new ListSpacesRequest()
                .filterByName("test-name")
                .withPage(-1);

        this.spaces.list(request).subscribe(new ExpectedExceptionSubscriber());
    }
}
