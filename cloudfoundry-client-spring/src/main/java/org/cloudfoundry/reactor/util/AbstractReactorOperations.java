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


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpClientRequest;
import reactor.io.netty.http.HttpClientResponse;
import reactor.io.netty.http.HttpException;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractReactorOperations {

    protected static final AsciiString APPLICATION_JSON = new AsciiString("application/json");

    protected static final AsciiString APPLICATION_X_WWW_FORM_URLENCODED = new AsciiString("application/x-www-form-urlencoded");

    protected static final AsciiString APPLICATION_ZIP = new AsciiString("application/zip");

    protected static final AsciiString AUTHORIZATION = new AsciiString("Authorization");

    protected static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");

    private static final String CF_WARNINGS = "X-Cf-Warnings";

    private final AuthorizationProvider authorizationProvider;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final Logger requestLogger = LoggerFactory.getLogger("cloudfoundry-client.request");

    private final Logger responseLogger = LoggerFactory.getLogger("cloudfoundry-client.response");

    private final Mono<String> root;

    protected AbstractReactorOperations(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        this.authorizationProvider = authorizationProvider;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.root = root;
    }

    protected final <T> Mono<T> doDelete(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                         Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.httpClient.delete(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(requestTransformer)
                .then(o -> o.send(serializedRequest(o, request))))
                .doOnSubscribe(s -> this.requestLogger.debug("DELETE {}", uri))
                .compose(logResponse(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doGet(Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return doGet(uriTransformer, requestTransformer)
            .compose(deserializedResponse(responseType));
    }

    protected final Mono<HttpClientResponse> doGet(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.httpClient.get(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(requestTransformer)
                .then(HttpClientRequest::sendHeaders))
                .log("stream.afterSendHeaders")
                .doOnSubscribe(s -> this.requestLogger.debug("GET    {}", uri))
                .compose(logResponse(uri)));
    }

    protected final <T> Mono<T> doPatch(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                        Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.httpClient.patch(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(requestTransformer)
                .then(o -> o.send(serializedRequest(o, request))))
                .doOnSubscribe(s -> this.requestLogger.debug("PATCH  {}", uri))
                .compose(logResponse(uri)))
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
            .then(uri -> this.httpClient.post(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .then(requestTransformer))
                .doOnSubscribe(s -> this.requestLogger.debug("POST   {}", uri))
                .compose(logResponse(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doPut(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                      Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.httpClient.put(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(requestTransformer)
                .then(o -> o.send(serializedRequest(o, request))))
                .doOnSubscribe(s -> this.requestLogger.debug("PUT    {}", uri))
                .compose(logResponse(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final <T> Mono<T> doPut(Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, Mono<Void>> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.httpClient.put(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .then(requestTransformer))
                .doOnSubscribe(s -> this.requestLogger.debug("PUT    {}", uri))
                .compose(logResponse(uri)))
            .compose(deserializedResponse(responseType));
    }

    protected final Mono<HttpClientResponse> doWs(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpClientRequest, HttpClientRequest> requestTransformer) {
        return this.root
            .map(root -> buildUri(root, uriTransformer))
            .then(uri -> this.httpClient.get(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(requestTransformer)
                .then(HttpClientRequest::upgradeToTextWebsocket))
                .doOnSubscribe(s -> this.requestLogger.debug("WS     {}", uri))
                .compose(logResponse(uri)));
    }

    private static String buildUri(String root, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return uriTransformer
            .apply(UriComponentsBuilder.fromUriString(root))
            .build().encode().toUriString();
    }

    private <T> Function<Mono<HttpClientResponse>, Mono<T>> deserializedResponse(Class<T> responseType) {
        return inbound -> inbound
            .then(i -> i.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.objectMapper, responseType));
    }

    private Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> logResponse(String uri) {
        return inbound -> inbound
            .doOnSuccess(i -> {
                List<String> warnings = i.responseHeaders().getAll(CF_WARNINGS);

                if (warnings.isEmpty()) {
                    this.responseLogger.debug("{}    {}", i.status().code(), uri);
                } else {
                    this.responseLogger.warn("{}    {} ({})", i.status().code(), uri, StringUtils.collectionToCommaDelimitedString(warnings));
                }
            })
            .doOnError(t -> {
                if (t instanceof HttpException) {
                    this.responseLogger.debug("{}    {}", ((HttpException) t).getResponseStatus().code(), uri);
                }
            });
    }

    private Mono<ByteBuf> serializedRequest(HttpClientRequest outbound, Object request) {
        return Mono.just(request)
            .filter(req -> this.objectMapper.canSerialize(req.getClass()))
            .map(JsonCodec.encode(this.objectMapper, outbound));
    }

}
