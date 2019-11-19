/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.reactor.loggregator;

import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.loggregator.v2.LoggregatorClient;
import org.cloudfoundry.loggregator.v2.StreamRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Value.Immutable
abstract class _ReactorLoggregatorClient implements LoggregatorClient {
    @Override
    public Flux<Envelope> stream(StreamRequest request) {
        return getLoggregatorEndpoints().stream(request);
    }

    @Value.Derived
    ReactorLoggregatorEndpoints getLoggregatorEndpoints() {
        return new ReactorLoggregatorEndpoints(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Value.Default
    Mono<String> getRoot() {
        return getConnectionContext().getRootProvider().getRoot("log_stream", getConnectionContext());
    }

    /**
     * The connection context
     */
    abstract ConnectionContext getConnectionContext();

    /**
     * The token provider
     */
    abstract TokenProvider getTokenProvider();
}
