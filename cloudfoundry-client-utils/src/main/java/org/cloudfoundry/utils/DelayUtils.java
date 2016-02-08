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

import org.cloudfoundry.utils.tuple.Function2;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.rx.Stream;

import java.util.concurrent.TimeUnit;

import static org.cloudfoundry.utils.tuple.TupleUtils.function;

/**
 * A utility class for delay strategies
 */
public final class DelayUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.delay");

    /**
     * Implements an exponential backoff delay
     *
     * @param minDuration the minimum duration of the delay
     * @param maxDuration the maximum duration of the delay
     * @param timeUnit    the time unit for the minimum and maximum durations
     * @param maxRetries  the maximum number of retries
     * @return a delayed {@link Mono}
     */
    public static Function<Stream<Long>, Publisher<?>> exponentialBackOff(final long minDuration, final long maxDuration, final TimeUnit timeUnit, final int maxRetries) {
        return new Function<Stream<Long>, Publisher<?>>() {

            @Override
            public Publisher<?> apply(Stream<Long> count) {
                return count
                    .takeWhile(new Predicate<Long>() {

                        @Override
                        public boolean test(Long count) {
                            return count == 0;
                        }

                    })
                    .zipWith(getRetryCounter(maxRetries))
                    .flatMap(function(new Function2<Long, Integer, Publisher<?>>() {

                        @Override
                        public Publisher<?> apply(Long itemCount, Integer retryCount) {
                            return getDelay(minDuration, maxDuration, timeUnit, retryCount);
                        }

                    }));
            }

        };
    }

    /**
     * Implements a fixed delay if the incoming count is 0
     *
     * @param duration the duration of the delay
     * @param timeUnit the time unit of the duration
     * @return a delayed {@link Mono}
     */
    public static Function<Stream<Long>, Publisher<?>> fixed(final long duration, final TimeUnit timeUnit) {
        return new Function<Stream<Long>, Publisher<?>>() {

            @Override
            public Publisher<?> apply(Stream<Long> volumes) {
                return volumes
                    .takeWhile(new Predicate<Long>() {

                        @Override
                        public boolean test(Long count) {
                            return count == 0;
                        }

                    })
                    .flatMap(new Function<Long, Publisher<Long>>() {

                        @Override
                        public Publisher<Long> apply(Long count) {
                            return Mono
                                .delay(duration, timeUnit)
                                .doOnSubscribe(new Consumer<Subscription>() {

                                    @Override
                                    public void accept(Subscription subscription) {
                                        LOGGER.debug("Delaying {} {}", duration, timeUnit);
                                    }

                                });
                        }

                    });
            }

        };
    }

    static long calculateDuration(long minDuration, long maxDuration, Integer retryCount) {
        long candidateDuration = minDuration * (long) Math.pow(2, retryCount);
        return Math.min(candidateDuration, maxDuration);
    }

    static Publisher<?> getDelay(long minDuration, long maxDuration, final TimeUnit timeUnit, Integer retryCount) {
        final long duration = calculateDuration(minDuration, maxDuration, retryCount);

        return Mono
            .delay(duration, timeUnit)
            .doOnSubscribe(new Consumer<Subscription>() {

                @Override
                public void accept(Subscription subscription) {
                    LOGGER.debug("Delaying {} {}", duration, timeUnit);
                }

            });
    }

    static Stream<Integer> getRetryCounter(int maxRetries) {
        return Stream
            .range(0, maxRetries);
//            .concatWith(Mono.<Integer>error(new IllegalStateException("Exceeded maximum number of retries"))) // TODO: Add back once capacity is honored
//            .capacity(1);
    }

}
