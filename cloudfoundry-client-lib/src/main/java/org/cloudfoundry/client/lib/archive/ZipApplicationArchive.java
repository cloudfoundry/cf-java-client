/*
 * Copyright 2009-2011 the original author or authors.
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

package org.cloudfoundry.client.lib.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.util.Assert;

/**
 * Implementation of {@link ApplicationArchive} backed by a {@link ZipFile}.
 *
 * @author Phillip Webb
 */
public class ZipApplicationArchive implements ApplicationArchive {

    private ZipFile zipFile;

    private List<Entry> entries;

    private String fileName;

    /**
     * Create a new {@link ZipApplicationArchive} instance for the given <tt>zipFile</tt>.
     * @param zipFile The underling zip file
     */
    public ZipApplicationArchive(ZipFile zipFile) {
        Assert.notNull(zipFile, "ZipFile must not be null");
        this.zipFile = zipFile;
        this.entries = adaptZipEntries(zipFile);
        this.fileName = new File(zipFile.getName()).getName();
    }

    private List<Entry> adaptZipEntries(ZipFile zipFile) {
        List<Entry> entries = new ArrayList<Entry>();
        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            entries.add(new EntryAdapter(zipEntries.nextElement()));
        }
        return Collections.unmodifiableList(entries);
    }

    public Iterable<Entry> getEntries() {
        return entries;
    }

    public String getFilename() {
        return fileName;
    }

    private class EntryAdapter extends AbstractApplicationArchiveEntry {

        private ZipEntry entry;

        public EntryAdapter(ZipEntry entry) {
            this.entry = entry;
        }

        public boolean isDirectory() {
            return entry.isDirectory();
        }

        public String getName() {
            return entry.getName();
        }

        public long getSize() {
            return entry.getSize();
        }

        public InputStream getInputStream() throws IOException {
            if(isDirectory()) {
                return null;
            }
            return zipFile.getInputStream(entry);
        }
    }
}
