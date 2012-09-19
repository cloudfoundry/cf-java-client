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

package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.cloudfoundry.client.lib.util.UploadApplicationPayloadHttpMessageConverter;
import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of the CloudControllerClient intended to serve as the base for v1 and v2 implementations.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Jennifer Hickey
 * @author Dave Syer
 * @author Thomas Risberg
 */
public abstract class AbstractCloudControllerClient implements CloudControllerClient {

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String PROXY_USER_HEADER_KEY = "Proxy-User";

	protected static final MediaType JSON_MEDIA_TYPE = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("UTF-8"));

	// This map only contains framework/runtime mapping for frameworks that we actively support
	private static Map<String, Integer> FRAMEWORK_DEFAULT_MEMORY = new HashMap<String, Integer>() {{
		put("spring", 512);
		put("lift", 512);
		put("grails", 512);
		put("java_web", 512);
	}};

	private static int DEFAULT_MEMORY = 256;

	private RestTemplate restTemplate;

	private URL cloudControllerUrl;

	protected CloudCredentials cloudCredentials;

	protected URL authorizationEndpoint;

	protected String token;

	public AbstractCloudControllerClient(URL cloudControllerUrl, HttpProxyConfiguration httpProxyConfiguration,
										 CloudCredentials cloudCredentials, URL authorizationEndpoint) {
		Assert.notNull(cloudControllerUrl, "CloudControllerUrl cannot be null");
		this.cloudCredentials = cloudCredentials;
		if (cloudCredentials != null && cloudCredentials.getToken() != null) {
			this.token = cloudCredentials.getToken();
		}
		this.cloudControllerUrl = cloudControllerUrl;
		this.authorizationEndpoint = authorizationEndpoint;
		this.restTemplate = RestUtil.createRestTemplate(httpProxyConfiguration);
		ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
		this.restTemplate.setRequestFactory(
				new CloudFoundryClientHttpRequestFactory(requestFactory));

		this.restTemplate.setErrorHandler(new ErrorHandler());
		this.restTemplate.setMessageConverters(getHttpMessageConverters());
	}

	public URL getCloudControllerUrl() {
		return this.cloudControllerUrl;
	}

	public List<CloudSpace> getSpaces() {
		ArrayList<CloudSpace> list = new ArrayList<CloudSpace>();
		return list;
	}

	public int[] getApplicationMemoryChoices() {
		// TODO: Get it from cloudcontroller's 'info/resources' end point
		int[] generalChoices = new int[] {64, 128, 256, 512, 1024, 2048};
		int maxMemory = getInfo().getLimits().getMaxTotalMemory();

		int length = 0;
		for (int generalChoice : generalChoices) {
			if (generalChoice <= maxMemory) {
				length++;
			}
		}

		int[] result = new int[length];
		System.arraycopy(generalChoices, 0, result, 0, length);
		return result;
	}

	public int getDefaultApplicationMemory(String framework) {
		Integer memory = FRAMEWORK_DEFAULT_MEMORY.get(framework);
		if (memory == null) {
			return DEFAULT_MEMORY;
		}
		return memory;
	}

	public void updatePassword(String newPassword) {
		updatePassword(cloudCredentials, newPassword);
	}

	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	protected String getUrl(String path) {
		return cloudControllerUrl + "/" + path;
	}

	private List<HttpMessageConverter<?>> getHttpMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new UploadApplicationPayloadHttpMessageConverter());
		messageConverters.add(getFormHttpMessageConverter());
		messageConverters.add(new MappingJacksonHttpMessageConverter());
		return messageConverters;
	}

	private FormHttpMessageConverter getFormHttpMessageConverter() {
		FormHttpMessageConverter formPartsMessageConverter = new CloudFoundryFormHttpMessageConverter();
		formPartsMessageConverter.setPartConverters(getFormPartsMessageConverters());
		return formPartsMessageConverter;
	}

	private List<HttpMessageConverter<?>> getFormPartsMessageConverters() {
		List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setSupportedMediaTypes(Collections.singletonList(JSON_MEDIA_TYPE));
		stringConverter.setWriteAcceptCharset(false);
		partConverters.add(stringConverter);
		partConverters.add(new ResourceHttpMessageConverter());
		partConverters.add(new UploadApplicationPayloadHttpMessageConverter());
		return partConverters;
	}

	private class CloudFoundryClientHttpRequestFactory implements ClientHttpRequestFactory {

		private static final String LEGACY_TOKEN_PREFIX = "0408";
		private ClientHttpRequestFactory delegate;

		public CloudFoundryClientHttpRequestFactory(ClientHttpRequestFactory delegate) {
			this.delegate = delegate;
		}

		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
			ClientHttpRequest request = delegate.createRequest(uri, httpMethod);
			if (token != null) {
				String header = token;
				if (!header.startsWith(LEGACY_TOKEN_PREFIX) && !header.toLowerCase().startsWith("bearer")) {
					header = "Bearer " + header; // UAA token without OAuth prefix
				}
				request.getHeaders().add(AUTHORIZATION_HEADER_KEY, header);
			}
			if (cloudCredentials != null && cloudCredentials.getProxyUser() != null) {
				request.getHeaders().add(PROXY_USER_HEADER_KEY, cloudCredentials.getProxyUser());
			}
			return request;
		}
	}

	public static class ErrorHandler extends DefaultResponseErrorHandler {
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			HttpStatus statusCode = response.getStatusCode();
			switch (statusCode.series()) {
				case CLIENT_ERROR:
					CloudFoundryException exception = new CloudFoundryException(statusCode, response.getStatusText());
					ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
					if (response.getBody() != null) {
						try {
								@SuppressWarnings("unchecked")
								Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
								exception.setDescription(CloudUtil.parse(String.class, map.get("description")));
						} catch (JsonParseException e) {
							exception.setDescription("Client error");
						}
					} else {
						exception.setDescription("Client error");
					}
					throw exception;
				case SERVER_ERROR:
					throw new HttpServerErrorException(statusCode, response.getStatusText());
				default:
					throw new RestClientException("Unknown status code [" + statusCode + "]");
			}
		}
	}

	public static class CloudFoundryFormHttpMessageConverter extends FormHttpMessageConverter {
		@Override
		protected String getFilename(Object part) {
			if (part instanceof UploadApplicationPayload) {
				return ((UploadApplicationPayload) part).getArchive().getFilename();
			}
			return super.getFilename(part);
		}
	}

	protected String doGetFile(String urlPath, Object app, int instanceIndex, String filePath, int startPosition, int endPosition) {
		Assert.isTrue(startPosition >= -1, "Invalid start position value: " + startPosition);
		Assert.isTrue(endPosition >= -1, "Invalid end position value: " + endPosition);
		Assert.isTrue(startPosition < 0 || endPosition < 0 || endPosition >= startPosition,
				"The end position (" + endPosition + ") can't be less than the start position (" + startPosition + ")");

		int start, end;
		if (startPosition == -1 && endPosition == -1) {
			start = 0;
			end = -1;
		} else {
			start = startPosition;
			end = endPosition;
		}

		final String range =
				"bytes=" + (start == -1 ? "" : start) + "-" + (end == -1 ? "" : end);

		boolean supportsRanges = false;
		try {
			supportsRanges = getRestTemplate().execute(getUrl(urlPath),
					HttpMethod.HEAD,
					new RequestCallback() {
						public void doWithRequest(ClientHttpRequest request) throws IOException {
							request.getHeaders().set("Range", "bytes=0-");
						}
					},
					new ResponseExtractor<Boolean>() {
						public Boolean extractData(ClientHttpResponse response) throws IOException {
							if (response.getStatusCode().equals(HttpStatus.PARTIAL_CONTENT)) {
								return true;
							}
							return false;
						}
					},
					app, instanceIndex, filePath);
		} catch (CloudFoundryException e) {
			if (e.getStatusCode().equals(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)) {
				// must be a 0 byte file
				return "";
			} else {
				throw e;
			}
		}
		HttpHeaders headers = new HttpHeaders();
		if (supportsRanges) {
			headers.set("Range", range);
		}
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
		ResponseEntity<String> responseEntity =
				getRestTemplate().exchange(getUrl(urlPath),
						HttpMethod.GET, requestEntity, String.class, app, instanceIndex, filePath);
		String response = responseEntity.getBody();
		boolean partialFile = false;
		if (responseEntity.getStatusCode().equals(HttpStatus.PARTIAL_CONTENT)) {
			partialFile = true;
		}
		if (!partialFile) {
			if (start == -1) {
				return response.substring(response.length() - end);
			} else {
				if (start >= response.length()) {
					if (response.length() == 0) {
						return "";
					}
					throw new CloudFoundryException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
							"The starting position " + start + " is past the end of the file content.");
				}
				if (end != -1) {
					if (end >= response.length()) {
						end = response.length() - 1;
					}
					return response.substring(start, end + 1);
				} else {
					return response.substring(start);
				}
			}
		}
		return response;
	}

}
