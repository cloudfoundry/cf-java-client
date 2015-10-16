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

package org.cloudfoundry.client;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a validation attempt.
 *
 * <p><b>This class is NOT threadsafe</b>
 */
public final class ValidationResult {

    private volatile Status status = Status.VALID;

    private final List<String> messages = new ArrayList<>();

    /**
     * Returns the messages indicating why validation failed
     *
     * @return the messages indicating why validation failed
     */
    public List<String> getMessages() {
        return this.messages;
    }

    /**
     * Returns the status of the validation attempt
     *
     * @return the status of the validation attempt
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Marks the {@link ValidationResult} as invalid and adds a message indicating why.  This method can be called
     * multiple times to indicate multiple validation failures.
     *
     * @param message the message indicating why validation failed
     * @return {@code this}
     */
    public ValidationResult invalid(String message) {
        this.status = Status.INVALID;
        this.messages.add(message);
        return this;
    }

    /**
     * The status of the {@link ValidationResult}
     */
    public enum Status {

        /**
         * Indicates that the validation is valid
         */
        VALID,

        /**
         * Indicates that the validation is invalid
         */
        INVALID

    }

}
