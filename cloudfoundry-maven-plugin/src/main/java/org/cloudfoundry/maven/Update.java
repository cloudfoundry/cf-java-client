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
package org.cloudfoundry.maven;

import java.io.File;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.AppState;

/**
 * Updates an application.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 * @goal update
 * @execute phase="package"
 */
public class Update extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() {

		final File path = getPath();
		final String appName = getAppname();

		validatePath(path);

		CloudApplication aplication = getClient().getApplication(appName);

		getLog().info(String.format("Updating application '%s' and Deploying '%s'.", appName, path.getAbsolutePath()));

		uploadApplication(getClient(), path, appName);

		if (AppState.STARTED.equals(aplication.getState())) {
			getClient().restartApplication(appName);
		}
	}
}
