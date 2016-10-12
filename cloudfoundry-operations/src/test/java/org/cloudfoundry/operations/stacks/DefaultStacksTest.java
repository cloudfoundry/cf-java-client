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

package org.cloudfoundry.operations.stacks;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksResponse;
import org.cloudfoundry.client.v2.stacks.StackResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultStacksTest {

    private static void requestStacks(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.stacks()
            .list(ListStacksRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListStacksResponse.builder())
                    .resource(fill(StackResource.builder(), "stack-")
                        .build())
                    .build()));
    }

    private static void requestStacks(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.stacks()
            .list(ListStacksRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListStacksResponse.builder())
                    .resource(fill(StackResource.builder(), "stack-")
                        .build())
                    .build()));
    }

    public static final class GetStack extends AbstractOperationsApiTest<Stack> {

        private final DefaultStacks stacks = new DefaultStacks(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestStacks(this.cloudFoundryClient, "test-stack-name");
        }

        @Override
        protected ScriptedSubscriber<Stack> expectations() {
            return ScriptedSubscriber.<Stack>create()
                .expectValue(fill(Stack.builder(), "stack-")
                    .build())
                .expectComplete();
        }

        @Override
        protected Publisher<Stack> invoke() {
            return this.stacks
                .get(GetStackRequest.builder()
                    .name("test-stack-name")
                    .build());
        }

    }

    public static final class ListStacks extends AbstractOperationsApiTest<Stack> {

        private final DefaultStacks stacks = new DefaultStacks(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestStacks(this.cloudFoundryClient);
        }

        @Override
        protected ScriptedSubscriber<Stack> expectations() {
            return ScriptedSubscriber.<Stack>create()
                .expectValue(fill(Stack.builder(), "stack-")
                    .build())
                .expectComplete();
        }

        @Override
        protected Publisher<Stack> invoke() {
            return this.stacks.list();
        }

    }

}
