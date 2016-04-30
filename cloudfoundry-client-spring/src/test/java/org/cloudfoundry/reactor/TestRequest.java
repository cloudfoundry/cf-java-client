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
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Data
public final class TestRequest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Optional<Boolean> anyPayload;

    private final HttpMethod method;

    private final String path;

    private final Optional<Buffer> payload;

    @Builder
    TestRequest(Boolean anyPayload, @NonNull HttpMethod method, @NonNull String path, String payload) {
        this.anyPayload = Optional.ofNullable(anyPayload);
        this.method = method;
        this.path = path;
        this.payload = Optional.ofNullable(payload).map(TestRequest::getBuffer);
    }

    public void assertEquals(RecordedRequest request) {
        Assert.assertEquals(this.method.toString(), request.getMethod());
        Assert.assertEquals(this.path, request.getPath());

        if (!this.anyPayload.orElse(false)) {
            if (this.payload.isPresent()) {
                assertBodyEquals(this.payload.get(), request.getBody());
            } else {
                Assert.assertEquals(0, request.getBodySize());
            }
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

}
