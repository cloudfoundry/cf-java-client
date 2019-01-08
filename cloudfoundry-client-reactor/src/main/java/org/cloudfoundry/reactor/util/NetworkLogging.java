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

package org.cloudfoundry.reactor.util;

import org.cloudfoundry.util.TimeUtils;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class NetworkLogging {

    static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.request");

    static final Logger RESPONSE_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.response");

    private static final String CF_WARNINGS = "X-Cf-Warnings";

    private NetworkLogging() {
    }

    public static Consumer<Subscription> delete(String uri) {
        return s -> REQUEST_LOGGER.debug("DELETE {}", uri);
    }

    public static Consumer<Subscription> get(String uri) {
        return s -> REQUEST_LOGGER.debug("GET    {}", uri);
    }

    public static Consumer<Subscription> patch(String uri) {
        return s -> REQUEST_LOGGER.debug("PATCH  {}", uri);
    }

    public static Consumer<Subscription> post(String uri) {
        return s -> REQUEST_LOGGER.debug("POST   {}", uri);
    }

    public static Consumer<Subscription> put(String uri) {
        return s -> REQUEST_LOGGER.debug("PUT    {}", uri);
    }

    public static Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> response(String uri) {
        if (!RESPONSE_LOGGER.isDebugEnabled()) {
            return inbound -> inbound;
        }

        AtomicLong startTimeHolder = new AtomicLong();
        AtomicReference<HttpClientResponse> responseHolder = new AtomicReference<>();

        return inbound -> inbound
            .doOnSubscribe(s -> startTimeHolder.set(System.currentTimeMillis()))
            .doOnNext(responseHolder::set)
            .doFinally(signalType -> {
                String elapsed = TimeUtils.asTime(System.currentTimeMillis() - startTimeHolder.get());

                Optional.ofNullable(responseHolder.get())
                    .ifPresent(response -> {
                        List<String> warnings = response.responseHeaders().getAll(CF_WARNINGS);

                        if (warnings.isEmpty()) {
                            RESPONSE_LOGGER.debug("{}    {} ({})", response.status().code(), uri, elapsed);
                        } else {
                            RESPONSE_LOGGER.warn("{}    {} ({}) [{}]", response.status().code(), uri, elapsed, warnings.stream().collect(Collectors.joining(", ")));
                        }
                    });
            });
    }

    public static Consumer<Subscription> ws(String uri) {
        return s -> REQUEST_LOGGER.debug("WS     {}", uri);
    }


}
