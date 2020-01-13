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

package org.cloudfoundry.client;

import reactor.core.Exceptions;
import reactor.util.function.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;

public final class ZipExpectations {

    public static Consumer<Tuple2<byte[], byte[]>> zipEquality() {
        return consumer((expected, actual) -> {
            List<Entry> expectedEntries = entries(expected);
            List<Entry> actualEntries = entries(expected);

            assertThat(expectedEntries).hasSameSizeAs(actualEntries);

            Iterator<Entry> expectedIterator = expectedEntries.iterator();

            for (Entry actualEntry : actualEntries) {
                assertThat(actualEntry).isEqualToComparingOnlyGivenFields(expectedIterator.next(), "compressedSize", "contents", "crc", "directory", "name", "size");
            }
        });
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

    }
}
