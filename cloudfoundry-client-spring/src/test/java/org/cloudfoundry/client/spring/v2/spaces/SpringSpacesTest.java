/*
 * Copyright 2013-2016 the original author or authors.
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

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.Domain;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceResponse;
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
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceEventsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceEventsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.UserSpaceRoleEntity;
import org.cloudfoundry.client.v2.spaces.UserSpaceRoleResource;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.utils.StringMap;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.Plan.Service;
import static org.cloudfoundry.client.v2.serviceinstances.ServiceInstance.builder;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringSpacesTest {

    public static final class AssociateAuditor extends AbstractApiTest<AssociateSpaceAuditorRequest, AssociateSpaceAuditorResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceAuditorRequest getInvalidRequest() {
            return AssociateSpaceAuditorRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/spaces/test-space-id/auditors/test-auditor-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_auditors_{id}_response.json");
        }

        @Override
        protected AssociateSpaceAuditorResponse getResponse() {
            return AssociateSpaceAuditorResponse.builder()
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
        }

        @Override
        protected AssociateSpaceAuditorRequest getValidRequest() throws Exception {
            return AssociateSpaceAuditorRequest.builder()
                .spaceId("test-space-id")
                .auditorId("test-auditor-id")
                .build();
        }

        @Override
        protected Mono<AssociateSpaceAuditorResponse> invoke(AssociateSpaceAuditorRequest request) {
            return this.spaces.associateAuditor(request);
        }

    }

    public static final class AssociateAuditorByUsername extends AbstractApiTest<AssociateSpaceAuditorByUsernameRequest, AssociateSpaceAuditorByUsernameResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceAuditorByUsernameRequest getInvalidRequest() {
            return AssociateSpaceAuditorByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("v2/spaces/test-space-id/auditors")
                .requestPayload("v2/spaces/PUT_{id}_auditors_request.json")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_auditors_response.json");
        }

        @Override
        protected AssociateSpaceAuditorByUsernameResponse getResponse() {
            return AssociateSpaceAuditorByUsernameResponse.builder()
                .metadata(Metadata.builder()
                    .id("873193ee-878c-436f-80bd-10d68927937d")
                    .url("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d")
                    .createdAt("2015-11-30T23:38:28Z")
                    .build())
                .entity(SpaceEntity.builder()
                    .allowSsh(true)
                    .applicationEventsUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/app_events")
                    .applicationsUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/apps")
                    .auditorsUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/auditors")
                    .developersUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/developers")
                    .domainsUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/domains")
                    .eventsUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/events")
                    .managersUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/managers")
                    .name("name-101")
                    .organizationId("5fddaf61-092d-4b33-9490-8350963db89e")
                    .organizationUrl("/v2/organizations/5fddaf61-092d-4b33-9490-8350963db89e")
                    .routesUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/routes")
                    .securityGroupsUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/security_groups")
                    .serviceInstancesUrl("/v2/spaces/873193ee-878c-436f-80bd-10d68927937d/service_instances")
                    .build())
                .build();
        }

        @Override
        protected AssociateSpaceAuditorByUsernameRequest getValidRequest() throws Exception {
            return AssociateSpaceAuditorByUsernameRequest.builder()
                .spaceId("test-space-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<AssociateSpaceAuditorByUsernameResponse> invoke(AssociateSpaceAuditorByUsernameRequest request) {
            return this.spaces.associateAuditorByUsername(request);
        }

    }

    public static final class AssociateDeveloper extends AbstractApiTest<AssociateSpaceDeveloperRequest, AssociateSpaceDeveloperResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceDeveloperRequest getInvalidRequest() {
            return AssociateSpaceDeveloperRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/spaces/test-space-id/developers/test-developer-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_developers_{id}_response.json");
        }

        @Override
        protected AssociateSpaceDeveloperResponse getResponse() {
            return AssociateSpaceDeveloperResponse.builder()
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
        }

        @Override
        protected AssociateSpaceDeveloperRequest getValidRequest() throws Exception {
            return AssociateSpaceDeveloperRequest.builder()
                .spaceId("test-space-id")
                .developerId("test-developer-id")
                .build();
        }

        @Override
        protected Mono<AssociateSpaceDeveloperResponse> invoke(AssociateSpaceDeveloperRequest request) {
            return this.spaces.associateDeveloper(request);
        }

    }

    public static final class AssociateManager extends AbstractApiTest<AssociateSpaceManagerRequest, AssociateSpaceManagerResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceManagerRequest getInvalidRequest() {
            return AssociateSpaceManagerRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/spaces/test-space-id/managers/test-manager-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_managers_{id}_response.json");
        }

        @Override
        protected AssociateSpaceManagerResponse getResponse() {
            return AssociateSpaceManagerResponse.builder()
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
        }

        @Override
        protected AssociateSpaceManagerRequest getValidRequest() throws Exception {
            return AssociateSpaceManagerRequest.builder()
                .spaceId("test-space-id")
                .managerId("test-manager-id")
                .build();
        }

        @Override
        protected Mono<AssociateSpaceManagerResponse> invoke(AssociateSpaceManagerRequest request) {
            return this.spaces.associateManager(request);
        }

    }

    public static final class AssociateSecurityGroup extends AbstractApiTest<AssociateSpaceSecurityGroupRequest, AssociateSpaceSecurityGroupResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceSecurityGroupRequest getInvalidRequest() {
            return AssociateSpaceSecurityGroupRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/spaces/test-space-id/security_groups/test-security-group-id")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_security_group_{id}_response.json");
        }

        @Override
        protected AssociateSpaceSecurityGroupResponse getResponse() {
            return AssociateSpaceSecurityGroupResponse.builder()
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
        }

        @Override
        protected AssociateSpaceSecurityGroupRequest getValidRequest() throws Exception {
            return AssociateSpaceSecurityGroupRequest.builder()
                .spaceId("test-space-id")
                .securityGroupId("test-security-group-id")
                .build();
        }

        @Override
        protected Mono<AssociateSpaceSecurityGroupResponse> invoke(AssociateSpaceSecurityGroupRequest request) {
            return this.spaces.associateSecurityGroup(request);
        }

    }

    public static final class AssociateSpaceDeveloperByUsername extends AbstractApiTest<AssociateSpaceDeveloperByUsernameRequest, AssociateSpaceDeveloperByUsernameResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceDeveloperByUsernameRequest getInvalidRequest() {
            return AssociateSpaceDeveloperByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("v2/spaces/test-space-id/developers")
                .requestPayload("v2/spaces/PUT_{id}_developers_request.json")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_developers_response.json");
        }

        @Override
        protected AssociateSpaceDeveloperByUsernameResponse getResponse() {
            return AssociateSpaceDeveloperByUsernameResponse.builder()
                .metadata(Metadata.builder()
                    .id("b6d11f17-1cea-4c00-a951-fef3223b8c84")
                    .url("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84")
                    .createdAt("2015-11-30T23:38:27Z")
                    .build())
                .entity(SpaceEntity.builder()
                    .name("name-58")
                    .organizationId("b13bbebe-427e-424d-8820-2937f7e218d5")
                    .allowSsh(true)
                    .organizationUrl("/v2/organizations/b13bbebe-427e-424d-8820-2937f7e218d5")
                    .developersUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/developers")
                    .managersUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/managers")
                    .auditorsUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/auditors")
                    .applicationsUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/apps")
                    .routesUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/routes")
                    .domainsUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/domains")
                    .serviceInstancesUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/service_instances")
                    .applicationEventsUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/app_events")
                    .eventsUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/events")
                    .securityGroupsUrl("/v2/spaces/b6d11f17-1cea-4c00-a951-fef3223b8c84/security_groups")
                    .build())
                .build();
        }

        @Override
        protected AssociateSpaceDeveloperByUsernameRequest getValidRequest() throws Exception {
            return AssociateSpaceDeveloperByUsernameRequest.builder()
                .spaceId("test-space-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<AssociateSpaceDeveloperByUsernameResponse> invoke(AssociateSpaceDeveloperByUsernameRequest request) {
            return this.spaces.associateDeveloperByUsername(request);
        }

    }

    public static final class AssociateSpaceManagerByUsername extends AbstractApiTest<AssociateSpaceManagerByUsernameRequest, AssociateSpaceManagerByUsernameResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateSpaceManagerByUsernameRequest getInvalidRequest() {
            return AssociateSpaceManagerByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("v2/spaces/test-space-id/managers")
                .requestPayload("v2/spaces/PUT_{id}_managers_request.json")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_managers_response.json");
        }

        @Override
        protected AssociateSpaceManagerByUsernameResponse getResponse() {
            return AssociateSpaceManagerByUsernameResponse.builder()
                .metadata(Metadata.builder()
                    .id("4351f97b-3485-4738-821b-5bf77bed44eb")
                    .url("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb")
                    .createdAt("2015-11-30T23:38:28Z")
                    .build())
                .entity(SpaceEntity.builder()
                    .name("name-98")
                    .organizationId("a488910d-2d69-46a2-bf6e-319248e03705")
                    .allowSsh(true)
                    .organizationUrl("/v2/organizations/a488910d-2d69-46a2-bf6e-319248e03705")
                    .developersUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/developers")
                    .managersUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/managers")
                    .auditorsUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/auditors")
                    .applicationsUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/apps")
                    .routesUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/routes")
                    .domainsUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/domains")
                    .serviceInstancesUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/service_instances")
                    .applicationEventsUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/app_events")
                    .eventsUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/events")
                    .securityGroupsUrl("/v2/spaces/4351f97b-3485-4738-821b-5bf77bed44eb/security_groups")
                    .build())
                .build();
        }

        @Override
        protected AssociateSpaceManagerByUsernameRequest getValidRequest() throws Exception {
            return AssociateSpaceManagerByUsernameRequest.builder()
                .spaceId("test-space-id")
                .username("user@example.com")
                .build();
        }

        @Override
        protected Mono<AssociateSpaceManagerByUsernameResponse> invoke(AssociateSpaceManagerByUsernameRequest request) {
            return this.spaces.associateManagerByUsername(request);
        }

    }

    public static final class Create extends AbstractApiTest<CreateSpaceRequest, CreateSpaceResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateSpaceRequest getInvalidRequest() {
            return CreateSpaceRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/spaces")
                .requestPayload("v2/spaces/POST_request.json")
                .status(OK)
                .responsePayload("v2/spaces/POST_response.json");
        }

        @Override
        protected CreateSpaceResponse getResponse() {
            return CreateSpaceResponse.builder()
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
        }

        @Override
        protected CreateSpaceRequest getValidRequest() throws Exception {
            return CreateSpaceRequest.builder()
                .name("development")
                .organizationId("c523070c-3006-4715-86dd-414afaecd949")
                .build();
        }

        @Override
        protected Mono<CreateSpaceResponse> invoke(CreateSpaceRequest request) {
            return this.spaces.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteSpaceRequest, DeleteSpaceResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteSpaceRequest getInvalidRequest() {
            return DeleteSpaceRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id")
                .status(NO_CONTENT);
        }

        @Override
        protected DeleteSpaceResponse getResponse() {
            return null;
        }

        @Override
        protected DeleteSpaceRequest getValidRequest() throws Exception {
            return DeleteSpaceRequest.builder()
                .spaceId("test-space-id")
                .build();
        }

        @Override
        protected Mono<DeleteSpaceResponse> invoke(DeleteSpaceRequest request) {
            return this.spaces.delete(request);
        }

    }

    public static final class DeleteAsync extends AbstractApiTest<DeleteSpaceRequest, DeleteSpaceResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteSpaceRequest getInvalidRequest() {
            return DeleteSpaceRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id?async=true")
                .status(ACCEPTED)
                .responsePayload("v2/routes/DELETE_{id}_async_response.json");
        }

        @Override
        protected DeleteSpaceResponse getResponse() {
            return DeleteSpaceResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected DeleteSpaceRequest getValidRequest() throws Exception {
            return DeleteSpaceRequest.builder()
                .async(true)
                .spaceId("test-space-id")
                .build();
        }

        @Override
        protected Mono<DeleteSpaceResponse> invoke(DeleteSpaceRequest request) {
            return this.spaces.delete(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetSpaceRequest, GetSpaceResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetSpaceRequest getInvalidRequest() {
            return GetSpaceRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/spaces/test-space-id")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_response.json");
        }

        @Override
        protected GetSpaceResponse getResponse() {
            return GetSpaceResponse.builder()
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
        }

        @Override
        protected GetSpaceRequest getValidRequest() throws Exception {
            return GetSpaceRequest.builder()
                .spaceId("test-space-id")
                .build();
        }

        @Override
        protected Mono<GetSpaceResponse> invoke(GetSpaceRequest request) {
            return this.spaces.get(request);
        }

    }

    public static final class GetSummary extends AbstractApiTest<GetSpaceSummaryRequest, GetSpaceSummaryResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetSpaceSummaryRequest getInvalidRequest() {
            return GetSpaceSummaryRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/spaces/test-space-id/summary")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_summary_response.json");
        }

        @Override
        protected GetSpaceSummaryResponse getResponse() {
            return GetSpaceSummaryResponse.builder()
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
        }

        @Override
        protected GetSpaceSummaryRequest getValidRequest() throws Exception {
            return GetSpaceSummaryRequest.builder()
                .spaceId("test-space-id")
                .build();
        }

        @Override
        protected Mono<GetSpaceSummaryResponse> invoke(GetSpaceSummaryRequest request) {
            return this.spaces.getSummary(request);
        }

    }

    public static final class List extends AbstractApiTest<ListSpacesRequest, ListSpacesResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpacesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/spaces?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_response.json");
        }

        @Override
        protected ListSpacesResponse getResponse() {
            return ListSpacesResponse.builder()
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
                        .serviceInstancesUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/service_instances")
                        .applicationEventsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/app_events")
                        .eventsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/events")
                        .securityGroupsUrl("/v2/spaces/b4293b09-8316-472c-a29a-6468a3adff59/security_groups")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSpacesRequest getValidRequest() throws Exception {
            return ListSpacesRequest.builder()
                .name("test-name")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpacesResponse> invoke(ListSpacesRequest request) {
            return this.spaces.list(request);
        }

    }

    public static final class ListApplications extends AbstractApiTest<ListSpaceApplicationsRequest, ListSpaceApplicationsResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceApplicationsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/spaces/test-space-id/apps?q=name%20IN%20test-name&page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_apps_response.json");
        }

        @Override
        protected ListSpaceApplicationsResponse getResponse() {
            return ListSpaceApplicationsResponse.builder()
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
        }

        @Override
        protected ListSpaceApplicationsRequest getValidRequest() throws Exception {
            return ListSpaceApplicationsRequest.builder()
                .spaceId("test-space-id")
                .name("test-name")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceApplicationsResponse> invoke(ListSpaceApplicationsRequest request) {
            return this.spaces.listApplications(request);
        }

    }

    public static final class ListAuditors extends AbstractApiTest<ListSpaceAuditorsRequest, ListSpaceAuditorsResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceAuditorsRequest getInvalidRequest() {
            return ListSpaceAuditorsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/auditors?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_auditors_response.json");
        }

        @Override
        protected ListSpaceAuditorsResponse getResponse() {
            return ListSpaceAuditorsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserResource.builder()
                    .metadata(Metadata.builder()
                        .id("uaa-id-15")
                        .url("/v2/users/uaa-id-15")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                    .entity(UserEntity.builder()
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
        }

        @Override
        protected ListSpaceAuditorsRequest getValidRequest() throws Exception {
            return ListSpaceAuditorsRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceAuditorsResponse> invoke(ListSpaceAuditorsRequest request) {
            return this.spaces.listAuditors(request);
        }
    }

    public static final class ListDevelopers extends AbstractApiTest<ListSpaceDevelopersRequest, ListSpaceDevelopersResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceDevelopersRequest getInvalidRequest() {
            return ListSpaceDevelopersRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/developers?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_developers_response.json");
        }

        @Override
        protected ListSpaceDevelopersResponse getResponse() {
            return ListSpaceDevelopersResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserResource.builder()
                    .metadata(Metadata.builder()
                        .id("uaa-id-24")
                        .url("/v2/users/uaa-id-24")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                    .entity(UserEntity.builder()
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
        }

        @Override
        protected ListSpaceDevelopersRequest getValidRequest() throws Exception {
            return ListSpaceDevelopersRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceDevelopersResponse> invoke(ListSpaceDevelopersRequest request) {
            return this.spaces.listDevelopers(request);
        }

    }

    public static final class ListDomains extends AbstractApiTest<ListSpaceDomainsRequest, ListSpaceDomainsResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceDomainsRequest getInvalidRequest() {
            return ListSpaceDomainsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/domains?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_domains_response.json");
        }

        @Override
        protected ListSpaceDomainsResponse getResponse() {
            return ListSpaceDomainsResponse.builder()
                .totalResults(2)
                .totalPages(1)
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("08ac844a-e880-48ef-a90c-f95131582fcc")
                        .url("/v2/domains/08ac844a-e880-48ef-a90c-f95131582fcc")
                        .createdAt("2015-07-27T22:43:05Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("customer-app-domain1.com")
                        .build())
                    .build())
                .resource(DomainResource.builder()
                    .metadata(Metadata.builder()
                        .id("973dcea1-5011-4bd0-aa9e-fa232bfaada7")
                        .url("/v2/domains/973dcea1-5011-4bd0-aa9e-fa232bfaada7")
                        .createdAt("2015-07-27T22:43:05Z")
                        .build())
                    .entity(DomainEntity.builder()
                        .name("customer-app-domain2.com")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSpaceDomainsRequest getValidRequest() throws Exception {
            return ListSpaceDomainsRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceDomainsResponse> invoke(ListSpaceDomainsRequest request) {
            return this.spaces.listDomains(request);
        }
    }

    public static final class ListEvents extends AbstractApiTest<ListSpaceEventsRequest, ListSpaceEventsResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceEventsRequest getInvalidRequest() {
            return ListSpaceEventsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/events?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_events_response.json");
        }

        @Override
        protected ListSpaceEventsResponse getResponse() {
            return ListSpaceEventsResponse.builder()
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
                        .metadatas(StringMap.builder()
                            .entry("request", StringMap.builder()
                                .entry("name", "new_name")
                                .build())
                            .build())
                        .spaceId("33d44b03-6203-47a7-b71c-9bf6fcaeb54a")
                        .organizationId("ab7dff90-0bc7-4ce0-be5b-b8ecc676bc4a")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSpaceEventsRequest getValidRequest() throws Exception {
            return ListSpaceEventsRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceEventsResponse> invoke(ListSpaceEventsRequest request) {
            return this.spaces.listEvents(request);
        }

    }

    public static final class ListManagers extends AbstractApiTest<ListSpaceManagersRequest, ListSpaceManagersResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceManagersRequest getInvalidRequest() {
            return ListSpaceManagersRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/managers?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_managers_response.json");
        }

        @Override
        protected ListSpaceManagersResponse getResponse() {
            return ListSpaceManagersResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserResource.builder()
                    .metadata(Metadata.builder()
                        .id("uaa-id-35")
                        .url("/v2/users/uaa-id-35")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                    .entity(UserEntity.builder()
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
        }

        @Override
        protected ListSpaceManagersRequest getValidRequest() throws Exception {
            return ListSpaceManagersRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceManagersResponse> invoke(ListSpaceManagersRequest request) {
            return this.spaces.listManagers(request);
        }

    }

    public static final class ListRoutes extends AbstractApiTest<ListSpaceRoutesRequest, ListSpaceRoutesResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceRoutesRequest getInvalidRequest() {
            return ListSpaceRoutesRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/routes?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_routes_response.json");
        }

        @Override
        protected ListSpaceRoutesResponse getResponse() {
            return ListSpaceRoutesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(RouteResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2015-07-27T22:43:07Z")
                        .id("cab98364-2ccb-42e0-b901-765c4e915d49")
                        .url("/v2/routes/cab98364-2ccb-42e0-b901-765c4e915d49")
                        .build())
                    .entity(RouteEntity.builder()
                        .applicationsUrl("/v2/routes/cab98364-2ccb-42e0-b901-765c4e915d49/apps")
                        .domainId("64aaa8e0-af71-4d7e-afef-e2efdbd66552")
                        .domainUrl("/v2/domains/64aaa8e0-af71-4d7e-afef-e2efdbd66552")
                        .host("host-1")
                        .path("")
                        .spaceId("8af29896-2f0d-42b4-a24c-169343b2aad9")
                        .spaceUrl("/v2/spaces/8af29896-2f0d-42b4-a24c-169343b2aad9")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSpaceRoutesRequest getValidRequest() throws Exception {
            return ListSpaceRoutesRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceRoutesResponse> invoke(ListSpaceRoutesRequest request) {
            return this.spaces.listRoutes(request);
        }

    }

    public static final class ListSecurityGroups extends AbstractApiTest<ListSpaceSecurityGroupsRequest, ListSpaceSecurityGroupsResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceSecurityGroupsRequest getInvalidRequest() {
            return ListSpaceSecurityGroupsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/security_groups?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_security_groups_response.json");
        }

        @Override
        protected ListSpaceSecurityGroupsResponse getResponse() {
            return ListSpaceSecurityGroupsResponse.builder()
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
        }

        @Override
        protected ListSpaceSecurityGroupsRequest getValidRequest() throws Exception {
            return ListSpaceSecurityGroupsRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceSecurityGroupsResponse> invoke(ListSpaceSecurityGroupsRequest request) {
            return this.spaces.listSecurityGroups(request);
        }

    }

    public static final class ListServiceInstances extends AbstractApiTest<ListSpaceServiceInstancesRequest, ListSpaceServiceInstancesResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceServiceInstancesRequest getInvalidRequest() {
            return ListSpaceServiceInstancesRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/service_instances?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_service_instances_response.json");
        }

        @Override
        protected ListSpaceServiceInstancesResponse getResponse() {
            return ListSpaceServiceInstancesResponse.builder()
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
        }

        @Override
        protected ListSpaceServiceInstancesRequest getValidRequest() throws Exception {
            return ListSpaceServiceInstancesRequest.builder()
                .spaceId("test-space-id")
                .returnUserProvidedServiceInstances(true)
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceServiceInstancesResponse> invoke(ListSpaceServiceInstancesRequest request) {
            return this.spaces.listServiceInstances(request);
        }

    }

    public static final class ListServices extends AbstractApiTest<ListSpaceServicesRequest, ListSpaceServicesResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceServicesRequest getInvalidRequest() {
            return ListSpaceServicesRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/services?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_services_response.json");
        }

        @Override
        protected ListSpaceServicesResponse getResponse() {
            return ListSpaceServicesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceResource.builder()
                    .metadata(Metadata.builder()
                        .id("fcc4261f-da9a-40ba-9194-6919e0ab87f8")
                        .url("/v2/services/fcc4261f-da9a-40ba-9194-6919e0ab87f8")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                    .entity(ServiceEntity.builder()
                        .label("label-5")
                        .description("desc-14")
                        .active(true)
                        .bindable(true)
                        .uniqueId("666902ad-81dc-41e9-a351-58e1055e3ab2")
                        .serviceBrokerId("15f1c3a0-910c-4b92-9386-377acada14cb")
                        .planUpdateable(false)
                        .servicePlansUrl("/v2/services/fcc4261f-da9a-40ba-9194-6919e0ab87f8/service_plans")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListSpaceServicesRequest getValidRequest() throws Exception {
            return ListSpaceServicesRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceServicesResponse> invoke(ListSpaceServicesRequest request) {
            return this.spaces.listServices(request);
        }

    }

    public static final class ListUserRoles extends AbstractApiTest<ListSpaceUserRolesRequest, ListSpaceUserRolesResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListSpaceUserRolesRequest getInvalidRequest() {
            return ListSpaceUserRolesRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("v2/spaces/test-space-id/user_roles?page=-1")
                .status(OK)
                .responsePayload("v2/spaces/GET_{id}_user_roles_response.json");
        }

        @Override
        protected ListSpaceUserRolesResponse getResponse() {
            return ListSpaceUserRolesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(UserSpaceRoleResource.builder()
                    .metadata(Metadata.builder()
                        .id("uaa-id-8")
                        .url("/v2/users/uaa-id-8")
                        .createdAt("2015-07-27T22:43:07Z")
                        .build())
                    .entity(UserSpaceRoleEntity.builder()
                        .admin(false)
                        .active(false)
                        .defaultSpaceId(null)
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
        }

        @Override
        protected ListSpaceUserRolesRequest getValidRequest() throws Exception {
            return ListSpaceUserRolesRequest.builder()
                .spaceId("test-space-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListSpaceUserRolesResponse> invoke(ListSpaceUserRolesRequest request) {
            return this.spaces.listUserRoles(request);
        }

    }

    public static final class RemoveAuditor extends AbstractApiTest<RemoveSpaceAuditorRequest, Void> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveSpaceAuditorRequest getInvalidRequest() {
            return RemoveSpaceAuditorRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id/auditors/test-auditor-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveSpaceAuditorRequest getValidRequest() throws Exception {
            return RemoveSpaceAuditorRequest.builder()
                .auditorId("test-auditor-id")
                .spaceId("test-space-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveSpaceAuditorRequest request) {
            return this.spaces.removeAuditor(request);
        }

    }

    public static final class RemoveAuditorByUsername extends AbstractApiTest<RemoveSpaceAuditorByUsernameRequest, Void> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveSpaceAuditorByUsernameRequest getInvalidRequest() {
            return RemoveSpaceAuditorByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id/auditors")
                .requestPayload("v2/spaces/DELETE_{id}_auditors_request.json")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveSpaceAuditorByUsernameRequest getValidRequest() throws Exception {
            return RemoveSpaceAuditorByUsernameRequest.builder()
                .spaceId("test-space-id")
                .username("auditor@example.com")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveSpaceAuditorByUsernameRequest request) {
            return this.spaces.removeAuditorByUsername(request);
        }

    }

    public static final class RemoveDeveloper extends AbstractApiTest<RemoveSpaceDeveloperRequest, Void> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveSpaceDeveloperRequest getInvalidRequest() {
            return RemoveSpaceDeveloperRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id/developers/test-developer-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveSpaceDeveloperRequest getValidRequest() throws Exception {
            return RemoveSpaceDeveloperRequest.builder()
                .developerId("test-developer-id")
                .spaceId("test-space-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveSpaceDeveloperRequest request) {
            return this.spaces.removeDeveloper(request);
        }
    }

    public static final class RemoveDeveloperByUsername extends AbstractApiTest<RemoveSpaceDeveloperByUsernameRequest, Void> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveSpaceDeveloperByUsernameRequest getInvalidRequest() {
            return RemoveSpaceDeveloperByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id/developers")
                .requestPayload("v2/spaces/DELETE_{id}_developers_request.json")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveSpaceDeveloperByUsernameRequest getValidRequest() throws Exception {
            return RemoveSpaceDeveloperByUsernameRequest.builder()
                .spaceId("test-space-id")
                .username("developer@example.com")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveSpaceDeveloperByUsernameRequest request) {
            return this.spaces.removeDeveloperByUsername(request);
        }

    }

    public static final class RemoveManager extends AbstractApiTest<RemoveSpaceManagerRequest, Void> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveSpaceManagerRequest getInvalidRequest() {
            return RemoveSpaceManagerRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id/managers/test-manager-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveSpaceManagerRequest getValidRequest() throws Exception {
            return RemoveSpaceManagerRequest.builder()
                .spaceId("test-space-id")
                .managerId("test-manager-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveSpaceManagerRequest request) {
            return this.spaces.removeManager(request);
        }

    }

    public static final class RemoveManagerByUsername extends AbstractApiTest<RemoveSpaceManagerByUsernameRequest, Void> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveSpaceManagerByUsernameRequest getInvalidRequest() {
            return RemoveSpaceManagerByUsernameRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id/managers")
                .requestPayload("v2/spaces/DELETE_{id}_managers_request.json")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveSpaceManagerByUsernameRequest getValidRequest() throws Exception {
            return RemoveSpaceManagerByUsernameRequest.builder()
                .spaceId("test-space-id")
                .username("manager@example.com")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveSpaceManagerByUsernameRequest request) {
            return this.spaces.removeManagerByUsername(request);
        }

    }

    public static final class RemoveSecurityGroup extends AbstractApiTest<RemoveSpaceSecurityGroupRequest, Void> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveSpaceSecurityGroupRequest getInvalidRequest() {
            return RemoveSpaceSecurityGroupRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("v2/spaces/test-space-id/security_groups/test-security-group-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveSpaceSecurityGroupRequest getValidRequest() throws Exception {
            return RemoveSpaceSecurityGroupRequest.builder()
                .spaceId("test-space-id")
                .securityGroupId("test-security-group-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveSpaceSecurityGroupRequest request) {
            return this.spaces.removeSecurityGroup(request);
        }

    }

    public static final class Update extends AbstractApiTest<UpdateSpaceRequest, UpdateSpaceResponse> {

        private final SpringSpaces spaces = new SpringSpaces(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateSpaceRequest getInvalidRequest() {
            return UpdateSpaceRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("v2/spaces/test-space-id")
                .requestPayload("v2/spaces/PUT_{id}_request.json")
                .status(OK)
                .responsePayload("v2/spaces/PUT_{id}_response.json");
        }

        @Override
        protected UpdateSpaceResponse getResponse() {
            return UpdateSpaceResponse.builder()
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
        }

        @Override
        protected UpdateSpaceRequest getValidRequest() throws Exception {
            return UpdateSpaceRequest.builder()
                .spaceId("test-space-id")
                .name("New Space Name")
                .build();
        }

        @Override
        protected Mono<UpdateSpaceResponse> invoke(UpdateSpaceRequest request) {
            return this.spaces.update(request);
        }

    }

}
