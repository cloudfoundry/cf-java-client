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

package org.cloudfoundry.reactor.client.v3;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.CloudFoundryExceptionBuilder;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.MultipartHttpClientRequest;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpClientResponse;
import reactor.ipc.netty.http.HttpException;

import java.util.function.Function;

public abstract class AbstractClientV3Operations extends AbstractReactorOperations {

    private final ConnectionContext connectionContext;

    protected AbstractClientV3Operations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
        this.connectionContext = connectionContext;
    }

    protected final <T> Mono<T> delete(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doDelete(request, responseType, getUriAugmenter(request, uriTransformer), outbound -> outbound)
            .otherwise(HttpException.class, CloudFoundryExceptionBuilder::build);
    }

    protected final <T> Mono<T> get(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doGet(responseType, getUriAugmenter(request, uriTransformer), outbound -> outbound)
            .otherwise(HttpException.class, CloudFoundryExceptionBuilder::build);
    }

    protected final Mono<HttpClientResponse> get(Object request, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doGet(getUriAugmenter(request, uriTransformer), outbound -> outbound)
            .otherwise(HttpException.class, CloudFoundryExceptionBuilder::build);
    }

    protected final <T> Mono<T> patch(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doPatch(request, responseType, getUriAugmenter(request, uriTransformer), outbound -> outbound)
            .otherwise(HttpException.class, CloudFoundryExceptionBuilder::build);
    }

    protected final <T> Mono<T> post(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doPost(request, responseType, getUriAugmenter(request, uriTransformer), outbound -> outbound)
            .otherwise(HttpException.class, CloudFoundryExceptionBuilder::build);
    }

    protected final <T> Mono<T> post(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                     Function<MultipartHttpClientRequest, Mono<Void>> requestTransformer) {

        return doPost(responseType, getUriAugmenter(request, uriTransformer),
            outbound -> requestTransformer.apply(new MultipartHttpClientRequest(this.connectionContext.getObjectMapper(), outbound)))
            .otherwise(HttpException.class, CloudFoundryExceptionBuilder::build);
    }

    protected final <T> Mono<T> put(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doPut(request, responseType, getUriAugmenter(request, uriTransformer), outbound -> outbound)
            .otherwise(HttpException.class, CloudFoundryExceptionBuilder::build);
    }

    private static Function<UriComponentsBuilder, UriComponentsBuilder> getUriAugmenter(Object request, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return builder -> {
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
            return uriTransformer.apply(builder);
        };
    }

}
