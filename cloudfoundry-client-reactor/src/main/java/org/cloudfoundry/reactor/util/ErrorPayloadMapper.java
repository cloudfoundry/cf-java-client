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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpStatusClass;
import org.cloudfoundry.UnknownCloudFoundryException;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v3.ClientV3Exception;
import org.cloudfoundry.uaa.UaaException;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpStatusClass.CLIENT_ERROR;
import static io.netty.handler.codec.http.HttpStatusClass.SERVER_ERROR;

public final class ErrorPayloadMapper {

    @SuppressWarnings("unchecked")
    public static Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> clientV2(ObjectMapper objectMapper) {
        return inbound -> inbound
            .flatMap(mapToError((statusCode, payload) -> {
                Map<String, Object> map = objectMapper.readValue(payload, Map.class);
                Integer code = (Integer) map.get("code");
                String description = (String) map.get("description");
                String errorCode = (String) map.get("error_code");

                return new ClientV2Exception(statusCode, code, description, errorCode);
            }));
    }

    @SuppressWarnings("unchecked")
    public static Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> clientV3(ObjectMapper objectMapper) {
        return inbound -> inbound
            .flatMap(mapToError((statusCode, payload) -> {
                List<ClientV3Exception.Error> errors = ((Map<String, List<Map<String, Object>>>) objectMapper.readValue(payload, Map.class)).get("errors").stream()
                    .map(map -> {
                        Integer code = (Integer) map.get("code");
                        String detail = (String) map.get("detail");
                        String title = (String) map.get("title");

                        return new ClientV3Exception.Error(code, detail, title);
                    })
                    .collect(Collectors.toList());

                return new ClientV3Exception(statusCode, errors);
            }));
    }

    public static Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> fallback() {
        return inbound -> inbound
            .flatMap(response -> {
                if (!isError(response)) {
                    return Mono.just(response);
                }

                return response.receive().aggregate().asString()
                    .flatMap(payload -> Mono.error(new UnknownCloudFoundryException(response.status().code(), payload)));
            });
    }

    @SuppressWarnings("unchecked")
    public static Function<Mono<HttpClientResponse>, Mono<HttpClientResponse>> uaa(ObjectMapper objectMapper) {
        return inbound -> inbound
            .flatMap(mapToError((statusCode, payload) -> {
                Map<String, Object> map = objectMapper.readValue(payload, Map.class);
                String error = (String) map.get("error");
                String errorDescription = (String) map.get("error_description");

                return new UaaException(statusCode, error, errorDescription);
            }));
    }

    private static boolean isError(HttpClientResponse response) {
        HttpStatusClass statusClass = response.status().codeClass();
        return statusClass == CLIENT_ERROR || statusClass == SERVER_ERROR;
    }

    private static Function<HttpClientResponse, Mono<HttpClientResponse>> mapToError(ExceptionGenerator exceptionGenerator) {
        return response -> {
            if (!isError(response)) {
                return Mono.just(response);
            }

            return response.receive().aggregate().asString()
                .switchIfEmpty(Mono.error(new UnknownCloudFoundryException(response.status().code())))
                .flatMap(payload -> {
                    try {
                        return Mono.error(exceptionGenerator.apply(response.status().code(), payload));
                    } catch (Exception e) {
                        return Mono.error(new UnknownCloudFoundryException(response.status().code(), payload));
                    }
                });
        };
    }

    @FunctionalInterface
    private interface ExceptionGenerator {

        RuntimeException apply(Integer statusCode, String payload) throws Exception;

    }

}
