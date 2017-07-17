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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.nio.charset.Charset;
import java.util.function.Function;

public final class JsonCodec {

    private static final int MAX_PAYLOAD_SIZE = 100 * 1024 * 1024;

    public static HttpClientRequest addDecodeHeaders(HttpClientRequest request) {
        return request
            .header(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON);
    }

    public static <T> Function<Mono<HttpClientResponse>, Flux<T>> decode(ObjectMapper objectMapper, Class<T> responseType) {
        return inbound -> inbound
            .flatMapMany(response -> response.addHandler(new JsonObjectDecoder(MAX_PAYLOAD_SIZE)).receive().asByteArray())
            .map(payload -> {
                try {
                    return objectMapper.readValue(payload, responseType);
                } catch (Throwable t) {
                    throw new JsonParsingException(t.getMessage(), t, new String(payload, Charset.defaultCharset()));
                }
            });
    }

    static Function<Mono<HttpClientRequest>, Publisher<Void>> encode(ObjectMapper objectMapper, Object requestPayload) {
        if (!objectMapper.canSerialize(requestPayload.getClass())) {
            return outbound -> outbound
                .flatMap(HttpClientRequest::send);
        }

        return outbound -> outbound
            .flatMapMany(request -> {
                try {
                    byte[] bytes = objectMapper.writeValueAsBytes(requestPayload);

                    return request
                        .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .header(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(bytes.length))
                        .sendByteArray(Mono.just(bytes));
                } catch (JsonProcessingException e) {
                    throw Exceptions.propagate(e);
                }
            });
    }

}
