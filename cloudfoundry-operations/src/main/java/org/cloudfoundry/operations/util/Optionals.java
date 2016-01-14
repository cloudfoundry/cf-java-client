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

package org.cloudfoundry.operations.util;

import reactor.fn.Function;

/**
 * Utilities for dealing with {@link Optional}s
 */
public final class Optionals {

    private Optionals() {
    }

    /**
     * Converts an instance to an {@link Optional} containing the instance
     *
     * @param <T> the type of the instance
     * @return an {@link Optional} of containing the instance
     */
    public static <T> Function<T, Optional<T>> toOptional() {
        return new Function<T, Optional<T>>() {

            @Override
            public Optional<T> apply(T value) {
                return Optional.of(value);
            }

        };
    }

}
