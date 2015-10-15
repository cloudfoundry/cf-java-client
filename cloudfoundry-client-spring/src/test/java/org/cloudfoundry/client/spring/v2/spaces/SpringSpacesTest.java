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

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringSpacesTest extends AbstractRestTest {

    private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root);

    @Test
    public void associateAuditor() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id/auditors/test-auditor-id"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/spaces/PUT_{id}_auditors_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        AssociateSpaceAuditorRequest request = new AssociateSpaceAuditorRequest()
                .withId("test-id")
                .withAuditorId("test-auditor-id");

        AssociateSpaceAuditorResponse response = Streams.wrap(this.spaces.associateAuditor(request)).next().get();

        SpaceResource.Metadata metadata = response.getMetadata();
        assertEquals("2015-07-27T22:43:07Z", metadata.getCreatedAt());
        assertEquals("9639c996-9005-4b70-b852-d40f346d58dc", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc", metadata.getUrl());

        SpaceResource.SpaceEntity entity = response.getEntity();

        assertTrue(entity.getAllowSsh());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/app_events", entity.getApplicationEventsUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/apps", entity.getApplicationsUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/auditors", entity.getAuditorsUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/developers", entity.getDevelopersUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/domains", entity.getDomainsUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/events", entity.getEventsUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/managers", entity.getManagersUrl());
        assertEquals("name-59", entity.getName());
        assertEquals("bc168e1d-b399-4624-b7f6-fbe64eeb870f", entity.getOrganizationId());
        assertEquals("/v2/organizations/bc168e1d-b399-4624-b7f6-fbe64eeb870f", entity.getOrganizationUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/routes", entity.getRoutesUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/security_groups", entity.getSecurityGroupsUrl());
        assertEquals("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/service_instances",
                entity.getServiceInstancesUrl());
        assertNull(entity.getSpaceQuotaDefinitionId());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateAuditorError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id/auditors/test-auditor-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        AssociateSpaceAuditorRequest request = new AssociateSpaceAuditorRequest()
                .withId("test-id")
                .withAuditorId("test-auditor-id");

        Streams.wrap(this.spaces.associateAuditor(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateAuditorInvalidRequest() {
        Streams.wrap(this.spaces.associateAuditor(new AssociateSpaceAuditorRequest())).next().get();
    }

    @Test
    public void associateDeveloper() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id/developers/test-developer-id"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/spaces/PUT_{id}_developers_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        AssociateSpaceDeveloperRequest request = new AssociateSpaceDeveloperRequest()
                .withId("test-id")
                .withDeveloperId("test-developer-id");

        AssociateSpaceDeveloperResponse response = Streams.wrap(this.spaces.associateDeveloper(request)).next().get();

        SpaceResource.Metadata metadata = response.getMetadata();
        assertEquals("2015-07-27T22:43:07Z", metadata.getCreatedAt());
        assertEquals("6f8f8e0d-54f2-4736-a08e-1044fcf061d3", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3", metadata.getUrl());

        SpaceResource.SpaceEntity entity = response.getEntity();

        assertTrue(entity.getAllowSsh());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/app_events", entity.getApplicationEventsUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/apps", entity.getApplicationsUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/auditors", entity.getAuditorsUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/developers", entity.getDevelopersUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/domains", entity.getDomainsUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/events", entity.getEventsUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/managers", entity.getManagersUrl());
        assertEquals("name-68", entity.getName());
        assertEquals("5b556f7c-63f5-43e5-9522-c4fec533b09d", entity.getOrganizationId());
        assertEquals("/v2/organizations/5b556f7c-63f5-43e5-9522-c4fec533b09d", entity.getOrganizationUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/routes", entity.getRoutesUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/security_groups", entity.getSecurityGroupsUrl());
        assertEquals("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/service_instances",
                entity.getServiceInstancesUrl());
        assertNull(entity.getSpaceQuotaDefinitionId());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateDeveloperError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id/developers/test-developer-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        AssociateSpaceDeveloperRequest request = new AssociateSpaceDeveloperRequest()
                .withId("test-id")
                .withDeveloperId("test-developer-id");

        Streams.wrap(this.spaces.associateDeveloper(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateDeveloperInvalidRequest() {
        Streams.wrap(this.spaces.associateDeveloper(new AssociateSpaceDeveloperRequest())).next().get();
    }

    @Test
    public void associateManager() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id/managers/test-manager-id"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/spaces/PUT_{id}_managers_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        AssociateSpaceManagerRequest request = new AssociateSpaceManagerRequest()
                .withId("test-id")
                .withManagerId("test-manager-id");

        AssociateSpaceManagerResponse response = Streams.wrap(this.spaces.associateManager(request)).next().get();

        SpaceResource.Metadata metadata = response.getMetadata();
        assertEquals("2015-07-27T22:43:07Z", metadata.getCreatedAt());
        assertEquals("542943ff-a40b-4004-9559-434b0169508c", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c", metadata.getUrl());

        SpaceResource.SpaceEntity entity = response.getEntity();

        assertTrue(entity.getAllowSsh());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/app_events", entity.getApplicationEventsUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/apps", entity.getApplicationsUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/auditors", entity.getAuditorsUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/developers", entity.getDevelopersUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/domains", entity.getDomainsUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/events", entity.getEventsUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/managers", entity.getManagersUrl());
        assertEquals("name-85", entity.getName());
        assertEquals("0a68fcd5-dc1c-48d0-98dc-33008ce0d7ce", entity.getOrganizationId());
        assertEquals("/v2/organizations/0a68fcd5-dc1c-48d0-98dc-33008ce0d7ce", entity.getOrganizationUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/routes", entity.getRoutesUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/security_groups", entity.getSecurityGroupsUrl());
        assertEquals("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/service_instances",
                entity.getServiceInstancesUrl());
        assertNull(entity.getSpaceQuotaDefinitionId());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateManagerError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id/managers/test-manager-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        AssociateSpaceManagerRequest request = new AssociateSpaceManagerRequest()
                .withId("test-id")
                .withManagerId("test-manager-id");

        Streams.wrap(this.spaces.associateManager(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateManagerInvalidRequest() {
        Streams.wrap(this.spaces.associateManager(new AssociateSpaceManagerRequest())).next().get();
    }

    @Test
    public void get() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/spaces/GET_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        GetSpaceRequest request = new GetSpaceRequest()
                .withId("test-id");

        GetSpaceResponse response = Streams.wrap(this.spaces.get(request)).next().get();

        SpaceResource.Metadata metadata = response.getMetadata();
        assertEquals("2015-07-27T22:43:08Z", metadata.getCreatedAt());
        assertEquals("0f102457-c1fc-42e5-9c81-c7be2bc65dcd", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd", metadata.getUrl());

        SpaceResource.SpaceEntity entity = response.getEntity();

        assertTrue(entity.getAllowSsh());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/app_events", entity.getApplicationEventsUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/apps", entity.getApplicationsUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/auditors", entity.getAuditorsUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/developers", entity.getDevelopersUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/domains", entity.getDomainsUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/events", entity.getEventsUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/managers", entity.getManagersUrl());
        assertEquals("name-108", entity.getName());
        assertEquals("525a31fb-bc2b-4f7f-865e-1c93b42a6762", entity.getOrganizationId());
        assertEquals("/v2/organizations/525a31fb-bc2b-4f7f-865e-1c93b42a6762", entity.getOrganizationUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/routes", entity.getRoutesUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/security_groups", entity.getSecurityGroupsUrl());
        assertEquals("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/service_instances",
                entity.getServiceInstancesUrl());
        assertNull(entity.getSpaceQuotaDefinitionId());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces/test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        GetSpaceRequest request = new GetSpaceRequest()
                .withId("test-id");

        Streams.wrap(this.spaces.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        Streams.wrap(this.spaces.get(new GetSpaceRequest())).next().get();
    }

    @Test
    public void list() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v2/spaces/GET_response.json"))
                        .contentType(APPLICATION_JSON));

        ListSpacesRequest request = new ListSpacesRequest()
                .withName("test-name")
                .withPage(-1);

        ListSpacesResponse response = Streams.wrap(this.spaces.list(request)).next().get();

        assertNull(response.getNextUrl());
        assertNull(response.getPreviousUrl());
        assertEquals(Integer.valueOf(1), response.getTotalPages());
        assertEquals(Integer.valueOf(1), response.getTotalResults());

        assertEquals(1, response.getResources().size());
        ListSpacesResponse.ListSpacesResponseResource resource = response.getResources().get(0);

        SpaceResource.Metadata metadata = resource.getMetadata();
        assertEquals("2015-07-27T22:43:08Z", metadata.getCreatedAt());
        assertEquals("b4293b09-8316-472c-a29a-6468a3adff59", metadata.getId());
        assertNull(metadata.getUpdatedAt());
        assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59", metadata.getUrl());

        SpaceResource.SpaceEntity entity = resource.getEntity();
        assertTrue(entity.getAllowSsh());
        assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/app_events", entity.getApplicationEventsUrl());
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
        assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/security_groups", entity.getSecurityGroupsUrl());
        assertEquals("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/service_instances",
                entity.getServiceInstancesUrl());
        assertNull(entity.getSpaceQuotaDefinitionId());

        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/spaces?q=name%20IN%20test-name&page=-1"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListSpacesRequest request = new ListSpacesRequest()
                .withName("test-name")
                .withPage(-1);

        Streams.wrap(this.spaces.list(request)).next().get();
    }

}
