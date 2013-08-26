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
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;

/**
 * Scale the application instances up or down.
 *
 * @author Gunnar Hillert
 * @since 1.0.0
 *
 * @goal scale
 * @phase process-sources
 */
public class Scale extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		final Integer instances = getInstances();
		final String appname = getAppname();

		Assert.configurationNotNull(instances, "instances", SystemProperties.INSTANCES);

		getLog().info(String.format("Setting number of instances for application '%s' to '%s'", appname, instances));

		try {
			getClient().updateApplicationInstances(appname, instances);
		} catch (CloudFoundryException e) {
			throw new MojoExecutionException(
					String.format("Error setting  number of instances for " +
							"application '%s'. Error message: '%s'. Description: '%s'",
							getAppname(), e.getMessage(), e.getDescription()), e);
		}
	}
}
