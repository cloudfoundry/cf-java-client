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

package org.cloudfoundry.doppler;

import okio.ByteString;
import org.junit.Test;

public final class LogMessageTest {

    @Test
    public void dropsonde() {
        LogMessage.from(new org.cloudfoundry.dropsonde.events.LogMessage.Builder()
            .message(ByteString.encodeUtf8("test-message"))
            .message_type(org.cloudfoundry.dropsonde.events.LogMessage.MessageType.ERR)
            .timestamp(0L)
            .build());
    }

    @Test(expected = IllegalStateException.class)
    public void noMessage() {
        LogMessage.builder()
            .messageType(MessageType.ERR)
            .timestamp(0L)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMessageType() {
        LogMessage.builder()
            .message("test-message")
            .timestamp(0L)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noTimestamp() {
        LogMessage.builder()
            .message("test-message")
            .messageType(MessageType.ERR)
            .build();
    }

    @Test
    public void valid() {
        LogMessage.builder()
            .message("test-message")
            .messageType(MessageType.ERR)
            .timestamp(0L)
            .build();
    }

}
