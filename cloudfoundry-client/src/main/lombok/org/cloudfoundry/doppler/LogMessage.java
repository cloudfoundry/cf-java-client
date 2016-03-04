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

package org.cloudfoundry.doppler;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import okio.ByteString;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Optional;

@Data
public final class LogMessage implements Event, Validatable {

    /**
     * The application that emitted the message (or to which the application is related)
     *
     * @param applicationId the application that emitted the message
     * @return the application that emitted the message
     */
    private final String applicationId;

    /**
     * The log message
     *
     * @param message the log message
     * @return the log message
     */
    private final String message;

    /**
     * The type of the message
     *
     * @param messageType the type of the message
     * @return the type of the message
     */
    private final MessageType messageType;

    /**
     * The instance that emitted the message
     *
     * @param sourceInstance the instance that emitted the message
     * @return the instance that emitted the message
     */
    private final String sourceInstance;

    /**
     * The source of the message. For Cloud Foundry, this can be {@code APP}, {@code RTR}, {@code DEA}, {@code STG}, etc.
     *
     * @param sourceType the source of the message
     * @return the source of the message
     */
    private final String sourceType;

    /**
     * The UNIX timestamp (in nanoseconds) when the log was written
     *
     * @param timestamp the UNIX timestamp
     * @return the UNIX timestamp
     */
    private final Long timestamp;

    @Builder
    LogMessage(org.cloudfoundry.dropsonde.events.LogMessage dropsonde, String applicationId, String message, MessageType messageType, String sourceInstance, String sourceType, Long timestamp) {
        Optional<org.cloudfoundry.dropsonde.events.LogMessage> o = Optional.ofNullable(dropsonde);

        this.applicationId = o.map(v -> v.app_id).orElse(applicationId);
        this.message = o.map(d -> d.message).map(ByteString::utf8).orElse(message);
        this.messageType = o.map(d -> d.message_type).map(MessageType::dropsonde).orElse(messageType);
        this.sourceInstance = o.map(d -> d.source_instance).orElse(sourceInstance);
        this.sourceType = o.map(d -> d.source_type).orElse(sourceType);
        this.timestamp = o.map(d -> d.timestamp).orElse(timestamp);
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.message == null) {
            builder.message("message must be specified");
        }

        if (this.messageType == null) {
            builder.message("message type must be specified");
        }

        if (this.timestamp == null) {
            builder.message("timestamp must be specified");
        }

        return builder.build();
    }

    /**
     * The destination of the message
     */
    @ToString
    public enum MessageType {

        /**
         * {@code STDERR}
         */
        ERR,

        /**
         * {@code STDOUT}
         */
        OUT;

        static MessageType dropsonde(org.cloudfoundry.dropsonde.events.LogMessage.MessageType dropsonde) {
            switch (dropsonde) {
                case ERR:
                    return ERR;
                case OUT:
                    return OUT;
                default:
                    throw new IllegalArgumentException("Unknown message type");
            }
        }

    }

}
