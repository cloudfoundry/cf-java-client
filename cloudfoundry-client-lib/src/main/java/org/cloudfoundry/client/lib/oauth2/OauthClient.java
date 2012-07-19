package org.cloudfoundry.client.lib.oauth2;

import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: Dave Syer
 * @author: trisberg
 */
public class OauthClient {

	String authorizationUrl;

	public OauthClient(String authorizationUrl) {
		this.authorizationUrl = authorizationUrl;
	}

	public OAuth2AccessToken getToken(String username, String password) {
		OAuth2ProtectedResourceDetails resource = getImplicitResource();
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("credentials", String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password));
		AccessTokenRequest request = new DefaultAccessTokenRequest();
		request.setAll(parameters);
		OAuth2AccessToken token = new ImplicitAccessTokenProvider().obtainAccessToken(resource, request);
		return token;
	}

	private ImplicitResourceDetails getImplicitResource() {
		ImplicitResourceDetails resource = new ImplicitResourceDetails();
		String clientId = "vmc";
		resource.setClientId(clientId);
		resource.setId(clientId);
		resource.setClientAuthenticationScheme(AuthenticationScheme.header);
		resource.setAccessTokenUri(authorizationUrl + "/oauth/authorize");
		resource.setScope(Arrays.asList("read"));
		String redirectUri = "http://uaa.cloudfoundry.com/redirect/vmc";
		resource.setPreEstablishedRedirectUri(redirectUri);
		resource.setUseCurrentUri(false);
		return resource;
	}

}
