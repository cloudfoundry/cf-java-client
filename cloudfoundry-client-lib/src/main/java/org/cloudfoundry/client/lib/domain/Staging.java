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
 *
 */
public class Staging {

	private String runtime;

	private String framework;

	private String command;

	/**
	 *
	 * @param framework the application framework
	 */
	public Staging(String framework) {
		this.framework = framework;
	}

	/**
	 *
	 * @param runtime the runtime name (java, ruby18, ruby19 etc.)
	 * @param framework the application framework
	 */
	public Staging(String runtime, String framework) {
		this.runtime = runtime;
		this.framework = framework;
	}

	/**
	 *
	 * @return The application runtime. If null, the server will use the default
	 *         runtime associated with the framework
	 */
	public String getRuntime() {
		return runtime;
	}

	/**
	 *
	 * @param runtime
	 *            The application runtime. If null, the server will use the
	 *            default runtime associated with the framwework
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	/**
	 *
	 * @return The application framework
	 */
	public String getFramework() {
		return framework;
	}

	/**
	 *
	 * @return The start command to use if this app is a standalone app (has
	 *         framework named "standalone")
	 */
	public String getCommand() {
		return command;
	}

	/**
	 *
	 * @param command
	 *            The start command to use if this app is a standalone app (has
	 *            framework named "standalone")
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return "Staging [runtime=" + getRuntime() + " framework=" + getFramework() + " command=" + getCommand() + "]";
	}

}
