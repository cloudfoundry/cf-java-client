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

package org.cloudfoundry.logcache.v1;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.QueryBuilder;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.cloudfoundry.reactor.util.UriQueryParameter;
import org.cloudfoundry.reactor.util.UriQueryParameters;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class TestLogCacheEndpoints extends AbstractReactorOperations {

    public TestLogCacheEndpoints(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider, new HashMap<>());
    }

    Mono<Void> counter(String name, long delta) {
        return get(CounterRequest.builder()
            .name(name)
            .delta(delta)
            .build(), "counter");
    }

    Mono<Void> event(String title, String body) {
        return get(EventRequest.builder()
            .title(title)
            .body(body)
            .build(), "event");
    }

    Mono<Void> gauge(String name, Double value) {
        return get(GaugeRequest.builder()
            .name(name)
            .value(value.toString())
            .build(), "gauge");
    }

    Mono<Void> log(String message) {
        return get(LogRequest.builder()
            .message(message)
            .build(), "log");
    }

    private Function<UriComponentsBuilder, UriComponentsBuilder> buildPathSegments(String[] pathSegments) {
        return builder -> builder.pathSegment(pathSegments);
    }

    private Mono<Void> get(Object requestPayload, String... pathSegments) {
        return createOperator()
            .flatMap(operator -> operator.get()
                .uri(buildPathSegments(pathSegments).andThen(queryTransformer(requestPayload)))
                .response()
                .get())
            .then();
    }

    private Function<UriComponentsBuilder, UriComponentsBuilder> queryTransformer(Object requestPayload) {
        return builder -> {
            Stream<UriQueryParameter> parameters = new QueryBuilder().build(requestPayload);
            UriQueryParameters.set(builder, parameters);
            return builder;
        };
    }

}