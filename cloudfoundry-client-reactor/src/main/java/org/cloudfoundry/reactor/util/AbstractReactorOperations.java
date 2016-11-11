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

package org.cloudfoundry.reactor.util;


import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpClientRequest;
import reactor.ipc.netty.http.HttpClientResponse;
import reactor.ipc.netty.http.HttpOutbound;

import java.util.function.Function;

public abstract class AbstractReactorOperations {

    protected static final AsciiString APPLICATION_JSON = new AsciiString("application/json");

    protected static final AsciiString APPLICATION_X_WWW_FORM_URLENCODED = new AsciiString("application/x-www-form-urlencoded");

    protected static final AsciiString APPLICATION_ZIP = new AsciiString("application/zip");

    protected static final AsciiString AUTHORIZATION = new AsciiString("Authorization");

    protected static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");

    private final ConnectionContext connectionContext;

    private final Mono<String> root;

    private final TokenProvider tokenProvider;

    protected AbstractReactorOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        this.connectionContext = connectionContext;
        this.root = root;
        this.tokenProvider = tokenProvider;
    }

    protected final <T> Mono<T> doDelete(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                         Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.connectionContext.getHttpClient()
                .delete(uri, outbound -> addAuthorization(outbound, this.connectionContext, this.tokenProvider)
                    .map(requestTransformer)
                    .then(o -> o.send(serializedRequest(o, request))))
                .doOnSubscribe(NetworkLogging.delete(uri))
                .compose(NetworkLogging.response(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doGet(Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return doGet(uriTransformer, requestTransformer)
            .compose(deserializedResponse(responseType));
    }

    protected final Mono<HttpClientResponse> doGet(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.connectionContext.getHttpClient()
                .get(uri, outbound -> addAuthorization(outbound, this.connectionContext, this.tokenProvider)
                    .map(requestTransformer)
                    .then(HttpClientRequest::sendHeaders))
                .doOnSubscribe(NetworkLogging.get(uri))
                .compose(NetworkLogging.response(uri)));
    }

    protected final <T> Mono<T> doPatch(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                        Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.connectionContext.getHttpClient()
                .patch(uri, outbound -> addAuthorization(outbound, this.connectionContext, this.tokenProvider)
                    .map(requestTransformer)
                    .then(o -> o.send(serializedRequest(o, request))))
                .doOnSubscribe(NetworkLogging.patch(uri))
                .compose(NetworkLogging.response(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doPost(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                       Function<HttpClientRequest, HttpClientRequest> requestTransformer) {

        return doPost(responseType, uriTransformer, outbound -> requestTransformer.apply(outbound)
            .send(serializedRequest(outbound, request)));
    }

    protected final <T> Mono<T> doPost(Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, Mono<Void>> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.connectionContext.getHttpClient()
                .post(uri, outbound -> addAuthorization(outbound, this.connectionContext, this.tokenProvider)
                    .then(requestTransformer))
                .doOnSubscribe(NetworkLogging.post(uri))
                .compose(NetworkLogging.response(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doPut(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                      Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.connectionContext.getHttpClient()
                .put(uri, outbound -> addAuthorization(outbound, this.connectionContext, this.tokenProvider)
                    .map(requestTransformer)
                    .then(o -> o.send(serializedRequest(o, request))))
                .doOnSubscribe(NetworkLogging.put(uri))
                .compose(NetworkLogging.response(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doPut(Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, Mono<Void>> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.connectionContext.getHttpClient()
                .put(uri, outbound -> addAuthorization(outbound, this.connectionContext, this.tokenProvider)
                    .then(requestTransformer))
                .doOnSubscribe(NetworkLogging.put(uri))
                .compose(NetworkLogging.response(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final Mono<HttpClientResponse> doWs(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.connectionContext.getHttpClient()
                .get(uri, outbound -> addAuthorization(outbound, this.connectionContext, this.tokenProvider)
                    .map(requestTransformer)
                    .then(HttpClientRequest::upgradeToTextWebsocket))
                .doOnSubscribe(NetworkLogging.ws(uri))
                .compose(NetworkLogging.response(uri)));
    }

    private static <T extends HttpOutbound> Mono<T> addAuthorization(T outbound, ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return tokenProvider.getToken(connectionContext)
            .map(token -> {
                outbound.addHeader("Authorization", String.format("bearer %s", token));
                return outbound;
            });
    }

    private static String buildUri(String root, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return uriTransformer
            .apply(UriComponentsBuilder.fromUriString(root))
            .build().encode().toUriString();
    }

    private <T> Function<Mono<HttpClientResponse>, Mono<T>> deserializedResponse(Class<T> responseType) {
        return inbound -> inbound
            .then(i -> i.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.connectionContext.getObjectMapper(), responseType))
            .doOnError(JsonParsingException.class, e -> NetworkLogging.RESPONSE_LOGGER.debug("{}\n{}", e.getCause().getMessage(), e.getPayload()));
    }

    private Mono<ByteBuf> serializedRequest(HttpClientRequest outbound, Object request) {
        return Mono.just(request)
            .filter(req -> this.connectionContext.getObjectMapper().canSerialize(req.getClass()))
            .map(JsonCodec.encode(this.connectionContext.getObjectMapper(), outbound));
    }

}
