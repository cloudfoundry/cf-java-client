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

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Objects;

/**
 * Contains a "log line" and associated metadata.
 */
@Value.Immutable
abstract class _LogMessage {

    public static LogMessage from(org.cloudfoundry.dropsonde.events.LogMessage dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return LogMessage.builder()
            .applicationId(dropsonde.app_id)
            .message(dropsonde.message.utf8())
            .messageType(MessageType.from(dropsonde.message_type))
            .sourceInstance(dropsonde.source_instance)
            .sourceType(dropsonde.source_type)
            .timestamp(dropsonde.timestamp)
            .build();
    }

    /**
     * The application that emitted the message (or to which the application is related)
     */
    @Nullable
    abstract String getApplicationId();

    /**
     * The log message
     */
    abstract String getMessage();

    /**
     * The type of the message
     */
    abstract MessageType getMessageType();

    /**
     * The instance that emitted the message
     */
    @Nullable
    abstract String getSourceInstance();

    /**
     * The source of the message. For Cloud Foundry, this can be {@code APPLICATION}, {@code RTR}, {@code DEA}, {@code STG}, etc.
     */
    @Nullable
    abstract String getSourceType();

    /**
     * The UNIX timestamp (in nanoseconds) when the log was written
     */
    abstract Long getTimestamp();

}
