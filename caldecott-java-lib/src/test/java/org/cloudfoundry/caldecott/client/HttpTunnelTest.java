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
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class HttpTunnelTest {

	HttpTunnel httpTunnel;

	HttpTunnelFactory httpTunnelFactory;

	@Mock
	RestTemplate restTemplate;

	@Mock
	ResponseEntity<String> response;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		httpTunnelFactory = new HttpTunnelFactory("http://api.vcap.me", "localhost", 10000, "test", restTemplate);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testOpenTunnel() {
		openTunnel();
		verify(restTemplate).postForObject(isA(String.class), isA(HttpEntity.class), isA(Class.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testClosingTunnel() {
		openTunnel();
		httpTunnel.close();
		verify(restTemplate).exchange(isA(String.class), isA(HttpMethod.class), isA(HttpEntity.class), (Class)isNull());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWritingSomeBytes() {
		final byte[] data = "This is some data to write".getBytes();
		openTunnel();
		final byte[][] sent = new byte[1][1];
		when(restTemplate.exchange(isA(String.class), isA(HttpMethod.class), isA(HttpEntity.class), (Class)isNull()))
				.thenAnswer(new Answer<ResponseEntity<?>>() {
					public ResponseEntity<?> answer(InvocationOnMock invocation) throws Throwable {
						Object[] args = invocation.getArguments();
						HttpEntity arg3 = (HttpEntity) args[2];
						sent[0] = (byte[]) arg3.getBody();
						return response;
					}
				});
		httpTunnel.write(data);
		verify(restTemplate).exchange(isA(String.class), isA(HttpMethod.class), isA(HttpEntity.class), (Class)isNull());
		assertEquals(new String(data), new String(sent[0]));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testReadingSomeBytes() {
		final byte[] data = "This is some data to read".getBytes();
		openTunnel();
		when(restTemplate.execute(isA(String.class), isA(HttpMethod.class), isA(RequestCallback.class),
				isA(ResponseExtractor.class))).thenReturn(data);
		final byte[] answer = httpTunnel.read(false);
		verify(restTemplate).execute(isA(String.class), isA(HttpMethod.class), isA(RequestCallback.class),
				isA(ResponseExtractor.class));
		assertEquals(new String(data), new String(answer));
	}

	@SuppressWarnings("unchecked")
	private void openTunnel() {
		when(restTemplate.postForObject(isA(String.class), isA(HttpEntity.class), isA(Class.class))).thenReturn("{}");
		httpTunnel = (HttpTunnel) httpTunnelFactory.createTunnel();
	}
}
