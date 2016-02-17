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
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.rx.Promise;
import reactor.rx.Stream;

/**
 * A utility class with operators that do not exist
 */
public final class OperationUtils {

    private OperationUtils() {
    }

    /**
     * A predicate that produces the logical AND of its parameter predicates.
     *
     * <p>The predicates are tested in order and the first <b>{@code false}</b> result terminates the test. This is analogous to {@code &&}. </p>
     *
     * @param predicates the predicates to logically AND
     * @param <T>        the type of the test item
     * @return the logical AND predicate
     */
    @SafeVarargs
    public static <T> Predicate<T> and(final Predicate<T>... predicates) {
        return new Predicate<T>() {

            @Override
            public boolean test(T t) {
                for (Predicate<T> predicate : predicates) {
                    if (!predicate.test(t)) return false;
                }
                return true;
            }
        };
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
     * A predicate that negates the result of a delegate predicate
     *
     * @param predicate the delegate predicate
     * @param <T>       the type of the test item
     * @return the negating predicate
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
     * A predicate that produces the logical OR of its parameter predicates.
     *
     * <p>The predicates are tested in order and the first <b>{@code true}</b> result terminates the test. This is analogous to {@code ||}. </p>
     *
     * @param predicates the predicates to logically OR
     * @param <T>        the type of the test item
     * @return the logical OR predicate
     */
    @SafeVarargs
    public static <T> Predicate<T> or(final Predicate<T>... predicates) {
        return new Predicate<T>() {

            @Override
            public boolean test(T t) {
                for (Predicate<T> predicate : predicates) {
                    if (predicate.test(t)) return true;
                }
                return false;
            }
        };
    }

    /**
     * Converts a {@link Publisher} into a {@link Promise}
     *
     * @param <T> the type published
     * @return the promise
     */
    public static <T> Function<Publisher<T>, Promise<T>> promise() {
        return new Function<Publisher<T>, Promise<T>>() {

            @Override
            public Promise<T> apply(Publisher<T> publisher) {
                return Promise.from(publisher);
            }
        };
    }

    /**
     * Adds the {@code repeatWhen} operator to {@link Mono} until it is added natively
     *
     * @param f   the {@code repeatWhen} function
     * @param <T> the type of the stream
     * @return the {@code Mono} after it has been waited for
     */
    public static <T> Function<Mono<T>, Mono<T>> repeatWhen(final Function<Stream<Long>, ? extends Publisher<?>> f) { // TODO: Remove once Mono.repeatWhen()
        return new Function<Mono<T>, Mono<T>>() {

            @Override
            public Mono<T> apply(Mono<T> mono) {
                return mono
                    .as(OperationUtils.<T>stream())
                    .repeatWhen(f)
                    .single();
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
