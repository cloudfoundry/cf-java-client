/*
 * Copyright 2013-2017 the original author or authors.
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

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class NetworkLogging {

    public static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.request");

    public static final Logger RESPONSE_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.response");

    private static final String CF_WARNINGS = "X-Cf-Warnings";

    private static final double MILLISECOND = 1;

    private static final double SECOND = 1000 * MILLISECOND;

    private static final double MINUTE = 60 * SECOND;

    private static final double HOUR = 60 * MINUTE;

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
        return inbound -> inbound
            .elapsed()
            .map(function((elapsed, response) -> {
                List<String> warnings = response.responseHeaders().getAll(CF_WARNINGS);

                if (!warnings.isEmpty()) {
                    RESPONSE_LOGGER.warn("{}    {} ({}) [{}]", response.status().code(), uri, asTime(elapsed), StringUtils.collectionToCommaDelimitedString(warnings));
                } else if (RESPONSE_LOGGER.isDebugEnabled()) {
                    RESPONSE_LOGGER.debug("{}    {} ({})", response.status().code(), uri, asTime(elapsed));
                }

                return response;
            }));
    }

    public static Consumer<Subscription> ws(String uri) {
        return s -> REQUEST_LOGGER.debug("WS     {}", uri);
    }

    private static String asTime(long elapsed) {
        if (elapsed > HOUR) {
            return String.format("%.1f h", (elapsed / HOUR));
        } else if (elapsed > MINUTE) {
            return String.format("%.1f m", (elapsed / MINUTE));
        } else if (elapsed > SECOND) {
            return String.format("%.1f s", (elapsed / SECOND));
        } else {
            return String.format("%d ms", elapsed);
        }
    }

}
