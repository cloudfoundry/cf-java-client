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
 *
 * @since 1.0.0
 *
 */
public enum SystemProperties {

	APP_NAME("cf.appname", "appname"),
	COMMAND("cf.command", "command"),
	FRAMEWORK("cf.framework", "framework"),
	INSTANCES("cf.instances", "instances"),
	MEMORY("cf.memory", "memory"),
	NO_START("cf.no-start", "no-start"),
	PASSWORD("cf.password", "password"),
	PATH("cf.path", "path"),
	RUNTIME("cf.runtime", "runtime"),
	SETTINGS_SERVER("server", "server"),
	TARGET("cf.target", "target"),
	URL("cf.url", "url"),
	USERNAME("cf.username", "username"),
	WARFILE("cf.warfile", "warfile"), //deprecated use PATH instead
	WAIT("cf.wait", "wait");

	private String property;
	private String xmlElement;

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
