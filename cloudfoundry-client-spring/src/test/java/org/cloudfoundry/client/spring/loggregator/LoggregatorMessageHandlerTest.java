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

package org.cloudfoundry.client.spring.loggregator;

import com.google.protobuf.ByteString;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Test;

import java.util.Date;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.cloudfoundry.client.loggregator.LoggregatorMessage.MessageType.ERR;

public final class LoggregatorMessageHandlerTest {

    private final TestSubscriber<LoggregatorMessage> testSubscriber = new TestSubscriber<>();

    private final LoggregatorMessageHandler messageHandler = new LoggregatorMessageHandler(this.testSubscriber);

    @Test
    public void onMessage() throws InterruptedException {
        Date timestamp = new Date();

        LoggregatorProtocolBuffers.LogMessage logMessage = LoggregatorProtocolBuffers.LogMessage.newBuilder()
                .setAppId("test-app-id")
                .addDrainUrls("test-drain-url")
                .setMessage(ByteString.copyFromUtf8("test-message"))
                .setMessageType(LoggregatorProtocolBuffers.LogMessage.MessageType.ERR)
                .setSourceId("test-source-id")
                .setSourceName("test-source-name")
                .setTimestamp(MILLISECONDS.toNanos(timestamp.getTime()))
                .build();

        this.messageHandler.onMessage(logMessage.toByteArray());

        this.testSubscriber.onComplete();
        this.testSubscriber
                .assertEquals(LoggregatorMessage.builder()
                        .applicationId("test-app-id")
                        .drainUrl("test-drain-url")
                        .message("test-message")
                        .messageType(ERR)
                        .sourceId("test-source-id")
                        .sourceName("test-source-name")
                        .timestamp(timestamp)
                        .build())
                .verify(5, SECONDS);
    }

    @Test
    public void onMessageError() throws InterruptedException {
        this.messageHandler.onMessage(new byte[0]);

        this.testSubscriber
                .assertError(Exception.class)
                .verify(5, SECONDS);
    }

}
