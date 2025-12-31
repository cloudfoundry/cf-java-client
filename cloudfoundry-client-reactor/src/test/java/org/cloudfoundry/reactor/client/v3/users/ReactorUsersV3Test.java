package org.cloudfoundry.reactor.client.v3.users;

import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.users.*;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorUsersV3Test extends AbstractClientApiTest {
    private final ReactorUsersV3 users =
            new ReactorUsersV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/users")
                                        .payload("fixtures/client/v3/users/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload("fixtures/client/v3/users/POST_response.json")
                                        .build())
                        .build());

        this.users
                .create(
                        CreateUserRequest.builder()
                                .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateUserResponse.builder()
                                .from(
                                        expectedUserResource(
                                                Collections.emptyMap(), Collections.emptyMap()))
                                .build())
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
                                        .payload("fixtures/client/v3/users/GET_{id}_response.json")
                                        .build())
                        .build());

        this.users
                .get(
                        GetUserRequest.builder()
                                .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetUserResponse.builder()
                                .from(
                                        expectedUserResource(
                                                Collections.emptyMap(), Collections.emptyMap()))
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/users").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/client/v3/users/GET_response.json")
                                        .build())
                        .build());

        Link first =
                Link.builder().href("https://api.example.org/v3/users?page=1&per_page=1").build();
        Link last =
                Link.builder().href("https://api.example.org/v3/users?page=1&per_page=1").build();
        this.users
                .list(ListUsersRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListUsersResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .first(first)
                                                .last(last)
                                                .totalResults(1)
                                                .totalPages(1)
                                                .build())
                                .resource(
                                        expectedUserResource(
                                                Collections.emptyMap(), Collections.emptyMap()))
                                .build())
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
                                        .payload("fixtures/client/v3/users/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/users/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.users
                .update(
                        UpdateUserRequest.builder()
                                .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .metadata(
                                        Metadata.builder()
                                                .putAllAnnotations(
                                                        Collections.singletonMap(
                                                                "note", "detailed information"))
                                                .putAllLabels(
                                                        Collections.singletonMap(
                                                                "environment", "production"))
                                                .build())
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateUserResponse.builder()
                                .from(
                                        expectedUserResource(
                                                Collections.singletonMap(
                                                        "note", "detailed information"),
                                                Collections.singletonMap(
                                                        "environment", "production")))
                                .build())
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
                        .response(TestResponse.builder().status(ACCEPTED).build())
                        .build());

        this.users
                .delete(
                        DeleteUserRequest.builder()
                                .userId("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    UserResource expectedUserResource(Map<String, String> labels, Map<String, String> annotations) {
        return UserResource.builder()
                .id("3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                .createdAt("2019-03-08T01:06:19Z")
                .updatedAt("2019-03-08T01:06:19Z")
                .username("some-name")
                .presentationName("some-name")
                .origin("uaa")
                .metadata(
                        Metadata.builder()
                                .putAllAnnotations(labels)
                                .putAllLabels(annotations)
                                .build())
                .link(
                        "self",
                        Link.builder()
                                .href(
                                        "https://api.example.org/v3/users/3a5d3d89-3f89-4f05-8188-8a2b298c79d5")
                                .build())
                .build();
    }
}
