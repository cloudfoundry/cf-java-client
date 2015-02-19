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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.util.RestUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.client.RestTemplate;

/**
 * Factory used to create cloud controller client implementations.
 *
 * @author Thgomas Risberg
 * @author Ramnivas Laddad
 */
public class CloudControllerClientFactory {

	private final RestUtil restUtil;
	private final RestTemplate restTemplate;

	private OauthClient oauthClient;

	private final HttpProxyConfiguration httpProxyConfiguration;
	private final boolean trustSelfSignedCerts;

	private ObjectMapper objectMapper;

	private final Map<URL, Map<String, Object>> infoCache = new HashMap<URL, Map<String, Object>>();

	public CloudControllerClientFactory(HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		this.restUtil = new RestUtil();
		this.restTemplate = restUtil.createRestTemplate(httpProxyConfiguration, trustSelfSignedCerts);

		this.httpProxyConfiguration = httpProxyConfiguration;
		this.trustSelfSignedCerts = trustSelfSignedCerts;

		this.objectMapper = new ObjectMapper();
	}

	public CloudControllerClient newCloudController(URL cloudControllerUrl, CloudCredentials cloudCredentials,
	                                                CloudSpace sessionSpace) {
		createOauthClient(cloudControllerUrl);
		LoggregatorClient loggregatorClient = new LoggregatorClient(trustSelfSignedCerts);

		return new CloudControllerClientImpl(cloudControllerUrl, restTemplate, oauthClient, loggregatorClient,
				cloudCredentials, sessionSpace);
	}

	public CloudControllerClient newCloudController(URL cloudControllerUrl, CloudCredentials cloudCredentials,
	                                                String orgName, String spaceName) {
		createOauthClient(cloudControllerUrl);
		LoggregatorClient loggregatorClient = new LoggregatorClient(trustSelfSignedCerts);

		return new CloudControllerClientImpl(cloudControllerUrl, restTemplate, oauthClient, loggregatorClient,
				cloudCredentials, orgName, spaceName);
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public OauthClient getOauthClient() {
		return oauthClient;
	}

	private void createOauthClient(URL cloudControllerUrl) {
		Map<String, Object> infoMap = getInfoMap(cloudControllerUrl);
		URL authorizationEndpoint = getAuthorizationEndpoint(infoMap, cloudControllerUrl);
		this.oauthClient = restUtil.createOauthClient(authorizationEndpoint, httpProxyConfiguration, trustSelfSignedCerts);
	}

	private Map<String, Object> getInfoMap(URL cloudControllerUrl) {
		if (infoCache.containsKey(cloudControllerUrl)) {
			return infoCache.get(cloudControllerUrl);
		}

		String s = restTemplate.getForObject(cloudControllerUrl + "/info", String.class);

		try {
			return objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			throw new RuntimeException("Error getting /info from Cloud Controller", e);
		}
	}

	private URL getAuthorizationEndpoint(Map<String, Object> infoMap, URL cloudControllerUrl) {
		String authEndPoint = (String) infoMap.get("authorization_endpoint");

		try {
			return new URL(authEndPoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Error creating auth endpoint URL for endpoint " + authEndPoint, e);
		}
	}
}
