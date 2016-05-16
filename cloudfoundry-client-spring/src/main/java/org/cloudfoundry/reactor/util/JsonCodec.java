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
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.util.Exceptions;
import reactor.io.netty.http.HttpOutbound;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

final class JsonCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonCodec.class);

    private static final AsciiString APPLICATION_JSON = new AsciiString("application/json; charset=utf-8");

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");

    static <T> Function<InputStream, T> decode(ObjectMapper objectMapper, Class<T> type) {
        return inputStream -> {
            try(InputStream in = inputStream) {
                return objectMapper.readValue(in, type);
            } catch (IOException e) {
                throw Exceptions.propagate(e);
            }
        };
    }

    static <T> Function<T, ByteBuf> encode(ObjectMapper objectMapper, HttpOutbound httpOutbound) {
        httpOutbound.header(CONTENT_TYPE, APPLICATION_JSON);

        return source -> {
            try {
                return httpOutbound.delegate().alloc().directBuffer()
                    .writeBytes(objectMapper.writeValueAsBytes(source));
            } catch (JsonProcessingException e) {
                throw Exceptions.propagate(e);
            }
        };
    }

}
