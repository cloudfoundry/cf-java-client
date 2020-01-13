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

package org.cloudfoundry.routing.v1.tcproutes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The event type
 */
public enum EventType {

    /**
     * The delete event type
     */
    DELETE("DELETE"),

    /**
     * The upsert event type
     */
    UPSERT("UPSERT");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EventType from(String s) {
        switch (s.toLowerCase()) {
            case "delete":
                return DELETE;
            case "upsert":
                return UPSERT;
            default:
                throw new IllegalArgumentException(String.format("Unknown event type: %s", s));
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
