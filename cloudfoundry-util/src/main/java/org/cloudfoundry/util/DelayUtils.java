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

import org.atteo.evo.inflector.English;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

/**
 * Utilities for delaying progress
 */
public final class DelayUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.delay");

    private DelayUtils() {
    }

    /**
     * Implements an exponential backoff delay
     *
     * @param minimum the minimum duration
     * @param maximum the maximum duration
     * @return a delayed {@link Publisher}
     */
    public static Function<Flux<Long>, Publisher<?>> exponentialBackOff(Duration minimum, Duration maximum, Duration timeout) {
        Instant finish = Instant.now().plus(timeout);
        return iterations -> getDelay(minimum, maximum, finish, iterations);
    }

    private static Duration calculateDuration(Duration minimum, Duration maximum, Long iteration) {
        Duration candidate = minimum.multipliedBy((long) Math.pow(2, iteration));
        return min(candidate, maximum);
    }

    private static Long checkForTimeout(Instant finish, Long iteration) {
        if (Instant.now().isAfter(finish)) {
            throw new IllegalStateException("Timer expired");
        }

        return iteration;
    }

    private static Publisher<?> getDelay(Duration minimum, Duration maximum, Instant finish, Flux<Long> iterations) {
        return iterations
            .map(iteration -> checkForTimeout(finish, iteration))
            .map(iteration -> calculateDuration(minimum, maximum, iteration))
            .flatMap(delay -> Mono
                .delay(delay)
                .doOnSubscribe(subscription -> {
                    int seconds = (int) delay.getSeconds();
                    LOGGER.debug("Delaying {} {}", seconds, English.plural("second", seconds));
                }));
    }

    private static Duration min(Duration a, Duration b) {
        return (a.compareTo(b) <= 0) ? a : b;
    }

}
