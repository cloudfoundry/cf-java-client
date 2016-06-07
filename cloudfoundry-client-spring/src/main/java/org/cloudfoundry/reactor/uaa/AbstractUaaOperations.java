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

package org.cloudfoundry.reactor.uaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpInbound;
import reactor.io.netty.http.HttpOutbound;

import java.util.function.Function;

public abstract class AbstractUaaOperations extends AbstractReactorOperations {

    protected AbstractUaaOperations(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    protected static Function<UriComponentsBuilder, UriComponentsBuilder> getUriAugmenter(Object request, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return builder -> {
            QueryBuilder.augment(builder, request);
            return uriTransformer.apply(builder);
        };
    }

    protected final <T> Mono<T> delete(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doDelete(request, responseType, getUriAugmenter(request, uriTransformer), getRequestTransformer(request));
    }

    protected final <T> Mono<T> delete(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                       Function<HttpOutbound, HttpOutbound> requestTransformer) {

        return doDelete(request, responseType, getUriAugmenter(request, uriTransformer), outbound -> {
            getRequestTransformer(request).apply(outbound);
            return requestTransformer.apply(outbound);
        });
    }

    protected final Mono<HttpInbound> get(Object request, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doGet(getUriAugmenter(request, uriTransformer), getRequestTransformer(request));
    }

    protected final <T> Mono<T> get(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doGet(responseType, getUriAugmenter(request, uriTransformer), getRequestTransformer(request));
    }

    protected final <T> Mono<T> get(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                    Function<HttpOutbound, HttpOutbound> requestTransformer) {
        return doGet(responseType, getUriAugmenter(request, uriTransformer), outbound -> {
            IdentityZoneBuilder.augment(outbound, request);
            return requestTransformer.apply(outbound);
        });
    }

    protected final <T> Mono<T> post(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer,
                                     Function<HttpOutbound, HttpOutbound> requestTransformer) {

        return doPost(request, responseType, getUriAugmenter(request, uriTransformer), outbound -> {
            getRequestTransformer(request).apply(outbound);
            return requestTransformer.apply(outbound);
        });
    }

    protected final <T> Mono<T> post(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doPost(request, responseType, getUriAugmenter(request, uriTransformer), getRequestTransformer(request));
    }

    protected final <T> Mono<T> postForm(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return postForm(request, responseType, uriTransformer, outbound -> outbound);
    }

    protected final <T> Mono<T> postForm(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer, Function<HttpOutbound, HttpOutbound>
        requestTransformer) {
        return doPost(responseType, getUriAugmenter(request, uriTransformer), outbound -> {
            outbound.headers().remove(AUTHORIZATION);
            getRequestTransformer(request).apply(outbound);
            return requestTransformer.apply(outbound)
                .addHeader(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED)
                .removeTransferEncodingChunked()
                .sendHeaders();
        });
    }

    protected final <T> Mono<T> put(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doPut(request, responseType, getUriAugmenter(request, uriTransformer), getRequestTransformer(request));
    }

    private static Function<HttpOutbound, HttpOutbound> getRequestTransformer(Object request) {
        return outbound -> {
            BasicAuthorizationBuilder.augment(outbound, request);
            IdentityZoneBuilder.augment(outbound, request);
            VersionBuilder.augment(outbound, request);
            return outbound;
        };
    }

}
