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

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link RootProvider} that returns endpoints extracted from the `/` API for the configured endpoint.
 */
@Value.Immutable
abstract class _RootPayloadRootProvider extends AbstractRootProvider {

    @Override
    protected Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
        return Mono.just(getRoot());
    }

    @Override
    protected Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
        return getPayload(connectionContext)
            .map(payload -> {
                if (!payload.containsKey(key)) {
                    throw new IllegalArgumentException(String.format("Root payload does not contain key '%s'", key));
                }

                return normalize(UriComponentsBuilder.fromUriString(payload.get(key)));
            });
    }

    abstract ObjectMapper getObjectMapper();

    @SuppressWarnings("unchecked")
    @Value.Derived
    private Mono<Map<String, String>> getPayload(ConnectionContext connectionContext) {
        return createOperator(connectionContext)
            .flatMap(operator -> operator.get()
                .uri(Function.identity())
                .response()
                .parseBody(Map.class))
            .map(payload -> (Map<String, Map<String, Map<String, String>>>) payload)
            .map(this::processPayload)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Root endpoint does not contain a payload")))
            .checkpoint();
    }

    private Map<String, String> processPayload(Map<String, Map<String, Map<String, String>>> payload) {
        return payload.get("links").entrySet().stream()
            .filter(item -> null != item.getValue())
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get("href")));
    }

}
