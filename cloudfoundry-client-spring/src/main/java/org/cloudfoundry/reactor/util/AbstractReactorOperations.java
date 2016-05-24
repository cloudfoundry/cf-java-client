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
import org.cloudfoundry.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple2;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpException;
import reactor.io.netty.http.HttpInbound;
import reactor.io.netty.http.HttpOutbound;

import java.util.List;
import java.util.function.Function;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public abstract class AbstractReactorOperations {

    protected static final AsciiString APPLICATION_ZIP = new AsciiString("application/zip");

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

    protected final <REQ, RSP> Mono<RSP> doDelete(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                  Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return prepareRequest(request, uriTransformer)
            .then(function((validRequest, uri) -> this.httpClient.delete(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                .then(o -> o.send(serializedRequest(o, validRequest))))
                .doOnSubscribe(s -> this.requestLogger.debug("DELETE {}", uri))
                .compose(logResponse(uri))))
            .compose(deserializedResponse(responseType));
    }

    protected final <REQ, RSP> Mono<RSP> doGet(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                               Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return doGet(request, uriTransformer, requestTransformer)
            .compose(deserializedResponse(responseType));
    }

    protected final <REQ> Mono<HttpInbound> doGet(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                  Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return prepareRequest(request, uriTransformer)
            .then(function((validRequest, uri) -> this.httpClient.get(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                .then(HttpOutbound::sendHeaders))
                .doOnSubscribe(s -> this.requestLogger.debug("GET    {}", uri))
                .compose(logResponse(uri))));
    }

    protected final <REQ, RSP> Mono<RSP> doPatch(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                 Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return prepareRequest(request, uriTransformer)
            .then(function((validRequest, uri) -> this.httpClient.patch(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                .then(o -> o.send(serializedRequest(o, validRequest))))
                .doOnSubscribe(s -> this.requestLogger.debug("PATCH  {}", uri))
                .compose(logResponse(uri))))
            .compose(deserializedResponse(responseType));
    }

    protected final <REQ, RSP> Mono<RSP> doPost(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return doPostComplete(request, responseType, uriTransformer, function((outbound, validRequest) -> requestTransformer.apply(Tuple.of(outbound, validRequest))
            .send(serializedRequest(outbound, validRequest))));
    }

    protected final <REQ, RSP> Mono<RSP> doPostComplete(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                        Function<Tuple2<HttpOutbound, REQ>, Mono<Void>> requestTransformer) {
        return prepareRequest(request, uriTransformer)
            .then(function((validRequest, uri) -> this.httpClient.post(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .then(o -> requestTransformer.apply(Tuple.of(o, validRequest))))
                .doOnSubscribe(s -> this.requestLogger.debug("POST   {}", uri))
                .compose(logResponse(uri))))
            .compose(deserializedResponse(responseType));
    }

    protected final <REQ, RSP> Mono<RSP> doPut(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                               Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return prepareRequest(request, uriTransformer)
            .then(function((validRequest, uri) -> this.httpClient.put(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                .then(o -> o.send(serializedRequest(o, validRequest))))
                .doOnSubscribe(s -> this.requestLogger.debug("PUT    {}", uri))
                .compose(logResponse(uri))))
            .compose(deserializedResponse(responseType));
    }

    protected final <REQ, RSP> Mono<RSP> doPutComplete(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                       Function<Tuple2<HttpOutbound, REQ>, Mono<Void>> requestTransformer) {

        return prepareRequest(request, uriTransformer)
            .then(function((validRequest, uri) -> this.httpClient.put(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .then(o -> requestTransformer.apply(Tuple.of(o, validRequest))))
                .doOnSubscribe(s -> this.requestLogger.debug("PUT    {}", uri))
                .compose(logResponse(uri))))
            .compose(deserializedResponse(responseType));
    }

    protected final <REQ> Mono<HttpInbound> doWs(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                 Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return prepareRequest(request, uriTransformer)
            .then(function((validRequest, uri) -> this.httpClient.get(uri, outbound -> this.authorizationProvider.addAuthorization(outbound)
                .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                .then(HttpOutbound::upgradeToTextWebsocket))
                .doOnSubscribe(s -> this.requestLogger.debug("WS     {}", uri))
                .compose(logResponse(uri))));
    }

    private static <REQ> String buildUri(String root, REQ validRequest, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return uriTransformer
            .apply(Tuple.of(UriComponentsBuilder.fromUriString(root), validRequest))
            .build().encode().toUriString();
    }

    private <RSP> Function<Mono<HttpInbound>, Mono<RSP>> deserializedResponse(Class<RSP> responseType) {
        return inbound -> inbound
            .then(i -> i.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.objectMapper, responseType));
    }

    private Function<Mono<HttpInbound>, Mono<HttpInbound>> logResponse(String uri) {
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

    private <REQ> Mono<Tuple2<REQ, String>> prepareRequest(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, r) -> Tuple.of(validRequest, buildUri(r, validRequest, uriTransformer))));
    }

    private <REQ> Mono<ByteBuf> serializedRequest(HttpOutbound outbound, REQ validRequest) {
        return Mono.just(validRequest)
            .filter(req -> this.objectMapper.canSerialize(req.getClass()))
            .map(JsonCodec.encode(this.objectMapper, outbound));
    }

}
