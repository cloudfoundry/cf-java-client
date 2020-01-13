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

package org.cloudfoundry.reactor.doppler;

import org.cloudfoundry.doppler.ContainerMetricsRequest;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link DopplerClient}
 */
@Value.Immutable
abstract class _ReactorDopplerClient implements DopplerClient {

    @Override
    public Flux<Envelope> containerMetrics(ContainerMetricsRequest request) {
        return getDopplerEndpoints().containerMetrics(request);
    }

    @Override
    public Flux<Envelope> firehose(FirehoseRequest request) {
        return getDopplerEndpoints().firehose(request);
    }

    @Override
    public Flux<Envelope> recentLogs(RecentLogsRequest request) {
        return getDopplerEndpoints().recentLogs(request);
    }

    @Override
    public Flux<Envelope> stream(StreamRequest request) {
        return getDopplerEndpoints().stream(request);
    }

    /**
     * The connection context
     */
    abstract ConnectionContext getConnectionContext();

    @Value.Derived
    ReactorDopplerEndpoints getDopplerEndpoints() {
        return new ReactorDopplerEndpoints(getConnectionContext(), getRoot(), getTokenProvider(), getRequestTags());
    }

    @Value.Default
    Map<String, String> getRequestTags() {
        return Collections.emptyMap();
    }

    @Value.Default
    Mono<String> getRoot() {
        return getConnectionContext().getRootProvider().getRoot("logging", getConnectionContext());
    }

    /**
     * The token provider
     */
    abstract TokenProvider getTokenProvider();

}
