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

import reactor.Mono;
import reactor.fn.Function;

import java.util.NoSuchElementException;

/**
 * Utilities for dealing with {@link Exception}s
 */
public final class Exceptions {

    private Exceptions() {
    }

    /**
     * Converts {@link Throwable}s to more descriptive exception types, attaching the original {@link Throwable} as the new cause.
     *
     * <ul> <li>{@link NoSuchElementException} to {@link IllegalArgumentException}</li> </ul>
     *
     * @param message the message to add to the converted exception
     * @param <T>     the type of the {@link Mono} being converted
     * @return a function that converts errors
     */
    public static <T> Function<Throwable, Mono<T>> convert(final String message) {
        return new Function<Throwable, Mono<T>>() {

            @Override
            public Mono<T> apply(Throwable throwable) {
                if (throwable instanceof NoSuchElementException) {
                    return Mono.error(new IllegalArgumentException(message, throwable));
                } else {
                    return Mono.error(throwable);
                }
            }

        };
    }

}
