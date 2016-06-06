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

package org.cloudfoundry.reactor.uaa.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.uaa.users.ChangeUserPasswordRequest;
import org.cloudfoundry.uaa.users.ChangeUserPasswordResponse;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkRequest;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkResponse;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.UpdateUserRequest;
import org.cloudfoundry.uaa.users.UpdateUserResponse;
import org.cloudfoundry.uaa.users.Users;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Reactor-based implementation of {@link Users}
 */
public final class ReactorUsers extends AbstractUaaOperations implements Users {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorUsers(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<ChangeUserPasswordResponse> changePassword(ChangeUserPasswordRequest request) {
        return put(request, ChangeUserPasswordResponse.class, builder -> builder.pathSegment("Users", request.getUserId(), "password"));
    }

    @Override
    public Mono<CreateUserResponse> create(CreateUserRequest request) {
        return post(request, CreateUserResponse.class, builder -> builder.pathSegment("Users"));
    }

    @Override
    public Mono<DeleteUserResponse> delete(DeleteUserRequest request) {
        return delete(request, DeleteUserResponse.class, builder -> builder.pathSegment("Users", request.getUserId()),
            outbound -> ifMatch(outbound, request.getVersion()));
    }

    @Override
    public Mono<GetUserVerificationLinkResponse> getVerificationLink(GetUserVerificationLinkRequest request) {
        return get(request, GetUserVerificationLinkResponse.class, builder -> builder.pathSegment("Users", request.getUserId(), "verify-link").queryParam("redirect_uri", request.getRedirectUri()));
    }

    @Override
    public Mono<ListUsersResponse> list(ListUsersRequest request) {
        return get(request, ListUsersResponse.class, builder -> builder.pathSegment("Users"));
    }

    @Override
    public Mono<UpdateUserResponse> update(UpdateUserRequest request) {
        return put(request, UpdateUserResponse.class, builder -> builder.pathSegment("Users", request.getId()));
    }

}
