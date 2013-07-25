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

package org.cloudfoundry.client.lib.domain;

/**
 * The staging information related to an application. Used for creating the
 * application
 *
 * @author Jennifer Hickey
 * @author Ramnivas Laddad
 *
 */
public class Staging {
	private String command;
	private String buildpackUrl;

	/**
	 * Default staging: No command, default buildpack
	 */
	public Staging() {
		
	}
	
	/**
	 *
	 * @param command the application command, may be null
	 * @param buildpackUrl (git url) to be used, may be null (use the default)
	 */
	public Staging(String command, String buildpackUrl) {
		this.command = command;
		this.buildpackUrl = buildpackUrl;
	}

	/**
	 *
	 * @return The start command to use if this app is a standalone app
	 */
	public String getCommand() {
		return command;
	}

	/**
	 *
	 * @return The buildpack url. If null, the server will use the default
	 *         buildpack detected based on application content
	 */
	public String getBuildpackUrl() {
		return buildpackUrl;
	}

	@Override
	public String toString() {
		return "Staging [command=" + getCommand() + " buildpack=" + getBuildpackUrl() + "]";
	}

}
