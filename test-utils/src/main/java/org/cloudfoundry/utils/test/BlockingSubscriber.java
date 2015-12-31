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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BlockingSubscriber<T> implements Subscriber<T> {

    private final CountDownLatch latch = new CountDownLatch(1);

    public final void await(long timeout, TimeUnit unit) throws InterruptedException {
        if (!this.latch.await(timeout, unit)) {
            throw new IllegalStateException("Subscriber timed out");
        }
    }

    @Override
    public final void onComplete() {
        doOnComplete();
        this.latch.countDown();
    }

    @Override
    public final void onError(Throwable t) {
        doOnError(t);
        this.latch.countDown();
    }

    @Override
    public final void onNext(T t) {
        doOnNext(t);
    }

    @Override
    public final void onSubscribe(Subscription s) {
        doOnSubscribe(s);
        s.request(Long.MAX_VALUE);
    }

    protected void doOnComplete() {
    }

    protected void doOnError(Throwable t) {
    }

    protected void doOnNext(T t) {
    }

    protected void doOnSubscribe(Subscription s) {
    }

}
