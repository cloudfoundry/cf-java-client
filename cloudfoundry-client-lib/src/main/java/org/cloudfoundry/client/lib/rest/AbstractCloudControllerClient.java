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

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.cloudfoundry.client.lib.util.UploadApplicationPayloadHttpMessageConverter;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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

	private static final MediaType JSON_MEDIA_TYPE = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("UTF-8"));

	private RestTemplate restTemplate = new RestTemplate();
	private URL cloudControllerUrl;

	protected String token;

	protected CloudAuthenticationConfiguration authenticationConfiguration;

	public AbstractCloudControllerClient(URL cloudControllerUrl, CloudAuthenticationConfiguration authenticationConfiguration, String token) {
		this(cloudControllerUrl, authenticationConfiguration, token, new SimpleClientHttpRequestFactory());
	}

	public AbstractCloudControllerClient(URL cloudControllerUrl, CloudAuthenticationConfiguration authenticationConfiguration, String token, ClientHttpRequestFactory requestFactory) {
		Assert.notNull(cloudControllerUrl, "CloudControllerUrl cannot be null");
		Assert.notNull(authenticationConfiguration, "CloudAuthenticationConfiguration cannot be null");
		this.authenticationConfiguration = authenticationConfiguration;
		this.cloudControllerUrl = cloudControllerUrl;
		this.token = token;
		this.restTemplate.setRequestFactory(
				new CloudFoundryClientHttpRequestFactory(
						requestFactory == null ? new SimpleClientHttpRequestFactory() : requestFactory));
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

	public void setSessionSpace(CloudSpace space) {
		throw new UnsupportedOperationException(
				"This feature is not supported for the version of Cloud Controller you are accessing");
	}

	public void setProxyUser(String proxyUser) {
		if (this.authenticationConfiguration != null) {
			this.authenticationConfiguration.setProxyUser(proxyUser);
		}
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
			if (authenticationConfiguration != null && authenticationConfiguration.getProxyUser() != null) {
				request.getHeaders().add(PROXY_USER_HEADER_KEY, authenticationConfiguration.getProxyUser());
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
					try {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
						exception.setDescription(CloudUtil.parse(String.class, map.get("description")));
					} catch (JsonParseException e) {
						// ignore
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
}
