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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Dynamically creates {@link byte} arrays and caches them, reusing them once they have been released.
 * <p>
 * The maximum number of byte arrays is unbounded
 * <p>
 * The default time-to-live for unused byte arrays is one minute
 */
public final class ByteArrayPool {

    private static final AtomicLong EVICTOR_COUNTER = new AtomicLong();

    private static final ThreadFactory EVICTOR_FACTORY = r -> {
        Thread t = new Thread(r, "byte-buffer-evictor-" + EVICTOR_COUNTER.incrementAndGet());
        t.setDaemon(true);
        return t;
    };

    private static final int MIBIBYTE = 1_024 * 1_024;

    private static ByteArrayPool INSTANCE = new ByteArrayPool(MIBIBYTE, Duration.ofMinutes(1));

    private final Queue<ByteArrayExpiry> cache = new ConcurrentLinkedQueue<>();

    private final int capacity;

    private final Duration ttl;

    private ByteArrayPool(int capacity, Duration ttl) {
        this.capacity = capacity;
        this.ttl = ttl;

        Executors.newScheduledThreadPool(1, EVICTOR_FACTORY)
            .scheduleAtFixedRate(this::evict, ttl.toMillis(), ttl.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Executes a {@link Consumer} providing a pooled {@code byte} array
     *
     * @param consumer the {@link Consumer} of the {@link byte} array
     */
    public static void withByteArray(Consumer<byte[]> consumer) {
        INSTANCE.doWithByteArray(consumer);
    }

    private void doWithByteArray(Consumer<byte[]> consumer) {
        byte[] byteArray = Optional.ofNullable(this.cache.poll())
            .map(ByteArrayExpiry::getByteArray)
            .orElseGet(() -> new byte[this.capacity]);

        try {
            consumer.accept(byteArray);
        } finally {
            this.cache.offer(new ByteArrayExpiry(byteArray, Instant.now().plus(this.ttl)));
        }
    }

    private void evict() {
        Instant now = Instant.now();

        new ArrayList<>(this.cache).stream()
            .filter(expiry -> expiry.getExpiration().isBefore(now))
            .forEach(this.cache::remove);
    }

    private static class ByteArrayExpiry {

        private final byte[] byteArray;

        private final Instant expiration;

        private ByteArrayExpiry(byte[] byteArray, Instant expiration) {
            this.byteArray = byteArray;
            this.expiration = expiration;
        }

        private byte[] getByteArray() {
            return this.byteArray;
        }

        private Instant getExpiration() {
            return this.expiration;
        }

    }


}
