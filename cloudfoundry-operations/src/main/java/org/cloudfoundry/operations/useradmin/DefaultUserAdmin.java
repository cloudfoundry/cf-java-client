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

package org.cloudfoundry.operations.useradmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.UaaException;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class DefaultUserAdmin implements UserAdmin {

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<UaaClient> uaaClient;

    public DefaultUserAdmin(Mono<CloudFoundryClient> cloudFoundryClient, Mono<UaaClient> uaaClient) {
        this.uaaClient = uaaClient;
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<Void> create(CreateUserRequest request) {
        return Mono.zip(this.cloudFoundryClient, this.uaaClient)
            .flatMap(function((cloudFoundryClient, uaaClient) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                createUaaUserId(uaaClient, request))))
            .flatMap(function(DefaultUserAdmin::requestCreateUser))
            .then()
            .transform(OperationsLogging.log("Create User"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteUserRequest request) {
        return Mono.zip(this.cloudFoundryClient, this.uaaClient)
            .flatMap(function((cloudFoundryClient, uaaClient) -> Mono.zip(
                Mono.just(cloudFoundryClient),
                Mono.just(uaaClient),
                getUserId(uaaClient, request))))
            .flatMap(function((cloudFoundryClient, uaaClient, userId) -> Mono.zip(
                deleteUser(cloudFoundryClient, userId),
                requestDeleteUaaUser(uaaClient, userId))))
            .then()
            .transform(OperationsLogging.log("Delete User"))
            .checkpoint();
    }

    private static Mono<String> createUaaUserId(UaaClient uaaClient, CreateUserRequest request) {
        return requestCreateUaaUser(uaaClient, request)
            .map(CreateUserResponse::getId)
            .onErrorResume(UaaException.class, t -> ExceptionUtils.illegalArgument("User %s already exists", request.getUsername()));
    }

    private static Mono<Void> deleteUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return requestDeleteUser(cloudFoundryClient, userId)
            .onErrorResume(t -> t instanceof ClientV2Exception && ((ClientV2Exception) t).getStatusCode() == 404, t -> Mono.empty())
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, Duration.ofMinutes(5), job));
    }

    private static Mono<String> getUserId(UaaClient uaaClient, DeleteUserRequest request) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.users()
                .list(ListUsersRequest.builder()
                    .filter(String.format("userName eq \"%s\"", request.getUsername()))
                    .startIndex(startIndex)
                    .build()))
            .switchIfEmpty(ExceptionUtils.illegalArgument("User %s does not exist", request.getUsername()))
            .single()
            .map(User::getId);
    }

    private static Mono<CreateUserResponse> requestCreateUaaUser(UaaClient uaaClient, CreateUserRequest request) {
        return uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value(request.getUsername())
                    .build())
                .name(Name.builder()
                    .familyName(request.getUsername())
                    .givenName(request.getUsername())
                    .build())
                .origin(request.getOrigin())
                .password(request.getPassword())
                .userName(request.getUsername())
                .build());
    }

    private static Mono<org.cloudfoundry.client.v2.users.CreateUserResponse> requestCreateUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient.users()
            .create(org.cloudfoundry.client.v2.users.CreateUserRequest.builder()
                .uaaId(userId)
                .build());
    }

    private static Mono<DeleteUserResponse> requestDeleteUaaUser(UaaClient uaaClient, String userId) {
        return uaaClient.users().delete(org.cloudfoundry.uaa.users.DeleteUserRequest.builder()
            .userId(userId)
            .build());
    }

    private static Mono<org.cloudfoundry.client.v2.users.DeleteUserResponse> requestDeleteUser(CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient.users()
            .delete(org.cloudfoundry.client.v2.users.DeleteUserRequest.builder()
                .async(true)
                .userId(userId)
                .build());
    }

}
