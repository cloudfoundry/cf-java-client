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

package org.cloudfoundry.utils.tuple;

import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.tuple.Tuple1;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;
import reactor.fn.tuple.Tuple4;
import reactor.fn.tuple.Tuple5;
import reactor.fn.tuple.Tuple6;
import reactor.fn.tuple.Tuple7;
import reactor.fn.tuple.Tuple8;

public final class TupleUtils {

    private TupleUtils() {
    }

    public static <T1> Consumer<Tuple1<T1>> consumer(final Consumer1<T1> consumer) {
        return new Consumer<Tuple1<T1>>() {

            @Override
            public void accept(Tuple1<T1> tuple) {
                consumer.accept(tuple.t1);
            }

        };
    }

    public static <T1, T2> Consumer<Tuple2<T1, T2>> consumer(final Consumer2<T1, T2> consumer) {
        return new Consumer<Tuple2<T1, T2>>() {

            @Override
            public void accept(Tuple2<T1, T2> tuple) {
                consumer.accept(tuple.t1, tuple.t2);
            }

        };
    }

    public static <T1, T2, T3> Consumer<Tuple3<T1, T2, T3>> consumer(final Consumer3<T1, T2, T3> consumer) {
        return new Consumer<Tuple3<T1, T2, T3>>() {

            @Override
            public void accept(Tuple3<T1, T2, T3> tuple) {
                consumer.accept(tuple.t1, tuple.t2, tuple.t3);
            }

        };
    }

    public static <T1, T2, T3, T4> Consumer<Tuple4<T1, T2, T3, T4>> consumer(final Consumer4<T1, T2, T3, T4> consumer) {
        return new Consumer<Tuple4<T1, T2, T3, T4>>() {

            @Override
            public void accept(Tuple4<T1, T2, T3, T4> tuple) {
                consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4);
            }

        };
    }

    public static <T1, T2, T3, T4, T5> Consumer<Tuple5<T1, T2, T3, T4, T5>> consumer(final Consumer5<T1, T2, T3, T4, T5> consumer) {
        return new Consumer<Tuple5<T1, T2, T3, T4, T5>>() {

            @Override
            public void accept(Tuple5<T1, T2, T3, T4, T5> tuple) {
                consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6> Consumer<Tuple6<T1, T2, T3, T4, T5, T6>> consumer(final Consumer6<T1, T2, T3, T4, T5, T6> consumer) {
        return new Consumer<Tuple6<T1, T2, T3, T4, T5, T6>>() {

            @Override
            public void accept(Tuple6<T1, T2, T3, T4, T5, T6> tuple) {
                consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Consumer<Tuple7<T1, T2, T3, T4, T5, T6, T7>> consumer(final Consumer7<T1, T2, T3, T4, T5, T6, T7> consumer) {
        return new Consumer<Tuple7<T1, T2, T3, T4, T5, T6, T7>>() {

            @Override
            public void accept(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple) {
                consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Consumer<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> consumer(final Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> consumer) {
        return new Consumer<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>() {

            @Override
            public void accept(Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple) {
                consumer.accept(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7, tuple.t8);
            }

        };
    }

    public static <T1, R> Function<Tuple1<T1>, R> function(final Function1<T1, R> function) {
        return new Function<Tuple1<T1>, R>() {

            @Override
            public R apply(Tuple1<T1> tuple) {
                return function.apply(tuple.t1);
            }

        };
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> function(final Function2<T1, T2, R> function) {
        return new Function<Tuple2<T1, T2>, R>() {

            @Override
            public R apply(Tuple2<T1, T2> tuple) {
                return function.apply(tuple.t1, tuple.t2);
            }

        };
    }

    public static <T1, T2, T3, R> Function<Tuple3<T1, T2, T3>, R> function(final Function3<T1, T2, T3, R> function) {
        return new Function<Tuple3<T1, T2, T3>, R>() {

            @Override
            public R apply(Tuple3<T1, T2, T3> tuple) {
                return function.apply(tuple.t1, tuple.t2, tuple.t3);
            }

        };
    }

    public static <T1, T2, T3, T4, R> Function<Tuple4<T1, T2, T3, T4>, R> function(final Function4<T1, T2, T3, T4, R> function) {
        return new Function<Tuple4<T1, T2, T3, T4>, R>() {

            @Override
            public R apply(Tuple4<T1, T2, T3, T4> tuple) {
                return function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, R> Function<Tuple5<T1, T2, T3, T4, T5>, R> function(final Function5<T1, T2, T3, T4, T5, R> function) {
        return new Function<Tuple5<T1, T2, T3, T4, T5>, R>() {

            @Override
            public R apply(Tuple5<T1, T2, T3, T4, T5> tuple) {
                return function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6, R> Function<Tuple6<T1, T2, T3, T4, T5, T6>, R> function(final Function6<T1, T2, T3, T4, T5, T6, R> function) {
        return new Function<Tuple6<T1, T2, T3, T4, T5, T6>, R>() {

            @Override
            public R apply(Tuple6<T1, T2, T3, T4, T5, T6> tuple) {
                return function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> Function<Tuple7<T1, T2, T3, T4, T5, T6, T7>, R> function(final Function7<T1, T2, T3, T4, T5, T6, T7, R> function) {
        return new Function<Tuple7<T1, T2, T3, T4, T5, T6, T7>, R>() {

            @Override
            public R apply(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple) {
                return function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Function<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>, R> function(final Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> function) {
        return new Function<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>, R>() {

            @Override
            public R apply(Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple) {
                return function.apply(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7, tuple.t8);
            }

        };
    }

    public static <T1> Predicate<Tuple1<T1>> predicate(final Predicate1<T1> predicate) {
        return new Predicate<Tuple1<T1>>() {

            @Override
            public boolean test(Tuple1<T1> tuple) {
                return predicate.test(tuple.t1);
            }

        };
    }

    public static <T1, T2> Predicate<Tuple2<T1, T2>> predicate(final Predicate2<T1, T2> predicate) {
        return new Predicate<Tuple2<T1, T2>>() {

            @Override
            public boolean test(Tuple2<T1, T2> tuple) {
                return predicate.test(tuple.t1, tuple.t2);
            }

        };
    }

    public static <T1, T2, T3> Predicate<Tuple3<T1, T2, T3>> predicate(final Predicate3<T1, T2, T3> predicate) {
        return new Predicate<Tuple3<T1, T2, T3>>() {

            @Override
            public boolean test(Tuple3<T1, T2, T3> tuple) {
                return predicate.test(tuple.t1, tuple.t2, tuple.t3);
            }

        };
    }

    public static <T1, T2, T3, T4> Predicate<Tuple4<T1, T2, T3, T4>> predicate(final Predicate4<T1, T2, T3, T4> predicate) {
        return new Predicate<Tuple4<T1, T2, T3, T4>>() {

            @Override
            public boolean test(Tuple4<T1, T2, T3, T4> tuple) {
                return predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4);
            }

        };
    }

    public static <T1, T2, T3, T4, T5> Predicate<Tuple5<T1, T2, T3, T4, T5>> predicate(final Predicate5<T1, T2, T3, T4, T5> predicate) {
        return new Predicate<Tuple5<T1, T2, T3, T4, T5>>() {

            @Override
            public boolean test(Tuple5<T1, T2, T3, T4, T5> tuple) {
                return predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6> Predicate<Tuple6<T1, T2, T3, T4, T5, T6>> predicate(final Predicate6<T1, T2, T3, T4, T5, T6> predicate) {
        return new Predicate<Tuple6<T1, T2, T3, T4, T5, T6>>() {

            @Override
            public boolean test(Tuple6<T1, T2, T3, T4, T5, T6> tuple) {
                return predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Predicate<Tuple7<T1, T2, T3, T4, T5, T6, T7>> predicate(final Predicate7<T1, T2, T3, T4, T5, T6, T7> predicate) {
        return new Predicate<Tuple7<T1, T2, T3, T4, T5, T6, T7>>() {

            @Override
            public boolean test(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple) {
                return predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7);
            }

        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Predicate<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> predicate(final Predicate8<T1, T2, T3, T4, T5, T6, T7, T8> predicate) {
        return new Predicate<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>() {

            @Override
            public boolean test(Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple) {
                return predicate.test(tuple.t1, tuple.t2, tuple.t3, tuple.t4, tuple.t5, tuple.t6, tuple.t7, tuple.t8);
            }

        };
    }

}
