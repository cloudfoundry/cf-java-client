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

/**
 * A {@link RootProvider} that returns endpoints extracted from the `/v2/info` API for the configured endpoint.
 */
@Value.Immutable
abstract class _InfoPayloadRootProvider extends AbstractRootProvider {

    protected Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
        return Mono.just(getRoot());
    }

    protected Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
        return getInfo(connectionContext)
            .map(info -> {
                if (!info.containsKey(key)) {
                    throw new IllegalArgumentException(String.format("Info payload does not contain key '%s'", key));
                }

                return normalize(UriComponentsBuilder.fromUriString(info.get(key)));
            });
    }

    abstract ObjectMapper getObjectMapper();

    private UriComponentsBuilder buildInfoUri(UriComponentsBuilder root) {
        return root.pathSegment("v2", "info");
    }

    @SuppressWarnings("unchecked")
    @Value.Derived
    private Mono<Map<String, String>> getInfo(ConnectionContext connectionContext) {
        return createOperator(connectionContext)
            .flatMap(operator -> operator.get()
                .uri(this::buildInfoUri)
                .response()
                .parseBody(Map.class))
            .map(payload -> (Map<String, String>) payload)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Info endpoint does not contain a payload")))
            .checkpoint();
    }

}
