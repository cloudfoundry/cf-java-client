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

package org.cloudfoundry.reactor.client.v2.spaces;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link Spaces}
 */
public final class ReactorSpaces extends AbstractClientV2Operations implements Spaces {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorSpaces(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<AssociateSpaceAuditorResponse> associateAuditor(AssociateSpaceAuditorRequest request) {
        return put(request, AssociateSpaceAuditorResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "auditors", validRequest.getAuditorId())));
    }

    @Override
    public Mono<AssociateSpaceAuditorByUsernameResponse> associateAuditorByUsername(AssociateSpaceAuditorByUsernameRequest request) {
        return put(request, AssociateSpaceAuditorByUsernameResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "auditors")));
    }

    @Override
    public Mono<AssociateSpaceDeveloperResponse> associateDeveloper(AssociateSpaceDeveloperRequest request) {
        return put(request, AssociateSpaceDeveloperResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "developers", validRequest.getDeveloperId())));
    }

    @Override
    public Mono<AssociateSpaceDeveloperByUsernameResponse> associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest request) {
        return put(request, AssociateSpaceDeveloperByUsernameResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "developers")));
    }

    @Override
    public Mono<AssociateSpaceManagerResponse> associateManager(AssociateSpaceManagerRequest request) {
        return put(request, AssociateSpaceManagerResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "managers", validRequest.getManagerId())));
    }

    @Override
    public Mono<AssociateSpaceManagerByUsernameResponse> associateManagerByUsername(AssociateSpaceManagerByUsernameRequest request) {
        return put(request, AssociateSpaceManagerByUsernameResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "managers")));
    }

    @Override
    public Mono<AssociateSpaceSecurityGroupResponse> associateSecurityGroup(AssociateSpaceSecurityGroupRequest request) {
        return put(request, AssociateSpaceSecurityGroupResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "security_groups", validRequest.getSecurityGroupId())));
    }

    @Override
    public Mono<CreateSpaceResponse> create(CreateSpaceRequest request) {
        return post(request, CreateSpaceResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces")));
    }

    @Override
    public Mono<DeleteSpaceResponse> delete(DeleteSpaceRequest request) {
        return delete(request, DeleteSpaceResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId())));
    }

    @Override
    public Mono<GetSpaceResponse> get(GetSpaceRequest request) {
        return get(request, GetSpaceResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId())));
    }

    @Override
    public Mono<GetSpaceSummaryResponse> getSummary(GetSpaceSummaryRequest request) {
        return get(request, GetSpaceSummaryResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "summary")));
    }

    @Override
    public Mono<ListSpacesResponse> list(ListSpacesRequest request) {
        return get(request, ListSpacesResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces")));
    }

    @Override
    public Mono<ListSpaceApplicationsResponse> listApplications(ListSpaceApplicationsRequest request) {
        return get(request, ListSpaceApplicationsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "apps")));
    }

    @Override
    public Mono<ListSpaceAuditorsResponse> listAuditors(ListSpaceAuditorsRequest request) {
        return get(request, ListSpaceAuditorsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "auditors")));
    }

    @Override
    public Mono<ListSpaceDevelopersResponse> listDevelopers(ListSpaceDevelopersRequest request) {
        return get(request, ListSpaceDevelopersResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "developers")));
    }

    @Override
    public Mono<ListSpaceDomainsResponse> listDomains(ListSpaceDomainsRequest request) {
        return get(request, ListSpaceDomainsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "domains")));
    }

    @Override
    public Mono<ListSpaceEventsResponse> listEvents(ListSpaceEventsRequest request) {
        return get(request, ListSpaceEventsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "events")));
    }

    @Override
    public Mono<ListSpaceManagersResponse> listManagers(ListSpaceManagersRequest request) {
        return get(request, ListSpaceManagersResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "managers")));
    }

    @Override
    public Mono<ListSpaceRoutesResponse> listRoutes(ListSpaceRoutesRequest request) {
        return get(request, ListSpaceRoutesResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "routes")));
    }

    @Override
    public Mono<ListSpaceSecurityGroupsResponse> listSecurityGroups(ListSpaceSecurityGroupsRequest request) {
        return get(request, ListSpaceSecurityGroupsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "security_groups")));
    }

    @Override
    public Mono<ListSpaceServiceInstancesResponse> listServiceInstances(ListSpaceServiceInstancesRequest request) {
        return get(request, ListSpaceServiceInstancesResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "service_instances")));
    }

    @Override
    public Mono<ListSpaceServicesResponse> listServices(ListSpaceServicesRequest request) {
        return get(request, ListSpaceServicesResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "services")));
    }

    @Override
    public Mono<ListSpaceUserRolesResponse> listUserRoles(ListSpaceUserRolesRequest request) {
        return get(request, ListSpaceUserRolesResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "user_roles")));
    }

    @Override
    public Mono<Void> removeAuditor(RemoveSpaceAuditorRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "auditors", validRequest.getAuditorId())));
    }

    @Override
    public Mono<RemoveSpaceAuditorByUsernameResponse> removeAuditorByUsername(RemoveSpaceAuditorByUsernameRequest request) {
        return delete(request, RemoveSpaceAuditorByUsernameResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "auditors")));
    }

    @Override
    public Mono<Void> removeDeveloper(RemoveSpaceDeveloperRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "developers", validRequest.getDeveloperId())));
    }

    @Override
    public Mono<RemoveSpaceDeveloperByUsernameResponse> removeDeveloperByUsername(RemoveSpaceDeveloperByUsernameRequest request) {
        return delete(request, RemoveSpaceDeveloperByUsernameResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "developers")));
    }

    @Override
    public Mono<Void> removeManager(RemoveSpaceManagerRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "managers", validRequest.getManagerId())));
    }

    @Override
    public Mono<RemoveSpaceManagerByUsernameResponse> removeManagerByUsername(RemoveSpaceManagerByUsernameRequest request) {
        return delete(request, RemoveSpaceManagerByUsernameResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "managers")));
    }

    @Override
    public Mono<Void> removeSecurityGroup(RemoveSpaceSecurityGroupRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId(), "security_groups", validRequest.getSecurityGroupId())));
    }

    @Override
    public Mono<UpdateSpaceResponse> update(UpdateSpaceRequest request) {
        return put(request, UpdateSpaceResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "spaces", validRequest.getSpaceId())));
    }

}
