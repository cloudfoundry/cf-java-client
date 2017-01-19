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

package org.cloudfoundry.reactor.tokenprovider;

import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Value.Immutable
abstract class _BasicAuthTokenProvider implements TokenProvider {

    private final ConcurrentMap<ConnectionContext, Mono<String>> tokens = new ConcurrentHashMap<>(1);

    @Override
    public Mono<String> getToken(ConnectionContext connectionContext) {
        return this.tokens.computeIfAbsent(connectionContext, c -> {
            String encoded = Base64.getEncoder().encodeToString(new AsciiString(getUsername()).concat(":").concat(getPassword()).toByteArray());
            return Mono.just(String.format("Basic %s", encoded));
        });
    }

    /**
     * The password
     */
    abstract String getPassword();

    /**
     * The username
     */
    abstract String getUsername();

}
