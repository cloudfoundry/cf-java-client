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

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;

/**
 * The result of a validation attempt.
 */
@Data
public final class ValidationResult {

    /**
     * The status of the validation attempt
     *
     * @param status the status of the validation attempt
     * @return the status of the validation attempt
     */
    private final Status status;

    /**
     * The messages indicating why validation failed
     *
     * @param messages the messages indicating why validation failed
     * @return the messages indicating why validation failed
     */
    private final List<String> messages;

    @Builder
    ValidationResult(@Singular List<String> messages) {
        this.status = messages.isEmpty() ? Status.VALID : INVALID;
        this.messages = messages;
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
