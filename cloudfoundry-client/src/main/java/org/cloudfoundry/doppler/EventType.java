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

import java.util.Objects;

/**
 * Event Types
 */
public enum EventType {

    CONTAINER_METRIC,

    COUNTER_EVENT,

    ERROR,

    HTTP_START_STOP,

    LOG_MESSAGE,

    VALUE_METRIC;

    static EventType from(org.cloudfoundry.dropsonde.events.Envelope.EventType dropsonde) {
        switch (Objects.requireNonNull(dropsonde, "dropsonde")) {
            case ContainerMetric:
                return CONTAINER_METRIC;
            case CounterEvent:
                return COUNTER_EVENT;
            case Error:
                return ERROR;
            case HttpStartStop:
                return HTTP_START_STOP;
            case LogMessage:
                return LOG_MESSAGE;
            case ValueMetric:
                return VALUE_METRIC;
            default:
                throw new IllegalArgumentException(String.format("Unknown event type: %s", dropsonde));
        }
    }

}
