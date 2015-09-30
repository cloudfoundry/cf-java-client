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

package org.cloudfoundry.client.loggregator;

import org.cloudfoundry.client.loggregator.LoggregatorProtocolBuffers.LogMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Base class for Loggregator messages
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class LoggregatorMessage {

    private volatile String applicationId;

    private final List<String> drainUrls = new ArrayList<>();

    private volatile String message;

    private volatile MessageType messageType;

    private volatile String sourceId;

    private volatile String sourceName;

    private volatile Date timestamp;

    /**
     * Creates a new instance from the protobuf implementation
     *
     * @param logMessage the protobuf implementation
     * @return a new instance
     */
    public static LoggregatorMessage from(LogMessage logMessage) {
        return new LoggregatorMessage()
                .withApplicationId(logMessage.getAppId())
                .withDrainUrls(logMessage.getDrainUrlsList())
                .withMessage(logMessage.getMessage().toStringUtf8())
                .withMessageType(MessageType.valueOf(logMessage.getMessageType().toString()))
                .withSourceId(logMessage.getSourceId())
                .withSourceName(logMessage.getSourceName())
                .withTimestamp(new Date(NANOSECONDS.toMillis(logMessage.getTimestamp())));
    }

    /**
     * Returns the application id
     *
     * @return the application id
     */
    public String getApplicationId() {
        return this.applicationId;
    }

    /**
     * Configure the application id
     *
     * @param applicationId the application id
     * @return {@code this}
     */
    public LoggregatorMessage withApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    /**
     * Returns the drain urls
     *
     * @return the drain urls
     */
    public List<String> getDrainUrls() {
        return this.drainUrls;
    }

    /**
     * Configure a drain url
     *
     * @param drainUrl the drain url
     * @return {@code this}
     */
    public LoggregatorMessage withDrainUrl(String drainUrl) {
        this.drainUrls.add(drainUrl);
        return this;
    }

    /**
     * Configure the drain urls
     *
     * @param drainUrls the drain urls
     * @return {@code this}
     */

    public LoggregatorMessage withDrainUrls(List<String> drainUrls) {
        this.drainUrls.addAll(drainUrls);
        return this;
    }

    /**
     * Returns the message
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Configure the message
     *
     * @param message the message
     * @return {@code this}
     */
    public LoggregatorMessage withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Returns the message type
     *
     * @return the message type
     */
    public MessageType getMessageType() {
        return this.messageType;
    }

    /**
     * Configure the message type
     *
     * @param messageType the message type
     * @return {@code this}
     */
    public LoggregatorMessage withMessageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    /**
     * Returns the source id
     *
     * @return the source id
     */
    public String getSourceId() {
        return this.sourceId;
    }

    /**
     * Configure the source id
     *
     * @param sourceId the source id
     * @return {@code this}
     */
    public LoggregatorMessage withSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    /**
     * Returns the source name
     *
     * @return the source name
     */
    public String getSourceName() {
        return this.sourceName;
    }

    /**
     * Configure the source name
     *
     * @param sourceName the source name
     * @return {@code this}
     */
    public LoggregatorMessage withSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    /**
     * Returns the timestamp
     *
     * @return the timestamp
     */
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * Configure the timestamp
     *
     * @param timestamp the timestamp
     * @return {@code this}
     */
    public LoggregatorMessage withTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
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
