/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.securitygroups;

import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceResponse;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.GetSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.GetSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupSpacesRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupSpacesResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsResponse;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroups;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.UpdateSecurityGroupResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link SecurityGroups}
 */
public class ReactorSecurityGroups extends AbstractClientV2Operations implements SecurityGroups {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorSecurityGroups(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<AssociateSecurityGroupSpaceResponse> associateSpace(AssociateSecurityGroupSpaceRequest request) {
        return put(request, AssociateSecurityGroupSpaceResponse.class, builder -> builder.pathSegment("security_groups", request.getSecurityGroupId(), "spaces", request.getSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<CreateSecurityGroupResponse> create(CreateSecurityGroupRequest request) {
        return post(request, CreateSecurityGroupResponse.class, builder -> builder.pathSegment("security_groups"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteSecurityGroupResponse> delete(DeleteSecurityGroupRequest request) {
        return delete(request, DeleteSecurityGroupResponse.class, builder -> builder.pathSegment("security_groups", request.getSecurityGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<GetSecurityGroupResponse> get(GetSecurityGroupRequest request) {
        return get(request, GetSecurityGroupResponse.class, builder -> builder.pathSegment("security_groups", request.getSecurityGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<ListSecurityGroupsResponse> list(ListSecurityGroupsRequest request) {
        return get(request, ListSecurityGroupsResponse.class, builder -> builder.pathSegment("security_groups"))
            .checkpoint();
    }

    @Override
    public Mono<ListSecurityGroupRunningDefaultsResponse> listRunningDefaults(ListSecurityGroupRunningDefaultsRequest request) {
        return get(request, ListSecurityGroupRunningDefaultsResponse.class, builder -> builder.pathSegment("config", "running_security_groups"))
            .checkpoint();
    }

    @Override
    public Mono<ListSecurityGroupSpacesResponse> listSpaces(ListSecurityGroupSpacesRequest request) {
        return get(request, ListSecurityGroupSpacesResponse.class, builder -> builder.pathSegment("security_groups", request.getSecurityGroupId(), "spaces"))
            .checkpoint();
    }

    @Override
    public Mono<ListSecurityGroupStagingDefaultsResponse> listStagingDefaults(ListSecurityGroupStagingDefaultsRequest request) {
        return get(request, ListSecurityGroupStagingDefaultsResponse.class, builder -> builder.pathSegment("config", "staging_security_groups"))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeRunningDefault(RemoveSecurityGroupRunningDefaultRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("config", "running_security_groups", request.getSecurityGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeSpace(RemoveSecurityGroupSpaceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("security_groups", request.getSecurityGroupId(), "spaces", request.getSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<Void> removeStagingDefault(RemoveSecurityGroupStagingDefaultRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("config", "staging_security_groups", request.getSecurityGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<SetSecurityGroupRunningDefaultResponse> setRunningDefault(SetSecurityGroupRunningDefaultRequest request) {
        return put(request, SetSecurityGroupRunningDefaultResponse.class, builder -> builder.pathSegment("config", "running_security_groups", request.getSecurityGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<SetSecurityGroupStagingDefaultResponse> setStagingDefault(SetSecurityGroupStagingDefaultRequest request) {
        return put(request, SetSecurityGroupStagingDefaultResponse.class, builder -> builder.pathSegment("config", "staging_security_groups", request.getSecurityGroupId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateSecurityGroupResponse> update(UpdateSecurityGroupRequest request) {
        return put(request, UpdateSecurityGroupResponse.class, builder -> builder.pathSegment("security_groups", request.getSecurityGroupId()))
            .checkpoint();
    }

}
