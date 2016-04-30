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

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import okhttp3.mockwebserver.MockResponse;
import okio.Buffer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Optional;

@Data
public final class TestResponse {

    private final Optional<String> contentType;

    private final Optional<Buffer> payload;

    private final HttpResponseStatus status;

    @Builder
    TestResponse(String contentType, String payload, @NonNull HttpResponseStatus status) {
        this.contentType = Optional.ofNullable(contentType);
        this.payload = Optional.ofNullable(payload).map(TestResponse::getBuffer);
        this.status = status;
    }

    MockResponse getMockResponse() {
        MockResponse response = new MockResponse().setResponseCode(status.code());

        this.payload.ifPresent(buffer -> response
            .setHeader("Content-Type", this.contentType.orElse("application/json"))
            .setBody(buffer));

        return response;
    }

    private static Buffer getBuffer(String path) {
        try {
            return new Buffer().readFrom(new ClassPathResource(path).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
