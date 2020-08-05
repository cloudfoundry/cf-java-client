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

import org.atteo.evo.inflector.English;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utilities for delaying progress
 */
public final class DelayUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.delay");

    private DelayUtils() {
    }

    /**
     * Implements an exponential backoff delay for use with {@link Mono#repeatWhenEmpty(Function)}
     *
     * @param minimum the minimum duration
     * @param maximum the maximum duration
     * @param timeout the maximum amount of time to delay for
     * @return a delayed {@link Publisher}
     */
    public static Function<Flux<Long>, Publisher<?>> exponentialBackOff(Duration minimum, Duration maximum, Duration timeout) {
        Instant finish = Instant.now().plus(timeout);
        return iterations -> getDelay(minimum, maximum, finish, iterations);
    }

    /**
     * Implements an exponential backoff delay for use with {@link Mono#retryWhen(Function)}
     *
     * @param minimum the minimum duration
     * @param maximum the maximum duration
     * @param timeout the maximum amount of time to delay for
     * @return a delayed {@link Publisher}
     */
    public static Function<Flux<Throwable>, Publisher<?>> exponentialBackOffError(Duration minimum, Duration maximum, Duration timeout) {
        Instant finish = Instant.now().plus(timeout);
        return errors -> getDelay(minimum, maximum, finish, errors.zipWith(Flux.range(0, Integer.MAX_VALUE), (error, iteration) -> iteration.longValue()));
    }

    /**
     * Implements an fixed delay for use with {@link Mono#repeatWhenEmpty(Function)}
     *
     * @param duration the duration of the delay
     * @return a delayed {@link Publisher}
     */
    public static Function<Flux<Long>, Publisher<?>> fixed(Duration duration) {
        return iterations -> iterations
            .flatMap(iteration -> Mono
                .delay(duration)
                .doOnSubscribe(logDelay(duration)), 1);
    }

    /**
     * Implements an instant (no delay) for use with {@link Mono#repeatWhenEmpty(Function)}
     *
     * @return an instant (no delay) {@link Publisher}
     */
    public static Function<Flux<Long>, Publisher<?>> instant() {
        return iterations -> iterations
            .flatMap(iteration -> Mono
                .just(0L)
                .doOnSubscribe(logDelay(Duration.ZERO)), 1);
    }

    private static Duration calculateDuration(Duration minimum, Duration maximum, Long iteration) {
        Duration candidate = minimum.multipliedBy((long) Math.pow(2, iteration));
        return min(candidate, maximum);
    }

    private static Flux<?> getDelay(Duration minimum, Duration maximum, Instant finish, Flux<Long> iterations) {
        return iterations
            .map(iteration -> calculateDuration(minimum, maximum, iteration))
            .concatMap(delay -> {
                if (Instant.now().isAfter(finish)) {
                    return Mono.error(new DelayTimeoutException());
                }

                return Mono
                    .delay(delay)
                    .doOnSubscribe(logDelay(delay));
            });
    }

    private static Consumer<Subscription> logDelay(Duration delay) {
        return subscription -> {
            int seconds = (int) delay.getSeconds();
            if (seconds > 0) {
                LOGGER.debug("Delaying {} {}", seconds, English.plural("second", seconds));
                return;
            }

            int milliseconds = (int) delay.toMillis();
            LOGGER.debug("Delaying {} {}", milliseconds, English.plural("millisecond", milliseconds));
        };
    }

    private static Duration min(Duration a, Duration b) {
        return (a.compareTo(b) <= 0) ? a : b;
    }

}
