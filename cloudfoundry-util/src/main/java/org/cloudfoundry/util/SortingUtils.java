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

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
     * @param source     a {@link Flux} providing elements whose order may be scrambled
     * @param comparator a {@link Comparator} to use when sorting the elements within the window
     * @param timespan   the duration of the 'temporal locality'
     * @param <T>        The type of the elements to be sorted
     * @return a {@link Flux} providing the sorted elements
     */
    public static <T> Flux<T> timespan(Flux<T> source, Comparator<T> comparator, Duration timespan) {
        SortingAccumulator<T> accumulator = new SortingAccumulator<>(comparator, timespan);

        return source
            .timestamp()
            .window(timespan)
            .scan(accumulator, SortingAccumulator::accumulate)
            .concatMap(SortingAccumulator::getReleased)
            .concatWith(accumulator.drain());
    }

    private static final class SortingAccumulator<T> {

        private final Queue<Tuple2<Long, T>> queue;

        private final Flux<T> released;

        private SortingAccumulator(Comparator<T> comparator, Duration timespan) {
            this.queue = new SynchronousQueue<>(new PriorityQueue<>((o1, o2) -> comparator.compare(o1.t2, o2.t2)));
            this.released = Flux.fromIterable(() -> new TemporallyLimitedIterator<>(this.queue, timespan));
        }

        private SortingAccumulator<T> accumulate(Flux<Tuple2<Long, T>> source) {
            source.subscribe(this.queue::add);
            return this;
        }

        private Flux<T> drain() {
            return Flux.fromIterable(() -> new TemporallyLimitedIterator<>(this.queue, Duration.ZERO));
        }

        private Flux<T> getReleased() {
            return this.released;
        }

    }

    private static final class SynchronousQueue<T> implements Queue<T> {

        private final Object monitor = new Object();

        private final Queue<T> queue;

        private SynchronousQueue(Queue<T> queue) {
            this.queue = queue;
        }

        @Override
        public boolean add(T t) {
            synchronized (this.monitor) {
                return this.queue.add(t);
            }
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            synchronized (this.monitor) {
                return this.queue.addAll(c);
            }
        }

        @Override
        public void clear() {
            synchronized (this.monitor) {
                this.queue.clear();
            }
        }

        @Override
        public boolean contains(Object o) {
            synchronized (this.monitor) {
                return this.queue.contains(o);
            }
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (this.monitor) {
                return this.queue.containsAll(c);
            }
        }

        @Override
        public T element() {
            synchronized (this.monitor) {
                return this.queue.element();
            }
        }

        @Override
        public boolean equals(Object o) {
            synchronized (this.monitor) {
                return this.queue.equals(o);
            }
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            synchronized (this.monitor) {
                this.queue.forEach(action);
            }
        }

        @Override
        public int hashCode() {
            synchronized (this.monitor) {
                return this.queue.hashCode();
            }
        }

        @Override
        public boolean isEmpty() {
            synchronized (this.monitor) {
                return this.queue.isEmpty();
            }
        }

        @Override
        public Iterator<T> iterator() {
            synchronized (this.monitor) {
                return this.queue.iterator();
            }
        }

        @Override
        public boolean offer(T t) {
            synchronized (this.monitor) {
                return this.queue.offer(t);
            }
        }

        @Override
        public Stream<T> parallelStream() {
            synchronized (this.monitor) {
                return this.queue.parallelStream();
            }
        }

        @Override
        public T peek() {
            synchronized (this.monitor) {
                return this.queue.peek();
            }
        }

        @Override
        public T poll() {
            synchronized (this.monitor) {
                return this.queue.poll();
            }
        }

        @Override
        public T remove() {
            synchronized (this.monitor) {
                return this.queue.remove();
            }
        }

        @Override
        public boolean remove(Object o) {
            synchronized (this.monitor) {
                return this.queue.remove(o);
            }
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            synchronized (this.monitor) {
                return this.queue.removeAll(c);
            }
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            synchronized (this.monitor) {
                return this.queue.removeIf(filter);
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            synchronized (this.monitor) {
                return this.queue.retainAll(c);
            }
        }

        @Override
        public int size() {
            synchronized (this.monitor) {
                return this.queue.size();
            }
        }

        @Override
        public Spliterator<T> spliterator() {
            synchronized (this.monitor) {
                return this.queue.spliterator();
            }
        }

        @Override
        public Stream<T> stream() {
            synchronized (this.monitor) {
                return this.queue.stream();
            }
        }

        @Override
        public Object[] toArray() {
            synchronized (this.monitor) {
                return this.queue.toArray();
            }
        }

        @Override
        public <T1> T1[] toArray(T1[] a) {
            synchronized (this.monitor) {
                return this.queue.toArray(a);
            }
        }
    }

    private static final class TemporallyLimitedIterator<T> implements Iterator<T> {

        private final Queue<Tuple2<Long, T>> queue;

        private final Duration timespan;

        private TemporallyLimitedIterator(Queue<Tuple2<Long, T>> queue, Duration timespan) {
            this.queue = queue;
            this.timespan = timespan;
        }

        @Override
        public boolean hasNext() {
            Tuple2<Long, T> candidate = this.queue.peek();
            return candidate != null && Instant.ofEpochMilli(candidate.t1).isBefore(Instant.now().minus(this.timespan));
        }

        @Override
        public T next() {
            return this.queue.remove().t2;
        }

    }

}
