/*

 * Copyright 2009-2013 the original author or authors.
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
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Shows application logs
 *
 * @author Ali Moghadam
 * @author Scott Frederick
 * @since 1.0.0

 * @goal logs
 * @phase process-sources
 */

public class Logs extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		try {
			getLog().info(String.format("Getting logs for '%s'", getAppname()));

			final Map<String, String> logs = getClient().getLogs(getAppname());
			for (Map.Entry<String, String> entry : logs.entrySet()) {
				getLog().info(String.format("Reading '%s'\n%s", entry.getKey(), entry.getValue()));
			}
		} catch (CloudFoundryException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				throw new MojoExecutionException(String.format("Application '%s' does not exist",
						getAppname()), e);
			} else {
				throw new MojoExecutionException(String.format("Error getting logs for application '%s'. Error message: '%s'. Description: '%s'",
						getAppname(), e.getMessage(), e.getDescription()), e);
			}
		}
	}
}