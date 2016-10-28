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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.test.subscriber.ScriptedSubscriber;

import java.time.Duration;

public final class DelayUtilsTest {

    @After
    public void disableVirtualTime() throws Exception {
        VirtualTimeScheduler.reset();
    }

    @Before
    public void enableVirtualTime() {
        VirtualTimeScheduler.enable(true);
    }

    @Test
    public void exponentialBackOff() {
        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(2)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(4)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(8)))
            .expectNext(0L)
            .expectComplete();

        DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(5))
            .apply(Flux.just(1L, 2L, 3L))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

    @Test
    public void exponentialBackOffError() {
        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(2)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(4)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(8)))
            .expectNext(0L)
            .expectComplete();

        DelayUtils.exponentialBackOffError(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(5))
            .apply(Flux.just(new RuntimeException(), new RuntimeException(), new RuntimeException()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

    @Test
    public void exponentialBackOffErrorMaximum() {
        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .expectComplete();

        DelayUtils.exponentialBackOffError(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(5))
            .apply(Flux.just(new RuntimeException(), new RuntimeException(), new RuntimeException()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

    @Test
    public void exponentialBackOffErrorTimeout() {
        VirtualTimeScheduler.reset();

        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .expectError(DelayTimeoutException.class);

        DelayUtils.exponentialBackOffError(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(100))
            .apply(Mono.delay(Duration.ofMillis(200))
                .thenMany(Flux.just(new RuntimeException())))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

    @Test
    public void exponentialBackOffMaximum() {
        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .expectComplete();

        DelayUtils.exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(5))
            .apply(Flux.just(1L, 2L, 3L))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

    @Test
    public void exponentialBackOffTimeout() {
        VirtualTimeScheduler.reset();

        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .expectError(DelayTimeoutException.class);

        DelayUtils.exponentialBackOff(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(100))
            .apply(Mono.delay(Duration.ofMillis(200))
                .thenMany(Flux.just(1L)))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

    @Test
    public void fixed() {
        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(1)))
            .expectNext(0L)
            .expectComplete();

        DelayUtils.fixed(Duration.ofSeconds(1))
            .apply(Flux.just(1L, 2L, 3L))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

    @Test
    public void instant() {
        ScriptedSubscriber<Object> subscriber = ScriptedSubscriber.create()
            .expectNext(0L)
            .expectNext(0L)
            .expectNext(0L)
            .expectComplete();

        DelayUtils.instant()
            .apply(Flux.just(1L, 2L, 3L))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofSeconds(5));
    }

}
