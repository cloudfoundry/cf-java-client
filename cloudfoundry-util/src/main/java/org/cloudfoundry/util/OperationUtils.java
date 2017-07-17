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

package org.cloudfoundry.util;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

/**
 * Utilities with operations that do not (yet) exist
 */
public final class OperationUtils {

    private OperationUtils() {
    }

    /**
     * Operation to collect a {@code Flux<byte[]>} into a contiguous single byte array, delivered as a single element of a {@code Mono<byte[]>}.
     *
     * @param bytes a Flux of 0 or more byte arrays
     * @return a Mono of a byte array
     */
    public static Mono<byte[]> collectByteArray(Flux<byte[]> bytes) {
        return bytes
            .reduceWith(ByteArrayOutputStream::new, (prev, next) -> {
                try {
                    prev.write(next);
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
                return prev;
            })
            .map(ByteArrayOutputStream::toByteArray);
    }

}
