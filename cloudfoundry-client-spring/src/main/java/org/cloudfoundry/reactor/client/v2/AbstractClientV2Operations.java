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

package org.cloudfoundry.reactor.client.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.reactor.client.CloudFoundryExceptionBuilder;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.MultipartHttpOutbound;
import org.cloudfoundry.util.ExceptionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple2;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpException;
import reactor.io.netty.http.HttpInbound;

import java.util.function.Function;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public abstract class AbstractClientV2Operations extends AbstractReactorOperations {

    protected AbstractClientV2Operations(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    protected final <REQ, RSP> Mono<RSP> delete(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doDelete(request, responseType, getUriAugmenter(uriTransformer), function((outbound, validRequest) -> outbound))
            .otherwise(ExceptionUtils.replace(HttpException.class, CloudFoundryExceptionBuilder::build));
    }

    protected final <REQ, RSP> Mono<RSP> get(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doGet(request, responseType, getUriAugmenter(uriTransformer), function((outbound, validRequest) -> outbound))
            .otherwise(ExceptionUtils.replace(HttpException.class, CloudFoundryExceptionBuilder::build));
    }

    protected final <REQ> Mono<HttpInbound> get(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doGet(request, getUriAugmenter(uriTransformer), function((outbound, validRequest) -> outbound))
            .otherwise(ExceptionUtils.replace(HttpException.class, CloudFoundryExceptionBuilder::build));
    }

    protected final <REQ, RSP> Mono<RSP> post(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doPost(request, responseType, getUriAugmenter(uriTransformer), function((outbound, validRequest) -> outbound))
            .otherwise(ExceptionUtils.replace(HttpException.class, CloudFoundryExceptionBuilder::build));
    }

    protected final <REQ, RSP> Mono<RSP> post(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                              Function<Tuple2<MultipartHttpOutbound, REQ>, Mono<Void>> requestTransformer) {

        return doPostComplete(request, responseType, getUriAugmenter(uriTransformer),
            function((outbound, validRequest) -> requestTransformer.apply(Tuple.of(new MultipartHttpOutbound(outbound), validRequest))))
            .otherwise(ExceptionUtils.replace(HttpException.class, CloudFoundryExceptionBuilder::build));
    }

    protected final <REQ, RSP> Mono<RSP> put(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doPut(request, responseType, getUriAugmenter(uriTransformer), function((outbound, validRequest) -> outbound))
            .otherwise(ExceptionUtils.replace(HttpException.class, CloudFoundryExceptionBuilder::build));
    }

    protected final <REQ, RSP> Mono<RSP> put(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                             Function<Tuple2<MultipartHttpOutbound, REQ>, Mono<Void>> requestTransformer) {

        return doPutComplete(request, responseType, getUriAugmenter(uriTransformer),
            function((outbound, validRequest) -> requestTransformer.apply(Tuple.of(new MultipartHttpOutbound(outbound), validRequest))))
            .otherwise(ExceptionUtils.replace(HttpException.class, CloudFoundryExceptionBuilder::build));
    }

    private static <REQ> Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> getUriAugmenter(
        Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {

        return function((builder, validRequest) -> {
            FilterBuilder.augment(builder, validRequest);
            QueryBuilder.augment(builder, validRequest);
            return uriTransformer.apply(Tuple.of(builder, validRequest));
        });
    }

}
