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

/**
 * A {@link RootProvider} that returns endpoints by delegating to an {@link RootPayloadRootProvider} and then an {@link InfoPayloadRootProvider}.
 */
@Value.Immutable
abstract class _DelegatingRootProvider extends AbstractRootProvider {

    @Override
    protected Mono<UriComponents> doGetRoot(ConnectionContext connectionContext) {
        return getRootPayloadRootProvider().doGetRoot(connectionContext)
            .onErrorResume(t -> getInfoPayloadRootProvider().doGetRoot(connectionContext));
    }

    @Override
    protected Mono<UriComponents> doGetRoot(String key, ConnectionContext connectionContext) {
        return getRootPayloadRootProvider().doGetRoot(key, connectionContext)
            .onErrorResume(t -> {
                if ("cloud_controller_v2".equals(key)) {
                    return getInfoPayloadRootProvider().doGetRoot(connectionContext)
                        .map(uri -> UriComponentsBuilder.newInstance().uriComponents(uri).pathSegment("v2").build());
                } else if ("cloud_controller_v3".equals(key)) {
                    return getInfoPayloadRootProvider().doGetRoot(connectionContext)
                        .map(uri -> UriComponentsBuilder.newInstance().uriComponents(uri).pathSegment("v3").build());
                } else if ("logging".equals(key)) {
                    return getInfoPayloadRootProvider().doGetRoot("doppler_logging_endpoint", connectionContext);
                } else if ("routing".equals(key)) {
                    return getInfoPayloadRootProvider().doGetRoot("routing_endpoint", connectionContext);
                } else if ("uaa".equals(key)) {
                    return getInfoPayloadRootProvider().doGetRoot("token_endpoint", connectionContext);
                } else {
                    return getInfoPayloadRootProvider().doGetRoot(key, connectionContext);
                }
            });
    }

    @Value.Derived
    InfoPayloadRootProvider getInfoPayloadRootProvider() {
        return InfoPayloadRootProvider.builder()
            .apiHost(getApiHost())
            .objectMapper(getObjectMapper())
            .port(getPort())
            .secure(getSecure())
            .build();
    }

    abstract ObjectMapper getObjectMapper();

    @Value.Derived
    RootPayloadRootProvider getRootPayloadRootProvider() {
        return RootPayloadRootProvider.builder()
            .apiHost(getApiHost())
            .objectMapper(getObjectMapper())
            .port(getPort())
            .secure(getSecure())
            .build();
    }

}
