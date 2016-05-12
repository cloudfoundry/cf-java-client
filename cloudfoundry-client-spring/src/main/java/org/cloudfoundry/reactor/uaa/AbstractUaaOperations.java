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
import io.netty.util.AsciiString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple2;
import reactor.io.netty.http.HttpClient;
import reactor.io.netty.http.HttpInbound;
import reactor.io.netty.http.HttpOutbound;

import java.util.Base64;
import java.util.function.Function;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public abstract class AbstractUaaOperations extends AbstractReactorOperations {

    private static final AsciiString AUTHORIZATION = new AsciiString("Authorization");

    private static final AsciiString BASIC_PREAMBLE = new AsciiString("Basic ");

    protected AbstractUaaOperations(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    protected final HttpOutbound basicAuth(HttpOutbound outbound, String clientId, String clientSecret) {
        String encoded = Base64.getEncoder().encodeToString(new AsciiString(clientId).concat(":").concat(clientSecret).toByteArray());
        outbound.headers().set(AUTHORIZATION, BASIC_PREAMBLE + encoded);
        return outbound;
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> delete(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doDelete(request, responseType, getUriAugmenter(uriTransformer), function(IdentityZoneBuilder::augment));
    }

    protected final <REQ extends Validatable> Mono<HttpInbound> get(REQ request, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doGet(request, getUriAugmenter(uriTransformer), function(IdentityZoneBuilder::augment));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> get(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doGet(request, responseType, getUriAugmenter(uriTransformer), function(IdentityZoneBuilder::augment));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> post(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer,
                                                                  Function<Tuple2<HttpOutbound, REQ>, HttpOutbound> requestTransformer) {

        return doPost(request, responseType, getUriAugmenter(uriTransformer), function((outbound, validRequest) -> {
            IdentityZoneBuilder.augment(outbound, validRequest);
            return requestTransformer.apply(Tuple.of(outbound, validRequest));
        }));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> post(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doPost(request, responseType, getUriAugmenter(uriTransformer), function(IdentityZoneBuilder::augment));
    }

    protected final <REQ extends Validatable, RSP> Mono<RSP> put(REQ request, Class<RSP> responseType, Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {
        return doPut(request, responseType, getUriAugmenter(uriTransformer), function(IdentityZoneBuilder::augment));
    }

    private static <REQ extends Validatable> Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> getUriAugmenter(
        Function<Tuple2<UriComponentsBuilder, REQ>, UriComponentsBuilder> uriTransformer) {

        return function((builder, validRequest) -> {
            QueryBuilder.augment(builder, validRequest);
            return uriTransformer.apply(Tuple.of(builder, validRequest));
        });
    }

}
