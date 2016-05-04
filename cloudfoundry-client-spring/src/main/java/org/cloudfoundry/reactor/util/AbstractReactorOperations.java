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
import org.cloudfoundry.Validatable;
import org.cloudfoundry.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple2;
import reactor.io.netty.common.NettyInbound;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpInbound;
import reactor.io.netty.http.HttpOutbound;

import java.util.function.Function;

import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public abstract class AbstractReactorOperations {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.request");

    private final AuthorizationProvider authorizationProvider;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final Mono<String> root;

    protected AbstractReactorOperations(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        this.authorizationProvider = authorizationProvider;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.root = root;
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doDelete(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                      Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .doOnSuccess(consumer((validRequest, uri) -> this.logger.debug("DELETE {}", uri)))
            .then(function((validRequest, uri) -> this.httpClient.delete(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .then(HttpOutbound::sendHeaders))))
            .flatMap(NettyInbound::receive)
            .as(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doGet(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                   Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return doGet(request, uriTransformer, requestTransformer)
            .flatMap(NettyInbound::receive)
            .as(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable> Mono<HttpInbound> doGet(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                      Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .doOnSuccess(consumer((validRequest, uri) -> this.logger.debug("GET    {}", uri)))
            .then(function((validRequest, uri) -> this.httpClient.get(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .then(HttpOutbound::sendHeaders))));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doPost(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                    Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .doOnSuccess(consumer((validRequest, uri) -> this.logger.debug("POST   {}", uri)))
            .then(function((validRequest, uri) -> this.httpClient.post(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .then(o -> o.send(Mono.just(validRequest)
                        .as(JsonCodec.encode(this.objectMapper)))))))
            .flatMap(NettyInbound::receive)
            .as(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doPut(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                   Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .doOnSuccess(consumer((validRequest, uri) -> this.logger.debug("PUT    {}", uri)))
            .then(function((validRequest, uri) -> this.httpClient.put(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .then(o -> o.send(Mono.just(validRequest)
                        .as(JsonCodec.encode(this.objectMapper)))))))
            .flatMap(NettyInbound::receive)
            .as(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable> Mono<HttpInbound> doWs(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                     Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .doOnSuccess(consumer((validRequest, uri) -> this.logger.debug("WS     {}", uri)))
            .then(function((validRequest, uri) -> this.httpClient.get(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .then(HttpOutbound::upgradeToTextWebsocket))));
    }

    private static <REQ extends Validatable> String buildUri(String root, REQ validRequest, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return uriTransformer
            .apply(Tuple.of(UriComponentsBuilder.fromUriString(root), validRequest))
            .build().encode().toUriString();
    }

}
