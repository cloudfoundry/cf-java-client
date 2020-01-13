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

package org.cloudfoundry.reactor.uaa.users;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.users.ChangeUserPasswordRequest;
import org.cloudfoundry.uaa.users.ChangeUserPasswordResponse;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.ExpirePasswordRequest;
import org.cloudfoundry.uaa.users.ExpirePasswordResponse;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkRequest;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkResponse;
import org.cloudfoundry.uaa.users.InviteUsersRequest;
import org.cloudfoundry.uaa.users.InviteUsersResponse;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.LookupUserIdsRequest;
import org.cloudfoundry.uaa.users.LookupUserIdsResponse;
import org.cloudfoundry.uaa.users.UpdateUserRequest;
import org.cloudfoundry.uaa.users.UpdateUserResponse;
import org.cloudfoundry.uaa.users.UserInfoRequest;
import org.cloudfoundry.uaa.users.UserInfoResponse;
import org.cloudfoundry.uaa.users.Users;
import org.cloudfoundry.uaa.users.VerifyUserRequest;
import org.cloudfoundry.uaa.users.VerifyUserResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Users}
 */
public final class ReactorUsers extends AbstractUaaOperations implements Users {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorUsers(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<ChangeUserPasswordResponse> changePassword(ChangeUserPasswordRequest request) {
        return put(request, ChangeUserPasswordResponse.class, builder -> builder.pathSegment("Users", request.getUserId(), "password"))
            .checkpoint();
    }

    @Override
    public Mono<CreateUserResponse> create(CreateUserRequest request) {
        return post(request, CreateUserResponse.class, builder -> builder.pathSegment("Users"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteUserResponse> delete(DeleteUserRequest request) {
        return delete(request, DeleteUserResponse.class, builder -> builder.pathSegment("Users", request.getUserId()))
            .checkpoint();
    }

    @Override
    public Mono<ExpirePasswordResponse> expirePassword(ExpirePasswordRequest request) {
        return patch(request, ExpirePasswordResponse.class, builder -> builder.pathSegment("Users", request.getUserId(), "status"))
            .checkpoint();
    }

    @Override
    public Mono<GetUserVerificationLinkResponse> getVerificationLink(GetUserVerificationLinkRequest request) {
        return get(request, GetUserVerificationLinkResponse.class, builder -> builder.pathSegment("Users", request.getUserId(), "verify-link"))
            .checkpoint();
    }

    @Override
    public Mono<InviteUsersResponse> invite(InviteUsersRequest request) {
        return post(request, InviteUsersResponse.class, builder -> builder.pathSegment("invite_users"))
            .checkpoint();
    }

    @Override
    public Mono<ListUsersResponse> list(ListUsersRequest request) {
        return get(request, ListUsersResponse.class, builder -> builder.pathSegment("Users"))
            .checkpoint();
    }

    @Override
    public Mono<LookupUserIdsResponse> lookup(LookupUserIdsRequest request) {
        return get(request, LookupUserIdsResponse.class, builder -> builder.pathSegment("ids", "Users"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateUserResponse> update(UpdateUserRequest request) {
        return put(request, UpdateUserResponse.class, builder -> builder.pathSegment("Users", request.getId()))
            .checkpoint();
    }

    @Override
    public Mono<UserInfoResponse> userInfo(UserInfoRequest request) {
        return get(request, UserInfoResponse.class, builder -> builder.pathSegment("userinfo"))
            .checkpoint();
    }

    @Override
    public Mono<VerifyUserResponse> verify(VerifyUserRequest request) {
        return get(request, VerifyUserResponse.class, builder -> builder.pathSegment("Users", request.getUserId(), "verify"))
            .checkpoint();
    }

}
