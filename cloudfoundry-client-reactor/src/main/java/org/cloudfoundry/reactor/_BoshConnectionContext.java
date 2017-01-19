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

import org.cloudfoundry.bosh.BoshClient;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * The BOSH implementation of the {@link ConnectionContext} interface.  This is the implementation that should be used for the {@link BoshClient}.
 */
@Value.Immutable
abstract class _BoshConnectionContext extends AbstractConnectionContext {

    private static final String TOKEN_ENDPOINT = "token_endpoint";

    public _BoshConnectionContext() {
        super(25555, "bosh-client");
    }

    @Override
    public Mono<String> getRoot(String key) {
        if (!key.equals(TOKEN_ENDPOINT)) {
            return Mono.error(new IllegalArgumentException(String.format("Only the %s key is supported", TOKEN_ENDPOINT)));
        }

        return getInfo()
            .then(info -> extractUrl(info)
                .map(url -> normalize(UriComponentsBuilder.fromUriString(url), getScheme()))
                .map(Mono::just)
                .orElse(Mono.error(new IllegalStateException(String.format("Unable to extract token_endpoint from %s", info)))))
            .doOnNext(components -> trust(components, getSslCertificateTruster()))
            .map(UriComponents::toUriString)
            .cache();
    }

    @SuppressWarnings("unchecked")
    @Value.Derived
    Mono<Map<String, Object>> getInfo() {
        return getRoot()
            .map(uri -> UriComponentsBuilder.fromUriString(uri).pathSegment("info").build().toUriString())
            .then(uri -> getHttpClient()
                .get(uri)
                .doOnSubscribe(NetworkLogging.get(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(JsonCodec.decode(getObjectMapper(), Map.class))
            .map(m -> (Map<String, Object>) m)
            .cache();
    }

    @SuppressWarnings("unchecked")
    private static Optional<String> extractUrl(Map<String, Object> info) {
        Map<String, Object> user_authentication = (Map<String, Object>) info.get("user_authentication");
        Map<String, Object> options = (Map<String, Object>) user_authentication.get("options");
        return Optional.ofNullable((String) options.get("url"));
    }

}
