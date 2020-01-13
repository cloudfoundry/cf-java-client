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

package org.cloudfoundry.util.tuple;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuple6;
import reactor.util.function.Tuple7;
import reactor.util.function.Tuple8;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class TupleUtils {

    private TupleUtils() {
    }

    /**
     * Returns a {@link Consumer} of {@link Tuple2} that wraps a consumer of the component values of the tuple
     *
     * @param consumer the component value consumer
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @return the wrapper consumer
     */
    public static <T1, T2> Consumer<Tuple2<T1, T2>> consumer(Consumer2<T1, T2> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2());
    }

    /**
     * Returns a {@link Consumer} of {@link Tuple3} that wraps a consumer of the component values of the tuple
     *
     * @param consumer the component value consumer
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @return the wrapper consumer
     */
    public static <T1, T2, T3> Consumer<Tuple3<T1, T2, T3>> consumer(Consumer3<T1, T2, T3> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2(), tuple.getT3());
    }

    /**
     * Returns a {@link Consumer} of {@link Tuple4} that wraps a consumer of the component values of the tuple
     *
     * @param consumer the component value consumer
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @return the wrapper consumer
     */
    public static <T1, T2, T3, T4> Consumer<Tuple4<T1, T2, T3, T4>> consumer(Consumer4<T1, T2, T3, T4> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4());
    }

    /**
     * Returns a {@link Consumer} of {@link Tuple5} that wraps a consumer of the component values of the tuple
     *
     * @param consumer the component value consumer
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @return the wrapper consumer
     */
    public static <T1, T2, T3, T4, T5> Consumer<Tuple5<T1, T2, T3, T4, T5>> consumer(Consumer5<T1, T2, T3, T4, T5> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5());
    }

    /**
     * Returns a {@link Consumer} of {@link Tuple6} that wraps a consumer of the component values of the tuple
     *
     * @param consumer the component value consumer
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @param <T6>     the type of the sixth value
     * @return the wrapper consumer
     */
    public static <T1, T2, T3, T4, T5, T6> Consumer<Tuple6<T1, T2, T3, T4, T5, T6>> consumer(Consumer6<T1, T2, T3, T4, T5, T6> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6());
    }

    /**
     * Returns a {@link Consumer} of {@link Tuple7} that wraps a consumer of the component values of the tuple
     *
     * @param consumer the component value consumer
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @param <T6>     the type of the sixth value
     * @param <T7>     the type of the seventh value
     * @return the wrapper consumer
     */
    public static <T1, T2, T3, T4, T5, T6, T7> Consumer<Tuple7<T1, T2, T3, T4, T5, T6, T7>> consumer(Consumer7<T1, T2, T3, T4, T5, T6, T7> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6(), tuple.getT7());
    }

    /**
     * Returns a {@link Consumer} of {@link Tuple8} that wraps a consumer of the component values of the tuple
     *
     * @param consumer the component value consumer
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @param <T6>     the type of the sixth value
     * @param <T7>     the type of the seventh value
     * @param <T8>     the type of the eighth value
     * @return the wrapper consumer
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Consumer<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> consumer(Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6(), tuple.getT7(), tuple.getT8());
    }

    /**
     * Returns a {@link Function} of {@link Tuple2} that wraps a function of the component values of the tuple
     *
     * @param function the component value function
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <R>      the type of the result of the function
     * @return the wrapper function
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> function(Function2<T1, T2, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2());
    }

    /**
     * Returns a {@link Function} of {@link Tuple3} that wraps a function of the component values of the tuple
     *
     * @param function the component value function
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <R>      the type of the result of the function
     * @return the wrapper function
     */
    public static <T1, T2, T3, R> Function<Tuple3<T1, T2, T3>, R> function(Function3<T1, T2, T3, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2(), tuple.getT3());
    }

    /**
     * Returns a {@link Function} of {@link Tuple4} that wraps a function of the component values of the tuple
     *
     * @param function the component value function
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <R>      the type of the result of the function
     * @return the wrapper function
     */
    public static <T1, T2, T3, T4, R> Function<Tuple4<T1, T2, T3, T4>, R> function(Function4<T1, T2, T3, T4, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4());
    }

    /**
     * Returns a {@link Function} of {@link Tuple5} that wraps a function of the component values of the tuple
     *
     * @param function the component value function
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @param <R>      the type of the result of the function
     * @return the wrapper function
     */
    public static <T1, T2, T3, T4, T5, R> Function<Tuple5<T1, T2, T3, T4, T5>, R> function(Function5<T1, T2, T3, T4, T5, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5());
    }

    /**
     * Returns a {@link Function} of {@link Tuple6} that wraps a function of the component values of the tuple
     *
     * @param function the component value function
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @param <T6>     the type of the sixth value
     * @param <R>      the type of the result of the function
     * @return the wrapper function
     */
    public static <T1, T2, T3, T4, T5, T6, R> Function<Tuple6<T1, T2, T3, T4, T5, T6>, R> function(Function6<T1, T2, T3, T4, T5, T6, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6());
    }

    /**
     * Returns a {@link Function} of {@link Tuple7} that wraps a function of the component values of the tuple
     *
     * @param function the component value function
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @param <T6>     the type of the sixth value
     * @param <T7>     the type of the seventh value
     * @param <R>      the type of the result of the function
     * @return the wrapper function
     */
    public static <T1, T2, T3, T4, T5, T6, T7, R> Function<Tuple7<T1, T2, T3, T4, T5, T6, T7>, R> function(Function7<T1, T2, T3, T4, T5, T6, T7, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6(), tuple.getT7());
    }

    /**
     * Returns a {@link Function} of {@link Tuple8} that wraps a function of the component values of the tuple
     *
     * @param function the component value function
     * @param <T1>     the type of the first value
     * @param <T2>     the type of the second value
     * @param <T3>     the type of the third value
     * @param <T4>     the type of the fourth value
     * @param <T5>     the type of the fifth value
     * @param <T6>     the type of the sixth value
     * @param <T7>     the type of the seventh value
     * @param <T8>     the type of the eighth value
     * @param <R>      the type of the result of the function
     * @return the wrapper function
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Function<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>, R> function(Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> function) {
        return tuple -> function.apply(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6(), tuple.getT7(), tuple.getT8());
    }

    /**
     * Returns a {@link Predicate} of {@link Tuple2} that wraps a predicate of the component values of the tuple
     *
     * @param predicate the component value predicate
     * @param <T1>      the type of the first value
     * @param <T2>      the type of the second value
     * @return the wrapper predicate
     */
    public static <T1, T2> Predicate<Tuple2<T1, T2>> predicate(Predicate2<T1, T2> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2());
    }

    /**
     * Returns a {@link Predicate} of {@link Tuple3} that wraps a predicate of the component values of the tuple
     *
     * @param predicate the component value predicate
     * @param <T1>      the type of the first value
     * @param <T2>      the type of the second value
     * @param <T3>      the type of the third value
     * @return the wrapper predicate
     */
    public static <T1, T2, T3> Predicate<Tuple3<T1, T2, T3>> predicate(Predicate3<T1, T2, T3> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2(), tuple.getT3());
    }

    /**
     * Returns a {@link Predicate} of {@link Tuple4} that wraps a predicate of the component values of the tuple
     *
     * @param predicate the component value predicate
     * @param <T1>      the type of the first value
     * @param <T2>      the type of the second value
     * @param <T3>      the type of the third value
     * @param <T4>      the type of the fourth value
     * @return the wrapper predicate
     */
    public static <T1, T2, T3, T4> Predicate<Tuple4<T1, T2, T3, T4>> predicate(Predicate4<T1, T2, T3, T4> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4());
    }

    /**
     * Returns a {@link Predicate} of {@link Tuple5} that wraps a predicate of the component values of the tuple
     *
     * @param predicate the component value predicate
     * @param <T1>      the type of the first value
     * @param <T2>      the type of the second value
     * @param <T3>      the type of the third value
     * @param <T4>      the type of the fourth value
     * @param <T5>      the type of the fifth value
     * @return the wrapper predicate
     */
    public static <T1, T2, T3, T4, T5> Predicate<Tuple5<T1, T2, T3, T4, T5>> predicate(Predicate5<T1, T2, T3, T4, T5> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5());
    }

    /**
     * Returns a {@link Predicate} of {@link Tuple6} that wraps a predicate of the component values of the tuple
     *
     * @param predicate the component value predicate
     * @param <T1>      the type of the first value
     * @param <T2>      the type of the second value
     * @param <T3>      the type of the third value
     * @param <T4>      the type of the fourth value
     * @param <T5>      the type of the fifth value
     * @param <T6>      the type of the sixth value
     * @return the wrapper predicate
     */
    public static <T1, T2, T3, T4, T5, T6> Predicate<Tuple6<T1, T2, T3, T4, T5, T6>> predicate(Predicate6<T1, T2, T3, T4, T5, T6> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6());
    }

    /**
     * Returns a {@link Predicate} of {@link Tuple7} that wraps a predicate of the component values of the tuple
     *
     * @param predicate the component value predicate
     * @param <T1>      the type of the first value
     * @param <T2>      the type of the second value
     * @param <T3>      the type of the third value
     * @param <T4>      the type of the fourth value
     * @param <T5>      the type of the fifth value
     * @param <T6>      the type of the sixth value
     * @param <T7>      the type of the seventh value
     * @return the wrapper predicate
     */
    public static <T1, T2, T3, T4, T5, T6, T7> Predicate<Tuple7<T1, T2, T3, T4, T5, T6, T7>> predicate(Predicate7<T1, T2, T3, T4, T5, T6, T7> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6(), tuple.getT7());
    }

    /**
     * Returns a {@link Predicate} of {@link Tuple8} that wraps a predicate of the component values of the tuple
     *
     * @param predicate the component value predicate
     * @param <T1>      the type of the first value
     * @param <T2>      the type of the second value
     * @param <T3>      the type of the third value
     * @param <T4>      the type of the fourth value
     * @param <T5>      the type of the fifth value
     * @param <T6>      the type of the sixth value
     * @param <T7>      the type of the seventh value
     * @param <T8>      the type of the eighth value
     * @return the wrapper predicate
     */
    public static <T1, T2, T3, T4, T5, T6, T7, T8> Predicate<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> predicate(Predicate8<T1, T2, T3, T4, T5, T6, T7, T8> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5(), tuple.getT6(), tuple.getT7(), tuple.getT8());
    }

}
