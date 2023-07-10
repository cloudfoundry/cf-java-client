/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.securitygroups;

import org.cloudfoundry.client.v3.securitygroups.SecurityGroupsV3;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v3.securitygroups.GetSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.GetSecurityGroupResponse;
import org.cloudfoundry.client.v3.securitygroups.UpdateSecurityGroupResponse;
import org.cloudfoundry.client.v3.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v3.securitygroups.ListSecurityGroupsResponse;
import org.cloudfoundry.client.v3.securitygroups.BindRunningSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.BindRunningSecurityGroupResponse;
import org.cloudfoundry.client.v3.securitygroups.BindStagingSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.BindStagingSecurityGroupResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServiceBindingsV3}
 */
public final class ReactorSecurityGroupsV3 extends AbstractClientV3Operations implements SecurityGroupsV3 {

        /**
         * Creates an instance
         *
         * @param connectionContext the {@link ConnectionContext} to use when
         *                          communicating with the server
         * @param root              the root URI of the server. Typically something like
         *                          {@code https://api.run.pivotal.io}.
         * @param tokenProvider     the {@link TokenProvider} to use when communicating
         *                          with the server
         * @param requestTags       map with custom http headers which will be added to
         *                          web request
         */
        public ReactorSecurityGroupsV3(ConnectionContext connectionContext, Mono<String> root,
                        TokenProvider tokenProvider,
                        Map<String, String> requestTags) {
                super(connectionContext, root, tokenProvider, requestTags);
        }

        @Override
        public Mono<CreateSecurityGroupResponse> create(CreateSecurityGroupRequest request) {
                return post(request, CreateSecurityGroupResponse.class,
                                builder -> builder.pathSegment("security_groups"))
                                .checkpoint();

        }

        @Override
        public Mono<GetSecurityGroupResponse> get(GetSecurityGroupRequest request) {
                return get(request, GetSecurityGroupResponse.class,
                                builder -> builder.pathSegment("security_groups", request.getSecurityGroupId()))
                                .checkpoint();

        }

        @Override
        public Mono<ListSecurityGroupsResponse> list(ListSecurityGroupsRequest request) {
                return get(request, ListSecurityGroupsResponse.class,
                                builder -> builder.pathSegment("security_groups"))
                                .checkpoint();
        }

        @Override
        public Mono<UpdateSecurityGroupResponse> update(UpdateSecurityGroupRequest request) {
                return patch(request, UpdateSecurityGroupResponse.class,
                                builder -> builder.pathSegment("security_groups", request.getSecurityGroupId()))
                                .checkpoint();
        }

        @Override
        public Mono<String> delete(DeleteSecurityGroupRequest request) {
                return delete(request,
                                builder -> builder.pathSegment("security_groups", request.getSecurityGroupId()))
                                .checkpoint();

        }

        @Override
        public Mono<BindRunningSecurityGroupResponse> bindRunningSecurityGroup(
                        BindRunningSecurityGroupRequest request) {
                return post(request, BindRunningSecurityGroupResponse.class,
                                builder -> builder.pathSegment("security_groups", request.getSecurityGroupId(),
                                                "relationships",
                                                "running_spaces"))
                                .checkpoint();
        }

        @Override
        public Mono<BindStagingSecurityGroupResponse> bindStagingSecurityGroup(
                        BindStagingSecurityGroupRequest request) {
                return post(request, BindStagingSecurityGroupResponse.class,
                                builder -> builder.pathSegment("security_groups", request.getSecurityGroupId(),
                                                "relationships",
                                                "staging_spaces"))
                                .checkpoint();
        }
}
