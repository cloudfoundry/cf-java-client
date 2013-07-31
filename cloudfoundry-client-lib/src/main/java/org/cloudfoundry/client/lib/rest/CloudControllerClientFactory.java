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

	protected RestUtil restUtil;

	protected HttpProxyConfiguration httpProxyConfiguration;

	protected RestTemplate restTemplate;

	protected ObjectMapper objectMapper;

	private final Map<URL, Map<String, Object>> infoCache = new HashMap<URL, Map<String, Object>>();

	public CloudControllerClientFactory(RestUtil restUtil, HttpProxyConfiguration httpProxyConfiguration) {
		if (restUtil == null) {
			this.restUtil = new RestUtil();
		} else {
			this.restUtil = restUtil;
		}
		this.restUtil = restUtil;
		this.httpProxyConfiguration = httpProxyConfiguration;
		this.restTemplate = restUtil.createRestTemplate(httpProxyConfiguration);
		this.objectMapper = new ObjectMapper();
	}

	public CloudControllerClient newCloudController(URL cloudControllerUrl, CloudCredentials cloudCredentials,
													CloudSpace sessionSpace) {
		Map<String, Object> infoMap = getInfoMap(cloudControllerUrl);
		URL authorizationEndpoint = getAuthorizationEndpoint(infoMap);

		return new CloudControllerClientImpl(cloudControllerUrl, restUtil, cloudCredentials,
					authorizationEndpoint, sessionSpace, httpProxyConfiguration);
	}

	public CloudControllerClient newCloudController(URL cloudControllerUrl, CloudCredentials cloudCredentials,
												   String orgName, String spaceName) {
		Map<String, Object> infoMap = getInfoMap(cloudControllerUrl);
		URL authorizationEndpoint = getAuthorizationEndpoint(infoMap);

		return new CloudControllerClientImpl(cloudControllerUrl, restUtil, cloudCredentials,
				authorizationEndpoint, orgName, spaceName, httpProxyConfiguration);
	}
	
	private Map<String, Object> getInfoMap(URL cloudControllerUrl) {
		if (infoCache.containsKey(cloudControllerUrl)) {
			return infoCache.get(cloudControllerUrl);
		}
		Map<String, Object> infoMap = new HashMap<String, Object>();
		String s = restTemplate.getForObject(cloudControllerUrl + "/info", String.class);
		try {
			infoMap = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infoMap;
	}

	private URL getAuthorizationEndpoint(Map<String,Object> infoMap) {
		String authEndPoint =  (String) infoMap.get("authorization_endpoint");
		URL authEndPointUrl = null;
		if (authEndPoint != null) {
			try {
				authEndPointUrl = new URL(authEndPoint);
			} catch (MalformedURLException ignore) {}
		}
		return authEndPointUrl;
	}

}
