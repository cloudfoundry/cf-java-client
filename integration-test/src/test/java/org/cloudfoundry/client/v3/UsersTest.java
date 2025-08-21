/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3;


import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.users.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_4_v3)
public final class UsersTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    private static Mono<CreateUserResponse> createUser(
            CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient
                .usersV3()
                .create(CreateUserRequest.builder().userId(userId).build());
    }

    private static Mono<GetUserResponse> getUser(
            CloudFoundryClient cloudFoundryClient, String userId) {
        return cloudFoundryClient
                .usersV3()
                .get(GetUserRequest.builder().userId(userId).build());
    }

    @Test
    public void create() {
        String userId = this.nameFactory.getUserId();

        this.cloudFoundryClient
                .usersV3()
                .create(CreateUserRequest.builder()
                        .userId(userId)
                        .build()
                )
                .single()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String userId = this.nameFactory.getUserId();

        createUser(this.cloudFoundryClient, userId)
                .flatMap(createUserResponse ->
                        this.cloudFoundryClient.usersV3()
                                .get(GetUserRequest.builder()
                                        .userId(userId)
                                        .build()))
                .map(GetUserResponse::getId)
                .as(StepVerifier::create)
                .expectNext(userId)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String userId = this.nameFactory.getUserId();

        createUser(this.cloudFoundryClient, userId)
                .flatMap(createUserResponse ->
                        this.cloudFoundryClient.usersV3()
                                .update(UpdateUserRequest.builder()
                                        .userId(userId)
                                        .metadata(Metadata.builder()
                                                .annotation(
                                                        "annotationKey",
                                                        "annotationValue")
                                                .label(
                                                        "labelKey",
                                                        "labelValue")
                                                .build())
                                        .build()))
                .then(getUser(cloudFoundryClient, userId))
                .as(StepVerifier::create)
                .consumeNextWith(
                        GetUserResponse -> {
                            Metadata metadata = GetUserResponse.getMetadata();
                            assertThat(metadata.getAnnotations().get("annotationKey"))
                                    .isEqualTo("annotationValue");
                            assertThat(metadata.getLabels().get("labelKey"))
                                    .isEqualTo("labelValue");
                        })
                .expectComplete()
                .verify(Duration.ofMinutes(5));

    }

    @Test
    public void delete() {
        String userId = this.nameFactory.getUserId();

        createUser(this.cloudFoundryClient, userId)
                .flatMap(
                        createUserResponse ->
                                this.cloudFoundryClient
                                        .usersV3()
                                        .delete(
                                                DeleteUserRequest.builder()
                                                        .userId(createUserResponse.getId())
                                                        .build()))
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }
}
