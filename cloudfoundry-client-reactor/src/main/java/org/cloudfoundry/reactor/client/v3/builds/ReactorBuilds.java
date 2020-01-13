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

package org.cloudfoundry.reactor.client.v3.builds;

import org.cloudfoundry.client.v3.builds.Builds;
import org.cloudfoundry.client.v3.builds.CreateBuildRequest;
import org.cloudfoundry.client.v3.builds.CreateBuildResponse;
import org.cloudfoundry.client.v3.builds.GetBuildRequest;
import org.cloudfoundry.client.v3.builds.GetBuildResponse;
import org.cloudfoundry.client.v3.builds.ListBuildsRequest;
import org.cloudfoundry.client.v3.builds.ListBuildsResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;


/**
 * The Reactor-based implementation of {@link Builds}
 */
public final class ReactorBuilds extends AbstractClientV3Operations implements Builds {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorBuilds(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateBuildResponse> create(CreateBuildRequest request) {
        return post(request, CreateBuildResponse.class, builder -> builder.pathSegment("builds"))
            .checkpoint();
    }

    @Override
    public Mono<GetBuildResponse> get(GetBuildRequest request) {
        return get(request, GetBuildResponse.class, builder -> builder.pathSegment("builds", request.getBuildId()))
            .checkpoint();
    }

    @Override
    public Mono<ListBuildsResponse> list(ListBuildsRequest request) {
        return get(request, ListBuildsResponse.class, builder -> builder.pathSegment("builds"))
            .checkpoint();
    }

}
