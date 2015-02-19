package org.cloudfoundry.client.lib.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MultipartTest {

	public static final String CRLF = "\r\n";
	public static final String LINE1 = "line1a" + CRLF + "line1b";
	public static final String LINE2 = "line2a\nline2b";
	public static final String LINE3 = "line3a\nline3b" + CRLF;

	public static final String HEADER1 = "header1";
	public static final String HEADER2 = "header2";
	public static final String HEADER3 = "header3";

	public static final String BOUNDARY = "abcd";

	@Test
	public void parseMessageNoHeaders() {
		String message =
			"--abcd" + CRLF + "" + CRLF +
				LINE1 +
				CRLF + "--abcd" + CRLF + "" + CRLF +
				LINE2 +
				CRLF + "--abcd" + CRLF + "" + CRLF +
				LINE3 +
				CRLF + "--abcd--";

		List<Multipart.Part> parts = parseMessage(message);

		assertEquals(3, parts.size());
		assertPartContents(parts, LINE1, LINE2, LINE3);
		assertPartHeaders(parts, "", "", "");
	}

	@Test
	public void parseMessageNoHeadersTrailingCRLF() {
		String message =
			"--abcd" + CRLF + "" + CRLF +
				LINE1 +
				CRLF + "--abcd" + CRLF + "" + CRLF +
				LINE2 +
				CRLF + "--abcd" + CRLF + "" + CRLF +
				LINE3 +
				CRLF + "--abcd--" + CRLF;

		List<Multipart.Part> parts = parseMessage(message);

		assertEquals(3, parts.size());
		assertPartContents(parts, LINE1, LINE2, LINE3);
		assertPartHeaders(parts, "", "", "");
	}

	@Test
	public void parseMessageNoHeadersNoEom() {
		String message =
			"--abcd" + CRLF + "" + CRLF +
				LINE1 +
				CRLF + "--abcd" + CRLF + "" + CRLF +
				LINE2 +
				CRLF + "--abcd" + CRLF + "" + CRLF +
				LINE3;

		List<Multipart.Part> parts = parseMessage(message);

		assertEquals(3, parts.size());
		assertPartContents(parts, LINE1, LINE2, LINE3);
		assertPartHeaders(parts, "", "", "");
	}

	@Test
	public void parseMessageHeaders() {
		String message =
			"--abcd" + CRLF + HEADER1 + CRLF +
				LINE1 +
				CRLF + "--abcd" + CRLF + HEADER2 + CRLF +
				LINE2 +
				CRLF + "--abcd" + CRLF + HEADER3 + CRLF +
				LINE3 +
				CRLF + "--abcd--";

		List<Multipart.Part> parts = parseMessage(message);

		assertEquals(3, parts.size());
		assertPartContents(parts, LINE1, LINE2, LINE3);
		assertPartHeaders(parts, HEADER1, HEADER2, HEADER3);
	}

	@Test
	public void parseEmptyMessage() {
		String message =
			"--abcd" + CRLF + "" + CRLF +
				CRLF + "--abcd--" + CRLF;

		List<Multipart.Part> parts = parseMessage(message);

		assertEquals(1, parts.size());
		assertPartContents(parts, "");
	}

	@Test
	public void parseEomOnlyMessage() {
		String message = "--abcd--" + CRLF;

		List<Multipart.Part> parts = parseMessage(message);

		assertEquals(0, parts.size());
	}

	@Test
	public void parseEmptyString() {
		List<Multipart.Part> parts = parseMessage("");

		assertEquals(0, parts.size());
	}

	private List<Multipart.Part> parseMessage(String message) {
		ByteArrayInputStream stream = new ByteArrayInputStream(message.getBytes());

		Multipart multipart = new Multipart(stream, BOUNDARY);

		List<Multipart.Part> parts = new ArrayList<Multipart.Part>();
		Multipart.Part part;
		while ((part = multipart.nextPart()) != null) {
			parts.add(part);
		}
		return parts;
	}

	private void assertPartContents(List<Multipart.Part> parts, String... contents) {
		for (int i = 0; i < parts.size(); i++) {
			assertEquals("Content at " + i, contents[i], contentAsString(parts, i));
		}
	}

	private void assertPartHeaders(List<Multipart.Part> parts, String... headers) {
		for (int i = 0; i < parts.size(); i++) {
			assertEquals("Header at " + i, headers[i], parts.get(i).getHeader());
		}
	}

	private String contentAsString(List<Multipart.Part> parts, int index) {
		return new String(parts.get(index).getContent());
	}
}