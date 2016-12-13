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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.AsciiString;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Function;

public final class JsonCodec {

    public static final String JSON_DECODER = "jsonDecoder";

    private static final AsciiString APPLICATION_JSON = new AsciiString("application/json; charset=utf-8");

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");

    public static <T> Function<Mono<HttpClientResponse>, Flux<T>> decode(ObjectMapper objectMapper, Class<T> type) {
        return response -> response
            .flatMap(inbound -> inbound.addChannelHandler(JSON_DECODER, new JsonObjectDecoder()).receive().aggregate())
            .map(byteBuf -> {
                try {
                    return objectMapper.readValue(byteBuf.toString(Charset.defaultCharset()), type);
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            });
    }

    static <T> Function<T, ByteBuf> encode(ObjectMapper objectMapper, HttpClientRequest request) {
        request.header(CONTENT_TYPE, APPLICATION_JSON);

        return source -> {
            try {
                return request.channel().alloc().directBuffer().writeBytes(objectMapper.writeValueAsBytes(source));
            } catch (JsonProcessingException e) {
                throw Exceptions.propagate(e);
            }
        };
    }

}
