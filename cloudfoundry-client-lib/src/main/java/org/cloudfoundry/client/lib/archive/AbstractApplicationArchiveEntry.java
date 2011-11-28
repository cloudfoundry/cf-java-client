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
import java.security.MessageDigest;

/**
 * Base implementation of {@link ApplicationArchive.Entry} that is reads the input stream to deduce the size and SHA
 * digest.
 *
 * @author Phillip Webb
 */
public abstract class AbstractApplicationArchiveEntry implements ApplicationArchive.Entry {

    protected static final int UNDEFINED_SIZE = -1;

    private static final int BUFFER_SIZE = 4096;

    private long size = UNDEFINED_SIZE;

    private byte[] sha1Digest;

    /**
     * Sets the size that should be returned. If this method is not called the size will be deduced by reading the
     * stream.
     *
     * @param size the size.
     */
    protected void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        if (isDirectory()) {
            return 0;
        }
        if (size == UNDEFINED_SIZE) {
            deduceMissingData();
        }
        return size;
    }

    /**
     * Sets the SHA1 digest that should be returned. If this method is not called the digest will be deduced by reading
     * the stream.
     *
     * @param sha1Digest
     */
    protected void setSha1Digest(byte[] sha1Digest) {
        this.sha1Digest = sha1Digest;
    }

    public byte[] getSha1Digest() {
        if (isDirectory()) {
            return null;
        }
        if (sha1Digest == null) {
            deduceMissingData();
        }
        return sha1Digest;
    }

    private void deduceMissingData() {
        try {
            InputStream inputStream = getInputStream();
            try {
                try {
                    MessageDigest digest = (this.sha1Digest == null ? MessageDigest.getInstance("SHA") : null);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int byteCount = 0;
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteCount += bytesRead;
                        if (digest != null) {
                            digest.update(buffer, 0, bytesRead);
                        }
                    }
                    if (this.size == UNDEFINED_SIZE) {
                        this.size = byteCount;
                    }
                    if (this.sha1Digest == null) {
                        this.sha1Digest = digest.digest();
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }

            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
