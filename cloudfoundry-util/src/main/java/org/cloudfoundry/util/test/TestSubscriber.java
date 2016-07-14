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

package org.cloudfoundry.util.test;

import org.atteo.evo.inflector.English;
import org.junit.Assert;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.Exceptions;
import reactor.util.function.Tuple2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.junit.Assert.fail;

public final class TestSubscriber<T> implements Subscriber<T> {

    private final Queue<T> actuals = new LinkedList<>();

    private final Queue<Consumer<T>> expectations = new LinkedList<>();

    private final CountDownLatch latch = new CountDownLatch(1);

    private final AtomicBoolean subscribed = new AtomicBoolean(false);

    private Integer countExpectation;

    private volatile Throwable errorActual;

    private Consumer<? super Throwable> errorExpectation;

    private Consumer<Tuple2<Long, Long>> performanceCallback;

    private long startTime;

    private Subscription subscription;

    public TestSubscriber<T> assertCount(Integer expected) {
        this.countExpectation = expected;
        return this;
    }

    public TestSubscriber<T> assertEquals(T expected) {
        assertThat(actual -> Assert.assertEquals(expected, actual));
        return this;
    }

    public TestSubscriber<T> assertError(Class<? extends Throwable> expected, String format, Object... args) {
        this.errorExpectation = actual -> {
            StringWriter writer = new StringWriter();
            actual.printStackTrace(new PrintWriter(writer));

            Assert.assertEquals(String.format("Unexpected error %s", writer.toString()), expected, actual.getClass());
            if (format != null) {
                Assert.assertEquals("Unexpected exception text", String.format(format, args), actual.getMessage());
            }
        };

        return this;
    }

    public TestSubscriber<T> assertErrorMatch(Class<? extends Throwable> expected, String patternString) {
        this.errorExpectation = actual -> {
            StringWriter writer = new StringWriter();
            actual.printStackTrace(new PrintWriter(writer));

            Assert.assertEquals(String.format("Unexpected error %s", writer.toString()), expected, actual.getClass());
            if (patternString != null) {
                Matcher matcher = Pattern.compile(patternString).matcher(actual.getMessage());
                if (!matcher.matches()) {
                    fail(String.format("Exception text \"%s\" fails to match pattern \"%s\"", actual.getMessage(), patternString));
                }
            }
        };

        return this;
    }

    public TestSubscriber<T> assertThat(Consumer<T> expectation) {
        this.expectations.add(expectation);
        return this;
    }

    @Override
    public void onComplete() {
        this.latch.countDown();
    }

    @Override
    public void onError(Throwable t) {
        Exceptions.throwIfFatal(t);
        this.errorActual = t;
        this.latch.countDown();
    }

    @Override
    public void onNext(T t) {
        this.actuals.add(t);
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscribed.set(true);
        this.subscription = s;
        this.startTime = System.currentTimeMillis();

        s.request(Long.MAX_VALUE);
    }

    public TestSubscriber<T> setPerformanceCallback(Consumer<Tuple2<Long, Long>> performanceCallback) {
        this.performanceCallback = performanceCallback;
        return this;
    }

    public TestSubscriber<T> setPerformanceLoggerName(Supplier<String> name) {
        return setPerformanceCallback(consumer((startTime, finishTime) -> {
            Logger logger = LoggerFactory.getLogger(String.format("cloudfoundry-client.performance.%s", name.get()));

            if (logger.isDebugEnabled()) {
                logger.debug("{} ms", finishTime - startTime);
            }
        }));
    }

    public void verify(Duration duration) throws InterruptedException {
        if (!this.subscribed.get()) {
            throw new IllegalStateException("Subscriber not initialized");
        }

        if (!this.latch.await(duration.toMillis(), MILLISECONDS)) {
            throw new IllegalStateException("Subscriber timed out");
        }

        if (this.performanceCallback != null) {
            this.performanceCallback.accept(Tuple2.of(this.startTime, System.currentTimeMillis()));
        }

        verifyError();
        verifyCount();
        verifyItems();
    }

    private void verifyCount() {
        if (this.countExpectation != null) {
            Assert.assertEquals("Item count expectation not met", this.countExpectation, (Integer) this.actuals.size());
        }
    }

    private void verifyError() {
        if (this.errorActual != null) {
            if (this.errorExpectation == null) {
                throw new AssertionError("Unexpected error", this.errorActual);
            }

            this.errorExpectation.accept(this.errorActual);
            this.errorExpectation = null;
        }

        if (this.errorExpectation != null) {
            fail("Unexpected completion. Error expectation not met.");
        }
    }

    private void verifyItems() {
        for (T actual : this.actuals) {
            Consumer<T> expectation = this.expectations.poll();

            if (expectation != null) {
                expectation.accept(actual);
            } else if (this.countExpectation == null) {
                fail(String.format("Unexpected item %s", actual));
            }
        }

        if (!this.expectations.isEmpty()) {
            int count = this.expectations.size();
            fail(String.format("Unexpected completion. %d %s not met.", count, English.plural("expectation", count)));
        }
    }

}
