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

import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public final class DelayUtilsTest {

    @SuppressWarnings("unchecked")
    @Test
    public void exponentialBackOff() {
        StepVerifier.withVirtualTime(() -> (Publisher<Long>) DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(5))
            .apply(Flux.just(1L, 2L, 3L)))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(2)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(4)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(8)))
            .expectNext(0L)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void exponentialBackOffError() {
        StepVerifier.withVirtualTime(() -> (Publisher<Long>) DelayUtils.exponentialBackOffError(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(5))
            .apply(Flux.just(new RuntimeException(), new RuntimeException(), new RuntimeException())))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(2)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(4)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(8)))
            .expectNext(0L)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void exponentialBackOffErrorMaximum() {
        StepVerifier.withVirtualTime(() -> (Publisher<Long>) DelayUtils.exponentialBackOffError(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(5))
            .apply(Flux.just(new RuntimeException(), new RuntimeException(), new RuntimeException())))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void exponentialBackOffErrorTimeout() {
        StepVerifier.create(DelayUtils.exponentialBackOffError(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(100))
            .apply(Mono.delay(Duration.ofMillis(200))
                .thenMany(Flux.just(new RuntimeException()))))
            .expectError(DelayTimeoutException.class)
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void exponentialBackOffMaximum() {
        StepVerifier.withVirtualTime(() -> (Publisher<Long>) DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(5))
            .apply(Flux.just(1L, 2L, 3L)))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void exponentialBackOffTimeout() {
        StepVerifier.create(DelayUtils.exponentialBackOff(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(100))
            .apply(Mono.delay(Duration.ofMillis(200))
                .thenMany(Flux.just(1L))))
            .expectError(DelayTimeoutException.class)
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void fixed() {
        StepVerifier.withVirtualTime(() -> (Publisher<Long>) DelayUtils.fixed(Duration.ofSeconds(1))
            .apply(Flux.just(1L, 2L, 3L)))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instant() {
        StepVerifier.withVirtualTime(() -> (Publisher<Long>) DelayUtils.instant()
            .apply(Flux.just(1L, 2L, 3L)))
            .expectNext(0L)
            .expectNext(0L)
            .expectNext(0L)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
