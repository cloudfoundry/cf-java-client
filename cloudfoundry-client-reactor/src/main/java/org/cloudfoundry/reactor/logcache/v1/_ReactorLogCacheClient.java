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

package org.cloudfoundry.reactor.logcache.v1;

import org.cloudfoundry.logcache.v1.InfoRequest;
import org.cloudfoundry.logcache.v1.InfoResponse;
import org.cloudfoundry.logcache.v1.LogCacheClient;
import org.cloudfoundry.logcache.v1.MetaRequest;
import org.cloudfoundry.logcache.v1.MetaResponse;
import org.cloudfoundry.logcache.v1.ReadRequest;
import org.cloudfoundry.logcache.v1.ReadResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link LogCacheClient}
 */
@Value.Immutable
abstract class _ReactorLogCacheClient implements LogCacheClient {

    @Override
    public Mono<InfoResponse> info(InfoRequest request) {
        return getReactorLogCacheEndpoints().info(request);
    }

    @Override
    public Mono<MetaResponse> meta(MetaRequest request) {
        return getReactorLogCacheEndpoints().meta(request);
    }

    @Override
    public Mono<ReadResponse> read(ReadRequest request) {
        return getReactorLogCacheEndpoints().read(request);
    }

    /**
     * The connection context
     */
    abstract ConnectionContext getConnectionContext();

    @Value.Derived
    ReactorLogCacheEndpoints getReactorLogCacheEndpoints() {
        return new ReactorLogCacheEndpoints(getConnectionContext(), getRoot(), getTokenProvider(), getRequestTags());
    }

    @Value.Default
    Map<String, String> getRequestTags() {
        return Collections.emptyMap();
    }

    @Value.Default
    Mono<String> getRoot() {
        final Mono<String> cached = getConnectionContext().getRootProvider().getRoot("log-cache", getConnectionContext())
            .onErrorResume(IllegalArgumentException.class, e -> deriveLogCacheUrl());

        return getConnectionContext().getCacheDuration()
            .map(cached::cache)
            .orElseGet(cached::cache);
    }

    private Mono<String> deriveLogCacheUrl() {
        return getConnectionContext().getRootProvider().getRoot(getConnectionContext())
            .map(root -> root.replace("api", "log-cache"))
            .map(URI::create)
            .delayUntil(uri -> getConnectionContext().trust(uri.getHost(), uri.getPort()))
            .map(URI::toString);
    }

    /**
     * The token provider
     */
    abstract TokenProvider getTokenProvider();

}
