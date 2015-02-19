package org.cloudfoundry.client.lib.tokens;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.net.URI;
import java.util.HashMap;

public class TargetInfos extends HashMap<String, HashMap<String, String>> {
	private static final String ACCESS_TOKEN_KEY = ":token";
	private static final String REFRESH_TOKEN_KEY = ":refresh_token";
	private static final String SPACE_KEY = ":space";
	private static final String ORG_KEY = ":organization";
	private static final String VERSION_KEY = ":version";

	public HashMap<String, String> get(URI target) {
		return get(target.toString());
	}

	public void put(URI target, HashMap<String, String> targetInfo) {
		put(target.toString(), targetInfo);
	}

	public HashMap<String, String> remove(URI target) {
		return remove(target.toString());
	}

	public DefaultOAuth2AccessToken getToken(HashMap<String, String> target) {
		final String token = target.get(ACCESS_TOKEN_KEY);
		final String[] tokens = token.split(" ");
		return new DefaultOAuth2AccessToken(tokens[1]);
	}

	public void putToken(HashMap<String, String> target, OAuth2AccessToken token) {
		target.put(ACCESS_TOKEN_KEY, String.format("%s %s", token.getTokenType().toLowerCase(), token.getValue()));
	}

	public DefaultOAuth2RefreshToken getRefreshToken(HashMap<String, String> target) {
		final String token = target.get(REFRESH_TOKEN_KEY);
		return new DefaultOAuth2RefreshToken(token);
	}

	public void putRefreshToken(HashMap<String, String> target, OAuth2RefreshToken token) {
		target.put(REFRESH_TOKEN_KEY, token.getValue());
	}

	public String getSpace(HashMap<String, String> target) {
		return target.get(SPACE_KEY);
	}

	public void putSpace(HashMap<String, String> target, String space) {
		target.put(SPACE_KEY, space);
	}

	public String getOrganization(HashMap<String, String> target) {
		return target.get(ORG_KEY);
	}

	public void putOrganization(HashMap<String, String> target, String org) {
		target.put(ORG_KEY, org);
	}

	public String getVersion(HashMap<String, String> target) {
		return target.get(VERSION_KEY);
	}

	public void putVersion(HashMap<String, String> target, String version) {
		target.put(VERSION_KEY, version);
	}
}