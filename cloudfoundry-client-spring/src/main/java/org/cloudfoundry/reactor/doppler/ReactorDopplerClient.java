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
import org.cloudfoundry.doppler.CounterEvent;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.doppler.Error;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.doppler.HttpStart;
import org.cloudfoundry.doppler.HttpStartStop;
import org.cloudfoundry.doppler.HttpStop;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.doppler.ValueMetric;
import org.cloudfoundry.dropsonde.events.Envelope;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpInbound;

import java.io.IOException;
import java.io.InputStream;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

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
        return get(request, function((builder, validRequest) -> builder.pathSegment("apps", validRequest.getApplicationId(), "containermetrics")))
            .flatMap(inbound -> inbound.receiveMultipart().receiveInputStream())
            .map(ReactorDopplerClient::toEnvelope)
            .map(ReactorDopplerClient::toEvent);
    }

    @Override
    public Flux<Event> firehose(FirehoseRequest request) {
        return ws(request, function((builder, validRequest) -> builder.pathSegment("firehose", validRequest.getSubscriptionId())))
            .flatMap(HttpInbound::receiveInputStream)
            .map(ReactorDopplerClient::toEnvelope)
            .map(ReactorDopplerClient::toEvent);
    }

    @Override
    public Flux<LogMessage> recentLogs(RecentLogsRequest request) {
        return get(request, function((builder, validRequest) -> builder.pathSegment("apps", validRequest.getApplicationId(), "recentlogs")))
            .flatMap(inbound -> inbound.receiveMultipart().receiveInputStream())
            .map(ReactorDopplerClient::toEnvelope)
            .map(ReactorDopplerClient::toEvent);
    }

    @Override
    public Flux<Event> stream(StreamRequest request) {
        return ws(request, function((builder, validRequest) -> builder.pathSegment("apps", validRequest.getApplicationId(), "stream")))
            .flatMap(HttpInbound::receiveInputStream)
            .map(ReactorDopplerClient::toEnvelope)
            .map(ReactorDopplerClient::toEvent);
    }

    private static Envelope toEnvelope(InputStream inputStream) {
        try (InputStream in = inputStream) {
            return Envelope.ADAPTER.decode(in);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> T toEvent(Envelope envelope) {
        switch (envelope.eventType) {
            case HttpStart:
                return (T) HttpStart.builder()
                    .dropsonde(envelope.httpStart)
                    .build();
            case HttpStop:
                return (T) HttpStop.builder()
                    .dropsonde(envelope.httpStop)
                    .build();
            case HttpStartStop:
                return (T) HttpStartStop.builder()
                    .dropsonde(envelope.httpStartStop)
                    .build();
            case LogMessage:
                return (T) LogMessage.builder()
                    .dropsonde(envelope.logMessage)
                    .build();
            case ValueMetric:
                return (T) ValueMetric.builder()
                    .dropsonde(envelope.valueMetric)
                    .build();
            case CounterEvent:
                return (T) CounterEvent.builder()
                    .dropsonde(envelope.counterEvent)
                    .build();
            case Error:
                return (T) Error.builder()
                    .dropsonde(envelope.error)
                    .build();
            case ContainerMetric:
                return (T) ContainerMetric.builder()
                    .dropsonde(envelope.containerMetric)
                    .build();
            default:
                throw new IllegalStateException(String.format("Envelope event type %s is unsupported", envelope.eventType));
        }
    }

}
