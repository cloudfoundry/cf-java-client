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

package org.cloudfoundry.reactor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.reactivestreams.Publisher;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClientRequest;

import java.nio.charset.Charset;
import java.util.function.BiFunction;

public final class JsonCodec {

    private static final int MAX_PAYLOAD_SIZE = 100 * 1024 * 1024;

    public static <T> Mono<T> decode(ObjectMapper objectMapper, ByteBufFlux responseBody, Class<T> responseType) {
        return responseBody.aggregate().asByteArray()
            .map(payload -> {
                try {
                    return objectMapper.readValue(payload, responseType);
                } catch (Throwable t) {
                    throw new JsonParsingException(t.getMessage(), t, new String(payload, Charset.defaultCharset()));
                }
            });
    }

    public static void setDecodeHeaders(HttpHeaders httpHeaders) {
        httpHeaders.set(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON);
    }

    static JsonObjectDecoder createDecoder() {
        return new JsonObjectDecoder(MAX_PAYLOAD_SIZE);
    }

    static BiFunction<HttpClientRequest, NettyOutbound, Publisher<Void>> encode(ObjectMapper objectMapper, Object requestPayload) {
        if (!AnnotationUtils.findAnnotation(requestPayload.getClass(), JsonSerialize.class).isPresent()) {
            return (request, outbound) -> Mono.empty();
        }

        return (request, outbound) -> {
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(requestPayload);
                String contentLength = String.valueOf(bytes.length);
                Mono<byte[]> body = Mono.just(bytes);

                request.header(HttpHeaderNames.CONTENT_LENGTH, contentLength);
                request.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                return outbound.sendByteArray(body);
            } catch (JsonProcessingException e) {
                throw Exceptions.propagate(e);
            }
        };
    }

}
