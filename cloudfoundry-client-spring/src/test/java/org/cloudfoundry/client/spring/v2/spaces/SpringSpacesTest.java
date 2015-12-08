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
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.Domain;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceEventsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceEventsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceMembersResource;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.UserRoleEntity;
import org.cloudfoundry.client.v2.spaces.UserRoleResource;
import org.junit.Test;
import reactor.rx.Streams;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.Service;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.builder;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringSpacesTest extends AbstractRestTest {

    private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root);

    @Test
    public void associateAuditor() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/auditors/test-auditor-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_auditors_{id}_response.json"));

        AssociateSpaceAuditorRequest request = AssociateSpaceAuditorRequest.builder()
                .id("test-id")
                .auditorId("test-auditor-id")
                .build();

        AssociateSpaceAuditorResponse expected = AssociateSpaceAuditorResponse.builder()
                .metadata(Metadata.builder()
                        .id("9639c996-9005-4b70-b852-d40f346d58dc")
                        .url("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                .entity(SpaceEntity.builder()
                        .name("name-59")
                        .organizationId("bc168e1d-b399-4624-b7f6-fbe64eeb870f")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/bc168e1d-b399-4624-b7f6-fbe64eeb870f")
                        .developersUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/developers")
                        .managersUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/managers")
                        .auditorsUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/auditors")
                        .applicationsUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/apps")
                        .routesUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/routes")
                        .domainsUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/domains")
                        .serviceInstancesUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/service_instances")
                        .applicationEventsUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/app_events")
                        .eventsUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/events")
                        .securityGroupsUrl("/v2/spaces/9639c996-9005-4b70-b852-d40f346d58dc/security_groups")
                        .build())
                .build();

        AssociateSpaceAuditorResponse actual = Streams.wrap(this.spaces.associateAuditor(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateAuditorError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/auditors/test-auditor-id")
                .errorResponse());

        AssociateSpaceAuditorRequest request = AssociateSpaceAuditorRequest.builder()
                .id("test-id")
                .auditorId("test-auditor-id")
                .build();

        Streams.wrap(this.spaces.associateAuditor(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateAuditorInvalidRequest() {
        AssociateSpaceAuditorRequest request = AssociateSpaceAuditorRequest.builder()
                .build();

        Streams.wrap(this.spaces.associateAuditor(request)).next().get();
    }

    @Test
    public void associateDeveloper() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/developers/test-developer-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_developers_{id}_response.json"));

        AssociateSpaceDeveloperRequest request = AssociateSpaceDeveloperRequest.builder()
                .id("test-id")
                .developerId("test-developer-id")
                .build();

        AssociateSpaceDeveloperResponse expected = AssociateSpaceDeveloperResponse.builder()
                .metadata(Metadata.builder()
                        .id("6f8f8e0d-54f2-4736-a08e-1044fcf061d3")
                        .url("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                .entity(SpaceEntity.builder()
                        .name("name-68")
                        .organizationId("5b556f7c-63f5-43e5-9522-c4fec533b09d")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/5b556f7c-63f5-43e5-9522-c4fec533b09d")
                        .developersUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/developers")
                        .managersUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/managers")
                        .auditorsUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/auditors")
                        .applicationsUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/apps")
                        .routesUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/routes")
                        .domainsUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/domains")
                        .serviceInstancesUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/service_instances")
                        .applicationEventsUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/app_events")
                        .eventsUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/events")
                        .securityGroupsUrl("/v2/spaces/6f8f8e0d-54f2-4736-a08e-1044fcf061d3/security_groups")
                        .build())
                .build();

        AssociateSpaceDeveloperResponse actual = Streams.wrap(this.spaces.associateDeveloper(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateDeveloperError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/developers/test-developer-id")
                .errorResponse());

        AssociateSpaceDeveloperRequest request = AssociateSpaceDeveloperRequest.builder()
                .id("test-id")
                .developerId("test-developer-id")
                .build();

        Streams.wrap(this.spaces.associateDeveloper(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateDeveloperInvalidRequest() {
        AssociateSpaceDeveloperRequest request = AssociateSpaceDeveloperRequest.builder()
                .build();

        Streams.wrap(this.spaces.associateDeveloper(request)).next().get();
    }

    @Test
    public void associateManager() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/managers/test-manager-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_managers_{id}_response.json"));

        AssociateSpaceManagerRequest request = AssociateSpaceManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        AssociateSpaceManagerResponse expected = AssociateSpaceManagerResponse.builder()
                .metadata(Metadata.builder()
                        .id("542943ff-a40b-4004-9559-434b0169508c")
                        .url("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                .entity(SpaceEntity.builder()
                        .name("name-85")
                        .organizationId("0a68fcd5-dc1c-48d0-98dc-33008ce0d7ce")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/0a68fcd5-dc1c-48d0-98dc-33008ce0d7ce")
                        .developersUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/developers")
                        .managersUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/managers")
                        .auditorsUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/auditors")
                        .applicationsUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/apps")
                        .routesUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/routes")
                        .domainsUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/domains")
                        .serviceInstancesUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/service_instances")
                        .applicationEventsUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/app_events")
                        .eventsUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/events")
                        .securityGroupsUrl("/v2/spaces/542943ff-a40b-4004-9559-434b0169508c/security_groups")
                        .build())
                .build();

        AssociateSpaceManagerResponse actual = Streams.wrap(this.spaces.associateManager(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateManagerError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/managers/test-manager-id")
                .errorResponse());

        AssociateSpaceManagerRequest request = AssociateSpaceManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        Streams.wrap(this.spaces.associateManager(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateManagerInvalidRequest() {
        AssociateSpaceManagerRequest request = AssociateSpaceManagerRequest.builder()
                .build();

        Streams.wrap(this.spaces.associateManager(request)).next().get();
    }

    @Test
    public void associateSecurityGroup() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/security_groups/test-security-group-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_security_group_{id}_response.json"));

        AssociateSpaceSecurityGroupRequest request = AssociateSpaceSecurityGroupRequest.builder()
                .id("test-id")
                .securityGroupId("test-security-group-id")
                .build();

        AssociateSpaceSecurityGroupResponse expected = AssociateSpaceSecurityGroupResponse.builder()
                .metadata(Metadata.builder()
                        .id("c9424692-395b-403b-90e6-10049bbd9e23")
                        .url("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23")
                        .createdAt("2015-07-27T22:43:06Z")
                        .build())
                .entity(SpaceEntity.builder()
                        .name("name-39")
                        .organizationId("67096164-bdcf-4b53-92e1-a2991882a066")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/67096164-bdcf-4b53-92e1-a2991882a066")
                        .developersUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/developers")
                        .managersUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/managers")
                        .auditorsUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/auditors")
                        .applicationsUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/apps")
                        .routesUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/routes")
                        .domainsUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/domains")
                        .serviceInstancesUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/service_instances")
                        .applicationEventsUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/app_events")
                        .eventsUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/events")
                        .securityGroupsUrl("/v2/spaces/c9424692-395b-403b-90e6-10049bbd9e23/security_groups")
                        .build())
                .build();

        AssociateSpaceSecurityGroupResponse actual = Streams.wrap(this.spaces.associateSecurityGroup(request))
                .next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void associateSecurityGroupError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v2/spaces/test-id/security_groups/test-security-group-id")
                .errorResponse());

        AssociateSpaceSecurityGroupRequest request = AssociateSpaceSecurityGroupRequest.builder()
                .id("test-id")
                .securityGroupId("test-security-group-id")
                .build();

        Streams.wrap(this.spaces.associateSecurityGroup(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void associateSecurityGroupInvalidRequest() {
        AssociateSpaceSecurityGroupRequest request = AssociateSpaceSecurityGroupRequest.builder()
                .build();

        Streams.wrap(this.spaces.associateSecurityGroup(request)).next().get();
    }

    @Test
    public void create() {
        mockRequest(new RequestContext()
                .method(POST).path("/v2/spaces")
                .requestPayload("v2/spaces/POST_request.json")
                .status(OK)
                .responsePayload("v2/spaces/POST_response.json"));

        CreateSpaceRequest request = CreateSpaceRequest.builder()
                .name("development")
                .organizationId("c523070c-3006-4715-86dd-414afaecd949")
                .build();

        CreateSpaceResponse expected = CreateSpaceResponse.builder()
                .metadata(Metadata.builder()
                        .id("d29dc30c-793c-49a6-97fe-9aff75dcbd12")
                        .url("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12")
                        .createdAt("2015-07-27T22:43:08Z")
                        .build())
                .entity(SpaceEntity.builder()
                        .name("development")
                        .organizationId("c523070c-3006-4715-86dd-414afaecd949")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/c523070c-3006-4715-86dd-414afaecd949")
                        .developersUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/developers")
                        .managersUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/managers")
                        .auditorsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/auditors")
                        .applicationsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/apps")
                        .routesUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/routes")
                        .domainsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/domains")
                        .serviceInstancesUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/service_instances")
                        .applicationEventsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/app_events")
                        .eventsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/events")
                        .securityGroupsUrl("/v2/spaces/d29dc30c-793c-49a6-97fe-9aff75dcbd12/security_groups")
                        .build())
                .build();

        CreateSpaceResponse actual = Streams.wrap(this.spaces.create(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v2/spaces")
                .requestPayload("v2/spaces/POST_request.json")
                .errorResponse());

        CreateSpaceRequest request = CreateSpaceRequest.builder()
                .name("development")
                .organizationId("c523070c-3006-4715-86dd-414afaecd949")
                .build();

        Streams.wrap(this.spaces.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() {
        CreateSpaceRequest request = CreateSpaceRequest.builder()
                .build();

        Streams.wrap(this.spaces.create(request)).next().get();
    }

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id?async=true")
                .status(NO_CONTENT));

        DeleteSpaceRequest request = DeleteSpaceRequest.builder()
                .async(true)
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id?async=true")
                .errorResponse());

        DeleteSpaceRequest request = DeleteSpaceRequest.builder()
                .async(true)
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        DeleteSpaceRequest request = DeleteSpaceRequest.builder()
                .build();

        Streams.wrap(this.spaces.delete(request)).next().get();
    }

    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces/test-id")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_response.json"));

        GetSpaceRequest request = GetSpaceRequest.builder()
                .id("test-id")
                .build();

        GetSpaceResponse expected = GetSpaceResponse.builder()
                .metadata(Metadata.builder()
                        .id("0f102457-c1fc-42e5-9c81-c7be2bc65dcd")
                        .url("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd")
                        .createdAt("2015-07-27T22:43:08Z")
                        .build())
                .entity(SpaceEntity.builder()
                        .name("name-108")
                        .organizationId("525a31fb-bc2b-4f7f-865e-1c93b42a6762")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/525a31fb-bc2b-4f7f-865e-1c93b42a6762")
                        .developersUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/developers")
                        .managersUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/managers")
                        .auditorsUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/auditors")
                        .applicationsUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/apps")
                        .routesUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/routes")
                        .domainsUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/domains")
                        .serviceInstancesUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/service_instances")
                        .applicationEventsUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/app_events")
                        .eventsUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/events")
                        .securityGroupsUrl("/v2/spaces/0f102457-c1fc-42e5-9c81-c7be2bc65dcd/security_groups")
                        .build())
                .build();

        GetSpaceResponse actual = Streams.wrap(this.spaces.get(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces/test-id")
                .errorResponse());

        GetSpaceRequest request = GetSpaceRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        GetSpaceRequest request = GetSpaceRequest.builder()
                .build();

        Streams.wrap(this.spaces.get(request)).next().get();
    }

    @Test
    public void getSummary() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces/test-id/summary")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_summary_response.json"));

        GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                .id("test-id")
                .build();

        GetSpaceSummaryResponse expected = GetSpaceSummaryResponse.builder()
                .id("f9c44c5c-9613-40b2-9296-e156c661a0ba")
                .name("name-649")
                .application(SpaceApplicationSummary.builder()
                        .id("e1efe0a2-a931-4604-a419-f76dbe23ad76")
                        .url("host-11.domain-48.example.com")
                        .route(Route.builder()
                                .id("3445e88d-adda-4255-9b9d-6f701fb0de17")
                                .host("host-11")
                                .domain(Domain.builder()
                                        .id("af154090-baca-4805-a8a2-9db93a16a84b")
                                        .name("domain-48.example.com")
                                        .build())
                                .build())
                        .serviceCount(1)
                        .serviceName("name-654")
                        .runningInstances(0)
                        .name("name-652")
                        .production(false)
                        .spaceId("f9c44c5c-9613-40b2-9296-e156c661a0ba")
                        .stackId("01a9ea88-1028-4d1a-a8ee-d1acc686815c")
                        .memory(1024)
                        .instances(1)
                        .diskQuota(1024)
                        .state("STOPPED")
                        .version("6505d60e-2a6f-475c-8c1d-85c66139447e")
                        .console(false)
                        .packageState("PENDING")
                        .healthCheckType("port")
                        .diego(false)
                        .packageUpdatedAt("2015-07-27T22:43:19Z")
                        .detectedStartCommand("")
                        .enableSsh(true)
                        .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                        .build())
                .service(builder()
                        .id("83e3713f-5f9b-4168-a43c-02cc66493cc0")
                        .name("name-654")
                        .boundApplicationCount(1)
                        .servicePlan(Plan.builder()
                                .id("67bd9226-6d63-48ac-9114-a756a01bff7c")
                                .name("name-655")
                                .service(Service.builder()
                                        .id("64ce598e-0c24-4dba-bfa1-594187db7404")
                                        .label("label-23")
                                        .build())
                                .build())
                        .build())
                .build();

        GetSpaceSummaryResponse actual = Streams.wrap(this.spaces.getSummary(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getSummaryError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces/test-id/summary")
                .errorResponse());

        GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.getSummary(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getSummaryInvalidRequest() {
        GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                .build();

        Streams.wrap(this.spaces.getSummary(request)).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_response.json")
        );

        ListSpacesRequest request = ListSpacesRequest.builder()
                .name("test-name")
                .page(-1)
                .build();

        ListSpacesResponse expected = ListSpacesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SpaceResource.builder()
                        .metadata(Metadata.builder()
                                .id("b4293b09-8316-472c-a29a-6468a3adff59")
                                .url("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59")
                                .createdAt("2015-07-27T22:43:08Z")
                                .build())
                        .entity(SpaceEntity.builder()
                                .name("name-111")
                                .organizationId("3ce736dd-3b8c-4f64-acab-ed76488b79a3")
                                .allowSsh(true)
                                .organizationUrl("/v2/organizations/3ce736dd-3b8c-4f64-acab-ed76488b79a3")
                                .developersUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/developers")
                                .managersUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/managers")
                                .auditorsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/auditors")
                                .applicationsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/apps")
                                .routesUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/routes")
                                .domainsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/domains")
                                .serviceInstancesUrl
                                        ("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/service_instances")
                                .applicationEventsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/app_events")
                                .eventsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/events")
                                .securityGroupsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/security_groups")
                                .build())
                        .build())
                .build();

        ListSpacesResponse actual = Streams.wrap(this.spaces.list(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test
    public void listApplications() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces/test-id/apps?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_apps_response.json"));

        ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                .id("test-id")
                .names(Collections.singletonList("test-name"))
                .page(-1)
                .build();

        ListSpaceApplicationsResponse expected = ListSpaceApplicationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ApplicationResource.builder()
                        .metadata(Metadata.builder()
                                .id("4ee31730-3c0e-4ec6-8329-26e727ab8ccd")
                                .url("/v2/apps/4ee31730-3c0e-4ec6-8329-26e727ab8ccd")
                                .createdAt("2015-07-27T22:43:08Z")
                                .updatedAt("2015-07-27T22:43:08Z")
                                .build())
                        .entity(ApplicationEntity.builder()
                                .name("name-103")
                                .production(false)
                                .spaceId("ca816a1b-ed3e-4ea8-bda2-2031d2e5b89f")
                                .stackId("e458a99f-53a4-4da4-b78a-5f2eb212cc47")
                                .memory(1024)
                                .instances(1)
                                .diskQuota(1024)
                                .state("STOPPED")
                                .version("cc21d137-45d6-4687-ab71-8288ac0e5724")
                                .console(false)
                                .packageState("PENDING")
                                .healthCheckType("port")
                                .diego(false)
                                .packageUpdatedAt("2015-07-27T22:43:08Z")
                                .detectedStartCommand("")
                                .enableSsh(true)
                                .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                                .spaceUrl("/v2/spaces/ca816a1b-ed3e-4ea8-bda2-2031d2e5b89f")
                                .stackUrl("/v2/stacks/e458a99f-53a4-4da4-b78a-5f2eb212cc47")
                                .eventsUrl("/v2/apps/4ee31730-3c0e-4ec6-8329-26e727ab8ccd/events")
                                .serviceBindingsUrl("/v2/apps/4ee31730-3c0e-4ec6-8329-26e727ab8ccd/service_bindings")
                                .routesUrl("/v2/apps/4ee31730-3c0e-4ec6-8329-26e727ab8ccd/routes")
                                .build())
                        .build())
                .build();

        ListSpaceApplicationsResponse actual = Streams.wrap(this.spaces.listApplications(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listApplicationsError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces/test-id/apps?q=name%20IN%20test-name&page=-1")
                .errorResponse());

        ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                .id("test-id")
                .names(Collections.singletonList("test-name"))
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listApplications(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listApplicationsInvalidRequest() {
        ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                .build();

        Streams.wrap(this.spaces.listApplications(request)).next().get();
    }

    @Test
    public void listAuditors() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/auditors?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_auditors_response.json"));

        ListSpaceAuditorsRequest request = ListSpaceAuditorsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListSpaceAuditorsResponse expected = ListSpaceAuditorsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ListSpaceMembersResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-15")
                                .url("/v2/users/uaa-id-15")
                                .createdAt("2015-07-27T22:43:07Z")
                                .build())
                        .entity(ListSpaceMembersResource.ListSpaceMembersResourceEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .username("auditor@example.com")
                                .spacesUrl("/v2/users/uaa-id-15/spaces")
                                .organizationsUrl("/v2/users/uaa-id-15/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-15/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-15/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-15/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-15/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-15/audited_spaces")
                                .build())
                        .build())
                .build();

        ListSpaceAuditorsResponse actual = Streams.wrap(this.spaces.listAuditors(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listAuditorsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/auditors?page=-1")
                .errorResponse());

        ListSpaceAuditorsRequest request = ListSpaceAuditorsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listAuditors(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listAuditorsInvalidRequest() {
        ListSpaceAuditorsRequest request = ListSpaceAuditorsRequest.builder()
                .build();

        Streams.wrap(this.spaces.listAuditors(request)).next().get();
    }

    @Test
    public void listDevelopers() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/developers?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_developers_response.json"));

        ListSpaceDevelopersRequest request = ListSpaceDevelopersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListSpaceDevelopersResponse expected = ListSpaceDevelopersResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ListSpaceMembersResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-24")
                                .url("/v2/users/uaa-id-24")
                                .createdAt("2015-07-27T22:43:07Z")
                                .build())
                        .entity(ListSpaceMembersResource.ListSpaceMembersResourceEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .username("developer@example.com")
                                .spacesUrl("/v2/users/uaa-id-24/spaces")
                                .organizationsUrl("/v2/users/uaa-id-24/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-24/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-24/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-24/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-24/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-24/audited_spaces")
                                .build())
                        .build())
                .build();

        ListSpaceDevelopersResponse actual = Streams.wrap(this.spaces.listDevelopers(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listDevelopersError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/developers?page=-1")
                .errorResponse());

        ListSpaceDevelopersRequest request = ListSpaceDevelopersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listDevelopers(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listDevelopersInvalidRequest() {
        ListSpaceDevelopersRequest request = ListSpaceDevelopersRequest.builder()
                .build();

        Streams.wrap(this.spaces.listDevelopers(request)).next().get();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v2/spaces?q=name%20IN%20test-name&page=-1")
                .errorResponse());

        ListSpacesRequest request = ListSpacesRequest.builder()
                .name("test-name")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.list(request)).next().get();
    }

    @Test
    public void listEvents() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/events?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_events_response.json"));

        ListSpaceEventsRequest request = ListSpaceEventsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Map<String, Object> metadatas = new HashMap<>();
        metadatas.put("request", Collections.singletonMap("name", "new_name"));

        ListSpaceEventsResponse expected = ListSpaceEventsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(EventResource.builder()
                        .metadata(Metadata.builder()
                                .id("cbb42f10-2737-4522-95dc-3ada35056fa8")
                                .url("/v2/events/cbb42f10-2737-4522-95dc-3ada35056fa8")
                                .createdAt("2015-07-27T22:43:07Z")
                                .build())
                        .entity(EventEntity.builder()
                                .type("audit.space.update")
                                .actor("uaa-id-10")
                                .actorType("user")
                                .actorName("user@example.com")
                                .actee("33d44b03-6203-47a7-b71c-9bf6fcaeb54a")
                                .acteeType("space")
                                .acteeName("name-56")
                                .timestamp("2015-07-27T22:43:07Z")
                                .metadatas(metadatas)
                                .spaceId("33d44b03-6203-47a7-b71c-9bf6fcaeb54a")
                                .organizationId("ab7dff90-0bc7-4ce0-be5b-b8ecc676bc4a")
                                .build())
                        .build())
                .build();

        ListSpaceEventsResponse actual = Streams.wrap(this.spaces.listEvents(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listEventsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/events?page=-1")
                .errorResponse());

        ListSpaceEventsRequest request = ListSpaceEventsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listEvents(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listEventsInvalidRequest() {
        ListSpaceEventsRequest request = ListSpaceEventsRequest.builder()
                .build();

        Streams.wrap(this.spaces.listEvents(request)).next().get();
    }

    @Test
    public void listManagers() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/managers?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_managers_response.json"));

        ListSpaceManagersRequest request = ListSpaceManagersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListSpaceManagersResponse expected = ListSpaceManagersResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ListSpaceMembersResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-35")
                                .url("/v2/users/uaa-id-35")
                                .createdAt("2015-07-27T22:43:07Z")
                                .build())
                        .entity(ListSpaceMembersResource.ListSpaceMembersResourceEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceId(null)
                                .username("manager@example.com")
                                .spacesUrl("/v2/users/uaa-id-35/spaces")
                                .organizationsUrl("/v2/users/uaa-id-35/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-35/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-35/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-35/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-35/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-35/audited_spaces")
                                .build())
                        .build())
                .build();

        ListSpaceManagersResponse actual = Streams.wrap(this.spaces.listManagers(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listManagersError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/managers?page=-1")
                .errorResponse());

        ListSpaceManagersRequest request = ListSpaceManagersRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listManagers(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listManagersInvalidRequest() {
        ListSpaceManagersRequest request = ListSpaceManagersRequest.builder()
                .build();

        Streams.wrap(this.spaces.listManagers(request)).next().get();
    }

    @Test
    public void listRoutes() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/routes?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_routes_response.json"));

        ListSpaceRoutesRequest request = ListSpaceRoutesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListSpaceRoutesResponse expected = ListSpaceRoutesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(RouteResource.builder()
                        .metadata(Resource.Metadata.builder()
                                .createdAt("2015-07-27T22:43:07Z")
                                .id("cab98364-2ccb-42e0-b901-765c4e915d49")
                                .url("/v2/routes/cab98364-2ccb-42e0-b901-765c4e915d49")
                                .build())
                        .entity(RouteEntity.builder()
                                .applicationUrl("/v2/routes/cab98364-2ccb-42e0-b901-765c4e915d49/apps")
                                .domainId("64aaa8e0-af71-4d7e-afef-e2efdbd66552")
                                .domainUrl("/v2/domains/64aaa8e0-af71-4d7e-afef-e2efdbd66552")
                                .host("host-1")
                                .path("")
                                .spaceId("8af29896-2f0d-42b4-a24c-169343b2aad9")
                                .spaceUrl("/v2/spaces/8af29896-2f0d-42b4-a24c-169343b2aad9")
                                .build())
                        .build())
                .build();

        ListSpaceRoutesResponse actual = Streams.wrap(this.spaces.listRoutes(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listRoutesError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/routes?page=-1")
                .errorResponse());

        ListSpaceRoutesRequest request = ListSpaceRoutesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listRoutes(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listRoutesInvalidRequest() {
        ListSpaceRoutesRequest request = ListSpaceRoutesRequest.builder()
                .build();

        Streams.wrap(this.spaces.listRoutes(request)).next().get();
    }

    @Test
    public void listSecurityGroups() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/security_groups?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_security_groups_response.json"));

        ListSpaceSecurityGroupsRequest request = ListSpaceSecurityGroupsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListSpaceSecurityGroupsResponse expected = ListSpaceSecurityGroupsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(SecurityGroupResource.builder()
                        .metadata(Metadata.builder()
                                .id("a3728437-fe41-42c1-875c-b59cffc7498c")
                                .url("/v2/security_groups/a3728437-fe41-42c1-875c-b59cffc7498c")
                                .createdAt("2015-07-27T22:43:07Z")
                                .build())
                        .entity(SecurityGroupEntity.builder()
                                .name("name-47")
                                .rule(SecurityGroupEntity.RuleEntity.builder()
                                        .destination("198.41.191.47/1")
                                        .ports("8080")
                                        .protocol("udp")
                                        .build())
                                .runningDefault(false)
                                .spacesUrl("/v2/security_groups/a3728437-fe41-42c1-875c-b59cffc7498c/spaces")
                                .stagingDefault(false)
                                .build())
                        .build())
                .build();

        ListSpaceSecurityGroupsResponse actual = Streams.wrap(this.spaces.listSecurityGroups(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listSecurityGroupsError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/security_groups?page=-1")
                .errorResponse());

        ListSpaceSecurityGroupsRequest request = ListSpaceSecurityGroupsRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listSecurityGroups(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listSecurityGroupsInvalidRequest() {
        ListSpaceSecurityGroupsRequest request = ListSpaceSecurityGroupsRequest.builder()
                .build();

        Streams.wrap(this.spaces.listSecurityGroups(request)).next().get();
    }

    @Test
    public void listServiceInstances() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/service_instances?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_service_instances_response.json"));

        ListSpaceServiceInstancesRequest request = ListSpaceServiceInstancesRequest.builder()
                .id("test-id")
                .returnUserProvidedServiceInstances(true)
                .page(-1)
                .build();

        ListSpaceServiceInstancesResponse expected = ListSpaceServiceInstancesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceInstanceResource.builder()
                        .metadata(Metadata.builder()
                                .id("7046d37c-8a50-49d5-ba53-abb103a92142")
                                .url("/v2/service_instances/7046d37c-8a50-49d5-ba53-abb103a92142")
                                .createdAt("2015-07-27T22:43:08Z")
                                .build())
                        .entity(ServiceInstanceEntity.builder()
                                .name("name-97")
                                .credential("creds-key-52", "creds-val-52")
                                .servicePlanId("77157c85-203a-4fac-b9a3-003988ff879a")
                                .spaceId("aead50c9-0d45-410c-befd-431c8b7b3e30")
                                .type("managed_service_instance")
                                .spaceUrl("/v2/spaces/aead50c9-0d45-410c-befd-431c8b7b3e30")
                                .servicePlanUrl("/v2/service_plans/77157c85-203a-4fac-b9a3-003988ff879a")
                                .serviceBindingsUrl
                                        ("/v2/service_instances/7046d37c-8a50-49d5-ba53-abb103a92142/service_bindings")
                                .serviceKeysUrl
                                        ("/v2/service_instances/7046d37c-8a50-49d5-ba53-abb103a92142/service_keys")
                                .build())
                        .build())
                .build();

        ListSpaceServiceInstancesResponse actual = Streams.wrap(this.spaces.listServiceInstances(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listServiceInstancesError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/service_instances?page=-1")
                .errorResponse());

        ListSpaceServiceInstancesRequest request = ListSpaceServiceInstancesRequest.builder()
                .id("test-id")
                .returnUserProvidedServiceInstances(true)
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listServiceInstances(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listServiceInstancesInvalidRequest() {
        ListSpaceServiceInstancesRequest request = ListSpaceServiceInstancesRequest.builder()
                .build();

        Streams.wrap(this.spaces.listServiceInstances(request)).next().get();
    }

    @Test
    public void listUserRoles() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/user_roles?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_user_roles_response.json"));

        ListSpaceUserRolesRequest request = ListSpaceUserRolesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        ListSpaceUserRolesResponse expected = ListSpaceUserRolesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserRoleResource.builder()
                        .metadata(Metadata.builder()
                                .id("uaa-id-8")
                                .url("/v2/users/uaa-id-8")
                                .createdAt("2015-07-27T22:43:07Z")
                                .build())
                        .entity(UserRoleEntity.builder()
                                .admin(false)
                                .active(false)
                                .defaultSpaceGuid(null)
                                .username("everything@example.com")
                                .spaceRole("space_developer")
                                .spaceRole("space_manager")
                                .spaceRole("space_auditor")
                                .spacesUrl("/v2/users/uaa-id-8/spaces")
                                .organizationsUrl("/v2/users/uaa-id-8/organizations")
                                .managedOrganizationsUrl("/v2/users/uaa-id-8/managed_organizations")
                                .billingManagedOrganizationsUrl("/v2/users/uaa-id-8/billing_managed_organizations")
                                .auditedOrganizationsUrl("/v2/users/uaa-id-8/audited_organizations")
                                .managedSpacesUrl("/v2/users/uaa-id-8/managed_spaces")
                                .auditedSpacesUrl("/v2/users/uaa-id-8/audited_spaces")
                                .build())
                        .build())
                .build();

        ListSpaceUserRolesResponse actual = Streams.wrap(this.spaces.listUserRoles(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listUserRolesError() {
        mockRequest(new RequestContext()
                .method(GET).path("v2/spaces/test-id/user_roles?page=-1")
                .errorResponse());

        ListSpaceUserRolesRequest request = ListSpaceUserRolesRequest.builder()
                .id("test-id")
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listUserRoles(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listUserRolesInvalidRequest() {
        ListSpaceUserRolesRequest request = ListSpaceUserRolesRequest.builder()
                .page(-1)
                .build();

        Streams.wrap(this.spaces.listUserRoles(request)).next().get();
    }

    @Test
    public void removeAuditor() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/auditors/test-auditor-id")
                .status(NO_CONTENT));

        RemoveSpaceAuditorRequest request = RemoveSpaceAuditorRequest.builder()
                .auditorId("test-auditor-id")
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.removeAuditor(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void removeAuditorError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/auditors/test-auditor-id")
                .errorResponse());

        RemoveSpaceAuditorRequest request = RemoveSpaceAuditorRequest.builder()
                .auditorId("test-auditor-id")
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.removeAuditor(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void removeAuditorInvalidRequest() {
        RemoveSpaceAuditorRequest request = RemoveSpaceAuditorRequest.builder()
                .build();

        Streams.wrap(this.spaces.removeAuditor(request)).next().get();
    }

    @Test
    public void removeDeveloper() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/developers/test-developer-id")
                .status(NO_CONTENT));

        RemoveSpaceDeveloperRequest request = RemoveSpaceDeveloperRequest.builder()
                .developerId("test-developer-id")
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.removeDeveloper(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void removeDeveloperError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/developers/test-developer-id")
                .errorResponse());

        RemoveSpaceDeveloperRequest request = RemoveSpaceDeveloperRequest.builder()
                .developerId("test-developer-id")
                .id("test-id")
                .build();

        Streams.wrap(this.spaces.removeDeveloper(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void removeDeveloperInvalidRequest() {
        RemoveSpaceDeveloperRequest request = RemoveSpaceDeveloperRequest.builder()
                .build();

        Streams.wrap(this.spaces.removeDeveloper(request)).next().get();
    }

    @Test
    public void removeManager() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/managers/test-manager-id")
                .status(NO_CONTENT));

        RemoveSpaceManagerRequest request = RemoveSpaceManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        Streams.wrap(this.spaces.removeManager(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void removeManagerError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/managers/test-manager-id")
                .errorResponse());

        RemoveSpaceManagerRequest request = RemoveSpaceManagerRequest.builder()
                .id("test-id")
                .managerId("test-manager-id")
                .build();

        Streams.wrap(this.spaces.removeManager(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void removeManagerInvalidRequest() {
        RemoveSpaceManagerRequest request = RemoveSpaceManagerRequest.builder()
                .build();

        Streams.wrap(this.spaces.removeManager(request)).next().get();
    }

    @Test
    public void removeSecurityGroup() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/security_groups/test-security-group-id")
                .status(NO_CONTENT));

        RemoveSpaceSecurityGroupRequest request = RemoveSpaceSecurityGroupRequest.builder()
                .id("test-id")
                .securityGroupId("test-security-group-id")
                .build();

        Streams.wrap(this.spaces.removeSecurityGroup(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void removeSecurityGroupError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("v2/spaces/test-id/security_groups/test-security-group-id")
                .errorResponse());

        RemoveSpaceSecurityGroupRequest request = RemoveSpaceSecurityGroupRequest.builder()
                .id("test-id")
                .securityGroupId("test-security-group-id")
                .build();

        Streams.wrap(this.spaces.removeSecurityGroup(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void removeSecurityGroupInvalidRequest() {
        RemoveSpaceSecurityGroupRequest request = RemoveSpaceSecurityGroupRequest.builder()
                .build();

        Streams.wrap(this.spaces.removeSecurityGroup(request)).next().get();
    }

    @Test
    public void update() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/spaces/test-id")
                .requestPayload("v2/spaces/PUT_{id}_request.json")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_response.json"));

        UpdateSpaceRequest request = UpdateSpaceRequest.builder()
                .id("test-id")
                .name("New Space Name")
                .build();

        UpdateSpaceResponse expected = UpdateSpaceResponse.builder()
                .metadata(Metadata.builder()
                        .id("e7b9e252-88cb-415c-ace4-2864922e550c")
                        .url("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c")
                        .createdAt("2015-07-27T22:43:08Z")
                        .updatedAt("2015-07-27T22:43:08Z")
                        .build())
                .entity(SpaceEntity.builder()
                        .name("New Space Name")
                        .organizationId("71c72756-e8b8-4c4a-b832-b3f9e3052c70")
                        .allowSsh(true)
                        .organizationUrl("/v2/organizations/71c72756-e8b8-4c4a-b832-b3f9e3052c70")
                        .developersUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/developers")
                        .managersUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/managers")
                        .auditorsUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/auditors")
                        .applicationsUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/apps")
                        .routesUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/routes")
                        .domainsUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/domains")
                        .serviceInstancesUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/service_instances")
                        .applicationEventsUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/app_events")
                        .eventsUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/events")
                        .securityGroupsUrl("/v2/spaces/e7b9e252-88cb-415c-ace4-2864922e550c/security_groups")
                        .build())
                .build();

        UpdateSpaceResponse actual = Streams.wrap(this.spaces.update(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void updateError() {
        mockRequest(new RequestContext()
                .method(PUT).path("v2/spaces/test-id")
                .requestPayload("v2/spaces/PUT_{id}_request.json")
                .errorResponse());

        UpdateSpaceRequest request = UpdateSpaceRequest.builder()
                .id("test-id")
                .name("New Space Name")
                .build();

        Streams.wrap(this.spaces.update(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void updateInvalidRequest() {
        UpdateSpaceRequest request = UpdateSpaceRequest.builder()
                .build();

        Streams.wrap(this.spaces.update(request)).next().get();
    }

}
