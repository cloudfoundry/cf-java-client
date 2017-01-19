/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.bosh.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The state of a task
 */
public enum State {

    CANCELLED("cancelled"),

    CANCELLING("cancelling"),

    DONE("done"),

    ERROR("error"),

    PROCESSING("processing"),

    QUEUED("queued"),

    TIMEOUT("timeout");

    private final String value;

    State(String value) {
        this.value = value;
    }

    @JsonCreator
    public static State from(String s) {
        switch (s.toLowerCase()) {
            case "cancelled":
                return CANCELLED;
            case "cancelling":
                return CANCELLING;
            case "done":
                return DONE;
            case "error":
                return ERROR;
            case "processing":
                return PROCESSING;
            case "queued":
                return QUEUED;
            case "timeout":
                return TIMEOUT;
            default:
                throw new IllegalArgumentException(String.format("Unknown state: %s", s));
        }
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return getValue();
    }

}
