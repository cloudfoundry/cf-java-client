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

package org.cloudfoundry.reactor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpStatusClass;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v3.ClientV3Exception;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.io.IOException;
import java.util.function.Function;

import static io.netty.handler.codec.http.HttpStatusClass.CLIENT_ERROR;
import static io.netty.handler.codec.http.HttpStatusClass.SERVER_ERROR;

public final class ErrorPayloadMapper {

    public static Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> clientV2(ObjectMapper objectMapper) {
        return inbound -> inbound
            .then(mapToError(objectMapper, ClientV2Exception.class));
    }

    public static Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> clientV3(ObjectMapper objectMapper) {
        return inbound -> inbound
            .then(mapToError(objectMapper, ClientV3Exception.class));
    }

    private static boolean isError(HttpClientResponse response) {
        HttpStatusClass statusClass = response.status().codeClass();
        return statusClass != CLIENT_ERROR && statusClass != SERVER_ERROR;
    }

    private static Function<HttpClientResponse, Mono<HttpClientResponse>> mapToError(ObjectMapper objectMapper, Class<? extends RuntimeException> type) {
        return response -> {
            if (isError(response)) {
                return Mono.just(response);
            }

            return response.receive().aggregate().asByteArray()
                .then(bytes -> {
                    try {
                        return Mono.error(objectMapper.readValue(bytes, type));
                    } catch (IOException e) {
                        throw Exceptions.propagate(e);
                    }
                });
        };
    }

}
