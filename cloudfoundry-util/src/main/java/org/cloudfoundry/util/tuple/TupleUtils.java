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

package org.cloudfoundry.util.tuple;

import reactor.core.tuple.Tuple2;
import reactor.core.tuple.Tuple3;
import reactor.core.tuple.Tuple4;
import reactor.core.tuple.Tuple5;
import reactor.core.tuple.Tuple6;
import reactor.core.tuple.Tuple7;
import reactor.core.tuple.Tuple8;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class TupleUtils {

    private TupleUtils() {
    }

    public static <T1, T2> Consumer<Tuple2<T1, T2>> consumer(final Consumer2<T1, T2> consumer) {
        return tuple -> consumer.accept(tuple.t1, tuple.t2);
    }

    public static <T1, T2, T3> Consumer<Tuple3<T1, T2, T3>> consumer(final Consumer3<T1, T2, T3> consumer) {
        return tuple -> consumer.accept(tuple.t1, tuple.t2, tuple.t3);
    }

    public static <T1, T2, T3, T4> Consumer<Tuple4<T1, T2, T3, T4>> consumer(final Consumer4<T1, T2, T3, T4> consumer) {
        return tuple -> consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4);
    }

    public static <T1, T2, T3, T4, T5> Consumer<Tuple5<T1, T2, T3, T4, T5>> consumer(final Consumer5<T1, T2, T3, T4, T5> consumer) {
        return tuple -> consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5);
    }

    public static <T1, T2, T3, T4, T5, T6> Consumer<Tuple6<T1, T2, T3, T4, T5, T6>> consumer(final Consumer6<T1, T2, T3, T4, T5, T6> consumer) {
        return tuple -> consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Consumer<Tuple7<T1, T2, T3, T4, T5, T6, T7>> consumer(final Consumer7<T1, T2, T3, T4, T5, T6, T7> consumer) {
        return tuple -> consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Consumer<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> consumer(final Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> consumer) {
        return tuple -> consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7, tuple.t8);
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> function(final Function2<T1, T2, R> function) {
        return tuple -> function.apply(tuple.t1, tuple.t2);
    }

    public static <T1, T2, T3, R> Function<Tuple3<T1, T2, T3>, R> function(final Function3<T1, T2, T3, R> function) {
        return tuple -> function.apply(tuple.t1, tuple.t2, tuple.t3);
    }

    public static <T1, T2, T3, T4, R> Function<Tuple4<T1, T2, T3, T4>, R> function(final Function4<T1, T2, T3, T4, R> function) {
        return tuple -> function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4);
    }

    public static <T1, T2, T3, T4, T5, R> Function<Tuple5<T1, T2, T3, T4, T5>, R> function(final Function5<T1, T2, T3, T4, T5, R> function) {
        return tuple -> function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5);
    }

    public static <T1, T2, T3, T4, T5, T6, R> Function<Tuple6<T1, T2, T3, T4, T5, T6>, R> function(final Function6<T1, T2, T3, T4, T5, T6, R> function) {
        return tuple -> function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> Function<Tuple7<T1, T2, T3, T4, T5, T6, T7>, R> function(final Function7<T1, T2, T3, T4, T5, T6, T7, R> function) {
        return tuple -> function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Function<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>, R> function(final Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> function) {
        return tuple -> function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7, tuple.t8);
    }

    public static <T1, T2> Predicate<Tuple2<T1, T2>> predicate(final Predicate2<T1, T2> predicate) {
        return tuple -> predicate.test(tuple.t1, tuple.t2);
    }

    public static <T1, T2, T3> Predicate<Tuple3<T1, T2, T3>> predicate(final Predicate3<T1, T2, T3> predicate) {
        return tuple -> predicate.test(tuple.t1, tuple.t2, tuple.t3);
    }

    public static <T1, T2, T3, T4> Predicate<Tuple4<T1, T2, T3, T4>> predicate(final Predicate4<T1, T2, T3, T4> predicate) {
        return tuple -> predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4);
    }

    public static <T1, T2, T3, T4, T5> Predicate<Tuple5<T1, T2, T3, T4, T5>> predicate(final Predicate5<T1, T2, T3, T4, T5> predicate) {
        return tuple -> predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5);
    }

    public static <T1, T2, T3, T4, T5, T6> Predicate<Tuple6<T1, T2, T3, T4, T5, T6>> predicate(final Predicate6<T1, T2, T3, T4, T5, T6> predicate) {
        return tuple -> predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Predicate<Tuple7<T1, T2, T3, T4, T5, T6, T7>> predicate(final Predicate7<T1, T2, T3, T4, T5, T6, T7> predicate) {
        return tuple -> predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Predicate<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> predicate(final Predicate8<T1, T2, T3, T4, T5, T6, T7, T8> predicate) {
        return tuple -> predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7, tuple.t8);
    }

}
