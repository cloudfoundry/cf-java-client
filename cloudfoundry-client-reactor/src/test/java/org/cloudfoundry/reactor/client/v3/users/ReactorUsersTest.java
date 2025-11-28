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
package org.cloudfoundry.reactor.client.v3.users;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.users.*;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

final class ReactorUsersTest extends AbstractClientApiTest {
    private final ReactorUsersV3 users =
            new ReactorUsersV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/users")
                                        .payload(
                                                "fixtures/client/v3/users/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/users/POST_response.json")
                                        .build())
                        .build());

        this.users
                .create(CreateUserRequest.builder()
                        .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                        .build()
                )
                .as(StepVerifier::create)
                .expectNext(
                        CreateUserResponse.builder()
                                .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .createdAt("2019-03-08T01:06:19Z")
                                .updatedAt("2019-03-08T01:06:19Z")
                                .username("some-name")
                                .presentationName("some-name")
                                .origin("uaa")
                                .metadata(
                                        Metadata.builder()
                                                .putAllAnnotations(Collections.emptyMap())
                                                .putAllLabels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/users/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                                .build())
                                .build()
                )
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/users/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/users/GET_{id}_response.json")
                                        .build())
                        .build());

        this.users
                .get(GetUserRequest.builder()
                        .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                        .build()
                )
                .as(StepVerifier::create)
                .expectNext(
                        GetUserResponse.builder()
                                .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .createdAt("2019-03-08T01:06:19Z")
                                .updatedAt("2019-03-08T01:06:19Z")
                                .username("some-name")
                                .presentationName("some-name")
                                .origin("uaa")
                                .metadata(
                                        Metadata.builder()
                                                .putAllAnnotations(Collections.emptyMap())
                                                .putAllLabels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/users/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                                .build())
                                .build()
                )
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void update() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path("/users/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                        .payload(
                                                "fixtures/client/v3/users/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/users/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.users
                .update(UpdateUserRequest.builder()
                        .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                        .metadata(Metadata.builder()
                                .putAllAnnotations(Map.of("note", "detailed information"))
                                .putAllLabels(Map.of("environment", "production"))
                                .build())
                        .build()
                )
                .as(StepVerifier::create)
                .expectNext(
                        UpdateUserResponse.builder()
                                .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .createdAt("2019-03-08T01:06:19Z")
                                .updatedAt("2019-03-08T01:06:19Z")
                                .username("some-name")
                                .presentationName("some-name")
                                .origin("uaa")
                                .metadata(Metadata.builder()
                                        .putAllAnnotations(Map.of("note", "detailed information"))
                                        .putAllLabels(Map.of("environment", "production"))
                                        .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/users/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                                .build())
                                .build()
                )
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void delete() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path("/users/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .build())
                        .build());

        this.users
                .delete(DeleteUserRequest.builder()
                        .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                        .build()
                )
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

}
