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

package org.cloudfoundry.reactor.client.v2.securitygroups;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroups;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultResponse;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Reactor-based implementation of {@link SecurityGroups}
 */
public class ReactorSecurityGroups extends AbstractClientV2Operations implements SecurityGroups {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorSecurityGroups(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<CreateSecurityGroupResponse> create(CreateSecurityGroupRequest request) {
        return post(request, CreateSecurityGroupResponse.class, builder -> builder.pathSegment("v2", "security_groups"));
    }

    @Override
    public Mono<DeleteSecurityGroupResponse> delete(DeleteSecurityGroupRequest request) {
        return delete(request, DeleteSecurityGroupResponse.class, builder -> builder.pathSegment("v2", "security_groups", request.getSecurityGroupId()));
    }

    @Override
    public Mono<Void> deleteRunningDefault(DeleteSecurityGroupRunningDefaultRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "config", "running_security_groups", request.getSecurityGroupRunningDefaultId()));
    }

    @Override
    public Mono<Void> deleteStagingDefault(DeleteSecurityGroupStagingDefaultRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "config", "staging_security_groups", request.getSecurityGroupStagingDefaultId()));
    }

    @Override
    public Mono<ListSecurityGroupsResponse> list(ListSecurityGroupsRequest request) {
        return get(request, ListSecurityGroupsResponse.class, builder -> builder.pathSegment("v2", "security_groups"));
    }

    @Override
    public Mono<ListSecurityGroupRunningDefaultsResponse> listRunningDefaults(ListSecurityGroupRunningDefaultsRequest request) {
        return get(request, ListSecurityGroupRunningDefaultsResponse.class, builder -> builder.pathSegment("v2", "config", "running_security_groups"));
    }

    @Override
    public Mono<ListSecurityGroupStagingDefaultsResponse> listStagingDefaults(ListSecurityGroupStagingDefaultsRequest request) {
        return get(request, ListSecurityGroupStagingDefaultsResponse.class, builder -> builder.pathSegment("v2", "config", "staging_security_groups"));
    }

    @Override
    public Mono<SetSecurityGroupRunningDefaultResponse> setRunningDefault(SetSecurityGroupRunningDefaultRequest request) {
        return put(request, SetSecurityGroupRunningDefaultResponse.class, builder -> builder.pathSegment("v2", "config", "running_security_groups", request.getSecurityGroupRunningDefaultId()));
    }

    @Override
    public Mono<SetSecurityGroupStagingDefaultResponse> setStagingDefault(SetSecurityGroupStagingDefaultRequest request) {
        return put(request, SetSecurityGroupStagingDefaultResponse.class, builder -> builder.pathSegment("v2", "config", "staging_security_groups", request.getSecurityGroupStagingDefaultId()));
    }
}
