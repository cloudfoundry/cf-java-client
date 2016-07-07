/*
 * Copyright 2013-2016 the original author or authors.
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
import lombok.Builder;
import org.cloudfoundry.doppler.ContainerMetric;
import org.cloudfoundry.doppler.ContainerMetricsRequest;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.EventBuilder;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpClientResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * The Reactor-based implementation of {@link DopplerClient}
 */
public final class ReactorDopplerClient extends AbstractDopplerOperations implements DopplerClient {

    @Builder
    ReactorDopplerClient(ConnectionContextSupplier cloudFoundryClient) {
        this(cloudFoundryClient.getConnectionContext().getAuthorizationProvider(), cloudFoundryClient.getConnectionContext().getHttpClient(),
            cloudFoundryClient.getConnectionContext().getObjectMapper(), cloudFoundryClient.getConnectionContext().getRoot("doppler_logging_endpoint"));
    }

    ReactorDopplerClient(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Flux<ContainerMetric> containerMetrics(ContainerMetricsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "containermetrics"))
            .flatMap(inbound -> inbound.receiveMultipart().receiveInputStream())
            .map(ReactorDopplerClient::toEnvelope)
            .map(EventBuilder::toEvent);
    }

    @Override
    public Flux<Event> firehose(FirehoseRequest request) {
        return ws(builder -> builder.pathSegment("firehose", request.getSubscriptionId()))
            .flatMap(HttpClientResponse::receiveInputStream)
            .map(ReactorDopplerClient::toEnvelope)
            .map(EventBuilder::toEvent);
    }

    @Override
    public Flux<Envelope<? extends Event>> envelopedFirehose(FirehoseRequest request) {
        return ws(builder -> builder.pathSegment("firehose", request.getSubscriptionId()))
            .flatMap(HttpInbound::receiveInputStream)
            .map(ReactorDopplerClient::toEnvelope)
            .map(Envelope::from);
    }

    @Override
    public Flux<LogMessage> recentLogs(RecentLogsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "recentlogs"))
            .flatMap(inbound -> inbound.receiveMultipart().receiveInputStream())
            .map(ReactorDopplerClient::toEnvelope)
            .map(EventBuilder::toEvent);
    }

    @Override
    public Flux<Event> stream(StreamRequest request) {
        return ws(builder -> builder.pathSegment("apps", request.getApplicationId(), "stream"))
            .flatMap(HttpClientResponse::receiveInputStream)
            .map(ReactorDopplerClient::toEnvelope)
            .map(EventBuilder::toEvent);
    }

    private static org.cloudfoundry.dropsonde.events.Envelope toEnvelope(InputStream inputStream) {
        try (InputStream in = inputStream) {
            return org.cloudfoundry.dropsonde.events.Envelope.ADAPTER.decode(in);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }



}
