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

package org.cloudfoundry.reactor.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSource;
import reactor.core.subscriber.SubscriberBarrier;
import reactor.core.util.Exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public final class JsonCodec {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> Function<Flux<InputStream>, Flux<T>> decode(Class<T> type) {
        return decode(OBJECT_MAPPER, type);
    }

    public static <T> Function<Flux<InputStream>, Flux<T>> decode(ObjectMapper objectMapper, Class<T> type) {
        return source -> new JsonDecoder<T>(source, objectMapper, type);
    }

    private static final class JsonDecoder<T> extends FluxSource<InputStream, T> {

        private final ObjectMapper objectMapper;

        private final Class<T> type;

        private JsonDecoder(Publisher<? extends InputStream> source, ObjectMapper objectMapper, Class<T> type) {
            super(source);
            this.objectMapper = objectMapper;
            this.type = type;
        }

        @Override
        public void subscribe(Subscriber<? super T> subscriber) {
            this.source.subscribe(new JsonSubscriber<>(subscriber, this.objectMapper, this.type));
        }
    }

    private static final class JsonSubscriber<T> extends SubscriberBarrier<InputStream, T> {

        private final ObjectMapper objectMapper;

        private final Class<T> type;

        private boolean done = false;

        private JsonSubscriber(Subscriber<? super T> subscriber, ObjectMapper objectMapper, Class<T> type) {
            super(subscriber);
            this.objectMapper = objectMapper;
            this.type = type;
        }

        @Override
        protected void doComplete() {
            if (this.done) {
                return;
            }

            this.done = true;
            this.subscriber.onComplete();
        }

        @Override
        protected void doError(Throwable throwable) {
            if (this.done) {
                Exceptions.onErrorDropped(throwable);
                return;
            }

            this.done = true;
            this.subscriber.onError(throwable);
        }

        @Override
        protected void doNext(InputStream inputStream) {
            if (this.done) {
                Exceptions.onNextDropped(inputStream);
                return;
            }

            try {
                this.subscriber.onNext(this.objectMapper.readValue(inputStream, this.type));
            } catch (IOException e) {
                onError(e);
            }
        }

    }

}
