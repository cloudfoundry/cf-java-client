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

package org.cloudfoundry.utils;

import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.rx.Stream;

/**
 * A utility class with operators that do not exist
 */
public final class OperationUtils {

    private OperationUtils() {
    }

    /**
     * Casts an item from one type to another
     *
     * @param <IN>  the source type
     * @param <OUT> the target type
     * @return the same instance
     */
    public static <IN extends OUT, OUT> Function<IN, OUT> cast() {
        return new Function<IN, OUT>() {

            @Override
            public OUT apply(IN in) {
                return in;
            }

        };
    }

    /**
     * A predicate that returns the value as the result
     *
     * @return the value
     */
    public static Predicate<Boolean> identity() {
        return new Predicate<Boolean>() {

            @Override
            public boolean test(Boolean b) {
                return b;
            }

        };
    }

    /**
     * A predicate that reverses the result of a delegate predicate
     *
     * @param predicate the delegate predicate
     * @param <T>       the type of the test item
     * @return the reversing predicate
     */
    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return new Predicate<T>() {

            @Override
            public boolean test(T t) {
                return !predicate.test(t);
            }

        };
    }

    /**
     * Converts a {@link Publisher} into a {@link Stream}
     *
     * @param <T> the type published
     * @return the stream
     */
    public static <T> Function<Publisher<T>, Stream<T>> stream() {
        return new Function<Publisher<T>, Stream<T>>() {

            @Override
            public Stream<T> apply(Publisher<T> publisher) {
                return Stream.from(publisher);
            }

        };
    }

}
