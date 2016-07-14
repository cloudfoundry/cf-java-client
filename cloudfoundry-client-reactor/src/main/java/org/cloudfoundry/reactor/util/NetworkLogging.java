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

package org.cloudfoundry.reactor.util;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClientResponse;
import reactor.io.netty.http.HttpException;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class NetworkLogging {

    public static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.request");

    public static final Logger RESPONSE_LOGGER = LoggerFactory.getLogger("cloudfoundry-client.response");

    private static final String CF_WARNINGS = "X-Cf-Warnings";

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
            .doOnSuccess(i -> {
                List<String> warnings = i.responseHeaders().getAll(CF_WARNINGS);

                if (warnings.isEmpty()) {
                    RESPONSE_LOGGER.debug("{}    {}", i.status().code(), uri);
                } else {
                    RESPONSE_LOGGER.warn("{}    {} ({})", i.status().code(), uri, StringUtils.collectionToCommaDelimitedString(warnings));
                }
            })
            .doOnError(t -> {
                if (t instanceof HttpException) {
                    RESPONSE_LOGGER.debug("{}    {}", ((HttpException) t).getResponseStatus().code(), uri);
                }
            });
    }

    public static Consumer<Subscription> ws(String uri) {
        return s -> REQUEST_LOGGER.debug("WS     {}", uri);
    }

}
