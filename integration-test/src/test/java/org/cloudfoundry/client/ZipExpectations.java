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

package org.cloudfoundry.client;

import reactor.core.Exceptions;
import reactor.test.subscriber.ScriptedSubscriber;
import reactor.util.function.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ZipExpectations {

    public static ScriptedSubscriber<Tuple2<byte[], byte[]>> zipEquality() {
        Function<Tuple2<byte[], byte[]>, Optional<String>> assertion = function((expected, actual) -> {
            List<Entry> expectedEntries = entries(expected);
            List<Entry> actualEntries = entries(expected);

            if (expectedEntries.size() != actualEntries.size()) {
                return Optional.of(String.format("expected entries: %d; actual entries: %d", expectedEntries.size(), actualEntries.size()));
            }

            Iterator<Entry> expectedIterator = expectedEntries.iterator();
            Iterator<Entry> actualIterator = actualEntries.iterator();

            while (expectedIterator.hasNext()) {
                Optional<String> message = expectedIterator.next().equality(actualIterator.next());
                if (message.isPresent()) {
                    return message;
                }
            }

            return Optional.empty();
        });

        return ScriptedSubscriber.<Tuple2<byte[], byte[]>>create()
            .expectValueWith(tuple -> !assertion.apply(tuple).isPresent(),
                tuple -> assertion.apply(tuple).orElseThrow(() -> new IllegalStateException("Cannot generate assertion message for matching zip")))
            .expectComplete();
    }

    private static byte[] content(InputStream in, int length) throws IOException {
        byte[] content = new byte[length];
        int read = in.read(content, 0, length);
        if (read != length) {
            throw new IllegalStateException(String.format("expected read: %d; actual read: %d", length, read));
        }
        return content;
    }

    private static List<Entry> entries(byte[] bytes) {
        List<Entry> entries = new ArrayList<>();

        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                entries.add(new Entry(entry.getCompressedSize(), content(in, (int) entry.getSize()), entry.getCrc(), entry.isDirectory(), entry.getName(), entry.getSize()));
                in.closeEntry();
            }
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        Collections.sort(entries);
        return entries;
    }

    private static final class Entry implements Comparable<Entry> {

        private final long compressedSize;

        private final byte[] contents;

        private final long crc;

        private final boolean directory;

        private final String name;

        private final long size;

        private Entry(long compressedSize, byte[] contents, long crc, boolean directory, String name, long size) {
            this.compressedSize = compressedSize;
            this.contents = contents;
            this.crc = crc;
            this.directory = directory;
            this.name = name;
            this.size = size;
        }

        @Override
        public int compareTo(Entry o) {
            return this.name.compareTo(o.name);
        }

        private Optional<String> equality(Entry o) {
            if (this.compressedSize != o.compressedSize) {
                return Optional.of(String.format("%s; expected compressed size: %d; actual compressed size: %d", this.name, this.compressedSize, o.compressedSize));
            }

            if (!Arrays.equals(this.contents, o.contents)) {
                return Optional.of(String.format("%s; expected and actual contents do not match", this.name));
            }

            if (this.crc != o.crc) {
                return Optional.of(String.format("%s; expected crc: %d; actual crc: %d", this.name, this.crc, o.crc));
            }

            if (this.directory != o.directory) {
                return Optional.of(String.format("%s; expected is directory: %b; actual is directory: %b", this.name, this.directory, o.directory));
            }

            if (!this.name.equals(o.name)) {
                return Optional.of(String.format("%s; expected name: %s; actual name: %s", this.name, this.name, o.name));
            }

            if (this.size != o.size) {
                return Optional.of(String.format("%s; expected size: %d; actual size: %d", this.name, this.size, o.size));
            }

            return Optional.empty();
        }

    }
}
