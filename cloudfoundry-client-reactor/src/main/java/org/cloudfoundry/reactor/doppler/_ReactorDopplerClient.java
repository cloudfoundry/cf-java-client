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

package org.cloudfoundry.reactor.doppler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.Nullable;
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
import reactor.ipc.netty.http.client.HttpClient;

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

    @Nullable
    abstract ConnectionContext getConnectionContext();

    @Value.Derived
    ReactorDopplerEndpoints getDopplerEndpoints() {
        return new ReactorDopplerEndpoints(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Value.Default
    HttpClient getHttpClient() {
        return getConnectionContext().getHttpClient();
    }

    @Value.Default
    ObjectMapper getObjectMapper() {
        return getConnectionContext().getObjectMapper();
    }

    @Value.Default
    Mono<String> getRoot() {
        return getConnectionContext().getRoot("doppler_logging_endpoint");
    }

    abstract TokenProvider getTokenProvider();


}
