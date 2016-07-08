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

package org.cloudfoundry.reactor.client.v2.stacks;

import org.cloudfoundry.client.v2.Metadata;
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
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorStacksTest {

    public static final class Get extends AbstractClientApiTest<GetStackRequest, GetStackResponse> {

        private final ReactorStacks stacks = new ReactorStacks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/stacks/test-stack-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/stacks/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetStackResponse getResponse() {
            return GetStackResponse.builder()
                .metadata(Metadata.builder()
                    .id("fe4999cf-a207-4d40-bb03-f4bbf697edac")
                    .url("/v2/stacks/fe4999cf-a207-4d40-bb03-f4bbf697edac")
                    .createdAt("2015-12-22T18:27:59Z")
                    .build())
                .entity(StackEntity.builder()
                    .name("cflinuxfs2")
                    .description("cflinuxfs2")
                    .build())
                .build();
        }

        @Override
        protected GetStackRequest getValidRequest() throws Exception {
            return GetStackRequest.builder()
                .stackId("test-stack-id")
                .build();
        }

        @Override
        protected Mono<GetStackResponse> invoke(GetStackRequest request) {
            return this.stacks.get(request);
        }

    }

    public static final class List extends AbstractClientApiTest<ListStacksRequest, ListStacksResponse> {

        private final ReactorStacks stacks = new ReactorStacks(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/stacks?q=name%20IN%20test-name&page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/stacks/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListStacksResponse getResponse() {
            return ListStacksResponse.builder()
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
                .build();
        }

        @Override
        protected ListStacksRequest getValidRequest() throws Exception {
            return ListStacksRequest.builder()
                .name("test-name")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListStacksResponse> invoke(ListStacksRequest request) {
            return this.stacks.list(request);
        }

    }

}
