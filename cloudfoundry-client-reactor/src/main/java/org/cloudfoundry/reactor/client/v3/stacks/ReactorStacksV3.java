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

import java.util.Map;
import org.cloudfoundry.client.v3.stacks.CreateStackRequest;
import org.cloudfoundry.client.v3.stacks.CreateStackResponse;
import org.cloudfoundry.client.v3.stacks.DeleteStackRequest;
import org.cloudfoundry.client.v3.stacks.GetStackRequest;
import org.cloudfoundry.client.v3.stacks.GetStackResponse;
import org.cloudfoundry.client.v3.stacks.ListStacksRequest;
import org.cloudfoundry.client.v3.stacks.ListStacksResponse;
import org.cloudfoundry.client.v3.stacks.StacksV3;
import org.cloudfoundry.client.v3.stacks.UpdateStackRequest;
import org.cloudfoundry.client.v3.stacks.UpdateStackResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link StacksV3}
 */
public class ReactorStacksV3 extends AbstractClientV3Operations implements StacksV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorStacksV3(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateStackResponse> create(CreateStackRequest request) {
        return post(
                        request,
                        CreateStackResponse.class,
                        uriComponentsBuilder -> uriComponentsBuilder.pathSegment("stacks"))
                .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteStackRequest request) {
        return delete(
                        request,
                        Void.class,
                        uriComponentsBuilder ->
                                uriComponentsBuilder.pathSegment("stacks", request.getStackId()))
                .checkpoint();
    }

    @Override
    public Mono<GetStackResponse> get(GetStackRequest request) {
        return get(
                        request,
                        GetStackResponse.class,
                        uriComponentsBuilder ->
                                uriComponentsBuilder.pathSegment("stacks", request.getStackId()))
                .checkpoint();
    }

    @Override
    public Mono<ListStacksResponse> list(ListStacksRequest request) {
        return get(
                        request,
                        ListStacksResponse.class,
                        uriComponentsBuilder -> uriComponentsBuilder.pathSegment("stacks"))
                .checkpoint();
    }

    @Override
    public Mono<UpdateStackResponse> update(UpdateStackRequest request) {
        return patch(
                        request,
                        UpdateStackResponse.class,
                        uriComponentsBuilder ->
                                uriComponentsBuilder.pathSegment("stacks", request.getStackId()))
                .checkpoint();
    }
}
