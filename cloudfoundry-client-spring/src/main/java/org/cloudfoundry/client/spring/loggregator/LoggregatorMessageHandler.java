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

import com.google.protobuf.InvalidProtocolBufferException;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.reactivestreams.Subscriber;

import javax.websocket.MessageHandler;

import static org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers.LogMessage;

public final class LoggregatorMessageHandler implements MessageHandler.Whole<byte[]> {

    private final Subscriber<LoggregatorMessage> subscriber;

    public LoggregatorMessageHandler(Subscriber<LoggregatorMessage> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onMessage(byte[] message) {
        try {
            LogMessage logMessage = LogMessage.parseFrom(message);
            LoggregatorMessage loggregatorMessage = LoggregatorMessage.from(logMessage);

            this.subscriber.onNext(loggregatorMessage);
        } catch (InvalidProtocolBufferException e) {
            this.subscriber.onError(e);
        }
    }

}
