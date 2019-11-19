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

package org.cloudfoundry.reactor.doppler;

import com.google.protobuf.util.JsonFormat;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.cloudfoundry.loggregator.v2.LoggregatorEnvelope;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultipartCodec {

    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("multipart/.+; boundary=(.*)");

    private static final int MAX_PAYLOAD_SIZE = 1024 * 1024;

    private static final String SERVER_SENT_EVENT_DATA_STRING = "data: ";

    private MultipartCodec() {
    }

    static DelimiterBasedFrameDecoder createDecoder(HttpClientResponse response) {
        String boundary = extractMultipartBoundary(response);

        return new DelimiterBasedFrameDecoder(MAX_PAYLOAD_SIZE,
            Unpooled.copiedBuffer(String.format("--%s\r\n\r\n", boundary), Charset.defaultCharset()),
            Unpooled.copiedBuffer(String.format("\r\n--%s\r\n\r\n", boundary), Charset.defaultCharset()),
            Unpooled.copiedBuffer(String.format("\r\n--%s--", boundary), Charset.defaultCharset()),
            Unpooled.copiedBuffer(String.format("\r\n--%s--\r\n", boundary), Charset.defaultCharset()));
    }


    private static String extractMultipartBoundary(HttpClientResponse response) {
        String contentType = response.responseHeaders().get(HttpHeaderNames.CONTENT_TYPE);
        Matcher matcher = BOUNDARY_PATTERN.matcher(contentType);

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new IllegalStateException(String.format("Content-Type %s does not contain a valid multipart boundary", contentType));
        }
    }

    public static DelimiterBasedFrameDecoder createSimpleDecoder(HttpClientResponse response) {
        return new DelimiterBasedFrameDecoder(MAX_PAYLOAD_SIZE,
            Unpooled.copiedBuffer("\n\n", Charset.defaultCharset()));
    }

    public static Flux<InputStream> decode(ByteBufFlux body) {
        return body.asInputStream().skip(1);
    }

    public static Flux<LoggregatorEnvelope.Envelope> decodeAsEnvelope(ByteBufFlux body) {
        return body.asString().flatMap(bodyString -> Flux.fromIterable(parseBatch(bodyString).getBatchList()));
    }

    private static LoggregatorEnvelope.EnvelopeBatch parseBatch(String bodyString) {
        if (bodyString.startsWith("event: ")) {
            return emptyBatch();
        }

        if (bodyString.contains("heartbeat")) {
            return emptyBatch();
        }

        if (bodyString.startsWith(SERVER_SENT_EVENT_DATA_STRING)) {
            try {
                ServerSentEvent<String> serverSentEvent = ServerSentEvent.builder(bodyString.substring(SERVER_SENT_EVENT_DATA_STRING.length())).build();
                LoggregatorEnvelope.EnvelopeBatch.Builder builder = LoggregatorEnvelope.EnvelopeBatch.newBuilder();
                JsonFormat.parser().merge(new StringReader(serverSentEvent.data()), builder);
                return builder.build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return emptyBatch();
    }

    private static LoggregatorEnvelope.EnvelopeBatch emptyBatch() {
        return LoggregatorEnvelope.EnvelopeBatch.newBuilder().build();
    }


}
