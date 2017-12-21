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
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.users.DeleteUserResponse;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.UaaException;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.User;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultUserAdminTest extends AbstractOperationsTest {

    private final DefaultUserAdmin userAdmin = new DefaultUserAdmin(Mono.just(this.cloudFoundryClient), Mono.just(this.uaaClient));

    @Test
    public void createWithPassword() {
        requestCreateUaaUser(this.uaaClient);
        requestCreateUser(this.cloudFoundryClient);

        this.userAdmin
            .create(CreateUserRequest.builder()
                .username("test-username")
                .password("test-password")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createUaaUserExists() {
        requestCreateUaaUserAlreadyExists(this.uaaClient);
        requestCreateUser(this.cloudFoundryClient);

        this.userAdmin
            .create(CreateUserRequest.builder()
                .username("test-username")
                .password("test-password")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("User test-username already exists")
            .verify(Duration.ofSeconds(5));
    }



    @Test
    public void delete() {
        requestListUser(this.uaaClient);
        requestDeleteUser(this.cloudFoundryClient);
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");
        requestDeleteUaaUser(this.uaaClient);

        StepVerifier.withVirtualTime(() -> this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteFailure() {
        requestListUser(this.uaaClient);
        requestDeleteUser(this.cloudFoundryClient);
        requestJobFailure(this.cloudFoundryClient, "test-job-entity-id");
        requestDeleteUaaUser(this.uaaClient);

        StepVerifier.withVirtualTime(() -> this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-error-details-errorCode(1): test-error-details-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteNoCfUser() {
        requestListUser(this.uaaClient);
        requestDeleteUserEmpty(this.cloudFoundryClient);
        requestDeleteUaaUser(this.uaaClient);

        this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteNoUaaUser() {
        requestListUserEmpty(this.uaaClient);

        this.userAdmin
            .delete(DeleteUserRequest.builder()
                .username("test-username")
                .build())
            .as(StepVerifier::create)
            .expectErrorMessage("User test-username does not exist")
            .verify(Duration.ofSeconds(5));
    }

    private static void requestCreateUaaUser(UaaClient uaaClient) {
        when(uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value("test-username")
                    .build())
                .name(Name.builder()
                    .familyName("test-username")
                    .givenName("test-username")
                    .build())
                .password("test-password")
                .userName("test-username")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.uaa.users.CreateUserResponse.builder()
                    .id("test-user-id")
                    .active(true)
                    .meta(Meta.builder()
                        .created("test-created")
                        .lastModified("test-last-modified")
                        .version(1)
                        .build())
                    .name(Name.builder().build())
                    .origin("test-origin")
                    .passwordLastModified("test-password-last-modified")
                    .userName("test-username")
                    .verified(false)
                    .zoneId("test-zone-id")
                    .build()));
    }

    private static void requestCreateUaaUserAlreadyExists(UaaClient uaaClient) {
        when(uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value("test-username")
                    .build())
                .name(Name.builder()
                    .familyName("test-username")
                    .givenName("test-username")
                    .build())
                .password("test-password")
                .userName("test-username")
                .build()))
            .thenReturn(Mono.error(new UaaException(409, "test-error", "test-error-description")));
    }

    private static void requestCreateUser(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.users()
            .create(org.cloudfoundry.client.v2.users.CreateUserRequest.builder()
                .uaaId("test-user-id")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.client.v2.users.CreateUserResponse.builder()
                    .build()));
    }

    private static void requestDeleteUaaUser(UaaClient uaaClient) {
        when(uaaClient.users()
            .delete(org.cloudfoundry.uaa.users.DeleteUserRequest.builder()
                .userId("test-user-id")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.uaa.users.DeleteUserResponse.builder()
                    .id("test-user-id")
                    .active(true)
                    .meta(Meta.builder()
                        .created("test-created")
                        .lastModified("test-last-modified")
                        .version(1)
                        .build())
                    .name(Name.builder().build())
                    .origin("test-origin")
                    .passwordLastModified("test-password-last-modified")
                    .userName("test-username")
                    .verified(false)
                    .zoneId("test-zone-id")
                    .build()));
    }

    private static void requestDeleteUser(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.users()
            .delete(org.cloudfoundry.client.v2.users.DeleteUserRequest.builder()
                .async(true)
                .userId("test-user-id")
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteUserResponse.builder())
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));
    }

    private static void requestDeleteUserEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.users()
            .delete(org.cloudfoundry.client.v2.users.DeleteUserRequest.builder()
                .async(true)
                .userId("test-user-id")
                .build()))
            .thenReturn(Mono.error(new ClientV2Exception(404, 404, "test-description", "test-error-code")));
    }

    private static void requestJobFailure(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .errorDetails(fill(ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestListUser(UaaClient uaaClient) {
        when(uaaClient.users()
            .list(org.cloudfoundry.uaa.users.ListUsersRequest.builder()
                .filter("userName eq \"test-username\"")
                .startIndex(1)
                .build()))
            .thenReturn(Mono
                .just(fill(org.cloudfoundry.uaa.users.ListUsersResponse.builder())
                    .resource(User.builder()
                        .id("test-user-id")
                        .active(true)
                        .meta(Meta.builder()
                            .created("test-created")
                            .lastModified("test-last-modified")
                            .version(1)
                            .build())
                        .name(Name.builder().build())
                        .origin("test-origin")
                        .passwordLastModified("test-password-last-modified")
                        .userName("test-username")
                        .verified(false)
                        .zoneId("test-zone-id")
                        .build())
                    .build()));
    }

    private static void requestListUserEmpty(UaaClient uaaClient) {
        when(uaaClient.users()
            .list(org.cloudfoundry.uaa.users.ListUsersRequest.builder()
                .filter("userName eq \"test-username\"")
                .startIndex(1)
                .build()))
            .thenReturn(Mono
                .just(fill(org.cloudfoundry.uaa.users.ListUsersResponse.builder())
                    .build()));
    }

}
