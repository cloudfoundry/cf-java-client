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

package org.cloudfoundry.client.spring.loggregator;

import com.google.protobuf.ByteString;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers;
import org.cloudfoundry.client.spring.TestSubscriber;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.cloudfoundry.client.loggregator.LoggregatorMessage.MessageType.ERR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public final class LoggregatorMessageHandlerTest {

    private final TestSubscriber<LoggregatorMessage> subscriber = new TestSubscriber<>();

    private final LoggregatorMessageHandler messageHandler = new LoggregatorMessageHandler(this.subscriber);

    @Test
    public void onMessage() {
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

        LoggregatorMessage message = this.subscriber.getOnNextEvents().get(0);
        assertEquals("test-app-id", message.getApplicationId());
        assertEquals(Collections.singletonList("test-drain-url"), message.getDrainUrls());
        assertEquals("test-message", message.getMessage());
        assertEquals(ERR, message.getMessageType());
        assertEquals("test-source-id", message.getSourceId());
        assertEquals("test-source-name", message.getSourceName());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    public void onMessageError() {
        this.messageHandler.onMessage(new byte[0]);

        assertFalse(this.subscriber.getOnErrorEvents().isEmpty());
    }
}
