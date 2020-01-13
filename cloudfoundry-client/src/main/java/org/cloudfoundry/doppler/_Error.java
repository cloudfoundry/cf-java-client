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

package org.cloudfoundry.doppler;

import org.immutables.value.Value;

import java.util.Objects;

/**
 * An Error event represents an error in the originating process
 */
@Value.Immutable
abstract class _Error {

    public static Error from(org.cloudfoundry.dropsonde.events.Error dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return Error.builder()
            .code(dropsonde.code)
            .message(dropsonde.message)
            .source(dropsonde.source)
            .build();
    }

    /**
     * The numeric error code. This is provided for programmatic responses to the error.
     */
    abstract Integer getCode();

    /**
     * The error description (preferably human-readable)
     */
    abstract String getMessage();

    /**
     * The source of the error
     */
    abstract String getSource();

}
