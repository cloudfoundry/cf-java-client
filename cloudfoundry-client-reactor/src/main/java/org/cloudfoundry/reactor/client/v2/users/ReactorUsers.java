/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.users;

import org.cloudfoundry.client.v2.users.AssociateUserSpaceRequest;
import org.cloudfoundry.client.v2.users.AssociateUserSpaceResponse;
import org.cloudfoundry.client.v2.users.CreateUserRequest;
import org.cloudfoundry.client.v2.users.CreateUserResponse;
import org.cloudfoundry.client.v2.users.DeleteUserRequest;
import org.cloudfoundry.client.v2.users.DeleteUserResponse;
import org.cloudfoundry.client.v2.users.GetUserRequest;
import org.cloudfoundry.client.v2.users.GetUserResponse;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.ListUsersResponse;
import org.cloudfoundry.client.v2.users.SummaryUserRequest;
import org.cloudfoundry.client.v2.users.SummaryUserResponse;
import org.cloudfoundry.client.v2.users.UpdateUserRequest;
import org.cloudfoundry.client.v2.users.UpdateUserResponse;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link Users}
 */
public final class ReactorUsers extends AbstractClientV2Operations implements Users {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorUsers(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<AssociateUserSpaceResponse> associateSpace(AssociateUserSpaceRequest request) {
        return put(request, AssociateUserSpaceResponse.class, builder -> builder.pathSegment("v2", "users", request.getUserId(), "spaces", request.getSpaceId()))
            .checkpoint();
    }

    @Override
    public Mono<CreateUserResponse> create(CreateUserRequest request) {
        return post(request, CreateUserResponse.class, builder -> builder.pathSegment("v2", "users"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteUserResponse> delete(DeleteUserRequest request) {
        return delete(request, DeleteUserResponse.class, builder -> builder.pathSegment("v2", "users", request.getUserId()))
            .checkpoint();
    }

    @Override
    public Mono<GetUserResponse> get(GetUserRequest request) {
        return get(request, GetUserResponse.class, builder -> builder.pathSegment("v2", "users", request.getUserId()))
            .checkpoint();
    }

    @Override
    public Mono<ListUsersResponse> list(ListUsersRequest request) {
        return get(request, ListUsersResponse.class, builder -> builder.pathSegment("v2", "users"))
            .checkpoint();
    }

    @Override
    public Mono<SummaryUserResponse> summary(SummaryUserRequest request) {
        return get(request, SummaryUserResponse.class, builder -> builder.pathSegment("v2", "users", request.getUserId(), "summary"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateUserResponse> update(UpdateUserRequest request) {
        return put(request, UpdateUserResponse.class, builder -> builder.pathSegment("v2", "users", request.getUserId()))
            .checkpoint();
    }

}
