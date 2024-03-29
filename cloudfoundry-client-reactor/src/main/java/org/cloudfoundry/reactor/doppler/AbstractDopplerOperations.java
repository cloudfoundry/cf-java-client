/*
 * Copyright 2013-2021 the original author or authors.
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

import io.netty.channel.ChannelHandler;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientResponse;

abstract class AbstractDopplerOperations extends AbstractReactorOperations {

    AbstractDopplerOperations(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    final <T> Flux<T> get(
            Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
            Function<HttpClientResponse, ChannelHandler> channelHandlerBuilder,
            Function<ByteBufFlux, Flux<T>> bodyTransformer) {
        return createOperator()
                .flatMapMany(
                        operator ->
                                operator.get()
                                        .uri(uriTransformer)
                                        .response()
                                        .addChannelHandler(channelHandlerBuilder)
                                        .parseBodyToFlux(
                                                responseWithBody ->
                                                        bodyTransformer.apply(
                                                                responseWithBody.getBody())));
    }

    final Flux<InputStream> ws(
            Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
                .flatMapMany(operator -> operator.websocket().uri(uriTransformer).get());
    }
}
