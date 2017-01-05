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

import org.cloudfoundry.client.v2.ClientV2Exception;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Utilities for dealing with {@link Exception}s
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
    }

    /**
     * Returns a {@link Mono} containing an {@link IllegalArgumentException} with the configured message
     *
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string.  If there are more arguments than format specifiers, the extra arguments are ignored.  The number of arguments
     *               is variable and may be zero.  The maximum number of arguments is limited by the maximum dimension of a Java array as defined by <cite>The Java&trade; Virtual Machine
     *               Specification</cite>. The behaviour on a {@code null} argument depends on the <a href="../util/Formatter.html#syntax">conversion</a>.
     * @param <T>    the type of the {@link Mono} being converted
     * @return a {@link Mono} containing the error
     */
    public static <T> Mono<T> illegalArgument(String format, Object... args) {
        String message = String.format(format, args);
        return Mono.error(new IllegalArgumentException(message));
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
    public static <T> Mono<T> illegalState(String format, Object... args) {
        String message = String.format(format, args);
        return Mono.error(new IllegalStateException(message));
    }

    /**
     * A predicate that returns {@code true} if the exception is a {@link ClientV2Exception} and its code matches expectation
     *
     * @param codes the codes to match
     * @return {@code true} if the exception is a {@link ClientV2Exception} and its code matches
     */
    public static Predicate<? super Throwable> statusCode(int... codes) {
        return t -> t instanceof ClientV2Exception &&
            Arrays.stream(codes).anyMatch(candidate -> ((ClientV2Exception) t).getCode().equals(candidate));
    }

}
