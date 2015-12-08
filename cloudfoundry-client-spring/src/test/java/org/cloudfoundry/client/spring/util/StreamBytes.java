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

package org.cloudfoundry.client.spring.util;

import reactor.fn.BiFunction;
import reactor.fn.Function;
import reactor.fn.Supplier;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Some test utilities for reading byte array streamed responses.
 */
public final class StreamBytes {

    private StreamBytes() {
    }

    public static byte[] accumulateBytes(Stream<byte[]> stream) {
        final ArrayList<byte[]> byteArrays = new ArrayList<>();

        int actualLength = stream
                .map(new Function<byte[], Integer>() {
                    @Override
                    public Integer apply(byte[] bytes) {
                        byteArrays.add(bytes);
                        return bytes.length;
                    }
                })
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer l1, Integer l2) {
                        return l1 + l2;
                    }
                })
                .consumeNext().get();

        return Streams.from(byteArrays)
                .map(new Function<byte[], Tuple2<Integer, byte[]>>() {
                    @Override
                    public Tuple2<Integer, byte[]> apply(byte[] bytes) {
                        return Tuple2.of(bytes.length, bytes);
                    }
                })
                .reduce(Tuple2.of(0, new byte[actualLength]),
                        new BiFunction<Tuple2<Integer, byte[]>, Tuple2<Integer, byte[]>, Tuple2<Integer, byte[]>>() {
                            @Override
                            public Tuple2<Integer, byte[]> apply(Tuple2<Integer, byte[]> b1,
                                                                 Tuple2<Integer, byte[]> b2) {
                                System.arraycopy(b2.getT2(), 0, b1.getT2(), b1.getT1(), b2.getT2().length);
                                return Tuple2.of(b1.getT2().length + b2.getT2().length, b1.getT2());
                            }
                        })
                .consumeNext().get().getT2();
    }

    public static byte[] toByteArray(final InputStream inputStream) throws IOException {
        final byte[] byteBuffer = new byte[16384];

        return accumulateBytes(Streams.generate(new Supplier<byte[]>() {
            @Override
            public byte[] get() {
                try {
                    int bytesRead = inputStream.read(byteBuffer, 0, byteBuffer.length);
                    if (bytesRead > 0) {
                        return Arrays.copyOf(byteBuffer, bytesRead);
                    }
                } catch (IOException ignored) {
                }
                return null;
            }
        }));
    }
}
