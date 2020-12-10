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

package org.cloudfoundry.reactor.client.v3.roles;

import org.cloudfoundry.client.v3.roles.CreateRoleRequest;
import org.cloudfoundry.client.v3.roles.CreateRoleResponse;
import org.cloudfoundry.client.v3.roles.DeleteRoleRequest;
import org.cloudfoundry.client.v3.roles.GetRoleRequest;
import org.cloudfoundry.client.v3.roles.GetRoleResponse;
import org.cloudfoundry.client.v3.roles.ListRolesRequest;
import org.cloudfoundry.client.v3.roles.ListRolesResponse;
import org.cloudfoundry.client.v3.roles.RolesV3;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link RolesV3}
 */
public final class ReactorRolesV3 extends AbstractClientV3Operations implements RolesV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorRolesV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateRoleResponse> create(CreateRoleRequest request) {
        return post(request, CreateRoleResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("roles"))
            .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteRoleRequest request) {
        return delete(request, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("roles", request.getRoleId()))
            .checkpoint();
    }

    @Override
    public Mono<GetRoleResponse> get(GetRoleRequest request) {
        return get(request, GetRoleResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("roles", request.getRoleId()))
            .checkpoint();
    }

    @Override
    public Mono<ListRolesResponse> list(ListRolesRequest request) {
        return get(request, ListRolesResponse.class, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("roles"))
            .checkpoint();
    }

}
