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
 * @author Scott Frederick
 *
 */
public class Staging {
	private String command;
	private String buildpackUrl;
	private String detectedBuildpack;
	private String stack;
	private Integer healthCheckTimeout;

	/**
	 * Default staging: No command, default buildpack
	 */
	public Staging() {
		
	}
	
	/**
	 *
	 * @param command the application command; may be null
	 * @param buildpackUrl a custom buildpack url (e.g. https://github.com/cloudfoundry/java-buildpack.git); may be null
	 */
	public Staging(String command, String buildpackUrl) {
		this.command = command;
		this.buildpackUrl = buildpackUrl;
	}
	
	/**
	 *
	 * @param command the application command; may be null
	 * @param buildpackUrl a custom buildpack url (e.g. https://github.com/cloudfoundry/java-buildpack.git); may be null
	 * @param detectedBuildpack raw, free-form information regarding a detected buildpack. It is a read-only property, and should not be set except when parsing a response. May be null.
	 */
	public Staging(String command, String buildpackUrl, String detectedBuildpack) {
		this(command, buildpackUrl);
		this.detectedBuildpack = detectedBuildpack;
	}

	/**
	 *
	 * @param command the application command; may be null
	 * @param buildpackUrl a custom buildpack url (e.g. https://github.com/cloudfoundry/java-buildpack.git); may be null
	 * @param stack the stack to use when staging the application; may be null
	 * @param healthCheckTimeout the amount of time the platform should wait when verifying that an app started; may be null
	 */
	public Staging(String command, String buildpackUrl, String stack, Integer healthCheckTimeout) {
		this(command, buildpackUrl);
		this.stack = stack;
		this.healthCheckTimeout = healthCheckTimeout;
	}
	
	/**
	 *
	 * @param command the application command; may be null
	 * @param buildpackUrl a custom buildpack url (e.g. https://github.com/cloudfoundry/java-buildpack.git); may be null
	 * @param stack the stack to use when staging the application; may be null
	 * @param healthCheckTimeout the amount of time the platform should wait when verifying that an app started; may be null
	 * @param detectedBuildpack raw, free-form information regarding a detected buildpack. It is a read-only property, and should not be set except when parsing a response. May be null.
	 */
	public Staging(String command, String buildpackUrl, String stack, Integer healthCheckTimeout, String detectedBuildpack) {
		this(command, buildpackUrl, stack, healthCheckTimeout);
		this.detectedBuildpack = detectedBuildpack;
	}

	/**
	 *
	 * @return The start command to use
	 */
	public String getCommand() {
		return command;
	}

	/**
	 *
	 * @return The buildpack url, or null to use the default
	 *         buildpack detected based on application content
	 */
	public String getBuildpackUrl() {
		return buildpackUrl;
	}

	/**
	 *
	 * @return Raw, free-form information regarding a detected buildpack, or
	 *         null if no detected buildpack was resolved. For example, if the
	 *         application is stopped, the detected buildpack may be null.
	 */
	public String getDetectedBuildpack() {
		return detectedBuildpack;
	}
	
	/**
	 *
	 * @return the stack to use when staging the application, or null to use the default stack
	 */
	public String getStack() {
		return stack;
	}

	/**
	 *
	 * @return the health check timeout value
	 */
	public Integer getHealthCheckTimeout() {
		return healthCheckTimeout;
	}

	@Override
	public String toString() {
		return "Staging [command=" + getCommand() +
				" buildpack=" + getBuildpackUrl() +
				" stack=" + getStack() +
				" healthCheckTimeout=" + getHealthCheckTimeout() +
				"]";
	}
}
