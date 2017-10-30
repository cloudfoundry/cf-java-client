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

package org.cloudfoundry.reactor;

import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.cloudfoundry.reactor.util.UserAgent;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A {@link RootProvider} that returns endpoints extracted from the `/` API for the configured endpoint.
 */
@Value.Immutable
abstract class _RootPayloadRootProvider extends AbstractPayloadCachingRootProvider {

    @Override
    @SuppressWarnings("unchecked")
    protected Mono<Map<String, String>> doGetPayload(ConnectionContext connectionContext) {
        return getRoot(connectionContext)
            .then(uri -> connectionContext.getHttpClient()
                .get(uri, request -> Mono.just(request)
                    .map(UserAgent::addUserAgent)
                    .map(JsonCodec::addDecodeHeaders)
                    .flatMapMany(HttpClientRequest::send))
                .doOnSubscribe(NetworkLogging.get(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(JsonCodec.decode(getObjectMapper(), Map.class))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Root endpoint does not contain a payload")))
            .map(this::parsePayload)
            .checkpoint();
    }

    @Override
    protected final Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
        return Mono.just(getRoot());
    }

    private Map<String, String> parsePayload(Map<String, Map<String, Map<String, String>>> payload) {
        return payload.get("links").entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get("href")));
    }

}
