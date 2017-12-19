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
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.UaaException;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.util.ExceptionUtils;
import reactor.core.publisher.Mono;

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
        return Mono.when(this.cloudFoundryClient, this.uaaClient)
            .then(function((cloudFoundryClient, uaaClient) -> Mono.when(
                Mono.just(cloudFoundryClient),
                createUaaUserId(uaaClient, request))))
            .then(function(DefaultUserAdmin::requestCreateUser))
            .then()
            .transform(OperationsLogging.log("Create User"))
            .checkpoint();
    }

    private static Mono<String> createUaaUserId(UaaClient uaaClient, CreateUserRequest request) {
        return requestCreateUaaUser(uaaClient, request)
            .map(CreateUserResponse::getId)
            .onErrorResume(UaaException.class, t -> ExceptionUtils.illegalArgument("User %s already exists", request.getUsername()));
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

}
