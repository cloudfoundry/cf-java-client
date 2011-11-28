
package org.cloudfoundry.client.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.util.Assert;

/**
 * InputStream that dynamically creates ZIP contents as the stream is read without consuming too much memory. Zip
 * {@link Entry entries} should be provided on {@link #DynamicZipInputStream(Iterable) construction}.
 *
 * @author Phillip Webb
 */
public class DynamicZipInputStream extends DynamicInputStream {

    private static final int BUFFER_SIZE = 4096;

    private static InputStream EMPTY_STREAM = new InputStream() {

        @Override
        public int read() throws IOException {
            return -1;
        }
    };

    /**
     * The underlying ZIP stream.
     */
    private ZipOutputStream zipStream;

    /**
     * Entries to be written.
     */
    private Iterator<Entry> entries;

    /**
     * The current entry {@link InputStream}.
     */
    private InputStream entryStream = EMPTY_STREAM;

    /**
     * Buffer for reading stream contents.
     */
    private byte[] buffer = new byte[BUFFER_SIZE];

    /**
     * Create a new {@link DynamicZipInputStream} instance.
     *
     * @param entries the zip entries that should be written to the stream
     */
    public DynamicZipInputStream(Iterable<Entry> entries) {
        Assert.notNull(entries, "Entries must not be null");
        this.zipStream = new ZipOutputStream(getOutputStream());
        this.entries = entries.iterator();
    }

    @Override
    protected boolean writeMoreData() throws IOException {

        // Write data from the current stream if possible
        int count = entryStream.read(buffer);
        if (count != -1) {
            zipStream.write(buffer, 0, count);
            return true;
        }

        // Close any open entry
        if (entryStream != EMPTY_STREAM) {
            zipStream.closeEntry();
            entryStream.close();
            entryStream = EMPTY_STREAM;
        }

        // Move to the next entry if there is one (no need to write data as returning true causes another call)
        if (entries.hasNext()) {
            Entry entry = entries.next();
            zipStream.putNextEntry(new ZipEntry(entry.getName()));
            entryStream = entry.getInputStream();
            if(entryStream == null) {
                entryStream = EMPTY_STREAM;
            }
            return true;
        }

        // No more entries, close and flush the stream
        zipStream.flush();
        zipStream.close();
        return false;
    }

    @Override
    public void close() throws IOException {
        super.close();
        zipStream.close();
    }

    /**
     * Represents a single entry from a ZIP files.
     */
    public static interface Entry {

        /**
         * Returns the name of the entry complete with path, equivalent to {@link ZipEntry#getName()}.
         *
         * @return the name of the entry
         */
        String getName();

        /**
         * Opens a new stream that can be used to read the contents of the entry. The steam will be closed by the
         * caller.
         *
         * @return the entry input stream
         * @throws IOException
         */
        InputStream getInputStream() throws IOException;
    }
}
