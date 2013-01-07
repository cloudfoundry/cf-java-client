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
import org.cloudfoundry.client.lib.domain.CloudService;

/**
 * Unbind Services
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal unbind-services
 * @phase process-sources
 */

public class UnbindServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		for (CloudService service : getServices()) {
			if (getClient().getService(service.getName()) == null) {
				throw new MojoExecutionException(String.format("The Service '%s' does not exist.",
						service.getName()));
			}

			if (getClient().getApplication(getAppname()).getServices().contains(service.getName())) {
				getClient().unbindService(getAppname(), service.getName());
				getLog().info(String.format("Unbinding Service '%s': OK", service.getName()));
			} else {
				getLog().info(String.format("Unbinding Service '%s': Already Unbinded", service.getName()));
			}
		}
	}
}