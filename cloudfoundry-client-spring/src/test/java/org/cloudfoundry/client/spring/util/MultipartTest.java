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

package org.cloudfoundry.client.spring.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers.LogMessage;
import static org.junit.Assert.assertEquals;

public final class MultipartTest {

    private static final String BOUNDARY = "90ad9060c87222ee30ddcffe751393a7c5734c48e070a623121abf82eb3c";

    @Test
    public void test() throws IOException {
        Long count = Multipart.from(new ClassPathResource("loggregator_response.bin").getInputStream(), BOUNDARY)
                .map(part -> {
                    try {
                        return LogMessage.parseFrom(part);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                })
                .count()
                .next().get();

        assertEquals(Long.valueOf(14), count);
    }
}
