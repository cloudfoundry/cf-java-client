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

import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.cloudfoundry.doppler.ContainerMetricsRequest;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.util.MultipartDecoderChannelHandler;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.ByteBufFlux;
import reactor.ipc.netty.NettyInbound;

import java.io.IOException;

final class ReactorDopplerEndpoints extends AbstractDopplerOperations {

    ReactorDopplerEndpoints(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    Flux<Envelope> containerMetrics(ContainerMetricsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "containermetrics"))
            .flatMap(inbound -> inbound.addHandler(new MultipartDecoderChannelHandler(inbound)).receiveObject())
            .takeWhile(t -> MultipartDecoderChannelHandler.CLOSE_DELIMITER != t)
            .window()
            .concatMap(w -> w
                .takeWhile(t -> MultipartDecoderChannelHandler.DELIMITER != t)
                .as(ByteBufFlux::fromInbound)
                .aggregate()
                .asByteArray(), Integer.MAX_VALUE)
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    Flux<Envelope> firehose(FirehoseRequest request) {
        return ws(builder -> builder.pathSegment("firehose", request.getSubscriptionId()))
            .flatMap(NettyInbound::receiveObject)  // TODO: Replace with proper alias from reactor-netty 0.6.0
            .cast(WebSocketFrame.class)
            .window()
            .concatMap(w -> w
                .takeUntil(WebSocketFrame::isFinalFragment)
                .as(ByteBufFlux::fromInbound)
                .aggregate()
                .asByteArray(), Integer.MAX_VALUE)
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    Flux<Envelope> recentLogs(RecentLogsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "recentlogs"))
            .flatMap(inbound -> inbound.addHandler(new MultipartDecoderChannelHandler(inbound)).receiveObject())
            .takeWhile(t -> MultipartDecoderChannelHandler.CLOSE_DELIMITER != t)
            .window()
            .concatMap(w -> w
                .takeWhile(t -> MultipartDecoderChannelHandler.DELIMITER != t)
                .as(ByteBufFlux::fromInbound)
                .aggregate()
                .asByteArray(), Integer.MAX_VALUE)
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    Flux<Envelope> stream(StreamRequest request) {
        return ws(builder -> builder.pathSegment("apps", request.getApplicationId(), "stream"))
            .flatMap(NettyInbound::receiveObject)  // TODO: Replace with proper alias from reactor-netty 0.6.0
            .cast(WebSocketFrame.class)
            .window()
            .concatMap(w -> w
                .takeUntil(WebSocketFrame::isFinalFragment)
                .as(ByteBufFlux::fromInbound)
                .aggregate()
                .asByteArray(), Integer.MAX_VALUE)
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    private static Envelope toEnvelope(byte[] bytes) {
        try {
            return Envelope.from(org.cloudfoundry.dropsonde.events.Envelope.ADAPTER.decode(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            throw Exceptions.propagate(e);
        }
    }

}
