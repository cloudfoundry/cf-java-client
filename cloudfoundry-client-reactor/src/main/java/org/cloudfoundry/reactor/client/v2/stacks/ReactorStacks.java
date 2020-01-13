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

import org.cloudfoundry.client.v2.stacks.CreateStackRequest;
import org.cloudfoundry.client.v2.stacks.CreateStackResponse;
import org.cloudfoundry.client.v2.stacks.DeleteStackRequest;
import org.cloudfoundry.client.v2.stacks.DeleteStackResponse;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksResponse;
import org.cloudfoundry.client.v2.stacks.Stacks;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Stacks}
 */
public final class ReactorStacks extends AbstractClientV2Operations implements Stacks {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorStacks(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateStackResponse> create(CreateStackRequest request) {
        return post(request, CreateStackResponse.class, builder -> builder.pathSegment("stacks"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteStackResponse> delete(DeleteStackRequest request) {
        return delete(request, DeleteStackResponse.class, builder -> builder.pathSegment("stacks", request.getStackId()))
            .checkpoint();
    }

    @Override
    public Mono<GetStackResponse> get(GetStackRequest request) {
        return get(request, GetStackResponse.class, builder -> builder.pathSegment("stacks", request.getStackId()))
            .checkpoint();
    }

    @Override
    public Mono<ListStacksResponse> list(ListStacksRequest request) {
        return get(request, ListStacksResponse.class, builder -> builder.pathSegment("stacks"))
            .checkpoint();
    }

}
