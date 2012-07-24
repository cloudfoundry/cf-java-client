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

/**
 * Class that encapsulates credentials used for authentication
 *
 * @author: Thomas Risberg
 */
public class CloudCredentials {

	private String email;

	private String password;

	private String token;

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
	 * Create credentials using a token.
	 *
	 * @param token token to use for authorization
	 */
	public CloudCredentials(String token) {
		this.token = token;
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
	public String getToken() {
		return token;
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
		return proxyUser == null ? false : true;
	}

	/**
	 * Run commands as a different user.  The authenticated user must be
	 * privileged to run as this user.

	 * @param user
	 * @return
	 */
	public CloudCredentials proxyForUser(String user) {
		return new CloudCredentials(this, user);
	}

}
