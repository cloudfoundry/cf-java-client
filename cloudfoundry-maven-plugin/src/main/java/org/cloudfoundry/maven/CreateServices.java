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

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Create Services
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal create-services
 * @phase process-sources
 */

public class CreateServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {

		ServiceCreation serviceCreation = new ServiceCreation(getClient(), getNonCreatedServices());

		List<String> serviceNames = serviceCreation.createServices();

		if (serviceNames.isEmpty()) {
			getLog().info(String.format("Service(s) have been already created"));
		} else {
			for (String serviceName : serviceNames) {
				getLog().info(String.format("Creating Service '%s': OK", serviceName));
			}
		}
	}
}