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

package org.cloudfoundry.util;

import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Utilities for dealing with {@link Exception}s
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    /**
     * Converts {@link Throwable}s to more descriptive exception types, attaching the original {@link Throwable} as the new cause.
     *
     * <ul> <li>{@link NoSuchElementException} to {@link IllegalArgumentException}</li> </ul>
     *
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string.  If there are more arguments than format specifiers, the extra arguments are ignored.  The number of arguments
     *               is variable and may be zero.  The maximum number of arguments is limited by the maximum dimension of a Java array as defined by <cite>The Java&trade; Virtual Machine
     *               Specification</cite>. The behaviour on a {@code null} argument depends on the <a href="../util/Formatter.html#syntax">conversion</a>.
     * @param <T>    the type of the {@link Mono} being converted
     * @return a function that converts errors
     */
    public static <T> Function<Throwable, Mono<T>> convert(final String format, final Object... args) {
        return throwable -> {
            if (throwable instanceof NoSuchElementException) {
                String message = String.format(format, args);
                return Mono.error(new IllegalArgumentException(message, throwable));
            } else {
                return Mono.error(throwable);
            }
        };
    }

    /**
     * Returns a {@link Mono} containing an {@link IllegalStateException} with the configured message
     *
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string.  If there are more arguments than format specifiers, the extra arguments are ignored.  The number of arguments
     *               is variable and may be zero.  The maximum number of arguments is limited by the maximum dimension of a Java array as defined by <cite>The Java&trade; Virtual Machine
     *               Specification</cite>. The behaviour on a {@code null} argument depends on the <a href="../util/Formatter.html#syntax">conversion</a>.
     * @param <T>    the type of the {@link Mono} being converted
     * @return a {@link Mono} containing the error
     */
    public static <T> Mono<T> illegalState(final String format, final Object... args) {
        String message = String.format(format, args);
        return Mono.error(new IllegalStateException(message));
    }

}
