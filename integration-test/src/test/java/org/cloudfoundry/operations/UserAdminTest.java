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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.operations.useradmin.CreateUserRequest;
import org.cloudfoundry.operations.useradmin.DeleteUserRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public final class UserAdminTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void create() {
        String username = this.nameFactory.getUserName();

        this.cloudFoundryOperations.userAdmin()
            .create(CreateUserRequest.builder()
                .password("test-password")
                .username(username)
                .build())
            .thenMany(listUsers(this.cloudFoundryClient))
            .filter(response -> username.equals(ResourceUtils.getEntity(response).getUsername()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createDuplicate() {
        String username = this.nameFactory.getUserName();

        createUser(this.cloudFoundryOperations, username)
            .then(createUser(this.cloudFoundryOperations, username))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("User %s already exists", username))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String username = this.nameFactory.getUserName();

        createUser(this.cloudFoundryOperations, username)
            .then(this.cloudFoundryOperations.userAdmin()
                .delete(DeleteUserRequest.builder()
                    .username(username)
                    .build()))
            .thenMany(listUsers(this.cloudFoundryClient))
            .filter(response -> username.equals(ResourceUtils.getEntity(response).getUsername()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteNotFound() {
        String username = this.nameFactory.getUserName();

        this.cloudFoundryOperations.userAdmin()
                .delete(DeleteUserRequest.builder()
                    .username(username)
                    .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("User %s does not exist", username))
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> createUser(CloudFoundryOperations cloudFoundryOperations, String username) {
        return cloudFoundryOperations.userAdmin()
            .create(CreateUserRequest.builder()
                .password("test-password")
                .username(username)
                .build());
    }

    private static Flux<UserResource> listUsers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.users()
            .list(ListUsersRequest.builder()
                .page(page)
                .build()));
    }

}
