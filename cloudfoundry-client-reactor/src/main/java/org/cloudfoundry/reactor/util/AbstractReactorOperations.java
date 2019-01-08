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

package org.cloudfoundry.reactor.util;


import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.reactivestreams.Publisher;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.function.Function;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public abstract class AbstractReactorOperations {

    protected static final AsciiString APPLICATION_ZIP = new AsciiString("application/zip");

    private final ConnectionContext connectionContext;

    private final Mono<String> root;

    private final TokenProvider tokenProvider;

    protected AbstractReactorOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        this.connectionContext = connectionContext;
        this.root = root;
        this.tokenProvider = tokenProvider;
    }

    protected final <T> Mono<T> doDelete(Object requestPayload, Class<T> responseType,
                                         Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                         Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                         Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {

        return doDelete(requestPayload, uriTransformer, requestTransformer, responseTransformer)
            .transform(deserializedResponse(responseType));
    }

    protected final Mono<HttpClientResponse> doDelete(Object requestPayload,
                                                      Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                                      Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                                      Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {
        return this.root
            .transform(transformUri(uriTransformer))
            .flatMap(uri -> this.connectionContext.getHttpClient()
                .delete(uri, request -> Mono.just(request)
                    .map(AbstractReactorOperations::disableFailOnError)
                    .transform(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .map(JsonCodec::addDecodeHeaders)
                    .transform(requestTransformer)
                    .transform(serializedRequest(requestPayload)))
                .doOnSubscribe(NetworkLogging.delete(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(this::invalidateToken)
            .transform(responseTransformer)
            .transform(ErrorPayloadMapper.fallback());
    }

    protected final <T> Mono<T> doGet(Class<T> responseType,
                                      Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                      Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                      Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {

        return doGet(uriTransformer,
            outbound -> outbound
                .map(JsonCodec::addDecodeHeaders)
                .transform(requestTransformer),
            inbound -> inbound
                .transform(responseTransformer))
            .transform(deserializedResponse(responseType));
    }

    protected final Mono<HttpClientResponse> doGet(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                                   Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                                   Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {
        return this.root
            .transform(transformUri(uriTransformer))
            .flatMap(uri -> this.connectionContext.getHttpClient()
                .get(uri, request -> Mono.just(request)
                    .map(AbstractReactorOperations::disableFailOnError)
                    .transform(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .transform(requestTransformer)
                    .flatMap(HttpClientRequest::send))
                .doOnSubscribe(NetworkLogging.get(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(this::invalidateToken)
            .transform(responseTransformer)
            .transform(ErrorPayloadMapper.fallback());
    }

    protected final <T> Mono<T> doPatch(Object requestPayload, Class<T> responseType,
                                        Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                        Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                        Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {

        return doPatch(responseType, uriTransformer,
            outbound -> outbound
                .transform(requestTransformer)
                .transform(serializedRequest(requestPayload)),
            responseTransformer);
    }

    protected final <T> Mono<T> doPatch(Class<T> responseType,
                                        Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                        Function<Mono<HttpClientRequest>, Publisher<Void>> requestTransformer,
                                        Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {
        return this.root
            .transform(transformUri(uriTransformer))
            .flatMap(uri -> this.connectionContext.getHttpClient()
                .patch(uri, request -> Mono.just(request)
                    .map(AbstractReactorOperations::disableChunkedTransfer)
                    .map(AbstractReactorOperations::disableFailOnError)
                    .transform(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .map(JsonCodec::addDecodeHeaders)
                    .transform(requestTransformer))
                .doOnSubscribe(NetworkLogging.patch(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(this::invalidateToken)
            .transform(responseTransformer)
            .transform(ErrorPayloadMapper.fallback())
            .transform(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doPost(Object requestPayload, Class<T> responseType,
                                       Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                       Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                       Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {

        return doPost(responseType, uriTransformer,
            outbound -> outbound
                .transform(requestTransformer)
                .transform(serializedRequest(requestPayload)),
            responseTransformer);
    }

    protected final <T> Mono<T> doPost(Class<T> responseType,
                                       Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                       Function<Mono<HttpClientRequest>, Publisher<Void>> requestTransformer,
                                       Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {
        return this.root
            .transform(transformUri(uriTransformer))
            .flatMap(uri -> this.connectionContext.getHttpClient()
                .post(uri, request -> Mono.just(request)
                    .map(AbstractReactorOperations::disableChunkedTransfer)
                    .map(AbstractReactorOperations::disableFailOnError)
                    .transform(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .map(JsonCodec::addDecodeHeaders)
                    .transform(requestTransformer))
                .doOnSubscribe(NetworkLogging.post(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(this::invalidateToken)
            .transform(responseTransformer)
            .transform(ErrorPayloadMapper.fallback())
            .transform(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doPut(Object requestPayload, Class<T> responseType,
                                      Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                      Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                      Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {

        return doPut(responseType, uriTransformer,
            outbound -> outbound
                .transform(requestTransformer)
                .transform(serializedRequest(requestPayload)),
            responseTransformer);
    }

    protected final <T> Mono<T> doPut(Class<T> responseType,
                                      Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                      Function<Mono<HttpClientRequest>, Publisher<Void>> requestTransformer,
                                      Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {
        return this.root
            .transform(transformUri(uriTransformer))
            .flatMap(uri -> this.connectionContext.getHttpClient()
                .put(uri, request -> Mono.just(request)
                    .map(AbstractReactorOperations::disableChunkedTransfer)
                    .map(AbstractReactorOperations::disableFailOnError)
                    .transform(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .map(JsonCodec::addDecodeHeaders)
                    .transform(requestTransformer))
                .doOnSubscribe(NetworkLogging.put(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(this::invalidateToken)
            .transform(responseTransformer)
            .transform(ErrorPayloadMapper.fallback())
            .transform(deserializedResponse(responseType));
    }

    protected final Mono<HttpClientResponse> doWs(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                                  Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer,
                                                  Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> responseTransformer) {
        return this.root
            .transform(transformUri(uriTransformer))
            .flatMap(uri -> this.connectionContext.getHttpClient()
                .get(uri, request -> Mono.just(request)
                    .map(AbstractReactorOperations::disableFailOnError)
                    .transform(this::addAuthorization)
                    .map(UserAgent::addUserAgent)
                    .transform(requestTransformer)
                    .flatMapMany(HttpClientRequest::sendWebsocket))
                .doOnSubscribe(NetworkLogging.ws(uri))
                .transform(NetworkLogging.response(uri)))
            .transform(this::invalidateToken)
            .transform(responseTransformer)
            .transform(ErrorPayloadMapper.fallback());
    }

    private static HttpClientRequest disableChunkedTransfer(HttpClientRequest request) {
        return request.chunkedTransfer(false);
    }

    private static HttpClientRequest disableFailOnError(HttpClientRequest request) {
        return request
            .failOnClientError(false)
            .failOnServerError(false);
    }

    private static boolean isUnauthorized(HttpClientResponse response) {
        return response.status() == HttpResponseStatus.UNAUTHORIZED;
    }

    private static Function<Mono<String>, Mono<String>> transformUri(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return uri -> uri
            .map(UriComponentsBuilder::fromUriString)
            .map(uriTransformer)
            .map(builder -> builder.build().encode().toUriString());
    }

    private Mono<HttpClientRequest> addAuthorization(Mono<HttpClientRequest> outbound) {
        return Mono
            .zip(outbound, this.tokenProvider.getToken(this.connectionContext))
            .map(function((request, token) -> request.header(AUTHORIZATION, token)));
    }

    private <T> Function<Mono<HttpClientResponse>, Mono<T>> deserializedResponse(Class<T> responseType) {
        return inbound -> inbound
            .transform(JsonCodec.decode(this.connectionContext.getObjectMapper(), responseType))
            .doOnNext(response -> NetworkLogging.RESPONSE_LOGGER.trace("       {}", response))
            .doOnError(JsonParsingException.class, e -> NetworkLogging.RESPONSE_LOGGER.error("{}\n{}", e.getCause().getMessage(), e.getPayload()));
    }

    private Mono<HttpClientResponse> invalidateToken(Mono<HttpClientResponse> inbound) {
        return inbound
            .flatMap(response -> {
                if (isUnauthorized(response)) {
                    this.tokenProvider.invalidate(this.connectionContext);
                    return inbound
                        .transform(this::invalidateToken);
                } else {
                    return Mono.just(response);
                }
            });
    }

    private Function<Mono<HttpClientRequest>, Publisher<Void>> serializedRequest(Object requestPayload) {
        return outbound -> outbound
            .doOnNext(request -> NetworkLogging.REQUEST_LOGGER.trace("       {}", requestPayload))
            .transform(JsonCodec.encode(this.connectionContext.getObjectMapper(), requestPayload));
    }

}
