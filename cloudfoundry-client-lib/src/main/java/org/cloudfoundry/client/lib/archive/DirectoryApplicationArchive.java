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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipFile;

import org.springframework.util.Assert;

/**
 * Implementation of {@link ApplicationArchive} backed by a {@link ZipFile}.
 *
 * @author A.B.Srinivasan
 * @author Phillip Webb
 */
public class DirectoryApplicationArchive implements ApplicationArchive {

    private File directory;

    private List<Entry> entries;

    public DirectoryApplicationArchive(File directory) {
        Assert.notNull(directory, "Directory must not be null");
        Assert.isTrue(directory.isDirectory(), "File must reference a directory");
        this.directory = directory;
        List<Entry> entries = new ArrayList<Entry>();
        collectEntries(entries, directory);
        this.entries = Collections.unmodifiableList(entries);
    }

    private void collectEntries(List<Entry> entries, File directory) {
        for (File child : directory.listFiles()) {
            if(!exclude(child)){
                entries.add(new EntryAdapter(child));
                if (child.isDirectory()) {
                    collectEntries(entries, child);
                }
            }
        }
    }

    private boolean exclude(File file) {
        return file.getName().startsWith(".git") || 
               file.getName().startsWith(".svn") || 
               file.getName().startsWith(".darcs");
    }

    public String getFilename() {
        return directory.getName();
    }

    public Iterable<Entry> getEntries() {
        return entries;
    }

    private class EntryAdapter extends AbstractApplicationArchiveEntry {

        private File file;
        private String name;

        public EntryAdapter(File file) {
            this.file = file;
            this.name = file.getAbsolutePath().substring(directory.getAbsolutePath().length()+1);
            if (isDirectory()) {
                this.name = this.name + File.separatorChar;
            }
        }

        public boolean isDirectory() {
            return file.isDirectory();
        }

        public String getName() {
            return name;
        }

        public InputStream getInputStream() throws IOException {
            if (isDirectory()) {
                return null;
            }
            return new FileInputStream(file);
        }
    }
}
