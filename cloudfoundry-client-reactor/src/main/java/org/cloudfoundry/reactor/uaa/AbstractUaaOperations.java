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

package org.cloudfoundry.reactor.uaa;

import io.netty.handler.codec.http.HttpHeaders;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.ErrorPayloadMappers;
import org.cloudfoundry.reactor.util.Operator;
import org.cloudfoundry.reactor.util.UriQueryParameter;
import org.cloudfoundry.reactor.util.UriQueryParameters;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractUaaOperations extends AbstractReactorOperations {

    protected AbstractUaaOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    protected Mono<Operator> createOperator() {
        return super.createOperator().map(this::attachErrorPayloadMapper);
    }

    protected final <T> Mono<T> delete(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload))
                .delete()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    protected final Mono<HttpClientResponse> get(Object requestPayload, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload))
                .get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .get());
    }

    protected final Mono<HttpClientResponse> get(Object requestPayload, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Consumer<HttpHeaders> headersTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload, headersTransformer))
                .get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .get());
    }

    protected final Mono<HttpClientResponse> get(Object requestPayload, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Consumer<HttpHeaders> headersTransformer,
                                                 Function<HttpHeaders, Mono<? extends HttpHeaders>> headersWhenTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload, headersTransformer)).headersWhen(headersWhenTransformer)
                .get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .get());
    }

    protected final <T> Mono<T> get(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload))
                .get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> get(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Consumer<HttpHeaders> headersTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload, headersTransformer))
                .get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> patch(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload))
                .patch()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> post(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Consumer<HttpHeaders> headersTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload, headersTransformer))
                .post()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> post(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Consumer<HttpHeaders> headersTransformer,
                                     Function<HttpHeaders, Mono<? extends HttpHeaders>> headersWhenTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload, headersTransformer)).headersWhen(headersWhenTransformer)
                .post()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> post(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload))
                .post()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> put(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.headers(headers -> addHeaders(headers, requestPayload))
                .put()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    private static void addHeaders(HttpHeaders httpHeaders, Object requestPayload, Consumer<HttpHeaders> headersTransformer) {
        addHeaders(httpHeaders, requestPayload);
        headersTransformer.accept(httpHeaders);
    }

    private static void addHeaders(HttpHeaders httpHeaders, Object requestPayload) {
        IdentityZoneBuilder.augment(httpHeaders, requestPayload);
        VersionBuilder.augment(httpHeaders, requestPayload);
    }

    private Operator attachErrorPayloadMapper(Operator operator) {
        return operator.withErrorPayloadMapper(ErrorPayloadMappers.uaa(this.connectionContext.getObjectMapper()));
    }

    private Function<UriComponentsBuilder, UriComponentsBuilder> queryTransformer(Object requestPayload) {
        return builder -> {
            Stream<UriQueryParameter> parameters = new QueryBuilder().build(requestPayload);
            UriQueryParameters.set(builder, parameters);
            return builder;
        };
    }

}
