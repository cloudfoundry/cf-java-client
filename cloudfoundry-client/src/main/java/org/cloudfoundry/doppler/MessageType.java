/*
 * Copyright 2013-2019 the original author or authors.
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

import org.cloudfoundry.loggregator.v2.LoggregatorEnvelope;

import java.util.Objects;

/**
 * The destination of the message
 */
public enum MessageType {

    /**
     * {@code STDERR}
     */
    ERR,

    /**
     * {@code STDOUT}
     */
    OUT;

    static MessageType from(org.cloudfoundry.dropsonde.events.LogMessage.MessageType dropsonde) {
        switch (Objects.requireNonNull(dropsonde, "dropsonde")) {
            case ERR:
                return ERR;
            case OUT:
                return OUT;
            default:
                throw new IllegalArgumentException(String.format("Unknown message type: %s", dropsonde));
        }
    }
    static MessageType from(LoggregatorEnvelope.Log.Type type) {
        switch (Objects.requireNonNull(type, "log type")) {
            case ERR:
                return ERR;
            case OUT:
                return OUT;
            default:
                throw new IllegalArgumentException(String.format("Unknown message type: %s", type));
        }
    }

}
