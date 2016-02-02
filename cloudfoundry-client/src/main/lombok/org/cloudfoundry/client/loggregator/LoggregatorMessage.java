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

package org.cloudfoundry.client.loggregator;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers.LogMessage;

import java.util.Date;
import java.util.List;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Base class for Loggregator messages
 */
@Data
public final class LoggregatorMessage {

    private final String applicationId;

    private final List<String> drainUrls;

    private final String message;

    private final MessageType messageType;

    private final String sourceId;

    private final String sourceName;

    private final Date timestamp;

    @Builder
    LoggregatorMessage(String applicationId,
                       @Singular List<String> drainUrls,
                       String message,
                       MessageType messageType,
                       String sourceId,
                       String sourceName,
                       Date timestamp) {
        this.applicationId = applicationId;
        this.drainUrls = drainUrls;
        this.message = message;
        this.messageType = messageType;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.timestamp = timestamp;
    }

    /**
     * Creates a new instance from the protobuf implementation
     *
     * @param logMessage the protobuf implementation
     * @return a new instance
     */
    public static LoggregatorMessage from(LogMessage logMessage) {
        return LoggregatorMessage.builder()
            .applicationId(logMessage.getAppId())
            .drainUrls(logMessage.getDrainUrlsList())
            .message(logMessage.getMessage().toStringUtf8())
            .messageType(MessageType.valueOf(logMessage.getMessageType().toString()))
            .sourceId(logMessage.getSourceId())
            .sourceName(logMessage.getSourceName())
            .timestamp(new Date(NANOSECONDS.toMillis(logMessage.getTimestamp())))
            .build();
    }

    /**
     * The type of message
     */
    public enum MessageType {

        /**
         * {@code stderr} message type
         */
        ERR,

        /**
         * {@code stdout} message type
         */
        OUT

    }

}
