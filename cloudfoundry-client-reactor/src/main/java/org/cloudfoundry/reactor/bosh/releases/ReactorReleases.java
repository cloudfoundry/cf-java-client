/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.bosh.releases;

import org.cloudfoundry.bosh.releases.ListReleasesRequest;
import org.cloudfoundry.bosh.releases.ListReleasesResponse;
import org.cloudfoundry.bosh.releases.Releases;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.bosh.AbstractBoshOperations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link Releases}
 */
public final class ReactorReleases extends AbstractBoshOperations implements Releases {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorReleases(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<ListReleasesResponse> list(ListReleasesRequest request) {
        return get(request, ListReleasesResponse.class, builder -> builder.pathSegment("releases"));
    }

}
