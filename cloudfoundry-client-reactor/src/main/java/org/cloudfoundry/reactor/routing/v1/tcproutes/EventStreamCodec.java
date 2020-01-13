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

package org.cloudfoundry.reactor.routing.v1.tcproutes;

import io.netty.handler.codec.LineBasedFrameDecoder;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientResponse;

final class EventStreamCodec {

    private static final int MAX_PAYLOAD_SIZE = 1024 * 1024;

    private EventStreamCodec() {
    }

    static LineBasedFrameDecoder createDecoder(HttpClientResponse response) {
        return new LineBasedFrameDecoder(MAX_PAYLOAD_SIZE);
    }

    static Flux<ServerSentEvent> decode(ByteBufFlux body) {
        return body.asString()
            .windowWhile(s -> !s.isEmpty())
            .concatMap(window -> window
                .reduce(ServerSentEvent.builder(), EventStreamCodec::parseLine))
            .map(ServerSentEvent.Builder::build)
            .filter(sse -> sse.getData() != null || sse.getEventType() != null || sse.getId() != null || sse.getRetry() != null);
    }

    private static Field parseField(String line) {
        String[] split = line.split("[ ]?:[ ]?", 2);

        String key = split.length > 0 ? split[0] : "";
        String value = split.length > 1 ? split[1] : "";

        return new Field(key, value);
    }

    private static ServerSentEvent.Builder parseLine(ServerSentEvent.Builder builder, String line) {
        Field field = parseField(line);

        if ("data".equals(field.getKey())) {
            builder.data(field.getValue());
        } else if ("event".equals(field.getKey())) {
            builder.eventType(field.getValue());
        } else if ("id".equals(field.getKey())) {
            builder.id(field.getValue());
        } else if ("retry".equals(field.getKey())) {
            builder.retry(Integer.parseInt(field.getValue()));
        }

        return builder;
    }

    private static final class Field {

        private final String key;

        private final String value;

        private Field(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private String getKey() {
            return this.key;
        }

        private String getValue() {
            return this.value;
        }

    }

}
