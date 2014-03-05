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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The Http implementation of a Tunnel designed to interact with the Caldecott server REST application.
 *
 * @author Thomas Risberg
 */
public class HttpTunnel implements Tunnel {

	protected final Log logger = LogFactory.getLog(getClass());

	// configuration options for the tunnel
	private String url;
	private String host;
	private int port;
	private String auth;

	// REST template to use for tunnel communication
	private final RestOperations restOperations;

	// variables to keep track of communication state with the tunnel web service
	private Map<String, String> tunnelInfo;
	private long lastWrite = 0;
	private long lastRead = 0;

	public HttpTunnel(String url, String host, int port, String auth, RestOperations restOperations) {
		this.url = url;
		this.host = host;
		this.port = port;
		this.auth = auth;
		this.restOperations = restOperations;
		openTunnel();
	}

	public void write(byte[] data) {
		sendBytes(data, ++lastWrite);
	}

	public byte[] read(boolean retry) {
		if (!retry) {
			lastRead++;
		}
		return receiveBytes(lastRead);
	}

	private void openTunnel() {
		String initMsg = "{\"host\":\"" + host + "\",\"port\":" + port + "}";
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing tunnel: " + initMsg);
		}
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Auth-Token", auth);
		requestHeaders.set("Content-Length", initMsg.length()+"");
		HttpEntity<String> requestEntity = new HttpEntity<String>(initMsg, requestHeaders);
		String jsonResponse;
		try {
			jsonResponse = restOperations.postForObject(url + "/tunnels", requestEntity, String.class);
		} catch (RuntimeException e) {
			logger.error("Fatal error while opening tunnel: " + e.getMessage());
			close();
			throw e;
		}
		try {
			this.tunnelInfo = TunnelHelper.convertJsonToMap(jsonResponse);
		} catch (IOException ignore) {
			this.tunnelInfo = new HashMap<String, String>();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void close() {
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting tunnel " + this.tunnelInfo.get("path"));
		}
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Auth-Token", auth);
		HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
		try {
			restOperations.exchange(url + this.tunnelInfo.get("path"), HttpMethod.DELETE, requestEntity, null);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == 404) {
				if (logger.isDebugEnabled()) {
					logger.debug("Tunnel not found [" + e.getStatusCode() + "] " + e.getStatusText());
				}
			}
			else {
				logger.warn("Error while deleting tunnel [" + e.getStatusCode() + "] " + e.getStatusText());
			}
		}
	}

	private void sendBytes(byte[] bytes, long page) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Auth-Token", auth);
		requestHeaders.set("Content-Length", bytes.length+"");
		String dataUrl = url + this.tunnelInfo.get("path_in") + "/" + page;
		HttpEntity<byte[]> requestEntity = new HttpEntity<byte[]>(bytes, requestHeaders);
		if (logger.isTraceEnabled()) {
			logger.trace("SENDING: " + printBytes(bytes));
		}
		ResponseEntity<?> response = restOperations.exchange(dataUrl, HttpMethod.PUT, requestEntity, null);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + bytes.length + " bytes] PUT to " + dataUrl +" resulted in: " + response.getStatusCode());
		}
	}

	private byte[] receiveBytes(long page) {
		byte[] response = receiveDataBuffered(page);
		if (logger.isTraceEnabled()) {
			logger.trace("RECEIVED: " + printBytes(response));
		}
		return response;
	}

	private byte[] receiveDataBuffered(long page) {
		final String dataUrl = url + this.tunnelInfo.get("path_out") + "/" + page;
		byte[] responseBytes;
		try {
			responseBytes = restOperations.execute(
					dataUrl,
					HttpMethod.GET,
					new RequestCallback() {
						public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
							clientHttpRequest.getHeaders().set("Auth-Token", auth);
						}
					},
					new ResponseExtractor<byte[]>() {
						public byte[] extractData(ClientHttpResponse clientHttpResponse) throws IOException {
							if (logger.isDebugEnabled()) {
								logger.debug("HEADER: " + clientHttpResponse.getHeaders().toString());
							}
							InputStream stream = clientHttpResponse.getBody();
							byte[] data = readContentData(stream);
							if (logger.isDebugEnabled()) {
								logger.debug("[" + data.length + " bytes] GET from " + dataUrl + " resulted in: " + clientHttpResponse.getStatusCode());
							}
							return data;
						}
					}
			);
		} catch (HttpStatusCodeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("GET from " + dataUrl + " resulted in: " + e.getStatusCode().value());
			}
			throw e;
		}
		return responseBytes;
	}

	@Override
	public String toString() {
		return "HttpTunnel for " + url + " on " + host + ":" + port;
	}

	private byte[] readContentData(InputStream stream) throws IOException {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		while (true) {
			byte[] buffer = new byte[1024];
			int len = stream.read(buffer);
			if (len < 0) {
				break;
			}
			data.write(buffer, 0, len);
		}
		return data.toByteArray();
	}

	private static String printBytes(byte[] array) {
		StringBuilder printable = new StringBuilder();
		printable.append("[").append(array.length).append("] = ").append("0x");
	        for (byte aByte : array) {
	            printable.append(byteToHex(aByte));
	        }
			return printable.toString();
		}

	private static String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = {
				'0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
		};
		char[] array = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
		return new String(array);
	}

}
