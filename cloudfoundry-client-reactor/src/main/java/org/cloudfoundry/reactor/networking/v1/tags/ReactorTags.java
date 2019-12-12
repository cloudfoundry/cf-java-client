/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.reactor.networking.v1.tags;

import org.cloudfoundry.networking.v1.tags.ListTagsRequest;
import org.cloudfoundry.networking.v1.tags.ListTagsResponse;
import org.cloudfoundry.networking.v1.tags.Tags;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.networking.AbstractNetworkingOperations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Tags}
 */
public class ReactorTags extends AbstractNetworkingOperations implements Tags {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorTags(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider,
                       Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<ListTagsResponse> list(ListTagsRequest request) {
        return get(ListTagsResponse.class, builder -> builder.pathSegment("tags")).checkpoint();
    }
}
