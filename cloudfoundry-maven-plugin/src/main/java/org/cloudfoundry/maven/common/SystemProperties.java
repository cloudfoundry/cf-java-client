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
package org.cloudfoundry.maven.common;

/**
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Scott Frederick
 *
 * @since 1.0.0
 *
 */
public enum SystemProperties {

	APP_NAME("appname"),
	APP_STARTUP_TIMEOUT("appStartupTimeout"),
	COMMAND("command"),
	BUILDPACK("buildpack"),
	DISK_QUOTA("diskQuota"),
	HEALTH_CHECK_TIMEOUT("healthCheckTimeout"),
	INSTANCES("instances"),
	MEMORY("memory"),
	NO_START("no-start"),
	ORG("org"),
	PASSWORD("password"),
	PATH("path"),
	SETTINGS_SERVER("server", "server"),
	SPACE("space"),
	STACK("stack"),
	TARGET("target"),
	URL("url"),
	USERNAME("username");

	private String property;
	private String xmlElement;

	private SystemProperties(String xmlElement) {
		this.property = "cf." + xmlElement;
		this.xmlElement = xmlElement;
	}

	private SystemProperties(String property, String xmlElement) {
		this.property = property;
		this.xmlElement = xmlElement;
	}

	public String getProperty() {
		return property;
	}

	public String getXmlElement() {
		return xmlElement;
	}

}
