/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.stacks;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.stacks.CreateStackRequest;
import org.cloudfoundry.client.v2.stacks.CreateStackResponse;
import org.cloudfoundry.client.v2.stacks.DeleteStackRequest;
import org.cloudfoundry.client.v2.stacks.DeleteStackResponse;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksResponse;
import org.cloudfoundry.client.v2.stacks.StackEntity;
import org.cloudfoundry.client.v2.stacks.StackResource;
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
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorStacksTest extends AbstractClientApiTest {

    private final ReactorStacks stacks = new ReactorStacks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/stacks")
                .payload("fixtures/client/v2/stacks/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/stacks/POST_response.json")
                .build())
            .build());

        this.stacks
            .create(CreateStackRequest.builder()
                .description("Description for the example stack")
                .name("example_stack")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateStackResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:23Z")
                    .id("c7d0b591-2572-4d23-bf7c-9dac95074a9e")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/stacks/c7d0b591-2572-4d23-bf7c-9dac95074a9e")
                    .build())
                .entity(StackEntity.builder()
                    .description("Description for the example stack")
                    .name("example_stack")
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
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/stacks/test-stack-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/stacks/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.stacks
            .delete(DeleteStackRequest.builder()
                .async(true)
                .stackId("test-stack-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteStackResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-02-02T17:16:31Z")
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/stacks/test-stack-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/stacks/GET_{id}_response.json")
                .build())
            .build());

        this.stacks
            .get(GetStackRequest.builder()
                .stackId("test-stack-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetStackResponse.builder()
                .metadata(Metadata.builder()
                    .id("fe4999cf-a207-4d40-bb03-f4bbf697edac")
                    .url("/v2/stacks/fe4999cf-a207-4d40-bb03-f4bbf697edac")
                    .createdAt("2015-12-22T18:27:59Z")
                    .build())
                .entity(StackEntity.builder()
                    .name("cflinuxfs2")
                    .description("cflinuxfs2")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/stacks?q=name%3Atest-name&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/stacks/GET_response.json")
                .build())
            .build());

        this.stacks
            .list(ListStacksRequest.builder()
                .name("test-name")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListStacksResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(StackResource.builder()
                    .metadata(Metadata.builder()
                        .id("fe4999cf-a207-4d40-bb03-f4bbf697edac")
                        .url("/v2/stacks/fe4999cf-a207-4d40-bb03-f4bbf697edac")
                        .createdAt("2015-12-22T18:27:59Z")
                        .build())
                    .entity(StackEntity.builder()
                        .name("cflinuxfs2")
                        .description("cflinuxfs2")
                        .build())
                    .build())
                .resource(StackResource.builder()
                    .metadata(Metadata.builder()
                        .id("ff0f87c9-9add-477a-8674-c11c012667a6")
                        .url("/v2/stacks/ff0f87c9-9add-477a-8674-c11c012667a6")
                        .createdAt("2015-12-22T18:27:59Z")
                        .build())
                    .entity(StackEntity.builder()
                        .name("default-stack-name")
                        .description("default-stack-description")
                        .build())
                    .build())
                .resource(StackResource.builder()
                    .metadata(Metadata.builder()
                        .id("01bd93b4-f252-4517-a4a5-191eb4c7fc7e")
                        .url("/v2/stacks/01bd93b4-f252-4517-a4a5-191eb4c7fc7e")
                        .createdAt("2015-12-22T18:27:59Z")
                        .build())
                    .entity(StackEntity.builder()
                        .name("cider")
                        .description("cider-description")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
