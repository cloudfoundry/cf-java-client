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

package org.cloudfoundry.client.lib.oauth2;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.util.CloudUtil;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client that can handle authentication against a UAA instance
 *
 * @author: Dave Syer
 * @author: Thomas Risberg
 */
public class OauthClient {

	private URL authorizationUrl;

	private RestTemplate restTemplate;

	private String uaaVersion;

	public OauthClient(URL authorizationUrl) {
		this.authorizationUrl = authorizationUrl;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(new CommonsClientHttpRequestFactory());
		this.uaaVersion = lookupVersion(authorizationUrl);
	}

	public OAuth2AccessToken getToken(String username, String password) {
		OAuth2ProtectedResourceDetails resource = getImplicitResource();
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("credentials", String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password));
		AccessTokenRequest request = new DefaultAccessTokenRequest();
		request.setAll(parameters);
		ImplicitAccessTokenProvider provider = new ImplicitAccessTokenProvider();
		provider.setRestTemplate(restTemplate);
		OAuth2AccessToken token = null;
		try {
			token = provider.obtainAccessToken(resource, request);
		}
		catch (OAuth2AccessDeniedException oauthEx) {
			HttpStatus status = HttpStatus.valueOf(oauthEx.getHttpErrorCode());
			CloudFoundryException cfEx = new CloudFoundryException(status, oauthEx.getMessage());
			cfEx.setDescription(oauthEx.getSummary());
			throw cfEx;
		}
		return token;
	}

	private ImplicitResourceDetails getImplicitResource() {
		ImplicitResourceDetails resource = new ImplicitResourceDetails();
		String clientId = "vmc";
		resource.setClientId(clientId);
		resource.setId(clientId);
		resource.setClientAuthenticationScheme(AuthenticationScheme.header);
		resource.setAccessTokenUri(authorizationUrl + "/oauth/authorize");
		if (uaaVersion.startsWith("1.0")) {
			resource.setScope(Arrays.asList("read"));
		}
		String redirectUri = "http://uaa.cloudfoundry.com/redirect/vmc";
		resource.setPreEstablishedRedirectUri(redirectUri);
		resource.setUseCurrentUri(false);
		return resource;
	}

	private String lookupVersion(URL authorizationUrl) {
		String resp = restTemplate.getForObject(authorizationUrl + "/login", String.class);
		Map<String, Object> respMap = JsonUtil.convertJsonToMap(resp);
		if (respMap == null) {
			return "1.0";
		}
		Map<String, Object> appMap = (Map<String, Object>) respMap.get("app");
		if (appMap != null) {
			String version = String.valueOf(appMap.get("version"));
			if (version != null) {
				return version;
			}
		}
		return "1.0";
	}

}
