/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.logcache.v1;

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

public class AbstractLogCacheOperations extends AbstractReactorOperations {

    protected AbstractLogCacheOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    protected final <T> Mono<T> get(Object requestPayload, Class<T> responseType, String... pathSegments) {
        return createOperator()
            .flatMap(operator -> operator.get()
                .uri(buildPathSegments(pathSegments).andThen(queryTransformer(requestPayload)))
                .response()
                .parseBody(responseType));
    }

    private Function<UriComponentsBuilder, UriComponentsBuilder> buildPathSegments(String[] pathSegments) {
        return builder -> builder.pathSegment("api", "v1").pathSegment(pathSegments);
    }

    private Function<UriComponentsBuilder, UriComponentsBuilder> queryTransformer(Object requestPayload) {
        return builder -> {
            Stream<UriQueryParameter> parameters = new QueryBuilder().build(requestPayload);
            UriQueryParameters.set(builder, parameters);
            return builder;
        };
    }


}
