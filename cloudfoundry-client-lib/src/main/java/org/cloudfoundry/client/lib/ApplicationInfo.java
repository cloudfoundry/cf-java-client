/*
 * Copyright 2009-2011 the original author or authors.
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

import org.cloudfoundry.client.lib.domain.CloudApplication;

import java.io.File;

public class ApplicationInfo {
	private String appName;
	private File warFile;
	private String framework;

	public ApplicationInfo(String appName) {
		this.appName = appName;
		this.framework = CloudApplication.SPRING;
	}

	public String getAppName() {
		return appName;
	}

	public void setWarFile(File warFile) {
		this.warFile = warFile;
	}

	public File getWarFile() {
		return warFile;
	}

	public String getFramework() {
		return framework;
	}

	public void setFramework(String framework) {
		this.framework = framework;
	}
}
