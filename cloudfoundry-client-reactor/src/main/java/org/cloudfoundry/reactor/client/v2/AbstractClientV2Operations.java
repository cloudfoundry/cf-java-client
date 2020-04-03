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

package org.cloudfoundry.reactor.client.v2;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.DelegatingUriQueryParameterBuilder;
import org.cloudfoundry.reactor.util.ErrorPayloadMappers;
import org.cloudfoundry.reactor.util.MultipartHttpClientRequest;
import org.cloudfoundry.reactor.util.Operator;
import org.cloudfoundry.reactor.util.UriQueryParameter;
import org.cloudfoundry.reactor.util.UriQueryParameterBuilder;
import org.cloudfoundry.reactor.util.UriQueryParameters;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractClientV2Operations extends AbstractReactorOperations {

    protected AbstractClientV2Operations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    protected Mono<Operator> createOperator() {
        return super.createOperator().map(this::attachErrorPayloadMapper);
    }

    protected final <T> Mono<T> delete(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.delete()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Flux<T> get(Object requestPayload, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<ByteBufFlux, Flux<T>> bodyTransformer) {
        return createOperator()
            .flatMapMany(operator -> operator.followRedirects()
                .get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .parseBodyToFlux(responseWithBody -> bodyTransformer.apply(responseWithBody.getBody())));
    }

    protected final <T> Mono<T> get(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> post(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.post()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> put(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                    Consumer<MultipartHttpClientRequest> requestTransformer, Runnable onTerminate) {
        return createOperator()
            .flatMap(operator -> operator.put()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .sendForm(multipartRequest(requestTransformer))
                .response()
                .parseBody(responseType))
            .doFinally(signalType -> onTerminate.run());
    }

    protected final <T> Mono<T> put(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.put()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    private Operator attachErrorPayloadMapper(Operator operator) {
        return operator.withErrorPayloadMapper(ErrorPayloadMappers.clientV2(this.connectionContext.getObjectMapper()));
    }

    private MultipartHttpClientRequest createMultipartRequest(HttpClientRequest request, HttpClientForm form) {
        return new MultipartHttpClientRequest(this.connectionContext.getObjectMapper(), request, form);
    }

    private UriQueryParameterBuilder getUriQueryParameterBuilder() {
        return DelegatingUriQueryParameterBuilder.builder().builders(new FilterBuilder(), new QueryBuilder()).build();
    }

    private BiConsumer<HttpClientRequest, HttpClientForm> multipartRequest(Consumer<MultipartHttpClientRequest> requestTransformer) {
        return (request, form) -> {
            MultipartHttpClientRequest multipartRequest = createMultipartRequest(request, form);
            requestTransformer.accept(multipartRequest);
        };
    }

    private Function<UriComponentsBuilder, UriComponentsBuilder> queryTransformer(Object requestPayload) {
        return builder -> {
            Stream<UriQueryParameter> parameters = getUriQueryParameterBuilder().build(requestPayload);
            UriQueryParameters.set(builder, parameters);
            return builder;
        };
    }

}
