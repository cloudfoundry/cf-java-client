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
import io.netty.handler.codec.http.HttpHeaderNames;
import reactor.core.Exceptions;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class MultipartHttpClientRequest {

    private final HttpClientForm form;

    private final ObjectMapper objectMapper;

    private final List<Consumer<PartHttpClientRequest>> partConsumers = new ArrayList<>();

    private final HttpClientRequest request;

    public MultipartHttpClientRequest(ObjectMapper objectMapper, HttpClientRequest request, HttpClientForm form) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.form = form;
    }

    public MultipartHttpClientRequest addPart(Consumer<PartHttpClientRequest> partConsumer) {
        this.partConsumers.add(partConsumer);
        return this;
    }

    public void done() {
        List<PartHttpClientRequest> parts = this.partConsumers.stream()
            .map(partConsumer -> {
                PartHttpClientRequest part = new PartHttpClientRequest(this.objectMapper);
                partConsumer.accept(part);
                return part;
            })
            .collect(Collectors.toList());

        this.request.requestHeaders()
            .remove(HttpHeaderNames.TRANSFER_ENCODING);

        this.form.multipart(true);

        HttpClientForm intermediateForm = this.form;
        for (PartHttpClientRequest part : parts) {
            intermediateForm = part.send(intermediateForm);
        }
    }

    public static final class PartHttpClientRequest {

        private final ObjectMapper objectMapper;

        private String contentType;

        private Path file;

        private String filename;

        private String name;

        private ByteArrayInputStream payload;

        private PartHttpClientRequest(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public void send(Object source) {
            try {
                byte[] bytes = this.objectMapper.writeValueAsBytes(source);
                this.payload = new ByteArrayInputStream(bytes);
            } catch (JsonProcessingException e) {
                throw Exceptions.propagate(e);
            }
        }

        public void sendFile(Path file) {
            this.file = file;
        }

        public PartHttpClientRequest setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public PartHttpClientRequest setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public PartHttpClientRequest setName(String name) {
            this.name = name;
            return this;
        }

        private String getFilenameOrDefault() {
            return this.filename != null ? this.filename : this.file.getFileName().toString();
        }

        private HttpClientForm send(HttpClientForm form) {
            if (this.file != null) {
                return form.file(this.name, getFilenameOrDefault(), this.file.toFile(), this.contentType);
            } else if (this.payload != null) {
                return form.file(this.name, this.payload, this.contentType);
            }
            return form;
        }

    }

}
