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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

/**
 * Client that can handle authentication against a UAA instance
 *
 * @author Dave Syer
 * @author Thomas Risberg
 */
public class OauthClient {

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

	private URL authorizationUrl;

	private RestTemplate restTemplate;

	private OAuth2AccessToken token;
	private CloudCredentials credentials;

	public OauthClient(URL authorizationUrl, RestTemplate restTemplate) {
		this.authorizationUrl = authorizationUrl;
		this.restTemplate = restTemplate;
	}

	public void init(CloudCredentials credentials) {
		if (credentials != null) {
			this.credentials = credentials;

			if (credentials.getToken() != null) {
				this.token = credentials.getToken();
			} else {
				this.token = createToken(credentials.getEmail(), credentials.getPassword(),
						credentials.getClientId(), credentials.getClientSecret());
			}
		}
	}

	public void clear() {
		this.token = null;
		this.credentials = null;
	}

	public OAuth2AccessToken getToken() {
		if (token == null) {
			return null;
		}

		if(this.credentials.isRefreshable()) {
			if (token.getExpiresIn() < 50) { // 50 seconds before expiration? Then refresh it.
				token = refreshToken(token, credentials.getEmail(), credentials.getPassword(),
						credentials.getClientId(), credentials.getClientSecret());
			}
		}

		return token;
	}

	public String getAuthorizationHeader() {
		OAuth2AccessToken accessToken = getToken();
		if (accessToken != null) {
			return accessToken.getTokenType() + " " + accessToken.getValue();
		}
		return null;
	}

	private OAuth2AccessToken createToken(String username, String password, String clientId, String clientSecret) {
		OAuth2ProtectedResourceDetails resource = getResourceDetails(username, password, clientId, clientSecret);
		AccessTokenRequest request = createAccessTokenRequest(username, password);

		ResourceOwnerPasswordAccessTokenProvider provider = createResourceOwnerPasswordAccessTokenProvider();
		try {
			return provider.obtainAccessToken(resource, request);
		}
		catch (OAuth2AccessDeniedException oauthEx) {
			HttpStatus status = HttpStatus.valueOf(oauthEx.getHttpErrorCode());
			CloudFoundryException cfEx = new CloudFoundryException(status, oauthEx.getMessage());
			cfEx.setDescription(oauthEx.getSummary());
			throw cfEx;
		}
	}

	private OAuth2AccessToken refreshToken(OAuth2AccessToken currentToken, String username, String password, String clientId, String clientSecret) {
		OAuth2ProtectedResourceDetails resource = getResourceDetails(username, password, clientId, clientSecret);
		AccessTokenRequest request = createAccessTokenRequest(username, password);

		ResourceOwnerPasswordAccessTokenProvider provider = createResourceOwnerPasswordAccessTokenProvider();

		return provider.refreshAccessToken(resource, currentToken.getRefreshToken(), request);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void changePassword(String oldPassword, String newPassword) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION_HEADER_KEY, token.getTokenType() + " " + token.getValue());
		HttpEntity info = new HttpEntity(headers);
		ResponseEntity<String> response = restTemplate.exchange(authorizationUrl + "/userinfo", HttpMethod.GET, info, String.class);
		Map<String, Object> responseMap = JsonUtil.convertJsonToMap(response.getBody());
		String userId = (String) responseMap.get("user_id");
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("schemas", new String[] {"urn:scim:schemas:core:1.0"});
		body.put("password", newPassword);
		body.put("oldPassword", oldPassword);
		HttpEntity<Map> httpEntity = new HttpEntity<Map>(body, headers);
		restTemplate.put(authorizationUrl + "/User/{id}/password", httpEntity, userId);
	}

	protected ResourceOwnerPasswordAccessTokenProvider createResourceOwnerPasswordAccessTokenProvider() {
		ResourceOwnerPasswordAccessTokenProvider resourceOwnerPasswordAccessTokenProvider = new ResourceOwnerPasswordAccessTokenProvider();
		resourceOwnerPasswordAccessTokenProvider.setRequestFactory(restTemplate.getRequestFactory()); //copy the http proxy along
		return resourceOwnerPasswordAccessTokenProvider;
	}

	private AccessTokenRequest createAccessTokenRequest(String username, String password) {
		AccessTokenRequest request = new DefaultAccessTokenRequest();
		return request;
	}

	private OAuth2ProtectedResourceDetails getResourceDetails(String username, String password, String clientId, String clientSecret) {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		resource.setUsername(username);
		resource.setPassword(password);

		resource.setClientId(clientId);
		resource.setClientSecret(clientSecret);
		resource.setId(clientId);
		resource.setClientAuthenticationScheme(AuthenticationScheme.header);
		resource.setAccessTokenUri(authorizationUrl + "/oauth/token");

		return resource;
	}
}
