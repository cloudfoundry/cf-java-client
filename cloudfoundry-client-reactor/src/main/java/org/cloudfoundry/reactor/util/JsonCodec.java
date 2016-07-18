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
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.AsciiString;
import reactor.core.Exceptions;
import reactor.io.netty.http.HttpClientRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public final class JsonCodec {

    private static final AsciiString APPLICATION_JSON = new AsciiString("application/json; charset=utf-8");

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");

    public static <T> Function<InputStream, T> decode(ObjectMapper objectMapper, Class<T> type) {
        return inputStream -> {
            try (InputStream in = inputStream) {
                return objectMapper.readValue(in, type);
            } catch (IOException e) {
                throw new JsonParsingException("Unable to parse JSON Payload", e, inputStream);
            }
        };
    }

    static <T> Function<T, ByteBuf> encode(ObjectMapper objectMapper, HttpClientRequest request) {
        request.header(CONTENT_TYPE, APPLICATION_JSON);
        return source -> encode(request.delegate().alloc(), objectMapper, source);
    }

    static <T> ByteBuf encode(ByteBufAllocator allocator, ObjectMapper objectMapper, T source) {
        try {
            return allocator.directBuffer().writeBytes(objectMapper.writeValueAsBytes(source));
        } catch (JsonProcessingException e) {
            throw Exceptions.propagate(e);
        }
    }

}
