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

package org.cloudfoundry.reactor.routing.v1;

import io.netty.channel.ChannelHandler;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientResponse;

import java.util.Map;
import java.util.function.Function;

public abstract class AbstractRoutingV1Operations extends AbstractReactorOperations {

    protected AbstractRoutingV1Operations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    protected final <T> Mono<T> get(Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.get()
                .uri(uriTransformer)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Flux<T> get(Function<HttpClientResponse, ChannelHandler> handlerBuilder, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                    Function<ByteBufFlux, Flux<T>> bodyTransformer) {
        return createOperator()
            .flatMapMany(operator -> operator.get()
                .uri(uriTransformer)
                .response()
                .addChannelHandler(handlerBuilder)
                .parseBodyToFlux(responseWithBody -> bodyTransformer.apply(responseWithBody.getBody())));
    }

    protected final <T> Mono<T> post(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.post()
                .uri(uriTransformer)
                .send(request)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> put(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.put()
                .uri(uriTransformer)
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

}
