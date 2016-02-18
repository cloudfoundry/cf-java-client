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

package org.cloudfoundry.spring.logging;

import org.cloudfoundry.logging.LogMessage;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpInputMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class LoggregatorMessageHttpMessageConverterTest {

    private static final MediaType MEDIA_TYPE = MediaType.parseMediaType("multipart/x-protobuf; boundary=90ad9060c87222ee30ddcffe751393a7c5734c48e070a623121abf82eb3c");

    private final LoggregatorMessageHttpMessageConverter messageConverter = new LoggregatorMessageHttpMessageConverter();

    @Test
    public void canWrite() {
        assertFalse(this.messageConverter.canWrite(null));
    }

    @Test
    public void mediaType() {
        for (MediaType mediaType : this.messageConverter.getSupportedMediaTypes()) {
            assertTrue(mediaType.isCompatibleWith(MEDIA_TYPE));
        }
    }

    @Test
    public void readInternal() throws IOException, InterruptedException {
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(new ClassPathResource("logging/loggregator_response.bin").getInputStream());
        inputMessage.getHeaders().setContentType(MEDIA_TYPE);

        List<LogMessage> messages = this.messageConverter.readInternal(null, inputMessage);

        assertEquals(14, messages.size());
    }

    @Test
    public void supports() {
        assertFalse(this.messageConverter.supports(Map.class));
        assertTrue(this.messageConverter.supports(List.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void writeInternal() {
        this.messageConverter.writeInternal(null, null);
    }
}
