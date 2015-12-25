/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.utils.test;

import org.junit.Assert;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.error.Exceptions;
import reactor.core.error.ReactorFatalException;
import reactor.fn.Consumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public final class TestSubscriber<T> implements Subscriber<T> {

    private final Queue<Consumer<T>> expectations = new LinkedList<>();

    private final CountDownLatch latch = new CountDownLatch(1);

    private Consumer<? super Throwable> errorExpectation;

    public TestSubscriber<T> assertEquals(final T expected) {
        assertThat(new Consumer<T>() {

            @Override
            public void accept(T actual) {
                Assert.assertEquals(expected, actual);
            }

        });

        return this;
    }

    public TestSubscriber<T> assertError(final Class<? extends Throwable> expected) {
        this.errorExpectation = new Consumer<Throwable>() {

            @Override
            public void accept(Throwable actual) {
                Assert.assertTrue(String.format("Unexpected error %s", actual),
                        expected.isAssignableFrom(actual.getClass()));
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

        if (t instanceof AssertionError) {
            throw ReactorFatalException.create(t);
        }

        if (this.errorExpectation == null) {
            fail(String.format("Unexpected error %s", t));
        }

        this.errorExpectation.accept(t);
        this.errorExpectation = null;

        this.latch.countDown();
    }

    @Override
    public void onNext(T t) {
        Consumer<T> expectation = this.expectations.poll();

        if (expectation == null) {
            fail(String.format("Unexpected item %s", t));
        }

        expectation.accept(t);
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    public void verify(long timeout, TimeUnit unit) throws InterruptedException {
        this.latch.await(timeout, unit);
        verifyExpectations();
    }

    private void verifyExpectations() {
        if (!this.expectations.isEmpty()) {
            fail(String.format("Unexpected completion. %d expectations not met.", this.expectations.size()));
        }

        if (this.errorExpectation != null) {
            fail("Unexpected completion. Error expectation not met.");
        }
    }

}
