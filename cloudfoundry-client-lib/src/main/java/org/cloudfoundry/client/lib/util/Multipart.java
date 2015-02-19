package org.cloudfoundry.client.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Multipart {

	public static final String CRLF = "\r\n";
	public static final String DASHES = "--";

	private final InputStream input;
	private final String boundary;
	private final String eomBoundary;

	private boolean firstPart = true;

	private ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream(1024);
	private byte[] prevHeader;

	public Multipart(InputStream input, String boundary) {
		this.input = input;
		this.boundary = DASHES + boundary + CRLF;
		this.eomBoundary = DASHES + boundary + DASHES;
	}

	public Part nextPart() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		byte[] line = readLine(input);

		while (line.length > 0) {
			if (isBoundaryLine(line)) {
				byte[] header = trimCRLF(readLine(input));

				if (firstPart) {
					// found the first boundary maker, continue to get the first part
					firstPart = false;
					prevHeader = header;
				} else {
					Part part = new Part(trimCRLF(output.toByteArray()), prevHeader);
					prevHeader = header;
					return part;
				}
			} else if (isEomBoundaryLine(line)) {
				drainInput();
				if (firstPart) {
					return null;
				} else {
					return new Part(trimCRLF(output.toByteArray()), prevHeader);
				}
			} else {
				try {
					output.write(line);
				} catch (IOException e) {
					rethrow(e);
				}
			}

			line = readLine(input);
		}

		byte[] content = output.toByteArray();
		if (content.length > 0) {
			return new Part(content, prevHeader);
		} else {
			return null;
		}
	}

	private byte[] readLine(InputStream input) {
		byteBuffer.reset();

		try {
			int value;
			while ((value = input.read()) != -1) {
				byteBuffer.write(value);

				if (value == '\n') {
					break;
				}
			}
		} catch (IOException e) {
			return rethrow(e);
		}

		return byteBuffer.toByteArray();
	}

	private void drainInput() {
		try {
			input.skip(input.available());
		} catch (IOException e) {
			rethrow(e);
		}
	}

	private byte[] trimCRLF(byte[] bytes) {
		if (endsWithCRLF(bytes)) {
			return Arrays.copyOf(bytes, bytes.length - 2);
		}
		return bytes;
	}

	private boolean endsWithCRLF(byte[] bytes) {
		int length = bytes.length;

		if (length < 2) {
			return false;
		}

		return bytes[length - 2] == '\r' && bytes[length - 1] == '\n';
	}

	private boolean isBoundaryLine(byte[] line) {
		return new String(line).equals(boundary);
	}

	private boolean isEomBoundaryLine(byte[] line) {
		return new String(line).startsWith(eomBoundary);
	}

	private byte[] rethrow(IOException e) {
		throw new RuntimeException("Error reading multipart message", e);
	}

	public class Part {
		private byte[] content;
		private String header;

		Part(byte[] content, byte[] header) {
			this.content = content;
			this.header = new String(header);
		}

		public byte[] getContent() {
			return content;
		}

		public String getHeader() {
			return header;
		}
	}
}
