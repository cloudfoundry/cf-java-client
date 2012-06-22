/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.caldecott.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Mock tests for the SocketClient
 *
 * @author Thomas Risberg
 */
public class SocketClientTest {

	@Mock
	Socket socket;

	@Mock
	InputStream inputStream;

	@Mock
	OutputStream outputStream;

	SocketClient client;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		client = new SocketClient(socket);
	}

	@Test
	public void testReadSomeBytes() throws IOException {
		final byte[] data = "This is some data to read".getBytes();
		when(socket.getInputStream()).thenReturn(inputStream);
		when(inputStream.read(isA(byte[].class))).thenAnswer( new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				byte[] arg1 = (byte[]) args[0];
				for (int i = 0; i < data.length; i++) {
					arg1[i] = data[i];
				}
				return data.length;
			}
		});
		byte[] answer = client.read();
		assertEquals(new String(data), new String(answer));
		assertTrue(client.isOpen());
		when(inputStream.read(isA(byte[].class))).thenReturn(-1);
		client.read();
		assertFalse(client.isOpen());
	}

	@Test
	public void testWriteSomeBytes() throws IOException {
		final byte[] data = "This is some data to write".getBytes();
		final byte[] result;
		when(socket.getOutputStream()).thenReturn(outputStream);
		ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
		client.write(data);
		verify(outputStream).write(captor.capture());
		assertEquals(new String(data), new String(captor.getValue()));
		verify(outputStream).flush();
	}

}
