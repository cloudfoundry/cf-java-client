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

package org.cloudfoundry.reactor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import okhttp3.Headers;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.cloudfoundry.AllowNulls;
import org.immutables.value.Value;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Value.Immutable
abstract class _TestRequest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Pattern PATH_PATTERN = Pattern.compile("[A-Z]+ (.*) [A-Z0-9\\./]+");

    public static Buffer getBuffer(String path) {
        try {
            return new Buffer().readFrom(new ClassPathResource(path).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void assertEquals(RecordedRequest request) {
        assertThat(getMethod()).hasToString(request.getMethod());
        assertThat(extractPath(request)).isEqualTo(getPath());

        assertThat(request.getHeader(HttpHeaderNames.TRANSFER_ENCODING.toString())).as("Does not have Transfer-Encoding header").isNull();

        if (!HttpMethod.GET.toString().equals(request.getMethod())) {
            assertThat(request.getHeader(HttpHeaderNames.CONTENT_LENGTH.toString())).as("Has Content-Length header").isNotNull();
        }

        getHeaders().forEach((key, value) -> {
            if (value == null) {
                assertThat(request.getHeader(key)).as("Does not have %s header", key).isNull();
            } else {
                assertThat(request.getHeader(key)).as("Header %s value", key).isEqualTo(value);
            }
        });

        if (getPayload().isPresent()) {
            assertBodyEquals(request.getBody(), getPayload().map(_TestRequest::getBuffer).get());
        } else if (getContents().isPresent()) {
            getContents().get().accept(Tuples.of(request.getHeaders(), request.getBody()));
        } else {
            assertThat(request.getBodySize()).as("Invalid request body: %s", request.getBody().readUtf8()).isEqualTo(0);
        }
    }

    abstract Optional<Consumer<Tuple2<Headers, Buffer>>> getContents();

    @AllowNulls
    abstract Map<String, String> getHeaders();

    abstract HttpMethod getMethod();

    abstract String getPath();

    abstract Optional<String> getPayload();

    private static void assertBodyEquals(Buffer actualBuffer, Buffer expectedBuffer) {
        assertThat(getValue(actualBuffer)).isEqualTo(getValue(expectedBuffer));
    }

    private static Object getValue(Buffer buffer) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(buffer.readByteArray());
            return root.isArray() ? OBJECT_MAPPER.treeToValue(root, List.class) : OBJECT_MAPPER.treeToValue(root, Map.class);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private String extractPath(RecordedRequest request) {
        Matcher matcher = PATH_PATTERN.matcher(request.getRequestLine());

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException(String.format("Request Line %s does not contain a valid path", request.getRequestLine()));
        }
    }

}
