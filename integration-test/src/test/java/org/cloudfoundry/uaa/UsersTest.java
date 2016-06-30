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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.users.ChangeUserPasswordRequest;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkRequest;
import org.cloudfoundry.uaa.users.InviteUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.LookupUserIdsRequest;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.UpdateUserRequest;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.uaa.users.VerifyUserRequest;
import org.cloudfoundry.uaa.users.VerifyUserResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;

public final class UsersTest extends AbstractIntegrationTest {

    @Autowired
    private UaaClient uaaClient;

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/122457091 to grant password.write scope")
    @Test
    public void changePassword() {
        String userName = getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .changePassword(ChangeUserPasswordRequest.builder()
                    .oldPassword("test-password")
                    .password("test-new-password")
                    .userId(userId)
                    .build()))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void create() {
        String userName = getUserName();

        this.uaaClient.users()
            .create(CreateUserRequest.builder()
                .email(Email.builder()
                    .value("test-email")
                    .primary(true)
                    .build())
                .externalId("test-external-id")
                .name(Name.builder()
                    .familyName("test-family-name")
                    .givenName("test-given-name")
                    .build())
                .password("test-password")
                .userName(userName)
                .build())
            .subscribe(this.<CreateUserResponse>testSubscriber()
                .assertThat(response -> {
                    assertEquals("test-external-id", response.getExternalId());
                    assertEquals(userName, response.getUserName());
                }));
    }

    @Test
    public void delete() {
        String userName = getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .delete(DeleteUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(DeleteUserResponse::getId)
            .then(userId -> requestListUsers(this.uaaClient, userId))
            .map(ListUsersResponse::getTotalResults)
            .subscribe(this.testSubscriber()
                .assertEquals(0));
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/122457091 to grant scim.create, zones.uaa.admin scopes")
    @Test
    public void getVerificationLink() {
        String userName = getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .getVerificationLink(GetUserVerificationLinkRequest.builder()
                    .redirectUri("test-redirect-uri")
                    .userId(userId)
                    .build()))
            .subscribe(this.testSubscriber());
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/122457091 to grant scim.invite scope")
    @Test
    public void invite() {
        this.uaaClient.users()
            .invite(InviteUsersRequest.builder()
                .email("test-email-address")
                .redirectUri("test-redirect-uri")
                .build())
            .subscribe(this.testSubscriber());
    }

    @Test
    public void list() {
        String userName = getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .list(ListUsersRequest.builder()
                    .filter(String.format("id eq \"%s\"", userId))
                    .build()))
            .map(ListUsersResponse::getResources)
            .flatMap(Flux::fromIterable)
            .map(User::getUserName)
            .subscribe(this.testSubscriber()
                .assertEquals(userName));
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/122457091 to grant scim.userids scope")
    @Test
    public void lookup() {
        String userName = getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .lookup(LookupUserIdsRequest.builder()
                    .filter(String.format("id eq \"%s\"", userId))
                    .build()))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void update() {
        String userName = getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .update(UpdateUserRequest.builder()
                    .email(Email.builder()
                        .value("test-email-2")
                        .primary(true)
                        .build())
                    .id(userId)
                    .name(Name.builder()
                        .familyName("test-family-name")
                        .givenName("test-given-name")
                        .build())
                    .userName(userName)
                    .version("0")
                    .build()))
            .flatMap(response -> Flux.fromIterable(response.getEmail())
                .map(Email::getValue))
            .subscribe(this.testSubscriber()
                .assertEquals("test-email-2"));
    }

    @Test
    public void verifyUser() {
        String userName = getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .verify(VerifyUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(VerifyUserResponse::getUserName)
            .subscribe(this.testSubscriber()
                .assertEquals(userName));
    }

    private static Mono<String> createUserId(UaaClient uaaClient, String userName) {
        return requestCreateUser(uaaClient, userName)
            .map(CreateUserResponse::getId);
    }

    private static Mono<CreateUserResponse> requestCreateUser(UaaClient uaaClient, String userName) {
        return uaaClient.users()
            .create(CreateUserRequest.builder()
                .email(Email.builder()
                    .value("test-email")
                    .primary(true)
                    .build())
                .name(Name.builder()
                    .familyName("test-family-name")
                    .givenName("test-given-name")
                    .build())
                .password("test-password")
                .userName(userName)
                .build());
    }

    private static Mono<ListUsersResponse> requestListUsers(UaaClient uaaClient, String userId) {
        return uaaClient.users()
            .list(ListUsersRequest.builder()
                .filter(String.format("id eq \"%s\"", userId))
                .build());
    }

}