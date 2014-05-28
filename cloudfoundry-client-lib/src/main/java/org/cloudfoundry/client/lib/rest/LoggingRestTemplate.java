/*
 * Copyright 2009-2014 the original author or authors.
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

package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.RestLogCallback;
import org.cloudfoundry.client.lib.RestLogEntry;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * RestTemplate that provides for logging of any REST calls made
 *
 * @author: Thomas Risberg
 */
public class LoggingRestTemplate extends RestTemplate {

	private Set<RestLogCallback> listeners = new LinkedHashSet<RestLogCallback>();

	@Override
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, final ResponseExtractor<T> responseExtractor) throws RestClientException {
		final String[] status = new String[1];
		final HttpStatus[] httpStatus = new HttpStatus[1];
		final Object[] headers = new Object[1];
		final String[] message = new String[1];
		T results = null;
		RestClientException exception = null;
		try {
			results = super.doExecute(url, method, requestCallback,
					new ResponseExtractor<T>() {
						@SuppressWarnings("rawtypes")
						public T extractData(ClientHttpResponse response) throws IOException {
							httpStatus[0] = response.getStatusCode();
							headers[0] = response.getHeaders();
							T data = null;
							if (responseExtractor != null && (data = responseExtractor.extractData(response)) != null) {
								if (data instanceof String) {
									message[0] = ((String)data).length() + " bytes";
								} else if (data instanceof Map) {
									message[0] = ((Map)data).keySet().toString();
								} else {
									message[0] = data.getClass().getName();
								}
								return data;
							}
							else {
								message[0] = "<no data>";
								return null;
							}
						}
					});
			status[0] = "OK";
		} catch (RestClientException e) {
			status[0] = "ERROR";
			message[0] = e.getMessage();
			exception = e;
			if (e instanceof HttpStatusCodeException) {
				httpStatus[0] = ((HttpStatusCodeException)e).getStatusCode();
			}
		}
		addLogMessage(method, url, status[0], httpStatus[0], message[0]);
		if (exception != null) {
			throw exception;
		}
		return results;
	}

	public void addLogMessage(HttpMethod method, URI url, String status, HttpStatus httpStatus, String message) {
		RestLogEntry logEntry = new RestLogEntry(method, url, status, httpStatus, message);
		for (RestLogCallback callback : listeners) {
			callback.onNewLogEntry(logEntry);
		}
	}

	void registerRestLogListener(RestLogCallback callBack) {
		if (callBack != null) {
			listeners.add(callBack);
		}
	}

	void unRegisterRestLogListener(RestLogCallback callBack) {
		listeners.remove(callBack);
	}

}
