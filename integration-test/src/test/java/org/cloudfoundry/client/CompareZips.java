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

import reactor.core.tuple.Tuple2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

final class CompareZips {

    /***
     * Asserts two InputStreams equivalent as Zip streams.
     *
     * @param is1 The first InputStream
     * @param is2 The second InputStream
     */
    public static void zipAssertEquivalent(InputStream is1, InputStream is2) {
        try {
            zipAssertEquivalentMaps(
                zipAddInputStreamToMap(new HashMap<>(), "", new ZipInputStream(is1)),
                zipAddInputStreamToMap(new HashMap<>(), "", new ZipInputStream(is2))
            );
        } catch (IOException ioe) {
            fail(String.format("IOException (%s) in Zip Stream comparison", ioe));
        }
    }

    private static byte[] getBufferFrom(InputStream is) throws IOException {
        final int BUFSIZE = 65536;
        byte[] buffer = new byte[BUFSIZE];
        int totalBytesRead = 0;
        int bytesRead = is.read(buffer, 0, BUFSIZE);
        if (bytesRead <= 0) return null;
        totalBytesRead += bytesRead;
        while (bytesRead > 0 && totalBytesRead < BUFSIZE) {
            bytesRead = is.read(buffer, totalBytesRead, BUFSIZE - totalBytesRead);
            totalBytesRead += bytesRead;
        }
        if (totalBytesRead == BUFSIZE) return buffer;
        byte[] result = new byte[totalBytesRead];
        System.arraycopy(buffer, 0, result, 0, totalBytesRead);
        return result;
    }

    private static byte[] getContentFrom(InputStream is) throws IOException {
        List<byte[]> bytesList = new ArrayList<>();
        byte[] contentBytes = getBufferFrom(is);
        int length = 0;
        while (contentBytes != null) {
            bytesList.add(contentBytes);
            length += contentBytes.length;
            contentBytes = getBufferFrom(is);
        }
        byte[] result = new byte[length];
        int bytesCopied = 0;
        for (byte[] bytes : bytesList) {
            System.arraycopy(bytes, 0, result, bytesCopied, bytes.length);
            bytesCopied += bytes.length;
        }
        return result;
    }

    private static Map<String, Tuple2<ZipEntry, byte[]>> zipAddEntryToMap(Map<String, Tuple2<ZipEntry, byte[]>> zMap, ZipEntry zipEntry, String rootPath, InputStream entryInputStream)
        throws IOException {
        if (zipEntry.isDirectory()) {
            zMap.put(rootPath + zipEntry.getName(), Tuple2.of(zipEntry, new byte[0]));
        } else if (zipSuffix(zipEntry.getName().toLowerCase())) {
            zMap = zipAddInputStreamToMap(zMap, rootPath + zipEntry.getName() + "/", new ZipInputStream(entryInputStream));
        } else {
            zMap.put(rootPath + zipEntry.getName(), Tuple2.of(zipEntry, getContentFrom(entryInputStream)));
        }
        return zMap;
    }

    private static Map<String, Tuple2<ZipEntry, byte[]>> zipAddInputStreamToMap(Map<String, Tuple2<ZipEntry, byte[]>> zMap, String rootPath, ZipInputStream zis) throws IOException {
        ZipEntry entry = zis.getNextEntry();
        entry.getMethod();
        while (entry != null) {
            zMap = zipAddEntryToMap(zMap, entry, rootPath, zis);
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
        return zMap;
    }

    private static void zipAssertEquivalentMaps(Map<String, Tuple2<ZipEntry, byte[]>> m1, Map<String, Tuple2<ZipEntry, byte[]>> m2) {
        Set<String> names1 = m1.keySet();
        Set<String> names2 = m2.keySet();

        Set<String> allNames = new HashSet<>();
        allNames.addAll(names1);
        allNames.addAll(names2);

        for (String name : allNames) {
            if (names1.contains(name) && (!names2.contains(name))) {
                fail(String.format("Zip entry '%s' has been removed", name));
            } else if (names2.contains(name) && (!names1.contains(name))) {
                fail(String.format("Zip entry '%s' has been added", name));
            } else if (names1.contains(name) && (names2.contains(name))) {
                Tuple2<ZipEntry, byte[]> entry1 = m1.get(name);
                Tuple2<ZipEntry, byte[]> entry2 = m2.get(name);
                zipEntriesMatch(entry1, entry2);
            } else {
                throw new IllegalStateException("unexpected state of zip entry map");
            }
        }
    }

    private static void zipEntriesMatch(Tuple2<ZipEntry, byte[]> ze1, Tuple2<ZipEntry, byte[]> ze2) {
        assertEquals("Entry names differ", ze1.t1.getName(), ze2.t1.getName());
        assertEquals("Not both directories", ze1.t1.isDirectory(), ze2.t1.isDirectory());
        assertEquals("Different sizes", ze1.t1.getSize(), ze2.t1.getSize());
        assertEquals("Different compressed sizes", ze1.t1.getCompressedSize(), ze2.t1.getCompressedSize());
        // assertEquals("Different timestamps", ze1.getTime(), ze2.getTime());  // CF changes the timestamps of the entries
        assertEquals("Different cyclic redundancy check", ze1.t1.getCrc(), ze2.t1.getCrc());
        assertArrayEquals("Entry contents differ", ze1.t2, ze2.t2);
    }

    private static boolean zipSuffix(String lowercaseName) {
        return lowercaseName.endsWith(".zip")
            || lowercaseName.endsWith(".ear")
            || lowercaseName.endsWith(".war")
            || lowercaseName.endsWith(".rar")
            || lowercaseName.endsWith(".jar");
    }
}
