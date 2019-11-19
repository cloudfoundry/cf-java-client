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

import io.netty.channel.ChannelHandler;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.client.v3.FilterBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientResponse;

import java.util.function.Function;


abstract class AbstractLoggregatorOperations extends AbstractReactorOperations {

    protected AbstractLoggregatorOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    <T> Flux<T> get(Object requestPayload, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientResponse, ChannelHandler> channelHandlerBuilder,
                    Function<ByteBufFlux, Flux<T>> bodyTransformer) {
        return createOperator().flatMapMany(operator -> operator.get()
            .uri(queryTransformer(requestPayload).andThen(uriTransformer))
            .response()
            .addChannelHandler(channelHandlerBuilder)
            .parseBodyToFlux(responseWithBody -> bodyTransformer.apply(responseWithBody.getBody())));
    }

    private static Function<UriComponentsBuilder, UriComponentsBuilder> queryTransformer(Object requestPayload) {
        return builder -> {
            FilterBuilder.augment(builder, requestPayload);
            QueryBuilder.augment(builder, requestPayload);

            return builder;
        };
    }

}
