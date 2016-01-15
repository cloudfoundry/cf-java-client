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

package org.cloudfoundry.client.spring.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.fn.Function;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers.LogMessage;

public final class MultipartTest {

    private static final String BOUNDARY = "90ad9060c87222ee30ddcffe751393a7c5734c48e070a623121abf82eb3c";

    @Test
    public void test() throws IOException, InterruptedException {
        TestSubscriber<LogMessage> testSubscriber = new TestSubscriber<>();

        Multipart.from(new ClassPathResource("loggregator_response.bin").getInputStream(), BOUNDARY)
                .map(toLogMessage())
                .subscribe(testSubscriber
                        .assertCount(14));

        testSubscriber.verify(5, SECONDS);
    }

    private Function<byte[], LogMessage> toLogMessage() {
        return new Function<byte[], LogMessage>() {

            @Override
            public LogMessage apply(byte[] part) {
                try {
                    return LogMessage.parseFrom(part);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }

}
