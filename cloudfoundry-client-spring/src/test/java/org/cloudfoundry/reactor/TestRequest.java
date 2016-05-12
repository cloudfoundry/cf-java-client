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

package org.cloudfoundry.reactor;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import okhttp3.Headers;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;
import reactor.core.tuple.Tuple;
import reactor.core.tuple.Tuple2;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public final class TestRequest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Pattern PATH_PATTERN = Pattern.compile("[A-Z]+ (.*) [A-Z0-9\\./]+");

    private final Optional<Consumer<Tuple2<Headers, Buffer>>> contents;

    private final HttpMethod method;

    private final String path;

    private final Optional<Buffer> payload;

    @Builder
    TestRequest(Consumer<Tuple2<Headers, Buffer>> contents, @NonNull HttpMethod method, @NonNull String path, String payload) {
        this.contents = Optional.ofNullable(contents);
        this.method = method;
        this.path = path;
        this.payload = Optional.ofNullable(payload).map(TestRequest::getBuffer);
    }

    public void assertEquals(RecordedRequest request) {
        Assert.assertEquals(this.method.toString(), request.getMethod());
        Assert.assertEquals(this.path, extractPath(request));

        if (this.payload.isPresent()) {
            assertBodyEquals(this.payload.get(), request.getBody());
        } else if (this.contents.isPresent()) {
            this.contents.get().accept(Tuple.of(request.getHeaders(), request.getBody()));
        } else {
            Assert.assertEquals(0, request.getBodySize());
        }
    }

    private static void assertBodyEquals(Buffer expectedBuffer, Buffer actualBuffer) {
        try {
            Map<?, ?> expected = OBJECT_MAPPER.readValue(expectedBuffer.readByteArray(), Map.class);
            Map<?, ?> actual = OBJECT_MAPPER.readValue(actualBuffer.readByteArray(), Map.class);
            Assert.assertEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Buffer getBuffer(String path) {
        try {
            return new Buffer().readFrom(new ClassPathResource(path).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
