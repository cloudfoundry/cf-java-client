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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCounted;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSource;
import reactor.core.subscriber.SubscriberBarrier;
import reactor.core.util.Exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

final class JsonCodec {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static <T> Function<Flux<ByteBuf>, Mono<T>> decode(Class<T> type) {
        return decode(OBJECT_MAPPER, type);
    }

    static <T> Function<Flux<ByteBuf>, Mono<T>> decode(ObjectMapper objectMapper, Class<T> type) {
        return source -> new JsonDecoder<>(source, objectMapper, type);
    }

    static <T> Function<Mono<T>, Mono<ByteBuf>> encode() {
        return encode(OBJECT_MAPPER);
    }

    static <T> Function<Mono<T>, Mono<ByteBuf>> encode(ObjectMapper objectMapper) {
        return source -> new JsonEncoder<>(source, objectMapper);
    }

    private static final class JsonDecoder<T> extends MonoSource<ByteBuf, T> {

        private final ObjectMapper objectMapper;

        private final Class<T> type;

        private JsonDecoder(Publisher<? extends ByteBuf> source, ObjectMapper objectMapper, Class<T> type) {
            super(source);
            this.objectMapper = objectMapper;
            this.type = type;
        }

        @Override
        public void subscribe(Subscriber<? super T> subscriber) {
            this.source.subscribe(new JsonDecoderSubscriber<>(subscriber, this.objectMapper, this.type));
        }
    }

    private static final class JsonDecoderSubscriber<T> extends SubscriberBarrier<ByteBuf, T> {

        private final List<ByteBuf> byteBufs = new ArrayList<>();

        private final ObjectMapper objectMapper;

        private final Class<T> type;

        private boolean done = false;

        private JsonDecoderSubscriber(Subscriber<? super T> subscriber, ObjectMapper objectMapper, Class<T> type) {
            super(subscriber);
            this.objectMapper = objectMapper;
            this.type = type;
        }

        @Override
        protected void doComplete() {
            if (this.done) {
                return;
            }

            if (!this.byteBufs.isEmpty()) {
                try {
                    this.subscriber.onNext(this.objectMapper.readValue(getInputStream(this.byteBufs), this.type));
                } catch (IOException e) {
                    onError(e);
                }
            }

            this.done = true;
            this.byteBufs.forEach(ReferenceCounted::release);
            this.subscriber.onComplete();
        }

        @Override
        protected void doError(Throwable throwable) {
            if (this.done) {
                Exceptions.onErrorDropped(throwable);
                return;
            }

            this.done = true;
            this.byteBufs.forEach(ReferenceCounted::release);
            this.subscriber.onError(throwable);
        }

        @Override
        protected void doNext(ByteBuf byteBuf) {
            if (this.done) {
                Exceptions.onNextDropped(byteBuf);
                return;
            }

            this.byteBufs.add(byteBuf);
        }

        private static InputStream getInputStream(List<ByteBuf> byteBufs) {
            ByteBuf composite = Unpooled.wrappedBuffer(byteBufs.toArray(new ByteBuf[byteBufs.size()]));
            return new ByteBufInputStream(composite);
        }

    }

    private static final class JsonEncoder<T> extends MonoSource<T, ByteBuf> {

        private final ObjectMapper objectMapper;

        private JsonEncoder(Publisher<? extends T> source, ObjectMapper objectMapper) {
            super(source);
            this.objectMapper = objectMapper;
        }

        @Override
        public void subscribe(Subscriber<? super ByteBuf> subscriber) {
            this.source.subscribe(new JsonEncoderSubscriber<>(subscriber, this.objectMapper));
        }
    }

    private static final class JsonEncoderSubscriber<T> extends SubscriberBarrier<T, ByteBuf> {

        private final ObjectMapper objectMapper;

        private boolean done = false;

        private JsonEncoderSubscriber(Subscriber<? super ByteBuf> subscriber, ObjectMapper objectMapper) {
            super(subscriber);
            this.objectMapper = objectMapper;
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
        protected void doNext(T t) {
            if (this.done) {
                Exceptions.onNextDropped(t);
                return;
            }

            if (!this.objectMapper.canSerialize(t.getClass())) {
                return;
            }

            try {
                this.subscriber.onNext(Unpooled.wrappedBuffer(this.objectMapper.writeValueAsBytes(t)));
            } catch (IOException e) {
                onError(e);
            }
        }

    }

}
