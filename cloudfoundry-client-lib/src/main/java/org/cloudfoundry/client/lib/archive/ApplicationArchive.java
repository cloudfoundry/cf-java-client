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

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that represents an application archive (for example a WAR file) that can be uploaded to Cloud Foundry.
 * Archives consist of a {@link #getFilename() filename} and one or more {@link #getEntries() entries}.
 *
 * @author Phillip Webb
 */
public interface ApplicationArchive {

    /**
     * Returns the filename of the archive (excluding any path).
     *
     * @return the filename (for example myproject.war)
     */
    String getFilename();

    /**
     * Returns {@link Entry entries} that the archive contains.
     *
     * @return a collection of entries.
     */
    Iterable<Entry> getEntries();

    /**
     * A single entry contained within an {@link ApplicationArchive}. Entries are used to represent both files and
     * directories.
     */
    public static interface Entry {

        /**
         * Returns <tt>true</tt> if the entry represents a directory.
         *
         * @return if the entry is a directory.
         */
        boolean isDirectory();

        /**
         * Returns the name of entry including a path. The <tt>'/'</tt> character should be used as a path separator.
         * The name should never start with <tt>'/'</tt>.
         *
         * @return the name
         */
        String getName();

        /**
         * Returns the size of entry or <tt>0</tt> if the entry is a {@link #isDirectory() directory}.
         *
         * @return the size
         */
        long getSize();

        /**
         * Returns a SHA1 digest over the {@link #getInputStream() contents} of the entry or <tt>null</tt> if the entry
         * is a {@link #isDirectory() directory}.
         *
         * @return the SHA1 digest
         */
        byte[] getSha1Digest();

        /**
         * Returns the content of the entry or <tt>null</tt> if the entry is a {@link #isDirectory() directory}. The
         * caller is responsible for closing the stream.
         *
         * @return the file contents
         * @throws IOException
         */
        InputStream getInputStream() throws IOException;
    }
}
