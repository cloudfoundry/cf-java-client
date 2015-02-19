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

package org.cloudfoundry.client.lib;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * Class that encapsulates credentials used for authentication
 *
 * @author Thomas Risberg
 */
public class CloudCredentials {

	private boolean refreshable = true;

	private String email;

	private String password;

	private String clientId = "cf";

	private String clientSecret = "";

	private OAuth2AccessToken token;

	private String proxyUser;

	/**
	 * Create credentials using email and password.
	 *
	 * @param email email to authenticate with
	 * @param password the password
	 */
	public CloudCredentials(String email, String password) {
		this.email = email;
		this.password = password;
	}

	/**
	 * Create credentials using email, password, and client ID.
	 *
	 * @param email email to authenticate with
	 * @param password the password
	 * @param clientId the client ID to use for authorization
	 */
	public CloudCredentials(String email, String password, String clientId) {
		this.email = email;
		this.password = password;
		this.clientId = clientId;
	}

	/**
	 * Create credentials using email, password and client ID.
	 * @param email email to authenticate with
	 * @param password the password
	 * @param clientId the client ID to use for authorization
	 * @param clientSecret the secret for the given client
	 */
	public CloudCredentials(String email, String password, String clientId, String clientSecret) {
		this.email = email;
		this.password = password;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	/**
	 * Create credentials using a token.
	 *
	 * @param token token to use for authorization
	 */
	public CloudCredentials(OAuth2AccessToken token) {
		this.token = token;
	}

    /**
     * Create credentials using a token and indicates if the token is
     * refreshable or not.
     *
     * @param token token to use for authorization
     * @param refreshable indicates if the token can be refreshed or not
     */
    public CloudCredentials(OAuth2AccessToken token, boolean refreshable) {
        this.token = token;
        this.refreshable=refreshable;
    }

	/**
	 * Create credentials using a token.
	 *
	 * @param token token to use for authorization
	 * @param clientId the client ID to use for authorization
	 */
	public CloudCredentials(OAuth2AccessToken token, String clientId) {
		this.token = token;
		this.clientId = clientId;
	}

	/**
	 * Create credentials using a token.
	 *
	 * @param token token to use for authorization
	 * @param clientId the client ID to use for authorization
	 * @param clientSecret the password for the specified client
	 */
	public CloudCredentials(OAuth2AccessToken token, String clientId, String clientSecret) {
		this.token = token;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	/**
	 * Create proxy credentials.
	 *
	 * @param cloudCredentials credentials to use
	 * @param proxyForUser user to be proxied
	 */
	public CloudCredentials(CloudCredentials cloudCredentials, String proxyForUser) {
		this.email = cloudCredentials.getEmail();
		this.password = cloudCredentials.getPassword();
		this.clientId = cloudCredentials.getClientId();
		this.token = cloudCredentials.getToken();
		this.proxyUser = proxyForUser;
	}

	/**
	 * Get the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Get the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get the token.
	 *
	 * @return the token
	 */
	public OAuth2AccessToken getToken() {
		return token;
	}

	/**
	 * Get the client ID.
	 *
	 * @return the client ID
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Get the client secret
	 *
	 * @return the client secret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * Get the proxy user.
	 *
	 * @return the proxy user
	 */
	public String getProxyUser() {
		return proxyUser;
	}

	/**
	 * Is this a proxied set of credentials?
	 *
	 * @return whether a proxy user is set
	 */
	public boolean isProxyUserSet()  {
		return proxyUser != null;
	}

	/**
	 * Run commands as a different user.  The authenticated user must be
	 * privileged to run as this user.

	 * @param user the user to proxy for
	 * @return credentials for the proxied user
	 */
	public CloudCredentials proxyForUser(String user) {
		return new CloudCredentials(this, user);
	}

	/**
	 * Indicates weather the token stored in the cloud credentials can be
	 * refreshed or not. This is useful when the token stored in this
	 * object was obtained via implicit OAuth2 authentication and therefore
	 * can not be refreshed.
	 *
	 * @return weather the token can be refreshed
	 */
	public boolean isRefreshable() {
		return refreshable;
	}
}
