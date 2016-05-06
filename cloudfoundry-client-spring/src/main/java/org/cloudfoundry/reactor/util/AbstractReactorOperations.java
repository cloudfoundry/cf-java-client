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
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpInbound;
import reactor.io.netty.http.HttpOutbound;

import java.util.function.Function;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public abstract class AbstractReactorOperations {

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

    protected final <REQ extends Validatable, RSP> Mono<RSP> doDelete(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                      Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .then(function((validRequest, uri) -> this.httpClient.delete(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .doOnSubscribe(s -> this.requestLogger.debug("DELETE {}", uri))
                    .then(o -> o.send(Mono.just(validRequest)
                        .where(req -> this.objectMapper.canSerialize(req.getClass()))
                        .map(JsonCodec.encode(this.objectMapper, o)))))
                .doOnSuccess(inbound -> this.responseLogger.debug("{}    {}", inbound.status().code(), uri))
                .doOnSuccess(inbound -> printWarnings(inbound, this.responseLogger, uri))))
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doGet(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                   Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return doGet(request, uriTransformer, requestTransformer)
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable> Mono<HttpInbound> doGet(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                      Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .then(function((validRequest, uri) -> this.httpClient.get(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .doOnSubscribe(s -> this.requestLogger.debug("GET    {}", uri))
                    .then(HttpOutbound::sendHeaders))
                .doOnSuccess(inbound -> this.responseLogger.debug("{}    {}", inbound.status().code(), uri))
                .doOnSuccess(inbound -> printWarnings(inbound, this.responseLogger, uri))));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doPatch(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                     Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .then(function((validRequest, uri) -> this.httpClient.patch(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .doOnSubscribe(s -> this.requestLogger.debug("PATCH  {}", uri))
                    .then(o -> o.send(Mono.just(validRequest)
                        .where(req -> this.objectMapper.canSerialize(req.getClass()))
                        .map(JsonCodec.encode(this.objectMapper, o)))))
                .doOnSuccess(inbound -> this.responseLogger.debug("{}    {}", inbound.status().code(), uri))
                .doOnSuccess(inbound -> printWarnings(inbound, this.responseLogger, uri))))
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doPost(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                    Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .then(function((validRequest, uri) -> this.httpClient.post(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .doOnSubscribe(s -> this.requestLogger.debug("POST   {}", uri))
                    .then(o -> o.send(Mono.just(validRequest)
                        .where(req -> this.objectMapper.canSerialize(req.getClass()))
                        .map(JsonCodec.encode(this.objectMapper, o)))))
                .doOnSuccess(inbound -> this.responseLogger.debug("{}    {}", inbound.status().code(), uri))
                .doOnSuccess(inbound -> printWarnings(inbound, this.responseLogger, uri))))
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> doPut(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                   Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .then(function((validRequest, uri) -> this.httpClient.put(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .doOnSubscribe(s -> this.requestLogger.debug("PUT    {}", uri))
                    .then(o -> o.send(Mono.just(validRequest)
                        .where(req -> this.objectMapper.canSerialize(req.getClass()))
                        .map(JsonCodec.encode(this.objectMapper, o)))))
                .doOnSuccess(inbound -> this.responseLogger.debug("{}    {}", inbound.status().code(), uri))
                .doOnSuccess(inbound -> printWarnings(inbound, this.responseLogger, uri))))
            .then(inbound -> inbound.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(this.objectMapper, responseType));
    }

    protected final <REQ extends Validatable> Mono<HttpInbound> doWs(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                     Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {
        return Mono
            .when(ValidationUtils.validate(request), this.root)
            .map(function((validRequest, root) -> Tuple.of(validRequest, buildUri(root, validRequest, uriTransformer))))
            .then(function((validRequest, uri) -> this.httpClient.get(uri,
                outbound -> this.authorizationProvider.addAuthorization(outbound)
                    .map(o -> requestTransformer.apply(Tuple.of(o, validRequest)))
                    .doOnSubscribe(s -> this.requestLogger.debug("WS     {}", uri))
                    .then(HttpOutbound::upgradeToTextWebsocket))
                .doOnSuccess(inbound -> this.responseLogger.debug("{}    {}", inbound.status().code(), uri))
                .doOnSuccess(inbound -> printWarnings(inbound, this.responseLogger, uri))));
    }

    private static <REQ extends Validatable> String buildUri(String root, REQ validRequest, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return uriTransformer
            .apply(Tuple.of(UriComponentsBuilder.fromUriString(root), validRequest))
            .build().encode().toUriString();
    }

    private static void printWarnings(HttpInbound inbound, Logger logger, String uri) {
        inbound.responseHeaders().getAll("X-Cf-Warnings")
            .forEach(warning -> logger.warn("{} ({})", warning, uri));
    }

}
