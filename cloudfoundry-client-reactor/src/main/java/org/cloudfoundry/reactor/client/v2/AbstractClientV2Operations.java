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

package org.cloudfoundry.reactor.client.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.ErrorPayloadMapper;
import org.cloudfoundry.reactor.util.MultipartHttpClientRequest;
import org.reactivestreams.Publisher;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.function.Function;

public abstract class AbstractClientV2Operations extends AbstractReactorOperations {

    private final ConnectionContext connectionContext;

    protected AbstractClientV2Operations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
        this.connectionContext = connectionContext;
    }

    protected final <T> Mono<T> delete(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doDelete(requestPayload, responseType,
            queryTransformer(requestPayload)
                .andThen(uriTransformer),
            outbound -> outbound,
            ErrorPayloadMapper.clientV2(this.connectionContext.getObjectMapper()));
    }

    protected final <T> Mono<T> get(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doGet(responseType,
            queryTransformer(requestPayload)
                .andThen(uriTransformer),
            outbound -> outbound,
            ErrorPayloadMapper.clientV2(this.connectionContext.getObjectMapper()));
    }

    protected final Mono<HttpClientResponse> get(Object requestPayload, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                                 Function<Mono<HttpClientRequest>, Mono<HttpClientRequest>> requestTransformer) {

        return doGet(queryTransformer(requestPayload)
                .andThen(uriTransformer),
            outbound -> outbound
                .transform(requestTransformer),
            ErrorPayloadMapper.clientV2(this.connectionContext.getObjectMapper()));
    }

    protected final <T> Mono<T> post(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doPost(requestPayload, responseType,
            queryTransformer(requestPayload)
                .andThen(uriTransformer),
            outbound -> outbound,
            ErrorPayloadMapper.clientV2(this.connectionContext.getObjectMapper()));
    }

    protected final <T> Mono<T> post(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                     Function<Mono<MultipartHttpClientRequest>, Publisher<Void>> requestTransformer) {
        return doPost(responseType,
            queryTransformer(requestPayload)
                .andThen(uriTransformer),
            outbound -> outbound
                .map(multipartRequest(this.connectionContext.getObjectMapper()))
                .transform(requestTransformer),
            ErrorPayloadMapper.clientV2(this.connectionContext.getObjectMapper()));
    }

    protected final <T> Mono<T> put(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doPut(requestPayload, responseType,
            queryTransformer(requestPayload)
                .andThen(uriTransformer),
            outbound -> outbound,
            ErrorPayloadMapper.clientV2(this.connectionContext.getObjectMapper()));
    }

    protected final <T> Mono<T> put(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                    Function<Mono<MultipartHttpClientRequest>, Publisher<Void>> requestTransformer) {
        return doPut(responseType,
            queryTransformer(requestPayload)
                .andThen(uriTransformer),
            outbound -> outbound
                .map(multipartRequest(this.connectionContext.getObjectMapper()))
                .transform(requestTransformer),
            ErrorPayloadMapper.clientV2(this.connectionContext.getObjectMapper()));
    }

    private static Function<HttpClientRequest, MultipartHttpClientRequest> multipartRequest(ObjectMapper objectMapper) {
        return request -> new MultipartHttpClientRequest(objectMapper, request);
    }

    private static Function<UriComponentsBuilder, UriComponentsBuilder> queryTransformer(Object requestPayload) {
        return builder -> {
            FilterBuilder.augment(builder, requestPayload);
            QueryBuilder.augment(builder, requestPayload);
            return builder;
        };
    }

}
