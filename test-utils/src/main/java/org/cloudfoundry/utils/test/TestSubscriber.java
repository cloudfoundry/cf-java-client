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
import reactor.core.error.Exceptions;
import reactor.fn.Consumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public final class TestSubscriber<T> extends BlockingSubscriber<T> {

    private final Queue<T> actuals = new LinkedList<T>();

    private final Queue<Consumer<T>> expectations = new LinkedList<>();

    private volatile Throwable errorActual;

    private Consumer<? super Throwable> errorExpectation;

    public TestSubscriber() { // TODO: Remove
    }

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
    public void doOnError(Throwable t) {
        Exceptions.throwIfFatal(t);
        this.errorActual = t;
    }

    @Override
    public void doOnNext(T t) {
        this.actuals.add(t);
    }

    public void verify(long timeout, TimeUnit unit) throws InterruptedException {
        await(timeout, unit);
        verifyError();
        verifyItems();
    }

    private void verifyError() {
        if (this.errorActual != null) {
            if (this.errorExpectation == null) {
                fail(String.format("Unexpected error %s", this.errorActual));
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

            if (expectation == null) {
                fail(String.format("Unexpected item %s", actual));
            }

            expectation.accept(actual);
        }

        if (!this.expectations.isEmpty()) {
            fail(String.format("Unexpected completion. %d expectations not met.", this.expectations.size()));
        }
    }

}
