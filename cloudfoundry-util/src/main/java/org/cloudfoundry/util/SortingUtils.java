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

package org.cloudfoundry.util;

import reactor.core.Disposable;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;

/**
 * Utilities for sorting
 */
public final class SortingUtils {

    private SortingUtils() {
    }

    /**
     * Sorts the elements of a {@link Flux} within a sliding time window.  This sorter should be used when element order may be scrambled, but that scrambling has a certain 'temporal locality' to it.
     * This assumption means that sorting can be limited to elements that arrive temporally close to one another without risking a latecomer being sorted incorrectly.
     *
     * @param comparator a {@link Comparator} to use when sorting the elements within the window
     * @param timespan   the duration of the 'temporal locality'
     * @param <T>        The type of the elements to be sorted
     * @return a {@link Flux} providing the sorted elements
     */
    public static <T> Function<Flux<T>, Flux<T>> timespan(Comparator<T> comparator, Duration timespan) {
        return source -> {
            Queue<Tuple2<Long, T>> accumulator = new PriorityQueue<>((o1, o2) -> comparator.compare(o1.getT2(), o2.getT2()));

            Object monitor = new Object();

            DirectProcessor<Void> d = DirectProcessor.create();

            Disposable disposable = source
                .timestamp()
                .subscribe(item -> {
                    synchronized (monitor) {
                        accumulator.add(item);
                    }
                }, d::onError, d::onComplete);

            return Flux
                .interval(timespan)
                .takeUntilOther(d)
                .flatMap(n -> getItems(accumulator, monitor, timespan), null, () -> getItems(accumulator, monitor, Duration.ZERO))
                .doOnCancel(disposable::dispose);
        };
    }

    private static <T> Flux<T> getItems(Queue<Tuple2<Long, T>> accumulator, Object monitor, Duration timespan) {
        List<T> items = new ArrayList<>();

        synchronized (monitor) {
            while (isBefore(accumulator.peek(), timespan)) {
                items.add(accumulator.remove().getT2());
            }
        }

        return Flux.fromIterable(items);
    }

    private static <T> boolean isBefore(Tuple2<Long, T> candidate, Duration timespan) {
        return candidate != null && (Duration.ZERO == timespan || Instant.ofEpochMilli(candidate.getT1()).isBefore(Instant.now().minus(timespan)));
    }

}
