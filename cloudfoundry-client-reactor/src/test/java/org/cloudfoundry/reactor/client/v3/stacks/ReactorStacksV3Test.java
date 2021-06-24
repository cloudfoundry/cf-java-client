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

package org.cloudfoundry.reactor.client.v3.stacks;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.stacks.CreateStackRequest;
import org.cloudfoundry.client.v3.stacks.CreateStackResponse;
import org.cloudfoundry.client.v3.stacks.DeleteStackRequest;
import org.cloudfoundry.client.v3.stacks.GetStackRequest;
import org.cloudfoundry.client.v3.stacks.GetStackResponse;
import org.cloudfoundry.client.v3.stacks.ListStacksRequest;
import org.cloudfoundry.client.v3.stacks.ListStacksResponse;
import org.cloudfoundry.client.v3.stacks.StackResource;
import org.cloudfoundry.client.v3.stacks.UpdateStackRequest;
import org.cloudfoundry.client.v3.stacks.UpdateStackResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorStacksV3Test extends AbstractClientApiTest {

    private final ReactorStacksV3 stacks = new ReactorStacksV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/stacks")
                .payload("fixtures/client/v3/stacks/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/stacks/POST_response.json")
                .build())
            .build());

        this.stacks.create(CreateStackRequest.builder()
            .name("test-stack-name")
            .description("test-stack-description")
            .build())
            .as(StepVerifier::create)
            .expectNext(CreateStackResponse.builder()
                .id("11c916c9-c2f9-440e-8e73-102e79c4704d")
                .name("test-stack-name")
                .description("test-stack-description")
                .createdAt("2018-11-09T22:43:28Z")
                .updatedAt("2018-11-09T22:43:28Z")
                .metadata(Metadata.builder()
                    .putAllAnnotations(Collections.emptyMap())
                    .putAllLabels(Collections.emptyMap())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.com/v3/stacks/11c916c9-c2f9-440e-8e73-102e79c4704d")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/stacks/test-stack-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.stacks
            .delete(DeleteStackRequest.builder()
                .stackId("test-stack-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/stacks/11c916c9-c2f9-440e-8e73-102e79c4704d")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/stacks/GET_{id}_response.json")
                .build())
            .build());

        this.stacks
            .get(GetStackRequest.builder()
                .stackId("11c916c9-c2f9-440e-8e73-102e79c4704d")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetStackResponse.builder()
                .id("11c916c9-c2f9-440e-8e73-102e79c4704d")
                .name("test-stack-name")
                .description("test-stack-description")
                .createdAt("2018-11-09T22:43:28Z")
                .updatedAt("2018-11-09T22:43:28Z")
                .metadata(Metadata.builder()
                    .putAllAnnotations(Collections.emptyMap())
                    .putAllLabels(Collections.emptyMap())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.com/v3/stacks/11c916c9-c2f9-440e-8e73-102e79c4704d")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/stacks")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/stacks/GET_response.json")
                .build())
            .build());

        this.stacks.list(ListStacksRequest.builder().build())
            .as(StepVerifier::create)
            .expectNext(ListStacksResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/stacks?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/stacks?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/stacks?page=2&per_page=2")
                        .build())
                    .build())
                .resource(StackResource.builder()
                    .id("11c916c9-c2f9-440e-8e73-102e79c4704d")
                    .name("test-stack-name-1")
                    .description("test-stack-description-1")
                    .createdAt("2018-11-09T22:43:28Z")
                    .updatedAt("2018-11-09T22:43:28Z")
                    .metadata(Metadata.builder()
                        .putAllAnnotations(Collections.emptyMap())
                        .putAllLabels(Collections.emptyMap())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/stacks/11c916c9-c2f9-440e-8e73-102e79c4704d")
                        .build())
                    .build())
                .resource(StackResource.builder()
                    .id("81c916c9-c2f9-440e-8e73-102e79c4704h")
                    .name("test-stack-name-2")
                    .description("test-stack-description-2")
                    .createdAt("2018-11-09T22:43:29Z")
                    .updatedAt("2018-11-09T22:43:29Z")
                    .metadata(Metadata.builder()
                        .putAllAnnotations(Collections.emptyMap())
                        .putAllLabels(Collections.emptyMap())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/stacks/81c916c9-c2f9-440e-8e73-102e79c4704h")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/stacks/test-stack-id")
                .payload("fixtures/client/v3/stacks/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/stacks/PATCH_{id}_response.json")
                .build())
            .build());

        this.stacks
            .update(UpdateStackRequest.builder()
                .stackId("test-stack-id")
                .metadata(Metadata.builder()
                    .label("key", "value")
                    .annotation("note", "detailed information")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateStackResponse.builder()
                .id("11c916c9-c2f9-440e-8e73-102e79c4704d")
                .name("test-stack-name")
                .description("test-stack-description")
                .createdAt("2018-11-09T22:43:28Z")
                .updatedAt("2018-11-09T22:43:28Z")
                .metadata(Metadata.builder()
                    .label("key", "value")
                    .annotation("note", "detailed information")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.com/v3/stacks/11c916c9-c2f9-440e-8e73-102e79c4704d")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
