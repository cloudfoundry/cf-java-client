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

import io.netty.handler.codec.http.HttpResponseStatus;
import okhttp3.mockwebserver.MockResponse;
import okio.Buffer;
import org.cloudfoundry.AllowNulls;
import org.immutables.value.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
abstract class _TestResponse {

    private static final int MAX_CHUNK_SIZE = 16 * 1024;

    abstract Optional<String> getContentType();

    @AllowNulls
    abstract Map<String, String> getHeaders();

    MockResponse getMockResponse() {
        MockResponse response = new MockResponse().setResponseCode(getStatus().code());

        getHeaders().forEach(response::addHeader);

        getPayload()
            .map(_TestResponse::getBuffer)
            .ifPresent(buffer -> response
                .setHeader("Content-Type", getContentType().orElse("application/json"))
                .setChunkedBody(buffer, MAX_CHUNK_SIZE));

        return response;
    }

    abstract Optional<String> getPayload();

    abstract HttpResponseStatus getStatus();

    private static Buffer getBuffer(String path) {
        try {
            return new Buffer().readFrom(new ClassPathResource(path).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
