/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.RequestMatcher;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class ContentMatchers {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static RequestMatcher jsonPayload(final Resource resource) {
        return new RequestMatcher() {

            @Override
            public void match(ClientHttpRequest request) throws IOException, AssertionError {
                MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;

                Map<?, ?> expected = OBJECT_MAPPER.readValue(resource.getInputStream(), Map.class);
                Map<?, ?> actual = OBJECT_MAPPER.readValue(mockRequest.getBodyAsBytes(), Map.class);

                assertEquals(expected, actual);
            }

        };
    }

}
