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

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Optional;

/**
 * An Error event represents an error in the originating process
 */
@Data
public final class Error implements Event, Validatable {

    /**
     * The numeric error code. This is provided for programmatic responses to the error.
     *
     * @param code the numeric error code
     * @return the numeric error code
     */
    private final Integer code;

    /**
     * The error description (preferably human-readable)
     *
     * @param message the error description
     * @return the error description
     */
    private final String message;

    /**
     * The source of the error
     *
     * @param source the source of the error
     * @return the source of the error
     */
    private final String source;

    @Builder
    Error(org.cloudfoundry.dropsonde.events.Error dropsonde, Integer code, String message, String source) {
        Optional<org.cloudfoundry.dropsonde.events.Error> o = Optional.ofNullable(dropsonde);

        this.code = o.map(d -> d.code).orElse(code);
        this.message = o.map(d -> d.message).orElse(message);
        this.source = o.map(d -> d.source).orElse(source);
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.code == null) {
            builder.message("code must be specified");
        }

        if (this.message == null) {
            builder.message("message must be specified");
        }

        if (this.source == null) {
            builder.message("source must be specified");
        }

        return builder.build();
    }

}
