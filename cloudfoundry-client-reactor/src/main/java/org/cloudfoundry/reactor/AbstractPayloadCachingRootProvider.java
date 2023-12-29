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

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

abstract class AbstractPayloadCachingRootProvider extends AbstractRootProvider {

    private final ConcurrentMap<ConnectionContext, Mono<Map<String, String>>> payloads =
            new ConcurrentHashMap<>(1);

    protected abstract Mono<Map<String, String>> doGetPayload(ConnectionContext connectionContext);

    @Override
    protected final Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
        return this.payloads
                .computeIfAbsent(connectionContext, this::getPayload)
                .map(
                        payload -> {
                            if (!payload.containsKey(key)) {
                                throw new IllegalArgumentException(
                                        String.format("Payload does not contain key '%s;", key));
                            }

                            return normalize(UriComponentsBuilder.fromUriString(payload.get(key)));
                        });
    }

    abstract ObjectMapper getObjectMapper();

    private Mono<Map<String, String>> getPayload(ConnectionContext connectionContext) {
        Mono<Map<String, String>> cached = doGetPayload(connectionContext);

        return connectionContext.getCacheDuration().map(cached::cache).orElseGet(cached::cache);
    }
}
