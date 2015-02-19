/*
 * Copyright 2009-2012 the original author or authors.
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

package org.cloudfoundry.client.lib.io;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.Assert;

/**
 * InputStream that dynamically loads data on demand as the stream is read. Subclasses must implement the
 * {@link #writeMoreData()} method.
 *
 * @author Phillip Webb
 */
public abstract class DynamicInputStream extends InputStream {

	private BufferedOutputStream outputStream = new BufferedOutputStream();

	private byte[] singleByte = new byte[1];

	@Override
	public int read() throws IOException {
		int s = read(singleByte);
		if (s == 1) {
			return (int) singleByte[0] & 0xFF;
		}
		return -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		}
		if (off < 0 || len < 0 || len > (b.length - off)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return 0;
		}
		return doRead(b, off, len, true);
	}

	private int doRead(byte[] b, int off, int len, boolean lastWriteWasSuccessful) throws IOException {
		if (outputStream.getAvailable() > 0) {
			return outputStream.read(b, off, len);
		}
		if (!lastWriteWasSuccessful) {
			return -1;
		}
		outputStream.clear();
		boolean writeSuccess = writeMoreData();
		return doRead(b, off, len, writeSuccess);
	}

	/**
	 * Returns the {@link OutputStream} that should be used when {@link #writeMoreData() writing} data. The output
	 * stream instance will not change during the life of the object and so can be used as the source to a a
	 * {@link FilterInputStream}. This is an in-memory stream so care should be taken to not write large amounts of
	 * data.
	 *
	 * @return the output stream
	 * @see #writeMoreData()
	 */
	protected final OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Called when more data should be written to the {@link #getOutputStream() output stream}. This method can be
	 * called many times, implementations should write zero or more bytes to {@link #getOutputStream() output stream} on
	 * each call. Generally it is recommended that not more that 4096 bytes are written in a single call.
	 *
	 * @return <tt>false</tt> when no more data is available to write.
	 * @throws IOException
	 */
	protected abstract boolean writeMoreData() throws IOException;

	/**
	 * Internal buffered {@link OutputStream} implementation.
	 */
	private static class BufferedOutputStream extends ByteArrayOutputStream {

		private int offset;

		public int getAvailable() {
			return count - offset;
		}

		public void clear() {
			this.count = 0;
			this.offset = 0;
		}

		public int read(byte[] b, int off, int len) {
			int length = Math.min(getAvailable(), len);
			Assert.state(length > 0, "No data available in buffer");
			System.arraycopy(this.buf, this.offset, b, off, length);
			offset += length;
			return length;
		}
	}

}
