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
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

final class ReactorDopplerEndpoints extends AbstractDopplerOperations {

    ReactorDopplerEndpoints(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    Flux<Envelope> containerMetrics(ContainerMetricsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "containermetrics"), MultipartCodec::createDecoder, MultipartCodec::decode).map(ReactorDopplerEndpoints::toEnvelope)
            .checkpoint();
    }

    Flux<Envelope> firehose(FirehoseRequest request) {
        return ws(builder -> builder.pathSegment("firehose", request.getSubscriptionId())).map(ReactorDopplerEndpoints::toEnvelope)
            .checkpoint();
    }

    Flux<Envelope> recentLogs(RecentLogsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "recentlogs"), MultipartCodec::createDecoder, MultipartCodec::decode).map(ReactorDopplerEndpoints::toEnvelope)
            .checkpoint();
    }

    Flux<Envelope> stream(StreamRequest request) {
        return ws(builder -> builder.pathSegment("apps", request.getApplicationId(), "stream")).map(ReactorDopplerEndpoints::toEnvelope)
            .checkpoint();
    }

    private static Envelope toEnvelope(InputStream content) {
        try (InputStream in = content) {
            return Envelope.from(org.cloudfoundry.dropsonde.events.Envelope.ADAPTER.decode(in));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
