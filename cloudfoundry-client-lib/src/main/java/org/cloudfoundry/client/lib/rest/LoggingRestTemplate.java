package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.RestLogCallback;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * RestTemplate that provides for logging of any REST calls made
 *
 * @author: Thomas Risberg
 */
public class LoggingRestTemplate extends RestTemplate {

	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(100);

	private List<RestLogCallback> listeners = new ArrayList<RestLogCallback>();

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
						public T extractData(ClientHttpResponse response) throws IOException {
							httpStatus[0] = response.getStatusCode();
							headers[0] = response.getHeaders();
							if (responseExtractor != null) {
								T data = responseExtractor.extractData(response);
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
		addLogMessage(formatLogMessage(method, url, status[0], status[0], httpStatus[0], message[0]));
		if (exception != null) {
			throw exception;
		}
		return results;
	}

	private String formatLogMessage(HttpMethod method, URI url, String s, String status, HttpStatus httpStatus, String message) {
		return status + " :: HTTP STATUS: " + httpStatus + " :: REQUEST: " + method + " " + url + " :: " + message;
	}

	public void addLogMessage(String message) {
		queue.add(message);
		for (RestLogCallback callback : listeners) {
			callback.onNewLogEntry(message);
		}
	}

	public void clearLogMessages() {
		queue.clear();
	}

	public List<String> getLogMessages() {
		return new ArrayList<String>(queue);
	}

	void registerRestLogListener(RestLogCallback callBack) {
		listeners.add(callBack);
	}

	void unRegisterRestLogListener(RestLogCallback callBack) {
		listeners.remove(callBack);
	}

}
