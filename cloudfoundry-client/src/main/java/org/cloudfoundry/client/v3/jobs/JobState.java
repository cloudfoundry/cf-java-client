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

package org.cloudfoundry.client.v3.jobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The state of a {@link Job}
 */
public enum JobState {

    /**
     * The complete state
     */
    COMPLETE("COMPLETE"),

    /**
     * The failed state
     */
    FAILED("FAILED"),

    /**
     * The polling state
     */
    POLLING("POLLING"),

    /**
     * The processing state
     */
    PROCESSING("PROCESSING");

    private final String value;

    JobState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static JobState from(String s) {
        switch (s.toLowerCase()) {
            case "complete":
                return COMPLETE;
            case "failed":
                return FAILED;
            case "polling":
                return POLLING;
            case "processing":
                return PROCESSING;
            default:
                throw new IllegalArgumentException(String.format("Unknown job state: %s", s));
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
