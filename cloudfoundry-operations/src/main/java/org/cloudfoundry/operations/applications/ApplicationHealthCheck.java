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

package org.cloudfoundry.operations.applications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The health check type of an application
 */
public enum ApplicationHealthCheck {

    /**
     * HTTP health check
     */
    HTTP("http"),

    /**
     * No health check
     */
    NONE("none"),

    /**
     * Process health check
     */
    PROCESS("process"),

    /**
     * Port health check
     */
    PORT("port");

    private final String value;

    ApplicationHealthCheck(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ApplicationHealthCheck from(String s) {
        switch (s.toLowerCase()) {
            case "http":
                return HTTP;
            case "none":
                return NONE;
            case "port":
                return PORT;
            case "process":
                return PROCESS;
            default:
                throw new IllegalArgumentException(String.format("Unknown application health check: %s", s));
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
