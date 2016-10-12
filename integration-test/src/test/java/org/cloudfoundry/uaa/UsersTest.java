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
import org.cloudfoundry.uaa.users.ChangeUserPasswordResponse;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkRequest;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkResponse;
import org.cloudfoundry.uaa.users.Invite;
import org.cloudfoundry.uaa.users.InviteUsersRequest;
import org.cloudfoundry.uaa.users.InviteUsersResponse;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.LookupUserIdsRequest;
import org.cloudfoundry.uaa.users.LookupUserIdsResponse;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.UpdateUserRequest;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.uaa.users.UserId;
import org.cloudfoundry.uaa.users.VerifyUserRequest;
import org.cloudfoundry.uaa.users.VerifyUserResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public final class UsersTest extends AbstractIntegrationTest {

    @Autowired
    private String password;

    @Autowired
    private UaaClient uaaClient;

    @Autowired
    private String username;

    @Ignore("TODO: Refresh token after password change")
    @Test
    public void changePassword() throws TimeoutException, InterruptedException {
        Function<ChangeUserPasswordResponse, Optional<String>> assertion = response -> {
            if (!"password updated".equals(response.getMessage())) {
                return Optional.of(String.format("expected message: %s; actual message: %s", "password updated", response.getMessage()));
            }

            if (!"ok".equals(response.getStatus())) {
                return Optional.of(String.format("expected status: %s; actual status: %s", "ok", response.getStatus()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<ChangeUserPasswordResponse> subscriber = ScriptedSubscriber.<ChangeUserPasswordResponse>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching response")))
            .expectComplete();

        getUserIdByUsername(this.uaaClient, this.username)
            .then(userId -> this.uaaClient.users()
                .changePassword(ChangeUserPasswordRequest.builder()
                    .oldPassword(this.password)
                    .password("test-new-password")
                    .userId(userId)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        Function<CreateUserResponse, Optional<String>> assertion = response -> {
            if (!"test-external-id".equals(response.getExternalId())) {
                return Optional.of(String.format("expected external id: %s; actual external id: %s", "test-external-id", response.getExternalId()));
            }

            if (!userName.equals(response.getUserName())) {
                return Optional.of(String.format("expected username: %s; actual username: %s", userName, response.getUserName()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<CreateUserResponse> subscriber = ScriptedSubscriber.<CreateUserResponse>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching response")))
            .expectComplete();

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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<Integer> subscriber = ScriptedSubscriber.<Integer>create()
            .expectValue(0)
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .delete(DeleteUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(DeleteUserResponse::getId)
            .then(userId -> requestListUsers(this.uaaClient, userId))
            .map(ListUsersResponse::getTotalResults)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void getVerificationLink() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        Function<String, Optional<String>> assertion = location -> {
            if (!location.contains("/verify_user?code=")) {
                return Optional.of(String.format("expected location to start with: %s; actual location: %s", "/verify_user?code=", location));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching link")))
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .getVerificationLink(GetUserVerificationLinkRequest.builder()
                    .redirectUri("test-redirect-uri")
                    .userId(userId)
                    .build()))
            .map(GetUserVerificationLinkResponse::getVerifyLink)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void invite() throws TimeoutException, InterruptedException {
        Function<Invite, Optional<String>> assertion = invite -> {
            if (!"test-email-address".equals(invite.getEmail())) {
                return Optional.of(String.format("expected email: %s; actual email: %s", "test-email-address", invite.getEmail()));
            }

            if (invite.getErrorCode() != null) {
                return Optional.of(String.format("expected error code: %s; actual error code: %s", null, invite.getErrorCode()));
            }

            if (!invite.getSuccess()) {
                return Optional.of(String.format("expected success: %s; actual success: %s", true, invite.getSuccess()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<Invite> subscriber = ScriptedSubscriber.<Invite>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching invite")))
            .expectComplete();

        this.uaaClient.users()
            .invite(InviteUsersRequest.builder()
                .email("test-email-address")
                .redirectUri("test-redirect-uri")
                .build())
            .flatMapIterable(InviteUsersResponse::getNewInvites)
            .single()
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(userName)
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .list(ListUsersRequest.builder()
                    .filter(String.format("id eq \"%s\"", userId))
                    .build()))
            .flatMapIterable(ListUsersResponse::getResources)
            .map(User::getUserName)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void lookup() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(userName)
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .lookup(LookupUserIdsRequest.builder()
                    .filter(String.format("id eq \"%s\"", userId))
                    .build()))
            .flatMapIterable(LookupUserIdsResponse::getResources)
            .map(UserId::getUserName)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue("test-email-2")
            .expectComplete();

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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void verifyUser() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(userName)
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .verify(VerifyUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(VerifyUserResponse::getUserName)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createUserId(UaaClient uaaClient, String userName) {
        return requestCreateUser(uaaClient, userName)
            .map(CreateUserResponse::getId);
    }

    private static Mono<String> getUserIdByUsername(UaaClient uaaClient, String username) {
        return requestLookupByUsername(uaaClient, username)
            .flatMapIterable(LookupUserIdsResponse::getResources)
            .map(UserId::getId)
            .single();
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
                .verified(false)
                .userName(userName)
                .build());
    }

    private static Mono<ListUsersResponse> requestListUsers(UaaClient uaaClient, String userId) {
        return uaaClient.users()
            .list(ListUsersRequest.builder()
                .filter(String.format("id eq \"%s\"", userId))
                .build());
    }

    private static Mono<LookupUserIdsResponse> requestLookupByUsername(UaaClient uaaClient, String username) {
        return uaaClient.users()
            .lookup(LookupUserIdsRequest.builder()
                .filter(String.format("userName eq \"%s\"", username))
                .build());
    }

}
