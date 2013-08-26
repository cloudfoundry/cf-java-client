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

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.StartingInfo;
import org.springframework.http.HttpStatus;

/**
 * Restarts an application.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 * @goal restart
 * @phase process-sources
 */
public class Restart extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		try {
			getLog().info(String.format("Restarting application '%s'", getAppname()));

			final StartingInfo startingInfo = getClient().restartApplication(getAppname());
			// showStagingStatus(startingInfo);
			showStartingStatus();
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				throw new MojoExecutionException(String.format("Application '%s' does not exist",
						getAppname()), e);
			}
		}
	}
}
