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

package org.cloudfoundry.client.spring;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

public final class TestSubscriber<T> implements Subscriber<T> {

    private final List<Void> onCompleteEvents = new ArrayList<>();

    private final List<Throwable> onErrorEvents = new ArrayList<>();

    private final List<T> onNextEvents = new ArrayList<>();

    private final List<Subscription> onSubscribeEvents = new ArrayList<>();

    public List<Void> getOnCompleteEvents() {
        return this.onCompleteEvents;
    }

    public List<Throwable> getOnErrorEvents() {
        return this.onErrorEvents;
    }

    public List<T> getOnNextEvents() {
        return this.onNextEvents;
    }

    public List<Subscription> getOnSubscribeEvents() {
        return this.onSubscribeEvents;
    }

    @Override
    public void onComplete() {
        this.onCompleteEvents.add(null);
    }

    @Override
    public void onError(Throwable throwable) {
        this.onErrorEvents.add(throwable);
    }

    @Override
    public void onNext(T t) {
        this.onNextEvents.add(t);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.onSubscribeEvents.add(subscription);
    }
}
