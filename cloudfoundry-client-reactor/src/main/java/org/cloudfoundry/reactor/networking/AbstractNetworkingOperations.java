/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.networking;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.UriQueryParameter;
import org.cloudfoundry.reactor.util.UriQueryParameters;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractNetworkingOperations extends AbstractReactorOperations {

    protected AbstractNetworkingOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    protected final <T> Mono<T> get(Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.get()
                .uri(uriTransformer)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> get(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.get()
                .uri(queryTransformer(requestPayload).andThen(uriTransformer))
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> post(Object request, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.post()
                .uri(uriTransformer)
                .send(request)
                .response()
                .parseBody(responseType));
    }

    protected final <T> Mono<T> put(Object requestPayload, Class<T> responseType, Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return createOperator()
            .flatMap(operator -> operator.put()
                .uri(uriTransformer)
                .send(requestPayload)
                .response()
                .parseBody(responseType));
    }

    private Function<UriComponentsBuilder, UriComponentsBuilder> queryTransformer(Object requestPayload) {
        return builder -> {
            Stream<UriQueryParameter> parameters = new QueryBuilder().build(requestPayload);
            UriQueryParameters.set(builder, parameters);
            return builder;
        };
    }

}
