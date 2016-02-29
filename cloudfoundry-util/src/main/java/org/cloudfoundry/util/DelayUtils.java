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

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.rx.Fluxion;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

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
     * @param minDuration the minimum duration of the delay
     * @param maxDuration the maximum duration of the delay
     * @param timeUnit    the time unit for the minimum and maximum durations
     * @param maxRetries  the maximum number of retries
     * @return a delayed {@link Mono}
     */
    public static Function<Fluxion<Long>, Publisher<?>> exponentialBackOff(long minDuration, long maxDuration, TimeUnit timeUnit, int maxRetries) {
        return count -> getTest(count)
            .zipWith(getRetryCounter(maxRetries), 1)
            .flatMap(function((itemCount, retryCount) -> getDelay(minDuration, maxDuration, timeUnit, retryCount)));
    }

    private static long calculateDuration(long minDuration, long maxDuration, Integer retryCount) {
        long candidateDuration = minDuration * (long) Math.pow(2, retryCount);
        return Math.min(candidateDuration, maxDuration);
    }

    private static Publisher<?> getDelay(long minDuration, long maxDuration, TimeUnit timeUnit, Integer retryCount) {
        long duration = calculateDuration(minDuration, maxDuration, retryCount);

        return Mono
            .delay(duration, timeUnit)
            .doOnSubscribe(subscription -> LOGGER.debug("Delaying {} {}", duration, timeUnit.toString().toLowerCase()));
    }

    private static Fluxion<Integer> getRetryCounter(int maxRetries) {
        return Fluxion
            .range(0, maxRetries)
            .concatWith(Fluxion.error(new IllegalStateException("Exceeded maximum number of retries"), true));
    }

    private static Fluxion<Long> getTest(Fluxion<Long> count) {
        return count
            .takeWhile(count1 -> count1 == 0);
    }

}
