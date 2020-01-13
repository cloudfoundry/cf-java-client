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

package org.cloudfoundry.reactor.client.v3.droplets;

import org.cloudfoundry.client.v3.droplets.CopyDropletRequest;
import org.cloudfoundry.client.v3.droplets.CopyDropletResponse;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Droplets}
 */
public final class ReactorDroplets extends AbstractClientV3Operations implements Droplets {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorDroplets(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CopyDropletResponse> copy(CopyDropletRequest request) {
        return post(request, CopyDropletResponse.class, builder -> builder.pathSegment("droplets"));
    }

    @Override
    public Mono<String> delete(DeleteDropletRequest request) {
        return delete(request, builder -> builder.pathSegment("droplets", request.getDropletId()))
            .checkpoint();
    }

    @Override
    public Mono<GetDropletResponse> get(GetDropletRequest request) {
        return get(request, GetDropletResponse.class, builder -> builder.pathSegment("droplets", request.getDropletId()))
            .checkpoint();
    }

    @Override
    public Mono<ListDropletsResponse> list(ListDropletsRequest request) {
        return get(request, ListDropletsResponse.class, builder -> builder.pathSegment("droplets"))
            .checkpoint();
    }

}
