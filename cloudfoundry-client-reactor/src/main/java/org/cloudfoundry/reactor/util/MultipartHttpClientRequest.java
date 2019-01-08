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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyOutbound;
import reactor.ipc.netty.http.client.HttpClientRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_DISPOSITION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.MULTIPART_FORM_DATA;

public final class MultipartHttpClientRequest {

    private static final byte[] BOUNDARY_CHARS = new byte[]{'-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final AsciiString BOUNDARY_PREAMBLE = MULTIPART_FORM_DATA.concat("; boundary=");

    private static final AsciiString CRLF = new AsciiString("\r\n");

    private static final AsciiString DOUBLE_DASH = new AsciiString("--");

    private static final Random RND = new Random();

    private final ObjectMapper objectMapper;

    private final List<Consumer<PartHttpClientRequest>> partConsumers = new ArrayList<>();

    private final HttpClientRequest request;

    public MultipartHttpClientRequest(ObjectMapper objectMapper, HttpClientRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public MultipartHttpClientRequest addPart(Consumer<PartHttpClientRequest> partConsumer) {
        this.partConsumers.add(partConsumer);
        return this;
    }

    public Mono<Void> done() {
        AsciiString boundary = generateMultipartBoundary();
        AsciiString delimiter = getDelimiter(boundary);
        AsciiString closeDelimiter = getCloseDelimiter(boundary);

        List<PartHttpClientRequest> parts = this.partConsumers.stream()
            .map(partConsumer -> {
                PartHttpClientRequest part = new PartHttpClientRequest(this.objectMapper);
                partConsumer.accept(part);
                return part;
            })
            .collect(Collectors.toList());

        Long contentLength = parts.stream()
            .mapToLong(part -> delimiter.length() + CRLF.length() + part.getLength())
            .sum() + closeDelimiter.length();

        NettyOutbound intermediateRequest = this.request
            .chunkedTransfer(false)
            .header(CONTENT_TYPE, BOUNDARY_PREAMBLE.concat(boundary))
            .header(CONTENT_LENGTH, String.valueOf(contentLength));

        for (PartHttpClientRequest part : parts) {
            intermediateRequest = intermediateRequest.sendObject(Unpooled.wrappedBuffer(delimiter.toByteArray()));
            intermediateRequest = intermediateRequest.sendObject(Unpooled.wrappedBuffer(CRLF.toByteArray()));
            intermediateRequest = intermediateRequest.sendObject(part.renderedHeaders);
            intermediateRequest = part.sendPayload(intermediateRequest);
        }

        intermediateRequest = intermediateRequest.sendObject(Unpooled.wrappedBuffer(closeDelimiter.toByteArray()));

        return intermediateRequest
            .then();
    }

    private static AsciiString generateMultipartBoundary() {
        byte[] boundary = new byte[RND.nextInt(11) + 30];
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_CHARS[RND.nextInt(BOUNDARY_CHARS.length)];
        }
        return new AsciiString(boundary);
    }

    private static AsciiString getCloseDelimiter(AsciiString boundary) {
        return CRLF.concat(DOUBLE_DASH).concat(boundary).concat(DOUBLE_DASH);
    }

    private static AsciiString getDelimiter(AsciiString boundary) {
        return CRLF.concat(DOUBLE_DASH).concat(boundary);
    }

    public static final class PartHttpClientRequest {

        private static final AsciiString HEADER_DELIMITER = new AsciiString(": ");

        private final HttpHeaders headers = new DefaultHttpHeaders(true);

        private final ObjectMapper objectMapper;

        private Path file;

        private byte[] payload;

        private ByteBuf renderedHeaders;

        private PartHttpClientRequest(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public void send(Object source) {
            try {
                byte[] bytes = this.objectMapper.writeValueAsBytes(source);
                this.headers.set(CONTENT_LENGTH, bytes.length);
                this.renderedHeaders = renderHeaders();
                this.payload = bytes;
            } catch (JsonProcessingException e) {
                throw Exceptions.propagate(e);
            }
        }

        public void sendFile(Path file) {
            try {
                this.headers.set(CONTENT_LENGTH, Files.size(file));
                this.renderedHeaders = renderHeaders();
                this.file = file;
            } catch (IOException e) {
                throw Exceptions.propagate(e);
            }
        }

        public PartHttpClientRequest setContentDispositionFormData(String name) {
            return setContentDispositionFormData(name, null);
        }

        public PartHttpClientRequest setContentDispositionFormData(String name, String filename) {
            AsciiString s = new AsciiString("form-data; name=\"").concat(name).concat("\"");

            if (filename != null) {
                s = s.concat("; filename=\"").concat(filename).concat("\"");
            }

            this.headers.set(CONTENT_DISPOSITION, s);
            return this;
        }

        public PartHttpClientRequest setHeader(CharSequence name, CharSequence value) {
            this.headers.set(name, value);
            return this;
        }

        private long getLength() {
            return this.renderedHeaders.readableBytes() + getPayloadLength();
        }

        private long getPayloadLength() {
            if (this.file != null) {
                try {
                    return Files.size(this.file);
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            } else if (this.payload != null) {
                return this.payload.length;
            } else {
                return 0;
            }
        }

        private ByteBuf renderHeaders() {
            AsciiString s = this.headers.entries().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> new AsciiString(entry.getKey()).concat(HEADER_DELIMITER).concat(entry.getValue()).concat(CRLF))
                .reduce(AsciiString::concat)
                .orElse(AsciiString.EMPTY_STRING)
                .concat(CRLF);

            return Unpooled.wrappedBuffer(s.toByteArray());
        }

        private NettyOutbound sendPayload(NettyOutbound request) {
            if (this.file != null) {
                return request.sendFile(this.file);
            } else if (this.payload != null) {
                return request.sendByteArray(Mono.just(this.payload));
            } else {
                return request;
            }
        }
    }

}
