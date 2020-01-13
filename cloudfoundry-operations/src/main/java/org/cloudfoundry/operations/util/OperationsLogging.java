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

package org.cloudfoundry.operations.util;

import org.cloudfoundry.util.TimeUtils;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

public final class OperationsLogging {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.operations");

    private OperationsLogging() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Publisher<U>, U> Function<T, T> log(String message) {
        if (!LOGGER.isDebugEnabled()) {
            return f -> f;
        }

        AtomicLong startTimeHolder = new AtomicLong();

        Consumer<Subscription> start = subscription -> {
            startTimeHolder.set(System.currentTimeMillis());
            LOGGER.debug("START  {}", message);
        };

        Consumer<SignalType> finish = signalType -> {
            String elapsed = TimeUtils.asTime(System.currentTimeMillis() - startTimeHolder.get());
            LOGGER.debug("FINISH {} ({}/{})", message, signalType, elapsed);
        };

        return f -> {
            if (f instanceof Mono) {
                return (T) ((Mono<U>) f)
                    .doOnSubscribe(start)
                    .doFinally(finish);
            }
            if (f instanceof Flux) {
                return (T) ((Flux<U>) f)
                    .doOnSubscribe(start)
                    .doFinally(finish);
            } else {
                return f;
            }
        };
    }

}
