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

package org.cloudfoundry.client.v3.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The state of the {@link Task}
 */
public enum TaskState {

    /**
     * The canceling state
     */
    CANCELING("CANCELING"),

    /**
     * The failed state
     */
    FAILED("FAILED"),

    /**
     * The pending state
     */
    PENDING("PENDING"),

    /**
     * The running state
     */
    RUNNING("RUNNING"),

    /**
     * The succeeded state
     */
    SUCCEEDED("SUCCEEDED");

    private final String value;

    TaskState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TaskState from(String s) {
        switch (s.toLowerCase()) {
            case "canceling":
                return CANCELING;
            case "failed":
                return FAILED;
            case "pending":
                return PENDING;
            case "running":
                return RUNNING;
            case "succeeded":
                return SUCCEEDED;
            default:
                throw new IllegalArgumentException(String.format("Unknown task state: %s", s));
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
