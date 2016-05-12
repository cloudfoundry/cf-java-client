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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;
import reactor.io.netty.http.HttpOutbound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public final class MultipartHttpOutbound {

    private static final byte[] BOUNDARY_CHARS = new byte[]{'-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final AsciiString BOUNDARY_PREAMBLE = new AsciiString("; boundary=");

    private static final AsciiString CONTENT_DISPOSITION = new AsciiString("Content-Disposition");

    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");

    private static final AsciiString CRLF = new AsciiString("\r\n");

    private static final AsciiString DOUBLE_DASH = new AsciiString("--");

    private static final AsciiString HEADER_DELIMITER = new AsciiString(": ");

    private static final AsciiString MULTIPART_FORM_DATA = new AsciiString("multipart/form-data");

    private static final Random RND = new Random();

    private final HttpOutbound outbound;

    private final List<Consumer<PartHttpOutbound>> partConsumers = new ArrayList<>();

    public MultipartHttpOutbound(HttpOutbound outbound) {
        this.outbound = outbound;
    }

    public MultipartHttpOutbound addPart(Consumer<PartHttpOutbound> partConsumer) {
        this.partConsumers.add(partConsumer);
        return this;
    }

    public Mono<Void> done() {
        AsciiString boundary = generateMultipartBoundary();
        ByteBufAllocator allocator = this.outbound.delegate().alloc();

        CompositeByteBuf bodyBuf = allocator.compositeBuffer();
        this.partConsumers.forEach(partConsumer -> bodyBuf.addComponent(getPart(allocator, boundary, partConsumer)));
        bodyBuf.addComponent(getCloseDelimiter(allocator, boundary));

        return this.outbound
            .removeTransferEncodingChunked()
            .addHeader(CONTENT_TYPE, MULTIPART_FORM_DATA.concat(BOUNDARY_PREAMBLE).concat(boundary))
            .addHeader(CONTENT_LENGTH, String.valueOf(bodyBuf.capacity()))
            .sendOne(bodyBuf.writerIndex(bodyBuf.capacity()));
    }

    private static AsciiString generateMultipartBoundary() {
        byte[] boundary = new byte[RND.nextInt(11) + 30];
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_CHARS[RND.nextInt(BOUNDARY_CHARS.length)];
        }
        return new AsciiString(boundary);
    }

    private static ByteBuf getCloseDelimiter(ByteBufAllocator allocator, AsciiString boundary) {
        AsciiString s = DOUBLE_DASH.concat(boundary).concat(DOUBLE_DASH);
        return allocator.directBuffer(s.length()).writeBytes(s.toByteArray());
    }

    private static ByteBuf getData(ByteBufAllocator allocator, InputStream inputStream) {
        try (InputStream in = inputStream) {
            ByteBuf dataBuf = allocator.directBuffer(CRLF.length() + inputStream.available() + CRLF.length());

            dataBuf.writeBytes(CRLF.toByteArray());
            dataBuf.writeBytes(in, in.available());
            dataBuf.writeBytes(CRLF.toByteArray());

            return dataBuf;
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private static ByteBuf getDelimiter(ByteBufAllocator allocator, AsciiString boundary) {
        AsciiString s = DOUBLE_DASH.concat(boundary).concat(CRLF);
        return allocator.directBuffer(s.length()).writeBytes(s.toByteArray());
    }

    private static ByteBuf getHeaders(ByteBufAllocator allocator, HttpHeaders headers) {
        AsciiString s = AsciiString.EMPTY_STRING;

        for (Map.Entry<String, String> entry : headers) {
            s = s.concat(new AsciiString(entry.getKey())).concat(HEADER_DELIMITER).concat(entry.getValue()).concat(CRLF);
        }

        return allocator.directBuffer(s.length()).writeBytes(s.toByteArray());
    }

    private static ByteBuf getPart(ByteBufAllocator allocator, AsciiString boundary, Consumer<PartHttpOutbound> partConsumer) {
        PartHttpOutbound part = new PartHttpOutbound();
        partConsumer.accept(part);

        CompositeByteBuf body = allocator.compositeBuffer();
        body.addComponent(getDelimiter(allocator, boundary));
        body.addComponent(getHeaders(allocator, part.getHeaders()));
        body.addComponent(getData(allocator, part.getInputStream()));

        return body.writerIndex(body.capacity());
    }

    public static final class PartHttpOutbound {

        private final HttpHeaders headers = new DefaultHttpHeaders(true);

        private InputStream inputStream;

        public PartHttpOutbound addHeader(CharSequence name, CharSequence value) {
            this.headers.add(name, value);
            return this;
        }

        public void sendInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public PartHttpOutbound setContentDispositionFormData(String name, String filename) {
            StringBuilder sb = new StringBuilder("form-data; name=\"");
            sb.append(name).append('\"');
            if (filename != null) {
                sb.append("; filename=\"");
                sb.append(filename).append('\"');
            }

            this.headers.add(CONTENT_DISPOSITION, sb);
            return this;
        }

        private HttpHeaders getHeaders() {
            return this.headers;
        }

        private InputStream getInputStream() {
            return this.inputStream;
        }

    }

}
