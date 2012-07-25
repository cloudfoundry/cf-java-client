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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory used to create cloud controller client implementations suitable for use against v1 or v2 cloud controller.
 * The factory should determine the type of client to create.
 *
 * @author Thgomas Risberg
 */
public class CloudControllerClientFactory {

	protected RestTemplate restTemplate;

	protected ObjectMapper objectMapper;

	private final Map<URL, Map<String, Object>> infoCache = new HashMap<URL, Map<String, Object>>();

	public CloudControllerClientFactory() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}

	public CloudControllerClient newCloudController(URL cloudControllerUrl) {
		CloudAuthenticationConfiguration authenticationConfiguration = new CloudAuthenticationConfiguration();
		return createCloudControllerClient(cloudControllerUrl, authenticationConfiguration, null);
	}

	public CloudControllerClient newCloudController(URL cloudControllerUrl, String email, String password) {
		CloudAuthenticationConfiguration authenticationConfiguration =
				new CloudAuthenticationConfiguration(email, password,
						getAuthorizationEndpoint(getInfoMap(cloudControllerUrl)));
		return createCloudControllerClient(cloudControllerUrl, authenticationConfiguration, null);
	}

	public CloudControllerClient newCloudController(URL cloudControllerUrl, String email, String password, String token) {
		CloudAuthenticationConfiguration authenticationConfiguration =
				new CloudAuthenticationConfiguration(email, password,
						getAuthorizationEndpoint(getInfoMap(cloudControllerUrl)));
		return createCloudControllerClient(cloudControllerUrl, authenticationConfiguration, token);
	}

	private CloudControllerClient createCloudControllerClient(URL cloudControllerUrl,
			CloudAuthenticationConfiguration authenticationConfiguration,
			String token) {
		CloudControllerClient cc = null;
		boolean v2 = isV2(getInfoMap(cloudControllerUrl));
		if (v2) {
			cc = new CloudControllerClientV2(cloudControllerUrl, authenticationConfiguration, token);
		}
		else {
			cc = new CloudControllerClientV1(cloudControllerUrl, authenticationConfiguration, token);
		}
		return cc;
	}

	private Map<String, Object> getInfoMap(URL cloudControllerUrl) {
		if (infoCache.containsKey(cloudControllerUrl)) {
			return infoCache.get(cloudControllerUrl);
		}
		Map<String, Object> infoMap = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		String s = restTemplate.getForObject(cloudControllerUrl + "/info", String.class);
		try {
			infoMap = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return infoMap;
	}

	private boolean isV2(Map<String,Object> infoMap) {
		Object v = infoMap.get("version");
		if (v != null && v instanceof String && Double.valueOf((String) v) <= 1.0) {
			return false;
		}
		else {
			return true;
		}
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
